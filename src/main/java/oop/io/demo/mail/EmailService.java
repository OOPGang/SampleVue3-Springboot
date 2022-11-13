package oop.io.demo.mail;

import freemarker.template.Configuration;
import freemarker.template.Template;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.io.IOException;

@Service
public class EmailService {
    @Autowired
    JavaMailSender javaMailSender;
    @Qualifier("getFreeMarkerConfiguration")
    @Autowired
    Configuration fmConfiguration;

    //Confirmation Token Email Session
    public Session session(){
        final String username = "oopg2t4@outlook.com";
        final String password = "g2t4OOP!";

        Properties prop = new Properties();
		prop.put("mail.smtp.host", "smtp.office365.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true"); //TLS
        
        return Session.getInstance(prop, new javax.mail.Authenticator(){
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
    }
    //Confirmation Token Email
    public void sendEmail(Email mail) {
        try {
            Message message = new MimeMessage(session());
            message.setFrom(new InternetAddress("oopg2t4@outlook.com"));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(mail.getTo())
            );
            message.setSubject(mail.getSubject());
            message.setText(mail.getContent());

            Transport.send(message);
            System.out.println("Done");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    // Collected Email/To Collect Email/Overdue Email
    public void sendSimpleEmail(String email, String subject, String template) throws Exception {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setFrom("singaporesportsschooltest@outlook.com");
            mimeMessageHelper.setSubject(subject);
    
            fmConfiguration.setClassForTemplateLoading(this.getClass(), "/templates");
            Template t = fmConfiguration.getTemplate(template);

            String text = FreeMarkerTemplateUtils.processTemplateIntoString(t, null);
            mimeMessageHelper.setText(text, true);

            javaMailSender.send(mimeMessageHelper.getMimeMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Template email with modelling --> "Booking Email without Attachment"
    public void sendEmailTemplate(Email mail,  String template, String templatePath) throws Exception {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

            fmConfiguration.setClassForTemplateLoading(this.getClass(), templatePath);
            Template t = fmConfiguration.getTemplate(template);

            String text = FreeMarkerTemplateUtils.processTemplateIntoString(t, mail.getModel());

            mimeMessageHelper.setSubject(mail.getSubject());
            mimeMessageHelper.setFrom(mail.getFrom());
            mimeMessageHelper.setTo(mail.getTo());
            mimeMessageHelper.setText(text, true);

            javaMailSender.send(mimeMessageHelper.getMimeMessage());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Template email with modelling and attachment --> "Booking Email with Attachment"
    public void sendEmailWithAttachment(Email mail, String template, String templatePath, String attachment, String attachmentPath) throws MessagingException, IOException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

            fmConfiguration.setClassForTemplateLoading(this.getClass(), templatePath);
            Template t = fmConfiguration.getTemplate(template);

            String text = FreeMarkerTemplateUtils.processTemplateIntoString(t, mail.getModel());

            mimeMessageHelper.setSubject(mail.getSubject());
            mimeMessageHelper.setFrom(mail.getFrom());
            mimeMessageHelper.setTo(mail.getTo());
            mimeMessageHelper.setText(text, true);

            mimeMessageHelper.addAttachment(attachment, new ClassPathResource(attachmentPath));

            javaMailSender.send(mimeMessageHelper.getMimeMessage());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

    /* 
    public void sendTemplateEmail(Email mail) {
        try {
            Message message = new MimeMessage(session());
            message.setFrom(new InternetAddress("oopg2t4@outlook.com"));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(mail.getTo())
                    //InternetAddress.parse("oopg2t4@outlook.com")
            );
            message.setSubject(mail.getSubject());
            message.setText(mail.getContent());

            Transport.send(message);
            System.out.println("Done");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }


    // Simple template email with no modelling --> "Collected Email"
    public void sendSimpleEmailTemplate(Email mail, String template) throws Exception {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

            fmConfiguration.setClassForTemplateLoading(this.getClass(), "/templates");
            Template t = fmConfiguration.getTemplate(template);

            String text = FreeMarkerTemplateUtils.processTemplateIntoString(t, mail.getModel());

            mimeMessageHelper.setSubject(mail.getSubject());
            mimeMessageHelper.setFrom(mail.getFrom());
            mimeMessageHelper.setTo(mail.getTo());
            mimeMessageHelper.setText(text, true);

            javaMailSender.send(mimeMessageHelper.getMimeMessage());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
     */