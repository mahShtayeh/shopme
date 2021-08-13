package com.shopme.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
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
}
