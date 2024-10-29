package com.agri.dto;

import org.springframework.web.multipart.MultipartFile;
import lombok.Data;

import jakarta.validation.constraints.*;
import java.util.List;

@Data
public class AgripreneurUpdateDto {

    @NotEmpty(message = "Full name cannot be empty")
    private String fullName;

    @NotEmpty(message = "Venture name cannot be empty")
    private String ventureName;

    @NotNull(message = "Service category IDs cannot be null")
    private List<Long> serviceCategoryIds;

    @NotNull(message = "State ID cannot be null")
    private Long stateId;

    @NotEmpty(message = "Service cost cannot be empty")
    private String serviceCost;

    @NotNull(message = "District ID cannot be null")
    private Long districtId;

    @NotNull(message = "Sub-District ID cannot be null")
    private Long subDistrictId;

    @NotEmpty(message = "Village name cannot be empty")
    private String villageName;

    @NotNull(message = "Pincode cannot be null")
    @Digits(integer = 6, fraction = 0, message = "Pincode must be a 6-digit number")
    private Long pincode;

    @Digits(integer = 10, fraction = 0, message = "Alternate number must be a valid 10-digit number")
    private Long alternateNumber;

    @Email(message = "Email should be valid")
    private String email;

    @NotEmpty(message = "Training center name cannot be empty")
    private String trainingCenterName;

    @NotNull(message = "Annual turnover cannot be null")
    @Positive(message = "Annual turnover must be a positive number")
    private Double annualTurnover;

    @NotNull(message = "Annual income cannot be null")
    @Positive(message = "Annual income must be a positive number")
    private Double annualIncome;

    @NotNull(message = "Number of persons employed cannot be null")
    @Min(value = 0, message = "Persons employed cannot be negative")
    private Integer personsEmployed;

    @NotNull(message = "Number of farmers covered cannot be null")
    @Min(value = 0, message = "Farmers covered cannot be negative")
    private Integer farmersCovered;

    @NotNull(message = "Number of villages covered cannot be null")
    @Min(value = 0, message = "Villages covered cannot be negative")
    private Integer villagesCovered;

    private Boolean bankLoan;

    private Boolean subsidy;

    @NotEmpty(message = "Keywords cannot be empty")
    private String keywords;

    @NotNull(message = "Latitude cannot be null")
    private Double latitude;

    @NotNull(message = "Longitude cannot be null")
    private Double longitude;

    @NotNull(message = "Images cannot be null")
    private List<MultipartFile> images;

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getVentureName() {
		return ventureName;
	}

	public void setVentureName(String ventureName) {
		this.ventureName = ventureName;
	}

	public List<Long> getServiceCategoryIds() {
		return serviceCategoryIds;
	}

	public void setServiceCategoryIds(List<Long> serviceCategoryIds) {
		this.serviceCategoryIds = serviceCategoryIds;
	}

	public Long getStateId() {
		return stateId;
	}

	public void setStateId(Long stateId) {
		this.stateId = stateId;
	}

	public String getServiceCost() {
		return serviceCost;
	}

	public void setServiceCost(String serviceCost) {
		this.serviceCost = serviceCost;
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

	public String getVillageName() {
		return villageName;
	}

	public void setVillageName(String villageName) {
		this.villageName = villageName;
	}

	public Long getPincode() {
		return pincode;
	}

	public void setPincode(Long pincode) {
		this.pincode = pincode;
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

	public String getTrainingCenterName() {
		return trainingCenterName;
	}

	public void setTrainingCenterName(String trainingCenterName) {
		this.trainingCenterName = trainingCenterName;
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

	public List<MultipartFile> getImages() {
		return images;
	}

	public void setImages(List<MultipartFile> images) {
		this.images = images;
	}
    
    
}
