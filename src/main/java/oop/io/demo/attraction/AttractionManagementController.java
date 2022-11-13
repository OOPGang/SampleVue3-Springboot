package oop.io.demo.attraction;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import oop.io.demo.pass.PASSSTATUS;
import oop.io.demo.pass.Pass;
import oop.io.demo.pass.PassRepository;

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/attractionmanagement")
public class AttractionManagementController {
    private final AttractionRepository repository;

    private final PassRepository passRepository;

    public AttractionManagementController(AttractionRepository attractionRepository, PassRepository passRepository) {
        this.repository= attractionRepository;
        this.passRepository = passRepository;
    }
    
    //Create new place of interest
    @PostMapping("/new")
    public ResponseEntity createAttraction(@RequestBody AttractionRequest attractionRequest) {

        String attractionName = attractionRequest.getAttraction();
        double replacementFee = attractionRequest.getReplacementFee();
        PASSTYPE passtype = PASSTYPE.valueOf(attractionRequest.getPassType().toUpperCase());
        
        if(replacementFee==0.0) {
             Attraction attraction = new Attraction(attractionName, 53.50, passtype);
             return ResponseEntity.ok(repository.save(attraction));
        } else {
            Attraction attraction = new Attraction(attractionName, replacementFee, passtype);
            return ResponseEntity.ok(repository.save(attraction));
        }
    }

    //Upload files
    @PutMapping("/{attraction}/uploadfiles")
    public ResponseEntity createAttraction(@PathVariable("attraction") String attractionName, 
    @RequestParam(name= "emailtemplate") MultipartFile emailTemplateFile, 
    @RequestParam(name="attachment", required = false) MultipartFile attachmentPDFFile){
        AttractionService attractionService = new AttractionService();
        Optional<Attraction> a = repository.findByAttractionName(attractionName);
        //Check: If attraction is not present, return Http bad request
        if(!a.isPresent()) {
            return ResponseEntity.badRequest().body("Attraction not found!");
        }
        //If attraction is present, proceed
        Attraction attraction = a.get();
        //Check: If both templates are not present return Http bad request
        if(attraction.getTemplateFilename()==null && emailTemplateFile==null){
            return ResponseEntity.badRequest().body("Please include an email template");
        }

        try {
            //If user attached an email template file in the request, call the service to upload the email template
            if(emailTemplateFile!=null) {
                String emailTemplateFilename = StringUtils.cleanPath(emailTemplateFile.getOriginalFilename());
                attractionService.uploadEmailTemplate(attractionName, emailTemplateFile, emailTemplateFilename);
                //Set the templateFilename attribute to be the filename of the email template
                attraction.setTemplateFilename(emailTemplateFilename);
            }
            //If user attached an pdf file in the request
            if(attachmentPDFFile!=null) {
                String attachmentPDFFilename = StringUtils.cleanPath(attachmentPDFFile.getOriginalFilename());
                attractionService.uploadAttachmentPDF(attractionName, attachmentPDFFile, attachmentPDFFilename);
                //Set the attatchementpdffilename attribute to be the filename of the pdf
                attraction.setAttachmentPDFFilename(attachmentPDFFilename);
            }
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Failed to upload files");
        }
        //save the attraction to the repository
        return ResponseEntity.ok(repository.save(attraction));
    }

    @PutMapping("/{attraction}/uploadimage")
    public ResponseEntity uploadImage(@PathVariable("attraction") String attractionName, @RequestParam("image") MultipartFile imageFile) {
        AttractionService attractionService = new AttractionService();
        Optional<Attraction> a = repository.findByAttractionName(attractionName);
        //Check: If attraction is not present, return Http bad request
        if(!a.isPresent()) {
            return ResponseEntity.badRequest().body("Attraction not found");
        }
        Attraction attraction = a.get();
        //Check: If image file is not present, return Http bad request
        if(imageFile==null) {
            return ResponseEntity.badRequest().body("Please select an image");
        }
        //
        String imageFilename = StringUtils.cleanPath(imageFile.getOriginalFilename());
        try {
            attractionService.uploadImage(attractionName, imageFile, imageFilename);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        attraction.setImageFilename(imageFilename);
        return ResponseEntity.ok(repository.save(attraction));
    }

    //Edit the passtype, attractionname, and/or replacement fee attributes of the attraction
    @PutMapping("/{attraction}/edit")
    public ResponseEntity editAttraction(@PathVariable("attraction") String attractionName, @RequestBody AttractionRequest attractionRequest) {
        Optional<Attraction> _attraction = repository.findByAttractionName(attractionName);
        if(_attraction.isPresent()){
            Attraction attraction = _attraction.get();
            if(attractionRequest.getPassType()!=null) attraction.setPasstype(PASSTYPE.valueOf(attractionRequest.getPassType().toUpperCase()));
            if(attractionRequest.getAttraction()!=null) attraction.setAttractionName(attractionRequest.getAttraction());
            if(attractionRequest.getReplacementFee()!=0.0) attraction.setReplacementFee(attractionRequest.getReplacementFee());
            return ResponseEntity.ok(repository.save(attraction));
        } else {
            return ResponseEntity.badRequest().body("Attraction not found.");
        }
    }

    //deactivate an attraction by updating the 'active' attribute of attraction to false
    @PutMapping("/{attraction}/deactivate")
    public ResponseEntity deactivateAttraction(@PathVariable("attraction") String attractionName) {
        Optional<Attraction> _attraction = repository.findByAttractionName(attractionName);
        if(_attraction.isPresent()){
            Attraction attraction = _attraction.get();
            attraction.setActive(false);
            repository.save(attraction);
            List<Pass> passes = passRepository.findByAttractionName(attractionName).get();
            if(passes.isEmpty()) return ResponseEntity.ok("Deactivated Attraction. No passes to deactivate.");
            for(Pass p: passes){
                p.setPassStatus(PASSSTATUS.DEACTIVATED);
            }
            passRepository.saveAll(passes);
            return ResponseEntity.ok("Deactivated "+ attractionName + " attraction and all passes under "+ attractionName+".");
        } else {
            return ResponseEntity.badRequest().body("Attraction not found.");
        }
    }

    //reactivate an attraction by setting the 'active' attribute of attraction to true
    @PutMapping("/{attraction}/reactivate")
    public ResponseEntity reactivateAttraction(@PathVariable("attraction") String attractionName) {
        Optional<Attraction> _attraction = repository.findByAttractionName(attractionName);
        if(_attraction.isPresent()){
            Attraction attraction = _attraction.get();
            attraction.setActive(true);
            repository.save(attraction);
            List<Pass> passes = passRepository.findByAttractionName(attractionName).get();
            if(passes.isEmpty()) return ResponseEntity.ok("Reactivated Attraction. No passes to reactivate.");
            for(Pass p: passes){
                p.setPassStatus(PASSSTATUS.INOFFICE);
            }
            passRepository.saveAll(passes);
            return ResponseEntity.ok("Reactivated "+ attractionName + " attraction and all passes under "+ attractionName+".");
        } else {
            return ResponseEntity.badRequest().body("Attraction not found.");
        }
    }

}