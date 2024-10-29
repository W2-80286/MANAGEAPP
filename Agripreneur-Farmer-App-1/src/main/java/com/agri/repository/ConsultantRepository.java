package com.agri.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.agri.model.Consultant;

public interface ConsultantRepository  extends JpaRepository<Consultant, Long> {
    Consultant findByEmail(String email);

	boolean existsByMobileNumber(String mobileNumber);

    Optional<Consultant> findByMobileNumber(String mobileNumber);
}
