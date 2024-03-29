/**
 * Contains endpoints for Staff and Admins to get loans
 * 1. Post new loan booking by /loan/book
 * 2. Get number of loans by /loan/getbookingcount
 * 3. Get Pass by attraction /loan/getLoanID/{userEmail}
 * 4. Get loanID /loan/getLoanID/{userEmail}
 * 5. Change status to cancelled /loan/cancel
 * 6. Delete a new booking /loan/delete/{loanId}
 * 7. Get the previous borrowers of the pass /loan/previousborrower
 * 8. Get all loans by userEmail /loan/{userEmail}
 * 9. Get the availability of the passes using /loan/availpasses
 */

package oop.io.demo.loan;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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

import oop.io.demo.pass.PASSSTATUS;
import oop.io.demo.pass.Pass;
import oop.io.demo.pass.PassRepository;
import oop.io.demo.user.UserRepository;
import oop.io.demo.user.UserService;
import oop.io.demo.attraction.AttractionRepository;
import oop.io.demo.auth.security.jwt.JwtUtils;
import oop.io.demo.mail.*;
import oop.io.demo.mail.payload.BookingRequest;

@CrossOrigin(origins = "*", maxAge = 7000)
@RestController
@RequestMapping("/loan")
@Controller
public class LoanController {
    
    @Autowired
    JwtUtils jwtUtils;

    LOANSTATUS passStatus = LOANSTATUS.ACTIVE;
    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PassRepository passRepository;
    @Autowired
    private AttractionRepository attractionRepository;
    @Autowired
    private LoanService loanService;
    @Autowired
    private EmailSender emailSender;

    //POST new loan booking
    @PostMapping("/book")
    public ResponseEntity addBooking(@RequestBody LoanRequest loanRequest) {

        String userEmail = loanRequest.getUserEmail();
        //check if user exists
        if(!userRepository.findByEmail(userEmail).isPresent()) return ResponseEntity.badRequest().body("User not found");
        Double outstandingFee = userRepository.findByEmail(userEmail).get().getOutstandingFees();
        
        if (outstandingFee > 0){
            return ResponseEntity.badRequest().body("You are currently on penalty hold and cannot make the booking. Please settle your outstanding fees first before booking.");
        }

        String attractionName = loanRequest.getAttractionName();
        int noOfPass = loanRequest.getNoOfPass();

        //get and parse loan date
        String dateString = loanRequest.getLoanDate();
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate loanDate = LocalDate.parse(dateString, dateFormat);
        if(noOfPass==0) return ResponseEntity.badRequest().body("Please enter the number of passes you want (1 or 2)");
        
        loanService = new LoanService(loanRepository, passRepository, userRepository, attractionRepository);
        if (checkBooking(userEmail, loanDate)) {
            for(int i=0; i<noOfPass; i++) {
                Loan loan = loanService.addBooking(userEmail, loanDate, attractionName, "1");
                try {
                    BookingRequest booking = new BookingRequest(userEmail, loan.getLoanID());
                    emailSender.sendAttachmentMessage(booking);
                } catch (Exception e) {
                    return ResponseEntity.badRequest().body("Booking error.");
                }
            }
            String passesCreated = noOfPass== 1? "One pass": "Two passes";
            return ResponseEntity.ok(passesCreated+ " created for " + attractionName + " for use on " + loanDate);
        } else {
            return ResponseEntity.badRequest().body("Booking already made for user on this date");
        }
    }
    
    //check booking exists
    public Boolean checkBooking(String userEmail, LocalDate loanDate) {
        for (Loan loan : loanRepository.findAll()) {
            if (loan.getUserEmail().equals(userEmail) && loan.getLoanDate().equals(loanDate)) {
                return false;
            }
        }
        return true;
    }

    //GET number of bookings made
    @GetMapping("/getbookingcount")
    public int countBooking(@RequestParam("userEmail") String userEmail) {
        ArrayList<Loan> loan = loanRepository.findAllByUserEmail(userEmail);
        List<Loan> thisMonthsLoans = loan.stream().filter(l-> l.getLoanDate().getMonth().equals(LocalDate.now().getMonth())).collect(Collectors.toList());
        if (!loan.isEmpty()) {
            return thisMonthsLoans.size();
        } else {
            return 0;
        }
    }

    //GET loanID based on userEmail
    @GetMapping("/getLoanID/{userEmail}")
    public ResponseEntity getLoanID(@PathVariable("userEmail") String loanId) {
        Optional<Loan> loan = this.loanRepository.findById(loanId);
        if (loan != null) {
            return ResponseEntity.ok("Loan ID is: " +loan);
        } else {
            return ResponseEntity.ok("No loans made" );
        }
    }

