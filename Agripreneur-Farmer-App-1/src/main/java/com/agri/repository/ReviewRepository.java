package com.agri.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.agri.model.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
