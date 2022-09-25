package oop.io.demo.pass;
import java.util.*;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PassRepository extends MongoRepository<Pass, String> {
    // List<Pass> GetPasses(String PassName);
    // List<Pass> findByPassContaining(String Passname);
    // Pass findByPassName(String PassName);
}

