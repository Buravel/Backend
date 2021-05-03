package buravel.buravel.modules.plan;

import buravel.buravel.modules.IndexController;
import org.springframework.hateoas.EntityModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


public class PlanResource extends EntityModel<PlanResponseDto> {
    public static EntityModel<PlanResponseDto> modelOf(PlanResponseDto planResponseDto) {
        EntityModel<PlanResponseDto> planResource = EntityModel.of(planResponseDto);
        planResource.add(linkTo(methodOn(IndexController.class).index()).withRel("index"));
        return planResource;
    }
}
