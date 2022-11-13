package oop.io.demo.pass;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.http.ResponseEntity;

import oop.io.demo.attraction.AttractionRepository;


public class PassService {

    private final PassRepository repository;

    private AttractionRepository attractionRepository;
    
    public PassService(PassRepository passRepository, AttractionRepository attractionRepository){
        this.repository = passRepository;
        this.attractionRepository = attractionRepository;
    }

    public ResponseEntity createPass(PassRequest createPassRequest){
        String attractionName = createPassRequest.getAttractionName();
        String passNo = createPassRequest.getPassNo();
        String passId = attractionName + passNo;
        if (attractionName==null || passNo ==null) {
            //can throw exception due to null value of field
            return ResponseEntity.badRequest().body("Place of Interest Name and Pass Number can't be empty");
        } else if (!attractionRepository.findByAttractionName(attractionName).isPresent()) {
            //can throw exception due to nonexistence of entity
            return ResponseEntity.badRequest().body("Place of interest " + attractionName + " does not exist.");
        } else if (repository.findById(passId).isPresent()) {
            return ResponseEntity.badRequest().body("Pass with pass number: '" + passNo + "' for place of interest: '" + attractionName + "' already exists.");
        }
        Pass pass = new Pass(passNo, attractionName);
        return ResponseEntity.ok(repository.save(pass));
    }

    public ResponseEntity changePassStatus(String passId, PASSSTATUS passStatus){
        Optional<Pass> p = repository.findById(passId);
        if(!p.isPresent()) {
            return ResponseEntity.badRequest().body("Pass does not exist");
        }
        Pass pass = p.get();
        pass.setPassStatus(passStatus);
        repository.save(pass);
        return ResponseEntity.ok("Changed status of pass successfully to: " + passStatus.toString());
    }

    public List<Pass> getPassesByAttraction(String attraction) {
        return repository.findByAttractionName(attraction).get();
    }

    public List<Pass> getAvailablePassesByAttraction(String attraction) {
        List<Pass> passes = repository.findByAttractionName(attraction).get();
        if(passes==null) return null;
        List<Pass> passesByStatus = new ArrayList<>();
        for(Pass p : passes){
            if(p.getPassStatus()==PASSSTATUS.INOFFICE) passesByStatus.add(p);
        }
        return passesByStatus;
    }

}
