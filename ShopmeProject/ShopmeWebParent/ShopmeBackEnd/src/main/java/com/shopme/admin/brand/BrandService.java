package com.shopme.admin.brand;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Brand;

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
}
