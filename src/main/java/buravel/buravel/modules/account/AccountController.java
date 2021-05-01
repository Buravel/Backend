package buravel.buravel.modules.account;

import buravel.buravel.modules.errors.ErrorResource;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;


/** 테스트용 컨트롤러 */
@RestController
@RequiredArgsConstructor
public class AccountController {

    private final AccountRepository accountRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final AccountService accountService;
    private final SignUpValidator signUpValidator;

    @PostMapping("/signUp")
    public ResponseEntity signUp(@RequestBody AccountDto accountDto, Errors errors) {
        
        if(errors.hasErrors()){
            EntityModel<Errors> error = ErrorResource.modelOf(errors);
            return ResponseEntity.badRequest().body(error);
        } // error check

        // 중복 email or 잘못된 형식 검사
        signUpValidator.validate(accountDto, errors);
        if(errors.hasErrors()){
            EntityModel<Errors> error = ErrorResource.modelOf(errors);
            return ResponseEntity.badRequest().body(error);
        } // service로 빼는게 나은가?

        // 회원가입
        Account account = accountService.signUp(accountDto);

        return ResponseEntity.ok().body(AccountResource.modelOf(account));
    }

    @GetMapping("/emailCheck")
    public ResponseEntity emailCheck(@RequestParam String token, @RequestParam String email){
        if(!accountService.emailCheck(token, email)){
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }



    @PostMapping("/tempPassword")
    public ResponseEntity sendTempPassword(@RequestBody AccountDto accountDto) {
        accountService.sendTempPassword(accountDto.getEmail());
        return ResponseEntity.ok().build();
    }

    /*@GetMapping("/hello")
    public String hello(@CurrentUser Account account) {
        if (account == null) {
            throw new AccessDeniedException("권한없음");
        }
        return account.getUsername()+"님 안녕하세요~";
    }*/
}