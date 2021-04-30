package buravel.buravel.modules.account;

import buravel.buravel.modules.account.validator.SignUpFormValidator;
import buravel.buravel.modules.errors.ErrorResource;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;



@RestController
@RequiredArgsConstructor
public class AccountController {

    /**
     * ★★★ jwt를 사용하기 때문에 프론트에서 모든요청에서
     * {
     *     "username":"~"을 담아서 항상 보내줘야함
     * }
     * 토큰헤더와 username헤더가  둘 다 있으니 프론트에선 username헤더 값도 저장해놓고 모든 요청보낼 때 위처럼 추가해주기
     * 백엔드에서도 테스트할 때 항상 포함해줘야함
     * */
    private final AccountRepository accountRepository;
    private final AccountService accountService;
    private final SignUpFormValidator validator;

    @PostMapping("/signUp")
    public ResponseEntity signUp(@RequestBody @Valid AccountDto accountDto, Errors errors) {
        if (errors.hasErrors()) {
            EntityModel<Errors> jsr303error = ErrorResource.modelOf(errors);
            return ResponseEntity.badRequest().body(jsr303error);
        }
        validator.validate(accountDto, errors);
        if (errors.hasErrors()) {
            EntityModel<Errors> customError = ErrorResource.modelOf(errors);
            return ResponseEntity.badRequest().body(customError);
        }
        Account account = accountService.processNewAccount(accountDto);
        EntityModel<Account> accountResource = AccountResource.modelOf(account);
        return ResponseEntity.ok(accountResource);
    }

    @GetMapping("/emailVerification")
    public ResponseEntity emailVerification(@RequestParam String token, @RequestParam String email) {
        Account account = accountRepository.findByEmail(email);
        if (account == null) {
            throw new UsernameNotFoundException(account.getUsername());
        }
        if (!account.isValidToken(token)) {
            return ResponseEntity.badRequest().build();
        }
        accountService.completeSignUp(account);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/tempPassword")
    public ResponseEntity sendTempPassword(@RequestParam String email) {
        accountService.sendTempPassword(email);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/hello")
    public String hello(@CurrentUser Account account) {
        if (account == null) {
            throw new AccessDeniedException("권한없음");
        }
        return account.getUsername()+"님 안녕하세요~";
    }
}