package oop.io.demo.auth;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import javax.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import oop.io.demo.auth.confirmationToken.ConfirmationToken;
import oop.io.demo.auth.confirmationToken.ConfirmationTokenRepository;
import oop.io.demo.auth.confirmationToken.ConfirmationTokenService;
import oop.io.demo.auth.payload.request.PasswordRequest;
import oop.io.demo.auth.payload.request.SignupRequest;
import oop.io.demo.auth.payload.request.VerificationRequest;
import oop.io.demo.auth.payload.response.MessageResponse;
import oop.io.demo.exception.EmailFailToSendException;
import oop.io.demo.exception.PasswordsDoNotMatchException;
import oop.io.demo.mail.Email;
import oop.io.demo.mail.EmailSender;
import oop.io.demo.mail.EmailService;
import oop.io.demo.user.USERTYPE;
import oop.io.demo.user.User;
import oop.io.demo.user.UserRepository;
import oop.io.demo.user.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;

public class AuthService {
    
    
    private final UserRepository userRepository;

    private final ConfirmationTokenRepository confirmationTokenRepository;

    private ConfirmationTokenService confirmationTokenService;

    private EmailService emailService;

    public AuthService(UserRepository userRepository, ConfirmationTokenRepository confirmationTokenRepository) {
        this.userRepository = userRepository;
        this.confirmationTokenRepository = confirmationTokenRepository;
    }

    public ResponseEntity<?> signUpOneUser(SignupRequest signUpRequest) throws EmailFailToSendException {
        // Create new user's account
        User user = new User(
                            signUpRequest.getName(),
                            signUpRequest.getEmail());

        user.setUserType(USERTYPE.STAFF);
        user.setVerified(false);
        userRepository.save(user);
        return sendConfirmationTokenEmail(user);
    }

    public String generateConfirmationToken(String email) {
        String token = UUID.randomUUID().toString();
        Optional<User> u = userRepository.findByEmail(email);
        if(!u.isPresent()){
            return "User with this email doesn't exist!";
        }
        User user = u.get();
        ConfirmationToken confirmationToken = new ConfirmationToken(
            token,
            LocalDateTime.now(),
            LocalDateTime.now().plusMinutes(15),
            user
            );
        confirmationTokenService = new ConfirmationTokenService(confirmationTokenRepository);
        confirmationTokenService.saveConfirmationToken(confirmationToken);
        return token;
    }

    public ResponseEntity<?> sendConfirmationTokenEmail(User user) throws EmailFailToSendException {
        String token = generateConfirmationToken(user.getEmail());
        //send email
        Email mail = new Email();
        mail.setTo(user.getEmail());
        mail.setSubject("Complete your registration for the Singapore Sports School Employee Pass Booking website");
        //TODO: replace with email template; could create email builder service to integrate link and email
        mail.setContent("Please use this token to complete registration process: " + token);
        emailService= new EmailService();
        try {
            emailService.sendEmail(mail);
            return ResponseEntity.ok(new MessageResponse("Please check email to complete registration"));
        } catch (Exception e) {
            throw new EmailFailToSendException("Failed to send confirmation token email");
        }
    }

    public ResponseEntity<?> sendForgotPasswordEmail(String email) throws PasswordsDoNotMatchException{
        String token = generateConfirmationToken(email);
        //send email
        Email mail = new Email();
        mail.setTo(email);
        mail.setSubject("Reset password for Singapore Sports School Employee Pass Booking website");
        //TODO: replace with email template; could create email builder service to integrate link and email
        mail.setContent("Please use this token to reset password: " + token);
        emailService= new EmailService();
        emailService.sendEmail(mail);
        return ResponseEntity.ok(new MessageResponse("Please check email to reset password"));
    }

    /* to check if passwords are equal if not done on frontend
    public ResponseEntity<?> setPassword(User user, VerificationRequest verificationRequest) throws PasswordsDoNotMatchException{
        if(verificationRequest.getPassword()!=verificationRequest.getRetypePassword()) {
            throw new PasswordsDoNotMatchException("Passwords do not match");
        }
        user.setPassword(encoder.encode(verificationRequest.getPassword()));
        userRepository.save(user);
        return ResponseEntity.ok("Password set succesfully!");
    }*/

    public void changePassword(String password, String token) {
        ConfirmationToken confirmationToken = confirmToken(token);
        User user = confirmationToken.getUser();
        user.setPassword(password);
        userRepository.save(user);
    }

    @Transactional
    public ConfirmationToken confirmToken(String token){
        confirmationTokenService = new ConfirmationTokenService(confirmationTokenRepository);
        ConfirmationToken confirmationToken = confirmationTokenService.getToken(token);
        if(confirmationToken==null) {
            System.out.println("Token not found!");
            throw new IllegalStateException("Token not found!");
        }
        if(confirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException("Email already exists!");
        }
        if(LocalDateTime.now().isAfter(confirmationToken.getExpiresAt())) {
            throw new IllegalStateException("Token has expired");
        }
        return confirmationToken;
    }
}
