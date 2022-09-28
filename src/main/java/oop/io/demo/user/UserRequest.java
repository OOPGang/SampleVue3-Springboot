package oop.io.demo.user;

public class UserRequest {
    private String email;
	private String name;
	private USERTYPE userType;
    private String password;
    
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRequest(String email, String name, USERTYPE userType, String password) {
        this.email = email;
        this.name = name;
        this.userType = userType;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public USERTYPE getUserType() {
        return userType;
    }

    public void setUserType(USERTYPE userType) {
        this.userType = userType;
    }

    
}
