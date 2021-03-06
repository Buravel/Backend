package buravel.buravel.modules.account;

import buravel.buravel.modules.IndexController;
import buravel.buravel.modules.mypage.MypageController;
import buravel.buravel.modules.plan.PlanController;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RequiredArgsConstructor
public class AccountResource extends EntityModel<Account> {
    public static EntityModel<Account> modelOf(Account account) {
        EntityModel<Account> accountResource = EntityModel.of(account);
        accountResource.add(linkTo(PlanController.class).withRel("create-plan"));
        accountResource.add(linkTo(methodOn(MypageController.class).getUserinfo(account)).withRel("myPage"));
        accountResource.add(linkTo(IndexController.class).slash("search").withRel("search"));
        accountResource.add(linkTo(PlanController.class).slash("mine").slash("closed").withRel("my-closed-plans"));
        accountResource.add(linkTo(PlanController.class).slash("mine").slash("published").withRel("my-published-plans"));
        accountResource.add(Link.of("https://docs.google.com/spreadsheets/d/1XmJZD9VyPquyhDm9XyfJL_KO89xLlN5-VTQGh6MTW-g/edit#gid=0").withRel("profile"));

        return accountResource;
    }
}
