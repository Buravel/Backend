package buravel.buravel.modules.account;

import buravel.buravel.infra.AppProperties;
import buravel.buravel.infra.mail.EmailMessage;
import buravel.buravel.infra.mail.EmailService;
import buravel.buravel.modules.account.event.TempPasswordEvent;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final AppProperties appProperties;
    private final ApplicationEventPublisher publisher;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final TemplateEngine templateEngine;
    private final EmailService emailService;
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

    public Account signUp(AccountDto accountDto){
        Account account = modelMapper.map(accountDto, Account.class);
        Account saved = createAccount(account);

        // email 인증 메일 발송
        sendConfirmEmail(saved);

        return saved;
    }

    public void sendConfirmEmail(Account account){
        Context context = new Context();

        // mail에 담을 link 생성
        context.setVariable("link", "/emailCheck?token=" + account.getEmailCheckToken()
                                + "&email=" + account.getEmail());
        context.setVariable("username", account.getUsername());
        context.setVariable("host", appProperties.getHost());

        // 임시 mail html에 변수 전달
        String message = templateEngine.process("mail/emailCheck", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .to(account.getEmail())
                .subject("Bravel 인증 메일 입니다.")
                .message(message).build();

        emailService.sendEmail(emailMessage);
    }

    public boolean emailCheck(String token, String email){
        Account account = accountRepository.findByEmail(email);

        if(account == null){
            throw new UsernameNotFoundException(email);
        }

        if(!account.getEmailCheckToken().equals(token)){
            return false;
        }

        account.setEmailVerified(true);
        return true;
    }
}
