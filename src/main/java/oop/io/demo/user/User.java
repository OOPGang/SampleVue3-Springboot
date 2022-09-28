package oop.io.demo.user;

import java.util.Set;

import javax.persistence.Column;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

//import oop.io.demo.auth.registrationtoken.RegistrationToken;


@Document("User")
public class User {

    @Id
    private String userId;

    @Column(unique=true)
	private String email;

    @Column(unique =true)
    private String contactNo;
	private String name;
	private USERTYPE userType;
    private String password;

    private boolean isVerified;
    //private Set<RegistrationToken> tokens;

    public String getContactNo() {
        return contactNo;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public User() {}

    public User(String email, String password) {
        this.email= email;
        this.password= password;
    }

	public User(String email, String name, String password){
        this.email = email;
        this.name = name;
        this.password = password;
		this.userType = USERTYPE.STAFF;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public USERTYPE getUserType() {
        return userType;
    }


    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUserType(USERTYPE userType) {
        this.userType = userType;
    }
    public String getUserId() {
        return userId;
    }

    public void setVerified(boolean isVerified) {
        this.isVerified = isVerified;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }


}