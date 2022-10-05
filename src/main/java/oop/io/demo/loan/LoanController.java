package oop.io.demo.loan;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.ReadOnlyProperty;

@RestController
public class LoanController {
    @Autowired
    private LoanRepository repository;
    
    @Autowired
    private LoanService loanService;

    @PostMapping("/loan")
    public String addBooking(@RequestBody Loan loan){
        return loanService.addBooking(loan);
    }

    @DeleteMapping("/loan/all/{loanID")
    public String deleteBooking(@PathVariable String loanID){
        return loanService.deleteBooking(loanID);
    }
    
    //loan controller should:
    //have method endpoint: "newbooking" calls method in loanservice to make new booking
    ////access: both staff and admin can access to make booking for themself- userEmail automatically assigned based on their identity

    //have method endpoint: "cancelbooking" calls loanservice to cancel booking
    ////access: both staff and admin
    
    //have method to allow user to report loss of card (associated with booking?) endpoint: reportloss

    //have method endpoint: "loans" calls loanservice to retrieve loan for a user by email
    ////access: staff can only see their own but admin can see for any selected user

    //have method to retrieve all bookings made on a certain date for a certain attraction
    ////access: all because it is for calendar display- should this be under service then?
    
}