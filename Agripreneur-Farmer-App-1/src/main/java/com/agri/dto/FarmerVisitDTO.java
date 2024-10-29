package com.agri.dto;

import com.agri.model.District;
import com.agri.model.State;
import com.agri.model.SubDistrict;

public class FarmerVisitDTO {

    private String fullName;
    private String email;
    private String mobileNumber;
    private String stateName;
    private String districtName;
    private String subDistrictName;

  
	public FarmerVisitDTO(String fullName, String email, String mobileNumber, String stateName, String districtName,
			String subDistrictName) {
		super();
		this.fullName = fullName;
		this.email = email;
		this.mobileNumber = mobileNumber;
		this.stateName = stateName;
		this.districtName = districtName;
		this.subDistrictName = subDistrictName;
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

    // Getters and setters for each field
}
