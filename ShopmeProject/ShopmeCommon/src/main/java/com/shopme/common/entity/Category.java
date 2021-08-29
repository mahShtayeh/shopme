package com.shopme.common.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "categories")
public class Category {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id; 
	
	@Column(length = 128, nullable = false, unique = true)
	private String name; 
	
	@Column(length = 64, nullable = false, unique = true)
	private String alias; 
	
	@Column(length = 128, nullable = false)
	private String image; 
	
	private boolean enabled; 
	
	@OneToOne
	@JoinColumn(name = "parent_id")
	private Category parent; 
	
	@OneToMany(mappedBy = "parent")
	private Set<Category> children = new HashSet<>();
	
	public Category() {
	}
	
	public Category(Integer id) {
		this.id = id; 
	}
	
	public Category(Integer id, String name) {
		this.id = id; 
		this.name = name; 
	}

	public Category(String name, String alias, String image) {
		this(name, alias, image, false); 
	}
	
	public Category(String name, String alias, String image, Category parent) {
		this.name = name;
		this.alias = alias;
		this.image = image;
		this.parent = parent;
	}

	public Category(String name, String alias, String image, boolean enabled) {
		this.name = name; 
		this.alias = alias; 
		this.image = image; 
		this.enabled = enabled; 
	}
	
	public Category(String name, String alias, String image, Category parent, boolean enabled) {
		this.name = name;
		this.alias = alias;
		this.image = image;
		this.parent = parent;
		this.enabled = enabled;
	}
	
	public Category(String name, String alias, String image, boolean enabled, Category parent, 
			Set<Category> children) {
		this.name = name;
		this.alias = alias;
		this.image = image;
		this.enabled = enabled;
		this.parent = parent;
		this.children = children;
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

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Category getParent() {
		return parent;
	}

	public void setParent(Category parent) {
		this.parent = parent;
	}

	public Set<Category> getChildren() {
		return children;
	}

	public void setChildren(Set<Category> children) {
		this.children = children;
	} 
	
	public void addChild(Category child) {
		children.add(child); 
	}

	@Override
	public String toString() {
		return "Category [name=" + name + ", alias=" + alias + "]";
	}
	
	@Transient
	public String getImagePath() {
		return "/category-images/" + this.getId() + "/" + this.image; 
	}
}
