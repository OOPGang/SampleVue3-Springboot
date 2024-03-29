/**
 * Contains endpoints for STAFF & ADMIN to
 * 1. Receive an email on their booking details [POST] /email/booking
 * 2. Receive an email to indicate that they have collected their pass [POST] /email/collected
 * 
 * There are scheduled emails as well for the following:
 * 1. Reminder emails one day before booking (7am Everyday)
 * 2. Overdue emails to remind them to return their passes (10am every week from Monday to Friday)
*/

package oop.io.demo.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.CrossOrigin;

import oop.io.demo.attraction.Attraction;
import oop.io.demo.attraction.AttractionRepository;
import oop.io.demo.auth.security.jwt.JwtUtils;
import oop.io.demo.loan.Loan;
import oop.io.demo.loan.LoanRepository;
import oop.io.demo.mail.payload.BookingRequest;
import oop.io.demo.mail.payload.CollectedRequest;
import oop.io.demo.user.User;
import oop.io.demo.user.UserRepository;
import java.util.*;

import javax.validation.Valid;

@CrossOrigin(maxAge = 3600)
@Controller
@RequestMapping("/email")
public class EmailSender {

    @Autowired
    JwtUtils jwtUtils;
    
    @Autowired
    EmailService emailService;

    private final UserRepository userRepository;

    private final LoanRepository loanRepository;

    private final AttractionRepository attractionRepository;

    public EmailSender(UserRepository userRepository, LoanRepository loanRepository, AttractionRepository attractionRepository) {
        this.userRepository = userRepository;
        this.loanRepository = loanRepository;
        this.attractionRepository = attractionRepository;
    }
    
    // Sending Email Templates With/Without Attachments --> For Confirmed Bookings
    @PostMapping("/booking")
    public ResponseEntity sendAttachmentMessage(@Valid @RequestBody BookingRequest bookingRequest) throws Exception {
        try{
            //Take user email and loanId out of Booking Request
            String emailTo = bookingRequest.getEmail();
            String loanId = bookingRequest.getLoanId();
            User user = userRepository.findByEmail(emailTo).get();
            Loan loan = loanRepository.findByLoanId(loanId);

            //Create email
            Email email = new Email();
            email.setTo(emailTo);
            email.setFrom("oopg2t4@outlook.com");
            email.setSubject("Booking Confirmation");
            email.setContent("Sending mail");

            //Set values into email template - name, attractionname, passno, loandate
            Map<String, Object> model = new HashMap<>();
            model.put("name", user.getName());
            String attractionName = loan.getAttractionName();
            model.put("attractionName", attractionName);
            model.put("corpPassNumber", loan.getPassId());
            
            String dateString = loan.getLoanDate().toString();

            model.put("loanDate", dateString);
            email.setModel(model);
    
            // Prepare template path and attachment path (if found)
            Attraction attraction = attractionRepository.findByAttractionName(attractionName).get();
            String template = attraction.getTemplateFilename();
            String templatePath = attraction.getTemplateFilePath();
            String attachment = attraction.getAttachmentPDFFilename();

            // two ways to send email dependent on whether there is attachment or not 
            if (attachment == null){
                emailService.sendEmailTemplate(email, template, templatePath);
            } else {
                String attachmentPath = attraction.getAttachmentPDFFilePath();
                emailService.sendEmailWithAttachment(email, template, templatePath, attachment, attachmentPath);
            }
            return ResponseEntity.ok("Check your email for your booking information!");
            
        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Booking was unsuccessful.");
        }
    }
    // Sending Pass Collected Emails
    @PostMapping("/collected")
    public ResponseEntity sendCollectedMessage(@Valid @RequestBody CollectedRequest collectedRequest) throws Exception {
        try {
            //Take user email to direct the collected message to the user
            String email = collectedRequest.getEmail();
            String subject = "[Notification] Pass Collected!";
            String template = "Pass Collected Email.html";
            emailService.sendSimpleEmail(email,subject,template);
            return ResponseEntity.ok("Email sent!");
        } catch (Exception e){
            return ResponseEntity.badRequest().body("Email is not sent.");
        }
    }

    // Sending To Collect Emails
    @Scheduled(cron = "0 0 7 * * *")
    public ResponseEntity sendToCollectMessage() throws Exception {
        try {
            //Take user email to direct the collected message to the user
            ArrayList<Loan> reminderLoans = loanRepository.findAllByStatus("REMINDER");
            for (Loan loan: reminderLoans){
                String email = loan.getUserEmail();
                String subject = "[Pass Notification] Collect By Today!";
                String template = "To Collect Pass Email.html";
                emailService.sendSimpleEmail(email,subject,template);
            }
            return ResponseEntity.ok("Reminder emails sent!");
        } catch (Exception e){
            return ResponseEntity.badRequest().body("Email is not sent.");
        }
    }

    // Sending Overdue Emails
    @Scheduled(cron = "0 0 10 * * MON-FRI")
    public ResponseEntity sendOverdueMessage() throws Exception {
        try {
            //Take user email to direct the collected message to the user
            ArrayList<Loan> overdueLoans = loanRepository.findAllByStatus("OVERDUE");
            for (Loan loan: overdueLoans){
                String email = loan.getUserEmail();
                String subject = "[Pass Notification] Overdue!";
                String template = "Overdue Pass Email.html";
                emailService.sendSimpleEmail(email,subject,template);
            }
            return ResponseEntity.ok("Overdue emails sent!");
        } catch (Exception e){
            return ResponseEntity.badRequest().body("Email is not sent.");
        }
    }
}