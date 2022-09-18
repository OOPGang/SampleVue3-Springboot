package oop.io.demo.user;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;


@Document("User")
public class User {

    @Id
    private String userId;
    @Indexed(unique=true)
	private String email;
	private String firstName;
	private String lastName;
	private USERTYPE userType;
    private String password;

    public User() {}
    public User(String email, String password) {
        this.email= email;
        this.password= password;
    }
	public User(String email, String firstName, String lastName){
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
		this.userType = USERTYPE.STAFF;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
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

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setUserType(USERTYPE userType) {
        this.userType = userType;
    }
    public String getUserId() {
        return userId;
    }


}