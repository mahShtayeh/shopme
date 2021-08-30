package com.shopme.admin.user.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import com.shopme.admin.security.ShopmeUserDetails;
import com.shopme.admin.user.UserExporter;
import com.shopme.admin.user.UserNotFoundException;
import com.shopme.admin.user.UserService;
import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

@Controller
public class UserController {
	
	@Autowired
	private UserService service; 
	
	@GetMapping("/users") 
	public String listAll(Model model) {
		return getUsersPage(1, "firstName", "asc", null, model); 
	}
	
	@GetMapping("/users/new") 
	public String newUser(Model model) {
		User user = new User();
		List<Role> listRoles = service.listRoles(); 
		
		model.addAttribute("user", user); 
		model.addAttribute("listRoles", listRoles); 
		model.addAttribute("pageTitle", "Create New User"); 
		
		return "user/user_form"; 
	}
	
	@GetMapping("/users/page/{pageNum}")
	public String getUsersPage(
			@PathVariable("pageNum") int pageNum, 
			@Param("sortField") String sortField, 
			@Param("sortDir") String sortDir, 
			@Param("keyword") String keyword, 
			Model model) {
		
		Page<User> usersPage = service.getUsersByPage(pageNum -1, sortField, sortDir, keyword); 
		
		List<User> listUsers = usersPage.getContent(); 
		
		long startCount = (pageNum -1) * UserService.USER_PER_PAGE + 1;
		long endCount = startCount + UserService.USER_PER_PAGE - 1; 
		if(endCount > usersPage.getTotalElements()) 
			endCount = usersPage.getTotalElements(); 
		
		String reverseSortDir = sortDir.equals("desc") ? "asc" : "desc"; 
		
		model.addAttribute("totalPages", usersPage.getTotalPages()); 
		model.addAttribute("currentPage", pageNum); 
		model.addAttribute("startCount", startCount); 
		model.addAttribute("endCount", endCount); 
		model.addAttribute("totalItems", usersPage.getTotalElements()); 
		model.addAttribute("sortField", sortField); 
		model.addAttribute("sortDir", sortDir); 
		model.addAttribute("reverseSortDir", reverseSortDir); 
		model.addAttribute("listUsers", listUsers); 
		model.addAttribute("keyword", keyword); 
		
		return "user/users"; 
	}
	
	@PostMapping("/users/save")
	public String saveUser(User user, 
			RedirectAttributes redirectAttributes, 
			@RequestParam("image") MultipartFile multipartFile) throws IOException {
		
		if(!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename()); 
			
			user.setPhotos(fileName); 
			User savedUser = service.saveUser(user); 
			
			String uploadDir = "user-photos/" + savedUser.getId(); 
			
			FileUploadUtil.cleanDir(uploadDir); 
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		} else {
			if(user.getPhotos().isEmpty()) 
				user.setPhotos(null); 
			
			service.saveUser(user); 
		}
		
		redirectAttributes.addFlashAttribute("message", 
				"The user [" + user.getFirstName() + "] has been saved successfully"); 
		
		return getRedirectUrlToAffectedUser(user); 
	}

	private String getRedirectUrlToAffectedUser(User user) {
		String firstPartOfEmail = user.getEmail().split("@")[0]; 
		return "redirect:/users/page/1?sortField=id&sortDir=asc&keyword=" + firstPartOfEmail;
	}
	
	@GetMapping("/users/edit/{id}")
	public String editUser(@PathVariable(name = "id") Integer id, 
			RedirectAttributes redirectAttributes, 
			Model model) {
		try {
			User user = service.getUser(id);
			List<Role> listRoles = service.listRoles(); 
			
			model.addAttribute("user", user); 
			model.addAttribute("listRoles", listRoles); 
			model.addAttribute("pageTitle", "Edit User(ID: " + id + ")"); 
			
			return "user/user_form"; 
		} catch (UserNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage()); 
			
			return "redirect:/users"; 
		} 
	}
	
	@GetMapping("/users/delete/{id}")
	public String deleteUser(@PathVariable("id") Integer id, 
			RedirectAttributes redirectAttributes) {
		try {
			service.deleteUser(id); 
			redirectAttributes.addFlashAttribute("message", "User with ID [" + id + "] has been deleted"); 
		} catch (UserNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage()); 
		} 
		
		return "redirect:/users"; 
	}
	
	@GetMapping("/users/{id}/enabled/{status}")
	public String enableUser(@PathVariable("id") Integer id, 
			@PathVariable("status") boolean status, 
			RedirectAttributes redirectAttributes) {
		service.updateUseEnabledStatus(id, status); 
		redirectAttributes.addFlashAttribute("message", "User with ID [" + id + "] has been " + 
				(status ? "Enabled" : "Disabled")); 
		
		return "redirect:/users"; 
	}
	
	@GetMapping("/users/export/csv")
	public void exportToCSV(HttpServletResponse response) throws IOException {
		List<User> usersList = service.listUsers(); 
		UserExporter.exportToCSV(usersList, response); 
	}
	
	@GetMapping("/users/export/excel")
	public void exportToExcel(HttpServletResponse response) throws IOException {
		List<User> usersList = service.listUsers(); 
		UserExporter.exportToExcel(usersList, response); 
	}
	
	@GetMapping("/users/export/pdf")
	public void exportToPDF(HttpServletResponse response) throws IOException {
		List<User> usersList = service.listUsers(); 
		UserExporter.exportToPDF(usersList, response); 
	}

	@PostMapping("/account/update")
	public String updateAccount(User user, 
			@AuthenticationPrincipal ShopmeUserDetails loggedUser, 
			RedirectAttributes redirectAttributes, 
			@RequestParam("image") MultipartFile multipartFile) throws IOException {
		
		if(!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename()); 
			
			user.setPhotos(fileName); 
			User savedUser = service.updateAccount(user); 
			
			String uploadDir = "user-photos/" + savedUser.getId(); 
			
			FileUploadUtil.cleanDir(uploadDir); 
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		} else {
			if(user.getPhotos().isEmpty()) 
				user.setPhotos(null); 
			
			service.updateAccount(user); 
		}
		
		loggedUser.setFirstName(user.getFirstName());
		loggedUser.setLastName(user.getLastName());
		
		redirectAttributes.addFlashAttribute("message", 
				"Your account details have been updated"); 
		
		return "redirect:/account"; 
	}
}
