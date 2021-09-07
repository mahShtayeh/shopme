package com.shopme.admin.brand.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.brand.BrandNotFoundException;
import com.shopme.admin.brand.BrandService;
import com.shopme.admin.category.CategoryNotFoundException;
import com.shopme.admin.category.CategoryService;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;

@Controller
@RequestMapping("/brands")
public class BrandController {
	
	@Autowired
	private BrandService service; 
	
	@Autowired
	private CategoryService categoryService; 
	
	@GetMapping
	public String getBrands(Model model) {
		return getBrandsByPage(1, "name", "asc", model); 
	}
	
	@GetMapping("/page/{pageNum}")
	public String getBrandsByPage(
			@PathVariable int pageNum, 
			@Param("sortField") String sortField, 
			@Param("sortDir") String sortDir, 
			Model model) {
		
		Page<Brand> brandsPage = service.getBrandsByPage(pageNum, sortField, sortDir); 
		
		long startCount = (pageNum -1) * BrandService.BRANDS_BER_PAGE + 1;
		long endCount = startCount + BrandService.BRANDS_BER_PAGE - 1; 
		if(endCount > brandsPage.getTotalElements()) 
			endCount = brandsPage.getTotalElements(); 
		
		String reverseSortDir = sortDir.equals("desc") ? "asc" : "desc"; 
		
		model.addAttribute("totalPages", brandsPage.getTotalPages()); 
		model.addAttribute("currentPage", pageNum); 
		model.addAttribute("startCount", startCount); 
		model.addAttribute("endCount", endCount); 
		model.addAttribute("brandsList", brandsPage.getContent()); 
		model.addAttribute("totalItems", brandsPage.getTotalElements()); 
		model.addAttribute("sortField", sortField); 
		model.addAttribute("sortDir", sortDir); 
		model.addAttribute("reverseSortDir", reverseSortDir); 
		
		return "brands/brands";
	}
	
	@GetMapping("/new")
	public String getNewBrandForm(Model model) throws CloneNotSupportedException {
		Brand brand = new Brand(); 
		List<Category> categoriesList = categoryService.getDropDownlistCategories(); 
		
		model.addAttribute("pageTitle", "Create New Brand"); 
		model.addAttribute("brand", brand); 
		model.addAttribute("categoriesList", categoriesList); 
		
		return "brands/brands_form"; 
	}
	
	@GetMapping("/edit/{id}")
	public String editCategory(@PathVariable("id") Integer id, 
			RedirectAttributes redirectAttributes, 
			Model model) throws CloneNotSupportedException {
		try {
			Brand brand = service.getBrandById(id); 
			List<Category> categoriesList = categoryService.getDropDownlistCategories(); 
			
			model.addAttribute("brand", brand); 
			model.addAttribute("categoriesList", categoriesList); 
			model.addAttribute("pageTitle", "Edit Brand (ID: " + id + ")"); 
			
			return "brands/brands_form"; 
		} catch (BrandNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage()); 
			
			return "redirect:/brands"; 
		} 
	}
	
	@PostMapping("/save")
	public String saveBrand(Brand brand, 
			RedirectAttributes redirectAttributes, 
			@RequestParam("logoFile") MultipartFile multipartFile) throws IOException {
		
		if(!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename()); 
			
			brand.setLogo(fileName); 
			Brand savedBrand = service.saveBrand(brand); 
			
			String uploadDir = "../brands-images/" + savedBrand.getId(); 
			
			FileUploadUtil.cleanDir(uploadDir); 
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile); 
		} else {
			if(brand.getLogo().isEmpty())
				brand.setLogo(null); 
			
			service.saveBrand(brand); 
		}
		
		redirectAttributes.addFlashAttribute("message", 
				"The Brand [" + brand.getName() + "] has been saved successfully"); 
		
		return getRedirectUrlToAffectedCategory(brand); 
	} 
	
	@GetMapping("/delete/{id}")
	public String deleteBrand(@PathVariable("id") Integer id, 
			RedirectAttributes redirectAttributes) {
		try {
			service.deleteBrand(id); 
			
			String dir = "../brands-images/" + id; 
			FileUploadUtil.removeDir(dir); 
			
			redirectAttributes.addFlashAttribute("message", "The Brand with ID [" + id + "] has been deleted"); 
		} catch (BrandNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage()); 
		} 
		
		return "redirect:/brands"; 
	}
	
	private String getRedirectUrlToAffectedCategory(Brand brand) {
		String brandName = brand.getName(); 
		
		return "redirect:/brand/page/1?sortField=name&sortDir=asc";
//		return "redirect:/brand/page/1?sortField=name&sortDir=asc&keyword=" + brandName;
	}
}
