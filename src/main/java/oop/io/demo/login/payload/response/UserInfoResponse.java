package oop.io.demo.login.payload.response;

import org.springframework.security.core.GrantedAuthority;

public class UserInfoResponse {
    private String id;
	private String email;
	private GrantedAuthority usertype;

	public UserInfoResponse(String string,  String email, GrantedAuthority usertype) {
		this.id = string;
		this.email = email;
		this.usertype = usertype;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public GrantedAuthority getUserType() {
		return usertype;
	}
}