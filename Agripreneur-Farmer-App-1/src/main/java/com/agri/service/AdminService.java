package com.agri.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.agri.dto.AgripreneurUpdateDto;
import com.agri.dto.AgripreneursByDistrictIdDto;
import com.agri.dto.AgripreneursByStateIdDto;
import com.agri.dto.AgripreneursBySubDistrictIdDto;
import com.agri.dto.ConsultantUpdateDto;
import com.agri.dto.CoordinatesDto;
import com.agri.dto.FarmerByDistrictIdDto;
import com.agri.dto.FarmerByStateIdDto;
import com.agri.dto.FarmerBySubDistrictDto;
import com.agri.dto.FeedbackResponseDto;
import com.agri.dto.consultantGetDto;
import com.agri.exception.ResourceNotFoundException;
import com.agri.model.Admin;
import com.agri.model.Agripreneur;
import com.agri.model.AgripreneurImage;
import com.agri.model.Category;
import com.agri.model.Consultant;
import com.agri.model.Coordinates;
import com.agri.model.District;
import com.agri.model.Farmer;
import com.agri.model.FeedbackQuestion;
import com.agri.model.FeedbackResponse;
import com.agri.model.Review;
import com.agri.model.State;
import com.agri.model.SubDistrict;
import com.agri.model.Village;
import com.agri.repository.AdminRepository;
import com.agri.repository.AgripreneurImageRepository;
import com.agri.repository.AgripreneurRepository;
import com.agri.repository.CategoryRepository;
import com.agri.repository.ConsultantRepository;
import com.agri.repository.DistrictRepository;
import com.agri.repository.FarmerRepository;
import com.agri.repository.FeedbackQuestionRepository;
import com.agri.repository.FeedbackResponseRepository;
import com.agri.repository.StateRepository;
import com.agri.repository.SubDistrictRepository;
import com.agri.repository.VillageRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
@Service
public class AdminService {

	@Autowired
    private AdminRepository adminRepository;

	@PersistenceContext
    private EntityManager entityManager;

	@Autowired
    private ConsultantRepository consultantRepository;

    @Autowired
    private StateRepository stateRepository;
    
    @Autowired
    private FeedbackResponseRepository feedbackResponseRepository;

    @Autowired
    private  AgripreneurRepository agripreneurRepository;
    @Autowired
    private DistrictRepository districtRepository;

    @Autowired
    private SubDistrictRepository subDistrictRepository;

    @Autowired
    private FarmerRepository farmerRepository;
    

    @Autowired
    private VillageRepository villageRepository;

    @Autowired
    private  AgripreneurImageRepository agripreneurImageRepository;
    

    @Autowired
    private CategoryRepository categoryRepository;

    private final String uploadDir = "/home/cdachyd/AgripreneurFarmer-App_updated/Backend/Agripreneur-Farmer-App-1/AgripreneurImages/";

	
	@Autowired
	private FeedbackQuestionRepository feedbackQuestionRepository;
	
	   public List<FeedbackQuestion> getAllFeedbackQuestion()
	   {
		  return feedbackQuestionRepository.findAll(); 
	   }

	   public FeedbackQuestion addFeedbackQuestion(FeedbackQuestion feedbackQuestion) {
		    // Find the smallest available ID
		    Long availableId = findSmallestAvailableId();
		    feedbackQuestion.setId(availableId);
		    return feedbackQuestionRepository.save(feedbackQuestion);
		}

		private Long findSmallestAvailableId() {
		    List<Long> existingIds = feedbackQuestionRepository.findAll()
		                                                       .stream()
		                                                       .map(FeedbackQuestion::getId)
		                                                       .sorted()
		                                                       .collect(Collectors.toList());
		    
		    for (long i = 1; i <= existingIds.size(); i++) {
		        if (!existingIds.contains(i)) {
		            return i;
		        }
		    }
		    return existingIds.size() + 1L; // If all IDs are consecutive, return the next ID
		}

		
		
