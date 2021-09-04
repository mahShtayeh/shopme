package com.shopme.admin.category.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.category.CategoryExporter;
import com.shopme.admin.category.CategoryNotFoundException;
import com.shopme.admin.category.CategoryService;
import com.shopme.common.entity.Category;

@Controller
public class CategoryController {
	
	@Autowired
	private CategoryService service; 
	
	@GetMapping("/category") 
	public String listAll(Model model) throws CloneNotSupportedException {
		return getCategoriesPage(1, "name", "asc", null, model); 
	}
	
	@GetMapping("/category/new") 
	public String newCategory(Model model) throws CloneNotSupportedException {
		List<Category> categoriesList = service.listHierarchicalCategories("name", "asc", "--"); 
		
		model.addAttribute("category", new Category()); 
		model.addAttribute("categoriesList", categoriesList); 
		model.addAttribute("pageTitle", "Create New Category"); 
		
		return "category/category_form"; 
	}
	
	@GetMapping("/category/page/{pageNum}")
	public String getCategoriesPage(
			@PathVariable("pageNum") int pageNum, 
			@Param("sortField") String sortField, 
			@Param("sortDir") String sortDir, 
			@Param("keyword") String keyword, 
			Model model) throws CloneNotSupportedException {
		
		List<Category> categoriesList; 
		Page<Category> categoriesPage; 
		
		if(keyword != null && !keyword.isEmpty()) {
			categoriesPage = service.listCategoriesPage(pageNum, sortField, sortDir, keyword); 
			categoriesList = categoriesPage.getContent(); 
		} else {
			List<Object> pair = service.listHierarchicalCategoriesByPage(pageNum, sortField, sortDir);; 
			categoriesList = (List<Category>) pair.get(0); 
			categoriesPage = (Page<Category>) pair.get(1); 
		}
		
		long startCount = (pageNum -1) * CategoryService.ROOT_CATEGORIES_PER_PAGE + 1;
		long endCount = startCount + CategoryService.ROOT_CATEGORIES_PER_PAGE - 1; 
		if(endCount > categoriesPage.getTotalElements()) 
			endCount = categoriesPage.getTotalElements(); 
		
		String reverseSortDir = sortDir.equals("desc") ? "asc" : "desc"; 
		
		model.addAttribute("totalPages", categoriesPage.getTotalPages()); 
		model.addAttribute("currentPage", pageNum); 
		model.addAttribute("startCount", startCount); 
		model.addAttribute("endCount", endCount); 
		model.addAttribute("totalItems", categoriesPage.getTotalElements()); 
		model.addAttribute("sortField", sortField); 
		model.addAttribute("sortDir", sortDir); 
		model.addAttribute("reverseSortDir", reverseSortDir); 
		model.addAttribute("categoriesList", categoriesList); 
		model.addAttribute("keyword", keyword); 
		
		return "category/categories"; 
	}
	
	@PostMapping("/category/save")
	public String saveCategory(Category category, 
			RedirectAttributes redirectAttributes, 
			@RequestParam("fileImage") MultipartFile multipartFile) throws IOException {
		
		if(!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename()); 
			
			category.setImage(fileName); 
			Category savedCategory = service.saveCategory(category); 
			
			String uploadDir = "../category-images/" + savedCategory.getId(); 
			
			FileUploadUtil.cleanDir(uploadDir); 
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile); 
		} else {
			if(category.getImage().isEmpty())
				category.setImage(null);  
			
			service.saveCategory(category); 
		}
		
		redirectAttributes.addFlashAttribute("message", 
				"The category [" + category.getName() + "] has been saved successfully"); 
		
		return getRedirectUrlToAffectedCategory(category); 
	}

	private String getRedirectUrlToAffectedCategory(Category category) {
		String categoryName = category.getName(); 
		return "redirect:/category/sort?sortField=name&sortDir=asc&keyword=" + categoryName;
	}
	
	@GetMapping("/category/edit/{id}")
	public String editCategory(@PathVariable(name = "id") Integer id, 
			RedirectAttributes redirectAttributes, 
			Model model) throws CloneNotSupportedException {
		try {
			Category category = service.getCategory(id);
			List<Category> categoriesList = service.getDropDownlistCategories(); 
			
			model.addAttribute("category", category); 
			model.addAttribute("categoriesList", categoriesList);
			model.addAttribute("pageTitle", "Edit Category(ID: " + id + ")"); 
			
			return "category/category_form"; 
		} catch (CategoryNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage()); 
			
			return "redirect:/category"; 
		} 
	}
	
	@GetMapping("/category/delete/{id}")
	public String deleteCategory(@PathVariable("id") Integer id, 
			RedirectAttributes redirectAttributes) {
		try {
			service.deleteCategory(id); 
			
			String dir = "../category-images/" + id; 
			FileUploadUtil.removeDir(dir); 
			
			redirectAttributes.addFlashAttribute("message", "The category with ID [" + id + "] has been deleted"); 
		} catch (CategoryNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage()); 
		} 
		
		return "redirect:/category"; 
	}
	
	@GetMapping("/category/{id}/enabled/{status}")
	public String enableCategory(@PathVariable("id") Integer id, 
			@PathVariable("status") boolean status, 
			RedirectAttributes redirectAttributes) {
		service.updateCategoryEnabledStatus(id, status); 
		redirectAttributes.addFlashAttribute("message", "The category with ID [" + id + "] has been " + 
				(status ? "Enabled" : "Disabled")); 
		
		return "redirect:/category"; 
	}
	
	@GetMapping("/category/export/csv")
	public void exportToCSV(HttpServletResponse response) throws IOException, CloneNotSupportedException {
		List<Category> categoriesList = service.listHierarchicalCategories("name", "asc", "  "); 
		CategoryExporter.exportToCSV(categoriesList, response); 
	}
	
	@GetMapping("/category/export/excel")
	public void exportToExcel(HttpServletResponse response) throws IOException {
		List<Category> categoriesList = service.listCategories("name", "asc"); 
		CategoryExporter.exportToExcel(categoriesList, response); 
	}
	
	@GetMapping("/category/export/pdf")
	public void exportToPDF(HttpServletResponse response) throws IOException {
		List<Category> categoriesList = service.listCategories("name", "asc"); 
		CategoryExporter.exportToPDF(categoriesList, response); 
	}
}
