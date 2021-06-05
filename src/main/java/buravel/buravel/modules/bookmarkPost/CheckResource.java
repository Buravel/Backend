package buravel.buravel.modules.bookmarkPost;

import buravel.buravel.modules.IndexController;
import buravel.buravel.modules.bookmarkPost.dtos.CheckResponseDto;
import buravel.buravel.modules.plan.PlanController;
import org.springframework.hateoas.EntityModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class CheckResource extends EntityModel<CheckResponseDto> {

    public static EntityModel<CheckResponseDto> modelOf(CheckResponseDto checkResponseDto){
        EntityModel<CheckResponseDto> checkResource = EntityModel.of(checkResponseDto);
        checkResource.add(linkTo(IndexController.class).slash("search").withRel("search"));
        checkResource.add(linkTo(PlanController.class)
                .slash(checkResource.getContent().getPlanId()).withRel("mappingPlan"));

        return checkResource;
    }

}
