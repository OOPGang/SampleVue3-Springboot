package oop.io.demo.pass;
import java.util.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import oop.io.demo.attraction.AttractionRepository;
@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/pass")
public class PassController {

    private final PassRepository repository;

    private final AttractionRepository attractionRepository;

    public PassController(PassRepository passRepository, AttractionRepository attractionRepository) {
        this.repository = passRepository;
        this.attractionRepository = attractionRepository; 
    }

    //GET pass by id
    @GetMapping("/{passid}")
    public ResponseEntity<Optional<Pass>> getPassDetails(@PathVariable("passid") String passId) {
        return ResponseEntity.ok(repository.findById(passId));
    }
  
    //Get passes by attraction
    @GetMapping("/passes/{attraction}")
    public ResponseEntity getAvailablePassesByAttraction(@PathVariable("attraction") String attraction) {
        List<Pass> passes = new PassService(repository, attractionRepository).getAvailablePassesByAttraction(attraction);
        return ResponseEntity.ok(passes);
    }
    
    //Get all passes
    @GetMapping("/passes")
    public ResponseEntity<List<Pass>> getAllPasses() {
        return ResponseEntity.ok(repository.findAll());
    }

    //Get passes by pass status
    @GetMapping("/passes/status")
    public ResponseEntity<List<Pass>> getPassesByPassStatus(@RequestParam(value="status") String passStatus) {
        PASSSTATUS status = PASSSTATUS.valueOf(passStatus.toUpperCase());
        return ResponseEntity.ok(repository.findByPassStatus(status).get());
    }
    
}
