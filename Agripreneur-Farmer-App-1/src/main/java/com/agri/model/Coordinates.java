package com.agri.model;

import jakarta.persistence.*;

import lombok.Data;

@Entity
@Data
public class Coordinates {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double latitude;
    private double longitude;
    
    public Agripreneur getAgripreneur() {
		return agripreneur;
	}
	public void setAgripreneur(Agripreneur agripreneur) {
		this.agripreneur = agripreneur;
	}
	@OneToOne
    @JoinColumn(name = "agripreneur_id", referencedColumnName = "agripreneur_id")
    private Agripreneur agripreneur;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
    
}
