package com.agri.dto;

import java.util.List;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public class AgripreneurGetDto {

    @NotNull(message = "Agripreneur ID is required")
    private Long agripreneurId;

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "ID No is required")
    private String idNo;

    @NotBlank(message = "Venture name is required")
    private String ventureName;

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "District is required")
    private String district;

    @NotBlank(message = "Sub-district is required")
    private String subDistrict;

    @NotBlank(message = "Village is required")
    private String village;

    @NotNull(message = "Pincode is required")
    @Digits(integer = 6, fraction = 0, message = "Pincode should be a valid 6-digit number")
    private Long pincode;

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[7-9][0-9]{9}$", message = "Mobile number should be a valid 10-digit number starting with 7, 8, or 9")
    private String mobileNumber;

    @Digits(integer = 10, fraction = 0, message = "Alternate number should be a valid 10-digit number")
    private Long alternateNumber;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Training center name is required")
    private String traningCenterName;

    @NotNull(message = "Annual turnover is required")
    @Positive(message = "Annual turnover must be a positive value")
    private Double annualTurnover;

    @NotNull(message = "Annual income is required")
    @Positive(message = "Annual income must be a positive value")
    private Double annualIncome;

    @NotNull(message = "Number of persons employed is required")
    @PositiveOrZero(message = "Persons employed must be zero or positive")
    private Integer personsEmployed;

    @NotNull(message = "Number of farmers covered is required")
    @PositiveOrZero(message = "Farmers covered must be zero or positive")
    private Integer farmersCovered;

    @NotNull(message = "Number of villages covered is required")
    @PositiveOrZero(message = "Villages covered must be zero or positive")
    private Integer villagesCovered;

    @NotNull(message = "Bank loan status is required")
    private Boolean bankLoan;

    @NotNull(message = "Subsidy status is required")
    private Boolean subsidy;

    @NotBlank(message = "Keywords are required")
    private String keywords;

    private String serviceCost; // Optional field, so no validation

    @NotEmpty(message = "At least one service category is required")
    private List<String> serviceCategories;

    @NotEmpty(message = "At least one image is required")
    private List<AgripreneurImageDto> images;

    private CoordinatesDto coordinates; // Optional field, so no validation

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

	public List<AgripreneurImageDto> getImages() {
		return images;
	}

	public void setImages(List<AgripreneurImageDto> images) {
		this.images = images;
	}

	public CoordinatesDto getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(CoordinatesDto coordinates) {
		this.coordinates = coordinates;
	}

    // Getters and setters...
    
    
}
