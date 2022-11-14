package oop.io.demo.loan;
import java.util.*;



import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;



@Repository
//by extending MongoRepository, we can use MongoRepository’s methods: save(), findOne(), findById(), findAll(), count(), delete(), deleteById()
public interface LoanRepository extends MongoRepository<Loan, String>{

//Attribute first letter has to be in caps e.g UserEmail    
   
    ArrayList<Loan> findAllByUserEmail(String userEmail);
    ArrayList<Loan> findAllByAttractionName(String attractionName);
    ArrayList<Loan> findAllByStatus(String status);
    Loan findByLoanId(String loanId);
    Optional<Loan> findByUserEmail(String userEmail);
    ArrayList<Loan> findAllByPassNo(String passNo);
   
   
 
    

}