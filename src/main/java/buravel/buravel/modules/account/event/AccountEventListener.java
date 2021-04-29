package buravel.buravel.modules.account.event;

import buravel.buravel.infra.AppProperties;
import buravel.buravel.infra.mail.EmailMessage;
import buravel.buravel.infra.mail.EmailService;
import buravel.buravel.modules.account.Account;
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
