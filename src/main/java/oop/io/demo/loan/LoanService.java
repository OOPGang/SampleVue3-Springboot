package oop.io.demo.loan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import oop.io.demo.loan.LoanRepository;

@Service
public class LoanService {

    @Autowired
    private LoanRepository repository;
    //loan service should:
    //have method be able to change pass status (when pass is made available)
    
    //method to allow user to make booking

    //method to allow user to cancel booking 
    public String addBooking(Loan loan){
        repository.save(loan);
        return "Booking to " + loan.getAttractionName() + " made by " + loan.getUserEmail() + " has been added.";
    }

    public String deleteBooking(String loanID){
        Loan loan = repository.getByID(loanID);
        repository.delete(loan);
        return "Booking to " + loan.getAttractionName() + " made by " + loan.getUserEmail() + " has been deleted.";
    }

    public String updateBooking(Loan loan){
        repository.save(loan);
        return "";

    }
    
}
