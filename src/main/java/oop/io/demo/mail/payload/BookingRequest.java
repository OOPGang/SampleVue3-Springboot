package oop.io.demo.mail.payload;

import javax.validation.constraints.NotBlank;

public class BookingRequest {
    @NotBlank
	private String email;
	@NotBlank
	private String loanId;


	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getLoanId() {
		return loanId;
	}
	public void setLoanId(String loanId) {
		this.loanId = loanId;
	}
	
}

