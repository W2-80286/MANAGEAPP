package com.agri.service;

import com.agri.dto.AgripreneurGetDto;
import com.agri.dto.AgripreneurImageDto;
import com.agri.dto.AgripreneurMapDto;
import com.agri.dto.AgripreneursByCategoryDto;
import com.agri.dto.AgripreneursByDistrictIdDto;
import com.agri.dto.AgripreneursByKeywordDto;
import com.agri.dto.AgripreneursByStateIdDto;
import com.agri.dto.AgripreneursBySubDistrictIdDto;
import com.agri.dto.CoordinatesDto;
import com.agri.dto.FarmerRegistrationDto;
import com.agri.dto.FarmerServiceSelectionDto;
import com.agri.dto.FeedbackResponseDto;
import com.agri.dto.QueryDto;
import com.agri.dto.ReviewDto;
//import com.agri.dto.FarmerServiceSelectionDto;
import com.agri.exception.ResourceNotFoundException;
import com.agri.jwtFarmer.JwtUtil;
import com.agri.model.*;
import com.agri.repository.*;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class FarmerService {
	
		
    //private static final String IMAGE_DIR = "/home/cdachyd/AgripreneurFarmer-App/Backend/Agripreneur-Farmer-App-1/QueryImages";
    
    private static final String IMAGE_DIR = "/home/cdachyd/AgripreneurFarmer-App_updated/Backend/Agripreneur-Farmer-App-1/QueryImages/";

	@Autowired
    private FarmerRepository farmerRepository;

    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private DistrictRepository districtRepository;

    @Autowired
    private SubDistrictRepository subDistrictRepository;

     
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private AgripreneurRepository agripreneurRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private QueryRepository queryRepository;

    @Autowired
    private QueryImageRepository queryImageRepository;

    @Autowired
	 private  OtpService otpService;
    
    @Autowired
    private FeedbackQuestionRepository feedbackQuestionRepository;

    @Autowired
    private FeedbackResponseRepository feedbackResponseRepository;


    @Autowired
    private JwtUtil jwtUtil;
    
    
    @Autowired
    private ProfileVisitRepository profileVisitRepository;
    
    public Optional<Farmer> findById(Long id) {
        return farmerRepository.findById(id);
    }

/*
    public Farmer saveFarmer(FarmerRegistrationDto farmerRegistrationDto) throws ResourceNotFoundException {
        Farmer farmer = new Farmer();
        farmer.setFullname(farmerRegistrationDto.getFullname());
        farmer.setEmail(farmerRegistrationDto.getEmail());
        farmer.setMobileNumber(farmerRegistrationDto.getMobileNumber());

        // Check and set state
        State state = stateRepository.findById(farmerRegistrationDto.getStateId())
                .orElseThrow(() -> new ResourceNotFoundException("State not found"));
        farmer.setState(state);

        // Check and set district
        District district = districtRepository.findById(farmerRegistrationDto.getDistrictId())
                .orElseThrow(() -> new ResourceNotFoundException("District not found"));
        farmer.setDistrict(district);

        // Check and set sub-district
        SubDistrict subDistrict = subDistrictRepository.findById(farmerRegistrationDto.getSubDistrictId())
                .orElseThrow(() -> new ResourceNotFoundException("SubDistrict not found"));
        farmer.setSubDistrict(subDistrict);

        // Save farmer
        farmerRepository.save(farmer);

        return farmer;
    }
    
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
    public String registerFarmer(FarmerRegistrationDto farmerDTO) {
        Optional<Farmer> existingFarmer = farmerRepository.findByMobileNumber(farmerDTO.getMobileNumber());
        if (existingFarmer.isPresent()) {
            return "Mobile number already registered.";
        }

        Farmer farmer = new Farmer();
        farmer.setFullname(farmerDTO.getFullname());
        farmer.setEmail(farmerDTO.getEmail());
        farmer.setMobileNumber(farmerDTO.getMobileNumber());

        State state = stateRepository.findById(farmerDTO.getStateId())
                .orElseThrow(() -> new ResourceNotFoundException("State not found"));
        farmer.setState(state);

        District district = districtRepository.findById(farmerDTO.getDistrictId())
                .orElseThrow(() -> new ResourceNotFoundException("District not found"));
        farmer.setDistrict(district);

        SubDistrict subDistrict = subDistrictRepository.findById(farmerDTO.getSubDistrictId())
                .orElseThrow(() -> new ResourceNotFoundException("SubDistrict not found"));
        farmer.setSubDistrict(subDistrict);

        // Generate and send OTP
        String otp = otpService.generateOtp();
        System.out.println("Sending OTP: " + otp + " to mobile number: " + farmer.getMobileNumber());

        farmer.setOtp(otp);
        farmerRepository.save(farmer);

        return "OTP has been sent to your mobile number for registration.";
    }

    public String verifyRegistrationOtp(String mobileNumber, String otp) {
        Optional<Farmer> farmerOptional = farmerRepository.findByMobileNumber(mobileNumber);
        if (farmerOptional.isPresent()) {
            Farmer farmer = farmerOptional.get();
            if (farmer.getOtp().equals(otp)) {
                farmer.setOtp(null); // Clear OTP after successful verification
                farmerRepository.save(farmer);

                // Generate JWT token upon successful OTP verification
                String token = jwtUtil.generateToken(mobileNumber);
                return token; // Return JWT token
            }
        }
        return "Invalid OTP.";
    }

    
    public String loginFarmer(String mobileNumber) {
        Optional<Farmer> farmerOptional = farmerRepository.findByMobileNumber(mobileNumber);
        if (farmerOptional.isPresent()) {
            String otp = otpService.generateOtp();
            System.out.println("Sending OTP: " + otp + " to mobile number: " + mobileNumber);
            Farmer farmer = farmerOptional.get();
            farmer.setOtp(otp);
            farmerRepository.save(farmer);
            return "OTP has been sent to your mobile number for login.";
        }
        return "Mobile number not registered.";
    }

    public String verifyLoginOtp(String mobileNumber, String otp) {
        Optional<Farmer> farmerOptional = farmerRepository.findByMobileNumber(mobileNumber);
        if (farmerOptional.isPresent()) {
            Farmer farmer = farmerOptional.get();
            if (farmer.getOtp().equals(otp)) {
                farmer.setOtp(null); // Clear OTP after successful verification
                farmerRepository.save(farmer);
                
                // Generate JWT token
                String token = jwtUtil.generateToken(mobileNumber);
                return token;  // Return token after OTP verification
            }
        }
        return "Invalid OTP.";
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


   
    
    public Farmer selectFarmerCategoriesByMobileNumber(String mobileNumber, FarmerServiceSelectionDto farmerServiceSelectionDto) throws IllegalAccessException {
        // Find the farmer using the mobile number
        Farmer farmer = farmerRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new IllegalArgumentException("Invalid farmer mobile number"));

        // Fetch the categories
        List<Category> categories = categoryRepository.findAllById(farmerServiceSelectionDto.getCategoryIds());
        List<Long> foundCategoryIds = categories.stream().map(Category::getId).collect(Collectors.toList());

        // Check for any invalid category IDs
        List<Long> missingCategoryIds = farmerServiceSelectionDto.getCategoryIds().stream()
                .filter(id -> !foundCategoryIds.contains(id))
                .collect(Collectors.toList());
        if (!missingCategoryIds.isEmpty()) {
            throw new IllegalAccessException("Invalid category IDs: " + missingCategoryIds);
        }

        // Set the categories to the farmer
        farmer.setServiceCategories(categories);

        // Save and return the updated farmer
        return farmerRepository.save(farmer);
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
    
    
    public void submitReview(String mobileNumber, ReviewDto reviewDto) {
        // Find the farmer based on their mobile number from the token
        Farmer farmer = farmerRepository.findByMobileNumber(mobileNumber)
                            .orElseThrow(() -> new RuntimeException("Farmer not found"));

        // Find the agripreneur based on the ID in the review DTO
        Agripreneur agripreneur = agripreneurRepository.findById(reviewDto.getAgripreneurId())
                                    .orElseThrow(() -> new RuntimeException("Agripreneur not found"));

        // Create and save the review
        Review review = new Review();
        review.setFarmer(farmer);
        review.setAgripreneur(agripreneur);
        review.setRating(reviewDto.getRating());
        review.setReviewText(reviewDto.getReviewText());

        reviewRepository.save(review);
    }

    public FarmerQuery submitQuery(QueryDto queryDto, String mobileNumber) throws IOException {
        // Validate required fields
        validateQueryFields(queryDto);

        // Find Farmer using the mobile number from the JWT token
        Farmer farmer = farmerRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Farmer not found"));

        // Find Agripreneur
        Agripreneur agripreneur = agripreneurRepository.findById(queryDto.getAgripreneurId())
                .orElseThrow(() -> new ResourceNotFoundException("Agripreneur not found"));

        // Create and save Query entity
        FarmerQuery query = new FarmerQuery();
        query.setFarmer(farmer);
        query.setAgripreneur(agripreneur);
        query.setQueryText(queryDto.getQueryText());
        queryRepository.save(query);

        // Save query images
        List<QueryImage> queryImages = new ArrayList<>();
        for (MultipartFile file : queryDto.getQueryImages()) {
            String filePath = saveFile(file);
            QueryImage queryImage = new QueryImage();
            queryImage.setFarmerquery(query);
            queryImage.setImageUrl(filePath);
            queryImages.add(queryImage);
        }
        query.setImages(queryImages);
        queryImageRepository.saveAll(queryImages);

        return query;
    }
    
    
    private void validateQueryFields(QueryDto queryDto) {
    	/*
        if (queryDto.getFarmerId() == null) {
            throw new IllegalArgumentException("Farmer ID is required.");
        }
        */
        if (queryDto.getAgripreneurId() == null) {
            throw new IllegalArgumentException("Agripreneur ID is required.");
        }
        if (queryDto.getQueryText() == null || queryDto.getQueryText().isEmpty()) {
            throw new IllegalArgumentException("Query text is required.");
        }
        if (queryDto.getQueryImages() == null || queryDto.getQueryImages().isEmpty()) {
            throw new IllegalArgumentException("At least one query image is required.");
        }
    }
    private String saveFile(MultipartFile file) throws IOException {
        String directory = IMAGE_DIR;
        File dest = new File(directory + file.getOriginalFilename());
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        file.transferTo(dest);
        return dest.getAbsolutePath();
    }

    public String submitFeedback(String mobileNumber, Long questionId, String response) {
        // Fetch Farmer using mobile number
        Farmer farmer = farmerRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new RuntimeException("Farmer not found"));

        // Fetch Feedback Question using questionId
        FeedbackQuestion feedbackQuestion = feedbackQuestionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Feedback question not found"));

        // Create and save feedback response
        FeedbackResponse feedbackResponse = new FeedbackResponse();
        feedbackResponse.setFarmer(farmer);
        feedbackResponse.setFeedbackQuestion(feedbackQuestion);
        feedbackResponse.setResponse(response);

        feedbackResponseRepository.save(feedbackResponse);
        return "Feedback submitted successfully";
    }

   
    
    public AgripreneurGetDto getProfile(Long agripreneurId, String token) {
        // Retrieve the Agripreneur details
        Agripreneur agripreneur = agripreneurRepository.findById(agripreneurId)
                .orElseThrow(() -> new ResourceNotFoundException("Agripreneur not found with id: " + agripreneurId));

        // Map Agripreneur entity to DTO
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

        // Map service categories
        agripreneurGetDto.setServiceCategories(agripreneur.getServiceCategories().stream()
                .map(Category::getName)
                .collect(Collectors.toList()));

        // Map images with Base64 encoding
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


        // Set coordinates if available
        if (agripreneur.getCoordinates() != null) {
            CoordinatesDto coordinatesDto = new CoordinatesDto();
            coordinatesDto.setLatitude(agripreneur.getCoordinates().getLatitude());
            coordinatesDto.setLongitude(agripreneur.getCoordinates().getLongitude());
            agripreneurGetDto.setCoordinates(coordinatesDto);
        }

        // Extract mobile number from token and retrieve Farmer
        String mobileNumber = jwtUtil.extractUsername(token.substring(7)); // Removing "Bearer " prefix
        Farmer farmer = farmerRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Farmer not found with mobile number: " + mobileNumber));

        // Save the Profile Visit record
        ProfileVisit visit = new ProfileVisit();
        visit.setFarmer(farmer);
        visit.setAgripreneur(agripreneur);
        visit.setVisitDate(LocalDate.now());
        profileVisitRepository.save(visit);

        return agripreneurGetDto;
    }
    private String encodeFileToBase64(String filePath) throws IOException {
	    File file = new File(filePath);
	    byte[] fileContent = Files.readAllBytes(file.toPath());
	    return Base64.getEncoder().encodeToString(fileContent);
	}

} 
/*
    public Farmer findByMobileNumber(Long mobileNumber) {
        return farmerRepository.findByMobileNumber(mobileNumber).orElse(null);
    }

    public Farmer saveFarmer(FarmerRegistrationDto registrationDto) {
        Farmer farmer = new Farmer();
        farmer.setFullname(registrationDto.getFullname());
        farmer.setEmail(registrationDto.getEmail());
        farmer.setMobileNumber(registrationDto.getMobileNumber());
        
        State state = stateRepository.findById(registrationDto.getStateId())
                .orElseThrow(() -> new ResourceNotFoundException("State not found"));
        farmer.setState(state);

        // Check and set district
        District district = districtRepository.findById(registrationDto.getDistrictId())
                .orElseThrow(() -> new ResourceNotFoundException("District not found"));
        farmer.setDistrict(district);

        // Check and set sub-district
        SubDistrict subDistrict = subDistrictRepository.findById(registrationDto.getSubDistrictId())
                .orElseThrow(() -> new ResourceNotFoundException("SubDistrict not found"));
        farmer.setSubDistrict(subDistrict);

               return farmerRepository.save(farmer);
    }
    */
   
    
   
    
    
    
    
  	

  

