package oop.io.demo.pass;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import oop.io.demo.login.security.jwt.JwtUtils;

@RestController
@RequestMapping("/api/Pass")
public class PassController {

    
    @Autowired
    AuthenticationManager authenticationManager;



    @Autowired
    JwtUtils jwtUtils;

    private final PassRepository passRepository;

    public PassController(PassRepository passRepository) {
        this.passRepository = passRepository;
    }
    
    @GetMapping("/pass")
    public ResponseEntity<List<Pass>> getAllPasses() {
        return ResponseEntity.ok(passRepository.findAll());
    }

    @GetMapping("/pass/{passname}")
    public ResponseEntity findByPassName(@PathVariable("pass") String pass) {
        Optional<Pass> passes= this.passRepository.findByPassName(pass);
        if(passes.isPresent()){
            return ResponseEntity.ok(passes);
        }
        else {
            return ResponseEntity.ok("The pass name"+ pass +" was not found.");
        }
    }


    //for creating new user
    @PostMapping("/pass")
    public ResponseEntity<Pass> createPasses(@RequestBody PassRequest passRequest) {
        Pass pass = new Pass();
        pass.SetPasses(passRequest.GetPasses());
        pass.SetAvailability(passRequest.GetAvailability());
        return ResponseEntity.ok(passRepository.save(pass));
        

    }



    @DeleteMapping("/pass/{passname}")
    public ResponseEntity deletePass(@PathVariable("pass") String passname) {
        Optional<Pass> pass = this.passRepository.findById(passname);
        //findbyid is mongos way of finding the unique thing, in out case everypass type is unique
        if(pass.isPresent()){
            this.passRepository.deleteById(passname);
            return ResponseEntity.ok("Successfully deleted.");
        }
        else {
            return ResponseEntity.ok("The pass " + passname+ " was not found.");
        }
    }



    
}
