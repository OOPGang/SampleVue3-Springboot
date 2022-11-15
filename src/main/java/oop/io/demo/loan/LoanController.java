package oop.io.demo.loan;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import oop.io.demo.auth.security.jwt.JwtUtils;
import oop.io.demo.pass.PassRepository;
import oop.io.demo.user.UserRepository;
import oop.io.demo.mail.*;
import oop.io.demo.mail.payload.BookingRequest;

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/loan")
@Controller
public class LoanController {
    // loan controller should:
    // have method endpoint: "newbooking" calls method in loanservice to make new
    // booking
    //// access: both staff and admin can access to make booking for themself-
    // userEmail automatically assigned based on their identity

    LOANSTATUS passStatus = LOANSTATUS.ACTIVE;
    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PassRepository passRepository;
    @Autowired
    private LoanService loanService;
    @Autowired
    private EmailSender emailSender;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/book")
    public String addBooking(@RequestBody LoanRequest loanRequest) {

        String userEmail = loanRequest.getUserEmail();
        Date loanDate = loanRequest.getLoanDate();
        String attractionName = loanRequest.getAttractionName();
        int noOfPass = loanRequest.getNoOfPass();
        loanService = new LoanService(loanRepository, passRepository, userRepository);
        DateFormat monthFormat = new SimpleDateFormat("MM");
        String strMonth = monthFormat.format(loanDate);

        if (checkBooking(userEmail, loanDate)) {
            countBooking(userEmail);
            if (noOfPass == 1) {
                Loan loan = loanService.addBooking(userEmail, loanDate, attractionName, "1");
                if (loan != null) {
                    try {
                        BookingRequest booking = new BookingRequest(userEmail, loan.getLoanID());
                        emailSender.sendAttachmentMessage(booking);
                    } catch (Exception e) {
                        return "Booking error.";
                    }
                    return "One pass was created for " + attractionName + " for use on " + loanDate;
                }
            } else {
                Loan loan1 = loanService.addBooking(userEmail, loanDate, attractionName, "1");
                Loan loan2 = loanService.addBooking(userEmail, loanDate, attractionName, "2");
                if (loan1 != null && loan2 != null) {
                    try {
                        BookingRequest booking1 = new BookingRequest(userEmail, loan1.getLoanID());
                        emailSender.sendAttachmentMessage(booking1);
                        BookingRequest booking2 = new BookingRequest(userEmail, loan2.getLoanID());
                        emailSender.sendAttachmentMessage(booking2);
                    } catch (Exception e) {
                        return "Booking error.";
                    }
                    return "Two passes were created for " + attractionName + " for use on " + loanDate;
                }
            }
        }
        return "Booking unsuccessful!";
    }

    public Boolean checkBooking(String userEmail, Date loanDate) {
        Boolean status = true;
        for (Loan loan : loanRepository.findAll()) {
            if (loan.getUserEmail().equals(userEmail) && loan.getLoanDate().equals(loanDate)) {
                status = false;
            }
        }
        return status;
    }

    @GetMapping("/getbookingcount")
    public ResponseEntity countBooking(@RequestParam("userEmail") String userEmail) {
        int count = 0;
        ArrayList<Loan> loan = loanRepository.findAllByUserEmail(userEmail);
        if (loan != null) {
            count = loan.size();
        } else {
            return ResponseEntity.ok("No bookings made for this user" );
        }
        return ResponseEntity.ok("Number of bookings: " + count);
    }

    //endpoint to get loan ID
    @GetMapping("/getLoanID/{userEmail}")
    public ResponseEntity getLoanID(@PathVariable("userEmail") String loanId) {
        Optional<Loan> loan = this.loanRepository.findById(loanId);
        if (loan != null) {
            return ResponseEntity.ok("Loan ID is: " +loan);

        } else {
            return ResponseEntity.ok("No loans made" );

        }

    }

    @GetMapping("/cancel")
    public ResponseEntity cancellLoan(@RequestBody Map<String, String> loanIdMap) {
        String loanId = loanIdMap.get("loanId");// key JSON in postman
        ResponseEntity responseEntity = new LoanService(loanRepository, passRepository, userRepository)
                .changeLoanStatus(loanId, LOANSTATUS.CANCELLED);
        return responseEntity;
    }

