package buravel.buravel.infra.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("local")
@Component
@Slf4j
public class ConsoleEmailService implements EmailService{
    @Override
    public void sendEmail(EmailMessage emailMessage) {
        log.info("sent email : " + emailMessage.getMessage());
        //로컬에선 mailsender로 보낼필요없이 콘솔에 로그만 찍히도록
    }
}