	public FeedbackQuestion updateFeedbackQuestion(Long id, FeedbackQuestion feedbackQuestion) {
        return feedbackQuestionRepository.findById(id)
                .map(existingQuestion -> {
                    existingQuestion.setQuestion(feedbackQuestion.getQuestion());
                    return feedbackQuestionRepository.save(existingQuestion);
                })
                .orElseThrow(() -> new RuntimeException("FeedbackQuestion not found"));
    }
	
	@Transactional
    public void deleteFeedbackQuestion(Long id) {
        feedbackQuestionRepository.deleteById(id);
        resetAutoIncrement();
    }

    @Transactional
    public void resetAutoIncrement() {
        Long maxId = (Long) entityManager.createQuery("SELECT COALESCE(MAX(fq.id), 0) FROM FeedbackQuestion fq").getSingleResult();
        
        // Reset the auto-increment to maxId + 1 to continue from the highest existing ID
        entityManager.createNativeQuery("ALTER TABLE feedback_question AUTO_INCREMENT = " + maxId).executeUpdate();
    }
    
  


    public List<Consultant> getAllConsultants() {
        return consultantRepository.findAll();
    }

    public List<consultantGetDto> getAllConsultantResponses() {
        List<Consultant> consultants = getAllConsultants();
        return consultants.stream()
                .map(c -> new consultantGetDto(
                        c.getConsultantId(),
                        c.getFullname(),
                        c.getEmail(),
                        c.getMobileNumber(),
                        c.getState().getName() // Only state name
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public Consultant updateConsultant(Long id, Consultant consultantDetails) {
        // Check if the consultant exists
        Consultant consultant = consultantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consultant not found"));

        // Update fields as necessary
        consultant.setFullname(consultantDetails.getFullname());
        consultant.setEmail(consultantDetails.getEmail());
        consultant.setState(consultantDetails.getState());
        consultant.setMobileNumber(consultantDetails.getMobileNumber());
        // mobileNumber is not updated, assuming it's immutable

        // Save the updated consultant back to the database
        return consultantRepository.save(consultant);
    }

    @Transactional
    public Consultant updateConsultant(Long id, ConsultantUpdateDto consultantUpdateRequest) {
        // Check if the consultant exists
        Consultant consultant = consultantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consultant not found"));

        // Fetch the state using stateId from the request
        State state = stateRepository.findById(consultantUpdateRequest.getStateId())
                .orElseThrow(() -> new RuntimeException("State not found"));

        // Update consultant details
        consultant.setFullname(consultantUpdateRequest.getFullname());
        consultant.setMobileNumber(consultantUpdateRequest.getMobileNumber());
        consultant.setEmail(consultantUpdateRequest.getEmail());
        consultant.setState(state);  // Associate the state with consultant

        // Save the updated consultant
        return consultantRepository.save(consultant);
    }
    
    @Transactional
    public void deleteConsultant(Long id) {
        // Check if the consultant exists
        Consultant consultant = consultantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consultant not found"));

        // Proceed to delete the consultant
        consultantRepository.delete(consultant);
    }

    public List<FeedbackResponseDto> getAllFeedbackResponses() {
        List<FeedbackResponse> responses = feedbackResponseRepository.findAll();
        
        // Map FeedbackResponse entities to FeedbackResponseDTO with id
        return responses.stream()
                .map(response -> new FeedbackResponseDto(
                        response.getId(),
                        response.getFarmer().getFullname(),
                        response.getFeedbackQuestion().getQuestion(),
                        response.getResponse()))
                .collect(Collectors.toList());
    }

    public Long findSmallestAvailableConsultantId() {
        List<Long> existingIds = consultantRepository.findAll()
            .stream()
            .map(Consultant::getConsultantId)
            .sorted()
            .collect(Collectors.toList());

        // Find the smallest available ID
        for (long i = 1; i <= existingIds.size(); i++) {
            if (!existingIds.contains(i)) {
                return i;
            }
        }
        return existingIds.size() + 1L; // If no gaps, return the next ID
    }

    public Consultant saveConsultant(Consultant consultant) {
        return consultantRepository.save(consultant);
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
        
        List<Review> reviews = agripreneur.getReviews();
        if (reviews != null && !reviews.isEmpty()) {
            double avgRating = reviews.stream()
                                      .mapToDouble(Review::getRating)
                                      .average()
                                      .orElse(0.0);  // Handle if no reviews
            dto.setRating(avgRating);
        } else {
            dto.setRating(0.0);  // No reviews, default rating to 0
        }

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
        List<Review> reviews = agripreneur.getReviews();
        if (reviews != null && !reviews.isEmpty()) {
            double avgRating = reviews.stream()
                                      .mapToDouble(Review::getRating)
                                      .average()
                                      .orElse(0.0);  // Handle if no reviews
            dto.setRating(avgRating);
        } else {
            dto.setRating(0.0);  // No reviews, default rating to 0
        }

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
        
        List<Review> reviews = agripreneur.getReviews();
        if (reviews != null && !reviews.isEmpty()) {
            double avgRating = reviews.stream()
                                      .mapToDouble(Review::getRating)
                                      .average()
                                      .orElse(0.0);  // Handle if no reviews
            dto.setRating(avgRating);
        } else {
            dto.setRating(0.0);  // No reviews, default rating to 0
        }

        return dto;
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
    
    public List<FarmerByStateIdDto> getFarmersByStateName(String stateName) {
        List<Farmer> farmers = farmerRepository.findByStateName(stateName);
        return farmers.stream()
                .map(this::convertToStateDto)
                .collect(Collectors.toList());
    }

    private FarmerByStateIdDto convertToStateDto(Farmer farmer) {
    	FarmerByStateIdDto dto = new FarmerByStateIdDto();
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
    
    
    public List<FarmerBySubDistrictDto> getFarmersBySubDistrictName(String subDistrictName) {
        List<Farmer> farmers = farmerRepository.findBySubDistrictName(subDistrictName);
        return farmers.stream()
                .map(this::convertToSubDistrictDto)
                .collect(Collectors.toList());
    }

    private FarmerBySubDistrictDto convertToSubDistrictDto(Farmer farmer) {
    	FarmerBySubDistrictDto dto = new FarmerBySubDistrictDto();
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

    

    @Transactional
  public Agripreneur updateAgripreneur(Long id, AgripreneurUpdateDto updateRequest) throws IOException {
      Agripreneur agripreneur = agripreneurRepository.findById(id)
              .orElseThrow(() -> new ResourceNotFoundException("Agripreneur not found with id " + id));

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

   
    // Save or update admin credentials
    public void saveAdmin(Admin admin) {
        adminRepository.save(admin);
    }
    
    
    public boolean existsByEmailOrMobileNumber(String email, String mobileNumber) {
        return adminRepository.existsByEmail(email) || adminRepository.existsByMobileNumber(mobileNumber);
    }
    
    
    public boolean approveOrRejectAgripreneur(Long agripreneurId, boolean approve) {
        Optional<Agripreneur> agripreneurOpt = agripreneurRepository.findById(agripreneurId);
        
        if (agripreneurOpt.isPresent()) {
            Agripreneur agripreneur = agripreneurOpt.get();
            
            // Check if the Agripreneur is verified by a consultant
            if (agripreneur.isVerified()) {
                agripreneur.setApproved(approve);
                agripreneurRepository.save(agripreneur);
                return true;
            }
        }
        return false;
    }

    @Transactional
    public Admin registerAdmin(Admin admin) {
        if (adminRepository.existsByEmail(admin.getEmail())) {
            throw new IllegalArgumentException("Email is already in use.");
        }

        if (adminRepository.existsByMobileNumber(admin.getMobileNumber())) {
            throw new IllegalArgumentException("Mobile number is already in use.");
        }

        return adminRepository.save(admin);
    }
    
    
    public boolean isMobileNumberRegistered(String mobileNumber) {
        return adminRepository.findByMobileNumber(mobileNumber).isPresent();
    }



}

   /* public Admin findByUsername(String username) {
        Optional<Consultant> admin = adminRepository.findByFullName(username);

        if (admin == null) {
            admin = adminRepository.findByEmail(username);
        }

        if (admin == null) {
            admin = adminRepository.findByMobileNumber(username);
        }

        return admin; // null if not found
    }
*/
