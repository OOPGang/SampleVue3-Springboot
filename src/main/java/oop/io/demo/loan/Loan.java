package oop.io.demo.loan;

import java.util.Date;

import org.springframework.data.annotation.Id;

public class Loan {


    public Loan(){

    }

    
    @Id
    private String loanID;
    private Date loanDate;
    private Date bookingMadeDate;
    private String attractionName;
    private String userEmail;
    //what type should passNo be?
    private int passNo;
    private LOANSTATUS status;

    //constructor with attributes required to create a new loan
    public Loan(Date loanDate, Date bookingMadeDate, String attractionName, String userEmail) {
        this.loanDate = loanDate;
        this.bookingMadeDate = bookingMadeDate;
        this.attractionName = attractionName;
        this.userEmail = userEmail;
    }

    public Date getLoanDate() {
        return loanDate;
    }

    public Date getBookingMadeDate() {
        return bookingMadeDate;
    }

    public String getAttractionName() {
        return attractionName;
    }

    public int getPassNo() {
        return passNo;
    }
    public void setPassNo(int passNo) {
        this.passNo = passNo;
    }
    public String getUserEmail() {
        return userEmail;
    }

    public LOANSTATUS getStatus() {
        return status;
    }
    public void setStatus(LOANSTATUS status) {
        this.status = status;
    }
}
