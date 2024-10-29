package com.agri.controller;

import com.agri.model.District;
import com.agri.model.SubDistrict;
import com.agri.response.ApiResponse;
import com.agri.service.DistrictService;
import com.agri.service.SubDistrictService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/districts")
public class DistrictController {

    @Autowired
    private DistrictService districtService;
    
    @Autowired
    private SubDistrictService subDistrictService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<District>>> getAllDistricts() {
        List<District> districts = districtService.getAllDistricts();
        return new ResponseEntity<>(new ApiResponse<>("Districts fetched successfully", districts), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<District>> getDistrictById(@PathVariable Long id) {
        District district = districtService.getDistrictById(id);
        if (district != null) {
            return new ResponseEntity<>(new ApiResponse<>("District fetched successfully", district), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiResponse<>("District not found", null), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/addDistrict")
    public ResponseEntity<ApiResponse<District>> createDistrict(@RequestBody District district) {
        District createdDistrict = districtService.saveDistrict(district);
        return new ResponseEntity<>(new ApiResponse<>("District added successfully", createdDistrict), HttpStatus.CREATED);
    }

    @PutMapping("/updateDistrict/{id}")
    public ResponseEntity<ApiResponse<District>> updateDistrict(@PathVariable Long id, @RequestBody District districtDetails) {
        District currentDistrict = districtService.getDistrictById(id);
        if (currentDistrict != null) {
            currentDistrict.setName(districtDetails.getName());
            currentDistrict.setState(districtDetails.getState());
            currentDistrict.setSubDistricts(districtDetails.getSubDistricts());
            District updatedDistrict = districtService.saveDistrict(currentDistrict);
            return new ResponseEntity<>(new ApiResponse<>("District updated successfully", updatedDistrict), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiResponse<>("District not found", null), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/deleteDistrict/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDistrict(@PathVariable Long id) {
        District district = districtService.getDistrictById(id);
        if (district != null) {
            districtService.deleteDistrict(id);
            return new ResponseEntity<>(new ApiResponse<>("District deleted successfully", null), HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(new ApiResponse<>("District not found", null), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/resetAutoIncrement")
    public ResponseEntity<ApiResponse<Void>> resetAutoIncrement() {
        districtService.resetAutoIncrement();
        return new ResponseEntity<>(new ApiResponse<>("Auto-increment reset successfully", null), HttpStatus.OK);
    }
    @GetMapping("/{id}/subdistricts")
    public ResponseEntity<ApiResponse<List<SubDistrict>>> getSubDistrictsByDistrict(@PathVariable Long id) {
        List<SubDistrict> subDistricts = subDistrictService.getSubDistrictsByDistrictId(id);
        return new ResponseEntity<>(new ApiResponse<>("Sub-districts fetched successfully", subDistricts), HttpStatus.OK);
    }
}
