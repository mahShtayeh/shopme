package com.shopme.admin.brand.controller;

import com.shopme.common.entity.Category;

public class CategoryDTO {
	
	private Integer id; 
	private String name;
	
	public CategoryDTO() {
	}
	
	public CategoryDTO(Category category) {
		this(category.getId(), category.getName()); 
	}
	
	public CategoryDTO(Integer id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	} 
}
