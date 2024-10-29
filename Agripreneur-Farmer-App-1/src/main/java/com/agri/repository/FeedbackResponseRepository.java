package com.agri.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.agri.model.FeedbackResponse;

public interface FeedbackResponseRepository extends JpaRepository<FeedbackResponse, Long> {

}
