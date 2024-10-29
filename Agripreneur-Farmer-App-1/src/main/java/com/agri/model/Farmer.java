package com.agri.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "farmers")
public class Farmer {
	
	
    
    public Farmer(Long farmerId, @NotNull(message = "Full name is required.") String fullname,
			@Email(message = "Email should be valid.") String email,
			@NotNull(message = "Mobile number is required.") @Pattern(regexp = "^\\d{10}$", message = "Mobile number must be 10 digits.") String mobileNumber,
			@NotNull(message = "State ID is required.") State state,
			@NotNull(message = "District ID is required.") District district,
			@NotNull(message = "Sub-District ID is required.") SubDistrict subDistrict,
			List<Category> serviceCategories, List<Review> reviews, List<FarmerQuery> queries,
			List<ProfileVisit> profileVisits, String otp) {
		super();
		this.farmerId = farmerId;
		this.fullname = fullname;
		this.email = email;
		this.mobileNumber = mobileNumber;
		this.state = state;
		this.district = district;
		this.subDistrict = subDistrict;
		this.serviceCategories = serviceCategories;
		this.reviews = reviews;
		this.queries = queries;
		this.profileVisits = profileVisits;
		this.otp = otp;
	}
    public Farmer() {}
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "farmer_id")
    private Long farmerId;

    @NotNull(message = "Full name is required.")
    private String fullname;
    
    @Email(message = "Email should be valid.")
    private String email;
    
    @NotNull(message = "Mobile number is required.")
    @Pattern(regexp = "^\\d{10}$", message = "Mobile number must be 10 digits.")
    @Column(unique = true)
    private String mobileNumber;  // Changed to String for better representation

    @ManyToOne
    @JoinColumn(name = "state_id", nullable = false)
    @NotNull(message = "State ID is required.")
    private State state;

    @ManyToOne
    @JoinColumn(name = "district_id", nullable = false)
    @NotNull(message = "District ID is required.")
    private District district;

    @ManyToOne
    @JoinColumn(name = "subDistrict_id", nullable = false)
    @NotNull(message = "Sub-District ID is required.")
    private SubDistrict subDistrict;

    @ManyToMany
    @JoinTable(
        name = "farmer_category",
        joinColumns = @JoinColumn(name = "farmer_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> serviceCategories;

    @OneToMany(mappedBy = "farmer", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Review> reviews;

    @OneToMany(mappedBy = "farmer", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<FarmerQuery> queries;
    
    @OneToMany(mappedBy = "farmer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProfileVisit> profileVisits;

    
    public List<ProfileVisit> getProfileVisits() {
		return profileVisits;
	}

	public void setProfileVisits(List<ProfileVisit> profileVisits) {
		this.profileVisits = profileVisits;
	}

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

	private String otp; // For storing OTP

    // Getters and Setters
    public Long getFarmerId() {
        return farmerId;
    }

    public void setFarmerId(Long farmerId) {
        this.farmerId = farmerId;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileNumber() {
        return mobileNumber;  // Changed to String
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;  // Changed to String
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

    public List<Category> getServiceCategories() {
        return serviceCategories;
    }

    public void setServiceCategories(List<Category> serviceCategories) {
        this.serviceCategories = serviceCategories;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public List<FarmerQuery> getQueries() {
        return queries;
    }

    public void setQueries(List<FarmerQuery> queries) {
        this.queries = queries;
    }
}
