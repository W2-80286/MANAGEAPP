package com.agri.dto;

import java.util.List;

public class FarmerServiceSelectionDto {
	private Long farmerId;
	private List<Long>categoryIds;
	public Long getFarmerId() {
		return farmerId;
	}
	public void setFarmerId(Long farmerId) {
		this.farmerId = farmerId;
	}
	public List<Long> getCategoryIds() {
		return categoryIds;
	}
	public void setCategoryIds(List<Long> categoryIds) {
		this.categoryIds = categoryIds;
	}
	

}
