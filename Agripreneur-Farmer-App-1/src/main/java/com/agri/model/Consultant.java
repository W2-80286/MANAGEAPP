package com.agri.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Consultant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "consultant_id")
    private Long consultantId;

    @NotBlank // Ensure fullname is not null or empty
    @Column(nullable = false)
    private String fullname;

    @Email // Ensure the email follows email format
    @NotBlank // Ensure email is not null or empty
    @Column(nullable = false, unique = true) // Ensure email is unique
    private String email;

    @NotBlank // Mobile number should not be null or empty
    @Column(nullable = false, unique = true) // Ensure mobile number is unique
    private String mobileNumber; // Changed to String to avoid leading zero issues

    @ManyToOne
    @JoinColumn(name = "state_id", nullable = false)
    @NotNull // Ensure the state is not null
    private State state;

    @JsonIgnore // To hide the password field in the JSON response
    @NotBlank // Password should not be null or empty
   // private String password;

    // Getters and Setters

    public Long getConsultantId() {
        return consultantId;
    }

    public void setConsultantId(Long consultantId) {
        this.consultantId = consultantId;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
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

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

  
}