/*    public String loginFarmer(String mobileNumber) {
        Optional<Farmer> farmerOptional = farmerRepository.findByMobileNumber(mobileNumber);
        if (farmerOptional.isPresent()) {
            String otp = otpService.generateOtp();
            System.out.println("Sending OTP: " + otp + " to mobile number: " + mobileNumber);
            Farmer farmer = farmerOptional.get();
            farmer.setOtp(otp);
            farmerRepository.save(farmer);
            return "OTP has been sent to your mobile number for login.";
        }
        return "Mobile number not registered.";
    }

    public String verifyLoginOtp(String mobileNumber, String otp) {
        Optional<Farmer> farmerOptional = farmerRepository.findByMobileNumber(mobileNumber);
        if (farmerOptional.isPresent()) {
            Farmer farmer = farmerOptional.get();
            if (farmer.getOtp().equals(otp)) {
                farmer.setOtp(null); // Clear OTP after successful verification
                farmerRepository.save(farmer);
                return "Login successful.";
            }
        }
        return "Invalid OTP.";
    }
     public String registerFarmer(FarmerRegistrationDto farmerDTO) {
        Optional<Farmer> existingFarmer = farmerRepository.findByMobileNumber(farmerDTO.getMobileNumber());
        if (existingFarmer.isPresent()) {
            return "Mobile number already registered.";
        }

        Farmer farmer = new Farmer();
        farmer.setFullname(farmerDTO.getFullname());
        farmer.setEmail(farmerDTO.getEmail());
        farmer.setMobileNumber(farmerDTO.getMobileNumber());
        State state = stateRepository.findById(farmerDTO.getStateId())
                .orElseThrow(() -> new ResourceNotFoundException("State not found"));
        farmer.setState(state);

        // Check and set district
        District district = districtRepository.findById(farmerDTO.getDistrictId())
                .orElseThrow(() -> new ResourceNotFoundException("District not found"));
        farmer.setDistrict(district);

        // Check and set sub-district
        SubDistrict subDistrict = subDistrictRepository.findById(farmerDTO.getSubDistrictId())
                .orElseThrow(() -> new ResourceNotFoundException("SubDistrict not found"));
        farmer.setSubDistrict(subDistrict);

                // Generate and send OTP
        String otp = otpService.generateOtp();
        System.out.println("Sending OTP: " + otp + " to mobile number: " + farmer.getMobileNumber());

        farmer.setOtp(otp);
        farmerRepository.save(farmer);

        return "OTP has been sent to your mobile number for registration.";
    }

    public String verifyRegistrationOtp(String mobileNumber, String otp) {
        Optional<Farmer> farmerOptional = farmerRepository.findByMobileNumber(mobileNumber);
        if (farmerOptional.isPresent()) {
            Farmer farmer = farmerOptional.get();
            if (farmer.getOtp().equals(otp)) {
                farmer.setOtp(null); // Clear OTP after successful verification
                farmerRepository.save(farmer);
                return "Registration successful. You can now log in.";
            }
        }
        return "Invalid OTP.";
    }

 public Farmer selectFarmerCategories(FarmerServiceSelectionDto farmerServiceSelectionDto) throws IllegalAccessException
    {
         Farmer farmer=farmerRepository.findById(farmerServiceSelectionDto.getFarmerId()).orElseThrow(()-> new IllegalArgumentException("Invalid farmer id"));
         List<Category> categories =categoryRepository.findAllById(farmerServiceSelectionDto.getCategoryIds());
         List<Long> foundCategoryIds = categories.stream().map(Category::getId).collect(Collectors.toList());
         List<Long> missingCategoryIds = farmerServiceSelectionDto.getCategoryIds().stream()
                 .filter(id -> !foundCategoryIds.contains(id))
                 .collect(Collectors.toList());
         if (!missingCategoryIds.isEmpty()) {
             throw new IllegalAccessException("Invalid category IDs: " + missingCategoryIds);
         }
         // Set the categories to the farmer
         farmer.setServiceCategories(categories);

         // Save and return the updated farmer
         return farmerRepository.save(farmer);
    }
    
 public void submitReview(ReviewDto reviewDto) {
        Farmer farmer = farmerRepository.findById(reviewDto.getFarmerId())
                            .orElseThrow(() -> new RuntimeException("Farmer not found"));
        Agripreneur agripreneur = agripreneurRepository.findById(reviewDto.getAgripreneurId())
                                    .orElseThrow(() -> new RuntimeException("Agripreneur not found"));

        Review review = new Review();
        review.setFarmer(farmer);
        review.setAgripreneur(agripreneur);
        review.setRating(reviewDto.getRating());
        review.setReviewText(reviewDto.getReviewText());

        reviewRepository.save(review);
    }
     
    public FarmerQuery submitQuery(QueryDto queryDto) throws IOException {
        // Validate required fields
        validateQueryFields(queryDto);

        // Find Farmer and Agripreneur
        Farmer farmer = farmerRepository.findById(queryDto.getFarmerId())
                .orElseThrow(() -> new ResourceNotFoundException("Farmer not found"));
        Agripreneur agripreneur = agripreneurRepository.findById(queryDto.getAgripreneurId())
                .orElseThrow(() -> new ResourceNotFoundException("Agripreneur not found"));

        // Create and save Query entity
        FarmerQuery query = new FarmerQuery();
        query.setFarmer(farmer);
        query.setAgripreneur(agripreneur);
        query.setQueryText(queryDto.getQueryText());
        queryRepository.save(query);

        // Save query images
        List<QueryImage> queryImages = new ArrayList<>();
        for (MultipartFile file : queryDto.getQueryImages()) {
            String filePath = saveFile(file);
            QueryImage queryImage = new QueryImage();
            queryImage.setFarmerquery(query);
            queryImage.setImageUrl(filePath);
            queryImages.add(queryImage);
        }
        query.setImages(queryImages);
        queryImageRepository.saveAll(queryImages);

        return query;
    }

    private String saveFile(MultipartFile file) throws IOException {
        String directory = IMAGE_DIR;
        File dest = new File(directory + file.getOriginalFilename());
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        file.transferTo(dest);
        return dest.getAbsolutePath();
    }

    private void validateQueryFields(QueryDto queryDto) {
        if (queryDto.getFarmerId() == null) {
            throw new IllegalArgumentException("Farmer ID is required.");
        }
        if (queryDto.getAgripreneurId() == null) {
            throw new IllegalArgumentException("Agripreneur ID is required.");
        }
        if (queryDto.getQueryText() == null || queryDto.getQueryText().isEmpty()) {
            throw new IllegalArgumentException("Query text is required.");
        }
        if (queryDto.getQueryImages() == null || queryDto.getQueryImages().isEmpty()) {
            throw new IllegalArgumentException("At least one query image is required.");
        }
    }
    public String submitFeedback(Long farmerId, Long questionId, String response) {
        Farmer farmer = farmerRepository.findById(farmerId)
                .orElseThrow(() -> new RuntimeException("Farmer not found"));
        FeedbackQuestion feedbackQuestion = feedbackQuestionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Feedback question not found"));

        FeedbackResponse feedbackResponse = new FeedbackResponse();
        feedbackResponse.setFarmer(farmer);
        feedbackResponse.setFeedbackQuestion(feedbackQuestion);
        feedbackResponse.setResponse(response);

        feedbackResponseRepository.save(feedbackResponse); // save FeedbackResponse entity here
        return "Feedback submitted successfully";
    }

*/