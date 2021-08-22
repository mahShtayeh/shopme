package com.shopme.admin.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.shopme.admin.security.ShopmeUserDetails;
import com.shopme.common.entity.User;

@Controller
public class AccountController {

	@Autowired
	private UserService service; 
	
	@GetMapping("/account")
	public String viewAccountDetials(@AuthenticationPrincipal ShopmeUserDetails loggedUser, 
			Model model) {
		String userEmail = loggedUser.getUsername(); 
		User userLatestUpdate = service.getUserByEmail(userEmail); 
		
		model.addAttribute("user", userLatestUpdate); 
		
		return "account_form"; 
	}
}