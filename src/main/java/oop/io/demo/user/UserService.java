package oop.io.demo.user;

import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UserService {

    @Autowired
    PasswordEncoder encoder;
    
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //Enable user or disable user and save user to repository
    public ResponseEntity enableDisableUser(String email, String enableOrDisable){
        Optional<User> _user = userRepository.findByEmail(email);
        if(_user.isPresent()) {
            User user= _user.get();
            boolean verified = enableOrDisable== "Enable"? true: false;
            user.setVerified(verified);
            userRepository.save(user);
            return ResponseEntity.ok(user);
            //return ResponseEntity.ok(enableOrDisable+ "d user");
        } else {
            return ResponseEntity.badRequest().body("User not found!");
        }
    }

    public ResponseEntity changeUserType(Map<String, String> map, USERTYPE userType) {
        String email = (String) map.get("email");
        if(email==null) return ResponseEntity.badRequest().body("No email entered.");
        try {
            User user = this.userRepository.findByEmail(email).get();
            user.setUserType(userType);
            return ResponseEntity.ok(userRepository.save(user));
        } catch(Exception e) {
            return ResponseEntity.badRequest().body("User not found!");
        }
    }
}
