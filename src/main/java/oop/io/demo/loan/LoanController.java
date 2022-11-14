package oop.io.demo.loan;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

import javax.management.Query;
import javax.xml.transform.Source;

import org.hibernate.Criteria;
import org.hibernate.sql.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;


import oop.io.demo.auth.security.jwt.JwtUtils;
import oop.io.demo.pass.PassRepository;
import oop.io.demo.user.UserRepository;

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/loan")
@Controller
public class LoanController {
    //loan controller should:
    //have method endpoint: "newbooking" calls method in loanservice to make new booking
    ////access: both staff and admin can access to make booking for themself- userEmail automatically assigned based on their identity



    LOANSTATUS passStatus =LOANSTATUS.ACTIVE;
    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PassRepository passRepository;
    @Autowired
    private LoanService loanService;



    @Autowired
    JwtUtils jwtUtils;
    @PostMapping("/book")
    public String addBooking(@RequestBody LoanRequest loanRequest){

        String userEmail = loanRequest.getUserEmail();
        Date loanDate = loanRequest.getLoanDate();
     
        String attractionName = loanRequest.getAttractionName();
        LoanService loanService= new LoanService(loanRepository,passRepository,userRepository);
        return loanService.addBooking(userEmail, loanDate, attractionName);
    }

    @GetMapping("/cancel")
    public ResponseEntity cancellLoan(@RequestBody Map<String, String> loanIdMap) {
        String loanId = loanIdMap.get("loanId");//key JSON in postman
        ResponseEntity responseEntity = new LoanService(loanRepository, passRepository, userRepository).changeLoanStatus(loanId, LOANSTATUS.CANCELLED);
        return responseEntity;
    }
    @GetMapping("/lost")
    public ResponseEntity ReportLoss(@RequestBody Map<String, String> loanIdMap) {
        String loanId = loanIdMap.get("loanId");//key JSON in postman
        ResponseEntity responseEntity = new LoanService(loanRepository, passRepository, userRepository).changeLoanStatus(loanId, LOANSTATUS.LOST);
        return responseEntity;
       
    }   


    @DeleteMapping("/delete/{loanId}")
    public ResponseEntity deleteBooking(@RequestBody Map<String, String> loanIdMap) {
        String loanId = loanIdMap.get("loanId");
        Optional<Loan> loan = this.loanRepository.findById(loanId);
        if(loan.isPresent()){
            this.loanRepository.deleteById(loanId);
            return ResponseEntity.ok("Successfully deleted.");
        }
        else {
            return ResponseEntity.ok("Loan: " + loan + " was not found.");
        }
    }


    @GetMapping("/{userEmail}")
    public ResponseEntity getLoanByUserEmail(@PathVariable("userEmail") String userEmail){
        Optional<Loan> loan = loanRepository.findByUserEmail(userEmail);
        if(loan.isPresent()) {
            return ResponseEntity.ok(loan.get());
        }
        else {
            return ResponseEntity.badRequest().body("User was not found!");
        }
    }

    @Scheduled(cron = "0 0 9 * * MON-FRI")//overdue sent at 9AM

    public ResponseEntity setOverDueDate() throws Exception {
        try {
            //Take user email to direct the collected message to the user
            ArrayList<Loan> reminderLoans = loanRepository.findAllByStatus("CONFIRMED");
            ArrayList<Loan>savedLoans=new ArrayList<>();
            for (Loan loan: reminderLoans){
                Date currentDate=new Date();
           
        
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyy");
  
    
            
                System.out.println(currentDate.compareTo(loan.getDueDate())<0);
                if(currentDate.compareTo(loan.getDueDate())>0){
                    loan.setStatus(LOANSTATUS.OVERDUE);
                    savedLoans.add(loan);
                    System.out.println("Overdue Status Set");

                }
            }
            
            loanRepository.saveAll(savedLoans);
          
            return ResponseEntity.ok("Overdue sent!");
        } catch (Exception e){
            return ResponseEntity.badRequest().body("Overdue not sent.");
        }
    }


    public static boolean isSameDay(Date date1, Date date2) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        return fmt.format(date1).equals(fmt.format(date2));
    }
    

    @Scheduled(cron = "0 0 9 * * MON-FRI")//Reminder sent at 9am 1 day before

    public ResponseEntity setReminder() throws Exception {
        try {
            //Take user email to direct the collected message to the user
            ArrayList<Loan> reminderLoans = loanRepository.findAllByStatus("CONFIRMED");
            ArrayList<Loan>savedLoans=new ArrayList<>();
            for (Loan loan: reminderLoans){
                Date currentDate=new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyy");
                Calendar cal = Calendar.getInstance();
                cal.setTime(loan.getDueDate());
                cal.add(Calendar.DAY_OF_YEAR,-1);
                Date oneDayBefore= cal.getTime();
                
              
                if(isSameDay(currentDate, oneDayBefore)){
                    loan.setStatus(LOANSTATUS.OVERDUE);
                    savedLoans.add(loan);
                
               
                    
                }
                
            }
            loanRepository.saveAll(savedLoans);
            return ResponseEntity.ok("Overdue sent!");
        } catch (Exception e){
            return ResponseEntity.badRequest().body("Overdue not sent.");
        }
    }

    /**
  * GET /read  --> Read a booking by booking id from the database.
  */




    //have method endpoint: "cancelbooking" calls loanservice to cancel booking
    ////access: both staff and admin

    //have method to allow user to report loss of card (associated with booking?) endpoint: reportloss

    //have method endpoint: "loans" calls loanservice to retrieve loan for a user by email
    ////access: staff can only see their own but admin can see for any selected user
//have method to retrieve all bookings made on a certain date for a certain attraction
    ////access: all because it is for calendar display- should this be under service then?
    
}