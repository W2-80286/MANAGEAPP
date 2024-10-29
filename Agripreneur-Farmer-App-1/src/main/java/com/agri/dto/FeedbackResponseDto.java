package com.agri.dto;

public class FeedbackResponseDto {
    
	private Long id;
    private String farmerName;
    private String question;
    private String response;

   
   
	public FeedbackResponseDto(Long id, String farmerName, String question, String response) {
		super();
		this.id = id;
		this.farmerName = farmerName;
		this.question = question;
		this.response = response;
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	// Getters and Setters
    public String getFarmerName() {
        return farmerName;
    }

    public void setFarmerName(String farmerName) {
        this.farmerName = farmerName;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
