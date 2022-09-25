package oop.io.demo.pass;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;


@Document("Pass")
public class Pass {

    @Id
    private String PassID;
    @Indexed(unique=true)
    private String PassName;
    private int Guest;


    public Pass(){
        
    }


    public Pass(String PassName, int Guest) {
        // this.PassID = PassID;
        this.PassName = PassName;
        this.Guest=3;
        // this.PassAvailability = PassAvailability;
    }
         /**
     * Gets the List of passes 
     * @return the Pass name
     */
    public String GetPasses() {
        return PassName;
    }

    public int GetGuest(){
        return Guest;
    }

     /**
     * Gets the number of passes left
     * @return the attraction availability
     */
    // public int GetAvailability() {
    //     return PassAvailability;
    // }

    public String GetPassId() {
        return PassID;
    }


    public void SetPasses(String PassName) {
        this.PassName = PassName;
    }

    public void SetGuest(int Guest){
        this.Guest=Guest;
    }


    public boolean isPresent() {
        return false;
    }
    

    // public void SetID(String PassID) {
    //     this.PassID=PassID;
    // }
    // public void SetAvailability(int PassAvailability) {
    //     this.PassAvailability=PassAvailability;
    // }


   







    
}
