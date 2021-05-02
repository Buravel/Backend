package buravel.buravel.modules.plan;

import buravel.buravel.modules.IndexController;
import org.springframework.hateoas.EntityModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class PlanResource extends EntityModel<Plan> {
    public static EntityModel<Plan> modelOf(Plan plan) {
        EntityModel<Plan> planResource = EntityModel.of(plan);
        planResource.add(linkTo(methodOn(IndexController.class).index()).withRel("index"));
        return planResource;
    }
}
