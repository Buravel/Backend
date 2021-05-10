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

    public List<BookmarkPostResponseDto> getBookmarkPosts(Long bookmarkId) throws NotFoundException{
        Optional<Bookmark> bookmarkEntity = bookmarkRepository.findById(bookmarkId);

        if(bookmarkEntity.isEmpty()){
            throw new NotFoundException("해당 북마크 폴더가 존재하지 않습니다.");
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

    public BookmarkPostResponseDto processAddBookmarkPosts(Long bookmarkId, Long postId) throws NotFoundException{

        Optional<Bookmark> bookmarkEntity = bookmarkRepository.findById(bookmarkId);
        if(bookmarkEntity.isEmpty()){
            throw new NotFoundException("해당 북마크가 없습니다.");
        }

        Optional<Post> postEntity = postRepository.findById(postId);
        if(postEntity.isEmpty()){
            throw new NotFoundException("해당 포스트가 존재하지 않습니다.");
        }
        // todo: 검사가 반복되는거같은데, 일단 기능 구현 후 나중에 리팩토링

        Bookmark bookmark = bookmarkEntity.get();
        Post post = postEntity.get();

        if(bookmarkPostRepository.findByBookmarkAndPost(bookmark, post) != null){
            return null;
        } // 중복 post 존재

        BookmarkPost bookmarkPost = addBookmarkPost(bookmark, post);

        return createBookmarkPostResponseDto(bookmarkPost);
    }

    public BookmarkPost addBookmarkPost(Bookmark bookmark, Post post){
        BookmarkPost bookmarkPost = createBookmarkPost(bookmark, post);
        BookmarkPost saved = bookmarkPostRepository.save(bookmarkPost);
        // 양방향 save
        bookmark.getBookmarkPosts().add(saved);
        post.getBookmarkPosts().add(saved);

        return saved;
    }

    public BookmarkPost createBookmarkPost(Bookmark bookmark, Post post){
        BookmarkPost bookmarkPost = new BookmarkPost();

        bookmarkPost.setBookmark(bookmark);
        bookmarkPost.setPost(post);
        bookmarkPost.setChecked(false);

        return bookmarkPost;
    }
    
    public boolean processDeleteBookmarkPost(Long bookmarkPostId) throws NotFoundException{
        Optional<BookmarkPost> bookmarkPostEntity = bookmarkPostRepository.findById(bookmarkPostId);
        if(bookmarkPostEntity.isEmpty()){
            throw new NotFoundException("해당 북마크 포스트가 존재하지 않습니다.");
        } // no such bookmark post

        BookmarkPost bookmarkPost = bookmarkPostEntity.get();

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
