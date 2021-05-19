package buravel.buravel.modules.account.event;

import buravel.buravel.infra.AppProperties;
import buravel.buravel.infra.mail.EmailMessage;
import buravel.buravel.infra.mail.EmailService;
import buravel.buravel.modules.account.Account;
import buravel.buravel.modules.account.AccountDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;



@Async
@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class AccountEventListener {
    private final EmailService emailService;
    private final TemplateEngine templateEngine;

    @EventListener
    public void handleAccountEvent(TempPasswordEvent event) {
        Account account = event.getAccount();
        sendTempPassword(account,event.getTempPassword());
    }

    @EventListener
    public void handleSignUpConfirmEvent(SignUpConfirmEvent event) {
        //throw new RuntimeException();
        //AccountDto account = event.getAccount();
        sendEmailCheckToken(event.getEmail(), event.getToken());
    }

    private void sendEmailCheckToken(String email, String token) {
        Context context = new Context(); // model에 내용담아주듯이
        context.setVariable("token", token);
        //context.setVariable("username", account.getUsername()); 아직 가입한 사람이 아니므로 굳이 닉네임 넣을 필요 없어보임
        context.setVariable("message","Buravel 서비스 사용을 위해 코드를 복사하여 붙여넣어주세요.");

        String message = templateEngine.process("mail/simple-link", context);

        EmailMessage build = EmailMessage.builder()
                .to(email)
                .subject("Buravel 회원 가입 인증")
                .message(message)
                .build();

        emailService.sendEmail(build);
    }

    private void sendTempPassword(Account account, String tempPassword) {
        Context context = new Context();
        context.setVariable("username", account.getUsername());
        context.setVariable("message", tempPassword);
        String message = templateEngine.process("mail/tempPassword", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .subject("Buravel - 임시 비밀번호 발급 안내")
                .to(account.getEmail())
                .message(message)
                .build();

        emailService.sendEmail(emailMessage);
    }
}
