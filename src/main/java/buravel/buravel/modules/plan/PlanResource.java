package buravel.buravel.modules.plan;

import buravel.buravel.modules.IndexController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


public class PlanResource extends EntityModel<PlanResponseDto> {
    public static EntityModel<PlanResponseDto> modelOf(PlanResponseDto planResponseDto) {
        EntityModel<PlanResponseDto> planResource = EntityModel.of(planResponseDto);
        planResource.add(linkTo(IndexController.class).slash("search").withRel("search"));
        planResource.add(Link.of("https://docs.google.com/spreadsheets/d/1XmJZD9VyPquyhDm9XyfJL_KO89xLlN5-VTQGh6MTW-g/edit#gid=0").withRel("profile"));
        return planResource;
    }
}
