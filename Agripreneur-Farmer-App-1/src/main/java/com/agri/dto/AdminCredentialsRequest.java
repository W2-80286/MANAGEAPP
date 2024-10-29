package com.agri.dto;

import jakarta.validation.constraints.NotBlank;

public class AdminCredentialsRequest {

    @NotBlank
    private String fullName;

    @NotBlank
    private String email;

    @NotBlank
    private String mobileNumber;

    @NotBlank
    private String password;

    @NotBlank
    private String confirmPassword; //
    
    
    public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	// Getters and Setters
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
