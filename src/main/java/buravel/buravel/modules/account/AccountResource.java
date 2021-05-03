package buravel.buravel.modules.account;

<<<<<<< HEAD
import org.springframework.hateoas.EntityModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class AccountResource extends EntityModel<Account> {

    public static EntityModel<Account> modelOf(Account account){
        // 셀프 링크 생성
        EntityModel<Account> userResource = EntityModel.of(account);
        userResource.add(linkTo(AccountController.class).slash(account.getId()).withSelfRel());

        // todo: login 링크 추가

        return userResource;
=======
import buravel.buravel.modules.IndexController;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RequiredArgsConstructor
public class AccountResource extends EntityModel<Account> {
    public static EntityModel<Account> modelOf(Account account) {
        EntityModel<Account> accountResource = EntityModel.of(account);
        accountResource.add(linkTo(methodOn(IndexController.class).index()).withRel("index"));
        return accountResource;
        // todo link추가하기 ex) 마이페이지, 여행계획작성페이지 등
>>>>>>> 7e2d8fd0efdf08791f5a0f57b482616da8c8e942
    }
}
