package com.agri.controller;

import com.agri.model.SubDistrict;
import com.agri.response.ApiResponse;
import com.agri.service.SubDistrictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subdistricts")
public class SubDistrictController {

    @Autowired
    private SubDistrictService subDistrictService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<SubDistrict>>> getAllSubDistricts() {
        List<SubDistrict> subDistricts = subDistrictService.getAllSubDistricts();
        return new ResponseEntity<>(new ApiResponse<>("Sub-districts fetched successfully", subDistricts), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SubDistrict>> getSubDistrictById(@PathVariable Long id) {
        SubDistrict subDistrict = subDistrictService.getSubDistrictById(id);
        if (subDistrict != null) {
            return new ResponseEntity<>(new ApiResponse<>("Sub-district fetched successfully", subDistrict), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiResponse<>("Sub-district not found", null), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/addSubDistrict")
    public ResponseEntity<ApiResponse<SubDistrict>> createSubDistrict(@RequestBody SubDistrict subDistrict) {
        SubDistrict createdSubDistrict = subDistrictService.saveSubDistrict(subDistrict);
        return new ResponseEntity<>(new ApiResponse<>("Sub-district added successfully", createdSubDistrict), HttpStatus.CREATED);
    }

    @PutMapping("/updateSubDistrict/{id}")
    public ResponseEntity<ApiResponse<SubDistrict>> updateSubDistrict(@PathVariable Long id, @RequestBody SubDistrict subDistrictDetails) {
        SubDistrict currentSubDistrict = subDistrictService.getSubDistrictById(id);
        if (currentSubDistrict != null) {
            currentSubDistrict.setName(subDistrictDetails.getName());
            currentSubDistrict.setDistrict(subDistrictDetails.getDistrict());
            SubDistrict updatedSubDistrict = subDistrictService.saveSubDistrict(currentSubDistrict);
            return new ResponseEntity<>(new ApiResponse<>("Sub-district updated successfully", updatedSubDistrict), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiResponse<>("Sub-district not found", null), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/deleteSubDistrict/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSubDistrict(@PathVariable Long id) {
        SubDistrict subDistrict = subDistrictService.getSubDistrictById(id);
        if (subDistrict != null) {
            subDistrictService.deleteSubDistrict(id);
            return new ResponseEntity<>(new ApiResponse<>("Sub-district deleted successfully", null), HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(new ApiResponse<>("Sub-district not found", null), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/resetAutoIncrement")
    public ResponseEntity<ApiResponse<Void>> resetAutoIncrement() {
        subDistrictService.resetAutoIncrement();
        return new ResponseEntity<>(new ApiResponse<>("Auto-increment reset successfully", null), HttpStatus.OK);
    }
}
