package asia.sustech.happyMatch.Email;

import asia.sustech.happyMatch.Config.Config;

import javax.mail.MessagingException;
import java.security.GeneralSecurityException;

class EmailSenderTest {
    public static void main(String[] args) {
        Config.init();
        EmailSender emailSender = new EmailSender(Config.email_Host, Config.email_Port, Config.email_User,
                Config.email_Pwd);
        try {
            emailSender.sendEmail("3070073429@qq.com", "test", "test");
        } catch (MessagingException | GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }
}