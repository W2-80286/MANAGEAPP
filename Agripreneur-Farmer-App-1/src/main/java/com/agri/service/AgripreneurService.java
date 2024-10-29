package com.agri.service;

import com.agri.dto.AgripreneurGetDto;
import com.agri.dto.AgripreneurImageDto;
import com.agri.dto.AgripreneurMapDto;
import com.agri.dto.AgripreneurRegistrationDto;
import com.agri.dto.AgripreneurUpdateDto;
import com.agri.dto.AgripreneursByCategoryDto;
import com.agri.dto.AgripreneursByDistrictIdDto;
import com.agri.dto.AgripreneursByKeywordDto;
import com.agri.dto.AgripreneursByStateIdDto;
import com.agri.dto.AgripreneursBySubDistrictIdDto;
import com.agri.dto.CoordinatesDto;
import com.agri.dto.FarmerByDistrictIdDto;
import com.agri.dto.FarmerQueryDto;
import com.agri.dto.FarmerVisitDTO;
import com.agri.dto.ReviewDto;
import com.agri.exception.ResourceNotFoundException;
import com.agri.jwtFarmer.CustomUserDetailsService;
import com.agri.jwtFarmer.JwtRequestFilter;
import com.agri.jwtFarmer.JwtUtil;
import com.agri.model.*;
import com.agri.repository.*;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class AgripreneurService {

	
        @Value("${file.upload-dir}")
    private String uploadDir;
        
        
        @Autowired
        private ProfileVisitRepository profileVisitRepository;


	  
	@Autowired
	private  JwtRequestFilter jwtAuthFilter;
	
	@Autowired
	private  CustomUserDetailsService userDetailsService;

	@Autowired
    private  JwtUtil jwtUtil;


    @Autowired
    private OtpService otpService;

    @Autowired
    private  AgripreneurRepository agripreneurRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private DistrictRepository districtRepository;

    @Autowired
    private SubDistrictRepository subDistrictRepository;

    @Autowired
    private VillageRepository villageRepository;

    @Autowired
    private  AgripreneurImageRepository agripreneurImageRepository;
    
    @Autowired
    private FarmerRepository farmerRepository;
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    private  final QueryRepository queryRepository;
    
    @Autowired
    public AgripreneurService(QueryRepository queryRepository) {
        this.queryRepository = queryRepository;
    }
    
    
    


// Register method that saves agripreneur details in an in-progress state
public String registerAgripreneur(AgripreneurRegistrationDto agripreneurDTO) throws IOException {
    validateRequiredFields(agripreneurDTO);

    // Check if the mobile number is already registered
    Optional<Agripreneur> existingAgripreneur = agripreneurRepository.findByMobileNumber(agripreneurDTO.getMobileNumber());
    if (existingAgripreneur.isPresent()) {
        return "Mobile number already registered.";
    }

    // Generate and send OTP
    String otp = otpService.generateOtp();
    System.out.println("Sending OTP: " + otp + " to mobile number: " + agripreneurDTO.getMobileNumber());

    // Save agripreneur details in "in-progress" state with OTP
    Agripreneur agripreneur = new Agripreneur();
    agripreneur.setFullName(agripreneurDTO.getFullName());
    agripreneur.setEmail(agripreneurDTO.getEmail());
    agripreneur.setMobileNumber(agripreneurDTO.getMobileNumber());
    agripreneur.setVentureName(agripreneurDTO.getVentureName());
    agripreneur.setIdNo(agripreneurDTO.getIdNo());
    agripreneur.setPincode(agripreneurDTO.getPincode());
    agripreneur.setAlternateNumber(agripreneurDTO.getAlternateNumber());
    agripreneur.setTraningCenterName(agripreneurDTO.getTrainingCenterName());
    agripreneur.setAnnualTurnover(agripreneurDTO.getAnnualTurnover());
    agripreneur.setAnnualIncome(agripreneurDTO.getAnnualIncome());
    agripreneur.setPersonsEmployed(agripreneurDTO.getPersonsEmployed());
    agripreneur.setFarmersCovered(agripreneurDTO.getFarmersCovered());
    agripreneur.setVillagesCovered(agripreneurDTO.getVillagesCovered());
    agripreneur.setBankLoan(agripreneurDTO.getBankLoan());
    agripreneur.setSubsidy(agripreneurDTO.getSubsidy());
    agripreneur.setKeywords(agripreneurDTO.getKeywords());
    agripreneur.setServiceCost(agripreneurDTO.getServiceCost());
    agripreneur.setOtp(otp); // Set the OTP for registration
    agripreneur.setRegistrationCompleted(false); // Mark as in-progress initially

    // Set location and category details
    setLocationAndCategoryDetails(agripreneur, agripreneurDTO);

    // Save the agripreneur entity with OTP
    agripreneurRepository.save(agripreneur);

    // Save images
    for (MultipartFile file : agripreneurDTO.getImages()) {
        String filePath = saveFile(file);
        AgripreneurImage agripreneurImage = new AgripreneurImage();
        agripreneurImage.setAgripreneur(agripreneur);
        agripreneurImage.setImagePath(filePath);
        agripreneurImageRepository.save(agripreneurImage);
    }

    return "OTP has been sent to your mobile number for registration.";
}

// OTP Verification method that marks the registration as complete and generates token
public String verifyRegistrationOtp(String mobileNumber, String otp) {
    Optional<Agripreneur> agripreneurOptional = agripreneurRepository.findByMobileNumber(mobileNumber);
    if (agripreneurOptional.isPresent()) {
        Agripreneur agripreneur = agripreneurOptional.get();
        if (agripreneur.getOtp().equals(otp)) {
            agripreneur.setOtp(null); // Clear OTP after successful verification
            agripreneur.setRegistrationCompleted(true); // Mark registration as complete
            agripreneurRepository.save(agripreneur);
            return jwtUtil.generateToken(mobileNumber); // Return JWT token
        }
    }
    return "Invalid OTP.";
}

// Utility methods for the service
    // Implement validation logic
	
	private void validateRequiredFields(AgripreneurRegistrationDto dto) {
	       if (dto.getFullName() == null || dto.getFullName().isEmpty()) {
	           throw new IllegalArgumentException("Full name is required.");
	       }
	       if (dto.getIdNo() == null || dto.getIdNo().isEmpty()) {
	           throw new IllegalArgumentException("ID number is required.");
	       }
	       if (dto.getVentureName() == null || dto.getVentureName().isEmpty()) {
	           throw new IllegalArgumentException("Venture name is required.");
	       }
	       if (dto.getPincode() == null) {
	           throw new IllegalArgumentException("Pincode is required.");
	       }
	       if (dto.getMobileNumber() == null) {
	           throw new IllegalArgumentException("Mobile number is required.");
	       }
	       if (dto.getEmail() == null || dto.getEmail().isEmpty()) {
	           throw new IllegalArgumentException("Email is required.");
	       }
	       if (dto.getTrainingCenterName() == null || dto.getTrainingCenterName().isEmpty()) {
	           throw new IllegalArgumentException("Training center name is required.");
	       }
	       if (dto.getAnnualTurnover() == null) {
	           throw new IllegalArgumentException("Annual turnover is required.");
	       }
	       if (dto.getAnnualIncome() == null) {
	           throw new IllegalArgumentException("Annual income is required.");
	       }
	       if (dto.getPersonsEmployed() == null) {
	           throw new IllegalArgumentException("Number of persons employed is required.");
	       }
	       if (dto.getFarmersCovered() == null) {
	           throw new IllegalArgumentException("Number of farmers covered is required.");
	       }
	       if (dto.getVillagesCovered() == null) {
	           throw new IllegalArgumentException("Number of villages covered is required.");
	       }
	       if (dto.getBankLoan() == null) {
	           throw new IllegalArgumentException("Bank loan details are required.");
	       }
	       if (dto.getSubsidy() == null) {
	           throw new IllegalArgumentException("Subsidy details are required.");
	       }
	       if (dto.getKeywords() == null || dto.getKeywords().isEmpty()) {
	           throw new IllegalArgumentException("Keywords are required.");
	       }
	       if (dto.getLatitude() == null) {
	           throw new IllegalArgumentException("Latitude is required.");
	       }
	       if (dto.getLongitude() == null) {
	           throw new IllegalArgumentException("Longitude is required.");
	       }
	       
	       if (dto.getImages() == null || dto.getImages().isEmpty()) {
	           throw new IllegalArgumentException("At least one image is required.");
	       }
	   
	}

	  private void setLocationAndCategoryDetails(Agripreneur agripreneur, AgripreneurRegistrationDto agripreneurDTO) {
	       // Set state
	       State state = stateRepository.findById(agripreneurDTO.getStateId())
	               .orElseThrow(() -> new ResourceNotFoundException("State not found"));
	       agripreneur.setState(state);

	       // Set district
	       District district = districtRepository.findById(agripreneurDTO.getDistrictId())
	               .orElseThrow(() -> new ResourceNotFoundException("District not found"));
	       agripreneur.setDistrict(district);

	       // Set sub-district
	       SubDistrict subDistrict = subDistrictRepository.findById(agripreneurDTO.getSubDistrictId())
	               .orElseThrow(() -> new ResourceNotFoundException("Sub-district not found"));
	       agripreneur.setSubDistrict(subDistrict);

	       // Set village
	       Village village = villageRepository.findByNameAndStateAndDistrictAndSubDistrict(
	               agripreneurDTO.getVillageName(), state, district, subDistrict);
	       if (village == null) {
	           village = new Village();
	           village.setName(agripreneurDTO.getVillageName());
	           village.setState(state);
	           village.setDistrict(district);
	           village.setSubDistrict(subDistrict);
	           villageRepository.save(village);
	       }
	       agripreneur.setVillage(village);

	       // Set coordinates
	       Coordinates coordinates = new Coordinates();
	       coordinates.setLatitude(agripreneurDTO.getLatitude());
	       coordinates.setLongitude(agripreneurDTO.getLongitude());
	       coordinates.setAgripreneur(agripreneur);
	       agripreneur.setCoordinates(coordinates);

	       // Set service categories
	       List<Long> categoryIds = agripreneurDTO.getServiceCategoryIds();
	       List<Category> categories = categoryRepository.findAllById(categoryIds);
	       if (categories.size() != categoryIds.size()) {
	           throw new ResourceNotFoundException("One or more categories not found");
	       }
	       agripreneur.setServiceCategories(categories);
	   }

	  private String saveFile(MultipartFile file) throws IOException {
	       // Ensure the directory exists
	       File directory = new File(uploadDir);
	       if (!directory.exists()) {
	           directory.mkdirs();
	       }

	       // Save the file
	       File dest = new File(directory, file.getOriginalFilename());
	       file.transferTo(dest);
	       return dest.getAbsolutePath();
	   }

    
	  
	  
	// Service method to send OTP for login
	  public String loginAgripreneur(String mobileNumber) {
	      // Check if the mobile number is registered
	      Optional<Agripreneur> agripreneurOptional = agripreneurRepository.findByMobileNumber(mobileNumber);
	      
	      if (agripreneurOptional.isPresent()) {
	          // Generate and send OTP if registered
	          String otp = otpService.generateOtp();
	          System.out.println("Sending OTP: " + otp + " to mobile number: " + mobileNumber);
	          
	          Agripreneur agripreneur = agripreneurOptional.get();
	          agripreneur.setOtp(otp);
	          agripreneurRepository.save(agripreneur);
	          
	          return "OTP has been sent to your mobile number for login.";
	      }
	      
	      // If not registered, prompt to register
	      return "Mobile number not registered. Please register first.";
	  }

	  // Service method to verify OTP for login
	  public String verifyLoginOtp(String mobileNumber, String otp) {
	      // Check if the mobile number is registered
	      Optional<Agripreneur> agripreneurOptional = agripreneurRepository.findByMobileNumber(mobileNumber);
	      
	      if (agripreneurOptional.isPresent()) {
	          Agripreneur agripreneur = agripreneurOptional.get();
	          
	          // Verify the OTP
	          if (agripreneur.getOtp() != null && agripreneur.getOtp().equals(otp)) {
	              agripreneur.setOtp(null); // Clear OTP after successful verification
	              agripreneurRepository.save(agripreneur);
	              
	              return "Login successful.";
	          } else {
	              return "Invalid OTP.";
	          }
	      }
	      
	      // If not registered, prompt to register
	      return "Mobile number not registered. Please register first.";
	  }
	     
	     

    
    
    
    @Transactional
    public Agripreneur updateAgripreneur(String mobileNumber, AgripreneurUpdateDto updateRequest) throws IOException {
        Agripreneur agripreneur = agripreneurRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Agripreneur not found with mobile number " + mobileNumber));

        if (updateRequest.getFullName() != null) {
            agripreneur.setFullName(updateRequest.getFullName());
        }
        if (updateRequest.getVentureName() != null) {
            agripreneur.setVentureName(updateRequest.getVentureName());
        }
        if (updateRequest.getServiceCategoryIds() != null) {
            List<Category> categories = categoryRepository.findAllById(updateRequest.getServiceCategoryIds());
            if (categories.size() != updateRequest.getServiceCategoryIds().size()) {
                throw new ResourceNotFoundException("One or more categories not found");
            }
            agripreneur.setServiceCategories(categories);
        }
        if (updateRequest.getStateId() != null) {
            State state = stateRepository.findById(updateRequest.getStateId())
                    .orElseThrow(() -> new ResourceNotFoundException("State not found"));
            agripreneur.setState(state);
        }
        if (updateRequest.getDistrictId() != null) {
            District district = districtRepository.findById(updateRequest.getDistrictId())
                    .orElseThrow(() -> new ResourceNotFoundException("District not found"));
            agripreneur.setDistrict(district);
        }

      
            if (updateRequest.getSubDistrictId() != null) {
            SubDistrict subDistrict = subDistrictRepository.findById(updateRequest.getSubDistrictId())
                    .orElseThrow(() -> new ResourceNotFoundException("SubDistrict not found"));
            agripreneur.setSubDistrict(subDistrict);
        }
        if (updateRequest.getVillageName() != null) {
            Village village = villageRepository.findByNameAndStateAndDistrictAndSubDistrict(
                    updateRequest.getVillageName(), agripreneur.getState(), agripreneur.getDistrict(), agripreneur.getSubDistrict());
            if (village == null) {
                village = new Village();
                village.setName(updateRequest.getVillageName());
                village.setState(agripreneur.getState());
                village.setDistrict(agripreneur.getDistrict());
                village.setSubDistrict(agripreneur.getSubDistrict());
                villageRepository.save(village);
            }
            agripreneur.setVillage(village);
        }
        if (updateRequest.getPincode() != null) {
            agripreneur.setPincode(updateRequest.getPincode());
        }
        if (updateRequest.getAlternateNumber() != null) {
            agripreneur.setAlternateNumber(updateRequest.getAlternateNumber());
        }
        if (updateRequest.getEmail() != null) {
            agripreneur.setEmail(updateRequest.getEmail());
        }
        if (updateRequest.getTrainingCenterName() != null) {
            agripreneur.setTraningCenterName(updateRequest.getTrainingCenterName());
        }
        if (updateRequest.getAnnualTurnover() != null) {
            agripreneur.setAnnualTurnover(updateRequest.getAnnualTurnover());
        }
        if (updateRequest.getAnnualIncome() != null) {
            agripreneur.setAnnualIncome(updateRequest.getAnnualIncome());
        }
        if (updateRequest.getPersonsEmployed() != null) {
            agripreneur.setPersonsEmployed(updateRequest.getPersonsEmployed());
        }
        if (updateRequest.getFarmersCovered() != null) {
            agripreneur.setFarmersCovered(updateRequest.getFarmersCovered());
        }
        if (updateRequest.getVillagesCovered() != null) {
            agripreneur.setVillagesCovered(updateRequest.getVillagesCovered());
        }
        if (updateRequest.getBankLoan() != null) {
            agripreneur.setBankLoan(updateRequest.getBankLoan());
        }
        if (updateRequest.getSubsidy() != null) {
            agripreneur.setSubsidy(updateRequest.getSubsidy());
        }
        if (updateRequest.getKeywords() != null) {
            agripreneur.setKeywords(updateRequest.getKeywords());
        }
        if (updateRequest.getServiceCost() != null) {
            agripreneur.setServiceCost(updateRequest.getServiceCost());
        }
        if (updateRequest.getLatitude() != null && updateRequest.getLongitude() != null) {
            Coordinates coordinates = agripreneur.getCoordinates();
            if (coordinates == null) {
                coordinates = new Coordinates();
                coordinates.setAgripreneur(agripreneur);
                agripreneur.setCoordinates(coordinates);
            }
            coordinates.setLatitude(updateRequest.getLatitude());
            coordinates.setLongitude(updateRequest.getLongitude());
        }
        if (updateRequest.getImages() != null && !updateRequest.getImages().isEmpty()) {
            agripreneurImageRepository.deleteByAgripreneur(agripreneur);
            for (MultipartFile file : updateRequest.getImages()) {
                String filePath = saveFile(file);
                AgripreneurImage agripreneurImage = new AgripreneurImage();
                agripreneurImage.setAgripreneur(agripreneur);
                agripreneurImage.setImagePath(filePath);
                agripreneurImageRepository.save(agripreneurImage);
            }
        }

        return agripreneurRepository.save(agripreneur);
    }
    
    
    public AgripreneurGetDto getProfileByMobileNumber(String mobileNumber) {
        Agripreneur agripreneur = agripreneurRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Agripreneur not found with mobile number: " + mobileNumber));
        
        AgripreneurGetDto agripreneurGetDto = new AgripreneurGetDto();
        agripreneurGetDto.setAgripreneurId(agripreneur.getAgripreneurId());
        agripreneurGetDto.setFullName(agripreneur.getFullName());
        agripreneurGetDto.setIdNo(agripreneur.getIdNo());
        agripreneurGetDto.setVentureName(agripreneur.getVentureName());
        agripreneurGetDto.setState(agripreneur.getState().getName());
        agripreneurGetDto.setDistrict(agripreneur.getDistrict().getName());
        agripreneurGetDto.setSubDistrict(agripreneur.getSubDistrict().getName());
        agripreneurGetDto.setVillage(agripreneur.getVillage().getName());
        agripreneurGetDto.setPincode(agripreneur.getPincode());
        agripreneurGetDto.setMobileNumber(agripreneur.getMobileNumber());
        agripreneurGetDto.setAlternateNumber(agripreneur.getAlternateNumber());
        agripreneurGetDto.setEmail(agripreneur.getEmail());
        agripreneurGetDto.setTraningCenterName(agripreneur.getTraningCenterName());
        agripreneurGetDto.setAnnualTurnover(agripreneur.getAnnualTurnover());
        agripreneurGetDto.setAnnualIncome(agripreneur.getAnnualIncome());
        agripreneurGetDto.setPersonsEmployed(agripreneur.getPersonsEmployed());
        agripreneurGetDto.setFarmersCovered(agripreneur.getFarmersCovered());
        agripreneurGetDto.setVillagesCovered(agripreneur.getVillagesCovered());
        agripreneurGetDto.setBankLoan(agripreneur.getBankLoan());
        agripreneurGetDto.setSubsidy(agripreneur.getSubsidy());
        agripreneurGetDto.setKeywords(agripreneur.getKeywords());
        agripreneurGetDto.setServiceCost(agripreneur.getServiceCost());
        agripreneurGetDto.setServiceCategories(agripreneur.getServiceCategories().stream()
                .map(Category::getName)
                .collect(Collectors.toList()));
        agripreneurGetDto.setImages(agripreneur.getImages().stream()
                .map(image -> {
                    AgripreneurImageDto imageDto = new AgripreneurImageDto();
                    imageDto.setId(image.getId());
                    imageDto.setImagePath(image.getImagePath());
                    try {
                        imageDto.setImageData(encodeFileToBase64(image.getImagePath()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return imageDto;
                }).collect(Collectors.toList()));
        if (agripreneur.getCoordinates() != null) {
            CoordinatesDto coordinatesDto = new CoordinatesDto();
            coordinatesDto.setLatitude(agripreneur.getCoordinates().getLatitude());
            coordinatesDto.setLongitude(agripreneur.getCoordinates().getLongitude());
            agripreneurGetDto.setCoordinates(coordinatesDto);
        }
        return agripreneurGetDto;
    }

    private String encodeFileToBase64(String filePath) throws IOException {
        File file = new File(filePath);
        byte[] fileContent = Files.readAllBytes(file.toPath());
        return Base64.getEncoder().encodeToString(fileContent);
    }

     
    public List<AgripreneursByKeywordDto> getAgripreneursByKeyword(String keyword) {
        List<Agripreneur> agripreneurs = agripreneurRepository.findByKeyword(keyword);
        return agripreneurs.stream()
                .map(this::convertToKeywordDto)
                .collect(Collectors.toList());
    }

    private AgripreneursByKeywordDto convertToKeywordDto(Agripreneur agripreneur) {
        AgripreneursByKeywordDto dto = new AgripreneursByKeywordDto();
        dto.setAgripreneurId(agripreneur.getAgripreneurId());
        dto.setFullName(agripreneur.getFullName());
        dto.setIdNo(agripreneur.getIdNo());
        dto.setVentureName(agripreneur.getVentureName());
        dto.setState(agripreneur.getState().getName());
        dto.setDistrict(agripreneur.getDistrict().getName());
        dto.setSubDistrict(agripreneur.getSubDistrict().getName());
        dto.setVillage(agripreneur.getVillage().getName());
        dto.setPincode(agripreneur.getPincode());
        dto.setMobileNumber(agripreneur.getMobileNumber());
        dto.setAlternateNumber(agripreneur.getAlternateNumber());
        dto.setEmail(agripreneur.getEmail());
        dto.setTraningCenterName(agripreneur.getTraningCenterName());
        dto.setAnnualTurnover(agripreneur.getAnnualTurnover());
        dto.setAnnualIncome(agripreneur.getAnnualIncome());
        dto.setPersonsEmployed(agripreneur.getPersonsEmployed());
        dto.setFarmersCovered(agripreneur.getFarmersCovered());
        dto.setVillagesCovered(agripreneur.getVillagesCovered());
        dto.setBankLoan(agripreneur.getBankLoan());
        dto.setSubsidy(agripreneur.getSubsidy());
        dto.setKeywords(agripreneur.getKeywords());
        dto.setServiceCost(agripreneur.getServiceCost());
        dto.setServiceCategories(agripreneur.getServiceCategories()
                .stream()
                .map(Category::getName)
                .collect(Collectors.toList()));
        dto.setImages(agripreneur.getImages()
                .stream()
                .map(this::convertToImageDto)
                .collect(Collectors.toList()));
        dto.setCoordinates(new CoordinatesDto(agripreneur.getCoordinates().getLatitude(),
                agripreneur.getCoordinates().getLongitude()));
        return dto;
    }

    private AgripreneurImageDto convertToImageDto(AgripreneurImage image) {
        AgripreneurImageDto imageDto = new AgripreneurImageDto();
        imageDto.setId(image.getId());
        imageDto.setImagePath(image.getImagePath());
        return imageDto;
    }
    
    
    
   
   public List<FarmerByDistrictIdDto> getFarmersByDistrictName(String districtName) {
       List<Farmer> farmers = farmerRepository.findByDistrictName(districtName);
       return farmers.stream()
               .map(this::convertToDistrictDto)
               .collect(Collectors.toList());
   }

   private FarmerByDistrictIdDto convertToDistrictDto(Farmer farmer) {
       FarmerByDistrictIdDto dto = new FarmerByDistrictIdDto();
       dto.setFarmerId(farmer.getFarmerId());
       dto.setFullName(farmer.getFullname());
       dto.setEmail(farmer.getEmail());
       dto.setMobileNumber(farmer.getMobileNumber());
       dto.setStateName(farmer.getState().getName());
       dto.setDistrictName(farmer.getDistrict().getName());
       dto.setSubDistrictName(farmer.getSubDistrict().getName());
       dto.setServiceCategories(farmer.getServiceCategories()
               .stream()
               .map(Category::getName)
               .collect(Collectors.toList()));
       return dto;
   }
   
   
     
   
   
   
   public List<AgripreneursByCategoryDto> getAgripreneursByCategory(String categoryName) {
       List<Agripreneur> agripreneurs = agripreneurRepository.findByServiceCategoryName(categoryName);
       return agripreneurs.stream()
               .map(this::convertToCategoryDto)
               .collect(Collectors.toList());
   }

   private AgripreneursByCategoryDto convertToCategoryDto(Agripreneur agripreneur) {
       AgripreneursByCategoryDto dto = new AgripreneursByCategoryDto();
       dto.setAgripreneurId(agripreneur.getAgripreneurId());
       dto.setFullName(agripreneur.getFullName());
       dto.setIdNo(agripreneur.getIdNo());
       dto.setVentureName(agripreneur.getVentureName());
       dto.setState(agripreneur.getState().getName());
       dto.setDistrict(agripreneur.getDistrict().getName());
       dto.setSubDistrict(agripreneur.getSubDistrict().getName());
       dto.setVillage(agripreneur.getVillage().getName());
       dto.setPincode(agripreneur.getPincode());
       dto.setMobileNumber(agripreneur.getMobileNumber());
       dto.setAlternateNumber(agripreneur.getAlternateNumber());
       dto.setEmail(agripreneur.getEmail());
       dto.setTraningCenterName(agripreneur.getTraningCenterName());
       dto.setAnnualTurnover(agripreneur.getAnnualTurnover());
       dto.setAnnualIncome(agripreneur.getAnnualIncome());
       dto.setPersonsEmployed(agripreneur.getPersonsEmployed());
       dto.setFarmersCovered(agripreneur.getFarmersCovered());
       dto.setVillagesCovered(agripreneur.getVillagesCovered());
       dto.setBankLoan(agripreneur.getBankLoan());
       dto.setSubsidy(agripreneur.getSubsidy());
       dto.setKeywords(agripreneur.getKeywords());
       dto.setServiceCost(agripreneur.getServiceCost());
       dto.setServiceCategories(agripreneur.getServiceCategories()
               .stream()
               .map(Category::getName)
               .collect(Collectors.toList()));
       dto.setImages(agripreneur.getImages()
               .stream()
               .map(image -> {
                   AgripreneurImageDto imageDto = new AgripreneurImageDto();
                   imageDto.setId(image.getId());
                   imageDto.setImagePath(image.getImagePath());
                   return imageDto;
               })
               .collect(Collectors.toList()));
       dto.setCoordinates(new CoordinatesDto(agripreneur.getCoordinates().getLatitude(),
               agripreneur.getCoordinates().getLongitude()));
       return dto;
   }
   
   
   
   
   
   
   
   
   public List<AgripreneursByDistrictIdDto> getAgripreneursByDistrictName(String districtName) {
       List<Agripreneur> agripreneurs = agripreneurRepository.findByDistrictName(districtName);
       return agripreneurs.stream()
               .map(this::convertToDto)
               .collect(Collectors.toList());
   }

   private AgripreneursByDistrictIdDto convertToDto(Agripreneur agripreneur) {
       AgripreneursByDistrictIdDto dto = new AgripreneursByDistrictIdDto();
       dto.setAgripreneurId(agripreneur.getAgripreneurId());
       dto.setFullName(agripreneur.getFullName());
       dto.setIdNo(agripreneur.getIdNo());
       dto.setVentureName(agripreneur.getVentureName());
       dto.setState(agripreneur.getState().getName());
       dto.setDistrict(agripreneur.getDistrict().getName());
       dto.setSubDistrict(agripreneur.getSubDistrict().getName());
       dto.setVillage(agripreneur.getVillage().getName());
       dto.setPincode(agripreneur.getPincode());
       dto.setMobileNumber(agripreneur.getMobileNumber());
       dto.setAlternateNumber(agripreneur.getAlternateNumber());
       dto.setEmail(agripreneur.getEmail());
       dto.setTraningCenterName(agripreneur.getTraningCenterName());
       dto.setAnnualTurnover(agripreneur.getAnnualTurnover());
       dto.setAnnualIncome(agripreneur.getAnnualIncome());
       dto.setPersonsEmployed(agripreneur.getPersonsEmployed());
       dto.setFarmersCovered(agripreneur.getFarmersCovered());
       dto.setVillagesCovered(agripreneur.getVillagesCovered());
       dto.setBankLoan(agripreneur.getBankLoan());
       dto.setSubsidy(agripreneur.getSubsidy());
       dto.setKeywords(agripreneur.getKeywords());
       dto.setServiceCost(agripreneur.getServiceCost());
       dto.setServiceCategories(agripreneur.getServiceCategories()
               .stream()
               .map(Category::getName)
               .collect(Collectors.toList()));
       dto.setImages(agripreneur.getImages());
       dto.setCoordinates(new CoordinatesDto(agripreneur.getCoordinates().getLatitude(),
               agripreneur.getCoordinates().getLongitude()));
       return dto;
   }
   
   
   public List<AgripreneursByStateIdDto> getAgripreneursByStateName(String stateName) {
       List<Agripreneur> agripreneurs = agripreneurRepository.findByStateName(stateName);
       return agripreneurs.stream()
               .map(this::convertToStateDto)
               .collect(Collectors.toList());
   }

   private AgripreneursByStateIdDto convertToStateDto(Agripreneur agripreneur) {
       AgripreneursByStateIdDto dto = new AgripreneursByStateIdDto();
       dto.setAgripreneurId(agripreneur.getAgripreneurId());
       dto.setFullName(agripreneur.getFullName());
       dto.setIdNo(agripreneur.getIdNo());
       dto.setVentureName(agripreneur.getVentureName());
       dto.setState(agripreneur.getState().getName());
       dto.setDistrict(agripreneur.getDistrict().getName());
       dto.setSubDistrict(agripreneur.getSubDistrict().getName());
       dto.setVillage(agripreneur.getVillage().getName());
       dto.setPincode(agripreneur.getPincode());
       dto.setMobileNumber(agripreneur.getMobileNumber());
       dto.setAlternateNumber(agripreneur.getAlternateNumber());
       dto.setEmail(agripreneur.getEmail());
       dto.setTraningCenterName(agripreneur.getTraningCenterName());
       dto.setAnnualTurnover(agripreneur.getAnnualTurnover());
       dto.setAnnualIncome(agripreneur.getAnnualIncome());
       dto.setPersonsEmployed(agripreneur.getPersonsEmployed());
       dto.setFarmersCovered(agripreneur.getFarmersCovered());
       dto.setVillagesCovered(agripreneur.getVillagesCovered());
       dto.setBankLoan(agripreneur.getBankLoan());
       dto.setSubsidy(agripreneur.getSubsidy());
       dto.setKeywords(agripreneur.getKeywords());
       dto.setServiceCost(agripreneur.getServiceCost());
       dto.setServiceCategories(agripreneur.getServiceCategories()
               .stream()
               .map(Category::getName)
               .collect(Collectors.toList()));
       dto.setImages(agripreneur.getImages());
       dto.setCoordinates(new CoordinatesDto(agripreneur.getCoordinates().getLatitude(),
               agripreneur.getCoordinates().getLongitude()));
       return dto;
   }
   
   public List<AgripreneursBySubDistrictIdDto> getAgripreneursBySubDistrictName(String subDistrictName) {
       List<Agripreneur> agripreneurs = agripreneurRepository.findBySubDistrictName(subDistrictName);
       return agripreneurs.stream()
               .map(this::convertToSubDistrictDto)
               .collect(Collectors.toList());
   }

   private AgripreneursBySubDistrictIdDto convertToSubDistrictDto(Agripreneur agripreneur) {
       AgripreneursBySubDistrictIdDto dto = new AgripreneursBySubDistrictIdDto();
       dto.setAgripreneurId(agripreneur.getAgripreneurId());
       dto.setFullName(agripreneur.getFullName());
       dto.setIdNo(agripreneur.getIdNo());
       dto.setVentureName(agripreneur.getVentureName());
       dto.setState(agripreneur.getState().getName());
       dto.setDistrict(agripreneur.getDistrict().getName());
       dto.setSubDistrict(agripreneur.getSubDistrict().getName());
       dto.setVillage(agripreneur.getVillage().getName());
       dto.setPincode(agripreneur.getPincode());
       dto.setMobileNumber(agripreneur.getMobileNumber());
       dto.setAlternateNumber(agripreneur.getAlternateNumber());
       dto.setEmail(agripreneur.getEmail());
       dto.setTraningCenterName(agripreneur.getTraningCenterName());
       dto.setAnnualTurnover(agripreneur.getAnnualTurnover());
       dto.setAnnualIncome(agripreneur.getAnnualIncome());
       dto.setPersonsEmployed(agripreneur.getPersonsEmployed());
       dto.setFarmersCovered(agripreneur.getFarmersCovered());
       dto.setVillagesCovered(agripreneur.getVillagesCovered());
       dto.setBankLoan(agripreneur.getBankLoan());
       dto.setSubsidy(agripreneur.getSubsidy());
       dto.setKeywords(agripreneur.getKeywords());
       dto.setServiceCost(agripreneur.getServiceCost());
       dto.setServiceCategories(agripreneur.getServiceCategories()
               .stream()
               .map(Category::getName)
               .collect(Collectors.toList()));
       dto.setImages(agripreneur.getImages());
       dto.setCoordinates(new CoordinatesDto(agripreneur.getCoordinates().getLatitude(),
               agripreneur.getCoordinates().getLongitude()));
       return dto;
   }
   
   

 
   
   
   public List<AgripreneurMapDto> getAgripreneursWithinRadius(double latitude, double longitude, double radius) {
       List<Agripreneur> agripreneurs = agripreneurRepository.findAgripreneursWithinRadius(latitude, longitude, radius);

       return agripreneurs.stream().map(this::convertToDto1).collect(Collectors.toList());
   }

   private AgripreneurMapDto convertToDto1(Agripreneur agripreneur) {
       AgripreneurMapDto dto = new AgripreneurMapDto();
       dto.setAgripreneurId(agripreneur.getAgripreneurId());
       dto.setFullName(agripreneur.getFullName());
       dto.setIdNo(agripreneur.getIdNo());
       dto.setVentureName(agripreneur.getVentureName());
       dto.setState(agripreneur.getState().getName());
       dto.setDistrict(agripreneur.getDistrict().getName());
       dto.setSubDistrict(agripreneur.getSubDistrict().getName());
       dto.setVillage(agripreneur.getVillage() != null ? agripreneur.getVillage().getName() : null);
       dto.setPincode(agripreneur.getPincode());
       dto.setMobileNumber(agripreneur.getMobileNumber());
       dto.setAlternateNumber(agripreneur.getAlternateNumber());
       dto.setEmail(agripreneur.getEmail());
       dto.setTraningCenterName(agripreneur.getTraningCenterName());
       dto.setAnnualTurnover(agripreneur.getAnnualTurnover());
       dto.setAnnualIncome(agripreneur.getAnnualIncome());
       dto.setPersonsEmployed(agripreneur.getPersonsEmployed());
       dto.setFarmersCovered(agripreneur.getFarmersCovered());
       dto.setVillagesCovered(agripreneur.getVillagesCovered());
       dto.setBankLoan(agripreneur.getBankLoan());
       dto.setSubsidy(agripreneur.getSubsidy());
       dto.setKeywords(agripreneur.getKeywords());
       dto.setServiceCost(agripreneur.getServiceCost());
       dto.setServiceCategories(agripreneur.getServiceCategories().stream().map(category -> category.getName()).collect(Collectors.toList()));
       dto.setImages(agripreneur.getImages().stream().map(image -> image.getImagePath()).collect(Collectors.toList()));
       return dto;
   }
   
   
   public List<FarmerQueryDto> getFarmerQueriesByAgripreneur(Long agripreneurId) {
	    // Retrieve the list of FarmerQuery entities for the given agripreneur ID
	    List<FarmerQuery> farmerQueries = queryRepository.findByAgripreneur_AgripreneurId(agripreneurId);

	    // Map the FarmerQuery entities to FarmerQueryDto objects
	    return farmerQueries.stream().map(query -> {
	        
	        // Extract image URLs from the QueryImage entities associated with each query
	        List<String> imageUrls = query.getImages().stream()
	            .map(QueryImage::getImageUrl)
	            .collect(Collectors.toList());
	        
	        // Create a new FarmerQueryDto and map the relevant fields
	        return new FarmerQueryDto(
	            query.getFarmer().getFullname(),   // Farmer's full name
	            query.getQueryText(),              // Query text
	            imageUrls                          // List of image URLs
	        );
	    }).collect(Collectors.toList()); // Collect results into a list
	}

   
   public FarmerQuery respondToQuery(Long agripreneurId, Long queryId, String responseText) {
       // Fetch the query by ID
       FarmerQuery query = queryRepository.findById(queryId)
               .orElseThrow(() -> new IllegalArgumentException("Query not found"));

       // Check if the query belongs to the agripreneur
       if (!query.getAgripreneur().getAgripreneurId().equals(agripreneurId)) {
           throw new IllegalArgumentException("Unauthorized: This query doesn't belong to this agripreneur");
       }

       // Set the response and update status
       query.setResponse(responseText);
       query.setStatus("Responded");

       // Save the updated query
       return queryRepository.save(query);
   }

   // Check if agripreneur has any queries
   public List<FarmerQuery> getQueriesForAgripreneur(Long agripreneurId) {
       // Find all queries for the given agripreneurId
       return queryRepository.findByAgripreneur_AgripreneurId(agripreneurId);
   }
   
   
   public Agripreneur findById(Long agripreneurId) {
       Optional<Agripreneur> agripreneurOpt = agripreneurRepository.findById(agripreneurId);
       return agripreneurOpt.orElse(null); // Return null if not found, or you can throw an exception
   }





   public boolean verifyAgripreneur(Long agripreneurId) {
       Optional<Agripreneur> optionalAgripreneur = agripreneurRepository.findById(agripreneurId);

       if (optionalAgripreneur.isPresent()) {
           Agripreneur agripreneur = optionalAgripreneur.get();
           
           if (!agripreneur.isVerified()) {
               agripreneur.setVerified(true);
               agripreneurRepository.save(agripreneur);
               return true;
           }
       }
       return false;
   }
      
   
   public void saveRefreshToken(String mobileNumber, String refreshToken) {
       Optional<Agripreneur> agripreneurOpt = agripreneurRepository.findByMobileNumber(mobileNumber);
       agripreneurOpt.ifPresent(agripreneur -> {
           agripreneur.setRefreshToken(refreshToken);
           agripreneurRepository.save(agripreneur);
       });
   }

   public String getRefreshToken(String mobileNumber) {
       return agripreneurRepository.findByMobileNumber(mobileNumber)
               .map(Agripreneur::getRefreshToken)
               .orElse(null);
   }

   public boolean validateRefreshToken(String mobileNumber, String refreshToken) {
       String storedRefreshToken = getRefreshToken(mobileNumber);
       return storedRefreshToken != null && storedRefreshToken.equals(refreshToken);
   }
   
   
   public List<FarmerVisitDTO> getVisitedFarmers(Long agripreneurId) {
	    // Fetch all profile visits for the given agripreneurId
	    List<ProfileVisit> visits = profileVisitRepository.findByAgripreneur_AgripreneurId(agripreneurId);

	    // Group by farmer and visit date to filter unique visits per day
	    Map<Long, ProfileVisit> uniqueVisits = visits.stream()
	            .collect(Collectors.toMap(
	                    visit -> visit.getFarmer().getFarmerId(),
	                    visit -> visit,
	                    (existing, replacement) -> existing // In case of duplicates, keep the first visit of the day
	            ));

	    // Convert unique visits to FarmerVisitDTO
	    return uniqueVisits.values().stream()
	            .map(visit -> {
	                Farmer farmer = visit.getFarmer();
	                return new FarmerVisitDTO(
	                        farmer.getFullname(),
	                        farmer.getEmail(),
	                        farmer.getMobileNumber(),
	                        farmer.getState().getName(),
	                        farmer.getDistrict().getName(),
	                        farmer.getSubDistrict().getName()
	                );
	            })
	            .collect(Collectors.toList());
	}   
   
   
}
  /* 
   public List<FarmerVisitDTO> getVisitedFarmers(Long agripreneurId) {
       // Fetching profile visits for the given agripreneurId
       List<ProfileVisit> visits = profileVisitRepository.findByAgripreneur_AgripreneurId(agripreneurId);

       // Mapping each ProfileVisit to a FarmerVisitDTO
       return visits.stream()
               .map(visit -> {
                   var farmer = visit.getFarmer();
                   return new FarmerVisitDTO(
                       farmer.getFullname(),
                       farmer.getEmail(),
                       farmer.getMobileNumber(),
                       farmer.getState().getName(),
                       farmer.getDistrict().getName(),
                       farmer.getSubDistrict().getName()
                   );
               })
               .collect(Collectors.toList());
   }





   public void recordVisit(Long agripreneurId, Long farmerId) {
       LocalDate today = LocalDate.now();

       ProfileVisit visit = profileVisitRepository
               .findByFarmer_FarmerIdAndAgripreneur_AgripreneurIdAndVisitDate(farmerId, agripreneurId, today);

       if (visit != null) {
           visit.setVisitCount(visit.getVisitCount() + 1);
       } else {
           Farmer farmer = farmerRepository.findById(farmerId)
                   .orElseThrow(() -> new IllegalArgumentException("Farmer not found with id: " + farmerId));
           Agripreneur agripreneur = agripreneurRepository.findById(agripreneurId)
                   .orElseThrow(() -> new IllegalArgumentException("Agripreneur not found with id: " + agripreneurId));
           
           visit = new ProfileVisit();
           visit.setFarmer(farmer);
           visit.setAgripreneur(agripreneur);
           visit.setVisitDate(today);
           visit.setVisitCount(1);
       }

       profileVisitRepository.save(visit);
   }

   
} 
   */
  /*  
   private final String uploadDir = "/home/cdachyd/AgripreneurFarmer-App_updated/Backend/Agripreneur-Farmer-App-1/AgripreneurImages/";

   // Register method that saves agripreneur details in an in-progress state
   public String registerAgripreneur(AgripreneurRegistrationDto agripreneurDTO) throws IOException {
	   
       validateRequiredFields(agripreneurDTO);

       // Step 1: Check if the mobile number is already registered
       Optional<Agripreneur> existingAgripreneur = agripreneurRepository.findByMobileNumber(agripreneurDTO.getMobileNumber());
       if (existingAgripreneur.isPresent()) {
           return "Mobile number already registered.";
       }

       // Step 2: Generate and send OTP
       String otp = otpService.generateOtp();
       System.out.println("Sending OTP: " + otp + " to mobile number: " + agripreneurDTO.getMobileNumber());

       // Step 3: Save agripreneur details in "in-progress" state with OTP
       Agripreneur agripreneur = new Agripreneur();
       agripreneur.setFullName(agripreneurDTO.getFullName());
       agripreneur.setEmail(agripreneurDTO.getEmail());
       agripreneur.setMobileNumber(agripreneurDTO.getMobileNumber());
       agripreneur.setVentureName(agripreneurDTO.getVentureName());
       agripreneur.setIdNo(agripreneurDTO.getIdNo());
       agripreneur.setPincode(agripreneurDTO.getPincode());
       agripreneur.setAlternateNumber(agripreneurDTO.getAlternateNumber());
       agripreneur.setTraningCenterName(agripreneurDTO.getTrainingCenterName());
       agripreneur.setAnnualTurnover(agripreneurDTO.getAnnualTurnover());
       agripreneur.setAnnualIncome(agripreneurDTO.getAnnualIncome());
       agripreneur.setPersonsEmployed(agripreneurDTO.getPersonsEmployed());
       agripreneur.setFarmersCovered(agripreneurDTO.getFarmersCovered());
       agripreneur.setVillagesCovered(agripreneurDTO.getVillagesCovered());
       agripreneur.setBankLoan(agripreneurDTO.getBankLoan());
       agripreneur.setSubsidy(agripreneurDTO.getSubsidy());
       agripreneur.setKeywords(agripreneurDTO.getKeywords());
       agripreneur.setServiceCost(agripreneurDTO.getServiceCost());
       agripreneur.setOtp(otp);  // Set the OTP for registration
       agripreneur.setRegistrationCompleted(false); // Mark as in-progress initially

       // Set location and category details
       setLocationAndCategoryDetails(agripreneur, agripreneurDTO);

       // Save the agripreneur entity with OTP
       agripreneurRepository.save(agripreneur);

       // Step 4: Save images
       for (MultipartFile file : agripreneurDTO.getImages()) {
           String filePath = saveFile(file);
           AgripreneurImage agripreneurImage = new AgripreneurImage();
           agripreneurImage.setAgripreneur(agripreneur);
           agripreneurImage.setImagePath(filePath);
           agripreneurImageRepository.save(agripreneurImage);
       }

       return "OTP has been sent to your mobile number for registration.";
   }

   // OTP Verification method that marks the registration as complete
   public String verifyRegistrationOtp(String mobileNumber, String otp) {
       Optional<Agripreneur> agripreneurOptional = agripreneurRepository.findByMobileNumber(mobileNumber);
       if (agripreneurOptional.isPresent()) {
           Agripreneur agripreneur = agripreneurOptional.get();
           if (agripreneur.getOtp().equals(otp)) {
               agripreneur.setOtp(null); // Clear OTP after successful verification
               agripreneur.setRegistrationCompleted(true); // Mark registration as complete
               agripreneurRepository.save(agripreneur);
               return "Registration successful. You can now log in.";
           }
       }
       return "Invalid OTP.";
   }

   private void setLocationAndCategoryDetails(Agripreneur agripreneur, AgripreneurRegistrationDto agripreneurDTO) {
       // Set state
       State state = stateRepository.findById(agripreneurDTO.getStateId())
               .orElseThrow(() -> new ResourceNotFoundException("State not found"));
       agripreneur.setState(state);

       // Set district
       District district = districtRepository.findById(agripreneurDTO.getDistrictId())
               .orElseThrow(() -> new ResourceNotFoundException("District not found"));
       agripreneur.setDistrict(district);

       // Set sub-district
       SubDistrict subDistrict = subDistrictRepository.findById(agripreneurDTO.getSubDistrictId())
               .orElseThrow(() -> new ResourceNotFoundException("Sub-district not found"));
       agripreneur.setSubDistrict(subDistrict);

       // Set village
       Village village = villageRepository.findByNameAndStateAndDistrictAndSubDistrict(
               agripreneurDTO.getVillageName(), state, district, subDistrict);
       if (village == null) {
           village = new Village();
           village.setName(agripreneurDTO.getVillageName());
           village.setState(state);
           village.setDistrict(district);
           village.setSubDistrict(subDistrict);
           villageRepository.save(village);
       }
       agripreneur.setVillage(village);

       // Set coordinates
       Coordinates coordinates = new Coordinates();
       coordinates.setLatitude(agripreneurDTO.getLatitude());
       coordinates.setLongitude(agripreneurDTO.getLongitude());
       coordinates.setAgripreneur(agripreneur);
       agripreneur.setCoordinates(coordinates);

       // Set service categories
       List<Long> categoryIds = agripreneurDTO.getServiceCategoryIds();
       List<Category> categories = categoryRepository.findAllById(categoryIds);
       if (categories.size() != categoryIds.size()) {
           throw new ResourceNotFoundException("One or more categories not found");
       }
       agripreneur.setServiceCategories(categories);
   }

   private String saveFile(MultipartFile file) throws IOException {
       // Ensure the directory exists
       File directory = new File(uploadDir);
       if (!directory.exists()) {
           directory.mkdirs();
       }

       // Save the file
       File dest = new File(directory, file.getOriginalFilename());
       file.transferTo(dest);
       return dest.getAbsolutePath();
   }
   
   private void validateRequiredFields(AgripreneurRegistrationDto dto) {
       if (dto.getFullName() == null || dto.getFullName().isEmpty()) {
           throw new IllegalArgumentException("Full name is required.");
       }
       if (dto.getIdNo() == null || dto.getIdNo().isEmpty()) {
           throw new IllegalArgumentException("ID number is required.");
       }
       if (dto.getVentureName() == null || dto.getVentureName().isEmpty()) {
           throw new IllegalArgumentException("Venture name is required.");
       }
       if (dto.getPincode() == null) {
           throw new IllegalArgumentException("Pincode is required.");
       }
       if (dto.getMobileNumber() == null) {
           throw new IllegalArgumentException("Mobile number is required.");
       }
       if (dto.getEmail() == null || dto.getEmail().isEmpty()) {
           throw new IllegalArgumentException("Email is required.");
       }
       if (dto.getTrainingCenterName() == null || dto.getTrainingCenterName().isEmpty()) {
           throw new IllegalArgumentException("Training center name is required.");
       }
       if (dto.getAnnualTurnover() == null) {
           throw new IllegalArgumentException("Annual turnover is required.");
       }
       if (dto.getAnnualIncome() == null) {
           throw new IllegalArgumentException("Annual income is required.");
       }
       if (dto.getPersonsEmployed() == null) {
           throw new IllegalArgumentException("Number of persons employed is required.");
       }
       if (dto.getFarmersCovered() == null) {
           throw new IllegalArgumentException("Number of farmers covered is required.");
       }
       if (dto.getVillagesCovered() == null) {
           throw new IllegalArgumentException("Number of villages covered is required.");
       }
       if (dto.getBankLoan() == null) {
           throw new IllegalArgumentException("Bank loan details are required.");
       }
       if (dto.getSubsidy() == null) {
           throw new IllegalArgumentException("Subsidy details are required.");
       }
       if (dto.getKeywords() == null || dto.getKeywords().isEmpty()) {
           throw new IllegalArgumentException("Keywords are required.");
       }
       if (dto.getLatitude() == null) {
           throw new IllegalArgumentException("Latitude is required.");
       }
       if (dto.getLongitude() == null) {
           throw new IllegalArgumentException("Longitude is required.");
       }
       
       if (dto.getImages() == null || dto.getImages().isEmpty()) {
           throw new IllegalArgumentException("At least one image is required.");
       }
   }
   */
   /*
   @Transactional
   public String verifyRegistrationOtp(String mobileNumber, String otp) {
       Optional<Agripreneur> agripreneurOptional = agripreneurRepository.findByMobileNumber(mobileNumber);

       if (agripreneurOptional.isPresent()) {
           Agripreneur agripreneur = agripreneurOptional.get();
           if (agripreneur.getOtp() != null && agripreneur.getOtp().equals(otp)) {
               agripreneur.setOtp(null);
               agripreneurRepository.save(agripreneur);
               return "OTP verified successfully";
           } else {
               return "Invalid OTP";
           }
       }
       return "Mobile number not registered";
   }

   @Transactional
   public String loginAgripreneur(String mobileNumber) {
       Optional<Agripreneur> agripreneurOptional = agripreneurRepository.findByMobileNumber(mobileNumber);

       if (agripreneurOptional.isPresent()) {
           String otp = otpService.generateOtp();
           System.out.println("Sending OTP: " + otp + " to mobile number: " + mobileNumber);
           Agripreneur agripreneur = agripreneurOptional.get();
           agripreneur.setOtp(otp);
           agripreneurRepository.save(agripreneur);
           return "OTP has been sent to your mobile number for login.";
       }
       return "Mobile number not registered. Please register first.";
   }   
   @Transactional
   public String verifyLoginOtp(String mobileNumber, String otp) {
       Optional<Agripreneur> agripreneurOptional = agripreneurRepository.findByMobileNumber(mobileNumber);

       if (agripreneurOptional.isPresent()) {
           Agripreneur agripreneur = agripreneurOptional.get();

           if (agripreneur.getOtp() != null && agripreneur.getOtp().equals(otp)) {
               agripreneur.setOtp(null);
               agripreneurRepository.save(agripreneur);
               UserDetails userDetails = userDetailsService.loadUserByUsername(mobileNumber);
               return jwtUtil.generateToken(userDetails.getUsername());
           } else {
               return "Invalid OTP.";
           }
       }
       return "Mobile number not registered. Please register first.";
   }

	}
*/

   
   
   
/*
public AgripreneurGetDto getProfile(Long id) {
    Agripreneur agripreneur = agripreneurRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Agripreneur not found with id: " + id));

    AgripreneurGetDto agripreneurGetDto = new AgripreneurGetDto();
    agripreneurGetDto.setAgripreneurId(agripreneur.getAgripreneurId());
    agripreneurGetDto.setFullName(agripreneur.getFullName());
    agripreneurGetDto.setIdNo(agripreneur.getIdNo());
    agripreneurGetDto.setVentureName(agripreneur.getVentureName());
    agripreneurGetDto.setState(agripreneur.getState().getName());
    agripreneurGetDto.setDistrict(agripreneur.getDistrict().getName());
    agripreneurGetDto.setSubDistrict(agripreneur.getSubDistrict().getName());
    agripreneurGetDto.setVillage(agripreneur.getVillage().getName());
    agripreneurGetDto.setPincode(agripreneur.getPincode());
    agripreneurGetDto.setMobileNumber(agripreneur.getMobileNumber());
    agripreneurGetDto.setAlternateNumber(agripreneur.getAlternateNumber());
    agripreneurGetDto.setEmail(agripreneur.getEmail());
    agripreneurGetDto.setTraningCenterName(agripreneur.getTraningCenterName());
    agripreneurGetDto.setAnnualTurnover(agripreneur.getAnnualTurnover());
    agripreneurGetDto.setAnnualIncome(agripreneur.getAnnualIncome());
    agripreneurGetDto.setPersonsEmployed(agripreneur.getPersonsEmployed());
    agripreneurGetDto.setFarmersCovered(agripreneur.getFarmersCovered());
    agripreneurGetDto.setVillagesCovered(agripreneur.getVillagesCovered());
    agripreneurGetDto.setBankLoan(agripreneur.getBankLoan());
    agripreneurGetDto.setSubsidy(agripreneur.getSubsidy());
    agripreneurGetDto.setKeywords(agripreneur.getKeywords());
    agripreneurGetDto.setServiceCost(agripreneur.getServiceCost());
    agripreneurGetDto.setServiceCategories(agripreneur.getServiceCategories().stream()
            .map(category -> category.getName())
            .collect(Collectors.toList()));
    agripreneurGetDto.setImages(agripreneur.getImages().stream()
            .map(image -> {
                AgripreneurImageDto imageDto = new AgripreneurImageDto();
                imageDto.setId(image.getId());
                imageDto.setImagePath(image.getImagePath());
                return imageDto;
            }).collect(Collectors.toList()));

    if (agripreneur.getCoordinates() != null) {
        CoordinatesDto coordinatesDto = new CoordinatesDto();
        coordinatesDto.setLatitude(agripreneur.getCoordinates().getLatitude());
        coordinatesDto.setLongitude(agripreneur.getCoordinates().getLongitude());
        agripreneurGetDto.setCoordinates(coordinatesDto);
    }

    return agripreneurGetDto;
}
*/
/*
public List<AgripreneursByDistrictIdDto> getAgripreneursByDistrictId(Long districtId) {
    List<Agripreneur> agripreneurs = agripreneurRepository.findByDistrictId(districtId);
    return agripreneurs.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
}

private AgripreneursByDistrictIdDto convertToDto(Agripreneur agripreneur) {
	   AgripreneursByDistrictIdDto dto = new AgripreneursByDistrictIdDto();
    dto.setAgripreneurId(agripreneur.getAgripreneurId());
    dto.setFullName(agripreneur.getFullName());
    dto.setIdNo(agripreneur.getIdNo());
    dto.setVentureName(agripreneur.getVentureName());
    dto.setState(agripreneur.getState().getName());
    dto.setDistrict(agripreneur.getDistrict().getName());
    dto.setSubDistrict(agripreneur.getSubDistrict().getName());
    dto.setVillage(agripreneur.getVillage().getName());
    dto.setPincode(agripreneur.getPincode());
    dto.setMobileNumber(agripreneur.getMobileNumber());
    dto.setAlternateNumber(agripreneur.getAlternateNumber());
    dto.setEmail(agripreneur.getEmail());
    dto.setTraningCenterName(agripreneur.getTraningCenterName());
    dto.setAnnualTurnover(agripreneur.getAnnualTurnover());
    dto.setAnnualIncome(agripreneur.getAnnualIncome());
    dto.setPersonsEmployed(agripreneur.getPersonsEmployed());
    dto.setFarmersCovered(agripreneur.getFarmersCovered());
    dto.setVillagesCovered(agripreneur.getVillagesCovered());
    dto.setBankLoan(agripreneur.getBankLoan());
    dto.setSubsidy(agripreneur.getSubsidy());
    dto.setKeywords(agripreneur.getKeywords());
    dto.setServiceCost(agripreneur.getServiceCost());
    dto.setServiceCategories(agripreneur.getServiceCategories()
            .stream()
            .map(Category::getName)
            .collect(Collectors.toList()));
    dto.setImages(agripreneur.getImages());
    dto.setCoordinates(new CoordinatesDto(agripreneur.getCoordinates().getLatitude(),
            agripreneur.getCoordinates().getLongitude()));
    return dto;
} 
*/
/*
//authunticate-verify-login part
private final Map<Long, String> tokenStorage = new HashMap<>();
private final Map<Long, String> otpStorage = new HashMap<>();

public void sendOtp(Long mobileNumber) {
    String otp = String.format("%06d", new Random().nextInt(999999));
    otpStorage.put(mobileNumber, otp);
    // Use Twilio service to send OTP
    System.out.println("Sending OTP " + otp + " to mobile number " + mobileNumber);
}

public boolean verifyOtp(Long mobileNumber, String otp) {
    return otpStorage.containsKey(mobileNumber) && otpStorage.get(mobileNumber).equals(otp);
}

public String login(Long mobileNumber, String otp) {
    if (verifyOtp(mobileNumber, otp)) {
        String token = generateToken();
        tokenStorage.put(mobileNumber, token);
        return token;
    } else {
        return null;
    }
}

private String generateToken() {
    // Generate a simple token (in a real application, use JWT or a similar token mechanism)
    return String.valueOf(new Random().nextInt(99999900));
}
*/


/*

// Agripreneur Registration
public String registerAgripreneur(AgripreneurRegistrationDto agripreneurRegistrationDto) throws IOException {
    Optional<Agripreneur> existingAgripreneur = agripreneurRepository.findByMobileNumber(agripreneurRegistrationDto.getMobileNumber());
    if (existingAgripreneur.isPresent()) {
        return "Mobile number already registered.";
    }

    // Save Agripreneur details
    Agripreneur agripreneur = saveAgripreneur(agripreneurRegistrationDto);

    // Generate and send OTP
    String otp = otpService.generateOtp();
    System.out.println("Sending OTP: " + otp + " to mobile number: " + agripreneur.getMobileNumber());

    agripreneur.setOtp(otp);
    agripreneurRepository.save(agripreneur);

    return "OTP has been sent to your mobile number for registration.";
}

// OTP Verification for Registration
public String verifyRegistrationOtp(String mobileNumber, String otp) {
    Optional<Agripreneur> agripreneurOptional = agripreneurRepository.findByMobileNumber(mobileNumber);
    if (agripreneurOptional.isPresent()) {
        Agripreneur agripreneur = agripreneurOptional.get();
        if (agripreneur.getOtp().equals(otp)) {
            agripreneur.setOtp(null); // Clear OTP after successful verification
            agripreneurRepository.save(agripreneur);
            return "Registration successful. You can now log in.";
        }
    }
    return "Invalid OTP.";
}

// Login Request
public String loginAgripreneur(String mobileNumber) {
    Optional<Agripreneur> agripreneurOptional = agripreneurRepository.findByMobileNumber(mobileNumber);
    if (agripreneurOptional.isPresent()) {
        String otp = otpService.generateOtp();
        System.out.println("Sending OTP: " + otp + " to mobile number: " + mobileNumber);
        Agripreneur agripreneur = agripreneurOptional.get();
        agripreneur.setOtp(otp);
        agripreneurRepository.save(agripreneur);
        return "OTP has been sent to your mobile number for login.";
    }
    return "Mobile number not registered.";
}

// OTP Verification for Login
public String verifyLoginOtp(String mobileNumber, String otp) {
    Optional<Agripreneur> agripreneurOptional = agripreneurRepository.findByMobileNumber(mobileNumber);
    if (agripreneurOptional.isPresent()) {
        Agripreneur agripreneur = agripreneurOptional.get();
        if (agripreneur.getOtp().equals(otp)) {
            agripreneur.setOtp(null); // Clear OTP after successful verification
            agripreneurRepository.save(agripreneur);
            return "Login successful.";
        }
    }
    return "Invalid OTP.";
}
  // Other service methods...

public Agripreneur saveAgripreneur(AgripreneurRegistrationDto agripreneurRegistrationDto) throws IOException {
    // Validate required fields
    validateRequiredFields(agripreneurRegistrationDto);

    Agripreneur agripreneur = new Agripreneur();
    agripreneur.setFullName(agripreneurRegistrationDto.getFullName());
    agripreneur.setIdNo(agripreneurRegistrationDto.getIdNo());
    agripreneur.setVentureName(agripreneurRegistrationDto.getVentureName());
    agripreneur.setPincode(agripreneurRegistrationDto.getPincode());
    agripreneur.setMobileNumber(agripreneurRegistrationDto.getMobileNumber());
    agripreneur.setAlternateNumber(agripreneurRegistrationDto.getAlternateNumber());
    agripreneur.setEmail(agripreneurRegistrationDto.getEmail());
    agripreneur.setTraningCenterName(agripreneurRegistrationDto.getTrainingCenterName());
    agripreneur.setAnnualTurnover(agripreneurRegistrationDto.getAnnualTurnover());
    agripreneur.setAnnualIncome(agripreneurRegistrationDto.getAnnualIncome());
    agripreneur.setPersonsEmployed(agripreneurRegistrationDto.getPersonsEmployed());
    agripreneur.setFarmersCovered(agripreneurRegistrationDto.getFarmersCovered());
    agripreneur.setVillagesCovered(agripreneurRegistrationDto.getVillagesCovered());
    agripreneur.setBankLoan(agripreneurRegistrationDto.getBankLoan());
    agripreneur.setSubsidy(agripreneurRegistrationDto.getSubsidy());
    agripreneur.setKeywords(agripreneurRegistrationDto.getKeywords());
    agripreneur.setServiceCost(agripreneurRegistrationDto.getServiceCost());

    // Check and set service categories
    List<Long> categoryIds = agripreneurRegistrationDto.getServiceCategoryIds();
    List<Category> categories = categoryRepository.findAllById(categoryIds);
    if (categories.size() != categoryIds.size()) {
        throw new ResourceNotFoundException("One or more categories not found");
    }
    agripreneur.setServiceCategories(categories);

    // Check and set state
    State state = stateRepository.findById(agripreneurRegistrationDto.getStateId())
            .orElseThrow(() -> new ResourceNotFoundException("State not found"));
    agripreneur.setState(state);

    // Check and set district
    District district = districtRepository.findById(agripreneurRegistrationDto.getDistrictId())
            .orElseThrow(() -> new ResourceNotFoundException("District not found"));
    agripreneur.setDistrict(district);

    // Check and set sub-district
    SubDistrict subDistrict = subDistrictRepository.findById(agripreneurRegistrationDto.getSubDistrictId())
            .orElseThrow(() -> new ResourceNotFoundException("Sub-district not found"));
    agripreneur.setSubDistrict(subDistrict);

    // Check and set village
    Village village = villageRepository.findByNameAndStateAndDistrictAndSubDistrict(
            agripreneurRegistrationDto.getVillageName(), state, district, subDistrict);
    if (village == null) {
        village = new Village();
        village.setName(agripreneurRegistrationDto.getVillageName());
        village.setState(state);
        village.setDistrict(district);
        village.setSubDistrict(subDistrict);
        villageRepository.save(village);
    }
    agripreneur.setVillage(village);

    // Set coordinates
    Coordinates coordinates = new Coordinates();
    coordinates.setLatitude(agripreneurRegistrationDto.getLatitude());
    coordinates.setLongitude(agripreneurRegistrationDto.getLongitude());
    coordinates.setAgripreneur(agripreneur);
    agripreneur.setCoordinates(coordinates);

    // Save agripreneur
    agripreneurRepository.save(agripreneur);

    // Save images
    for (MultipartFile file : agripreneurRegistrationDto.getImages()) {
        String filePath = saveFile(file);
        AgripreneurImage agripreneurImage = new AgripreneurImage();
        agripreneurImage.setAgripreneur(agripreneur);
        agripreneurImage.setImagePath(filePath);
        agripreneurImageRepository.save(agripreneurImage);
    }

    return agripreneur;
}

private void validateRequiredFields(AgripreneurRegistrationDto dto) {
    if (dto.getFullName() == null || dto.getFullName().isEmpty()) {
        throw new IllegalArgumentException("Full name is required.");
    }
    if (dto.getIdNo() == null || dto.getIdNo().isEmpty()) {
        throw new IllegalArgumentException("ID number is required.");
    }
    if (dto.getVentureName() == null || dto.getVentureName().isEmpty()) {
        throw new IllegalArgumentException("Venture name is required.");
    }
    if (dto.getPincode() == null) {
        throw new IllegalArgumentException("Pincode is required.");
    }
    if (dto.getMobileNumber() == null) {
        throw new IllegalArgumentException("Mobile number is required.");
    }
    if (dto.getEmail() == null || dto.getEmail().isEmpty()) {
        throw new IllegalArgumentException("Email is required.");
    }
    if (dto.getTrainingCenterName() == null || dto.getTrainingCenterName().isEmpty()) {
        throw new IllegalArgumentException("Training center name is required.");
    }
    if (dto.getAnnualTurnover() == null) {
        throw new IllegalArgumentException("Annual turnover is required.");
    }
    if (dto.getAnnualIncome() == null) {
        throw new IllegalArgumentException("Annual income is required.");
    }
    if (dto.getPersonsEmployed() == null) {
        throw new IllegalArgumentException("Number of persons employed is required.");
    }
    if (dto.getFarmersCovered() == null) {
        throw new IllegalArgumentException("Number of farmers covered is required.");
    }
    if (dto.getVillagesCovered() == null) {
        throw new IllegalArgumentException("Number of villages covered is required.");
    }
    if (dto.getBankLoan() == null) {
        throw new IllegalArgumentException("Bank loan details are required.");
    }
    if (dto.getSubsidy() == null) {
        throw new IllegalArgumentException("Subsidy details are required.");
    }
    if (dto.getKeywords() == null || dto.getKeywords().isEmpty()) {
        throw new IllegalArgumentException("Keywords are required.");
    }
    if (dto.getLatitude() == null) {
        throw new IllegalArgumentException("Latitude is required.");
    }
    if (dto.getLongitude() == null) {
        throw new IllegalArgumentException("Longitude is required.");
    }
    
    if (dto.getImages() == null || dto.getImages().isEmpty()) {
        throw new IllegalArgumentException("At least one image is required.");
    }
}


public String saveFile(MultipartFile file) throws IOException {
    // Ensure the directory exists
    File directory = new File(uploadDir);
    if (!directory.exists()) {
        directory.mkdirs();
    }

    // Save the file
    File dest = new File(directory, file.getOriginalFilename());
    file.transferTo(dest);
    return dest.getAbsolutePath();
}   
*/


/*  
  
  
  
public String registerAgripreneur(AgripreneurRegistrationDto agripreneurDTO) throws IOException {
    Optional<Agripreneur> existingAgripreneur = agripreneurRepository.findByMobileNumber(agripreneurDTO.getMobileNumber());
    if (existingAgripreneur.isPresent()) {
        return "Mobile number already registered.";
    }

    Agripreneur agripreneur = new Agripreneur();
    agripreneur.setFullName(agripreneurDTO.getFullName());
    agripreneur.setEmail(agripreneurDTO.getEmail());
    agripreneur.setMobileNumber(agripreneurDTO.getMobileNumber());
    agripreneur.setVentureName(agripreneurDTO.getVentureName());
    agripreneur.setIdNo(agripreneurDTO.getIdNo());
    
    // Set additional fields based on the DTO...

    
    agripreneur.setPincode(agripreneurDTO.getPincode());
    agripreneur.setAlternateNumber(agripreneurDTO.getAlternateNumber());
    agripreneur.setEmail(agripreneurDTO.getEmail());
    agripreneur.setTraningCenterName(agripreneurDTO.getTrainingCenterName());
    agripreneur.setAnnualTurnover(agripreneurDTO.getAnnualTurnover());
    agripreneur.setAnnualIncome(agripreneurDTO.getAnnualIncome());
    agripreneur.setPersonsEmployed(agripreneurDTO.getPersonsEmployed());
    agripreneur.setFarmersCovered(agripreneurDTO.getFarmersCovered());
    agripreneur.setVillagesCovered(agripreneurDTO.getVillagesCovered());
    agripreneur.setBankLoan(agripreneurDTO.getBankLoan());
    agripreneur.setSubsidy(agripreneurDTO.getSubsidy());
    agripreneur.setKeywords(agripreneurDTO.getKeywords());
    agripreneur.setServiceCost(agripreneurDTO.getServiceCost());

    

    // Check and set state
    State state = stateRepository.findById(agripreneurDTO.getStateId())
            .orElseThrow(() -> new ResourceNotFoundException("State not found"));
    agripreneur.setState(state);

    // Check and set district
    District district = districtRepository.findById(agripreneurDTO.getDistrictId())
            .orElseThrow(() -> new ResourceNotFoundException("District not found"));
    agripreneur.setDistrict(district);

    // Check and set sub-district
    SubDistrict subDistrict = subDistrictRepository.findById(agripreneurDTO.getSubDistrictId())
            .orElseThrow(() -> new ResourceNotFoundException("Sub-district not found"));
    agripreneur.setSubDistrict(subDistrict);


    // Check and set service categories
    List<Long> categoryIds = agripreneurDTO.getServiceCategoryIds();
    List<Category> categories = categoryRepository.findAllById(categoryIds);
    if (categories.size() != categoryIds.size()) {
        throw new ResourceNotFoundException("One or more categories not found");
    }
    agripreneur.setServiceCategories(categories);

    

    // Check and set village
    Village village = villageRepository.findByNameAndStateAndDistrictAndSubDistrict(
 		   agripreneurDTO.getVillageName(), state, district, subDistrict);
    if (village == null) {
        village = new Village();
        village.setName(agripreneurDTO.getVillageName());
        village.setState(state);
        village.setDistrict(district);
        village.setSubDistrict(subDistrict);
        villageRepository.save(village);
    }
    agripreneur.setVillage(village);

    // Set coordinates
    Coordinates coordinates = new Coordinates();
    coordinates.setLatitude(agripreneurDTO.getLatitude());
    coordinates.setLongitude(agripreneurDTO.getLongitude());
    coordinates.setAgripreneur(agripreneur);
    agripreneur.setCoordinates(coordinates);

    // Save agripreneur
    agripreneurRepository.save(agripreneur);

    // Save images
    for (MultipartFile file : agripreneurDTO.getImages()) {
        String filePath = saveFile(file);
        AgripreneurImage agripreneurImage = new AgripreneurImage();
        agripreneurImage.setAgripreneur(agripreneur);
        agripreneurImage.setImagePath(filePath);
        agripreneurImageRepository.save(agripreneurImage);
    }

    // Generate and send OTP
   String otp = otpService.generateOtp();
   System.out.println("Sending OTP: " + otp + " to mobile number: " + agripreneur.getMobileNumber());


    agripreneur.setOtp(otp);
    agripreneurRepository.save(agripreneur);

    return "OTP has been sent to your mobile number for registration.";
}


public String saveFile(MultipartFile file) throws IOException {
    // Ensure the directory exists
    File directory = new File(uploadDir);
    if (!directory.exists()) {
        directory.mkdirs();
    }

    // Save the file
    File dest = new File(directory, file.getOriginalFilename());
    file.transferTo(dest);
    return dest.getAbsolutePath();
}   


// Verify OTP for registration
public String verifyRegistrationOtp(String mobileNumber, String otp) {
    Optional<Agripreneur> agripreneurOptional = agripreneurRepository.findByMobileNumber(mobileNumber);
    if (agripreneurOptional.isPresent()) {
        Agripreneur agripreneur = agripreneurOptional.get();
        if (agripreneur.getOtp().equals(otp)) {
            agripreneur.setOtp(null); // Clear OTP after successful verification
            agripreneurRepository.save(agripreneur);
            return "Registration successful. You can now log in.";
        }
    }
    return "Invalid OTP.";
}

// Send OTP for login
public String loginAgripreneur(String mobileNumber) {
    Optional<Agripreneur> agripreneurOptional = agripreneurRepository.findByMobileNumber(mobileNumber);
    if (agripreneurOptional.isPresent()) {
        String otp = otpService.generateOtp();
        System.out.println("Sending OTP: " + otp + " to mobile number: " + mobileNumber);
        Agripreneur agripreneur = agripreneurOptional.get();
        agripreneur.setOtp(otp);
        agripreneurRepository.save(agripreneur);
        return "OTP has been sent to your mobile number for login.";
    }
    return "Mobile number not registered.";
}

// Verify OTP for login
public String verifyLoginOtp(String mobileNumber, String otp) {
    Optional<Agripreneur> agripreneurOptional = agripreneurRepository.findByMobileNumber(mobileNumber);
    if (agripreneurOptional.isPresent()) {
        Agripreneur agripreneur = agripreneurOptional.get();
        if (agripreneur.getOtp().equals(otp)) {
            agripreneur.setOtp(null); // Clear OTP after successful verification
            agripreneurRepository.save(agripreneur);
            return "Login successful.";
        }
    }
    return "Invalid OTP.";
}


*/

/*  
public Agripreneur saveAgripreneur(AgripreneurRegistrationDto agripreneurRegistrationDto) throws IOException {
    // Validate that all required fields are provided
    validateRequiredFields(agripreneurRegistrationDto);

    Agripreneur agripreneur = new Agripreneur();
    agripreneur.setFullName(agripreneurRegistrationDto.getFullName());
    agripreneur.setIdNo(agripreneurRegistrationDto.getIdNo());
    agripreneur.setVentureName(agripreneurRegistrationDto.getVentureName());
    agripreneur.setPincode(agripreneurRegistrationDto.getPincode());
    agripreneur.setMobileNumber(agripreneurRegistrationDto.getMobileNumber());
    agripreneur.setAlternateNumber(agripreneurRegistrationDto.getAlternateNumber());
    agripreneur.setEmail(agripreneurRegistrationDto.getEmail());
    agripreneur.setTraningCenterName(agripreneurRegistrationDto.getTrainingCenterName());
    agripreneur.setAnnualTurnover(agripreneurRegistrationDto.getAnnualTurnover());
    agripreneur.setAnnualIncome(agripreneurRegistrationDto.getAnnualIncome());
    agripreneur.setPersonsEmployed(agripreneurRegistrationDto.getPersonsEmployed());
    agripreneur.setFarmersCovered(agripreneurRegistrationDto.getFarmersCovered());
    agripreneur.setVillagesCovered(agripreneurRegistrationDto.getVillagesCovered());
    agripreneur.setBankLoan(agripreneurRegistrationDto.getBankLoan());
    agripreneur.setSubsidy(agripreneurRegistrationDto.getSubsidy());
    agripreneur.setKeywords(agripreneurRegistrationDto.getKeywords());
    agripreneur.setServiceCost(agripreneurRegistrationDto.getServiceCost());

    // Check and set service categories
    List<Long> categoryIds = agripreneurRegistrationDto.getServiceCategoryIds();
    List<Category> categories = categoryRepository.findAllById(categoryIds);
    if (categories.size() != categoryIds.size()) {
        throw new ResourceNotFoundException("One or more categories not found");
    }
    agripreneur.setServiceCategories(categories);

    // Check and set state
    State state = stateRepository.findById(agripreneurRegistrationDto.getStateId())
            .orElseThrow(() -> new ResourceNotFoundException("State not found"));
    agripreneur.setState(state);

    // Check and set district
    District district = districtRepository.findById(agripreneurRegistrationDto.getDistrictId())
            .orElseThrow(() -> new ResourceNotFoundException("District not found"));
    agripreneur.setDistrict(district);

    // Check and set sub-district
    SubDistrict subDistrict = subDistrictRepository.findById(agripreneurRegistrationDto.getSubDistrictId())
            .orElseThrow(() -> new ResourceNotFoundException("Sub-district not found"));
    agripreneur.setSubDistrict(subDistrict);

    // Check and set village
    Village village = villageRepository.findByNameAndStateAndDistrictAndSubDistrict(
            agripreneurRegistrationDto.getVillageName(), state, district, subDistrict);
    if (village == null) {
        village = new Village();
        village.setName(agripreneurRegistrationDto.getVillageName());
        village.setState(state);
        village.setDistrict(district);
        village.setSubDistrict(subDistrict);
        villageRepository.save(village);
    }
    agripreneur.setVillage(village);

    // Set coordinates
    Coordinates coordinates = new Coordinates();
    coordinates.setLatitude(agripreneurRegistrationDto.getLatitude());
    coordinates.setLongitude(agripreneurRegistrationDto.getLongitude());
    coordinates.setAgripreneur(agripreneur);
    agripreneur.setCoordinates(coordinates);

    // Save agripreneur
    agripreneurRepository.save(agripreneur);

    // Save images
    for (MultipartFile file : agripreneurRegistrationDto.getImages()) {
        String filePath = saveFile(file);
        AgripreneurImage agripreneurImage = new AgripreneurImage();
        agripreneurImage.setAgripreneur(agripreneur);
        agripreneurImage.setImagePath(filePath);
        agripreneurImageRepository.save(agripreneurImage);
    }

    return agripreneur;
}

private void validateRequiredFields(AgripreneurRegistrationDto dto) {
    if (dto.getFullName() == null || dto.getFullName().isEmpty()) {
        throw new IllegalArgumentException("Full name is required.");
    }
    if (dto.getIdNo() == null || dto.getIdNo().isEmpty()) {
        throw new IllegalArgumentException("ID number is required.");
    }
    if (dto.getVentureName() == null || dto.getVentureName().isEmpty()) {
        throw new IllegalArgumentException("Venture name is required.");
    }
    if (dto.getPincode() == null) {
        throw new IllegalArgumentException("Pincode is required.");
    }
    if (dto.getMobileNumber() == null) {
        throw new IllegalArgumentException("Mobile number is required.");
    }
    if (dto.getEmail() == null || dto.getEmail().isEmpty()) {
        throw new IllegalArgumentException("Email is required.");
    }
    if (dto.getTrainingCenterName() == null || dto.getTrainingCenterName().isEmpty()) {
        throw new IllegalArgumentException("Training center name is required.");
    }
    if (dto.getAnnualTurnover() == null) {
        throw new IllegalArgumentException("Annual turnover is required.");
    }
    if (dto.getAnnualIncome() == null) {
        throw new IllegalArgumentException("Annual income is required.");
    }
    if (dto.getPersonsEmployed() == null) {
        throw new IllegalArgumentException("Number of persons employed is required.");
    }
    if (dto.getFarmersCovered() == null) {
        throw new IllegalArgumentException("Number of farmers covered is required.");
    }
    if (dto.getVillagesCovered() == null) {
        throw new IllegalArgumentException("Number of villages covered is required.");
    }
    if (dto.getBankLoan() == null) {
        throw new IllegalArgumentException("Bank loan details are required.");
    }
    if (dto.getSubsidy() == null) {
        throw new IllegalArgumentException("Subsidy details are required.");
    }
    if (dto.getKeywords() == null || dto.getKeywords().isEmpty()) {
        throw new IllegalArgumentException("Keywords are required.");
    }
    if (dto.getLatitude() == null) {
        throw new IllegalArgumentException("Latitude is required.");
    }
    if (dto.getLongitude() == null) {
        throw new IllegalArgumentException("Longitude is required.");
    }
    if (dto.getImages() == null || dto.getImages().isEmpty()) {
        throw new IllegalArgumentException("At least one image is required.");
    }
}

public String saveFile(MultipartFile file) {
    String filePath = ""; // Logic to determine the file path
    try {
        // Assuming you have a directory to save the files
        String directory = "uploads/";
        filePath = directory + file.getOriginalFilename();
        File dest = new File(filePath);
        file.transferTo(dest); // Save the file

    } catch (IOException e) {
        e.printStackTrace();
        // Handle the exception
    }
 
    return filePath; // Return the file path
    
    
public String saveFile(MultipartFile file) throws IOException {
    String directory = "AgripreneuImages/";
    File dest = new File(directory + file.getOriginalFilename());
    file.transferTo(dest);
    return dest.getAbsolutePath();
}

*/ /*
   //current run

@Value("${file.upload-dir}")
private String uploadDir;

// Other service methods...

public Agripreneur saveAgripreneur(AgripreneurRegistrationDto agripreneurRegistrationDto) throws IOException {
    // Validate required fields
    validateRequiredFields(agripreneurRegistrationDto);

    Agripreneur agripreneur = new Agripreneur();
    agripreneur.setFullName(agripreneurRegistrationDto.getFullName());
    agripreneur.setIdNo(agripreneurRegistrationDto.getIdNo());
    agripreneur.setVentureName(agripreneurRegistrationDto.getVentureName());
    agripreneur.setPincode(agripreneurRegistrationDto.getPincode());
    agripreneur.setMobileNumber(agripreneurRegistrationDto.getMobileNumber());
    agripreneur.setAlternateNumber(agripreneurRegistrationDto.getAlternateNumber());
    agripreneur.setEmail(agripreneurRegistrationDto.getEmail());
    agripreneur.setTraningCenterName(agripreneurRegistrationDto.getTrainingCenterName());
    agripreneur.setAnnualTurnover(agripreneurRegistrationDto.getAnnualTurnover());
    agripreneur.setAnnualIncome(agripreneurRegistrationDto.getAnnualIncome());
    agripreneur.setPersonsEmployed(agripreneurRegistrationDto.getPersonsEmployed());
    agripreneur.setFarmersCovered(agripreneurRegistrationDto.getFarmersCovered());
    agripreneur.setVillagesCovered(agripreneurRegistrationDto.getVillagesCovered());
    agripreneur.setBankLoan(agripreneurRegistrationDto.getBankLoan());
    agripreneur.setSubsidy(agripreneurRegistrationDto.getSubsidy());
    agripreneur.setKeywords(agripreneurRegistrationDto.getKeywords());
    agripreneur.setServiceCost(agripreneurRegistrationDto.getServiceCost());

    // Check and set service categories
    List<Long> categoryIds = agripreneurRegistrationDto.getServiceCategoryIds();
    List<Category> categories = categoryRepository.findAllById(categoryIds);
    if (categories.size() != categoryIds.size()) {
        throw new ResourceNotFoundException("One or more categories not found");
    }
    agripreneur.setServiceCategories(categories);

    // Check and set state
    State state = stateRepository.findById(agripreneurRegistrationDto.getStateId())
            .orElseThrow(() -> new ResourceNotFoundException("State not found"));
    agripreneur.setState(state);

    // Check and set district
    District district = districtRepository.findById(agripreneurRegistrationDto.getDistrictId())
            .orElseThrow(() -> new ResourceNotFoundException("District not found"));
    agripreneur.setDistrict(district);

    // Check and set sub-district
    SubDistrict subDistrict = subDistrictRepository.findById(agripreneurRegistrationDto.getSubDistrictId())
            .orElseThrow(() -> new ResourceNotFoundException("Sub-district not found"));
    agripreneur.setSubDistrict(subDistrict);

    // Check and set village
    Village village = villageRepository.findByNameAndStateAndDistrictAndSubDistrict(
            agripreneurRegistrationDto.getVillageName(), state, district, subDistrict);
    if (village == null) {
        village = new Village();
        village.setName(agripreneurRegistrationDto.getVillageName());
        village.setState(state);
        village.setDistrict(district);
        village.setSubDistrict(subDistrict);
        villageRepository.save(village);
    }
    agripreneur.setVillage(village);

    // Set coordinates
    Coordinates coordinates = new Coordinates();
    coordinates.setLatitude(agripreneurRegistrationDto.getLatitude());
    coordinates.setLongitude(agripreneurRegistrationDto.getLongitude());
    coordinates.setAgripreneur(agripreneur);
    agripreneur.setCoordinates(coordinates);

    // Save agripreneur
    agripreneurRepository.save(agripreneur);

    // Save images
    for (MultipartFile file : agripreneurRegistrationDto.getImages()) {
        String filePath = saveFile(file);
        AgripreneurImage agripreneurImage = new AgripreneurImage();
        agripreneurImage.setAgripreneur(agripreneur);
        agripreneurImage.setImagePath(filePath);
        agripreneurImageRepository.save(agripreneurImage);
    }

    return agripreneur;
}

private void validateRequiredFields(AgripreneurRegistrationDto dto) {
    if (dto.getFullName() == null || dto.getFullName().isEmpty()) {
        throw new IllegalArgumentException("Full name is required.");
    }
    if (dto.getIdNo() == null || dto.getIdNo().isEmpty()) {
        throw new IllegalArgumentException("ID number is required.");
    }
    if (dto.getVentureName() == null || dto.getVentureName().isEmpty()) {
        throw new IllegalArgumentException("Venture name is required.");
    }
    if (dto.getPincode() == null) {
        throw new IllegalArgumentException("Pincode is required.");
    }
    if (dto.getMobileNumber() == null) {
        throw new IllegalArgumentException("Mobile number is required.");
    }
    if (dto.getEmail() == null || dto.getEmail().isEmpty()) {
        throw new IllegalArgumentException("Email is required.");
    }
    if (dto.getTrainingCenterName() == null || dto.getTrainingCenterName().isEmpty()) {
        throw new IllegalArgumentException("Training center name is required.");
    }
    if (dto.getAnnualTurnover() == null) {
        throw new IllegalArgumentException("Annual turnover is required.");
    }
    if (dto.getAnnualIncome() == null) {
        throw new IllegalArgumentException("Annual income is required.");
    }
    if (dto.getPersonsEmployed() == null) {
        throw new IllegalArgumentException("Number of persons employed is required.");
    }
    if (dto.getFarmersCovered() == null) {
        throw new IllegalArgumentException("Number of farmers covered is required.");
    }
    if (dto.getVillagesCovered() == null) {
        throw new IllegalArgumentException("Number of villages covered is required.");
    }
    if (dto.getBankLoan() == null) {
        throw new IllegalArgumentException("Bank loan details are required.");
    }
    if (dto.getSubsidy() == null) {
        throw new IllegalArgumentException("Subsidy details are required.");
    }
    if (dto.getKeywords() == null || dto.getKeywords().isEmpty()) {
        throw new IllegalArgumentException("Keywords are required.");
    }
    if (dto.getLatitude() == null) {
        throw new IllegalArgumentException("Latitude is required.");
    }
    if (dto.getLongitude() == null) {
        throw new IllegalArgumentException("Longitude is required.");
    }
    
    if (dto.getImages() == null || dto.getImages().isEmpty()) {
        throw new IllegalArgumentException("At least one image is required.");
    }
}

public String saveFile(MultipartFile file) throws IOException {
    // Ensure the directory exists
    File directory = new File(uploadDir);
    if (!directory.exists()) {
        directory.mkdirs();
    }

    // Save the file
    File dest = new File(directory, file.getOriginalFilename());
    file.transferTo(dest);
    return dest.getAbsolutePath();
}
*/
/*
@Value("${file.upload-dir}")
private String uploadDir;

// Other service methods...

public Agripreneur saveAgripreneur(AgripreneurRegistrationDto agripreneurRegistrationDto) throws IOException {
    Agripreneur agripreneur = new Agripreneur();
    agripreneur.setFullName(agripreneurRegistrationDto.getFullName());
    agripreneur.setIdNo(agripreneurRegistrationDto.getIdNo());
    agripreneur.setVentureName(agripreneurRegistrationDto.getVentureName());
    agripreneur.setPincode(agripreneurRegistrationDto.getPincode());
    agripreneur.setMobileNumber(agripreneurRegistrationDto.getMobileNumber());
    agripreneur.setAlternateNumber(agripreneurRegistrationDto.getAlternateNumber());
    agripreneur.setEmail(agripreneurRegistrationDto.getEmail());
    agripreneur.setTraningCenterName(agripreneurRegistrationDto.getTrainingCenterName());
    agripreneur.setAnnualTurnover(agripreneurRegistrationDto.getAnnualTurnover());
    agripreneur.setAnnualIncome(agripreneurRegistrationDto.getAnnualIncome());
    agripreneur.setPersonsEmployed(agripreneurRegistrationDto.getPersonsEmployed());
    agripreneur.setFarmersCovered(agripreneurRegistrationDto.getFarmersCovered());
    agripreneur.setVillagesCovered(agripreneurRegistrationDto.getVillagesCovered());
    agripreneur.setBankLoan(agripreneurRegistrationDto.getBankLoan());
    agripreneur.setSubsidy(agripreneurRegistrationDto.getSubsidy());
    agripreneur.setKeywords(agripreneurRegistrationDto.getKeywords());
    agripreneur.setServiceCost(agripreneurRegistrationDto.getServiceCost());

    // Check and set service categories
    List<Long> categoryIds = agripreneurRegistrationDto.getServiceCategoryIds();
    List<Category> categories = categoryRepository.findAllById(categoryIds);
    if (categories.size() != categoryIds.size()) {
        throw new ResourceNotFoundException("One or more categories not found");
    }
    agripreneur.setServiceCategories(categories);

    // Check and set state
    State state = stateRepository.findById(agripreneurRegistrationDto.getStateId())
            .orElseThrow(() -> new ResourceNotFoundException("State not found"));
    agripreneur.setState(state);

    // Check and set district
    District district = districtRepository.findById(agripreneurRegistrationDto.getDistrictId())
            .orElseThrow(() -> new ResourceNotFoundException("District not found"));
    agripreneur.setDistrict(district);

    // Check and set sub-district
    SubDistrict subDistrict = subDistrictRepository.findById(agripreneurRegistrationDto.getSubDistrictId())
            .orElseThrow(() -> new ResourceNotFoundException("Sub-district not found"));
    agripreneur.setSubDistrict(subDistrict);

    // Check and set village
    Village village = villageRepository.findByNameAndStateAndDistrictAndSubDistrict(
            agripreneurRegistrationDto.getVillageName(), state, district, subDistrict);
    if (village == null) {
        village = new Village();
        village.setName(agripreneurRegistrationDto.getVillageName());
        village.setState(state);
        village.setDistrict(district);
        village.setSubDistrict(subDistrict);
        villageRepository.save(village);
    }
    agripreneur.setVillage(village);

    // Set coordinates
    Coordinates coordinates = new Coordinates();
    coordinates.setLatitude(agripreneurRegistrationDto.getLatitude());
    coordinates.setLongitude(agripreneurRegistrationDto.getLongitude());
    coordinates.setAgripreneur(agripreneur);
    agripreneur.setCoordinates(coordinates);

    // Save agripreneur
    agripreneurRepository.save(agripreneur);

    // Save images
    for (MultipartFile file : agripreneurRegistrationDto.getImages()) {
        String filePath = saveFile(file);
        AgripreneurImage agripreneurImage = new AgripreneurImage();
        agripreneurImage.setAgripreneur(agripreneur);
        agripreneurImage.setImagePath(filePath);
        agripreneurImageRepository.save(agripreneurImage);
    }

    return agripreneur;
}

public String saveFile(MultipartFile file) throws IOException {
    // Ensure the directory exists
    File directory = new File(uploadDir);
    if (!directory.exists()) {
        directory.mkdirs();
    }

    // Save the file
    File dest = new File(directory, file.getOriginalFilename());
    file.transferTo(dest);
    return dest.getAbsolutePath();
}
 
      public AgripreneurGetDto getProfile(Long id) {
    	    Agripreneur agripreneur = agripreneurRepository.findById(id)
    	        .orElseThrow(() -> new ResourceNotFoundException("Agripreneur not found with id: " + id));

    	    AgripreneurGetDto agripreneurGetDto = new AgripreneurGetDto();
    	    agripreneurGetDto.setAgripreneurId(agripreneur.getAgripreneurId());
    	    agripreneurGetDto.setFullName(agripreneur.getFullName());
    	    agripreneurGetDto.setIdNo(agripreneur.getIdNo());
    	    agripreneurGetDto.setVentureName(agripreneur.getVentureName());
    	    agripreneurGetDto.setState(agripreneur.getState().getName());
    	    agripreneurGetDto.setDistrict(agripreneur.getDistrict().getName());
    	    agripreneurGetDto.setSubDistrict(agripreneur.getSubDistrict().getName());
    	    agripreneurGetDto.setVillage(agripreneur.getVillage().getName());
    	    agripreneurGetDto.setPincode(agripreneur.getPincode());
    	    agripreneurGetDto.setMobileNumber(agripreneur.getMobileNumber());
    	    agripreneurGetDto.setAlternateNumber(agripreneur.getAlternateNumber());
    	    agripreneurGetDto.setEmail(agripreneur.getEmail());
    	    agripreneurGetDto.setTraningCenterName(agripreneur.getTraningCenterName());
    	    agripreneurGetDto.setAnnualTurnover(agripreneur.getAnnualTurnover());
    	    agripreneurGetDto.setAnnualIncome(agripreneur.getAnnualIncome());
    	    agripreneurGetDto.setPersonsEmployed(agripreneur.getPersonsEmployed());
    	    agripreneurGetDto.setFarmersCovered(agripreneur.getFarmersCovered());
    	    agripreneurGetDto.setVillagesCovered(agripreneur.getVillagesCovered());
    	    agripreneurGetDto.setBankLoan(agripreneur.getBankLoan());
    	    agripreneurGetDto.setSubsidy(agripreneur.getSubsidy());
    	    agripreneurGetDto.setKeywords(agripreneur.getKeywords());
    	    agripreneurGetDto.setServiceCost(agripreneur.getServiceCost());
    	    agripreneurGetDto.setServiceCategories(agripreneur.getServiceCategories().stream()
    	            .map(Category::getName)
    	            .collect(Collectors.toList()));
    	    agripreneurGetDto.setImages(agripreneur.getImages().stream()
    	            .map(image -> {
    	                AgripreneurImageDto imageDto = new AgripreneurImageDto();
    	                imageDto.setId(image.getId());
    	                imageDto.setImagePath(image.getImagePath());
    	                try {
    	                    imageDto.setImageData(encodeFileToBase64(image.getImagePath()));
    	                } catch (IOException e) {
    	                    e.printStackTrace();
    	                }
    	                return imageDto;
    	            }).collect(Collectors.toList()));

    	    if (agripreneur.getCoordinates() != null) {
    	        CoordinatesDto coordinatesDto = new CoordinatesDto();
    	        coordinatesDto.setLatitude(agripreneur.getCoordinates().getLatitude());
    	        coordinatesDto.setLongitude(agripreneur.getCoordinates().getLongitude());
    	        agripreneurGetDto.setCoordinates(coordinatesDto);
    	    }

    	    return agripreneurGetDto;
    	}

    	private String encodeFileToBase64(String filePath) throws IOException {
    	    File file = new File(filePath);
    	    byte[] fileContent = Files.readAllBytes(file.toPath());
    	    return Base64.getEncoder().encodeToString(fileContent);
    	}

 public List<AgripreneursByKeywordDto> getAgripreneursByKeyword(String keyword) {
       List<Agripreneur> agripreneurs = agripreneurRepository.findByKeyword(keyword);
       return agripreneurs.stream()
               .map(this::convertToKeywordDto)
               .collect(Collectors.toList());
   }

   private AgripreneursByKeywordDto convertToKeywordDto(Agripreneur agripreneur) {
       AgripreneursByKeywordDto dto = new AgripreneursByKeywordDto();
       dto.setAgripreneurId(agripreneur.getAgripreneurId());
       dto.setFullName(agripreneur.getFullName());
       dto.setIdNo(agripreneur.getIdNo());
       dto.setVentureName(agripreneur.getVentureName());
       dto.setState(agripreneur.getState().getName());
       dto.setDistrict(agripreneur.getDistrict().getName());
       dto.setSubDistrict(agripreneur.getSubDistrict().getName());
       dto.setVillage(agripreneur.getVillage().getName());
       dto.setPincode(agripreneur.getPincode());
       dto.setMobileNumber(agripreneur.getMobileNumber());
       dto.setAlternateNumber(agripreneur.getAlternateNumber());
       dto.setEmail(agripreneur.getEmail());
       dto.setTraningCenterName(agripreneur.getTraningCenterName());
       dto.setAnnualTurnover(agripreneur.getAnnualTurnover());
       dto.setAnnualIncome(agripreneur.getAnnualIncome());
       dto.setPersonsEmployed(agripreneur.getPersonsEmployed());
       dto.setFarmersCovered(agripreneur.getFarmersCovered());
       dto.setVillagesCovered(agripreneur.getVillagesCovered());
       dto.setBankLoan(agripreneur.getBankLoan());
       dto.setSubsidy(agripreneur.getSubsidy());
       dto.setKeywords(agripreneur.getKeywords());
       dto.setServiceCost(agripreneur.getServiceCost());
       dto.setServiceCategories(agripreneur.getServiceCategories()
               .stream()
               .map(Category::getName)
               .collect(Collectors.toList()));
       dto.setImages(agripreneur.getImages()
               .stream()
               .map(this::convertToImageDto)
               .collect(Collectors.toList()));
       dto.setCoordinates(new CoordinatesDto(agripreneur.getCoordinates().getLatitude(),
               agripreneur.getCoordinates().getLongitude()));
       return dto;
   }

   private AgripreneurImageDto convertToImageDto(AgripreneurImage image) {
       AgripreneurImageDto imageDto = new AgripreneurImageDto();
       imageDto.setId(image.getId());
       imageDto.setImagePath(image.getImagePath());
       return imageDto;
   }
   
  public List<FarmerQueryDto> getFarmerQueriesByAgripreneur(Long agripreneurId) {
       List<FarmerQuery> farmerQueries = queryRepository.findByAgripreneur_AgripreneurId(agripreneurId);

       // Map FarmerQuery entities to FarmerQueryResponseDTOs
       return farmerQueries.stream().map(query -> {
           List<String> imageUrls = query.getImages().stream()
                   .map(QueryImage::getImageUrl)
                   .collect(Collectors.toList());

           return new FarmerQueryDto(query.getFarmer().getFullname(), query.getQueryText(), imageUrls);
       }).collect(Collectors.toList());
   }
   
   
*/

   
