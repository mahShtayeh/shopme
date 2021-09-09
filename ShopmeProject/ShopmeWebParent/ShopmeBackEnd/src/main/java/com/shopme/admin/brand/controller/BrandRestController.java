package com.shopme.admin.brand.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopme.admin.brand.BrandNotFoundException;
import com.shopme.admin.brand.BrandNotFoundRestException;
import com.shopme.admin.brand.BrandService;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;

@RestController
public class BrandRestController {
	
	@Autowired
	private BrandService service; 
	
	@PostMapping("/brands/check_unique") 
	public String checkCategoryDuplicate(@Param("id") Integer id, @Param("name") String name) {
		return service.isBrandUnique(id, name); 
	}
	
	@GetMapping("brands/{id}/categories")
	public List<CategoryDTO> listCategoriesByBrand(@PathVariable("id") Integer id) throws BrandNotFoundRestException {
		List<CategoryDTO> categoriesToSend = new ArrayList<>(); 
		
		try {
			Brand brand = service.getBrandById(id);
			
			Set<Category> categoriesFromDB = brand.getCategories(); 
			
			categoriesFromDB
				.stream()
				.map(category -> new CategoryDTO(category))
				.forEach(categoryDTO -> categoriesToSend.add(categoryDTO)); 
			
			return categoriesToSend; 
		} catch (BrandNotFoundException e) {
			throw new BrandNotFoundRestException(); 
		} 
	}
}
