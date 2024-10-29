package com.agri.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

public class AgripreneursByCategoryDto {

    @NotNull(message = "Agripreneur ID is required")
    private Long agripreneurId;

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "ID number is required")
    private String idNo;

    @NotBlank(message = "Venture name is required")
    private String ventureName;

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "District is required")
    private String district;

    @NotBlank(message = "Sub-District is required")
    private String subDistrict;

    @NotBlank(message = "Village is required")
    private String village;

    @NotNull(message = "Pincode is required")
    @Digits(integer = 6, fraction = 0, message = "Pincode must be a valid 6-digit number")
    private Long pincode;

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must be a valid 10-digit number")
    private String mobileNumber;

    @Pattern(regexp = "^[0-9]{10}$", message = "Alternate number must be a valid 10-digit number")
    private Long alternateNumber;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    private String traningCenterName;

    @Positive(message = "Annual turnover must be a positive value")
    private Double annualTurnover;

    @Positive(message = "Annual income must be a positive value")
    private Double annualIncome;

    @PositiveOrZero(message = "Persons employed must be a non-negative value")
    private Integer personsEmployed;

    @PositiveOrZero(message = "Farmers covered must be a non-negative value")
    private Integer farmersCovered;

    @PositiveOrZero(message = "Villages covered must be a non-negative value")
    private Integer villagesCovered;

    private Boolean bankLoan;

    private Boolean subsidy;

    private String keywords;

    @NotBlank(message = "Service cost is required")
    private String serviceCost;

    @NotEmpty(message = "At least one service category must be selected")
    private List<String> serviceCategories;

    @NotEmpty(message = "At least one image must be provided")
    private List<AgripreneurImageDto> images;

    @Valid
    private CoordinatesDto coordinates;

    // Getters and Setters

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
}
