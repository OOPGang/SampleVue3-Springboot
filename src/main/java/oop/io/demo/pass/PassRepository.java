package oop.io.demo.pass;
import java.util.*;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PassRepository extends MongoRepository<Pass, String> {
    // List<Pass> GetPasses(String PassName);
    // Optional<Pass> findByPass(String name);
    // Pass findByPassName(String PassName);
}

