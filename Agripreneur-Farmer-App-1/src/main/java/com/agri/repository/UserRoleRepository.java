package com.agri.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.agri.model.User;
import com.agri.model.UserRole;
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    Optional<List<UserRole>> findByUserId(Integer userId);}
	
