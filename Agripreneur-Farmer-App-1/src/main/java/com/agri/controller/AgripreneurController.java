package com.agri.controller;

import com.agri.dto.*;
import com.agri.exception.ResourceNotFoundException;
import com.agri.jwtFarmer.CustomUserDetailsService;
import com.agri.jwtFarmer.JwtUtil;

/*
import com.agri.dto.AgripreneurUpdateDto;
import com.agri.dto.AuthenticateRequest;
import com.agri.dto.LoginRequest;
import com.agri.dto.VerifyRequest;
*/
import org.springframework.http.HttpStatus;
import com.agri.model.Agripreneur;
import com.agri.model.District;
import com.agri.model.Farmer;
import com.agri.model.FarmerQuery;
import com.agri.model.ProfileVisit;
import com.agri.repository.AgripreneurRepository;
import com.agri.service.AgripreneurService;
import com.agri.service.FarmerService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;




@RestController
@RequestMapping("/api/agripreneurs")
@Validated
public class AgripreneurController {

	@Autowired
    private  FarmerService farmerService;
	
	@Autowired
	private AgripreneurRepository agripreneurRepository;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    @Autowired
    private final AgripreneurService agripreneurService;

    public AgripreneurController(JwtUtil jwtUtil, AuthenticationManager authenticationManager, CustomUserDetailsService userDetailsService, AgripreneurService agripreneurService) {
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.agripreneurService = agripreneurService;
    }
    

    @PostMapping("/register")
    public ResponseEntity<String> registerAgripreneur(@ModelAttribute AgripreneurRegistrationDto dto) throws IOException {
        return ResponseEntity.ok(agripreneurService.registerAgripreneur(dto));
    }

    @PostMapping("/verify-registration-otp")
    public ResponseEntity<String> verifyRegistrationOtp(
            @RequestParam String mobileNumber, 
            @RequestParam String otp) {
        
        String result = agripreneurService.verifyRegistrationOtp(mobileNumber, otp);
        if (result.equals("Invalid OTP.")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
        } else {
            return ResponseEntity.ok(result);
        }
    }

    @PostMapping("/login")
    public String loginAgripreneur(@RequestParam String mobileNumber) {
        return agripreneurService.loginAgripreneur(mobileNumber);
    }

