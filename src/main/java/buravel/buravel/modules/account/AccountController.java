package buravel.buravel.modules.account;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


/** 테스트용 컨트롤러 */
@RestController
@RequiredArgsConstructor
public class AccountController {

    private final AccountRepository accountRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final AccountService accountService;

    @PostMapping("/signUp")
    public String join(@RequestBody AccountDto accountDto) {
        Account account = modelMapper.map(accountDto, Account.class);
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        Account save = accountRepository.save(account);
        return save.getUsername() + save.getPassword() + " 회원가입완료";
    }


    @PostMapping("/tempPassword")
    public ResponseEntity sendTempPassword(@RequestBody AccountDto accountDto) {
        accountService.sendTempPassword(accountDto.getEmail());
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