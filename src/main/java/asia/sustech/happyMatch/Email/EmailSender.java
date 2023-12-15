package asia.sustech.happyMatch.Email;

import com.sun.mail.util.MailSSLSocketFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.security.GeneralSecurityException;
import java.util.Properties;

public class EmailSender {

    private final String host;
    private final int port;
    private final String username;
    private final String password;

    public EmailSender(String host, int port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public void sendEmail(String recipient, String subject, String htmlBody) throws MessagingException,
            GeneralSecurityException {
        Properties props = new Properties();
        // 发送服务器需要身份验证
        props.setProperty("mail.smtp.auth", "true");
        // 设置企业微信邮件服务器  企业邮箱
        props.setProperty("mail.smtp.host", host);
        // 设置企业微信邮件服务器端口
        props.setProperty("mail.smtp.port", String.valueOf(port));
        // 发送邮件协议名称
        props.setProperty("mail.transport.protocol", "smtp");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        MailSSLSocketFactory sf = new MailSSLSocketFactory();
        sf.setTrustAllHosts(true);
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.ssl.socketFactory", sf);
        
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
        message.setSubject(subject);

        // Create the HTML part
        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(htmlBody, "text/html");

        // Create the Multipart object and add the HTML part to it
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(htmlPart);

        // Set the content of the message to the Multipart object
        message.setContent(multipart);
        try {
            Transport.send(message);
            System.out.println("Email sent successfully to " + recipient);
        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Failed to send email to " + recipient);
        }
    }
}
