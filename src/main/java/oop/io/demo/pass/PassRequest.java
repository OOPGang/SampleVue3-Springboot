package oop.io.demo.pass;

public class PassRequest {
    // private String PassID;
    private String PassName;
    private int Guest;

    public PassRequest( String PassName, int Guest){
        // this.PassID = PassID;
        this.PassName = PassName;
        this.Guest=Guest;
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

    // public String GetId() {
    //     return PassID;
    // }


    public void SetPasses(String PassName) {
        this.PassName = PassName;
    }

    public void setGuest(int Guest){
        this.Guest=Guest;
    }

    // public void SetID(String PassID) {
    //     this.PassID=PassID;
    // }
    // public void SetAvailability(int PassAvailability) {
    //     this.PassAvailability=PassAvailability;
    // }



    
}
