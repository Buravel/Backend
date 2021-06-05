package buravel.buravel.modules.account;

import buravel.buravel.modules.account.dtos.AccountDto;
import buravel.buravel.modules.account.dtos.AccountResponseDto;
import buravel.buravel.modules.account.dtos.AccountWithPlanDto;
import buravel.buravel.modules.account.dtos.EmailDto;
import buravel.buravel.modules.errors.ErrorResource;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import buravel.buravel.modules.account.validator.SignUpFormValidator;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

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

    @GetMapping("/check")
    public ResponseEntity checkMyInfo(@CurrentUser Account account) {
        Optional<Account> target = accountRepository.findById(account.getId());
        if (target == null) {
            return ResponseEntity.notFound().build();
        }

        AccountWithPlanDto result = accountService.createResponseWithPlan(target.get());
        EntityModel<AccountWithPlanDto> returnVal = AccountWithPlanResource.modelOf(result);
        return ResponseEntity.ok().body(returnVal);
    }

    @GetMapping("/emailVerification")
    public ResponseEntity emailVerification(@CurrentUser Account account, @RequestParam String token){
        Optional<Account> byId = accountRepository.findById(account.getId());
        if (byId.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        AccountResponseDto dto = accountService.emailVerification(byId.get(), token);
        if(dto == null){
            return ResponseEntity.badRequest().build();
        } // 인증번호 맞지 않음

        return ResponseEntity.ok(dto);
    }

    //generate new emailCheckToken & re-send token
    @PostMapping("/emailCheckToken")
    public ResponseEntity resendEmailCheckToken(@CurrentUser Account account) {
        accountService.reSendEmailCheckToken(account);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/tempPassword")
    public ResponseEntity sendTempPassword(@RequestBody @Valid EmailDto email, Errors errors) {
        if (errors.hasErrors()) {
            EntityModel<Errors> jsr303error = ErrorResource.modelOf(errors);
            return ResponseEntity.badRequest().body(jsr303error);
        }
        Account byEmail = accountRepository.findByEmail(email.getEmail());
        if (byEmail == null) {
            return ResponseEntity.notFound().build();
        }
        accountService.sendTempPassword(byEmail);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/findUsername")
    public ResponseEntity sendUsername(@RequestParam String email){
        Account account = accountRepository.findByEmail(email);
        if(account == null) {
            return ResponseEntity.notFound().build();
        } // no such user

        accountService.sendUsername(account);
        return ResponseEntity.ok().build();
    }
}