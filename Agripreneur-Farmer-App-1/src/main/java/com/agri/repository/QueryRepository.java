package com.agri.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // Make sure this is imported
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.agri.model.FarmerQuery;

@Repository
public interface QueryRepository extends JpaRepository<FarmerQuery, Long> {

  //  @Query("SELECT q FROM FarmerQuery q WHERE q.agripreneur.agripreneurId = :agripreneurId")
    // List<FarmerQuery> findFarmerQueriesByAgripreneurId(@Param("agripreneurId") Long agripreneurId);
    List<FarmerQuery> findByAgripreneur_AgripreneurId(Long agripreneurId);
    
}
