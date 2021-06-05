package buravel.buravel.modules.plan;

import buravel.buravel.modules.IndexController;
import buravel.buravel.modules.plan.dtos.PatchPlanResponseDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class PatchPlanResource extends EntityModel<PatchPlanResponseDto> {
    public static EntityModel<PatchPlanResponseDto> modelOf(PatchPlanResponseDto patchPlanResponseDto) {
        EntityModel<PatchPlanResponseDto> planResource = EntityModel.of(patchPlanResponseDto);
        planResource.add(linkTo(IndexController.class).slash("search").withRel("search"));
        planResource.add(Link.of("https://docs.google.com/spreadsheets/d/1XmJZD9VyPquyhDm9XyfJL_KO89xLlN5-VTQGh6MTW-g/edit#gid=0").withRel("profile"));
        return planResource;
    }
}
