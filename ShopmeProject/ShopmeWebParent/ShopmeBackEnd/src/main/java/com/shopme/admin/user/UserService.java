package com.shopme.admin.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

@Service
public class UserService {
	
	@Autowired
	private UserRepository userRepo; 
	
	@Autowired
	private RolesRepository roleRepo; 
	
	@Autowired
	PasswordEncoder passwordEncoder; 
	
	public List<User> listUsers() {
		return (List<User>) userRepo.findAll(); 
	}
	
	public List<Role> listRoles() {
		return (List<Role>) roleRepo.findAll(); 
	}

	public void saveUser(User user) {
		encodePassword(user);
		userRepo.save(user); 
	}
	
	private void encodePassword(User user) {
		String encodedPssword = passwordEncoder.encode(user.getPassword()); 
		user.setPassword(encodedPssword); 
	}
	
	public boolean isEmailUnique(String email) {
		User userByEmail = userRepo.getUserByEmail(email); 
		
		return userByEmail == null; 
	}
}
