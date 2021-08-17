package com.shopme.admin.user;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

@Controller
public class UserController {
	
	@Autowired
	private UserService service; 
	
	@GetMapping("/users") 
	public String listAll(Model model) {
		List<User> listUsers = service.listUsers(); 
		
		model.addAttribute("listUsers", listUsers); 
		
		return "users"; 
	}
	
	@GetMapping("/users/new") 
	public String newUser(Model model) {
		User user = new User();
		List<Role> listRoles = service.listRoles(); 
		
		model.addAttribute("user", user); 
		model.addAttribute("listRoles", listRoles); 
		model.addAttribute("pageTitle", "Create New User"); 
		
		return "user_form"; 
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
			service.saveUser(user); 
		}
		
		redirectAttributes.addFlashAttribute("message", 
				"The user [" + user.getFirstName() + "] has been added successfully"); 
		
		return "redirect:/users"; 
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
			
			return "user_form"; 
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
}
