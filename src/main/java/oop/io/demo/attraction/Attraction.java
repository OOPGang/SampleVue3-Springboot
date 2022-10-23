package oop.io.demo.attraction;

import javax.persistence.Column;
import javax.persistence.Id;

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
}