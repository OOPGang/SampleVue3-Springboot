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

    public String GetPasses() {
        return PassName;
    }

    public int GetGuest(){
        return Guest;
    }

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
}
