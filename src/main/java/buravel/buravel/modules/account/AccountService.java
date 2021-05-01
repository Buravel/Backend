package buravel.buravel.modules.account;

import buravel.buravel.infra.AppProperties;
import buravel.buravel.infra.mail.EmailMessage;
import buravel.buravel.infra.mail.EmailService;
import buravel.buravel.modules.account.event.TempPasswordEvent;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final ApplicationEventPublisher publisher;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final EmailService emailService;
    private final AppProperties appProperties;
    private final TemplateEngine templateEngine;

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

    // signUp
    public Account processNewAccount(AccountDto accountDto) {
        Account account = saveNewAccount(accountDto);
        sendSignUpConfirmEmail(account);
        return account;
    }
    // save account
    public Account saveNewAccount(AccountDto accountDto) {
        Account map = modelMapper.map(accountDto, Account.class);
        map.setPassword(passwordEncoder.encode(map.getPassword()));
        map.generateEmailCheckToken();
        Account saved = accountRepository.save(map);
        return saved;
    }
    // verify email
    private void sendSignUpConfirmEmail(Account account) {
        Context context = new Context(); // model에 내용담아주듯이
        context.setVariable("token",account.getEmailCheckToken());
        context.setVariable("username", account.getUsername());
        context.setVariable("message","Buravel 서비스 사용을 위해 코드를 복사하여 붙여넣어주세요.");

        String message = templateEngine.process("mail/simple-link", context);

        EmailMessage build = EmailMessage.builder()
                .to(account.getEmail())
                .subject("Buravel 회원 가입 인증")
                .message(message)
                .build();

        emailService.sendEmail(build);
    }

    public void completeSignUp(Account account) {
        account.completeSignUp();
    }
}
