package com.shopme.admin.category;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Category;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CategoryRepositroyTest {
	
	@Autowired
	private CategoryRepository repo; 
	
	@Test
	public void createRootCategoryTest() {
		Category rootCategory = new Category("Electronics", "electronics", "default.png"); 
		Category savedCategory = repo.save(rootCategory); 
		
		assertThat(savedCategory.getId()).isGreaterThan(0); 
	}
	
	@Test
	public void createSubCategoryTest() {
		Category rootCategory = new Category(7); 
		Category subCategory = new Category("iPhone", "iphone", "default.png", rootCategory); 
		Category savedCategory = repo.save(subCategory); 
		
		assertThat(savedCategory.getId()).isGreaterThan(0); 
	}
	
	@Test
	public void retriveCategoryTest() {
		Category category = repo.findById(1).get(); 
		
		System.out.println(category);
		
		for (Category child : category.getChildren()) {
			System.out.println(child);
		}
		
		assertThat(category.getChildren().size()).isGreaterThan(0); 
	}
	
	@Test
	public void printingHierarchicalCategory() {
		Iterable<Category> categories = repo.findAll(); 
		
		for (Category category : categories) {
			if(category.getParent() == null) {
				System.out.println(category.getName());
				
				for (Category child : category.getChildren()) {
					System.out.println("--" + child.getName()); 
					
					printChildern(child, 2); 
				}
			}
		}
	}
	
	private void printChildern(Category parent, int level) {
		for (Category child : parent.getChildren()) {
			for(int i = 0; i < level; i++) 
				System.out.print("--");
			System.out.println(child.getName()); 
			
			printChildern(child, ++level); 
		}
	}
	
	@Test 
	public void getCategoryByNameTest() {
		String name = "Computers"; 
		
		Category category = repo.findByName(name); 
		
		assertThat(category).isNotNull(); 
		assertThat(category.getName().equals(name)); 
	}
	
	@Test 
	public void getCategoryByAliasTest() {
		String alias = "electronics"; 
		
		Category category = repo.findByAlias(alias); 
		
		assertThat(category).isNotNull(); 
		assertThat(category.getName().equals(alias)); 
	}
}
