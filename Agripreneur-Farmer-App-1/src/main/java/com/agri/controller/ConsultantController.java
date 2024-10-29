package com.agri.controller;

import java.util.List;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.agri.dto.AgripreneursByCategoryDto;
import com.agri.dto.AgripreneursByStateIdDto;
import com.agri.dto.AuthenticationResponse;
import com.agri.dto.FarmerByStateIdDto;
import com.agri.dto.OtpLoginRequest;
import com.agri.dto.PasswordSetRequest;
import com.agri.jwtFarmer.CustomUserDetailsService;
import com.agri.jwtFarmer.JwtUtil;
import com.agri.model.Consultant;
import com.agri.repository.ConsultantRepository;
import com.agri.response.MessageResponse;
import com.agri.service.ConsultantService;
import com.agri.service.OtpService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("/api/consultant")
@Validated
public class ConsultantController {

	@Autowired
	 private ConsultantService consultantService;
	
	@Autowired
	private PasswordEncoder passwordEncoder; // Inject the password encoder
	
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private  CustomUserDetailsService userDetailsService;

    @Autowired
    private  OtpService otpService;
    
    @Autowired
    private  ConsultantRepository consultantRepository;

    @Autowired
    private JwtUtil jwtUtil; 
    
    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(@RequestParam String mobileNumber) {
        // Step 1: Check if the mobile number is registered
        if (!consultantService.isMobileNumberRegistered(mobileNumber)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Mobile number not present");
        }

        // Step 2: Generate and send OTP
        otpService.generateOtp(mobileNumber);
        return ResponseEntity.ok("OTP sent to mobile number.");
    }

