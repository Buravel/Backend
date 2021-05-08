package buravel.buravel.modules.plan;

import buravel.buravel.modules.IndexController;
import org.hibernate.EntityMode;
import org.springframework.hateoas.EntityModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class PlanWithPostResource extends EntityModel<PlanWithPostResponseDto> {
    public static EntityModel<PlanWithPostResponseDto> modelOf(PlanWithPostResponseDto dto) {
        EntityModel<PlanWithPostResponseDto> of = EntityModel.of(dto);
        of.add(linkTo(methodOn(IndexController.class).index()).withRel("index"));
        return of;
    }
}
