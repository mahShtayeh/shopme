package com.shopme.admin.brand;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class BrandRepositoryTest {
	
	@Autowired
	private BrandRepository repo; 
	
	@Test
	public void testCreateBrandWithOneCategory() {
		Brand acer = new Brand("acer", "brand_logo.png"); 
		acer.addCategory(new Category(6));
		
		Brand savedBrand = repo.save(acer);
		
		assertThat(savedBrand.getId()).isGreaterThan(0); 
	}
	
	@Test
	public void testCreateBrandWithTwoCategories() {
		Brand apple = new Brand("apple", "brand_logo.png"); 
		apple.addCategories(new Category(4), new Category(7));
		
		Brand savedBrand = repo.save(apple);
		
		assertThat(savedBrand.getId()).isGreaterThan(0); 
		assertThat(savedBrand.getCategories().size()).isEqualTo(2);  
	}
	
	@Test
	public void testCreateBrandWithManyCategories() {
		Brand samsung = new Brand("Samsung", "brand_logo.png"); 
		samsung.addCategories(new Category(29), new Category(24), new Category(10)); 
		
		Brand savedBrand = repo.save(samsung);
		
		assertThat(savedBrand.getId()).isGreaterThan(0); 
		assertThat(savedBrand.getCategories().size()).isEqualTo(3);  
	}
	
	@Test
	public void testListBrandsDetails() {
		 Iterable<Brand> allBrands = repo.findAll(); 
		 
		 for (Brand brand : allBrands) {
			System.out.println(brand);
		}
	}
	
	@Test
	public void testGetBrandById() {
		 Brand brand = repo.findById(1).get(); 
		 
		 System.out.println(brand);
	}
	
	@Test
	public void testUpdateBrand() {
		 Brand samsung = repo.findById(3).get(); 
		 samsung.setName("Samsung Electronics"); 
		 
		 Brand savedBrand = repo.save(samsung); 
		 
		 System.out.println(savedBrand);
	}
	
	@Test
	public void testDeleteBrand() {
		 Brand apple = repo.findById(2).get(); 
		 
		 repo.delete(apple); 
	}
}
