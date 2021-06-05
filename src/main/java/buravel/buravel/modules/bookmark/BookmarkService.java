package buravel.buravel.modules.bookmark;

import buravel.buravel.modules.account.Account;
import buravel.buravel.modules.account.AccountRepository;
import buravel.buravel.modules.account.dtos.AccountResponseDto;
import buravel.buravel.modules.bookmark.dtos.BookmarkDto;
import buravel.buravel.modules.bookmark.dtos.BookmarkResponseDto;
import buravel.buravel.modules.bookmarkPost.BookmarkPost;
import buravel.buravel.modules.bookmarkPost.BookmarkPostService;
import buravel.buravel.modules.post.Post;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Service
@Transactional
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final ModelMapper modelMapper;
    private final BookmarkPostService bookmarkPostService;
    private final AccountRepository accountRepository;

    //북마크 목록 검색
    public CollectionModel<EntityModel<BookmarkResponseDto>> findAllBookmark(Account account) {
        List<Bookmark> bookmarks = bookmarkRepository.findAllByBookmarkManager(account);
        List<BookmarkResponseDto> bookmarkResponseDtos = new ArrayList<>();

        //북마크를 이미지가 포함된 bookmark response Dto 로 변환해서 어레이에 담
        for (Bookmark bookmark : bookmarks) {
            BookmarkResponseDto bookmarkResponseDto = createBookmarkImageDto(bookmark);
            bookmarkResponseDto.setAccountResponseDto(createAccountResponseDto(account));
            bookmarkResponseDtos.add(bookmarkResponseDto);
        }

        List<EntityModel<BookmarkResponseDto>> collect =
                bookmarkResponseDtos.stream().map(p -> BookmarkResource.modelOf(p)).collect(Collectors.toList());
        CollectionModel<EntityModel<BookmarkResponseDto>> response = CollectionModel.of(collect,linkTo(BookmarkController.class).withRel("getBookmarkList"));
        return response;
    }

    //북마크 생성하고 bookmark response dto 반환
    public BookmarkResponseDto createBookmark(BookmarkDto bookmarkDto, Account account) {
        Bookmark bookmark = new Bookmark();
        bookmark.setBookmarkTitle(bookmarkDto.getBookmarkTitle());
        bookmark.setBookmarkManager(account);
        bookmark = bookmarkRepository.save(bookmark);
        BookmarkResponseDto bookmarkResponseDto = createBookmarkResponseDto(bookmark);
        return bookmarkResponseDto;
    }

    //북마크를 만들때 북마크 포스트를 포함해야 하는 경우 사용
    public BookmarkResponseDto createBookmark(BookmarkDto bookmarkDto,Account account,List<BookmarkPost> bookmarkPosts){
        Bookmark bookmark = new Bookmark();
        bookmark.setBookmarkTitle(bookmarkDto.getBookmarkTitle());
        bookmark.setBookmarkManager(account);
        List<BookmarkPost> bookmarkPostList = bookmark.getBookmarkPosts();
        for(BookmarkPost bookmarkPost : bookmarkPosts){
            bookmarkPostList.add(bookmarkPost);
        }
        bookmark.setBookmarkPosts(bookmarkPosts);
        bookmark = bookmarkRepository.save(bookmark);
        BookmarkResponseDto bookmarkResponseDto = createBookmarkResponseDto(bookmark);
        return bookmarkResponseDto;
    }

    public void deleteBookmark(Long bookmark_id,Account account) throws NotFoundException{
        Account find = accountRepository.findById(account.getId()).get();
        Bookmark bookmark = getBookmarkById(bookmark_id);
        if (!bookmark.getBookmarkManager().equals(find)) {
            throw new AccessDeniedException("사용 권한이 없습니다.");
        }

        //연결된 bookmark post도 삭제
        List<BookmarkPost> bookmarkPostList = bookmark.getBookmarkPosts();
        List<Long> bookmarkPostIds = new ArrayList<>();
        for(BookmarkPost bookmarkPost : bookmarkPostList){
            bookmarkPostIds.add(bookmarkPost.getId());
        }
        for(Long id : bookmarkPostIds){
            bookmarkPostService.processDeleteBookmarkPost(id);
        }
        bookmarkRepository.deleteById(bookmark_id);
    }

    public BookmarkResponseDto modifyBookmark (Long bookmark_id,BookmarkDto bookmarkDto, Account account) throws NotFoundException{
        Account find = accountRepository.findById(account.getId()).get();
        Bookmark bookmark = getBookmarkById(bookmark_id);
        if (!bookmark.getBookmarkManager().equals(find)) {
            throw new AccessDeniedException("사용 권한이 없습니다.");
        }
        bookmark.setBookmarkTitle(bookmarkDto.getBookmarkTitle());
        BookmarkResponseDto bookmarkResponseDto = createBookmarkImageDto(bookmark);
        return bookmarkResponseDto;
    }

    public Bookmark getBookmarkById (Long bookmark_id) throws NotFoundException {
        Optional<Bookmark> bookmark = bookmarkRepository.findById(bookmark_id);
        if(bookmark.isEmpty())
            throw new NotFoundException("not found");
        return bookmark.get();
    }

    //reponse dto 에 Image 포함시키기를 고려하지 전에 생성한 메소드 사용부분
    public BookmarkResponseDto createBookmarkResponseDto(Bookmark bookmark){
        //BookmarkResponseDto bookmarkResponseDto = modelMapper.map(bookmark,BookmarkResponseDto.class);
        BookmarkResponseDto bookmarkResponseDto = new BookmarkResponseDto();
        bookmarkResponseDto.setBookmarkTitle(bookmark.getBookmarkTitle());
        bookmarkResponseDto.setId(bookmark.getId());
        AccountResponseDto accountResponseDto = createAccountResponseDto(bookmark.getBookmarkManager());
        bookmarkResponseDto.setAccountResponseDto(accountResponseDto);
        return bookmarkResponseDto;
    }

    //계정 응답 디티오 생성
    private AccountResponseDto createAccountResponseDto(Account account) {
        return modelMapper.map(account,AccountResponseDto.class);
    }

    //이미지 response dto 에 넣어서 반환
    private BookmarkResponseDto createBookmarkImageDto(Bookmark bookmark){
        BookmarkResponseDto bookmarkResponseDto = modelMapper.map(bookmark,BookmarkResponseDto.class);
        List<BookmarkPost> bookmarkPosts = bookmark.getBookmarkPosts();
        List<String> images = new ArrayList<>();
        int numOfImage = Math.min(bookmarkPosts.size(),4);
        for(int i =0;i<numOfImage;i++){
            Post post = bookmarkPosts.get(i).getPost();
            String image = post.getPostImage();
            images.add(image);
        }
        bookmarkResponseDto.setBookmarkImages(images);
        return bookmarkResponseDto;
    }

    public CollectionModel<EntityModel<BookmarkResponseDto>> addLinkWithBookmarks(CollectionModel<EntityModel<BookmarkResponseDto>> entityModels) {
        for(EntityModel<BookmarkResponseDto> entityModel : entityModels){
            entityModel.add(linkTo(BookmarkController.class).slash(entityModel.getContent().getId()).withRel("deleteBookmark"));
            entityModel.add(linkTo(BookmarkController.class).slash(entityModel.getContent().getId()).withRel("modifyBookmark"));
            entityModel.add(linkTo(BookmarkController.class).withRel("getBookmarkList"));
        }
        return entityModels;
    }
}
