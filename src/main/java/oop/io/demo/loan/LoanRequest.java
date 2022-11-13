package oop.io.demo.loan;
import java.util.*;

public class LoanRequest {
    private String userEmail;
    private String attractionName;
    private Date loanDate;
    private int noOfPass;

    public LoanRequest(String userEmail, String attractionName, Date loanDate, int noOfPass){
        this.userEmail = userEmail;
        this.attractionName = attractionName;
        this.loanDate = loanDate;
        this.noOfPass = noOfPass;
    }

    public String getUserEmail(){
        return this.userEmail;

    }

    public String getAttractionName(){
        return this.attractionName;
        
    }

    public Date getLoanDate(){
        return this.loanDate;
    }

    public int getNoOfPass(){
        return this.noOfPass;
    }
    
}