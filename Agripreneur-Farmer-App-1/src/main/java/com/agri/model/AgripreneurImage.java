package com.agri.model;


import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class AgripreneurImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "agripreneur_id", nullable = false)
    @JsonBackReference
    private Agripreneur agripreneur;

    private String imagePath;

	public AgripreneurImage(Long id, Agripreneur agripreneur, String imagePath) {
		super();
		this.id = id;
		this.agripreneur = agripreneur;
		this.imagePath = imagePath;
	}

	public AgripreneurImage() {
		// TODO Auto-generated constructor stub
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Agripreneur getAgripreneur() {
		return agripreneur;
	}

	public void setAgripreneur(Agripreneur agripreneur) {
		this.agripreneur = agripreneur;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
    
}
