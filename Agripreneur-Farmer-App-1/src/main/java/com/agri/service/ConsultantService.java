package com.agri.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.agri.dto.AgripreneursByStateIdDto;
import com.agri.dto.CoordinatesDto;
import com.agri.dto.FarmerByStateIdDto;
import com.agri.exception.ResourceNotFoundException;
import com.agri.model.Agripreneur;
import com.agri.model.Category;
import com.agri.model.Consultant;
import com.agri.model.Farmer;
import com.agri.model.State;
import com.agri.repository.AgripreneurRepository;
import com.agri.repository.ConsultantRepository;
import com.agri.repository.FarmerRepository;
@Service
public class ConsultantService {
	
	@Autowired
	private AgripreneurRepository agripreneurRepository;
	
	@Autowired
	private FarmerRepository farmerRepository;


	   
    @Autowired
    private ConsultantRepository consultantRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AgripreneurService agripreneurService;
    
    public Consultant findConsultantByEmail(String email) {
        return consultantRepository.findByEmail(email); // Assuming you have this in your repository
    }

    public void saveConsultant(Consultant consultant) {
        consultantRepository.save(consultant); // Save the updated consultant with the new password
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
	   
	   
	  	    public List<FarmerByStateIdDto> getFarmersByStateName(String stateName) {
	        List<Farmer> farmers = farmerRepository.findByStateName(stateName);
	        if (farmers.isEmpty()) {
	            System.out.println("No farmers found for state: " + stateName);
	        } else {
	            System.out.println("Found " + farmers.size() + " farmers for state: " + stateName);
	        }
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
	    
	    public boolean verifyAgripreneur(Long agripreneurId, boolean approve, String comments) {
	        Agripreneur agripreneur = agripreneurRepository.findById(agripreneurId)
	            .orElseThrow(() -> new ResourceNotFoundException("Agripreneur not found"));
	        if (approve) {
	            agripreneur.setVerified(true);
	        } else {
	            agripreneur.setVerified(false);
	        }
	        agripreneur.setVerificationComments(comments); // Add a new field if needed
	        agripreneurRepository.save(agripreneur);
	        return approve;
	    }


	    public boolean isMobileNumberRegistered(String mobileNumber) {
	        return consultantRepository.findByMobileNumber(mobileNumber).isPresent();
	    }


}
