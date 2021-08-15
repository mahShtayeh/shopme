package com.shopme.admin.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
		
		return "user_form"; 
	}
	
	@PostMapping("/users/save")
	public String saveUser(User user, RedirectAttributes redirectAttributes) {
		service.saveUser(user); 
		
		redirectAttributes.addFlashAttribute("message", 
				"The user [" + user.getFirstName() + "] has been added successfully"); 
		
		return "redirect:/users"; 
	}
}
