package com.agri.service;

import com.agri.model.District;
import com.agri.repository.DistrictRepository;

import jakarta.persistence.*;
import jakarta.transaction.Transactional;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DistrictService {

    @Autowired
    private DistrictRepository districtRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public List<District> getAllDistricts() {
        return districtRepository.findAll();
    }

    public District getDistrictById(Long id) {
        return districtRepository.findById(id).orElse(null);
    }

    public District saveDistrict(District district) {
        return districtRepository.save(district);
    }

    @Transactional
    public void deleteDistrict(Long id) {
        districtRepository.deleteById(id);
        resetAutoIncrement();
    }

    public List<District> getDistrictsByStateId(Long stateId) {
        return districtRepository.findByStateId(stateId);
    }

    @Transactional
    public void resetAutoIncrement() {
        Long maxId = (Long) entityManager.createQuery("SELECT COALESCE(MAX(d.id), 0) FROM District d").getSingleResult();
        entityManager.createNativeQuery("ALTER TABLE district AUTO_INCREMENT = " + (maxId + 1)).executeUpdate();
    }
}
