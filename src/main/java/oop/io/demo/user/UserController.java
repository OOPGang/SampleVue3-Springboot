package oop.io.demo.user;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import oop.io.demo.login.security.jwt.JwtUtils;

@CrossOrigin(maxAge = 3600)
@RestController
public class UserController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    private final UserRepository repository;

    public UserController(UserRepository userRepository) {
        this.repository = userRepository;
    }
    
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUser() {
        return ResponseEntity.ok(repository.findAll());
    }

    @GetMapping("/users/{email}")
    public ResponseEntity findByEmail(@PathVariable("email") String email) {
        Optional<User> user= this.repository.findByEmail(email);
        if(user.isPresent()){
            return ResponseEntity.ok(user);
        }
        else {
            return ResponseEntity.ok("A user with email:"+ email +" was not found.");
        }
    }

    @GetMapping("/users/{userType}")
    public ResponseEntity findByUserType(@PathVariable("userType") USERTYPE userType) {
        Optional<List<User>> users= this.repository.findByUserType(userType);
        if(users.isPresent()){
            return ResponseEntity.ok(users);
        }
        else{
            return ResponseEntity.ok("No users with staff type: "+ userType +" wwere not found.");
        }
    }

    //for creating new user
    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody UserRequest userRequest) {
        User user = new User();
        user.setEmail(userRequest.getEmail());
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        String uType = userRequest.getUserType().toString().toUpperCase();
        user.setUserType(USERTYPE.valueOf(uType));
        //TODO: return message if email already exists
        return ResponseEntity.ok(repository.save(user));
    }

    /*@PutMapping("/users/{email}")
    public ResponseEntity updateUser(@PathVariable("email") String email, @RequestBody User user) {
        Optional<User> _user = this.repository.findById(email);
        if (_user.isPresent()) {
            _user.setEmail(user.getEmail());
            _user.setFirstName(user.getFirstName());
            _user.setLastName(user.getLastName());
            _user.setUserType(user.getUserType());
        }
        //TODO: return message if email already exists
        return _user;
    }*/

    @DeleteMapping("/users/{email}")
    public ResponseEntity deleteUser(@PathVariable("id") String email) {
        Optional<User> user = this.repository.findById(email);
        if(user.isPresent()){
            this.repository.deleteById(email);
            return ResponseEntity.ok("Successfully deleted.");
        }
        else {
            return ResponseEntity.ok("User with email: " + email+ " was not found.");
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
