package com.agri.repository;

import com.agri.model.ProfileVisit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ProfileVisitRepository extends JpaRepository<ProfileVisit, Long> {
  // List<ProfileVisit> findByAgripreneurId(Long agripreneurId);
    List<ProfileVisit> findByAgripreneur_AgripreneurId(Long agripreneurId); // Use agripreneurId from Agripreneur
    ProfileVisit findByFarmer_FarmerIdAndAgripreneur_AgripreneurIdAndVisitDate(Long farmerId, Long agripreneurId, LocalDate visitDate);}
