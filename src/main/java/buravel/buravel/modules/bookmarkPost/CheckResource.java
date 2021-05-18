package buravel.buravel.modules.bookmarkPost;

import buravel.buravel.modules.IndexController;
import org.springframework.hateoas.EntityModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class CheckResource extends EntityModel<CheckResponseDto> {

    public static EntityModel<CheckResponseDto> modelOf(CheckResponseDto checkResponseDto){
        EntityModel<CheckResponseDto> checkResource = EntityModel.of(checkResponseDto);
        checkResource.add(linkTo(methodOn(IndexController.class).index()).withRel("index"));

        return checkResource;
    }

}
