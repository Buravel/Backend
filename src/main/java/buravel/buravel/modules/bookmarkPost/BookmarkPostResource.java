package buravel.buravel.modules.bookmarkPost;

import buravel.buravel.modules.bookmarkPost.dtos.BookmarkPostResponseDto;
import buravel.buravel.modules.plan.PlanController;
import org.springframework.hateoas.EntityModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class BookmarkPostResource extends EntityModel<BookmarkPostResponseDto> {

    public static EntityModel<BookmarkPostResponseDto> modelOf(BookmarkPostResponseDto bookmarkPostResponseDto){
        EntityModel<BookmarkPostResponseDto> bookmarkPostResource = EntityModel.of(bookmarkPostResponseDto);
        bookmarkPostResource.add(linkTo(BookmarkPostController.class)
                .slash(bookmarkPostResource.getContent().getBookmark_id()).withRel("bookmark"));
        // 본래 plan 열람할 수 있도록
        bookmarkPostResource.add(linkTo(PlanController.class)
                .slash(bookmarkPostResource.getContent().getPostBookmarkPostResponseDto().getOriginPlan_id()).withRel("originPlan"));
        bookmarkPostResource.add(linkTo(BookmarkPostController.class).slash("/post")
                .slash(bookmarkPostResource.getContent().getId()).withRel("deleteBookmarkPost"));

        return bookmarkPostResource;
    }
}
