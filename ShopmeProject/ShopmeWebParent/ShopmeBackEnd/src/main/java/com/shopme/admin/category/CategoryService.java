package com.shopme.admin.category;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Category;
import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

@Service
@Transactional
public class CategoryService {
	
	public static final int Category_PER_PAGE = 4; 
	
	@Autowired
	private CategoryRepository categoryRepo; 
	
	public List<Category> listCategoires() {
		return (List<Category>) categoryRepo.findAll(Sort.by("name").ascending()); 
	}
	
	public List<Category> listHierarchicalCategories() {
		List<Category> listHierarchey = new ArrayList<>(); 
		
		Iterable<Category> categories =  categoryRepo.findAll(); 
		
		for (Category category : categories) {
			if(category.getParent() == null) {
				listHierarchey.add(category);
				
				for (Category child : category.getChildren()) {
					child.setName("--" + child.getName()); 
					listHierarchey.add(child); 
					
					ListChildren(child, 2, listHierarchey); 
				}
			}
		}
		
		return listHierarchey; 
	}
	
	private void ListChildren(Category parent, int level, List<Category> listHierarchey) {
		for (Category child : parent.getChildren()) {
			StringBuilder hyphens = new StringBuilder(); 
			for(int i = 0; i < level; i++) 
				hyphens.append("--"); 
			
			child.setName(hyphens + child.getName()); 
			listHierarchey.add(child); 
			
			ListChildren(child, ++level, listHierarchey); 
		}
	}
	
	public Category saveCategory(Category category) {
		return categoryRepo.save(category); 
	}
	
	public boolean isNameUnique(Integer id, String name) {
		Category categoryByName = categoryRepo.findByName(name); 
		
		if(categoryByName == null) return true; 
		
		boolean isCreatingNewCategory = (id == null); 
		
		if(isCreatingNewCategory) {
			return false; 
		} else {
			if(categoryByName.getId() != id) 
				return false; 
		}
		
		return true; 
	} 
	
	public boolean isAliasUnique(Integer id, String alias) {
		Category categoryByName = categoryRepo.findByAlias(alias); 
		
		if(categoryByName == null) return true; 
		
		boolean isCreatingNewCategory = (id == null); 
		
		if(isCreatingNewCategory) {
			return false; 
		} else {
			if(categoryByName.getId() != id) 
				return false; 
		}
		
		return true; 
	} 

	public Category getCategory(Integer id) throws CategoryNotFoundException {
		try {
		Category category = categoryRepo.findById(id).get(); 
		
		return category;
		} catch(NoSuchElementException ex) {
			throw new CategoryNotFoundException("Couldn't find the category with ID = [" + id + "]"); 
		}
	}
	
	public void deleteCategory(Integer id) throws CategoryNotFoundException {
		Long countById = categoryRepo.countById(id); 
		
		if(countById == null || countById == 0) {
			throw new CategoryNotFoundException("Couldn't find Category with ID = [" + id + "]"); 
		}
		
		categoryRepo.deleteById(id); 
	}
	
	public void updateCategoryEnabledStatus(Integer id, boolean enabled) {
		categoryRepo.updateEnabledById(id, enabled);
	}
	
	public Page<Category> getCategoriesByPage(int pageNum, String sortField, String sortDir, String keyword) {
		Sort sort = Sort.by(sortField); 
		
		sort = sortDir.equals("desc") ? sort.descending() : sort.ascending(); 
		
		Pageable pageConf = PageRequest.of(pageNum, Category_PER_PAGE, sort); 
		
		if(keyword != null) 
			return categoryRepo.findAll(keyword, pageConf); 
		
		return categoryRepo.findAll(pageConf); 
	}
	
	public Category updateCategory(Category categoryInForm) {
		Category categoryInDB = categoryRepo.findById(categoryInForm.getId()).get(); 
		
		if(!categoryInForm.getImage().isEmpty()) {
			categoryInDB.setImage(categoryInForm.getImage()); 
		}
		
		categoryInDB.setName(categoryInForm.getName()); 
		categoryInDB.setAlias(categoryInForm.getAlias()); 
		
		return categoryRepo.save(categoryInDB); 
	}
}
