package oop.io.demo.pass;
import java.util.*;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import oop.io.demo.login.security.jwt.JwtUtils;
@CrossOrigin(maxAge = 3600)
@RestController
// @RequestMapping("/api")
public class PassController {


    // @Autowired
    // PassRepository passRepository;
  
    // @GetMapping("/pass")
    // public ResponseEntity<List<Pass>> getAllPasses(@RequestParam(required = false) String Passname) {
    //     try {
    //         List<Pass> pass = new ArrayList<Pass>();
        
    //         if (Passname== null)
    //           passRepository.findAll().forEach(pass::add);
    //         else
    //           // passRepository.findByPassContaining(Passname).forEach(pass::add);
    //           System.out.println("Test");
        
    //         if (pass.isEmpty()) {
    //           return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    //         }
        
    //         return new ResponseEntity<>(pass, HttpStatus.OK);
    //       } catch (Exception e) {
    //         return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    //       }
    // }
  
    // @GetMapping("/pass/{id}")
    // public ResponseEntity<Pass> getPassById(@PathVariable("id") String id) {
    //     Optional<Pass> passData = passRepository.findById(id);

    //     if (passData.isPresent()) {
    //       return new ResponseEntity<>(passData.get(), HttpStatus.OK);
    //     } else {
    //       return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    //     }
      
    // }
  
    // @PostMapping("/pass")
    // public ResponseEntity<Pass> createPass(@RequestBody Pass pass) {
    //     try {
    //         Pass _pass = passRepository.save(new Pass(pass.GetPasses(),pass.GetGuest()));
    //         return new ResponseEntity<>(_pass, HttpStatus.CREATED);
    //       } catch (Exception e) {
    //         return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    //       }
      
    // }
  
    // @PutMapping("/pass/{id}")
    // public ResponseEntity<Pass> updatePass(@PathVariable("id") String id, @RequestBody Pass pass) {
    //     Optional<Pass> passData = passRepository.findById(id);

    //     if (passData.isPresent()) {
    //         Pass _pass = passData.get();
    //         _pass.SetPasses(pass.GetPasses());
    //         _pass.SetGuest(pass.GetGuest());
        
    //         return new ResponseEntity<>(passRepository.save(_pass), HttpStatus.OK);
    //     } else {
    //         return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    //     }
    // }
  
    // @DeleteMapping("/pass/{id}")
    // public ResponseEntity<HttpStatus> deletePass(@PathVariable("id") String id) {
    //     try {
    //         passRepository.deleteById(id);
    //         return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    //       } catch (Exception e) {
    //         return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    //       }
    // }
  
    // @DeleteMapping("/pass")
    // public ResponseEntity<HttpStatus> deleteAllPass() {
    //     try {
    //         passRepository.deleteAll();
    //         return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    //       } catch (Exception e) {
    //         return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    //       }
      
    // }
  
 


    
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    private final PassRepository repository;

    public PassController(PassRepository passRepository) {
        this.repository = passRepository;
    }
    
    @GetMapping("/pass")
    public ResponseEntity<List<Pass>> getAllPasses() {
        return ResponseEntity.ok(repository.findAll());
    }

    @GetMapping("/pass/{passname}")
    public ResponseEntity findByPass(@PathVariable("name") String name) {
        Optional<Pass> pass= this.repository.findById(name);
        if(pass.isPresent()){
            return ResponseEntity.ok(pass);
        }
        else {
            return ResponseEntity.ok("Pass:"+ name +" was not found.");
        }
    }


    

    
    @PostMapping("/pass")
    public ResponseEntity<Pass> createPass(@RequestBody PassRequest passRequest) {
        Pass pass = new Pass();
        pass.SetPasses(passRequest.GetPasses());
        pass.SetGuest(passRequest.GetGuest());

        //TODO: return message if email already exists
        return ResponseEntity.ok(repository.save(pass));
    }



    @DeleteMapping("/pass/{passname}")
    public ResponseEntity deletePass(@PathVariable("id") String name) {
        Optional<Pass> pass = this.repository.findById(name);
        if(pass.isPresent()){
            this.repository.deleteById(name);
            return ResponseEntity.ok("Successfully deleted.");
        }
        else {
            return ResponseEntity.ok("Pass " + name+ " was not found.");
        }
    }
    @DeleteMapping("/pass")
    public ResponseEntity<HttpStatus> deleteAllPasses() {
        try {
            repository.deleteAll();
            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch(Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    } 
}
