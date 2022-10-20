package oop.io.demo.mail;

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import java.util.Properties;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

@Service
public class EmailService {
    @Autowired
    JavaMailSender javaMailSender;
    @Qualifier("getFreeMarkerConfiguration")
    @Autowired
    Configuration fmConfiguration;

    // EMAIL FUNCTIONS THAT WORK ----------------------------------------------------------------------
    // Normal Email
    public void sendEmail(Email mail) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(mail.getTo());
        msg.setFrom(mail.getFrom());
        msg.setSubject(mail.getSubject());
        msg.setText(mail.getContent());
        javaMailSender.send(msg);
    }

    // Simple template email with no modelling
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

    // Template email with modelling
    public void sendEmailTemplate(Email mail,  String template) throws Exception {
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

    public void sendEmailWithAttachment(Email mail, String template) throws MessagingException, IOException {
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
            mimeMessageHelper.addAttachment("pug.jpg", new ClassPathResource("attachments/pug.jpg"));

            javaMailSender.send(mimeMessageHelper.getMimeMessage());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
