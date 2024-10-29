package com.agri.model;


import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;

@Entity
@Table(name = "query_images")
public class QueryImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long imageId;

    @ManyToOne
    @JoinColumn(name = "query_id", nullable = false)
    @JsonBackReference
    private FarmerQuery farmerquery;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

	public Long getImageId() {
		return imageId;
	}

	public void setImageId(Long imageId) {
		this.imageId = imageId;
	}

	public FarmerQuery getFarmerquery() {
		return farmerquery;
	}

	public void setFarmerquery(FarmerQuery farmerquery) {
		this.farmerquery = farmerquery;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

    }
