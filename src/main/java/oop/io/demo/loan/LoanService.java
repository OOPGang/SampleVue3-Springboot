package oop.io.demo.loan;

import java.text.*;
import java.util.*;

import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import oop.io.demo.loan.LoanRepository;
import oop.io.demo.pass.PassRepository;
import oop.io.demo.user.UserRepository;

@Service
//Service component is used to annotate class at service layer, build business logic
public class LoanService {

    @Autowired
    private LoanRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PassRepository passRepository;
    //loan service should:
    //have method be able to change pass status (when pass is made available)
    
    //method to allow user to make booking

    //method to allow user to cancel booking 
    public String addBooking(Loan loan){
        Date loanDate = loan.getLoanDate();
        String attractionName = loan.getAttractionName();
        if (checkAvail(loanDate,attractionName)){
            repository.save(loan);
        return "Booking to " + loan.getAttractionName() + " made by " + loan.getUserEmail() + " has been added.";
        }
        getUserInfo(loanDate,attractionName);
        return "Booking is unsuccessful.";
    }

    public String cancelLoan(Loan loan){
        loan.setStatus(LOANSTATUS.CANCELLED);
        return "Booking has been cancelled.";
    }
    
    /* 
    public String deleteBooking(String userEmail, Date loanDate){
        Loan loan = repository.getLoan(userEmail, loanDate);
        repository.delete(loan);
        return "Booking to " + loan.getAttractionName() + " made by " + loan.getUserEmail() + " has been deleted.";
    } */


    //Method for GO to update the status of the loanpass once user has collected
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
        ArrayList<Loan> loanlist = repository.findAllByEmail(userEmail);
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

    //The system checks whether a booking for an attraction on a date is available or not.
    public boolean checkAvail(Date loanDate, String attractionName){
        ArrayList<Loan> loans = repository.findAllByAttraction(attractionName);
        for (Loan loan : loans) {
            Date checkdate = loan.getLoanDate();
            if (checkdate == loanDate){
                return false;
            }
        }
        return true;

    }

    //Method for getting userinfo of a loan
    public String getUserInfo(Date loanDate, String attractionName){
        ArrayList<Loan> loans = repository.findAllByAttraction(attractionName);
        for (Loan loan : loans) {
            Date checkdate = loan.getLoanDate();
            if (checkdate == loanDate){
                int passNo = loan.getPassNo();
                String userEmail = loan.getUserEmail();
                User booked_user = userRepository.findByEmail(userEmail);
                String userName = booked_user.getName();
                String contactNo = booked_user.getContactNo();
                return "The booking for pass " + passNo + " is already loaned by " + userName + " (Contact No: " + contactNo +").";

    }

    //Method for the user to report loss of cards
    public String ReportLoss(String userEmail, Date loanDate){
        String checkID = userEmail + loanDate;
        Loan loan = repository.findbyLoanID(checkID);
        loan.setStatus(LOANSTATUS.LOST);
        int passNo = loan.getPassNo();
        Date date = loan.getLoanDate();
        cancelAllLoans(passNo, date);
        return "";
    }

    //Method to cancel all loans
    public String cancelAllLoans(int passNo, Date date){
        ArrayList<Loan> loans = repository.findAllByPassNo(passNo);
        if (!loans.isEmpty()){
            for (Loan loan : loans) {
                Date checkdate = loan.getLoanDate();
                //to check whether the loan is made for later dates
                if (date.compareTo(checkdate) < 0){
                    loan.setStatus(LOANSTATUS.LOST); 
                }
            }
        }
        return "All changes have been made";
    }
}
