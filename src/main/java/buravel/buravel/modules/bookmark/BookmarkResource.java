package buravel.buravel.modules.bookmark;

import org.springframework.hateoas.EntityModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class BookmarkResource extends EntityModel<BookmarkResponseDto> {

    public static EntityModel<BookmarkResponseDto> modelOf(BookmarkResponseDto bookmarkResponseDto) {
        EntityModel<BookmarkResponseDto> userEntityModel = EntityModel.of(bookmarkResponseDto);
        userEntityModel.add(linkTo(BookmarkController.class).slash(bookmarkResponseDto.getId()).withSelfRel());
        return userEntityModel;
    }
}
