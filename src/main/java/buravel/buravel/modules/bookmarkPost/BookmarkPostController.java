package buravel.buravel.modules.bookmarkPost;

import buravel.buravel.modules.account.Account;
import buravel.buravel.modules.account.CurrentUser;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequiredArgsConstructor
public class BookmarkPostController {

    private final BookmarkPostService bookmarkPostService;

    @GetMapping("/bookmark/{bookmarkId}")
    public ResponseEntity getBookmarkPosts(@PathVariable(value = "bookmarkId") Long bookmarkId, @CurrentUser Account account){
        List<BookmarkPostResponseDto> bookmarkPostList = bookmarkPostService.getBookmarkPosts(bookmarkId);

        if(bookmarkPostList == null){
            return ResponseEntity.badRequest().build();
        }

        List<EntityModel<BookmarkPostResponseDto>> bookmarkPostCollect = bookmarkPostList.stream()
                .map(bookmarkPostResponseDto -> BookmarkPostResource.modelOf(bookmarkPostResponseDto))
                .collect(Collectors.toList());
        CollectionModel collectionModel = CollectionModel
                .of(bookmarkPostCollect, linkTo(BookmarkPostController.class).withSelfRel());

        return ResponseEntity.ok(collectionModel);

    }

    @PostMapping("/bookmark/post/{bookmarkId}/{postId}")
    public ResponseEntity addBookmarkPost(@CurrentUser Account account){

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/bookmark/post/{bookmarkPostId}/{postId}")
    public ResponseEntity deleteBookmarkPost(@CurrentUser Account account){

        return ResponseEntity.ok().build();
    }
}
