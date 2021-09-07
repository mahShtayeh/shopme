package com.shopme.admin.brand;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;

public interface BrandRepository extends PagingAndSortingRepository<Brand, Integer> {
	public Page<Brand> findAll(Pageable pageable); 
	public Brand findByName(String name); 
	public Long countById(Integer id);
	
	@Query("SELECT b "
		 + "FROM Brand b "
		 + "WHERE CONCAT(b.id, ' ', b.name) "
		 + "LIKE %?1%")
	public Page<Brand> searchForBrand(String keyword, Pageable pageable);  
}