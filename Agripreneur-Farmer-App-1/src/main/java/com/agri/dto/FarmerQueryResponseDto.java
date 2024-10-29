package com.agri.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class FarmerQueryResponseDto {
	
    @NotNull(message = "Query ID is mandatory")
    private Long queryId;  // ID of the query being responded to

    @NotBlank(message = "Response Text is mandatory")
    @Size(min = 1, max = 500, message = "Response Text must be between 1 and 500 characters")
    private String responseText;
public Long getQueryId() {
		return queryId;
	}
	public void setQueryId(Long queryId) {
		this.queryId = queryId;
	}
	public String getResponseText() {
		return responseText;
	}
	public void setResponseText(String responseText) {
		this.responseText = responseText;
	}
    
    
    
    
}
