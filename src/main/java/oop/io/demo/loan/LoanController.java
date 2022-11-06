package oop.io.demo.loan;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    private LoanRepository repository;
    @Autowired
    private UserRepository userrepository;
    @Autowired
    private PassRepository passrepository;
    @Autowired
    private LoanService loanService;



    @Autowired
    JwtUtils jwtUtils;
    @PostMapping("/book")
    public String addBooking(@RequestBody LoanRequest loanRequest){
        String userEmail = loanRequest.getUserEmail();
        Date loanDate = loanRequest.getLoanDate();
        String attractionName = loanRequest.getAttractionName();
        LoanService loanService= new LoanService(repository,passrepository,userrepository);
        return loanService.addBooking(userEmail, loanDate, attractionName);

    }

    @PostMapping("/cancel")
    public ResponseEntity cancellLoan(@RequestBody String loanID) {
        String cancel=new LoanService(repository,passrepository,userrepository).cancelLoan(loanID, LOANSTATUS.CANCELLED);
        return ResponseEntity.ok(cancel);
    }
    
    @PostMapping("/reportloss")
    public ResponseEntity reportLoss(@RequestBody String userEmail, Date loanDate){
        String result= new LoanService(repository,passrepository,userrepository).ReportLoss(userEmail, loanDate);
        return ResponseEntity.ok(result);
    }
    // //for creating new passes for an existing attraction
    // @PostMapping("{placeOfInterest}/newpass")
    // public ResponseEntity createPasses(@PathVariable("placeOfInterest") String placeOfInterest, @RequestBody PassRequest passRequest) {
    //     PassService passService = new PassService(repository);
    //     passService.createPass(placeOfInterest, passRequest);
    //     return ResponseEntity.ok("Uploaded");
    // }

    @DeleteMapping("/all/{loanId}")
    public ResponseEntity deleteBooking(@PathVariable String loanID) {
        Optional<Loan> loan = this.repository.findById(loanID);
        if(loan.isPresent()){
            this.repository.deleteById(loanID);
            return ResponseEntity.ok("Successfully deleted.");
        }
        else {
            return ResponseEntity.ok("Loan: " + loan + " was not found.");
        }
    }

    // @GetMapping("/{userEmail}")
    // public ResponseEntity<List<Loan>> getLoanByUserEmail(@PathVariable("userEmail") String userEmail) {
        
    //     List<Loan>uEmail=new LoanService(repository).checkLoan(userEmail);
    //     return ResponseEntity.ok(uEmail);

    // }





    

    



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