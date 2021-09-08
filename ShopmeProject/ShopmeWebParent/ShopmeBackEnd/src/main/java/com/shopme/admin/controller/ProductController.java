package com.shopme.admin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.shopme.admin.product.ProductService;
import com.shopme.common.entity.Product;

@Controller
@RequestMapping(path = "/products")
public class ProductController {
	
	@Autowired
	private ProductService service; 
	
	@GetMapping
	public String getProductsList(Model model) {
		List<Product> productsList = service.getProductsList(); 
		
		model.addAttribute("productsList", productsList); 
		
		return "products/products"; 
	}
}
