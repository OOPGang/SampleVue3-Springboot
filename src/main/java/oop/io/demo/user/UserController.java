package oop.io.demo.user;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mongodb.lang.Nullable;

import io.micrometer.core.ipc.http.HttpSender.Response;
import oop.io.demo.user.UserRepository;
import oop.io.demo.user.User;

@RestController
public class UserController {

    private final UserRepository repository;

    public UserController(UserRepository userRepository) {
        this.repository = userRepository;
    }
    
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllProducts() {
        return ResponseEntity.ok(repository.findAll());
    }

    /*@GetMapping("/users/{email}")
    public ResponseEntity<User> findByEmail(@PathVariable("email") String email) {
        try {
            if(repository.findById(email).isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return ResponseEntity.ok(repository.findById(email));
            }
        }
        catch(Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }*/

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User _user = repository.save(new User(user.getEmail(), user.getFirstName(), user.getLastName()));
        return ResponseEntity.status(200).body(repository.save(_user));
    } 
    /*@PutMapping("/users/{email}")
    public ResponseEntity<User> updateUser(@PathVariable("email") String email, @RequestBody User user) {
        //how do we update?
    } */

    @DeleteMapping("/users/{email}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable("id") String email) {
        try {
            repository.deleteById(email);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch(Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    @DeleteMapping("/users")
    public ResponseEntity<HttpStatus> deleteAllUsers() {
        try {
            repository.deleteAll();
            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch(Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    } 
} 
