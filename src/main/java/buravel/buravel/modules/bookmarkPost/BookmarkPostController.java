package buravel.buravel.modules.bookmarkPost;

import buravel.buravel.modules.account.Account;
import buravel.buravel.modules.account.CurrentUser;
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
    private final BookmarkPostResource bookmarkPostResource;

    @GetMapping("/bookmark/{bookmarkId}")
    public ResponseEntity getBookmarkPosts(@PathVariable(value = "bookmarkId") Long bookmarkId, @CurrentUser Account account){
        // bookmark 폴더 delete시 내부 post 조회도 필요할 수 있는데, bookmarkpostcontrolller? bookmarkcontroller?
        List<BookmarkPost> bookmarkPostList = bookmarkPostService.getBookmarkPosts(bookmarkId, account);

        List<EntityModel<BookmarkPost>> bookmarkPostCollect = bookmarkPostList.stream()
                .map(bookmarkPost -> bookmarkPostResource.modelOf(bookmarkPost)).collect(Collectors.toList());
        CollectionModel collectionModel = CollectionModel.of(bookmarkPostCollect, linkTo(BookmarkPostController.class).withSelfRel());

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
