package buravel.buravel.modules.account;

import buravel.buravel.modules.errors.ErrorResource;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import buravel.buravel.modules.account.validator.SignUpFormValidator;
import buravel.buravel.modules.errors.ErrorResource;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class AccountController {

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

    @PostMapping("/emailVerification")
    public ResponseEntity emailVerification(@CurrentUser Account account,@RequestParam String token) throws NotFoundException {
        Account found = accountRepository.findById(account.getId())
                .orElseThrow(() -> new NotFoundException(account.getUsername()));
        if (!account.isValidToken(token)) {
            return ResponseEntity.badRequest().build();
        }
        accountService.completeSignUp(found);
        return ResponseEntity.ok().build();
    }

    //generate new emailCheckToken & re-send token
    @PostMapping("/emailCheckToken")
    public ResponseEntity resendEmailCheckToken(@CurrentUser Account account) {
        accountService.reSendEmailCheckToken(account);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/tempPassword")
    public ResponseEntity sendTempPassword(@RequestParam String email) {
        accountService.sendTempPassword(email);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/findUsername")
    public ResponseEntity sendUsername(@RequestParam String email){
        if(!accountService.sendUsername(email))
            return ResponseEntity.badRequest().build();

        return ResponseEntity.ok().build();
    }
}