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
	
	public final static int BRANDS_BER_PAGE = 4; 
	
	@Autowired
	private BrandRepository repo; 
	
	public Page<Brand> getBrandsByPage(int pageNum) {
		Sort sort = Sort.by("name").ascending(); 
		
		Pageable pageConf = PageRequest.of(pageNum - 1, BrandService.BRANDS_BER_PAGE, sort); 
		return repo.findAll(pageConf); 
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
		Brand brandByName = repo.findByName(name); 
		
		if(brandByName == null) return "OK"; 
		
		boolean isCreatingNewBrand = (id == null); 
		
		if(isCreatingNewBrand) {
			return "Dublicated"; 
		} else {
			if(brandByName.getId() != id) 
				return "Dublicated"; 
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
