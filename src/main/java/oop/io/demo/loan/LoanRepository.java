package oop.io.demo.loan;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

//by extending MongoRepository, we can use MongoRepository’s methods: save(), findOne(), findById(), findAll(), count(), delete(), deleteById()
public interface LoanRepository extends MongoRepository<Loan, String>{
    //Optional<List<Loan>> findByLoanStatus(LOANSTATUS status);
    //Optional<Loan> findByEmail(String email);
    //Optional<Loan> findByPass(String passNo);
    // Optional <Loan> getByPass(String passNo);
    Loan getByID(String loanID);
    //boolean existsByEmail(String email);
}
