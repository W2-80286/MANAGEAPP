package com.agri.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.agri.dto.AdminCredentialsRequest;
import com.agri.dto.AdminLoginRequest;
import com.agri.dto.AgripreneurUpdateDto;
import com.agri.dto.AgripreneursByDistrictIdDto;
import com.agri.dto.AgripreneursByStateIdDto;
import com.agri.dto.AgripreneursBySubDistrictIdDto;
import com.agri.dto.ConsultantRequest;
import com.agri.dto.ConsultantUpdateDto;
import com.agri.dto.FarmerByDistrictIdDto;
import com.agri.dto.FarmerByStateIdDto;
import com.agri.dto.FarmerBySubDistrictDto;
import com.agri.dto.FeedbackResponseDto;
import com.agri.dto.consultantGetDto;
import com.agri.jwtFarmer.CustomUserDetailsService;
import com.agri.jwtFarmer.JwtUtil;
import com.agri.model.Admin;
import com.agri.model.Consultant;
import com.agri.model.FeedbackQuestion;
import com.agri.model.State;
import com.agri.repository.FeedbackQuestionRepository;
import com.agri.response.ApiResponse;
import com.agri.response.MessageResponse;
import com.agri.service.AdminService;
import com.agri.service.ConsultantService;
import com.agri.service.OtpService;
import com.agri.service.StateService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;




@RestController
@RequestMapping("/api/admin")
@Validated

public class AdminController{
	
	@Autowired
	private AdminService adminService;
	
	@Autowired
	private StateService stateService;
	
	@Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;
    
    