    //to set a loan status to cancelled
    @GetMapping("/cancel")
    public ResponseEntity cancellLoan(@RequestParam("loanId") String loanId) {
        ResponseEntity responseEntity = new LoanService(loanRepository, passRepository, userRepository, attractionRepository)
                .changeLoanStatus(loanId, LOANSTATUS.CANCELLED);
        return responseEntity;
    }

    //to set a loan status to lost
    @GetMapping("/lost")
    public ResponseEntity ReportLoss(@RequestParam("loanId") String loanId) {
        ResponseEntity responseEntity = new LoanService(loanRepository, passRepository, userRepository, attractionRepository)
                .ReportLoss(loanId, LOANSTATUS.LOST);
        return responseEntity;
    }

    //DELETE loan booking
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

    //GET previous borrower of a pass
    @GetMapping("/previousborrower")
    public ResponseEntity getPreviousBorrower(@RequestParam(value="passId") String passId, @RequestParam(value="date") String dateString) {
        LocalDate loanDate = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        System.out.println(loanDate);
        UserService userService = new UserService(userRepository);
        List<Loan> loan = loanRepository.findAllByPassId(passId);
        System.out.println(loan);
        for(Loan l: loan) {
            if(l.getLoanDate().equals(loanDate.minusDays(1))) {
                return ResponseEntity.ok(userService.getPublicUserDetailsByEmail(l.getUserEmail()));
            }
        } 
        if(loanDate.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
            for(Loan l: loan) {
                if(l.getLoanDate().equals(loanDate.minusDays(2))) {
                    return ResponseEntity.ok(userService.getPublicUserDetailsByEmail(l.getUserEmail()));
                }
            } 
        }
        return ResponseEntity.ok("No borrowers the day before you. Please check back the Friday before your loan to see if there is a borrower. Otherwise, collect your pass(es) from the General Office.");
    }

    //GET all loans based on userEmail
    @GetMapping("/{userEmail}")
    public ResponseEntity getLoansByUserEmail(@PathVariable("userEmail") String userEmail){
        List<Loan> loans = loanRepository.findAllByUserEmail(userEmail);
        if(!loans.isEmpty()) {
            List<LoanResponse> loanResponses = new ArrayList<>();
            for(Loan l: loans) {
                PASSSTATUS status = null;
                Boolean present = passRepository.findById(l.getPassId()).isPresent();
                if(present) status = passRepository.findById(l.getPassId()).get().getPassStatus();
                loanResponses.add(new LoanResponse(l.getLoanDate().toString(), l.getDueDate(), l.getAttractionName(), l.getUserEmail(), l.getLoanId(),
                l.getName(), l.getContactNo(), l.getPassId(), l.getStatus(), status));
            }
            return ResponseEntity.ok(loanResponses);
        }
        else {
            return ResponseEntity.badRequest().body("User was not found!");
        }
    }

    //check the dates of all loans at 9AM from Monday to Friday, and change status of loans to OVERDUE if it is past overdue date
    @Scheduled(cron = "0 0 9 * * MON-FRI")
    public ResponseEntity setOverDueDate() throws Exception {
        try {
            // Take user email to direct the collected message to the user
            ArrayList<Loan> reminderLoans = loanRepository.findAllByStatus("CONFIRMED");
            ArrayList<Loan> savedLoans = new ArrayList<>();
            for (Loan loan : reminderLoans) {
                LocalDate currentDate = LocalDate.now();
                
                DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate loanDate = LocalDate.parse(loan.getDueDate(), dateFormat);

                System.out.println(currentDate.compareTo(loanDate) < 0);
                if (currentDate.compareTo(loanDate) > 0) {
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

    //check whether the dates are equal
    public static boolean isSameDay(LocalDate date1, LocalDate date2) {
        return date1.equals(date2);
    }

    //check the dates of all loans at 9AM from Monday to Friday, and change status of loans to REMINDER if it is 1 day before the loan date
    @Scheduled(cron = "0 0 9 * * MON-FRI") 
    public ResponseEntity setReminder() throws Exception {
        try {
            // Take user email to direct the collected message to the user
            ArrayList<Loan> reminderLoans = loanRepository.findAllByStatus("CONFIRMED");
            ArrayList<Loan> savedLoans = new ArrayList<>();
            for (Loan loan : reminderLoans) {
                LocalDate currentDate = LocalDate.now();
                LocalDate oneDayBefore = currentDate.minusDays(1);

                if (isSameDay(currentDate, oneDayBefore)) {
                    loan.setStatus(LOANSTATUS.REMINDER);
                    savedLoans.add(loan);

                }

            }
            loanRepository.saveAll(savedLoans);
            return ResponseEntity.ok("Overdue sent!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Overdue not sent.");
        }
    }


    //GET the availability of each pass
    @GetMapping("/availpasses")
    public ResponseEntity availPass() throws Exception {
        try {
            return ResponseEntity.ok(loanService.checkUnavailPasses());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Passes can't be checked right now.");
        }
    }


}