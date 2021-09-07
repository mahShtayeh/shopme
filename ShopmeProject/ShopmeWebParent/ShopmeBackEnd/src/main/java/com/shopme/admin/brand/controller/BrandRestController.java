package com.shopme.admin.brand.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopme.admin.brand.BrandService;

@RestController
public class BrandRestController {
	
	@Autowired
	private BrandService service; 
	
	@PostMapping("/brands/check_unique") 
	public String checkCategoryDuplicate(@Param("id") Integer id, @Param("name") String name) {
		return service.isBrandUnique(id, name); 
	}
}
