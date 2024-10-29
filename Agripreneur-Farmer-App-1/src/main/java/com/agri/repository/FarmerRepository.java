package com.agri.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.agri.model.Farmer;
@Repository
public interface FarmerRepository extends JpaRepository<Farmer, Long>{
	
	
  //  @Query("SELECT f FROM Agripreneur f WHERE f.district.name = :districtName AND f.verified = true AND f.approved = true")
    List<Farmer> findByDistrictName(String districtName);
	
   // @Query("SELECT f FROM Agripreneur f WHERE f.subDistrict.name = :subDistrictName AND f.verified = true AND f.approved = true")
    List<Farmer> findBySubDistrictName(String subDistrictName);
    
   // @Query("SELECT a FROM Agripreneur a WHERE a.state.name = :stateName AND a.verified = true AND a.approved = true")
    List<Farmer> findByStateName(String stateName);
    
    Optional<Farmer> findByMobileNumber(String mobileNumber);
   // boolean existsByMobileNumber(Long mobileNumber);
    
    
    /*
    @Query("SELECT f FROM Agripreneur f WHERE f.district.name = :districtName AND f.verified = true AND f.approved = true")
    List<Farmer> findVerifiedAndApprovedByDistrictName(@Param("districtName") String districtName);
    
   @Query("SELECT f FROM Agripreneur f WHERE f.subDistrict.name = :subDistrictName AND f.verified = true AND f.approved = true")
    List<Farmer> findVerifiedAndApprovedBySubDistrictName(@Param("subDistrictName") String subDistrictName);
    
    @Query("SELECT f FROM Agripreneur f WHERE f.state.name = :stateName AND f.verified = true AND f.approved = true")
    List<Farmer> findVerifiedAndApprovedByStateName(@Param("stateName") String stateName);
    
*/
}
