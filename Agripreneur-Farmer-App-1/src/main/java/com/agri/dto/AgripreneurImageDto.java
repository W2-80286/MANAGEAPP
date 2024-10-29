package com.agri.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AgripreneurImageDto {

    @NotNull(message = "Image ID is required")
    private Long id;

    @NotBlank(message = "Image path is required")
    private String imagePath;
    
    @NotBlank(message = "Image data is required")
    private String imageData; // Base64 encoded image data

    public String getImageData() {
        return imageData;
    }

    public void setImageData(String imageData) {
        this.imageData = imageData;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

}
