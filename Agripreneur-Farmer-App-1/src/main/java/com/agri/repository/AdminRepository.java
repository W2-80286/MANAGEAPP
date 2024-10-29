package com.agri.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.agri.model.Admin;
import com.agri.model.Consultant;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    Admin findByFullName(String fullName);
    Admin findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByMobileNumber(String mobileNumber);
    Optional<Admin> findByMobileNumber(String mobileNumber);

    Optional<Admin> findByEmailOrMobileNumberOrFullName(String email, String mobileNumber, String fullName);
}