    @PostMapping("/verify-login-otp")
    public ResponseEntity<JwtResponse> verifyLoginOtp(@RequestParam String mobileNumber, @RequestParam String otp) {
        String result = agripreneurService.verifyLoginOtp(mobileNumber, otp);
        if ("Login successful.".equals(result)) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(mobileNumber);
            String accessToken = jwtUtil.generateToken(userDetails.getUsername());
            String refreshToken = jwtUtil.generateRefreshToken(userDetails.getUsername());
            return ResponseEntity.ok(new JwtResponse(accessToken, refreshToken));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

   

    @PutMapping("/updateAgripreneurProfile")
    public ResponseEntity<String> updateAgripreneur(
            @ModelAttribute AgripreneurUpdateDto agripreneurUpdateDto,
            @RequestParam(value = "images", required = false) List<MultipartFile> images,
            @RequestHeader("Authorization") String token) throws IOException {
        
        String jwtToken = token.substring(7);
        String mobileNumber = jwtUtil.extractUsername(jwtToken);
        
        if (jwtUtil.validateToken(jwtToken, userDetailsService.loadUserByUsername(mobileNumber))) {
            if (images != null) {
                agripreneurUpdateDto.setImages(images);
            }
            agripreneurService.updateAgripreneur(mobileNumber, agripreneurUpdateDto);
            return ResponseEntity.ok("Updated successfully");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
    }
    
    
    @GetMapping("/viewProfile")
    public ResponseEntity<AgripreneurGetDto> viewProfileAgripreneur(
            @RequestHeader("Authorization") String token) {
        
        String jwtToken = token.substring(7);
        String mobileNumber = jwtUtil.extractUsername(jwtToken);
        
        if (jwtUtil.validateToken(jwtToken, userDetailsService.loadUserByUsername(mobileNumber))) {
            AgripreneurGetDto agripreneurDto = agripreneurService.getProfileByMobileNumber(mobileNumber);
            return ResponseEntity.ok(agripreneurDto);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @GetMapping("/agripreneurs-by-keyword/{keywords}")
    public ResponseEntity<List<AgripreneursByKeywordDto>> getAgripreneursByKeyword(
            @PathVariable String keywords, 
            @RequestHeader(value = "Authorization", required = false) String token){
        
        // Validate Authorization header
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        try {
            String jwtToken = token.substring(7);
            String mobileNumber = jwtUtil.extractUsername(jwtToken);

            // Validate JWT token
            if (jwtUtil.validateToken(jwtToken, userDetailsService.loadUserByUsername(mobileNumber))) {
                List<AgripreneursByKeywordDto> agripreneurs = agripreneurService.getAgripreneursByKeyword(keywords);
                
                // Return empty list if no agripreneurs match the keyword
                if (agripreneurs.isEmpty()) {
                    return ResponseEntity.ok(Collections.emptyList());
                }
                
                return ResponseEntity.ok(agripreneurs);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
    

 
 
 @GetMapping("/select-by-state-name/{stateName}")
 public ResponseEntity<List<AgripreneursByStateIdDto>> getAgripreneursByStateName(
         @PathVariable String stateName, 
         @RequestHeader("Authorization") String token) {

     String jwtToken = token.substring(7);
     String mobileNumber = jwtUtil.extractUsername(jwtToken);

     if (jwtUtil.validateToken(jwtToken, userDetailsService.loadUserByUsername(mobileNumber))) {
         List<AgripreneursByStateIdDto> agripreneurs = agripreneurService.getAgripreneursByStateName(stateName);
         return ResponseEntity.ok(agripreneurs);
     } else {
         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
     }
 }

 @GetMapping("/select-by-district-name/{districtName}")
 public ResponseEntity<List<AgripreneursByDistrictIdDto>> getAgripreneursByDistrictName(
         @PathVariable String districtName, 
         @RequestHeader("Authorization") String token) {

     String jwtToken = token.substring(7);
     String mobileNumber = jwtUtil.extractUsername(jwtToken);

     if (jwtUtil.validateToken(jwtToken, userDetailsService.loadUserByUsername(mobileNumber))) {
         List<AgripreneursByDistrictIdDto> agripreneurs = agripreneurService.getAgripreneursByDistrictName(districtName);
         return ResponseEntity.ok(agripreneurs);
     } else {
         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
     }
 }

 @GetMapping("/select-by-sub-district-name/{subDistrictName}")
 public ResponseEntity<List<AgripreneursBySubDistrictIdDto>> getAgripreneursBySubDistrictName(
         @PathVariable String subDistrictName, 
         @RequestHeader("Authorization") String token) {

     String jwtToken = token.substring(7);
     String mobileNumber = jwtUtil.extractUsername(jwtToken);

     if (jwtUtil.validateToken(jwtToken, userDetailsService.loadUserByUsername(mobileNumber))) {
         List<AgripreneursBySubDistrictIdDto> agripreneurs = agripreneurService.getAgripreneursBySubDistrictName(subDistrictName);
         return ResponseEntity.ok(agripreneurs);
     } else {
         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
     }
 }
 
 @GetMapping("/select-by-category")
 public ResponseEntity<List<AgripreneursByCategoryDto>> getAgripreneursByCategory(
         @RequestParam String categoryName, 
         @RequestHeader("Authorization") String token) {

     String jwtToken = token.substring(7);
     String mobileNumber = jwtUtil.extractUsername(jwtToken);

     if (jwtUtil.validateToken(jwtToken, userDetailsService.loadUserByUsername(mobileNumber))) {
         List<AgripreneursByCategoryDto> agripreneurs = agripreneurService.getAgripreneursByCategory(categoryName);
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
    
    String jwtToken = token.substring(7);
    String mobileNumber = jwtUtil.extractUsername(jwtToken);
    
    if (jwtUtil.validateToken(jwtToken, userDetailsService.loadUserByUsername(mobileNumber))) {
        List<AgripreneurMapDto> agripreneurs = agripreneurService.getAgripreneursWithinRadius(latitude, longitude, radius);
        return ResponseEntity.ok(agripreneurs);
    } else {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }
}


@GetMapping("/Farmerselect-by-district-name/{districtName}")
public ResponseEntity<List<FarmerByDistrictIdDto>> getFarmersByDistrictName(
        @PathVariable String districtName, 
        @RequestHeader("Authorization") String token) {
    
    String jwtToken = token.substring(7);
    String mobileNumber = jwtUtil.extractUsername(jwtToken);
    
    if (jwtUtil.validateToken(jwtToken, userDetailsService.loadUserByUsername(mobileNumber))) {
        List<FarmerByDistrictIdDto> farmers = agripreneurService.getFarmersByDistrictName(districtName);
        return ResponseEntity.ok(farmers);
    } else {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }
}


@GetMapping("/farmerQuery")
public ResponseEntity<?> getFarmerQueriesByAgripreneur(@RequestHeader("Authorization") String token) {
    
    String jwtToken = token.substring(7); // Extract JWT token
    String mobileNumber = jwtUtil.extractUsername(jwtToken); // Extract mobile number

    // Validate the token
    if (!jwtUtil.validateToken(jwtToken, userDetailsService.loadUserByUsername(mobileNumber))) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(Collections.singletonMap("message", "Invalid token"));
    }

    // Find the agripreneur by mobile number
    return agripreneurRepository.findByMobileNumber(mobileNumber)
        .map(agripreneur -> {
            List<FarmerQueryDto> queries = agripreneurService.getFarmerQueriesByAgripreneur(agripreneur.getAgripreneurId());
            if (queries.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(Collections.singletonMap("message", "No queries found for this agripreneur"));
            }
            return new ResponseEntity<>(queries, HttpStatus.OK);
        })
        .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Collections.singletonMap("message", "Agripreneur not found")));
}

@PostMapping("/farmerQuery/respond")
public ResponseEntity<?> respondToFarmerQuery(@RequestHeader("Authorization") String token,
                                              @RequestParam Long queryId,
                                              @RequestParam String responseText) {

    String jwtToken = token.substring(7); // Extract JWT token
    String mobileNumber = jwtUtil.extractUsername(jwtToken); // Extract mobile number

    // Validate the token
    if (!jwtUtil.validateToken(jwtToken, userDetailsService.loadUserByUsername(mobileNumber))) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(Collections.singletonMap("message", "Invalid token"));
    }

    // Find the agripreneur by mobile number
    return agripreneurRepository.findByMobileNumber(mobileNumber)
        .map(agripreneur -> {
            // Check if the agripreneur has any queries
            List<FarmerQuery> queries = agripreneurService.getQueriesForAgripreneur(agripreneur.getAgripreneurId());
            if (queries.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(Collections.singletonMap("message", "No queries found for this agripreneur"));
            }

            // Now allow responding to the specific query
            FarmerQuery updatedQuery = agripreneurService.respondToQuery(agripreneur.getAgripreneurId(), queryId, responseText);
            return new ResponseEntity<>(updatedQuery, HttpStatus.OK);
        })
        .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Collections.singletonMap("message", "Agripreneur not found")));
}


@PostMapping("/refresh-token")
public ResponseEntity<JwtResponse> refreshAccessToken(@RequestParam String refreshToken) {
    String mobileNumber = jwtUtil.extractUsername(refreshToken);

    if (agripreneurService.validateRefreshToken(mobileNumber, refreshToken)) {
        String newAccessToken = jwtUtil.generateToken(mobileNumber);
        String newRefreshToken = jwtUtil.generateRefreshToken(mobileNumber);

        // Update the refresh token in the database
        agripreneurService.saveRefreshToken(mobileNumber, newRefreshToken);

        return ResponseEntity.ok(new JwtResponse(newAccessToken, newRefreshToken));
    } else {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }
}
@GetMapping("/visited-farmers")
public ResponseEntity<?> getVisitedFarmers(@RequestHeader("Authorization") String token) {
    // Extract JWT token and remove "Bearer " prefix if present
    String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;

    try {
        // Extract the mobile number (or username) from JWT token
        String mobileNumber = jwtUtil.extractUsername(jwtToken);

        // Validate the JWT token with the user details
        if (jwtUtil.validateToken(jwtToken, userDetailsService.loadUserByUsername(mobileNumber))) {
            // Find the agripreneur by mobile number
            Agripreneur agripreneur = agripreneurRepository.findByMobileNumber(mobileNumber)
                    .orElseThrow(() -> new IllegalArgumentException("Agripreneur not found with mobile number: " + mobileNumber));

            // Fetch the list of farmers who visited the agripreneur’s profile
            List<FarmerVisitDTO> visitedFarmers = agripreneurService.getVisitedFarmers(agripreneur.getAgripreneurId());
            return ResponseEntity.ok(visitedFarmers);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token.");
        }
    } catch (Exception e) {
        // Handle exceptions such as token parsing errors or agripreneur not found
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access: " + e.getMessage());
    }
}


}

/*
	@PostMapping("/register")
    public String registerAgripreneur(@ModelAttribute AgripreneurRegistrationDto dto) throws IOException {
        return agripreneurService.registerAgripreneur(dto);
    }

    @PostMapping("/verify-registration-otp")
    public String verifyRegistrationOtp(@RequestParam String mobileNumber, @RequestParam String otp) {
        return agripreneurService.verifyRegistrationOtp(mobileNumber, otp);
    }

      @PostMapping("/login")
    public String loginAgripreneur(@RequestParam String mobileNumber) {
        return agripreneurService.loginAgripreneur(mobileNumber);
    }

    @PostMapping("/verify-login-otp")
    public String verifyLoginOtp(@RequestParam String mobileNumber, @RequestParam String otp) {
        return agripreneurService.verifyLoginOtp(mobileNumber, otp);
    }

 @PutMapping("/updateAgripreneurProfile")
    public ResponseEntity<String> updateAgripreneur(
            @Valid @RequestBody AgripreneurUpdateDto agripreneurUpdateDto,
            @RequestParam(value = "images", required = false) List<MultipartFile> images,
            @RequestHeader("Authorization") String token) throws IOException {
        String jwtToken = token.substring(7);
        String mobileNumber = jwtUtil.extractUsername(jwtToken);

        if (jwtUtil.validateToken(jwtToken, userDetailsService.loadUserByUsername(mobileNumber))) {
            if (images != null) {
                agripreneurUpdateDto.setImages(images);
            }
            agripreneurService.updateAgripreneur(mobileNumber, agripreneurUpdateDto);
            return ResponseEntity.ok("Updated successfully");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
    }

      
      @PostMapping("/verify-login-otp")
      public ResponseEntity<JwtResponse> verifyLoginOtp(@RequestParam String mobileNumber, @RequestParam String otp) {
          String result = agripreneurService.verifyLoginOtp(mobileNumber, otp);
          if ("Login successful.".equals(result)) {
              UserDetails userDetails = userDetailsService.loadUserByUsername(mobileNumber);
              String token = jwtUtil.generateToken(userDetails.getUsername());
              return ResponseEntity.ok(new JwtResponse(token));
          } else {
              return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
          }
      }
/*
    @PutMapping("/updateAgripreneurProfile/{id}")
    public ResponseEntity<String> updateAgripreneur(
            @PathVariable(value = "id") Long id,
            @Valid AgripreneurUpdateDto agripreneurUpdateDto,
            @RequestParam(value = "images", required = false) List<MultipartFile> images) throws IOException {
        if (images != null) {
            agripreneurUpdateDto.setImages(images);
        }
        agripreneurService.updateAgripreneur(id, agripreneurUpdateDto);
        return ResponseEntity.ok("Updated successfully");
    }
    
          @PutMapping("/updateAgripreneurProfile")
    public ResponseEntity<String> updateAgripreneur(
            @Valid @RequestBody AgripreneurUpdateDto agripreneurUpdateDto,
            @RequestParam(value = "images", required = false) List<MultipartFile> images,
            @RequestHeader("Authorization") String token) throws IOException {
        String jwtToken = token.substring(7);
        String mobileNumber = jwtUtil.extractUsername(jwtToken);

        if (jwtUtil.validateToken(jwtToken, userDetailsService.loadUserByUsername(mobileNumber))) {
            if (images != null) {
                agripreneurUpdateDto.setImages(images);
            }
            agripreneurService.updateAgripreneur(mobileNumber, agripreneurUpdateDto);
            return ResponseEntity.ok("Updated successfully");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }
    

      @PutMapping("/updateAgripreneurProfile")
      public ResponseEntity<String> updateAgripreneur(
              @Valid @RequestBody AgripreneurUpdateDto agripreneurUpdateDto,
              @RequestParam(value = "images", required = false) List<MultipartFile> images,
              @RequestHeader("Authorization") String token) throws IOException {
          String jwtToken = token.substring(7);
          String mobileNumber = jwtUtil.extractUsername(jwtToken);

          if (jwtUtil.validateToken(jwtToken, userDetailsService.loadUserByUsername(mobileNumber))) {
              if (images != null) {
                  agripreneurUpdateDto.setImages(images);
              }
              agripreneurService.updateAgripreneur(mobileNumber, agripreneurUpdateDto);
              return ResponseEntity.ok("Updated successfully");
          } else {
              return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
          }
      }


      @PutMapping("/updateAgripreneurProfile")
      public ResponseEntity<String> updateAgripreneur(
              @Valid @RequestBody AgripreneurUpdateDto agripreneurUpdateDto,
              @RequestParam(value = "images", required = false) List<MultipartFile> images,
              @RequestHeader("Authorization") String token) throws IOException {
          String jwtToken = token.substring(7);
          String mobileNumber = jwtUtil.extractUsername(jwtToken);

          if (jwtUtil.validateToken(jwtToken, userDetailsService.loadUserByUsername(mobileNumber))) {
              if (images != null) {
                  agripreneurUpdateDto.setImages(images);
              }
              agripreneurService.updateAgripreneur(mobileNumber, agripreneurUpdateDto);
              return ResponseEntity.ok("Updated successfully");
          } else {
              return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
          }
      }

      @ExceptionHandler(Exception.class)
      public ResponseEntity<String> handleException(Exception e) {
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
      }

       @GetMapping("/viewProfile/{id}")
    public ResponseEntity<AgripreneurGetDto> viewProfileAgripreneur(@PathVariable Long id) {
        AgripreneurGetDto agripreneurDto = agripreneurService.getProfile(id);
        return ResponseEntity.ok(agripreneurDto);
    }

    @GetMapping("/Farmerselect-by-district-name/{districtName}")
    public List<FarmerByDistrictIdDto> getFarmersByDistrictName(@PathVariable String districtName) {
        return agripreneurService.getFarmersByDistrictName(districtName);
    }
    
    @GetMapping("/Agripreneurselect-by-keyword/{keywords}")
    public List<AgripreneursByKeywordDto> getAgripreneursByKeyword(@PathVariable String keywords) {
        return agripreneurService.getAgripreneursByKeyword(keywords);
    }
    
    
    @GetMapping("/select-by-category")
    public List<AgripreneursByCategoryDto> getAgripreneursByCategory(@RequestParam String categoryName) {
        return agripreneurService.getAgripreneursByCategory(categoryName);
    }
    
    
    @GetMapping("/select-by-state-name/{stateName}")
    public List<AgripreneursByStateIdDto> getAgripreneursByStateName(@PathVariable String stateName) {
        return agripreneurService.getAgripreneursByStateName(stateName);
    }

    @GetMapping("/select-by-district-name/{districtName}")
    public List<AgripreneursByDistrictIdDto> getAgripreneursByDistrictName(@PathVariable String districtName) {
        return agripreneurService.getAgripreneursByDistrictName(districtName);
    }
   
    @GetMapping("/select-by-sub-district-name/{subDistrictName}")
    public List<AgripreneursBySubDistrictIdDto> getAgripreneursBySubDistrictName(@PathVariable String subDistrictName) {
        return agripreneurService.getAgripreneursBySubDistrictName(subDistrictName);
    }
    
    @GetMapping("/{agripreneurId}/farmerQuery")
    public ResponseEntity<List<FarmerQueryDto>> getFarmerQueriesByAgripreneur(@PathVariable Long agripreneurId) {
        List<FarmerQueryDto> queries = agripreneurService.getFarmerQueriesByAgripreneur(agripreneurId);
        return new ResponseEntity<>(queries, HttpStatus.OK);
    }
   
    @GetMapping("/search-map")
    public List<AgripreneurMapDto> searchAgripreneurs(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam double radius) {
        return agripreneurService.getAgripreneursWithinRadius(latitude, longitude, radius);
    }
      
      
}  

/*
@PostMapping("/register")
public String registerAgripreneur(@ModelAttribute AgripreneurRegistrationDto dto) throws IOException {
    return agripreneurService.registerAgripreneur(dto);
}

@PostMapping("/verify-registration-otp")
public String verifyRegistrationOtp(@RequestParam String mobileNumber, @RequestParam String otp) {
    return agripreneurService.verifyRegistrationOtp(mobileNumber, otp);
}

@PostMapping("/login")
public String loginAgripreneur(@RequestParam String mobileNumber) {
    return agripreneurService.loginAgripreneur(mobileNumber);
}

@PostMapping("/verify-login-otp")
public String verifyLoginOtp(@RequestParam String mobileNumber, @RequestParam String otp) {
    return agripreneurService.verifyLoginOtp(mobileNumber, otp);
}
*/

/*
@PostMapping("/register1")
public ResponseEntity<Map<String, Object>> registerAgripreneur(
        @Valid @ModelAttribute AgripreneurRegistrationDto registrationDto) throws IOException {
    Agripreneur savedAgripreneur = agripreneurService.saveAgripreneur(registrationDto);
    String smsMessage = generateSmsMessage(savedAgripreneur);

    Map<String, Object> response = new HashMap<>();
    response.put("agripreneurId", savedAgripreneur.getAgripreneurId());
    response.put("smsMessage", smsMessage);
    response.put("message", "Registration successful.");

    return ResponseEntity.ok(response);
}

private String generateSmsMessage(Agripreneur agripreneur) {
    return "Registration successful. Your ID is: " + agripreneur.getAgripreneurId();
}


@PostMapping("/authenticate")
public ResponseEntity<Map<String, Object>> authenticateMobileNumber(@Valid @RequestBody MobileNumberDto mobileNumberDto) {
    agripreneurService.sendOtp(mobileNumberDto.getMobileNumber());
    
    Map<String, Object> response = new HashMap<>();
    response.put("status", "success");
    response.put("message", "OTP sent successfully");
    
    return ResponseEntity.ok(response);
}

@PostMapping("/verify")
public ResponseEntity<Map<String, Object>> verifyOtp(@Valid @RequestBody OtpVerificationDto otpVerificationDto) {
    boolean isVerified = agripreneurService.verifyOtp(otpVerificationDto.getMobileNumber(), otpVerificationDto.getOtp());
    
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
    String token = agripreneurService.login(otpVerificationDto.getMobileNumber(), otpVerificationDto.getOtp());
    
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
@PostMapping(value = "/register", consumes = "multipart/form-data")
public ResponseEntity<String> registerAgripreneur(
        @RequestPart("data") AgripreneurRegistrationDto agripreneurRegistrationDto,
        @RequestPart("files") List<MultipartFile> files) {
    try {
        agripreneurRegistrationDto.setImages(files);
        String response = agripreneurService.registerAgripreneur(agripreneurRegistrationDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
        return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}

// Verify OTP for registration
@PostMapping("/verify-registration-otp")
public ResponseEntity<String> verifyRegistrationOtp(@RequestParam String mobileNumber, @RequestParam String otp) {
    try {
        String response = agripreneurService.verifyRegistrationOtp(mobileNumber, otp);
        return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
        return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}

// Login Agripreneur
@PostMapping("/login")
public ResponseEntity<String> loginAgripreneur(@RequestParam String mobileNumber) {
    try {
        String response = agripreneurService.loginAgripreneur(mobileNumber);
        return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
        return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}

// Verify OTP for login
@PostMapping("/verify-login-otp")
public ResponseEntity<String> verifyLoginOtp(@RequestParam String mobileNumber, @RequestParam String otp) {
    try {
        String response = agripreneurService.verifyLoginOtp(mobileNumber, otp);
        return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
        return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}    




    @GetMapping("/viewProfile/{id}")
 public ResponseEntity<AgripreneurGetDto> viewProfileAgripreneur(@PathVariable Long id) {
     AgripreneurGetDto agripreneurDto = agripreneurService.getProfile(id);
     return ResponseEntity.ok(agripreneurDto);
 }

    @PostMapping("/register")
    public String registerAgripreneur(@ModelAttribute AgripreneurRegistrationDto dto) throws IOException {
        return agripreneurService.registerAgripreneur(dto);
    }

    @PostMapping("/verify-registration-otp")
    public String verifyRegistrationOtp(@RequestParam String mobileNumber, @RequestParam String otp) {
        return agripreneurService.verifyRegistrationOtp(mobileNumber, otp);
    }
 
 @GetMapping("/Agripreneurselect-by-keyword/{keywords}")
 public List<AgripreneursByKeywordDto> getAgripreneursByKeyword(@PathVariable String keywords) {
     return agripreneurService.getAgripreneursByKeyword(keywords);
 }
 
 @GetMapping("/select-by-category")
 public List<AgripreneursByCategoryDto> getAgripreneursByCategory(@RequestParam String categoryName) {
     return agripreneurService.getAgripreneursByCategory(categoryName);
 }
 
 
 @GetMapping("/select-by-state-name/{stateName}")
 public List<AgripreneursByStateIdDto> getAgripreneursByStateName(@PathVariable String stateName) {
     return agripreneurService.getAgripreneursByStateName(stateName);
 }

 @GetMapping("/select-by-district-name/{districtName}")
 public List<AgripreneursByDistrictIdDto> getAgripreneursByDistrictName(@PathVariable String districtName) {
     return agripreneurService.getAgripreneursByDistrictName(districtName);
 }

 @GetMapping("/select-by-sub-district-name/{subDistrictName}")
 public List<AgripreneursBySubDistrictIdDto> getAgripreneursBySubDistrictName(@PathVariable String subDistrictName) {
     return agripreneurService.getAgripreneursBySubDistrictName(subDistrictName);
 }
 

 @GetMapping("/{agripreneurId}/farmerQuery")
 public ResponseEntity<List<FarmerQueryDto>> getFarmerQueriesByAgripreneur(@PathVariable Long agripreneurId) {
     List<FarmerQueryDto> queries = agripreneurService.getFarmerQueriesByAgripreneur(agripreneurId);
     return new ResponseEntity<>(queries, HttpStatus.OK);
 }

 @GetMapping("/search-map")
 public List<AgripreneurMapDto> searchAgripreneurs(
         @RequestParam double latitude,
         @RequestParam double longitude,
         @RequestParam double radius) {
     return agripreneurService.getAgripreneursWithinRadius(latitude, longitude, radius);
 }
 
 
 @GetMapping("/Farmerselect-by-district-name/{districtName}")
 public List<FarmerByDistrictIdDto> getFarmersByDistrictName(@PathVariable String districtName) {
     return agripreneurService.getFarmersByDistrictName(districtName);
 }

 @PostMapping("/login")
    public String loginAgripreneur(@RequestParam String mobileNumber) {
        return agripreneurService.loginAgripreneur(mobileNumber);
    }

    @PostMapping("/verify-login-otp")
    public ResponseEntity<JwtResponse> verifyLoginOtp(@RequestParam String mobileNumber, @RequestParam String otp) {
        String result = agripreneurService.verifyLoginOtp(mobileNumber, otp);
        if ("Login successful.".equals(result)) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(mobileNumber);
            String token = jwtUtil.generateToken(userDetails.getUsername());
            return ResponseEntity.ok(new JwtResponse(token));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
*/

