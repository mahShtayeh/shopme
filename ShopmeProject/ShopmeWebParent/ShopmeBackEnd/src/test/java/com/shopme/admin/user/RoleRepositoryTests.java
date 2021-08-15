package com.shopme.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Role;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class RoleRepositoryTests {
	
	@Autowired
	private RolesRepository repo; 
	
	@Test
	public void testCreateFirstRole() {
		Role adminRole = new Role("Admin", "Manage Everything"); 
		
		Role savedRole = repo.save(adminRole); 
		
		assertThat(savedRole.getId()).isGreaterThan(0); 
	}
	
	@Test
	public void testCreateRestRole() {
		Role salesPersonRole = new Role("Salesperson", 
				"Manage product price, customers, shipping, orders and sales report"); 
		
		Role editorRole = new Role("Editor", 
				"Manage categories, brands, products, articles and menus");
		
		Role shipperRole = new Role("Shipper", 
				"View products, view orders and update orders status"); 
		
		Role assistantRole = new Role("Assistant", 
				"Manage questions and reviews"); 
		
		repo.saveAll(List.of(salesPersonRole, editorRole, shipperRole, assistantRole)); 
	}
}
