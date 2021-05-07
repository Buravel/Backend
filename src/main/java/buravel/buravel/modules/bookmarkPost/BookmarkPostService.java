package buravel.buravel.modules.bookmarkPost;

import buravel.buravel.modules.bookmark.Bookmark;
import buravel.buravel.modules.bookmark.BookmarkRepository;
import buravel.buravel.modules.post.Post;
import buravel.buravel.modules.post.PostRepository;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class BookmarkPostService {

    private final BookmarkPostRepository bookmarkPostRepository;
    private final BookmarkRepository bookmarkRepository;
    private final PostRepository postRepository;
    private final ModelMapper modelMapper;

    public List<BookmarkPostResponseDto> getBookmarkPosts(Long bookmarkId) {
        Optional<Bookmark> bookmarkEntity = bookmarkRepository.findById(bookmarkId);

        if(bookmarkEntity.isEmpty()){
            return null;
        } // no such bookmark folder

        Bookmark bookmark = bookmarkEntity.get();

        List<BookmarkPost> bookmarkPostList = bookmarkPostRepository.findByBookmarkAndChecked(bookmark, false);
        List<BookmarkPostResponseDto> bookmarkPostResponseDtos = new ArrayList<>();

        for(BookmarkPost bookmarkPost : bookmarkPostList){
            bookmarkPostResponseDtos.add(createBookmarkPostResponseDto(bookmarkPost));
        }

        return bookmarkPostResponseDtos;
    }

    public BookmarkPostResponseDto createBookmarkPostResponseDto(BookmarkPost bookmarkPost){
        BookmarkPostResponseDto bookmarkPostResponseDto = modelMapper.map(bookmarkPost, BookmarkPostResponseDto.class);

        if(bookmarkPost.isChecked()){
            bookmarkPostResponseDto.setPlanOf_id(bookmarkPost.getPlanOf().getId());
        }

        bookmarkPostResponseDto.setPostBookmarkPostResponseDto(
                createPostBookmarkPostResponseDto(bookmarkPost.getPost()));

        bookmarkPostResponseDto.setBookmark_id(bookmarkPost.getBookmark().getId());

        return bookmarkPostResponseDto;
    }

    public PostBookmarkPostResponseDto createPostBookmarkPostResponseDto(Post post){
        PostBookmarkPostResponseDto dto = modelMapper.map(post, PostBookmarkPostResponseDto.class);

        dto.setOriginPost_id(post.getId());
        return dto;
    }

    public BookmarkPostResponseDto addBookmarkPosts(Long bookmarkId, Long postId){
        BookmarkPostResponseDto bookmarkPostResponseDto = new BookmarkPostResponseDto();

        return bookmarkPostResponseDto;
    }

    public boolean processDeleteBookmarkPost(Long bookmarkPostId, Long postId){
        Optional<BookmarkPost> bookmarkPostEntity = bookmarkPostRepository.findById(bookmarkPostId);
        if(bookmarkPostEntity.isEmpty()){
            return false;
        } // no such bookmark post

        BookmarkPost bookmarkPost = bookmarkPostEntity.get();

        if(bookmarkPost.getPost().getId() != postId){
            return false;
        } // postId 잘못 들어옴. todo: 필요한가? postId 받을 필요가 없어보임. 오히려 null 잘못받으면 에러날 듯

        return deleteBookmarkPost(bookmarkPost);
    }

    public boolean deleteBookmarkPost(BookmarkPost bookmarkPost){
        if(bookmarkPost.isChecked()){
            return false;
        } // there is a plan that uses this bookmark post.

        Optional<Bookmark> bookmarkEntity = bookmarkRepository.findById(bookmarkPost.getBookmark().getId());
        if(!bookmarkEntity.isEmpty()){
            bookmarkEntity.get().getBookmarkPosts().remove(bookmarkPost);
        } // 양방향 북마크 매핑 삭제

        Optional<Post> postEntity = postRepository.findById(bookmarkPost.getPost().getId());
        if(!postEntity.isEmpty()){
            postEntity.get().getBookmarkPosts().remove(bookmarkPost);
        } // 양방향 포스트 매핑 삭제

        bookmarkPostRepository.deleteById(bookmarkPost.getId());
        return true;
    }
    
}
