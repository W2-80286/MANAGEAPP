package com.agri.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public class QueryDto {

   // @NotNull(message = "Farmer ID is required")
   // private Long farmerId;

    @NotNull(message = "Agripreneur ID is required")
    private Long agripreneurId;  

    private String queryText;

    private List<MultipartFile> queryImages;
/*
    // Getters and Setters
    public Long getFarmerId() {
        return farmerId;
    }

    public void setFarmerId(Long farmerId) {
        this.farmerId = farmerId;
    }
*/
    public Long getAgripreneurId() {
        return agripreneurId;
    }

    public void setAgripreneurId(Long agripreneurId) {
        this.agripreneurId = agripreneurId;
    }

    public String getQueryText() {
        return queryText;
    }

    public void setQueryText(String queryText) {
        this.queryText = queryText;
    }

    public List<MultipartFile> getQueryImages() {
        return queryImages;
    }

    public void setQueryImages(List<MultipartFile> queryImages) {
        this.queryImages = queryImages;
    }
    
}
