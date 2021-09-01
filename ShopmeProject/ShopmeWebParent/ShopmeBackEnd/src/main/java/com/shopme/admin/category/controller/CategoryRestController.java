package com.shopme.admin.category.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopme.admin.category.CategoryService;
import com.shopme.admin.user.UserService;

@RestController
public class CategoryRestController {
	
	@Autowired
	private CategoryService categoryService; 
	
	@PostMapping("/category/check_unique") 
	public String checkCategoryDuplicate(@Param("id") Integer id, @Param("name") String name, 
			@Param("alias") String alias) {
		return categoryService.isCategoryUnique(id, name, alias); 
	}
}
