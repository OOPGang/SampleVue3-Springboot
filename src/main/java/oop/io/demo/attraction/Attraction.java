package oop.io.demo.attraction;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.bson.types.Binary;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("Attraction")
public class Attraction {
    @Id
    private String id;

    @Column(unique=true)
    private String attractionName;

    private double replacementFee;

    private PASSTYPE passType;

    private boolean active;

    private String templateFilename;

    private String imageFilename;

    private String attachmentPDFFilename;

    public Attraction(String attractionName, double replacementFee, PASSTYPE passType) {
        this.attractionName=attractionName;
        this.replacementFee=replacementFee;
        this.passType= passType;
        this.active = true;
    }


    public String getAttractionName() {
        return attractionName;
    }

    public void setAttractionName(String attractionName) {
        this.attractionName = attractionName;
    }

    public double getReplacementFee() {
        return replacementFee;
    }

    public void setReplacementFee(double replacementFee) {
        this.replacementFee = replacementFee;
    }

    public PASSTYPE getPassType() {
        return passType;
    }

    public void setPasstype(PASSTYPE passType) {
        this.passType = passType;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getTemplateFilename() {
        return templateFilename;
    }

    public void setTemplateFilename(String templateFilename) {
        this.templateFilename = templateFilename;
    }

    public String getImageFilename() {
        return imageFilename;
    }

    public void setImageFilename(String imageFilename) {
        this.imageFilename = imageFilename;
    }

    public String getAttachmentPDFFilename() {
        return attachmentPDFFilename;
    }

    public void setAttachmentPDFFilename(String attachmentPDFFilename) {
        this.attachmentPDFFilename = attachmentPDFFilename;
    }

    //@Transient
    public String getTemplateFilePath() {
        return "/templates/" + attractionName;
    }

    @Transient
    public String getImageFilePath() {
        return "/src/main/resources/attractionicons/" + attractionName + "/" + imageFilename;
    }
    
    //@Transient
    public String getAttachmentPDFFilePath() {
        return "/attachments/" + attractionName;
    }
}