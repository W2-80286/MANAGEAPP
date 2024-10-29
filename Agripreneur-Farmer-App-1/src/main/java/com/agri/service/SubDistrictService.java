package com.agri.service;

import com.agri.model.SubDistrict;
import com.agri.repository.SubDistrictRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubDistrictService {

    @Autowired
    private SubDistrictRepository subDistrictRepository;

    public List<SubDistrict> getAllSubDistricts() {
        return subDistrictRepository.findAll();
    }

    public SubDistrict getSubDistrictById(Long id) {
        return subDistrictRepository.findById(id).orElse(null);
    }

    public SubDistrict saveSubDistrict(SubDistrict subDistrict) {
        return subDistrictRepository.save(subDistrict);
    }

    public void deleteSubDistrict(Long id) {
        subDistrictRepository.deleteById(id);
    }

    public void resetAutoIncrement() {
        subDistrictRepository.resetAutoIncrement();
    }
    
    public List<SubDistrict> getSubDistrictsByDistrictId(Long districtId) {
        return subDistrictRepository.findByDistrictId(districtId);
    }
}
