package com.agri.controller;
/*
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.agri.model.*;
import com.agri.service.LocationImportService;
import com.agri.service.LocationService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/locations")
public class LocationController {
    @Autowired
    private LocationService locationService;

    @Autowired
    private LocationImportService locationImportService;

    @GetMapping("/states")
    public List<State> getAllStates() {
        return locationService.getAllStates();
    }

    @GetMapping("/states/{id}")
    public Optional<State> getStateById(@PathVariable Long id) {
        return locationService.getStateById(id);
    }

    @GetMapping("/states/{stateId}/districts")
    public List<District> getDistrictsByState(@PathVariable Long stateId) {
        return locationService.getDistrictsByState(stateId);
    }

    @GetMapping("/districts/{districtId}/subDistricts")
    public List<SubDistrict> getSubDistrictsByDistrict(@PathVariable Long districtId) {
        return locationService.getSubDistrictsByDistrict(districtId);
    }

    @GetMapping("/subDistricts/{subDistrictId}/villages")
    public List<Village> getVillagesBySubDistrict(@PathVariable Long subDistrictId) {
        return locationService.getVillagesBySubDistrict(subDistrictId);
    }

    @PostMapping("/import")
    public String importLocations(@RequestParam String filePath) {
        try {
            locationImportService.importLocations(filePath);
            return "Import successful";
        } catch (IOException e) {
            e.printStackTrace();
            return "Import failed";
        }
    }
}
*/