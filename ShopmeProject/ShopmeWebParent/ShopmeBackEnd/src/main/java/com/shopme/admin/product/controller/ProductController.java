package com.shopme.admin.product.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.shopme.admin.brand.BrandService;
import com.shopme.admin.product.ProductService;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Product;

@Controller
@RequestMapping(path = "/products")
public class ProductController {
	
	@Autowired
	private ProductService service; 
	
	@Autowired
	private BrandService brandService; 
	
	@GetMapping
	public String getProductsList(Model model) {
		List<Product> productsList = service.getProductsList(); 
		
		model.addAttribute("productsList", productsList); 
		
		return "products/products"; 
	}
	
	@GetMapping("/new") 
	public String getNewProductForm(Model model) {
		List<Brand> brandsList = brandService.getBrandsListOrderedByName(); 
		
		Product product = new Product(); 
		product.setEnabled(true); 
		product.setInStack(true); 
		
		model.addAttribute("product", product); 
		model.addAttribute("brandsList", brandsList); 
		model.addAttribute("pageTitle", "Create New Product"); 
		
		return "products/product_form"; 
	}
	
	@PostMapping("/save")
	public String saveProduct(Product product) {
		return "redirect:/products"; 
	}
}
