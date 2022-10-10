package oop.io.demo.loan;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

//by extending MongoRepository, we can use MongoRepository’s methods+nameof id in the Loan.java class : save(), findOne(), findById(), findAll(), count(), delete(), deleteById()
public interface LoanRepository extends MongoRepository<Loan, String>{
     
    //Optional<Loan> findByEmail(String email);
    //Optional<Loan> findByPass(String passNo);
    // Optional <Loan> getByPass(String passNo);
    Loan findByLoanID(String loanID);
    //boolean existsByEmail(String email);


    //Autowired dependency injec
    
}
