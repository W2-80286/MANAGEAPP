package com.agri.repository;

import com.agri.model.SubDistrict;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface SubDistrictRepository extends JpaRepository<SubDistrict, Long> {

    @Modifying
    @Query(value = "ALTER TABLE sub_district AUTO_INCREMENT = 1", nativeQuery = true)
    void resetAutoIncrement();
    
    List<SubDistrict> findByDistrictId(Long districtId);
}
