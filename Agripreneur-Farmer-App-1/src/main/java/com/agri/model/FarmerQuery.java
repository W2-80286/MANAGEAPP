package com.agri.model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class FarmerQuery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long queryId;

    @ManyToOne
    @JoinColumn(name = "farmer_id", nullable = false)
    @JsonBackReference
    private Farmer farmer;

    @ManyToOne
    @JoinColumn(name = "agripreneur_id", nullable = false)
    @JsonBackReference
    private Agripreneur agripreneur;

    private String queryText;

    @OneToMany(mappedBy = "farmerquery", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<QueryImage> images;

    private String response ;  // Agripreneur's response to the query
    
    private String status;
    
    
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	// Getters and Setters
    public Long getQueryId() {
        return queryId;
    }

    public void setQueryId(Long queryId) {
        this.queryId = queryId;
    }

    public Farmer getFarmer() {
        return farmer;
    }

    public void setFarmer(Farmer farmer) {
        this.farmer = farmer;
    }

    public Agripreneur getAgripreneur() {
        return agripreneur;
    }

    public void setAgripreneur(Agripreneur agripreneur) {
        this.agripreneur = agripreneur;
    }

    public String getQueryText() {
        return queryText;
    }

    public void setQueryText(String queryText) {
        this.queryText = queryText;
    }

    public List<QueryImage> getImages() {
        return images;
    }

    public void setImages(List<QueryImage> images) {
        this.images = images;
    }
}
