package buravel.buravel.modules.account;

import buravel.buravel.modules.account.event.TempPasswordEvent;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.hateoas.EntityModel;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final ApplicationEventPublisher publisher;
    private final PasswordEncoder passwordEncoder;
    //private final SignUpValidator signUpValidator;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Account account = accountRepository.findByUsername(s);
        if (account == null) {
            throw new UsernameNotFoundException(s);
        }
        return new UserAccount(account);
    }

    public void sendTempPassword(String email) {
        Account account = accountRepository.findByEmail(email);
        if (account == null) {
            throw new UsernameNotFoundException(email);
        }
        // temp pass create
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        uuid = uuid.substring(0, 10);
        // set temp pass
        account.setPassword(passwordEncoder.encode(uuid));
        publisher.publishEvent(new TempPasswordEvent(account,uuid));

    }

    /*public boolean checkSignupValidation(AccountDto accountDto, Errors errors){
        if(errors.hasErrors()){
            return true;
        }

        signUpValidator.validate(accountDto, errors);
        if(errors.hasErrors())
            return true;

        return false;
    }*/
    public Account createAccount(Account account){

        account.setPassword(passwordEncoder.encode(account.getPassword()));
        // account.setProfileImage(); 그냥 null이면 front가 default 보여주던가, default 이미지를 resource에서 가져다 저장하던가.
        account.setEmailVerified(false);
        account.generateEmailCheckToken(); // email token 생성

        return accountRepository.save(account);
    }

    public void sendEmailValid(String email){

    }
}
