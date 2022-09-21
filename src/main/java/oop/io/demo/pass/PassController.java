package oop.io.demo.pass;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import oop.io.demo.login.security.jwt.JwtUtils;
@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api")
public class PassController {


    @Autowired
    PassRepository passRepository;
  
    @GetMapping("/pass")
    public ResponseEntity<List<Pass>> getAllPasses(@RequestParam(required = false) String Passname) {
        try {
            List<Pass> pass = new ArrayList<Pass>();
        
            if (Passname== null)
              passRepository.findAll().forEach(pass::add);
            else
              passRepository.findByPassContaining(Passname).forEach(pass::add);
        
            if (pass.isEmpty()) {
              return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
        
            return new ResponseEntity<>(pass, HttpStatus.OK);
          } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
          }
    }
  
    @GetMapping("/pass/{id}")
    public ResponseEntity<Pass> getPassById(@PathVariable("id") String id) {
        Optional<Pass> passData = passRepository.findById(id);

        if (passData.isPresent()) {
          return new ResponseEntity<>(passData.get(), HttpStatus.OK);
        } else {
          return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
      
    }
  
    @PostMapping("/pass")
    public ResponseEntity<Pass> createPass(@RequestBody Pass pass) {
        try {
            Pass _pass = passRepository.save(new Pass(pass.GetPasses(),pass.GetGuest()));
            return new ResponseEntity<>(_pass, HttpStatus.CREATED);
          } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
          }
      
    }
  
    @PutMapping("/pass/{id}")
    public ResponseEntity<Pass> updatePass(@PathVariable("id") String id, @RequestBody Pass pass) {
        Optional<Pass> passData = passRepository.findById(id);

        if (passData.isPresent()) {
            Pass _pass = passData.get();
            _pass.SetPasses(pass.GetPasses());
            _pass.SetGuest(pass.GetGuest());
        
            return new ResponseEntity<>(passRepository.save(_pass), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
  
    @DeleteMapping("/pass/{id}")
    public ResponseEntity<HttpStatus> deletePass(@PathVariable("id") String id) {
        try {
            passRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
          } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
          }
    }
  
    @DeleteMapping("/pass")
    public ResponseEntity<HttpStatus> deleteAllPass() {
        try {
            passRepository.deleteAll();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
          } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
          }
      
    }
  
 


    
    // @Autowired
    // AuthenticationManager authenticationManager;



    // @Autowired
    // JwtUtils jwtUtils;

    // private final PassRepository passRepository;

    // public PassController(PassRepository passRepository) {
    //     this.passRepository = passRepository;
    // }
    
    // @GetMapping("/pass")
    // public ResponseEntity<List<Pass>> getAllPasses() {
    //     return ResponseEntity.ok(passRepository.findAll());
    // }

    // @GetMapping("/pass/{id}")
    // public ResponseEntity findByPassName(@PathVariable("pass") String pass) {
    //     Optional<Pass> passes= this.passRepository.findByPassName(pass);
    //     if(passes.isPresent()){
    //         return ResponseEntity.ok(passes);
    //     }
    //     else {
    //         return ResponseEntity.ok("The pass name"+ pass +" was not found.");
    //     }
    // }


    // //for creating new user
    // @PostMapping("/pass")
    // public ResponseEntity<Pass> createPasses(@RequestBody PassRequest passRequest) {
    //     Pass pass = new Pass();
    //     pass.SetPasses(passRequest.GetPasses());
    //     // pass.SetAvailability(passRequest.GetAvailability());
    //     return ResponseEntity.ok(passRepository.save(pass));
        
    // }

    // @DeleteMapping("/pass/{id}")
    // public ResponseEntity deletePass(@PathVariable("pass") String passname) {
    //     Optional<Pass> pass = this.passRepository.findById(passname);
    //     //findbyid is mongos way of finding the unique thing, in out case everypass type is unique
    //     if(pass.isPresent()){
    //         this.passRepository.deleteById(passname);
    //         return ResponseEntity.ok("Successfully deleted.");
    //     }
    //     else {
    //         return ResponseEntity.ok("The pass " + passname+ " was not found.");
    //     }
    // }
}
