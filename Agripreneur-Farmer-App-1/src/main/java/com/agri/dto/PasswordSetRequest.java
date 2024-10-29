package com.agri.dto;

public class PasswordSetRequest {
    private String email;
    private String password;

    
    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public PasswordSetRequest(String email, String password) {
		super();
		this.email = email;
		this.password = password;
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
}