    @Autowired
    private  OtpService otpService;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    public AdminController(AuthenticationManager authenticationManager, 
            CustomUserDetailsService customUserDetailsService,
            JwtUtil jwtUtil) {
this.authenticationManager = authenticationManager;
this.customUserDetailsService = customUserDetailsService;
this.jwtUtil = jwtUtil;
}

  
    @PostMapping("/register")
    public ResponseEntity<String> registerAdmin(@RequestBody Admin admin) {
        try {
            adminService.registerAdmin(admin);
            return ResponseEntity.ok("Admin registered successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

   
      
    
    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(@RequestParam String mobileNumber) {
        // Step 1: Check if the mobile number is registered
        if (!adminService.isMobileNumberRegistered(mobileNumber)) {
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

    @PostMapping("/addFeedbackQuestion")
    public ResponseEntity<String> addFeedbackQuestion(@RequestBody FeedbackQuestion feedbackQuestion, @RequestHeader("Authorization") String token) {
        if (jwtUtil.validateToken(token.substring(7), customUserDetailsService.loadUserByUsername(jwtUtil.extractUsername(token.substring(7))))) {
            FeedbackQuestion addedFeedbackQuestion = adminService.addFeedbackQuestion(feedbackQuestion);
            String successMessage = "Feedback question added successfully with ID: " + addedFeedbackQuestion.getId();
            return ResponseEntity.status(HttpStatus.CREATED).body(successMessage);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }

    @PutMapping("/updateFeedbackQuestion/{id}")
    public ResponseEntity<String> updateFeedbackQuestion(@RequestHeader("Authorization") String token, @PathVariable Long id, @RequestBody FeedbackQuestion feedbackQuestion) {
        if (jwtUtil.validateToken(token.substring(7), customUserDetailsService.loadUserByUsername(jwtUtil.extractUsername(token.substring(7))))) {
            try {
                FeedbackQuestion updatedFeedbackQuestion = adminService.updateFeedbackQuestion(id, feedbackQuestion);
                String successMessage = "Feedback question with ID: " + id + " updated successfully.";
                return ResponseEntity.status(HttpStatus.OK).body(successMessage);
            } catch (RuntimeException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Feedback question with ID: " + id + " not found.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }

    @DeleteMapping("/deleteFeedbackQuestion/{id}")
    public ResponseEntity<String> deleteFeedbackQuestion(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        if (jwtUtil.validateToken(token.substring(7), customUserDetailsService.loadUserByUsername(jwtUtil.extractUsername(token.substring(7))))) {
            try {
                adminService.deleteFeedbackQuestion(id);
                String successMessage = "Feedback question with ID: " + id + " deleted successfully.";
                return ResponseEntity.status(HttpStatus.OK).body(successMessage);
            } catch (RuntimeException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Feedback question with ID: " + id + " not found.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }

    @GetMapping("/getAllFeedbackQuestions")
    public ResponseEntity<?> getAllFeedbackQuestions(@RequestHeader("Authorization") String token) {
        if (jwtUtil.validateToken(token.substring(7), customUserDetailsService.loadUserByUsername(jwtUtil.extractUsername(token.substring(7))))) {
            List<FeedbackQuestion> feedbackQuestions = adminService.getAllFeedbackQuestion();
            if (feedbackQuestions.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No feedback questions found.");
            }
            return ResponseEntity.ok(feedbackQuestions);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }
    
    @PostMapping("/addConsultant")
    public ResponseEntity<MessageResponse> createConsultant(@RequestBody ConsultantRequest request, @RequestHeader("Authorization") String token) {
        if (jwtUtil.validateToken(token.substring(7), customUserDetailsService.loadUserByUsername(jwtUtil.extractUsername(token.substring(7))))) {
            State state = stateService.getStateById(request.getStateId());
            if (state == null) {
                return ResponseEntity.badRequest().body(new MessageResponse("State not found"));
            }
            Consultant consultant = new Consultant();
            consultant.setFullname(request.getFullname());
            consultant.setEmail(request.getEmail());
            consultant.setMobileNumber(request.getMobileNumber());
            consultant.setState(state);
            Long availableId = adminService.findSmallestAvailableConsultantId();
            consultant.setConsultantId(availableId);
            adminService.saveConsultant(consultant);
            return ResponseEntity.ok(new MessageResponse("Consultant successfully created"));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Invalid token"));
        }
    }

    @GetMapping("/getAllConsultants")
    public ResponseEntity<ApiResponse<List<consultantGetDto>>> getAllConsultants(@RequestHeader("Authorization") String token) {
        String jwtToken = extractToken(token);  // Extract the token once
        
        if (isTokenValid(jwtToken)) {
            List<consultantGetDto> consultantResponses = adminService.getAllConsultantResponses();
            if (consultantResponses.isEmpty()) {
                return ResponseEntity.ok(new ApiResponse<>("No consultants found", new ArrayList<>()));
            }
            return ResponseEntity.ok(new ApiResponse<>("Consultants retrieved successfully", consultantResponses));
        } else {
            // Return an empty list for the data to maintain type consistency
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                 .body(new ApiResponse<>("Invalid token", new ArrayList<>()));
        }
    }

    @PutMapping("/updateConsultant/{id}")
    public ResponseEntity<ApiResponse<Consultant>> updateConsultant(@RequestHeader("Authorization") String token, @PathVariable Long id, @Valid @RequestBody ConsultantUpdateDto consultantUpdateRequest) {
        String jwtToken = extractToken(token);  // Extract the token once
        
        if (isTokenValid(jwtToken)) {
            try {
                Consultant updatedConsultant = adminService.updateConsultant(id, consultantUpdateRequest);
                return ResponseEntity.ok(new ApiResponse<>("Consultant updated successfully", updatedConsultant));
            } catch (RuntimeException e) {
                // Returning the correct response type with a null consultant when not found
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>("Consultant with ID " + id + " not found", null));
            }
        } else {
            // Returning the correct response type with a null consultant when token is invalid
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>("Invalid token", null));
        }
    }
    @DeleteMapping("/deleteConsultant/{id}")
    public ResponseEntity<ApiResponse<String>> deleteConsultant(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        String jwtToken = extractToken(token);  // Extract the token once
        
        if (isTokenValid(jwtToken)) {
            try {
                adminService.deleteConsultant(id);
                return ResponseEntity.ok(new ApiResponse<>("Consultant with ID " + id + " deleted successfully", null));
            } catch (RuntimeException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(null, "Consultant with ID " + id + " not found"));
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(null, "Invalid token"));
        }
    }
    
    @GetMapping("/agripreneurSelect-by-state-name/{stateName}")
    public ResponseEntity<ApiResponse<List<AgripreneursByStateIdDto>>> getAgripreneursByStateName(
            @RequestHeader("Authorization") String token, @PathVariable String stateName) {
        
        String jwtToken = extractToken(token);
        
        if (isTokenValid(jwtToken)) {
            List<AgripreneursByStateIdDto> agripreneurs = adminService.getAgripreneursByStateName(stateName);
            return ResponseEntity.ok(new ApiResponse<>("Agripreneurs retrieved successfully", agripreneurs));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>("Invalid token", null));
        }
    }

    @GetMapping("/agripreneurSelect-by-district-name/{districtName}")
    public ResponseEntity<ApiResponse<List<AgripreneursByDistrictIdDto>>> getAgripreneursByDistrictName(
            @RequestHeader("Authorization") String token, @PathVariable String districtName) {
        
        String jwtToken = extractToken(token);
        
        if (isTokenValid(jwtToken)) {
            List<AgripreneursByDistrictIdDto> agripreneurs = adminService.getAgripreneursByDistrictName(districtName);
            return ResponseEntity.ok(new ApiResponse<>("Agripreneurs retrieved successfully", agripreneurs));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>("Invalid token", null));
        }
    }

    @GetMapping("/agripreneurSelect-by-sub-district-name/{subDistrictName}")
    public ResponseEntity<ApiResponse<List<AgripreneursBySubDistrictIdDto>>> getAgripreneursBySubDistrictName(
            @RequestHeader("Authorization") String token, @PathVariable String subDistrictName) {
        
        String jwtToken = extractToken(token);
        
        if (isTokenValid(jwtToken)) {
            List<AgripreneursBySubDistrictIdDto> agripreneurs = adminService.getAgripreneursBySubDistrictName(subDistrictName);
            return ResponseEntity.ok(new ApiResponse<>("Agripreneurs retrieved successfully", agripreneurs));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>("Invalid token", null));
        }
    }

    @GetMapping("/farmerselect-by-district-name/{districtName}")
    public ResponseEntity<ApiResponse<List<FarmerByDistrictIdDto>>> getFarmersByDistrictName(
            @RequestHeader("Authorization") String token, @PathVariable String districtName) {
        
        String jwtToken = extractToken(token);
        
        if (isTokenValid(jwtToken)) {
            List<FarmerByDistrictIdDto> farmers = adminService.getFarmersByDistrictName(districtName);
            return ResponseEntity.ok(new ApiResponse<>("Farmers retrieved successfully", farmers));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>("Invalid token", null));
        }
    }

    @GetMapping("/farmerselect-by-sub-district-name/{SubDistrictName}")
    public ResponseEntity<ApiResponse<List<FarmerBySubDistrictDto>>> getFarmersBySubDistrictName(
            @RequestHeader("Authorization") String token, @PathVariable String SubDistrictName) {
        
        String jwtToken = extractToken(token);
        
        if (isTokenValid(jwtToken)) {
            List<FarmerBySubDistrictDto> farmers = adminService.getFarmersBySubDistrictName(SubDistrictName);
            return ResponseEntity.ok(new ApiResponse<>("Farmers retrieved successfully", farmers));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>("Invalid token", null));
        }
    }

    @GetMapping("/farmerselect-by-state-name/{StateName}")
    public ResponseEntity<ApiResponse<List<FarmerByStateIdDto>>> getFarmersByStateName(
            @RequestHeader("Authorization") String token, @PathVariable String StateName) {
        
        String jwtToken = extractToken(token);
        
        if (isTokenValid(jwtToken)) {
            List<FarmerByStateIdDto> farmers = adminService.getFarmersByStateName(StateName);
            return ResponseEntity.ok(new ApiResponse<>("Farmers retrieved successfully", farmers));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>("Invalid token", null));
        }
    }
   
    @GetMapping("/ViewFeedbackResponse")
    public ResponseEntity<ApiResponse<List<FeedbackResponseDto>>> getAllFeedbackResponses(
            @RequestHeader("Authorization") String token) {
        
        String jwtToken = extractToken(token);
        
        if (isTokenValid(jwtToken)) {
            List<FeedbackResponseDto> responses = adminService.getAllFeedbackResponses();
            return ResponseEntity.ok(new ApiResponse<>("Feedback responses retrieved successfully", responses));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>("Invalid token", null));
        }
    }

    @PutMapping(value = "/updateAgripreneurProfile/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<String>> updateAgripreneurProfile(
            @RequestHeader("Authorization") String token,
            @PathVariable(value = "id") Long id,
            @ModelAttribute AgripreneurUpdateDto agripreneurUpdateDto,  // Use @ModelAttribute instead of @RequestBody
            @RequestParam(value = "images", required = false) List<MultipartFile> images) throws IOException {

        String jwtToken = extractToken(token);

        if (isTokenValid(jwtToken)) {
            if (images != null && !images.isEmpty()) {
                agripreneurUpdateDto.setImages(images);  // Set the images in DTO if available
            }
            adminService.updateAgripreneur(id, agripreneurUpdateDto);  // Update agripreneur details
            return ResponseEntity.ok(new ApiResponse<>("Updated successfully", null));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>("Invalid token", null));
        }
    }

    
    // Helper method to extract the token from the Authorization header
    private String extractToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return null;
    }

    // Helper method to validate the token
    private boolean isTokenValid(String token) {
        if (token == null) {
            return false;
        }
        try {
            String username = jwtUtil.extractUsername(token);
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
            return jwtUtil.validateToken(token, userDetails);
        } catch (Exception e) {
            return false;
        }
    }
    
    
    
		

		    
		    @PostMapping("/approveAgripreneur")
		    public ResponseEntity<String> approveOrRejectAgripreneur(
		            @RequestParam Long agripreneurId, 
		            @RequestParam boolean approve, 
		            @RequestHeader("Authorization") String token) {

		        // Step 1: Extract JWT token (remove "Bearer " prefix)
		        String jwtToken = token.substring(7);
		        
		        // Step 2: Validate the JWT token
		        if (!jwtUtil.validateToken(jwtToken, customUserDetailsService.loadUserByUsername(jwtUtil.extractUsername(jwtToken)))) {
		            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
		        }

		        // Step 3: If valid, proceed to approve or reject the Agripreneur
		        boolean result = adminService.approveOrRejectAgripreneur(agripreneurId, approve);
		        String message = approve ? "Agripreneur approved successfully." : "Agripreneur approval rejected.";

		        return ResponseEntity.ok(message);
		    }
	}
