package buravel.buravel.modules.account;

import buravel.buravel.modules.IndexController;
import buravel.buravel.modules.account.dtos.AccountWithPlanDto;
import buravel.buravel.modules.plan.PlanController;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequiredArgsConstructor
public class AccountWithPlanResource extends EntityModel<AccountWithPlanDto> {
    public static EntityModel<AccountWithPlanDto> modelOf(AccountWithPlanDto target) {
        EntityModel<AccountWithPlanDto> accountResource = EntityModel.of(target);
        accountResource.add(linkTo(PlanController.class).withRel("create-plan"));
         accountResource.add(linkTo(IndexController.class).slash("search").withRel("search"));
        accountResource.add(linkTo(PlanController.class).slash("mine").slash("closed").withRel("my-closed-plans"));
        accountResource.add(linkTo(PlanController.class).slash("mine").slash("published").withRel("my-published-plans"));
        accountResource.add(Link.of("https://docs.google.com/spreadsheets/d/1XmJZD9VyPquyhDm9XyfJL_KO89xLlN5-VTQGh6MTW-g/edit#gid=0").withRel("profile"));

        return accountResource;
    }
}
