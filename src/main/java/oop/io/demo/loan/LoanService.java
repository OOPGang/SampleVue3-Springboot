package oop.io.demo.loan;

import java.text.*;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import oop.io.demo.loan.LoanRepository;

@Service
//Service component is used to annotate class at service layer, build business logic
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

    public String deleteBooking(String userEmail, Date loanDate){
        Loan loan = repository.getLoan(userEmail, loanDate);
        repository.delete(loan);
        return "Booking to " + loan.getAttractionName() + " made by " + loan.getUserEmail() + " has been deleted.";
    }

    public String LoanCollect(String userEmail){
        Calendar cal = Calendar.getInstance();
        Date todaydate = cal.getTime();
        String checkID = userEmail + todaydate;
        Loan loan = repository.findbyLoanID(checkID);
        loan.setStatus(LOANSTATUS.ACTIVE);
        //trigger email here
        return "Loan has been collected by the user";

    }

    //GO to check whether booking presents or not 
    public String checkLoan(String userEmail){
        ArrayList<Loan> loanlist = repository.findByEmail(userEmail);
        Calendar cal = Calendar.getInstance();
        Date todaydate = cal.getTime();
        for (int i=0; i<loanlist.size(); i++){
            Loan loan = loanlist.get(i);
            Date checkdate = loan.getLoanDate();
            if (todaydate == checkdate){
                return "Booking exists on " + todaydate;
            } else {
                return "Booking does not exist on " + todaydate;
            } 
        } 
        return "";

    }

    public String checkAvail(String loanDate, String attractionName){
        Loan loan = repository.findBy

    }

    public String ReportLoss(String userEmail, Date loanDate){
        String checkID = userEmail + loanDate;
        Loan loan = repository.findbyLoanID(checkID);
        loan.setStatus(LOANSTATUS.LOST);
        int passNo = loan.getPassNo();
        cancelAllLoans(passNo);
        return "";
    }
    public String cancelAllLoans(int passNo){
        
    }
    
    
}
