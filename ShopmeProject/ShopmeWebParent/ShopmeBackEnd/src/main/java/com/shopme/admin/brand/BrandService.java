package com.shopme.admin.brand;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.admin.category.CategoryNotFoundException;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.User;

@Service
public class BrandService {
	
	public final static int BRANDS_BER_PAGE = 2; 
	
	@Autowired
	private BrandRepository repo; 
	
	public Page<Brand> getBrandsByPage(int pageNum, String sortField, String sortDir, String keyword) {
		Sort sort = Sort.by(sortField); 
		
		sort = sortDir.equals("desc") ? sort.descending() : sort.ascending(); 
		
		Pageable pageConf = PageRequest.of(pageNum - 1, BrandService.BRANDS_BER_PAGE, sort); 
		
		if(keyword == null) 
			return repo.findAll(pageConf); 
		
		return repo.searchForBrand(keyword, pageConf); 
	}
	
	public Brand getBrandById(Integer id) throws BrandNotFoundException {
		try {
			Brand brand = repo.findById(id).get(); 
			
			return brand; 
		} catch (NoSuchElementException ex) {
			throw new BrandNotFoundException("Couldn't find the brand with ID = [" + id + "]");
		}
	}
	
	public Brand saveBrand(Brand brand) {
		return repo.save(brand); 
	}
	
	public String isBrandUnique(Integer id, String name) {
		boolean isCreatingNewBrand = (id == null || id == 0); 
		
		Brand brandByName = repo.findByName(name); 
		
		if(isCreatingNewBrand) {
			if(brandByName != null) return "Dublicated"; 
		} else {
			if(brandByName != null && brandByName.getId() != id) return "Dublicated"; 
		}
		
		return "OK"; 
	}

	
	public void deleteBrand(Integer id) throws BrandNotFoundException {
		Long countById = repo.countById(id); 

		if (countById == null || countById == 0) {
			throw new BrandNotFoundException("Couldn't find Brand with ID = [" + id + "]");
		}

		repo.deleteById(id);
	}
}
