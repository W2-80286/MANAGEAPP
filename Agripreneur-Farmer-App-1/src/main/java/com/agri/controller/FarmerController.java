package com.agri.controller;

import com.agri.dto.AgripreneurGetDto;
import com.agri.dto.AgripreneurMapDto;
import com.agri.dto.AgripreneursByCategoryDto;
import com.agri.dto.AgripreneursByDistrictIdDto;
import com.agri.dto.AgripreneursByKeywordDto;
import com.agri.dto.AgripreneursByStateIdDto;
import com.agri.dto.AgripreneursBySubDistrictIdDto;
import com.agri.dto.FarmerRegistrationDto;
import com.agri.dto.FarmerServiceSelectionDto;
import com.agri.dto.FeedbackResponseDto;
import com.agri.dto.MobileNumberDto;
import com.agri.dto.OtpVerificationDto;
import com.agri.dto.QueryDto;
//import com.agri.dto.QueryDto;
import com.agri.dto.ReviewDto;
import com.agri.exception.ResourceNotFoundException;
import com.agri.jwtFarmer.JwtUtil;
import com.agri.model.Agripreneur;
import com.agri.model.District;
import com.agri.model.Farmer;
import com.agri.model.FarmerQuery;
import com.agri.model.FeedbackResponse;
import com.agri.model.Review;
import com.agri.model.State;
import com.agri.model.SubDistrict;
import com.agri.repository.DistrictRepository;
import com.agri.repository.StateRepository;
import com.agri.repository.SubDistrictRepository;
import com.agri.service.AgripreneurService;
import com.agri.service.FarmerService;
import com.agri.service.OtpService;

import ch.qos.logback.classic.spi.STEUtil;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



@RestController
@RequestMapping("/api/farmers")
@Validated
public class FarmerController {

  
	 	@Autowired
	    private  OtpService otpService;

	    @Autowired
	    private StateRepository stateRepository;

	    @Autowired
	    private DistrictRepository districtRepository;

	    @Autowired
	    private SubDistrictRepository subDistrictRepository;


    @Autowired
    private AgripreneurService agripreneurService;
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    private final FarmerService farmerService;
    private final JwtUtil jwtUtil;