    @GetMapping("/lost")
    public ResponseEntity ReportLoss(@RequestBody Map<String, String> loanIdMap) {
        String loanId = loanIdMap.get("loanId");// key JSON in postman
        ResponseEntity responseEntity = new LoanService(loanRepository, passRepository, userRepository)
                .changeLoanStatus(loanId, LOANSTATUS.LOST);
        return responseEntity;
    }

    @DeleteMapping("/delete/{loanId}")
    public ResponseEntity deleteBooking(@RequestBody Map<String, String> loanIdMap) {
        String loanId = loanIdMap.get("loanId");
        Optional<Loan> loan = this.loanRepository.findById(loanId);
        if (loan.isPresent()) {
            this.loanRepository.deleteById(loanId);
            return ResponseEntity.ok("Successfully deleted.");
        } else {
            return ResponseEntity.ok("Loan: " + loan + " was not found.");
        }
    }

    @GetMapping("/allbookingsbyuser")
    public ResponseEntity getLoansByUserEmail(@RequestParam("userEmail") String userEmail){
        List<Loan> loans = loanRepository.findAllByUserEmail(userEmail);
        if(!loans.isEmpty()) {
            return ResponseEntity.ok(loans);
        }
        else {
            return ResponseEntity.badRequest().body("User was not found!");
        }
    }

    @Scheduled(cron = "0 0 9 * * MON-FRI") // overdue sent at 9AM

    public ResponseEntity setOverDueDate() throws Exception {
        try {
            // Take user email to direct the collected message to the user
            ArrayList<Loan> reminderLoans = loanRepository.findAllByStatus("CONFIRMED");
            ArrayList<Loan> savedLoans = new ArrayList<>();
            for (Loan loan : reminderLoans) {
                Date currentDate = new Date();

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyy");

                System.out.println(currentDate.compareTo(loan.getDueDate()) < 0);
                if (currentDate.compareTo(loan.getDueDate()) > 0) {
                    loan.setStatus(LOANSTATUS.OVERDUE);
                    savedLoans.add(loan);
                    System.out.println("Overdue Status Set");

                }
            }

            loanRepository.saveAll(savedLoans);

            return ResponseEntity.ok("Overdue sent!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Overdue not sent.");
        }
    }

    public static boolean isSameDay(Date date1, Date date2) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        return fmt.format(date1).equals(fmt.format(date2));
    }

    @Scheduled(cron = "0 0 9 * * MON-FRI") // Reminder sent at 9am 1 day before

    public ResponseEntity setReminder() throws Exception {
        try {
            // Take user email to direct the collected message to the user
            ArrayList<Loan> reminderLoans = loanRepository.findAllByStatus("CONFIRMED");
            ArrayList<Loan> savedLoans = new ArrayList<>();
            for (Loan loan : reminderLoans) {
                Date currentDate = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyy");
                Calendar cal = Calendar.getInstance();
                cal.setTime(loan.getDueDate());
                cal.add(Calendar.DAY_OF_YEAR, -1);
                Date oneDayBefore = cal.getTime();

                if (isSameDay(currentDate, oneDayBefore)) {
                    loan.setStatus(LOANSTATUS.OVERDUE);
                    savedLoans.add(loan);

                }

            }
            loanRepository.saveAll(savedLoans);
            return ResponseEntity.ok("Overdue sent!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Overdue not sent.");
        }
    }

    @GetMapping("/availpasses")
    public ResponseEntity availPass() throws Exception {
        try {
            return ResponseEntity.ok(loanService.checkUnavailPasses());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Passes can't be checked right now.");
        }
    }

    /**
     * GET /read --> Read a booking by booking id from the database.
     */

    // have method endpoint: "cancelbooking" calls loanservice to cancel booking
    //// access: both staff and admin

    // have method to allow user to report loss of card (associated with booking?)
    // endpoint: reportloss

    // have method endpoint: "loans" calls loanservice to retrieve loan for a user
    // by email
    //// access: staff can only see their own but admin can see for any selected
    // user
    // have method to retrieve all bookings made on a certain date for a certain
    // attraction
    //// access: all because it is for calendar display- should this be under
    // service then?

}