package oop.io.demo.pass;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import oop.io.demo.attraction.AttractionRepository;

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/passstatusupdate")
public class PassGOController {
    
    private final PassRepository repository;

    private final AttractionRepository attractionRepository;

    public PassGOController(PassRepository passRepository, AttractionRepository attractionRepository) {
        this.repository = passRepository;
        this.attractionRepository = attractionRepository; 
    }

    @PutMapping("/passreturn")
    public ResponseEntity setPassStatusToInOffice(@RequestBody Map<String, String> map) {
        String passId = map.get("passId");
        ResponseEntity responseEntity = new PassService(repository, attractionRepository).changePassStatus(passId, PASSSTATUS.INOFFICE);
        return responseEntity;
    }

    @PutMapping("/passloan")
    public ResponseEntity setPassStatusToOnLoan(@RequestBody Map<String, String> map) {
        String passId = map.get("passId");
        ResponseEntity responseEntity = new PassService(repository, attractionRepository).changePassStatus(passId, PASSSTATUS.ONLOAN);
        return responseEntity;
    }
    
}
