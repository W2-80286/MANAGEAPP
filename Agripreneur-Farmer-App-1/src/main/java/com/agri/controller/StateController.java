package com.agri.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.agri.model.District;
import com.agri.model.State;
import com.agri.response.ApiResponse;
import com.agri.service.DistrictService;
import com.agri.service.StateService;

import java.util.List;

@RestController
@RequestMapping("/states")
public class StateController {

    @Autowired
    private StateService stateService;
    
    @Autowired
    private DistrictService districtService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<State>>> getAllStates() {
        List<State> states = stateService.getAllStates();
        return new ResponseEntity<>(new ApiResponse<>("States fetched successfully", states), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<State>> getStateById(@PathVariable Long id) {
        State state = stateService.getStateById(id);
        if (state != null) {
            return new ResponseEntity<>(new ApiResponse<>("State fetched successfully", state), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiResponse<>("State not found", null), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/addState")
    public ResponseEntity<ApiResponse<State>> createState(@RequestBody State state) {
        State createdState = stateService.saveState(state);
        return new ResponseEntity<>(new ApiResponse<>("State added successfully", createdState), HttpStatus.CREATED);
    }

    @PutMapping("/updateState/{id}")
    public ResponseEntity<ApiResponse<State>> updateState(@PathVariable Long id, @RequestBody State stateDetails) {
        State currentState = stateService.getStateById(id);
        if (currentState != null) {
            currentState.setName(stateDetails.getName());  // Assuming `State` has a `name` field
            // Update other fields as necessary
            State updatedState = stateService.saveState(currentState);
            return new ResponseEntity<>(new ApiResponse<>("State updated successfully", updatedState), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiResponse<>("State not found", null), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/deleteState/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteState(@PathVariable Long id) {
        State state = stateService.getStateById(id);
        if (state != null) {
            stateService.deleteState(id);
            return new ResponseEntity<>(new ApiResponse<>("State deleted successfully", null), HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(new ApiResponse<>("State not found", null), HttpStatus.NOT_FOUND);
        }
    }
    
    @GetMapping("/{id}/districts")
    public ResponseEntity<ApiResponse<List<District>>> getDistrictsByState(@PathVariable Long id) {
        List<District> districts = districtService.getDistrictsByStateId(id);
        return new ResponseEntity<>(new ApiResponse<>("Districts fetched successfully", districts), HttpStatus.OK);
    }
}
