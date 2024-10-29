package com.agri.dto;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import com.agri.model.AgripreneurImage;

public class AgripreneursBySubDistrictIdDto {

    @NotNull
    private Long agripreneurId;

    @NotBlank
    @Size(min = 2, max = 100)
    private String fullName;

    @NotBlank
    @Size(min = 2, max = 20)
    private String idNo;

    @NotBlank
    @Size(min = 2, max = 100)
    private String ventureName;

    @NotBlank
    private String state;

    @NotBlank
    private String district;

    @NotBlank
    private String subDistrict;

    @NotBlank
    private String village;

    @NotNull
    @Pattern(regexp = "\\d{6}", message = "Pincode must be a 6-digit number")
    private Long pincode;

    @NotNull
    @Pattern(regexp = "\\d{10}", message = "Mobile number must be a 10-digit number")
    private String mobileNumber;

    @Pattern(regexp = "\\d{10}", message = "Alternate number must be a 10-digit number")
    private Long alternateNumber;

    @Email
    private String email;

    @NotBlank
    private String traningCenterName;

    @NotNull
    @Min(0)
    private Double annualTurnover;

    @NotNull
    @Min(0)
    private Double annualIncome;

    @NotNull
    @Min(0)
    private Integer personsEmployed;

    @NotNull
    @Min(0)
    private Integer farmersCovered;

    @NotNull
    @Min(0)
    private Integer villagesCovered;

    @NotNull
    private Boolean bankLoan;

    @NotNull
    private Boolean subsidy;

    @NotBlank
    private String keywords;

    @NotBlank
    @Pattern(regexp = "\\d+(\\.\\d{1,2})?", message = "Service cost must be a valid number with up to two decimal places")
    private String serviceCost;

    @NotEmpty
    private List<String> serviceCategories;

    @NotEmpty
    private List<AgripreneurImage> images;

    @NotNull
    private CoordinatesDto coordinates;
    
    private Double rating;  // Add this for average rating

    
   	public Double getRating() {
   		return rating;
   	}

   	public void setRating(Double rating) {
   		this.rating = rating;
   	}


	public Long getAgripreneurId() {
		return agripreneurId;
	}

	public void setAgripreneurId(Long agripreneurId) {
		this.agripreneurId = agripreneurId;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getIdNo() {
		return idNo;
	}

	public void setIdNo(String idNo) {
		this.idNo = idNo;
	}

	public String getVentureName() {
		return ventureName;
	}

	public void setVentureName(String ventureName) {
		this.ventureName = ventureName;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getSubDistrict() {
		return subDistrict;
	}

	public void setSubDistrict(String subDistrict) {
		this.subDistrict = subDistrict;
	}

	public String getVillage() {
		return village;
	}

	public void setVillage(String village) {
		this.village = village;
	}

	public Long getPincode() {
		return pincode;
	}

	public void setPincode(Long pincode) {
		this.pincode = pincode;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public Long getAlternateNumber() {
		return alternateNumber;
	}

	public void setAlternateNumber(Long alternateNumber) {
		this.alternateNumber = alternateNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTraningCenterName() {
		return traningCenterName;
	}

	public void setTraningCenterName(String traningCenterName) {
		this.traningCenterName = traningCenterName;
	}

	public Double getAnnualTurnover() {
		return annualTurnover;
	}

	public void setAnnualTurnover(Double annualTurnover) {
		this.annualTurnover = annualTurnover;
	}

	public Double getAnnualIncome() {
		return annualIncome;
	}

	public void setAnnualIncome(Double annualIncome) {
		this.annualIncome = annualIncome;
	}

	public Integer getPersonsEmployed() {
		return personsEmployed;
	}

	public void setPersonsEmployed(Integer personsEmployed) {
		this.personsEmployed = personsEmployed;
	}

	public Integer getFarmersCovered() {
		return farmersCovered;
	}

	public void setFarmersCovered(Integer farmersCovered) {
		this.farmersCovered = farmersCovered;
	}

	public Integer getVillagesCovered() {
		return villagesCovered;
	}

	public void setVillagesCovered(Integer villagesCovered) {
		this.villagesCovered = villagesCovered;
	}

	public Boolean getBankLoan() {
		return bankLoan;
	}

	public void setBankLoan(Boolean bankLoan) {
		this.bankLoan = bankLoan;
	}

	public Boolean getSubsidy() {
		return subsidy;
	}

	public void setSubsidy(Boolean subsidy) {
		this.subsidy = subsidy;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getServiceCost() {
		return serviceCost;
	}

	public void setServiceCost(String serviceCost) {
		this.serviceCost = serviceCost;
	}

	public List<String> getServiceCategories() {
		return serviceCategories;
	}

	public void setServiceCategories(List<String> serviceCategories) {
		this.serviceCategories = serviceCategories;
	}

	public List<AgripreneurImage> getImages() {
		return images;
	}

	public void setImages(List<AgripreneurImage> images) {
		this.images = images;
	}

	public CoordinatesDto getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(CoordinatesDto coordinates) {
		this.coordinates = coordinates;
	}

    // Getters and Setters
}
