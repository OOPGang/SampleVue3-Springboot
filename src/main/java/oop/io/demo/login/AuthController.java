package oop.io.demo.login;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthenticatedReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import oop.io.demo.user.USERTYPE;
import oop.io.demo.user.User;
import oop.io.demo.login.payload.request.LoginRequest;
import oop.io.demo.login.payload.request.SignupRequest;
import oop.io.demo.login.payload.response.UserInfoResponse;
import oop.io.demo.login.payload.response.MessageResponse;
import oop.io.demo.user.UserRepository;
import oop.io.demo.login.security.jwt.JwtUtils;
import oop.io.demo.login.security.services.UserDetailImplementation;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;


    private final UserRepository repository;

    public AuthController(UserRepository userRepository) {
        this.repository = userRepository;
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager
        .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailImplementation userDetails = (UserDetailImplementation) authentication.getPrincipal();

        return ResponseEntity.ok()
            .body(new UserInfoResponse(userDetails.getId(),
                                    userDetails.getEmail(),
                                    userDetails.getAuthority()));

        }

        @PostMapping("/signup")
        public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {


        if (repository.existsByEmail(signUpRequest.getEmail())) {
        return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        User user = new User(
                            signUpRequest.getEmail(),
                            encoder.encode(signUpRequest.getPassword()));
        
        user.setUserType(USERTYPE.STAFF);
        repository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));

        }

        @PostMapping("/signout")
        public ResponseEntity<?> logoutUser() {
        return ResponseEntity.ok()
            .body(new MessageResponse("You've been signed out!"));

        }

}
