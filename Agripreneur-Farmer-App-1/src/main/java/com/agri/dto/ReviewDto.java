package com.agri.dto;

public class ReviewDto {
	
	
    private Long farmerId;
    private Long agripreneurId;
    private String reviewText;
    private int rating;
    
	public Long getFarmerId() {
		return farmerId;
	}
	public void setFarmerId(Long farmerId) {
		this.farmerId = farmerId;
	}
	public Long getAgripreneurId() {
		return agripreneurId;
	}
	public void setAgripreneurId(Long agripreneurId) {
		this.agripreneurId = agripreneurId;
	}
	public String getReviewText() {
		return reviewText;
	}
	public void setReviewText(String reviewText) {
		this.reviewText = reviewText;
	}
	public int getRating() {
		return rating;
	}
	public void setRating(int rating) {
		this.rating = rating;
	}
    
    

    // Getters and Setters
    
    
}