/*

@PostMapping("/login")
public ResponseEntity<?> loginAdmin(@RequestBody AdminLoginRequest loginRequest) {

    // Find the admin by username (email, mobile number, or full name)
    Admin admin = adminService.findByUsername(loginRequest.getUsername());

    // If no admin is found, return an error response
    if (admin == null) {
        return ResponseEntity.badRequest().body(new MessageResponse("Invalid username or password"));
    }

    // Check if the password matches
    if (!passwordEncoder.matches(loginRequest.getPassword(), admin.getPassword())) {
        return ResponseEntity.badRequest().body(new MessageResponse("Invalid username or password"));
    }

    // If login is successful, return a success message
    return ResponseEntity.ok(new MessageResponse("Login successful"));
}
 @PostMapping("/addFeedbackQuestion")
    public ResponseEntity<String> addFeedbackQuestion(@RequestBody FeedbackQuestion feedbackQuestion) {
        FeedbackQuestion addedFeedbackQuestion = adminService.addFeedbackQuestion(feedbackQuestion);
        String successMessage = "Feedback question added successfully with ID: " + addedFeedbackQuestion.getId();
        return ResponseEntity.status(HttpStatus.CREATED).body(successMessage);
    }
    
      @PutMapping("/updateFeedbackQuestion/{id}")
    public ResponseEntity<String> updateFeedbackQuestion(@PathVariable Long id, @RequestBody FeedbackQuestion feedbackQuestion) {
        try {
            FeedbackQuestion updatedFeedbackQuestion = adminService.updateFeedbackQuestion(id, feedbackQuestion);
            String successMessage = "Feedback question with ID: " + id + " updated successfully.";
            return ResponseEntity.status(HttpStatus.OK).body(successMessage);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Feedback question with ID: " + id + " not found.");
        }
    }

    @DeleteMapping("/deleteFeedbackQuestion/{id}")
    public ResponseEntity<String> deleteFeedbackQuestion(@PathVariable Long id) {
        try {
            adminService.deleteFeedbackQuestion(id);
            String successMessage = "Feedback question with ID: " + id + " deleted successfully.";
            return ResponseEntity.status(HttpStatus.OK).body(successMessage);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Feedback question with ID: " + id + " not found.");
        }
    }
    
    
    @GetMapping("/getAllFeedbackQuestions")
    public ResponseEntity<?> getAllFeedbackQuestions() {
        List<FeedbackQuestion> feedbackQuestions = adminService.getAllFeedbackQuestion();
        if (feedbackQuestions.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No feedback questions found.");
        }
        return ResponseEntity.ok(feedbackQuestions);
    }
  @PostMapping("/addConsultant")
	    public ResponseEntity<MessageResponse> createConsultant(@RequestBody ConsultantRequest request) {
	        // Fetch the state by ID
	        State state = stateService.getStateById(request.getStateId());

	        if (state == null) {
	            return ResponseEntity.badRequest().body(new MessageResponse("State not found"));
	        }

	        Consultant consultant = new Consultant();
	        consultant.setFullname(request.getFullname());
	        consultant.setEmail(request.getEmail());
	        consultant.setMobileNumber(request.getMobileNumber());
	        consultant.setState(state);

	        // Find the smallest available ID and assign it
	        Long availableId = adminService.findSmallestAvailableConsultantId();
	        consultant.setConsultantId(availableId);

	        adminService.saveConsultant(consultant);

	        // Return success message without data
	        return ResponseEntity.ok(new MessageResponse("Consultant successfully created"));
	    }

	    
	    @GetMapping("/getAllConsultants")
	    public ResponseEntity<ApiResponse<List<consultantGetDto>>> getAllConsultants() {
	        List<consultantGetDto> consultantResponses = adminService.getAllConsultantResponses();
	        if (consultantResponses.isEmpty()) {
	            return ResponseEntity.ok(new ApiResponse<>("No consultants found", null));
	        }
	        return ResponseEntity.ok(new ApiResponse<>("Consultants retrieved successfully", consultantResponses));
	    }
	    
	    
		    @PutMapping("/updateConsultant/{id}")
	    public ResponseEntity<ApiResponse<Consultant>> updateConsultant(
	            @PathVariable Long id,
	            @Valid @RequestBody ConsultantUpdateDto consultantUpdateRequest) {
	        
	        try {
	            Consultant updatedConsultant = adminService.updateConsultant(id, consultantUpdateRequest);
	            return ResponseEntity.ok(new ApiResponse<>("Consultant updated successfully", updatedConsultant));
	        } catch (RuntimeException e) {
	            return ResponseEntity.status(404).body(new ApiResponse<>("Consultant not found", null));
	        }

	    }
		    
		    
		    @DeleteMapping("/deleteConsultant/{id}")
		    public ResponseEntity<MessageResponse> deleteConsultant(@PathVariable Long id) {
		        try {
		            adminService.deleteConsultant(id);
		            return ResponseEntity.ok(new MessageResponse("Consultant with ID " + id + " deleted successfully"));
		        } catch (RuntimeException e) {
		            return ResponseEntity.status(404).body(new MessageResponse("Consultant with ID " + id + " not found"));
		        }
		    }

		    @GetMapping("/select-by-state-name/{stateName}")
		    public List<AgripreneursByStateIdDto> getAgripreneursByStateName(@PathVariable String stateName) {
		        return adminService.getAgripreneursByStateName(stateName);
		    }
		    
		    @GetMapping("/select-by-district-name/{districtName}")
		    public List<AgripreneursByDistrictIdDto> getAgripreneursByDistrictName(@PathVariable String districtName) {
		        return adminService.getAgripreneursByDistrictName(districtName);
		    }
		   
		    @GetMapping("/select-by-sub-district-name/{subDistrictName}")
		    public List<AgripreneursBySubDistrictIdDto> getAgripreneursBySubDistrictName(@PathVariable String subDistrictName) {
		        return adminService.getAgripreneursBySubDistrictName(subDistrictName);
		    }
		    
		      @GetMapping("/Farmerselect-by-district-name/{districtName}")
		    public List<FarmerByDistrictIdDto> getFarmersByDistrictName(@PathVariable String districtName) {
		        return adminService.getFarmersByDistrictName(districtName);
		    }

		    
		    @GetMapping("/Farmerselect-by-SubdDistrict-name/{SubDistrictName}")
		    public List<FarmerBySubDistrictDto> getFarmersBySubDistrictName(@PathVariable String SubDistrictName) {
		        return adminService.getFarmersBySubDistrictName(SubDistrictName);
		    }

		    
		    @GetMapping("/Farmerselect-by-State-name/{StateName}")
		    public List<FarmerByStateIdDto> getFarmersByStateName(@PathVariable String StateName) {
		        return adminService.getFarmersByStateName(StateName);
		    }

              @GetMapping("/ViewFeedbackRespone")
		    public ResponseEntity<List<FeedbackResponseDto>> getAllFeedbackResponses() {
		        List<FeedbackResponseDto> responses = adminService.getAllFeedbackResponses();
		        return ResponseEntity.ok(responses);
		    }
               @PutMapping("/updateAgripreneurProfile/{id}")
		    public ResponseEntity<String> updateAgripreneur(
		            @PathVariable(value = "id") Long id,
		            @Valid AgripreneurUpdateDto agripreneurUpdateDto,
		            @RequestParam(value = "images", required = false) List<MultipartFile> images) throws IOException {
		        if (images != null) {
		            agripreneurUpdateDto.setImages(images);
		        }
		        adminService.updateAgripreneur(id, agripreneurUpdateDto);
		        return ResponseEntity.ok("Updated successfully");
		    }


    @PutMapping("/updateAgripreneurProfile/{id}")
    public ResponseEntity<ApiResponse<String>> updateAgripreneurProfile(
            @RequestHeader("Authorization") String token,
            @PathVariable(value = "id") Long id,
            @Valid @RequestBody AgripreneurUpdateDto agripreneurUpdateDto,
            @RequestParam(value = "images", required = false) List<MultipartFile> images) throws IOException {

        String jwtToken = extractToken(token);
        
        if (isTokenValid(jwtToken)) {
            if (images != null) {
                agripreneurUpdateDto.setImages(images);
            }
            adminService.updateAgripreneur(id, agripreneurUpdateDto);
            return ResponseEntity.ok(new ApiResponse<>("Updated successfully", null));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>("Invalid token", null));
        }
    }

        @PostMapping("/setCredentials")
		    public ResponseEntity<MessageResponse> setAdminCredentials(@RequestBody AdminCredentialsRequest request) {

		        // Check if the email or mobile number already exists
		        if (adminService.existsByEmailOrMobileNumber(request.getEmail(), request.getMobileNumber())) {
		            return ResponseEntity.badRequest().body(new MessageResponse("Email or mobile number already in use"));
		        }

		        // Check if password and confirm password match
		        if (!request.getPassword().equals(request.getConfirmPassword())) {
		            return ResponseEntity.badRequest().body(new MessageResponse("Passwords do not match"));
		        }

		        // Encrypt the password
		        String encryptedPassword = passwordEncoder.encode(request.getPassword());

		        // Create a new Admin entity
		        Admin admin = new Admin();
		        admin.setFullName(request.getFullName());
		        admin.setEmail(request.getEmail());
		        admin.setMobileNumber(request.getMobileNumber());
		        admin.setPassword(encryptedPassword); // Store the encrypted password

		        // Save the admin in the database
		        adminService.saveAdmin(admin);

		        return ResponseEntity.ok(new MessageResponse("Admin credentials set successfully"));
		    }
		    
		      @PostMapping("/login")
   public ResponseEntity<?> loginAdmin(@RequestBody AdminLoginRequest loginRequest) {
       try {
           authenticationManager.authenticate(
                   new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
           );
       } catch (BadCredentialsException e) {
           return ResponseEntity.badRequest().body(new MessageResponse("Invalid username or password"));
       }

       final UserDetails userDetails = customUserDetailsService.loadUserByUsername(loginRequest.getUsername());
       final String jwt = jwtUtil.generateToken(userDetails.getUsername());

       return ResponseEntity.ok(new JwtResponse(jwt));
   }

	*/	

