package com.shopme.admin.category;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.shopme.common.entity.Category;

public interface CategoryRepository extends PagingAndSortingRepository<Category, Integer>{
	public Category findByName(String name); 
	public Category findByAlias(String alias);
	public Long countById(Integer id);
	
	@Query("SELECT c "
			+ "FROM Category c "
			+ "WHERE CONCAT(c.id, ' ', c.name, ' ', c.alias) "
			+ "LIKE %?1%")
	public List<Category> findAll(String keyword);
	
	@Query("UPDATE Category c SET c.enabled = ?2 WHERE c.id = ?1")
	@Modifying
	public void updateEnabledById(Integer id, boolean enabled); 
}