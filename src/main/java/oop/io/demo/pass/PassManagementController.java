package oop.io.demo.pass;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import oop.io.demo.attraction.AttractionRepository;
@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/passmanagement")
public class PassManagementController {

    private final PassRepository repository;

    private final AttractionRepository attractionRepository;

    public PassManagementController(PassRepository passRepository, AttractionRepository attractionRepository) {
        this.repository = passRepository;
        this.attractionRepository = attractionRepository; 
    }
    
    //for creating new passes for an existing attraction
    @PostMapping("/new")
    public ResponseEntity createPasses(@RequestBody PassRequest passRequest) {
        PassService passService = new PassService(repository, attractionRepository);
        ResponseEntity responseEntity = passService.createPass(passRequest);
        return responseEntity;
    }
    
    //to deactivate a pass by id
    @GetMapping("/{passid}/deactivate")
    public ResponseEntity deactivatePass(@PathVariable("passid") String passId) {
        ResponseEntity responseEntity = new PassService(repository, attractionRepository).changePassStatus(passId, PASSSTATUS.DEACTIVATED);
        return responseEntity;
    }

    //to activate a pass by id
    @GetMapping("/{passid}/activate")
    public ResponseEntity activatePass(@PathVariable("passid") String passId) {
        ResponseEntity responseEntity = new PassService(repository, attractionRepository).changePassStatus(passId, PASSSTATUS.INOFFICE);
        return responseEntity;
    }

}
