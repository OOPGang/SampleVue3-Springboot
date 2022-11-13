package oop.io.demo.loan;
import java.util.*;

public class LoanRequest {
    private String userEmail;
    private String attractionName;
    private Date loanDate;

    

    public LoanRequest(String userEmail, String attractionName, Date loanDate){
        this.userEmail = userEmail;
        this.attractionName = attractionName;
        this.loanDate = loanDate;
     
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




    
}