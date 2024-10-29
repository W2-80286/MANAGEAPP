package com.agri.dto;

import jakarta.validation.constraints.NotNull;

public class MobileNumberDto {
	
	@NotNull(message="Mobile number must not be null")
	private Long mobileNumber;

	public Long getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(Long mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

		
}
