package com.agri.jwtFarmer;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.agri.model.Admin;
import com.agri.model.Agripreneur;
import com.agri.model.Consultant;
import com.agri.model.Farmer;
import com.agri.repository.AdminRepository;
import com.agri.repository.AgripreneurRepository;
import com.agri.repository.ConsultantRepository;
import com.agri.repository.FarmerRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final FarmerRepository farmerRepository;
    private final AgripreneurRepository agripreneurRepository;
    private final AdminRepository adminRepository;
    private final ConsultantRepository consultantRepository;

    public CustomUserDetailsService(FarmerRepository farmerRepository, 
                                    AgripreneurRepository agripreneurRepository,
                                    AdminRepository adminRepository, 
                                    ConsultantRepository consultantRepository) {
        this.farmerRepository = farmerRepository;
        this.agripreneurRepository = agripreneurRepository;
        this.adminRepository = adminRepository;
        this.consultantRepository = consultantRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        // 1. Check if the user is a Consultant (by mobile number only)
        Optional<Consultant> optionalConsultant = consultantRepository.findByMobileNumber(identifier);
        if (optionalConsultant.isPresent()) {
            Consultant consultant = optionalConsultant.get();
            return createUserDetails(consultant.getMobileNumber(), "");
        }

        // 2. Check if the user is an Admin ( mobile number, )
        Optional<Admin> optionalAdmin = adminRepository.findByMobileNumber(identifier);
        if (optionalAdmin.isPresent()) {
            Admin admin = optionalAdmin.get();
            return createUserDetails(admin.getMobileNumber(), "");
        }

        // 3. Check if the user is a Farmer (by mobile number)
        Optional<Farmer> optionalFarmer = farmerRepository.findByMobileNumber(identifier);
        if (optionalFarmer.isPresent()) {
            Farmer farmer = optionalFarmer.get();
            return createUserDetails(farmer.getMobileNumber(), "");
        }

        // 4. Check if the user is an Agripreneur (by mobile number)
        Optional<Agripreneur> optionalAgripreneur = agripreneurRepository.findByMobileNumber(identifier);
        if (optionalAgripreneur.isPresent()) {
            Agripreneur agripreneur = optionalAgripreneur.get();
            return createUserDetails(agripreneur.getMobileNumber(), "");
        }

        // If no user is found, throw an exception
        throw new UsernameNotFoundException("User not found with provided identifier: " + identifier);
    }

    private UserDetails createUserDetails(String username, String password) {
        return new org.springframework.security.core.userdetails.User(
            username, 
            password,  // Can be empty for OTP-based login
            new ArrayList<>() // Add roles if needed
        );
    }
}
