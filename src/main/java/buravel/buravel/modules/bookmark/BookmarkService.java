package buravel.buravel.modules.bookmark;

import buravel.buravel.modules.account.Account;
import buravel.buravel.modules.account.AccountResponseDto;
import buravel.buravel.modules.bookmarkPost.BookmarkPost;
import buravel.buravel.modules.bookmarkPost.BookmarkPostService;
import buravel.buravel.modules.bookmarkPost.BookmarkPostService;
import buravel.buravel.modules.plan.Plan;
import buravel.buravel.modules.post.Post;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Service
@Transactional
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final ModelMapper modelMapper;
    private final BookmarkPostService bookmarkPostService;

    //북마크 목록 검
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
        CollectionModel<EntityModel<BookmarkResponseDto>> response = CollectionModel.of(collect,linkTo(BookmarkController.class).withSelfRel());
        return response;
    }

    //북마크 생성하고 bookmark response dto 반
    public BookmarkResponseDto createBookmark(BookmarkDto bookmarkDto, Account account) {
        Bookmark bookmark = new Bookmark();
        bookmark.setBookmarkTitle(bookmarkDto.getBookmarkTitle());
        bookmark.setBookmarkManager(account);
        bookmark = bookmarkRepository.save(bookmark);
        BookmarkResponseDto bookmarkResponseDto = createBookmarkResponseDto(bookmark);
        return bookmarkResponseDto;
    }

    public void deleteBookmark(Long bookmark_id,Account account) throws NotFoundException,RuntimeException {
        Bookmark bookmark;
        if(bookmarkRepository.findById(bookmark_id).isEmpty())
            throw new NotFoundException("not found");
        else{
            bookmark = bookmarkRepository.findById(bookmark_id).get();
            if(bookmark.getBookmarkManager().getId()!=account.getId())
                throw new RuntimeException("invalid bookmark_id for this user");
        }

        //연결된 bookmark post도 삭
        List<BookmarkPost> bookmarkPostList = bookmark.getBookmarkPosts();
        for(BookmarkPost bookmarkPost : bookmarkPostList){
            bookmarkPostService.processDeleteBookmarkPost(bookmarkPost.getId());
        }
        bookmarkRepository.deleteById(bookmark_id);
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

    //계정 응답 디티오 생
    private AccountResponseDto createAccountResponseDto(Account account) {
        return modelMapper.map(account,AccountResponseDto.class);
    }

    //이미지 response dto 에 넣어서 반
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
}
