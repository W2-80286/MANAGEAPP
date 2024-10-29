package com.agri.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class CoordinatesDto {
	@NotNull(message = "Latitude is mandatory")
    @Min(value = -90, message = "Latitude must be greater than or equal to -90")
    @Max(value = 90, message = "Latitude must be less than or equal to 90")
    
	private Double latitude;
	@NotNull(message = "Latitude is mandatory")
    @Min(value = -90, message = "Latitude must be greater than or equal to -90")
    @Max(value = 90, message = "Latitude must be less than or equal to 90")
    private Double longitude;
    
    
	public CoordinatesDto() {
		
	}
	public CoordinatesDto(Double latitude, Double longitude) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
	}
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

}
