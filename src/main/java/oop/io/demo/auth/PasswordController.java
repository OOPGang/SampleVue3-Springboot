package oop.io.demo.auth;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import oop.io.demo.auth.confirmationToken.ConfirmationToken;
import oop.io.demo.auth.confirmationToken.ConfirmationTokenRepository;
import oop.io.demo.auth.confirmationToken.ConfirmationTokenService;
import oop.io.demo.auth.payload.request.PasswordRequest;
import oop.io.demo.auth.security.jwt.JwtUtils;
import oop.io.demo.exception.PasswordsDoNotMatchException;
import oop.io.demo.user.User;
import oop.io.demo.user.UserRepository;

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/password")
public class PasswordController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    private final UserRepository userRepository;

    private final ConfirmationTokenRepository confirmationTokenRepository;

    ConfirmationTokenService confirmationTokenService;
    
    public PasswordController (UserRepository userRepository, ConfirmationTokenRepository confirmationTokenRepository) {
        this.userRepository= userRepository;
        this.confirmationTokenRepository = confirmationTokenRepository;
    }

    @PostMapping("/reset")
    public ResponseEntity resetPasswordEmail(@RequestBody Map<String, String> json){
        String email = json.get("email");
        AuthService authService = new AuthService(userRepository, confirmationTokenRepository);
        if(!userRepository.existsByEmail(email)){
            return ResponseEntity.badRequest().body("User not found");
        }
        try {
            return authService.sendForgotPasswordEmail(email);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/reset")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordRequest passwordRequest) {
        AuthService authService = new AuthService(userRepository, confirmationTokenRepository);
        String token = passwordRequest.getToken();
        if(token==null) return ResponseEntity.badRequest().body("Please provide a token");
        try{
            ConfirmationToken confirmationToken = authService.confirmToken(token);
            confirmationTokenService = new ConfirmationTokenService(confirmationTokenRepository);
            confirmationTokenService.setConfirmedAt(token);
            //change password
            User user = confirmationToken.getUser();
            user.setPassword(encoder.encode(passwordRequest.getPassword()));
            userRepository.save(user);
            return ResponseEntity.ok("Changed password");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/change")
    public ResponseEntity<?> changePassword(@RequestHeader String email, @RequestBody PasswordRequest passwordRequest){
        try {
            User user = userRepository.findByEmail(email).get();
            String oldPassword = passwordRequest.getOldPassword();

            if(!encoder.matches(oldPassword, user.getPassword())) {
                return ResponseEntity.badRequest().body("Old password is incorrect!");
            } else {
            //Password validation to do on frontend
            user.setPassword(encoder.encode(passwordRequest.getPassword()));
            userRepository.save(user);
            return ResponseEntity.ok("Password changed successfully!");
            }
        } catch(Exception e) {
            return ResponseEntity.badRequest().body("User not found");
        }
    }
}