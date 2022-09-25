package oop.io.demo.pass;

public class PassRequest {

    private String PassName;
    private int Guest;

    public PassRequest( String PassName, int Guest){
        this.PassName = PassName;
        this.Guest=Guest;
 

    }

    public String GetPasses() {
        return PassName;
    }

    public int GetGuest(){
        return Guest;
    }




    public void SetPasses(String PassName) {
        this.PassName = PassName;
    }

    public void setGuest(int Guest){
        this.Guest=Guest;
    }





    
}
