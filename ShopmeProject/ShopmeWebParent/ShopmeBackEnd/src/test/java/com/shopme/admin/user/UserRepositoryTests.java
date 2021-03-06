package com.shopme.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import javax.print.attribute.standard.PageRanges;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class UserRepositoryTests {
	
	@Autowired
	private UserRepository repo; 
	
	@Autowired
	private TestEntityManager entityManager; 
	
	@Test
	public void testCreateUserOneRole() {
		Role adminRole = entityManager.find(Role.class, 1); 
		User newUser = new User("newUser@email.com", "pass", "New", "User"); 
		newUser.addRole(adminRole); 
		
		User savedUser = repo.save(newUser); 
		
		assertThat(savedUser.getId()).isGreaterThan(0); 
	}
	
	@Test
	public void testCreateUserTwoRoles() {
		User newUser = new User("TwoRolesUser@email.com", "pass", "Two", "RolesUser"); 
		
		Role editorRole = new Role(3); 
		Role assistantRole = new Role(5); 
		
		newUser.addRole(editorRole); 
		newUser.addRole(assistantRole); 
		
		User savedUser = repo.save(newUser); 
		
		assertThat(savedUser.getId()).isGreaterThan(0); 
		assertThat(savedUser.getRoles().size()).isEqualTo(2); 
	}
	
	@Test
	public void testListAllUsers() {
		Iterable<User> allUsers = repo.findAll();
		allUsers.forEach(user -> System.out.println(user));
	}
	
	@Test
	public void testGetUserByID() {
		User user = repo.findById(1).get(); 
		System.out.println(user);
		assertThat(user).isNotNull(); 
	} 
	
	@Test
	public void testUpdateUserDetials() {
		User user = repo.findById(2).get(); 
		user.setEnabled(true);
		user.addRole(new Role(2)); 
		
		repo.save(user); 
	}
	
	@Test
	public void testRemoveUserRole() {
		User user = repo.findById(2).get(); 
		
		user.getRoles().remove(new Role(3)); 
		
		repo.save(user); 
	}
	
	@Test
	public void testDeleteUser() {
		Integer userId = 2; 
		
		repo.deleteById(userId); 
	}
	
	@Test 
	public void testGetUserByEmail() {
		String email = "newUser@email.com"; 
		User user = repo.findByEmail(email); 
		
		assertThat(user).isNotNull(); 
	}
	
	@Test
	public void testCountUserById() {
		Integer id = 1; 
		Long countById = repo.countById(id);
		
		assertThat(countById).isNotNull().isGreaterThan(0); 
	}
	
	@Test
	public void updateUserEnabledStatus() {
		repo.updateEnabledById(4, false);
	} 
	
	@Test 
	public void testPagenation() {
		int pageNum = 1; 
		int pageSize = 4; 
		
		Pageable page = PageRequest.of(pageNum, pageSize); 
		
		Page<User> usersPage = repo.findAll(page); 
		
		List<User> usersList = usersPage.getContent(); 
		
		usersList.forEach(user -> System.out.println(user));
		
		assertThat(usersList.size()).isEqualTo(pageSize); 
	}
	
	@Test
	public void testSearchUser() {
		String keyword = "gmail"; 
		
		int pageNum = 0; 
		int pageSize = 4; 
		
		Pageable pageable = PageRequest.of(pageNum, pageSize); 
		Page<User> page = repo.findAll(keyword, pageable); 
		
		List<User> userList = page.getContent(); 
		
		userList.forEach(user -> System.out.println(user));
		
		assertThat(userList.size()).isGreaterThan(0); 
	}
}