    public FarmerController(FarmerService farmerService, JwtUtil jwtUtil) {
        this.farmerService = farmerService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerFarmer(@Valid @RequestBody FarmerRegistrationDto farmerDTO) {
        return ResponseEntity.ok(farmerService.registerFarmer(farmerDTO));
    }

    @PostMapping("/verify-registration-otp")
    public ResponseEntity<String> verifyRegistrationOtp(
            @RequestParam String mobileNumber, 
            @RequestParam String otp) {
        String token = farmerService.verifyRegistrationOtp(mobileNumber, otp);
        if ("Invalid OTP.".equals(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(token);
        } else {
            return ResponseEntity.ok(token);  // Return the JWT token
        }
    }
    @PostMapping("/login")
    public ResponseEntity<String> loginFarmer(@RequestParam String mobileNumber) {
        return ResponseEntity.ok(farmerService.loginFarmer(mobileNumber));
    }

    @PostMapping("/verify-login-otp")
    public ResponseEntity<String> verifyLoginOtp(
            @RequestParam String mobileNumber, 
            @RequestParam String otp) {
        String token = farmerService.verifyLoginOtp(mobileNumber, otp);
        if ("Invalid OTP.".equals(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(token);
        } else {
            return ResponseEntity.ok(token);
        }
    }
    
    
    private boolean isTokenValid(String token) {
        String jwtToken = token.substring(7); // Remove "Bearer " prefix
        String mobileNumber = jwtUtil.extractUsername(jwtToken);
        return jwtUtil.validateToken(jwtToken, userDetailsService.loadUserByUsername(mobileNumber));
    }

    @GetMapping("/select-by-category")
    public ResponseEntity<List<AgripreneursByCategoryDto>> getAgripreneursByCategory(
            @RequestParam String categoryName, 
            @RequestHeader("Authorization") String token) {

        if (isTokenValid(token)) {
            List<AgripreneursByCategoryDto> agripreneurs = farmerService.getAgripreneursByCategory(categoryName);
            
            
            return ResponseEntity.ok(agripreneurs);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @GetMapping("/select-by-state-name/{stateName}")
    public ResponseEntity<List<AgripreneursByStateIdDto>> getAgripreneursByStateName(
            @PathVariable String stateName, 
            @RequestHeader("Authorization") String token) {

        if (isTokenValid(token)) {
            List<AgripreneursByStateIdDto> agripreneurs = farmerService.getAgripreneursByStateName(stateName);
            return ResponseEntity.ok(agripreneurs);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @GetMapping("/select-by-district-name/{districtName}")
    public ResponseEntity<List<AgripreneursByDistrictIdDto>> getAgripreneursByDistrictName(
            @PathVariable String districtName, 
            @RequestHeader("Authorization") String token) {

        if (isTokenValid(token)) {
            List<AgripreneursByDistrictIdDto> agripreneurs = farmerService.getAgripreneursByDistrictName(districtName);
            return ResponseEntity.ok(agripreneurs);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @GetMapping("/select-by-sub-district-name/{subDistrictName}")
    public ResponseEntity<List<AgripreneursBySubDistrictIdDto>> getAgripreneursBySubDistrictName(
            @PathVariable String subDistrictName, 
            @RequestHeader("Authorization") String token) {

        if (isTokenValid(token)) {
            List<AgripreneursBySubDistrictIdDto> agripreneurs = farmerService.getAgripreneursBySubDistrictName(subDistrictName);
            return ResponseEntity.ok(agripreneurs);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
    
    @GetMapping("/select-by-keyword/{keywords}")
    public ResponseEntity<List<AgripreneursByKeywordDto>> getAgripreneursByKeyword(
            @PathVariable String keywords, 
            @RequestHeader("Authorization") String token) {

        if (isTokenValid(token)) {
            List<AgripreneursByKeywordDto> agripreneurs = farmerService.getAgripreneursByKeyword(keywords);
            return ResponseEntity.ok(agripreneurs);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

      
    @GetMapping("/search-map")
    public ResponseEntity<List<AgripreneurMapDto>> searchAgripreneurs(
            @RequestParam double latitude, 
            @RequestParam double longitude, 
            @RequestParam double radius, 
            @RequestHeader("Authorization") String token) {

        // Extract the JWT token by removing the "Bearer " prefix
        String jwtToken = token.substring(7);
        String mobileNumber = jwtUtil.extractUsername(jwtToken);

        // Validate the JWT token
        if (jwtUtil.validateToken(jwtToken, userDetailsService.loadUserByUsername(mobileNumber))) {
            // Perform the search if the token is valid
            List<AgripreneurMapDto> agripreneurs = farmerService.getAgripreneursWithinRadius(latitude, longitude, radius);
            return ResponseEntity.ok(agripreneurs);
        } else {
            // Return an Unauthorized response if the token is invalid
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
    
    
    @PostMapping("/select-services")
    public ResponseEntity<Map<String, Object>> selectServices(
            @RequestBody FarmerServiceSelectionDto farmerServiceSelectionDto, 
            @RequestHeader("Authorization") String token) {

        if (isTokenValid(token)) {
            String jwtToken = token.substring(7); // Remove "Bearer " prefix
            String mobileNumber = jwtUtil.extractUsername(jwtToken);

            Map<String, Object> response = new HashMap<>();
            try {
                Farmer farmer = farmerService.selectFarmerCategoriesByMobileNumber(mobileNumber, farmerServiceSelectionDto);
                response.put("status", "success");
                response.put("message", "Service selected successfully for farmer with Id: " + farmer.getFarmerId());
            } catch (IllegalAccessException e) {
                response.put("status", "error");
                response.put("message", e.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    
    

    @PostMapping("/review")
    public ResponseEntity<String> submitReview(
            @RequestBody ReviewDto reviewDto, 
            @RequestHeader("Authorization") String token) {

        // Extract the JWT token
        String jwtToken = token.substring(7);
        String mobileNumber = jwtUtil.extractUsername(jwtToken);

        // Validate the token and submit the review
        if (jwtUtil.validateToken(jwtToken, userDetailsService.loadUserByUsername(mobileNumber))) {
            farmerService.submitReview(mobileNumber, reviewDto);
            return ResponseEntity.ok("Review and rating submitted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
    
    
    @PostMapping("/submit-query")
    public ResponseEntity<Map<String, Object>> submitQuery(
            @ModelAttribute QueryDto queryDto, 
            @RequestHeader("Authorization") String token) {

        String jwtToken = token.substring(7);
        String mobileNumber = jwtUtil.extractUsername(jwtToken);

        // Validate token
        if (jwtUtil.validateToken(jwtToken, userDetailsService.loadUserByUsername(mobileNumber))) {
            try {
                FarmerQuery savedQuery = farmerService.submitQuery(queryDto, mobileNumber);  // Pass the mobile number from token
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Query submitted successfully");
                response.put("query", savedQuery);
                return new ResponseEntity<>(response, HttpStatus.CREATED);
            } catch (IOException e) {
                e.printStackTrace();
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @PostMapping("/submit-feedback")
    public ResponseEntity<String> submitFeedback(
            @RequestParam Long questionId,
            @RequestParam String response,
            @RequestHeader("Authorization") String token) {

        // Extract JWT token from the Authorization header
        String jwtToken = token.substring(7);
        String mobileNumber = jwtUtil.extractUsername(jwtToken);

        // Validate the JWT token
        if (jwtUtil.validateToken(jwtToken, userDetailsService.loadUserByUsername(mobileNumber))) {
            // Submit feedback
            String result = farmerService.submitFeedback(mobileNumber, questionId, response);
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }

    
    @GetMapping("/viewProfile/{id}")
    public ResponseEntity<?> viewProfileAgripreneur(
            @PathVariable Long id, 
            @RequestHeader("Authorization") String token) {

        // Validate the JWT token
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or missing token.");
        }

        // Fetch and return the Agripreneur profile if the token is valid
        AgripreneurGetDto agripreneurDto = farmerService.getProfile(id, token);
        return ResponseEntity.ok(agripreneurDto);
    }



      
  }   
           /*
    
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerFarmer(@Valid @RequestBody FarmerRegistrationDto registrationDto) throws IOException {
        Farmer savedFarmer = farmerService.saveFarmer(registrationDto);
        String smsMessage = generateSmsMessage(savedFarmer);

        Map<String, Object> response = new HashMap<>();
        response.put("farmerId", savedFarmer.getFarmerId());
        response.put("smsMessage", smsMessage);
       // response.put("message", "Registration successful.");

        return ResponseEntity.ok(response);
    }

    private String generateSmsMessage(Farmer farmer) {
        return "Registration successful. Your ID is: " + farmer.getFarmerId();
    }
     @PostMapping("/authenticate")
    public ResponseEntity<Map<String, Object>> authenticateMobileNumber(@Valid @RequestBody MobileNumberDto mobileNumberDto) {
        farmerService.sendOtp(mobileNumberDto.getMobileNumber());
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "OTP sent successfully");
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyOtp(@Valid @RequestBody OtpVerificationDto otpVerificationDto) {
        boolean isVerified = farmerService.verifyOtp(otpVerificationDto.getMobileNumber(), otpVerificationDto.getOtp());
        
        Map<String, Object> response = new HashMap<>();
        if (isVerified) {
            response.put("status", "success");
            response.put("message", "OTP verified successfully.");
        } else {
            response.put("status", "error");
            response.put("message", "Invalid OTP.");
        }
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody OtpVerificationDto otpVerificationDto) {
        String token = farmerService.login(otpVerificationDto.getMobileNumber(), otpVerificationDto.getOtp());
        
        Map<String, Object> response = new HashMap<>();
        if (token != null) {
            response.put("status", "success");
            response.put("message", "Login successful");
            response.put("token", token);
        } else {
            response.put("status", "error");
            response.put("message", "Invalid OTP.");
        }
        
        return ResponseEntity.ok(response);
    }

    

*/
  /*  
    @PostMapping("select-services")
    public ResponseEntity<String> selectFarmerCategories(
            @Valid @RequestBody FarmerServiceSelectionDto farmerServiceSelectionDto) {
        try {
            Farmer updatedFarmer = farmerService.selectFarmerCategories(farmerServiceSelectionDto);
            return ResponseEntity.ok("Categories selected successfully for farmer ID: " + updatedFarmer.getFarmerId());
        } catch (IllegalAccessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    
       
    @GetMapping("/select-by-keyword/{keywords}")
    public List<AgripreneursByKeywordDto> getAgripreneursByKeyword(@PathVariable String keywords) {
        return farmerService.getAgripreneursByKeyword(keywords);
    }
    
    
    @PostMapping("/select-services")
    public ResponseEntity<Map<String, Object>> selectServices(@RequestBody FarmerServiceSelectionDto farmerServiceSelectionDto) {
        //TODO: process POST request
        Map<String, Object> response=new HashMap<>();
       try { Farmer farmer=farmerService.selectFarmerCategories(farmerServiceSelectionDto);
            response.put("status", "success");
            response.put("message","service selected successfully for farmer with Id:"+farmer.getFarmerId());
       }
       catch (IllegalAccessException e) {
    	   response.put("status", "error");
    	   response.put("message", e.getMessage());
    	   return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}
        return ResponseEntity.ok(response);
    }
    
    
    @PostMapping("/review")
    public ResponseEntity<String> submitReview(@RequestBody ReviewDto reviewDto) {
        farmerService.submitReview(reviewDto);
        return ResponseEntity.ok("Review and rating submitted successfully.");
    }  
    
    @PostMapping("/query")
    
    public ResponseEntity<Map<String, Object>> submitQuery(@ModelAttribute QueryDto queryDto) {
        try {
            // Log the incoming queryDto for debugging
            System.out.println("Received QueryDto: " + queryDto);

            FarmerQuery savedQuery = farmerService.submitQuery(queryDto);

            // Log the saved query for debugging
            System.out.println("Saved Query: " + savedQuery);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Query submitted successfully");
            response.put("query", savedQuery);

            // Log the response before returning it
            System.out.println("Response: " + response);

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IOException e) {
            // Log the exception for debugging
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IllegalArgumentException e) {
            // Log the exception for debugging
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/search-map")
    public List<AgripreneurMapDto> searchAgripreneurs(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam double radius) {
        return farmerService.getAgripreneursWithinRadius(latitude, longitude, radius);
    }
     
   
    @PostMapping("/submit")
    public ResponseEntity<String> submitFeedback(
            @RequestParam Long farmerId,
            @RequestParam Long questionId,
            @RequestParam String response) {

        String result = farmerService.submitFeedback(farmerId, questionId, response);
        return ResponseEntity.ok("Your response successfully submitted");
    }

    
   }

  /*
    
    
    @PostMapping("/register")
    public String registerFarmer(@Valid @RequestBody FarmerRegistrationDto farmerDTO) {
        return farmerService.registerFarmer(farmerDTO);
    }

    @PostMapping("/verify-registration-otp")
    public String verifyRegistrationOtp(@RequestParam String mobileNumber, @RequestParam String otp) {
        return farmerService.verifyRegistrationOtp(mobileNumber, otp);
    }

    @PostMapping("/login")
    public String loginFarmer(@RequestParam String mobileNumber) {
        return farmerService.loginFarmer(mobileNumber);
    }

    @PostMapping("/verify-login-otp")
    public String verifyLoginOtp(@RequestParam String mobileNumber, @RequestParam String otp) {
        return farmerService.verifyLoginOtp(mobileNumber, otp);
    }

    @GetMapping("/select-by-category")
    public List<AgripreneursByCategoryDto> getAgripreneursByCategory(@RequestParam String categoryName) {
        return farmerService.getAgripreneursByCategory(categoryName);
    }
    
    
    @GetMapping("/select-by-state-name/{stateName}")
    public List<AgripreneursByStateIdDto> getAgripreneursByStateName(@PathVariable String stateName) {
        return farmerService.getAgripreneursByStateName(stateName);
    }

    @GetMapping("/select-by-district-name/{districtName}")
    public List<AgripreneursByDistrictIdDto> getAgripreneursByDistrictName(@PathVariable String districtName) {
        return farmerService.getAgripreneursByDistrictName(districtName);
    }
   
    @GetMapping("/select-by-sub-district-name/{subDistrictName}")
    public List<AgripreneursBySubDistrictIdDto> getAgripreneursBySubDistrictName(@PathVariable String subDistrictName) {
        return farmerService.getAgripreneursBySubDistrictName(subDistrictName);
    }
 @GetMapping("/search-map")
    public ResponseEntity<List<AgripreneurMapDto>> searchAgripreneurs(
            @RequestParam double latitude, 
            @RequestParam double longitude, 
            @RequestParam double radius, 
            @RequestHeader("Authorization") String token) {

        if (isTokenValid(token)) {
            List<AgripreneurMapDto> agripreneurs = farmerService.getAgripreneursWithinRadius(latitude, longitude, radius);
            return ResponseEntity.ok(agripreneurs);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
    
    @PostMapping("/review")
    public ResponseEntity<String> submitReview(
            @RequestBody ReviewDto reviewDto, 
            @RequestHeader("Authorization") String token) {

        if (isTokenValid(token)) {
            farmerService.submitReview(reviewDto);
            return ResponseEntity.ok("Review and rating submitted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
  @PostMapping("/query")
    public ResponseEntity<Map<String, Object>> submitQuery(
            @ModelAttribute QueryDto queryDto, 
            @RequestHeader("Authorization") String token) {

        if (isTokenValid(token)) {
            try {
                FarmerQuery savedQuery = farmerService.submitQuery(queryDto);
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Query submitted successfully");
                response.put("query", savedQuery);
                return new ResponseEntity<>(response, HttpStatus.CREATED);
            } catch (IOException e) {
                e.printStackTrace();
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }


    */
