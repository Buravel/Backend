package buravel.buravel.modules.account;

import org.springframework.hateoas.EntityModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class AccountResource extends EntityModel<Account> {

    public static EntityModel<Account> modelOf(Account account){
        // 셀프 링크 생성
        EntityModel<Account> userResource = EntityModel.of(account);
        userResource.add(linkTo(AccountController.class).slash(account.getId()).withSelfRel());

        // todo: login 링크 추가

        return userResource;
    }
}
