package com.shopme.admin.user;

import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

@Service
@Transactional
public class UserService {
	
	public static final int USER_PER_PAGE = 4; 
	
	@Autowired
	private UserRepository userRepo; 
	
	@Autowired
	private RolesRepository roleRepo; 
	
	@Autowired
	PasswordEncoder passwordEncoder; 
	
	public List<User> listUsers() {
		return (List<User>) userRepo.findAll(Sort.by("firstName").ascending()); 
	}
	
	public List<Role> listRoles() {
		return (List<Role>) roleRepo.findAll(); 
	}

	public User saveUser(User user) {
		boolean isUpdatingUser = (user.getId() != null); 
		
		if(isUpdatingUser) {
			User existingUser = userRepo.findById(user.getId()).get(); 
			
			if(user.getPassword().isEmpty()) {
				user.setPassword(existingUser.getPassword());
			} else {
				encodePassword(user);
			}
		} else {
			encodePassword(user);
		}
		
		return userRepo.save(user); 
	}
	
	private void encodePassword(User user) {
		String encodedPssword = passwordEncoder.encode(user.getPassword()); 
		user.setPassword(encodedPssword); 
	}
	
	public boolean isEmailUnique(Integer id, String email) {
		User userByEmail = userRepo.findByEmail(email); 
		
		if(userByEmail == null) return true; 
		
		boolean isCreatingNewUser = (id == null); 
		
		if(isCreatingNewUser) {
			return false; 
		} else {
			if(userByEmail.getId() != id) 
				return false; 
		}
		
		return true; 
	}

	public User getUser(Integer id) throws UserNotFoundException {
		try {
		User user = userRepo.findById(id).get(); 
		
		return user;
		} catch(NoSuchElementException ex) {
			throw new UserNotFoundException("Couldn't find user with ID = " + id); 
		}
	}
	
	public void deleteUser(Integer id) throws UserNotFoundException {
		Long countById = userRepo.countById(id); 
		
		if(countById == null || countById == 0) {
			throw new UserNotFoundException("Couldn't find user with ID = " + id); 
		}
		
		userRepo.deleteById(id); 
	}
	
	public void updateUseEnabledStatus(Integer id, boolean enabled) {
		userRepo.updateEnabledById(id, enabled);
	}
	
	public Page<User> getUsersByPage(int pageNum, String sortField, String sortDir, String keyword) {
		Sort sort = Sort.by(sortField); 
		
		sort = sortDir.equals("desc") ? sort.descending() : sort.ascending(); 
		
		Pageable pageConf = PageRequest.of(pageNum, USER_PER_PAGE, sort); 
		
		if(keyword != null) 
			return userRepo.findAll(keyword, pageConf); 
		
		return userRepo.findAll(pageConf); 
	}
	
	public User updateAccount(User userInForm) {
		User userInDB = userRepo.findById(userInForm.getId()).get(); 
		
		if(!userInForm.getPassword().isEmpty()) {
			userInDB.setPassword(userInForm.getPassword()); 
			encodePassword(userInDB); 
		}
		
		if(!userInForm.getPhotos().isEmpty()) {
			userInDB.setPhotos(userInForm.getPhotos()); 
		}
		
		userInDB.setFirstName(userInForm.getFirstName()); 
		userInDB.setLastName(userInForm.getLastName()); 
		
		return userRepo.save(userInDB); 
	}

	public User getUserByEmail(String userEmail) {
		return userRepo.findByEmail(userEmail); 
	}
}
