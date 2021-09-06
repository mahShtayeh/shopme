package com.shopme.admin.brand.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.shopme.admin.brand.BrandService;
import com.shopme.admin.category.CategoryService;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;

@Controller
public class BrandController {
	
	@Autowired
	private BrandService service; 
	
	@Autowired
	private CategoryService categoryService; 
	
	@GetMapping("/brands")
	public String getBrands(Model model) {
		return getBrandsByPage(1, model); 
	}
	
	@GetMapping("/brands/page/{pageNum}")
	public String getBrandsByPage(
			@PathVariable int pageNum, 
			Model model) {
		
		Page<Brand> brandsPage = service.getBrandsByPage(pageNum); 
		
		long startCount = (pageNum -1) * BrandService.BRANDS_BER_PAGE + 1;
		long endCount = startCount + BrandService.BRANDS_BER_PAGE - 1; 
		if(endCount > brandsPage.getTotalElements()) 
			endCount = brandsPage.getTotalElements(); 
		
		model.addAttribute("totalPages", brandsPage.getTotalPages()); 
		model.addAttribute("currentPage", pageNum); 
		model.addAttribute("startCount", startCount); 
		model.addAttribute("endCount", endCount); 
		model.addAttribute("brandsList", brandsPage.getContent()); 
		model.addAttribute("totalItems", brandsPage.getTotalElements()); 
		
		return "brands/brands";
	}
	
	@GetMapping("/brands/new")
	public String getNewBrandForm(Model model) throws CloneNotSupportedException {
		Brand brand = new Brand(); 
		List<Category> categoriesList = categoryService.getDropDownlistCategories(); 
		
		model.addAttribute("pageTitle", "Create New Brand"); 
		model.addAttribute("brand", brand); 
		model.addAttribute("categoriesList", categoriesList); 
		
		return "brands/brands_form"; 
	}
}
