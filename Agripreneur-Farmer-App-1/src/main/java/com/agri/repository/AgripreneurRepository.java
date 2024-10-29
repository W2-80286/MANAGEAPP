package com.agri.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.agri.model.Agripreneur;
import com.agri.model.Category;
import com.agri.model.District;

import java.util.List;
import java.util.Optional;

@Repository
public interface AgripreneurRepository extends JpaRepository<Agripreneur, Long> {
   // List<Agripreneur> findByDistrictId(Long districtId);
    /*
    @Query("SELECT a FROM Agripreneur a JOIN a.serviceCategories sc WHERE sc.name = :categoryName")
    List<Agripreneur> findByServiceCategoryName(@Param("categoryName") String categoryName);
    
    @Query("SELECT a FROM Agripreneur a WHERE LOWER(a.keywords) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Agripreneur> findByKeyword(@Param("keyword") String keyword);
    
*/
    
    @Query("SELECT f FROM Agripreneur f WHERE f.district.name = :districtName AND f.verified = true AND f.approved = true")
    List<Agripreneur> findByDistrictName(@Param("districtName") String districtName);
    
       
    @Query("SELECT a FROM Agripreneur a WHERE a.state.name = :stateName AND a.verified = true AND a.approved = true")
    List<Agripreneur> findByStateName(String stateName);
    
    @Query("SELECT f FROM Agripreneur f WHERE f.subDistrict.name = :subDistrictName AND f.verified = true AND f.approved = true")
    List<Agripreneur> findBySubDistrictName(String subDistrictName);

    
    
    @Query("SELECT a FROM Agripreneur a WHERE LOWER(a.keywords) LIKE LOWER(CONCAT('%', :keyword, '%')) AND a.verified = true AND a.approved = true")
    List<Agripreneur> findByKeyword(@Param("keyword") String keyword);

    @Query("SELECT a FROM Agripreneur a JOIN a.serviceCategories sc WHERE sc.name = :categoryName AND a.verified = true AND a.approved = true")
    List<Agripreneur> findByServiceCategoryName(@Param("categoryName") String categoryName);
    

    //@Query(value = "SELECT a FROM Agripreneur a JOIN a.coordinates c " +
           // "WHERE (6371 * acos(cos(radians(:latitude)) * cos(radians(c.latitude)) * cos(radians(c.longitude) - radians(:longitude)) + sin(radians(:latitude)) * sin(radians(c.latitude)))) < :radius")
    @Query(value = "SELECT a FROM Agripreneur a JOIN a.coordinates c " +
            "WHERE (6371 * acos(cos(radians(:latitude)) * cos(radians(c.latitude)) * cos(radians(c.longitude) - radians(:longitude)) + sin(radians(:latitude)) * sin(radians(c.latitude)))) < :radius " +
            "AND a.verified = true AND a.approved = true")
    List<Agripreneur> findAgripreneursWithinRadius(@Param("latitude") double latitude,
                                            @Param("longitude") double longitude,
                                            @Param("radius") double radius);

	Optional<Agripreneur> findByMobileNumber(String mobileNumber);
}
          