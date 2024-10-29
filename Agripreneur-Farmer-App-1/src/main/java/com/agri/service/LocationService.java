package com.agri.service;

import com.agri.dto.*;
import com.agri.model.*;
import com.agri.repository.StateRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
public class LocationService {

    @Autowired
    private StateRepository stateRepository;

    @Transactional
    public void loadLocationsFromFile(String filePath) throws IOException {
        if (isDataAlreadyLoaded()) {
            System.out.println("Data already exists, skipping loading.");
            return;
        }

        ObjectMapper mapper = new ObjectMapper();
        List<StateDTO> states = mapper.readValue(new File(filePath),
                mapper.getTypeFactory().constructCollectionType(List.class, StateDTO.class));

        for (StateDTO stateDTO : states) {
            State state = new State();
            state.setName(stateDTO.getState());

            for (DistrictDTO districtDTO : stateDTO.getDistricts()) {
                District district = new District();
                district.setName(districtDTO.getDistrict());
                district.setState(state);

                for (SubDistrictDTO subDistrictDTO : districtDTO.getSubDistricts()) {
                    SubDistrict subDistrict = new SubDistrict();
                    subDistrict.setName(subDistrictDTO.getSubDistrict());
                    subDistrict.setDistrict(district);
                    district.getSubDistricts().add(subDistrict);
                }

                state.getDistricts().add(district);
            }

            stateRepository.save(state);
        }

        System.out.println("Data Loaded!");
    }

    private boolean isDataAlreadyLoaded() {
        return stateRepository.count() > 0;
    }
}
