package com.agri.locationData;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.agri.service.LocationService;

@Configuration
public class DataLoader {

    @Autowired
    private LocationService locationService;

    @Bean
    public ApplicationRunner loadData() {
        return args -> {
            locationService.loadLocationsFromFile("/home/cdachyd/AgripreneurMobileAppDoc/filtered_Location.json");
        };
    }
}
