package com.agri.config;
/*
import com.agri.model.AuthenticationRequest;
import com.agri.model.User;
import com.agri.model.UserRole;
import com.agri.repository.UserRepository;
import com.agri.repository.UserRoleRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthenticationController {
	
	@Autowired
	private UserRepository userRepository;
	 
	@Autowired
	private UserRoleRepository userRoleRepository;
	 
	

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private MyUserDetailService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        try {
            // Register the user
            userDetailsService.registerUser(user);
            
            // Return success message
            return ResponseEntity.ok("User registered successfully!");
        } catch (Exception e) {
            // Catch any exceptions and return a bad request response
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User registration failed: " + e.getMessage());
        }
    }


    /*
    
    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
            );
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }
    */
/*
    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
            );
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        String jwt = jwtUtil.generateToken(userDetails);

        User user = userRepository.findByUserName(authenticationRequest.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + authenticationRequest.getUsername()));

        List<UserRole> userRoles = userRoleRepository.findByUserId(user.getId())
                .orElseThrow(() -> new UsernameNotFoundException("User roles not found for user: " + user.getUserName()));

        String userRole = userRoles.get(0).getRole();
        String message = "Successfully logged in as " + userRole + ": " + user.getUserName();

        return ResponseEntity.ok(new AuthenticationResponse(jwt, user.getUserName(), userRole, message));
    }
}
*/    
    
