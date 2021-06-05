package buravel.buravel.infra.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Slf4j
@Profile("dev")
@Component
@RequiredArgsConstructor
public class HtmlEmailService implements EmailService {

    private final JavaMailSender javaMailSender;

    @Override
    public void sendEmail(EmailMessage emailMessage) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8"); // 이미지 넣기 위해 true로
            mimeMessageHelper.setTo(emailMessage.getTo());
            mimeMessageHelper.setSubject(emailMessage.getSubject());
            mimeMessageHelper.setText(emailMessage.getMessage(), true); // html 적용
            // set image files
            mimeMessageHelper.addInline("logo", new ClassPathResource("static/images/logo.png"));
            mimeMessageHelper.addInline("emailImage", new ClassPathResource("static/images/emailImage.png"));
            javaMailSender.send(mimeMessage);
            log.info("sent email : "+emailMessage.getMessage());
        } catch (MessagingException e) {
            log.error("fail to send email",e);
        }
    }
}
