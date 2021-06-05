package buravel.buravel.modules.bookmark;

import buravel.buravel.modules.IndexController;
import buravel.buravel.modules.bookmark.dtos.BookmarkResponseDto;
import org.springframework.hateoas.EntityModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class BookmarkResource extends EntityModel<BookmarkResponseDto> {

    public static EntityModel<BookmarkResponseDto> modelOf(BookmarkResponseDto bookmarkResponseDto) {
        EntityModel<BookmarkResponseDto> userEntityModel = EntityModel.of(bookmarkResponseDto);
        userEntityModel.add(linkTo(IndexController.class).slash("search").withRel("search"));
        userEntityModel.add(linkTo(BookmarkController.class).slash(bookmarkResponseDto.getId()).withSelfRel());
        return userEntityModel;
    }
}
