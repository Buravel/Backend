package buravel.buravel.modules.bookmarkPost;

import buravel.buravel.modules.bookmark.Bookmark;
import buravel.buravel.modules.bookmark.BookmarkRepository;
import buravel.buravel.modules.post.Post;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class BookmarkPostService {

    private final BookmarkPostRepository bookmarkPostRepository;
    private final BookmarkRepository bookmarkRepository;
    private final ModelMapper modelMapper;

    public List<BookmarkPostResponseDto> getBookmarkPosts(Long bookmarkId) throws NotFoundException {
        Optional<Bookmark> bookmarkEntity = bookmarkRepository.findById(bookmarkId);

        if(bookmarkEntity.isEmpty()){
            throw new NotFoundException("not found");
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
        // post 정보 매핑 - 이렇게 말고는 방법이 없나?
        PostBookmarkPostResponseDto dto = new PostBookmarkPostResponseDto();

        dto.setOriginPost_id(post.getId());
        dto.setPostTitle(post.getPostTitle());
        dto.setPrice(post.getPrice());
        dto.setOutputPrice(post.getOutputPrice());
        dto.setPostImage(post.getPostImage());
        dto.setCategory(post.getCategory());
        dto.setRating(post.getRating());
        dto.setClosed(post.isClosed());
        dto.setLat(post.getLat());
        dto.setLog(post.getLog());
        dto.setMemo(post.getMemo());

        return dto;
    }
}
