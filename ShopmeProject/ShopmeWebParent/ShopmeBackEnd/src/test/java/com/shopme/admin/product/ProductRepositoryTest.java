package com.shopme.admin.product;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Product;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class ProductRepositoryTest {
	
	@Autowired
	private ProductRepository repo; 
	
	@Autowired
	private TestEntityManager entityManager; 
	
	@Test
	public void createProductTest() {
		Brand brand = entityManager.find(Brand.class, 6); 
		Category category = entityManager.find(Category.class, 4); 
		
		Product product = new Product(); 
		product.setName("IPhone X"); 
		product.setAlias("iphone_x"); 
		product.setShortDescription("IPhone X Short Description"); 
		product.setFullDescription("IPhone X Full Description"); 
		
		product.setBrand(brand); 
		product.setCategory(category); 
		
		product.setPrice(1250); 
		product.setCost(500);
		product.setCreatedAt(new Date()); 
		product.setLastModifiedAt(new Date()); 
		
		product.setEnabled(true);
		product.setInStack(true);
		
		Product savedProduct = repo.save(product); 
		
		assertThat(savedProduct).isNotNull(); 
		assertThat(savedProduct.getId()).isGreaterThan(0); 
	}
	
	@Test
	public void listAllProductsTest() {
		Iterable<Product> allProducts = repo.findAll(); 
		
		allProducts.forEach(System.out::println); 
	}
	
	@Test
	public void getProductTest() {
		Integer id = 2; 
		Product product = repo.findById(id).get(); 
		
		System.out.println(product); 
		
		assertThat(product).isNotNull(); 
	}
	
	@Test
	public void updateProductTest() {
		Integer id = 2; 
		float price = 5000f; 
		
		Product product = repo.findById(id).get(); 
		
		product.setPrice(price); 
		
		Product savedProduct = repo.save(product); 
		
		assertThat(savedProduct.getPrice()).isEqualTo(price); 
	}
	
	@Test
	public void deleteProductTest() {
		Integer id = 4; 
		
		repo.deleteById(id); 
		
		Optional<Product> optional = repo.findById(id); 
		
		assertThat(optional.isEmpty()); 
	}
}
