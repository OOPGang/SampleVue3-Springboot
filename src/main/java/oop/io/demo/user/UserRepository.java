package oop.io.demo.user;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

//by extending MongoRepository, we can use MongoRepository’s methods: save(), findOne(), findById(), findAll(), count(), delete(), deleteById()
public interface UserRepository extends MongoRepository<User, String>{
    List<User> findByStaffType(String staffType);
}
