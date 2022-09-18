package oop.io.demo.user;

public class UserRequest {
    private String email;
	private String firstName;
	private String lastName;
	private USERTYPE userType;
    
    public UserRequest(String email, String firstName, String lastName, USERTYPE userType) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userType = userType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public USERTYPE getUserType() {
        return userType;
    }

    public void setUserType(USERTYPE userType) {
        this.userType = userType;
    }

    
}
