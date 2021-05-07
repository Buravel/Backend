package buravel.buravel.modules.bookmarkPost;

import buravel.buravel.modules.IndexController;
import org.springframework.hateoas.EntityModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class BookmarkPostResource extends EntityModel<BookmarkPostResponseDto> {

    public static EntityModel<BookmarkPostResponseDto> modelOf(BookmarkPostResponseDto bookmarkPostResponseDto){
        EntityModel<BookmarkPostResponseDto> bookmarkPostResource = EntityModel.of(bookmarkPostResponseDto);
        bookmarkPostResource.add(linkTo(methodOn(IndexController.class).index()).withRel("index"));

        return bookmarkPostResource;
    }
}
