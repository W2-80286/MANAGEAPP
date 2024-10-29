package com.agri.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AgripreneurRegistrationDto {

	@NotEmpty(message = "Full name cannot be empty")
    @Size(max = 100, message = "Full name cannot exceed 100 characters")
    private String fullName;

    @NotEmpty(message = "ID number cannot be empty")
    @Size(max = 20, message = "ID number cannot exceed 20 characters")
    private String idNo;

    @NotEmpty(message = "Venture name cannot be empty")
    @Size(max = 100, message = "Venture name cannot exceed 100 characters")
    private String ventureName;

    @NotNull(message = "Service category IDs cannot be null")
    private List<Long> serviceCategoryIds;

    private String serviceCost;

    @NotNull(message = "State ID cannot be null")
    private Long stateId;

    @NotNull(message = "District ID cannot be null")
    private Long districtId;

    @NotNull(message = "Sub-district ID cannot be null")
    private Long subDistrictId;

    @NotEmpty(message = "Village name cannot be empty")
    @Size(max = 100, message = "Village name cannot exceed 100 characters")
    private String villageName;
    
    @NotNull(message = "Pincode cannot be null")
    @Pattern(regexp="^[0-9]{6}$", message="PIN code must be exactly 6 digits")
    private Long pincode;   
    
    @NotNull(message = "mobile Number cannot be null")
    @Pattern(regexp="^[0-9]{10}$", message="Mobile number must be 10 digits")
    private String mobileNumber;

    private Long alternateNumber;

    @NotEmpty(message = "Email cannot be empty")
    @Email(message = "Email should be valid")
    private String email;

    @NotEmpty(message = "Training center name cannot be empty")
    @Size(max = 100, message = "Training center name cannot exceed 100 characters")
    private String trainingCenterName;

    @NotNull(message = "Annual turnover cannot be null")
    private Double annualTurnover;

    @NotNull(message = "Annual income cannot be null")
    private Double annualIncome;

    @NotNull(message = "Number of persons employed cannot be null")
    private Integer personsEmployed;

    @NotNull(message = "Number of farmers covered cannot be null")
    private Integer farmersCovered;

    @NotNull(message = "Number of villages covered cannot be null")
    private Integer villagesCovered;

    @NotNull(message = "Bank loan details cannot be null")
    private Boolean bankLoan;

    @NotNull(message = "Subsidy details cannot be null")
    private Boolean subsidy;

    @NotEmpty(message = "Keywords cannot be empty")
    @Size(max = 255, message = "Keywords cannot exceed 255 characters")
    private String keywords;

    @NotNull(message = "Latitude cannot be null")
    private Double latitude;

    @NotNull(message = "Longitude cannot be null")
    private Double longitude;

    @NotEmpty(message = "Images cannot be empty")
    private List<MultipartFile> images;

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

	public List<Long> getServiceCategoryIds() {
		return serviceCategoryIds;
	}

	public void setServiceCategoryIds(List<Long> serviceCategoryIds) {
		this.serviceCategoryIds = serviceCategoryIds;
	}

	public String getServiceCost() {
		return serviceCost;
	}

	public void setServiceCost(String serviceCost) {
		this.serviceCost = serviceCost;
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
