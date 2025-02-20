package com.agri.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.agri.model.State;
@Repository
public interface StateRepository extends JpaRepository<State, Long> {
	
}
