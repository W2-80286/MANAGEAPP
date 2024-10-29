package com.agri.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class FarmerQueryDto {
	@NotBlank(message = "Farmer Name is mandatory")
    @Size(min = 1, max = 100, message = "Farmer Name must be between 1 and 100 characters")
    private String farmerName;

    @NotBlank(message = "Query Text is mandatory")
    @Size(min = 1, max = 500, message = "Query Text must be between 1 and 500 characters")
    private String queryText;

    @NotNull(message = "Images cannot be null")
    @Size(min = 1, message = "At least one image URL or path must be provided")
    private List<@NotBlank(message = "Image URL or path cannot be empty") String> images; // Assuming images are stored as URLs or file paths

    // Constructors, Getters, and Setters
    public FarmerQueryDto(String farmerName, String queryText, List<String> images) {
        this.farmerName = farmerName;
        this.queryText = queryText;
        this.images = images;
    }

    // Getters and Setters
    public String getFarmerName() {
        return farmerName;
    }

    public void setFarmerName(String farmerName) {
        this.farmerName = farmerName;
    }

    public String getQueryText() {
        return queryText;
    }

    public void setQueryText(String queryText) {
        this.queryText = queryText;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }
}
