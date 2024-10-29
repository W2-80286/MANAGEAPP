package com.agri.config;
/*
import com.agri.model.User;
import com.agri.model.UserRole;
import com.agri.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MyUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public MyUserDetailService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(userName)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userName));

        return new MyUserDetails(user);
    }
/*
    public void registerUser(User user) {
        // Encode the password before saving the user
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Associate roles with the user (this step is important if roles are linked via a foreign key)
        for (UserRole role : user.getRoles()) {
            role.setUser(user);  // Set the user reference in each role
        }
        
        // Save the user along with the roles
        userRepository.save(user);
    }
*//*
    public void registerUser(User user) {
        // Encode the password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Assign the user to each role
        for (UserRole role : user.getRoles()) {
            role.setUser(user); // This automatically associates the user with the role
        }

        // Save the user along with the roles
        userRepository.save(user);
    }

   }
*/