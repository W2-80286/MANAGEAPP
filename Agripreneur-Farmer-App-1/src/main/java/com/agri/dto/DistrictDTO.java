package com.agri.dto;

import java.util.List;

public class DistrictDTO {
	
    private String district;
    private List<SubDistrictDTO> subDistricts;

    // Getters and Setters
    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public List<SubDistrictDTO> getSubDistricts() {
        return subDistricts;
    }

    public void setSubDistricts(List<SubDistrictDTO> subDistricts) {
        this.subDistricts = subDistricts;
    }
}

