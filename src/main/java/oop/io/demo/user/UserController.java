package oop.io.demo.user;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import oop.io.demo.auth.security.jwt.JwtUtils;

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/user")
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
    
    ////ALL ACCESS
    //Get public user details by username
    @GetMapping("/userdetails")
    public ResponseEntity getPublicDetailsByEmail(@RequestBody Map<String, String> map) {
        try {
            String email = (String) map.get("email");
            User user= this.repository.findByEmail(email).get();
            UserPublicDetails publicUser = new UserPublicDetails(user.getEmail(), user.getContactNo(), user.getName());
            return ResponseEntity.ok(publicUser);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching user details. Please check that username is entered correctly.");
        }
    }

    ////USER AND ADMIN ACCESS
    //Get user by username
    //Access: only admin and user with {username} can access
    @GetMapping("/mydetails")
    public ResponseEntity getMyDetails(@RequestBody Map<String, String> map) {
        try {
            String email = (String) map.get("email");
            User user= this.repository.findByEmail(email).get();
            return ResponseEntity.ok(user);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching user details. Please check that email is entered correctly.");
        }
    }

    ////USER ONLY ACCESS
    //Edit user profile details
    //Access: user
    @PutMapping("/editprofile")
    public ResponseEntity editProfile(@RequestBody EditProfileRequest editProfileRequest) {
        try {
            User user = repository.findByEmail(editProfileRequest.getEmail()).get();
            if(editProfileRequest.getContactNo()!=null) user.setContactNo(editProfileRequest.getContactNo());
            if(editProfileRequest.getName()!=null) user.setName(editProfileRequest.getName());
            return ResponseEntity.ok(repository.save(user));
        } catch(Exception e) {
            return ResponseEntity.badRequest().body("User not found");
        }
    }

} 
