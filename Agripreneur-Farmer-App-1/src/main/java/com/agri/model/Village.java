package com.agri.model;


import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Village {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "village_id")
    private Long villageId;

    private String name;

    @ManyToOne
    @JoinColumn(name = "state_id", nullable = false)
    @JsonBackReference
    private State state;

    @ManyToOne
    @JoinColumn(name = "district_id", nullable = false)
    @JsonBackReference
    private District district;

    @ManyToOne
    @JoinColumn(name = "sub_district_id", nullable = false)
    private SubDistrict subDistrict;
    

	public Village() {
	}

	public Village(Long villageId, String name, State state, District district, SubDistrict subDistrict) {
		super();
		this.villageId = villageId;
		this.name = name;
		this.state = state;
		this.district = district;
		this.subDistrict = subDistrict;
	}

	public Long getVillageId() {
		return villageId;
	}

	public void setVillageId(Long villageId) {
		this.villageId = villageId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public District getDistrict() {
		return district;
	}

	public void setDistrict(District district) {
		this.district = district;
	}

	public SubDistrict getSubDistrict() {
		return subDistrict;
	}

	public void setSubDistrict(SubDistrict subDistrict) {
		this.subDistrict = subDistrict;
	}
    
}
