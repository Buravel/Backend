package buravel.buravel.modules.bookmarkPost;

import buravel.buravel.modules.account.Account;
import buravel.buravel.modules.account.CurrentUser;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequiredArgsConstructor
public class BookmarkPostController {

    private final BookmarkPostService bookmarkPostService;

    @GetMapping("/bookmark/{bookmarkId}")
    public ResponseEntity getBookmarkPosts(@PathVariable(value = "bookmarkId") Long bookmarkId,
                                           @CurrentUser Account account) throws NotFoundException {
        List<BookmarkPostResponseDto> bookmarkPostList = bookmarkPostService.getBookmarkPosts(bookmarkId);

        List<EntityModel<BookmarkPostResponseDto>> bookmarkPostCollect = bookmarkPostList.stream()
                .map(bookmarkPostResponseDto -> BookmarkPostResource.modelOf(bookmarkPostResponseDto))
                .collect(Collectors.toList());
        CollectionModel collectionModel = CollectionModel
                .of(bookmarkPostCollect, linkTo(BookmarkPostController.class).withSelfRel());

        return ResponseEntity.ok(collectionModel);

    }

    @PostMapping("/bookmark/post/{bookmarkId}/{postId}")
    public ResponseEntity addBookmarkPost(@PathVariable(value = "bookmarkId") Long bookmarkId,
                                          @PathVariable(value = "postId") Long postId,
                                          @CurrentUser Account account) throws NotFoundException {

        BookmarkPostResponseDto bookmarkPostResponseDto = bookmarkPostService.processAddBookmarkPosts(bookmarkId, postId);

        if(bookmarkPostResponseDto == null){
            return ResponseEntity.badRequest().build();
        } // 중복 post 검사. 이렇게만 보내도 될까? validator로 빼야되나?

        EntityModel<BookmarkPostResponseDto> bookmarkResource = BookmarkPostResource.modelOf(bookmarkPostResponseDto);
        return ResponseEntity.ok(bookmarkResource);
    }

    @DeleteMapping("/bookmark/post/{bookmarkPostId}")
    public ResponseEntity deleteBookmarkPost(@PathVariable(value = "bookmarkPostId") Long bookmarkPostId,
                                             @CurrentUser Account account) throws NotFoundException {

        if(!bookmarkPostService.processDeleteBookmarkPost(bookmarkPostId)){
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }
}
