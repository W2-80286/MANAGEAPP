package com.agri.jwtAgri;
/*
import java.util.ArrayList;
import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.agri.model.Agripreneur;
import com.agri.repository.AgripreneurRepository;

@Service
public class CustomUserDetailsServiceAgri implements UserDetailsService {

    private final AgripreneurRepository agripreneurRepository;

    public CustomUserDetailsServiceAgri(AgripreneurRepository agripreneurRepository) {
        this.agripreneurRepository = agripreneurRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String mobileNumber) throws UsernameNotFoundException {
        Optional<Agripreneur> optionalAgripreneur = agripreneurRepository.findByMobileNumber(mobileNumber);

        if (optionalAgripreneur.isPresent()) {
            Agripreneur agripreneur = optionalAgripreneur.get();
            return new org.springframework.security.core.userdetails.User(
                agripreneur.getMobileNumber(), 
                "", 
                new ArrayList<>()
            );
        } else {
            throw new UsernameNotFoundException("Agripreneur not found with mobile number: " + mobileNumber);
        }
    }
}
*/