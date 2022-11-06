package oop.io.demo.loan;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import oop.io.demo.pass.Pass;
import oop.io.demo.pass.PassRepository;
import oop.io.demo.user.User;
import oop.io.demo.user.UserRepository;
import oop.io.demo.pass.PASSSTATUS;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
// Service component is used to annotate class at service layer, build business
// logic
public class LoanService {

    @Autowired
    private LoanRepository repository;
    private final PassRepository passRepository;
    private final UserRepository userRepository;

    Pass p = new Pass();
    User u = new User();

    public LoanService(LoanRepository loanRepository, PassRepository PassRepository, UserRepository UserRepository) {
        this.repository = loanRepository;
        this.passRepository = PassRepository;
        this.userRepository = UserRepository;
    }


    public String extractPassNo(String attractionName){
        String passNo = "";
        Optional<List<Pass>> passList = passRepository.findByAttractionName(attractionName);
            if (passList.isPresent()){
                for (Pass pass: passList.get()){
                    if (pass.getPassStatus() == PASSSTATUS.INOFFICE){
                        passNo = pass.getPassNo();
                        break;
                    }
                }
            }
            return passNo;
    }

    public String extractContactNo(String userEmail){
        String contactNo = "";
        Optional<User> user = userRepository.findByEmail(userEmail);
        if (user.isPresent()){
            contactNo = user.get().getContactNo();
        }
        return contactNo;
    }



    // LoanRepository lr=new LoanRepository();
    // loan service should:
    // have method be able to change pass status (when pass is made available)

    // method to allow user to make booking

    // method to allow user to cancel booking

    public String addBooking(String userEmail, Date loanDate, String attractionName) {
    
        
        Boolean checkPass = checkAvail(loanDate, attractionName);

        if (checkPass) {
            Loan loan = new Loan(userEmail, loanDate, attractionName);
            loan.setLoanId();
            loan.setStatus(LOANSTATUS.CONFIRMED);
            String passNo = extractPassNo(attractionName);
            String contactNo = extractContactNo(userEmail);
            loan.setPassNo(passNo);
            loan.setContactNo(contactNo);
            repository.save(loan);
            return "Booking to " +loanDate+ " made for " +
            attractionName+ " has been added.";
        }

        // if (checkAvail(loanDate,attractionName)){
        // repository.save(loan);
        // return "Booking to " + loan.getAttractionName() + " made by " +
        // loan.getUserEmail() + " has been added.";
        // }
        // getUserInfo(loanDate,attractionName);
        return "Booking unsuccessful, please try again.";
    }

    public String cancelLoan(String loanID, LOANSTATUS loanStatus) {
        Loan l = repository.findByLoanId(loanID);
        l.setStatus(LOANSTATUS.CANCELLED);
        return "Booking has been cancelled.";
    }

    public String deleteBooking(String userEmail, Date loanDate) {
        Loan loan = repository.findByLoanId(userEmail);
        repository.delete(loan);
        return "Booking to " + loan.getAttractionName() + " made by " + loan.getUserEmail() + " has been deleted.";
    }

    // Method for GO to update the status of the loanpass once user has collected
    public String LoanCollect(String userEmail) {
        Calendar cal = Calendar.getInstance();
        Date todaydate = cal.getTime();
        String checkID = userEmail + todaydate;
        Loan loan = repository.findByLoanId(checkID);
        loan.setStatus(LOANSTATUS.ACTIVE);
        // trigger email here
        return "Loan has been collected by the user";

    }
    
    public String checkLoan(String userEmail) {
        ArrayList<Loan> loanlist = repository.findAllByUserEmail(userEmail);
        Calendar cal = Calendar.getInstance();
        Date todaydate = cal.getTime();
        for (int i = 0; i < loanlist.size(); i++) {
            Loan loan = loanlist.get(i);
            Date checkdate = loan.getLoanDate();
            if (todaydate == checkdate) {
                return "Booking exists on " + todaydate;
            } else {
                return "Booking does not exist on " + todaydate;
            }
        }
        return "";

    }

    // The system checks whether a booking for an attraction on a date is available
    // or not.
    public boolean checkAvail(Date loanDate, String attractionName) {
        ArrayList<Loan> loans = repository.findAllByAttractionName(attractionName);
        Iterator<Loan> iter = loans.iterator();

        while (iter.hasNext()) {
            Loan value = iter.next();
            if (value.equals(loanDate)) {
                return false;

            }
        }
        return true;

    }



    public void changeLoanStatus(String loanId, LOANSTATUS loanStatus) {
        Loan l = repository.findByLoanId(loanId);
        l.setStatus(loanStatus);
    }

    // Method for getting userinfo of a loan
    public String getUserInfo(Date loanDate, String attractionName) {
        ArrayList<Loan> loans = repository.findAllByAttractionName(attractionName);
        String userEmail = u.getEmail();
        String toReturn = "";

        for (Loan loan : loans) {
            Date checkdate = loan.getLoanDate();
            if (checkdate == loanDate) {
                String passNo = p.getPassNo();
                String uEmail = loan.getUserEmail();
                Loan booked_user = repository.findByUserEmail(uEmail);
                String userName = booked_user.getName();
                String contactNo = booked_user.getContactNo();

                toReturn = "The booking for pass " + passNo + " is already loaned by " + userName + " (Contact No: "
                        + contactNo + ")";
            } else {
                toReturn = "Not found!";
            }

        }
        return toReturn;

    }

    // Method for the user to report loss of cards
    public String ReportLoss(String userEmail, Date loanDate) {
        SimpleDateFormat dateFor = new SimpleDateFormat("dd/MM/yyyy");
        String dateL = dateFor.format(loanDate);
        String checkID = userEmail + dateL;
        Loan loan = repository.findByLoanId(checkID);
        loan.setStatus(LOANSTATUS.LOST);

        String passNo = p.getPassNo();
        Date date = loan.getLoanDate();
        cancelAllLoans(passNo, date);
        return "";

    }
    

    // Method to cancel all loans
    public String cancelAllLoans(String passNo, Date date) {
        ArrayList<Loan> loans = repository.findAllByPassNo(passNo);
        if (!loans.isEmpty()) {
            for (Loan loan : loans) {
                Date checkdate = loan.getLoanDate();
                // to check whether the loan is made for later dates
                if (checkdate.after(date)) {
                    loan.setStatus(LOANSTATUS.LOST);
                }
            }
        }
        return "All changes have been made";
    }
} 