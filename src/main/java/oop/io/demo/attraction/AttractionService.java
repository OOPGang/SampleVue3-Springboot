package oop.io.demo.attraction;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import oop.io.demo.fileuploadhelper.FileUploadUtil;

public class AttractionService {

    public ResponseEntity uploadEmailTemplate(String attractionName, MultipartFile emailTemplateFile, String emailTemplateFilename) throws IOException{
        String emailTemplateUploadDir = "src/main/resources/templates/" + attractionName;
        FileUploadUtil.saveFile(emailTemplateUploadDir, emailTemplateFilename, emailTemplateFile);
        return ResponseEntity.ok("Uploaded email template");
    }

    public ResponseEntity uploadImage(String attractionName, MultipartFile imageFile, String imageFilename) throws IOException{
        String imageUploadDir = "src/main/resources/attractionicons/" + attractionName;
        FileUploadUtil.saveFile(imageUploadDir, imageFilename, imageFile);
        return ResponseEntity.ok("Uploaded attraction icon");
    }

    public ResponseEntity uploadAttachmentPDF(String attractionName, MultipartFile attachmentPDFFile, String attachmentPDFFilename) throws IOException{
        String attachmentPDFUploadDir = "src/main/resources/attachments/" + attractionName;
        FileUploadUtil.saveFile(attachmentPDFUploadDir, attachmentPDFFilename, attachmentPDFFile);
        return ResponseEntity.ok("Uploaded attachment PDF");
    }
}
