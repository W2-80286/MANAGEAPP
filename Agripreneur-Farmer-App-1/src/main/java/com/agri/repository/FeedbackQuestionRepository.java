package com.agri.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.agri.model.FeedbackQuestion;

@Repository
public interface FeedbackQuestionRepository extends JpaRepository<FeedbackQuestion, Long> {
	
}
