package buravel.buravel.modules.plan;

import buravel.buravel.modules.IndexController;
import buravel.buravel.modules.plan.dtos.PlanWithPostResponseDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class PlanWithPostResource extends EntityModel<PlanWithPostResponseDto> {
    public static EntityModel<PlanWithPostResponseDto> modelOf(PlanWithPostResponseDto dto) {
        EntityModel<PlanWithPostResponseDto> of = EntityModel.of(dto);
        of.add(linkTo(IndexController.class).slash("search").withRel("search"));
        of.add(Link.of("https://docs.google.com/spreadsheets/d/1XmJZD9VyPquyhDm9XyfJL_KO89xLlN5-VTQGh6MTW-g/edit#gid=0").withRel("profile"));
        return of;
    }
}
