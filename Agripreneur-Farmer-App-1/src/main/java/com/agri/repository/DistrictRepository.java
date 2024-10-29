package com.agri.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.agri.model.District;

import java.util.List;

public interface DistrictRepository extends JpaRepository<District, Long> {
    List<District> findByStateId(Long stateId);
}
