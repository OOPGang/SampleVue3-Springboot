package oop.io.demo.user;

public class EditProfileRequest {

    private String email;
    private String contactNo;
	private String name;


    public String getContactNo() {
        return contactNo;
    }
    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return email;
    }
    

}
