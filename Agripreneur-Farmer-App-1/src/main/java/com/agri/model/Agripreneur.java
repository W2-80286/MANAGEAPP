package com.agri.model;


import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Agripreneur {
	
	
    public Agripreneur(Long agripreneurId, String fullName, String idNo, String ventureName,
			List<Category> serviceCategories, String serviceCost, State state, District district,
			SubDistrict subDistrict, Village village, Coordinates coordinates, Long pincode,
			@Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must be exactly 10 digits") String mobileNumber,
			Long alternateNumber, String email, String traningCenterName, Double annualTurnover, Double annualIncome,
			Integer personsEmployed, Integer farmersCovered, Integer villagesCovered, Boolean bankLoan, Boolean subsidy,
			String keywords, List<AgripreneurImage> images, List<Review> reviews, List<FarmerQuery> queries, String otp,
			boolean registrationCompleted, Boolean verified, Boolean approved, String refreshToken,
			String verificationComments, List<ProfileVisit> profileVisits) {
		super();
		this.agripreneurId = agripreneurId;
		this.fullName = fullName;
		this.idNo = idNo;
		this.ventureName = ventureName;
		this.serviceCategories = serviceCategories;
		this.serviceCost = serviceCost;
		this.state = state;
		this.district = district;
		this.subDistrict = subDistrict;
		this.village = village;
		this.coordinates = coordinates;
		this.pincode = pincode;
		this.mobileNumber = mobileNumber;
		this.alternateNumber = alternateNumber;
		this.email = email;
		this.traningCenterName = traningCenterName;
		this.annualTurnover = annualTurnover;
		this.annualIncome = annualIncome;
		this.personsEmployed = personsEmployed;
		this.farmersCovered = farmersCovered;
		this.villagesCovered = villagesCovered;
		this.bankLoan = bankLoan;
		this.subsidy = subsidy;
		this.keywords = keywords;
		this.images = images;
		this.reviews = reviews;
		this.queries = queries;
		this.otp = otp;
		this.registrationCompleted = registrationCompleted;
		this.verified = verified;
		this.approved = approved;
		this.refreshToken = refreshToken;
		this.verificationComments = verificationComments;
		this.profileVisits = profileVisits;
	}


	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "agripreneur_id")
    private Long agripreneurId;

    private String fullName;
    private String idNo;
    private String ventureName;

    @ManyToMany
    @JoinTable(
        name = "agripreneur_category",
        joinColumns = @JoinColumn(name = "agripreneur_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @JsonManagedReference
    private List<Category> serviceCategories;

    private String serviceCost;

    @ManyToOne
    @JoinColumn(name = "state_id", nullable = false)
    private State state;

    @ManyToOne
    @JoinColumn(name = "district_id", nullable = false)
    private District district;

    @ManyToOne
    @JoinColumn(name = "subDistrict_id", nullable = false)
    private SubDistrict subDistrict;

    @ManyToOne
    @JoinColumn(name = "village_id")
    private Village village;
    
    @OneToOne(mappedBy = "agripreneur", cascade = CascadeType.ALL)
    private Coordinates coordinates;

    private Long pincode;
    
    @Pattern(regexp="^[0-9]{10}$", message="Mobile number must be exactly 10 digits")
    @Column(unique = true)
    private String mobileNumber;
    
    private Long alternateNumber;
    
    private String email;
    
    private String traningCenterName;
    
    private Double annualTurnover;
    
    private Double annualIncome;
    
    private Integer personsEmployed;
    private Integer farmersCovered;
    private Integer villagesCovered;
    private Boolean bankLoan;
    private Boolean subsidy;
    
    @Column(name = "keywords")
    private String keywords;

    @OneToMany(mappedBy = "agripreneur", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<AgripreneurImage> images;
    
    @OneToMany(mappedBy = "agripreneur", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews;
   
    @OneToMany(mappedBy = "agripreneur", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<FarmerQuery> queries;
    
	private String otp; // For storing OTP
	
   
	private boolean registrationCompleted;
	
	@Column(name = "verified", nullable = false)
	private Boolean verified=false;
	

	@Column(name = "approved", nullable = false)
	private Boolean approved = false;
	
	
    private String refreshToken;

    
	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public Boolean getVerified() {
		return verified;
	}

	public Boolean getApproved() {
		return approved;
	}

	// Getters and Setters
	public Boolean isApproved() {
	    return approved;
	}

	public void setApproved(Boolean approved) {
	    this.approved = approved;
	}


	public Boolean isVerified() {
	    return verified;
	}

	public void setVerified(Boolean verified) {
	    this.verified = verified;
	}

	
	@Column(name = "verification_comments")
	
	private String verificationComments;

	public String getVerificationComments() {
	    return verificationComments;
	}

	public void setVerificationComments(String verificationComments) {
	    this.verificationComments = verificationComments;
	}

	
	@OneToMany(mappedBy = "agripreneur", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ProfileVisit> profileVisits;

	
	public List<ProfileVisit> getProfileVisits() {
		return profileVisits;
	}

	public void setProfileVisits(List<ProfileVisit> profileVisits) {
		this.profileVisits = profileVisits;
	}

	public boolean isRegistrationCompleted() {
        return registrationCompleted;
    }

    public void setRegistrationCompleted(boolean registrationCompleted) {
        this.registrationCompleted = registrationCompleted;
    }



    public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}
	
	public List<FarmerQuery> getQueries() {
		return queries;
	}

	public void setQueries(List<FarmerQuery> queries) {
		this.queries = queries;
	}

	public Agripreneur() {
		 
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

	public List<Category> getServiceCategories() {
		return serviceCategories;
	}

	public void setServiceCategories(List<Category> serviceCategories) {
		this.serviceCategories = serviceCategories;
	}

	public String getServiceCost() {
		return serviceCost;
	}

	public void setServiceCost(String serviceCost) {
		this.serviceCost = serviceCost;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public District getDistrict() {
		return district;
	}

	public void setDistrict(District district) {
		this.district = district;
	}

	public SubDistrict getSubDistrict() {
		return subDistrict;
	}

	public void setSubDistrict(SubDistrict subDistrict) {
		this.subDistrict = subDistrict;
	}

	public Village getVillage() {
		return village;
	}

	public void setVillage(Village village) {
		this.village = village;
	}

	public Coordinates getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(Coordinates coordinates) {
		this.coordinates = coordinates;
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

	public List<AgripreneurImage> getImages() {
		return images;
	}

	public void setImages(List<AgripreneurImage> images) {
		this.images = images;
	}

	public List<Review> getReviews() {
		return reviews;
	}

	public void setReviews(List<Review> reviews) {
		this.reviews = reviews;
	}

	
}
