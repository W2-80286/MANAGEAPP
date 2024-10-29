package com.agri.dto;

import java.util.List;

public class StateDTO {
    private String state;
    private List<DistrictDTO> districts;

    // Getters and Setters
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<DistrictDTO> getDistricts() {
        return districts;
    }

    public void setDistricts(List<DistrictDTO> districts) {
        this.districts = districts;
    }
}
