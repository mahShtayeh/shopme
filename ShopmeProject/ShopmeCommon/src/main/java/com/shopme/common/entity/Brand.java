package com.shopme.common.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "brands")
public class Brand {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id; 
	
	@Column(nullable = false, length = 45, unique = true)
	private String name; 
	
	@Column(nullable = false, length = 128)
	private String logo; 
	
	@ManyToMany
	@JoinTable( 
			name = "brands_categories", 
			joinColumns = @JoinColumn(name = "brand_id"), 
			inverseJoinColumns = @JoinColumn(name = "category_id")
			)
	private Set<Category> categories = new HashSet<>();
	
	public Brand() {
	}
	
	public Brand(Integer id) {
		this.id = id;
	}

	public Brand(String name, String logo) {
		this.name = name;
		this.logo = logo;
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

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public Set<Category> getCategories() {
		return categories;
	}

	public void setCategories(Set<Category> categories) {
		this.categories = categories;
	} 
	
	public void addCategory(Category category) {
		this.categories.add(category); 
	} 
	
	public void addCategories(Category... categories) {
		for (Category category : categories) {
			this.addCategory(category); 
		}
	}
	
	@Transient
	public String getLogoPath() {
		if(this.id == null || this.logo == null) 
			return "/images/default-logo.png"; 
		
		return "/brands-images/" + this.getId() + "/" + this.logo; 
	} 
	
	@Override
	public String toString() {
		return "Brand [id=" + id + ", name=" + name + ", logo=" + logo + ", categories=" + categories + "]";
	}
}