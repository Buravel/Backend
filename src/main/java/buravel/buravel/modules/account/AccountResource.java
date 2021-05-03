package buravel.buravel.modules.account;

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
        // todo EntityModel<> account -> AccountResponseDto로 변경
    }
}
