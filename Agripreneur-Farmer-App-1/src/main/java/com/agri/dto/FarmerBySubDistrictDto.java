package com.agri.dto;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class FarmerBySubDistrictDto {
	@NotNull(message = "Farmer ID is mandatory")
    private Long farmerId;

    @NotBlank(message = "Full Name is mandatory")
    @Size(min = 1, max = 100, message = "Full Name must be between 1 and 100 characters")
    private String fullName;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is mandatory")
    private String email;

    @NotBlank(message = "Mobile Number is mandatory")
    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile Number must be exactly 10 digits")
    private String mobileNumber;

    @NotBlank(message = "State Name is mandatory")
    private String stateName;

    @NotBlank(message = "District Name is mandatory")
    private String districtName;

    @NotBlank(message = "Sub-District Name is mandatory")
    private String subDistrictName;

    @NotNull(message = "Service Categories cannot be null")
    @Size(min = 1, message = "At least one service category must be provided")
    private List<String> serviceCategories;

    
	public Long getFarmerId() {
		return farmerId;
	}
	public void setFarmerId(Long farmerId) {
		this.farmerId = farmerId;
	}
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
	public String getStateName() {
		return stateName;
	}
	public void setStateName(String stateName) {
		this.stateName = stateName;
	}
	public String getDistrictName() {
		return districtName;
	}
	public void setDistrictName(String districtName) {
		this.districtName = districtName;
	}
	public String getSubDistrictName() {
		return subDistrictName;
	}
	public void setSubDistrictName(String subDistrictName) {
		this.subDistrictName = subDistrictName;
	}
	public List<String> getServiceCategories() {
		return serviceCategories;
	}
	public void setServiceCategories(List<String> serviceCategories) {
		this.serviceCategories = serviceCategories;
	}
	
    
}
