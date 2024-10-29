package com.agri.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
@Data
public class FarmerRegistrationDto {

    @NotNull(message = "Full name is required.")
    @Size(min = 2, message = "Full name must be at least 2 characters long.")
    private String fullname;

    @Email(message = "Email should be valid.")
    private String email;

    @NotNull(message = "Mobile number is required.")
    @Pattern(regexp = "^\\d{10}$", message = "Mobile number must be 10 digits.")
    private String mobileNumber;

    @NotNull(message = "State ID is required.")
    private Long stateId;

    @NotNull(message = "District ID is required.")
    private Long districtId;

    @NotNull(message = "Sub-District ID is required.")
    private Long subDistrictId;
   
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

	public Long getStateId() {
		return stateId;
	}

	public void setStateId(Long stateId) {
		this.stateId = stateId;
	}

	public Long getDistrictId() {
		return districtId;
	}

	public void setDistrictId(Long districtId) {
		this.districtId = districtId;
	}

	public Long getSubDistrictId() {
		return subDistrictId;
	}

	public void setSubDistrictId(Long subDistrictId) {
		this.subDistrictId = subDistrictId;
	}

    // Getters and Setters
    
}
