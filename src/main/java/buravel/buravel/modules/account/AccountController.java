package buravel.buravel.modules.account;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


/** 테스트용 컨트롤러 */
@RestController
@RequiredArgsConstructor
public class AccountController {

    private final AccountRepository accountRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/signUp")
    public String join(@RequestBody AccountDto accountDto) {
        Account account = modelMapper.map(accountDto, Account.class);
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        Account save = accountRepository.save(account);
        return save.getUsername() + save.getPassword() + " 회원가입완료";
    }

    @GetMapping("/hello")
    public String hello(@CurrentUser Account account) {
        if (account == null) {
            throw new AccessDeniedException("권한없음");
        }
        return account.getUsername()+"님 안녕하세요~";
    }
}