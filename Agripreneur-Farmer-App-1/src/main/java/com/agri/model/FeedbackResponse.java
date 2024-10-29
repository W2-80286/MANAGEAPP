package com.agri.model;

import jakarta.persistence.*;

@Entity
public class FeedbackResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "farmer_id")
    private Farmer farmer;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private FeedbackQuestion feedbackQuestion;

    private String response;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Farmer getFarmer() {
        return farmer;
    }

    public void setFarmer(Farmer farmer) {
        this.farmer = farmer;
    }

    public FeedbackQuestion getFeedbackQuestion() {
        return feedbackQuestion;
    }

    public void setFeedbackQuestion(FeedbackQuestion feedbackQuestion) {
        this.feedbackQuestion = feedbackQuestion;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