    // Endpoint to verify OTP
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestParam String mobileNumber, @RequestParam String otp) {
        if (otpService.validateOtp(mobileNumber, otp)) {
            otpService.clearOtp(mobileNumber); // Clear OTP after successful verification
            
            // Generate JWT token
            String token = jwtUtil.generateToken(mobileNumber);

            return ResponseEntity.ok(token);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid OTP");
        }
    }
    
	@GetMapping("/AgripreneurSelect-by-state-name/{stateName}")
    public ResponseEntity<List<AgripreneursByStateIdDto>> getAgripreneursByStateName(
            @PathVariable String stateName,
            @RequestHeader("Authorization") String token) {
        String jwtToken = token.substring(7);
        String email = jwtUtil.extractUsername(jwtToken);

        if (jwtUtil.validateToken(jwtToken, userDetailsService.loadUserByUsername(email))) {
            List<AgripreneursByStateIdDto> agripreneurs = consultantService.getAgripreneursByStateName(stateName);
            return ResponseEntity.ok(agripreneurs);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @GetMapping("/FarmerSelect-by-state-name/{stateName}")
    public ResponseEntity<List<FarmerByStateIdDto>> getFarmersByStateName(
            @PathVariable String stateName,
            @RequestHeader("Authorization") String token) {
        String jwtToken = token.substring(7);
        String email = jwtUtil.extractUsername(jwtToken);

        if (jwtUtil.validateToken(jwtToken, userDetailsService.loadUserByUsername(email))) {
            List<FarmerByStateIdDto> farmers = consultantService.getFarmersByStateName(stateName);
            return ResponseEntity.ok(farmers);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }


    @PostMapping("/verifyAgripreneur")
    public ResponseEntity<String> verifyAgripreneur(
        @RequestParam Long agripreneurId, 
        @RequestParam boolean approve, 
        @RequestParam(required = false) String comments,
        @RequestHeader("Authorization") String token) {

        // Step 1: Extract the JWT token from the "Authorization" header
        String jwtToken = token.substring(7); // Remove the "Bearer " prefix
        String email = jwtUtil.extractUsername(jwtToken); // Extract email/username from the token

        // Step 2: Validate the token
        if (jwtUtil.validateToken(jwtToken, userDetailsService.loadUserByUsername(email))) {
            
            // Step 3: Proceed with verification if the token is valid
            boolean isVerified = consultantService.verifyAgripreneur(agripreneurId, approve, comments);
            String message = approve ? "Agripreneur verified successfully." : "Agripreneur verification rejected.";
            
            return ResponseEntity.ok(message);
        } else {
            // Return unauthorized response if token validation fails
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }
    
}
}  
    /*
    
	@PostMapping("/login")
	public ResponseEntity<MessageResponse> login(@RequestParam String email, @RequestParam String password) {
	    // Step 1: Check if the email exists
	    Consultant consultant = consultantService.findConsultantByEmail(email);
	    
	    if (consultant == null) {
	        return ResponseEntity.badRequest().body(new MessageResponse("Invalid email: Consultant not found"));
	    }

	    // Step 2: Compare the entered password with the stored encrypted password
	    if (!passwordEncoder.matches(password, consultant.getPassword())) {
	        return ResponseEntity.badRequest().body(new MessageResponse("Invalid password"));
	    }

	    // Step 3: If successful, return a success message (or token for JWT-based authentication)
	    return ResponseEntity.ok(new MessageResponse("Login successful"));
	}

    @PostMapping("/verify")
    public ResponseEntity<String> verifyAgripreneur(
        @RequestParam Long agripreneurId, 
        @RequestParam boolean approve, 
        @RequestParam(required = false) String comments ) {
        boolean isVerified = consultantService.verifyAgripreneur(agripreneurId, approve, comments);
        String message = approve ? "Agripreneur verified successfully." : "Agripreneur verification rejected.";
        return ResponseEntity.ok(message);
    }


	@PostMapping("/setConsultantPassword")
	public ResponseEntity<MessageResponse> setConsultantPassword(@RequestParam String email, @RequestParam String password) {
	    // Fetch the consultant by email
	    Consultant consultant = consultantService.findConsultantByEmail(email);

	    if (consultant == null) {
	        return ResponseEntity.badRequest().body(new MessageResponse("Invalid email: Consultant not found"));
	    }

	    String encryptedPassword = passwordEncoder.encode(password);
	    consultant.setPassword(encryptedPassword);

	    return ResponseEntity.ok(new MessageResponse("Password successfully set for consultant"));
	}
	
	@PostMapping("/setConsultantPassword")
	public ResponseEntity<MessageResponse> setConsultantPassword(
	        @RequestParam String email, 
	        @RequestParam String password, 
	        @RequestParam String confirmPassword) {

	    // Step 1: Check if password and confirmPassword match
	    if (!password.equals(confirmPassword)) {
	        return ResponseEntity.badRequest().body(new MessageResponse("Passwords do not match"));
	    }

	    // Step 2: Fetch the consultant by email
	    Consultant consultant = consultantService.findConsultantByEmail(email);

	    if (consultant == null) {
	        return ResponseEntity.badRequest().body(new MessageResponse("Invalid email: Consultant not found"));
	    }

	    // Step 3: Encrypt the password
	    String encryptedPassword = passwordEncoder.encode(password);
	    consultant.setPassword(encryptedPassword);

	    // Step 4: Save the consultant with the encrypted password
	    consultantService.saveConsultant(consultant);

	    // Step 5: Return success message
	    return ResponseEntity.ok(new MessageResponse("Password successfully set for consultant"));
	}
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestParam String email, @RequestParam String password) {
	    // Step 1: Check if the email exists
	    Consultant consultant = consultantService.findConsultantByEmail(email);
	    if (consultant == null) {
	        return ResponseEntity.badRequest().body(new MessageResponse("Invalid email: Consultant not found"));
	    }
	    // Step 2: Compare the entered password with the stored encrypted password
	    if (!passwordEncoder.matches(password, consultant.getPassword())) {
	        return ResponseEntity.badRequest().body(new MessageResponse("Invalid password"));
	    }
	    // Step 3: If successful, generate a JWT token and return it
	    UserDetails userDetails = userDetailsService.loadUserByUsername(email);
	    String token = jwtUtil.generateToken(userDetails.getUsername());

	    return ResponseEntity.ok(new JwtResponse(token));
	}

@PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestParam String mobileNumber, @RequestParam String otp) {
        // Step 3: Verify OTP
        if (otpService.validateOtp(mobileNumber, otp)) {
            otpService.clearOtp(mobileNumber); // Clear OTP after successful verification
            return ResponseEntity.ok("Login successful");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid OTP");
        }
    }

	*/


