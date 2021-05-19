package buravel.buravel.modules.account;

import buravel.buravel.infra.AppProperties;
import buravel.buravel.infra.mail.EmailMessage;
import buravel.buravel.infra.mail.EmailService;
import buravel.buravel.modules.account.event.SignUpConfirmEvent;
import buravel.buravel.modules.account.event.TempPasswordEvent;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.hateoas.EntityModel;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final AppProperties appProperties;
    private final ApplicationEventPublisher publisher;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final TemplateEngine templateEngine;
    private final ModelMapper modelMapper;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Account account = accountRepository.findByUsername(s);
        if (account == null) {
            throw new UsernameNotFoundException(s);
        }
        return new UserAccount(account);
    }

    // signUp

    public Account processNewAccount(AccountDto accountDto) {
        if(!accountDto.isEmailVerified()){
            return null;
        }

        Account account = saveNewAccount(accountDto);
        //sendSignUpConfirmEmail(account);
        return account;
    }
    // save account
    public Account saveNewAccount(AccountDto accountDto) {
        Account map = modelMapper.map(accountDto, Account.class);
        map.setPassword(passwordEncoder.encode(map.getPassword()));
        //map.generateEmailCheckToken();
        Account saved = accountRepository.save(map);
        return saved;
    }
    public void sendTempPassword(Account account) {
        if (!account.isEmailVerified()) {
            throw new AccessDeniedException("이메일 인증된 회원만 가능합니다.");
        }
        // temp pass create
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        uuid = uuid.substring(0, 10);
        // set temp pass
        account.setPassword(passwordEncoder.encode(uuid));
        publisher.publishEvent(new TempPasswordEvent(account,uuid));

    }
    // resend emailCheckToken
    private void sendSignUpConfirmEmail(String email, String token) {
        publisher.publishEvent(new SignUpConfirmEvent(email, token));
    }

    public EmailSendResponseDto reSendEmailCheckToken(String email) {
        String token = generateEmailCheckToken();
        sendSignUpConfirmEmail(email, token);

        EmailSendResponseDto dto = new EmailSendResponseDto();
        dto.setToken(token);

        return dto;
    }

    public String generateEmailCheckToken() {
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        uuid = uuid.substring(0, 10);
        //this.emailCheckTokenGeneratedAt = LocalDateTime.now();

        return uuid;
    }

    public void completeSignUp(Account find) {
        find.completeSignUp();
    }

    public Account findById(Long id) {
        return accountRepository.findById(id).get();
    }

    public Account findByEmail(String email) {
        return accountRepository.findByEmail(email);
    }


    public boolean sendUsername(String email){
        Account account = accountRepository.findByEmail(email);

        if(account == null){
            throw new UsernameNotFoundException(email);
        } // no such email user

        if(!account.isEmailVerified()){
            return false;
        } // not verified user

        Context context = new Context();
        context.setVariable("username", account.getUsername());
        context.setVariable("host", appProperties.getHost());

        String message = templateEngine.process("mail/sendUsername", context);

        EmailMessage build = EmailMessage.builder()
                .to(account.getEmail())
                .subject("Buravel 아이디 찾기")
                .message(message)
                .build();

        emailService.sendEmail(build);
        return true;
    }
}
