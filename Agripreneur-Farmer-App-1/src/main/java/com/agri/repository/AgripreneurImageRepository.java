package com.agri.repository;

import com.agri.model.Agripreneur;
import com.agri.model.AgripreneurImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgripreneurImageRepository extends JpaRepository<AgripreneurImage, Long> {
    void deleteByAgripreneur(Agripreneur agripreneur);
}
