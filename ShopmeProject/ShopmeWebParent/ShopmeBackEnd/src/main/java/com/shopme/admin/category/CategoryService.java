package com.shopme.admin.category;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.transaction.Transactional;

import org.aspectj.weaver.bcel.UnwovenClassFile.ChildClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Category;

@Service
@Transactional
public class CategoryService {

	public static final int ROOT_CATEGORIES_PER_PAGE = 2; 
	public static final int SEARCH_RESULT_CATEGORIES_PER_PAGE = 4;
	
	@Autowired
	private CategoryRepository categoryRepo; 
	
	public List<Object> listHierarchicalCategoriesByPage(int pageNum, String sortField, String sortDir) throws CloneNotSupportedException {
		Sort sort = Sort.by(sortField); 
		sort = sortDir.equals("desc") ? sort.descending() : sort.ascending(); 
		
		Pageable pageConf = PageRequest.of(pageNum - 1, ROOT_CATEGORIES_PER_PAGE, sort); 
		
		Page<Category> pageOfCategories = categoryRepo.findByParent(null, pageConf); 
		List<Category> rootCategories = pageOfCategories.getContent(); 
		
		return List.of(listHierarchicalRootCategories(rootCategories, sortField, sortDir), pageOfCategories); 
	}

	public List<Category> listCategories(String sortField, String sortDir) {
		Sort sort = Sort.by(sortField); 
		sort = (sortDir.equals("desc") ? sort.descending() : sort.ascending()); 
		
		return (List<Category>) categoryRepo.findAll(sort); 
	} 
	
	public Page<Category> listCategoriesPage(int pageNum, String sortField, String sortDir, String keyword) {
		Sort sort = Sort.by(sortField); 
		sort = (sortDir.equals("desc") ? sort.descending() : sort.ascending()); 
		
		Pageable pageConf = PageRequest.of(pageNum - 1,  SEARCH_RESULT_CATEGORIES_PER_PAGE, sort); 
		
		return categoryRepo.searchForCategory(keyword, pageConf); 
	}
	
	public List<Category> listHierarchicalRootCategories(List<Category> categories, String sortField, String sortDir) throws CloneNotSupportedException {
		List<Category> listHierarchey = new ArrayList<>(); 
		
		for (Category category : categories) {
			if (category.getParent() == null) {
				listHierarchey.add(category.clone()); 

				SortedSet<Category> sortedChildren = this.sortChildren(category.getChildren(), "asc");

				for (Category child : sortedChildren) {
					Category childClone = child.clone(); 
					childClone.setName("--" + childClone.getName()); 
					
					listHierarchey.add(childClone);
					
					ListChildren(child, 2, listHierarchey, "asc", "--");
				}
			}
		}

		return listHierarchey;
	}
	
	public List<Category> listHierarchicalCategories(String sortField, String sortDir, String delemiter) throws CloneNotSupportedException {
		List<Category> listHierarchey = new ArrayList<>(); 
		
		Iterable<Category> categories = this.listCategories(sortField, sortDir);

		for (Category category : categories) {
			if (category.getParent() == null) {
				listHierarchey.add(category.clone()); 

				SortedSet<Category> sortedChildren = this.sortChildren(category.getChildren(), "asc");

				for (Category child : sortedChildren) {
					Category childClone = child.clone(); 
					childClone.setName(delemiter + childClone.getName()); 
					
					listHierarchey.add(childClone);
					
					ListChildren(child, 2, listHierarchey, "asc", delemiter);
				}
			}
		}

		return listHierarchey;
	}
	
	public List<Category> getDropDownlistCategories() throws CloneNotSupportedException {
		return listHierarchicalCategories("name", "asc", "--"); 
	}

	private void ListChildren(Category parent, int level, List<Category> listHierarchey, String sortDir, 
			String delimiter) throws CloneNotSupportedException {
		SortedSet<Category> sortedChildren = this.sortChildren(parent.getChildren(), sortDir);

		for (Category child : sortedChildren) {
			StringBuilder hyphens = new StringBuilder();

			for (int i = 0; i < level; i++)
				hyphens.append(delimiter);

			Category childClone = child.clone(); 
			childClone.setName(hyphens + child.getName());

			listHierarchey.add(childClone);

			ListChildren(child, level + 1, listHierarchey, sortDir, delimiter);
		}
	}

	public Category saveCategory(Category category) {
		return categoryRepo.save(category);
	}

	public String isCategoryUnique(Integer id, String name, String alias) {
		boolean isEditMod = !(id == null || id == 0);

		Category categoryByName = categoryRepo.findByName(name);

		if (categoryByName == null) {
			Category categoryByAlias = categoryRepo.findByAlias(alias);
			if (categoryByAlias != null)
				if (isEditMod)
					if (categoryByAlias.getId() != id)
						return "DublicatedAlias";
					else
						return "DublicatedAlias";
		} else if (isEditMod)
			if (categoryByName.getId() == id) {
				Category categoryByAlias = categoryRepo.findByAlias(alias);
				if (categoryByAlias != null)
					if (categoryByAlias.getId() != id)
						return "DublicatedAlias";
			} else
				return "DublicatedName";
		else
			return "DublicatedName";

		return "OK";
	}

	public Category getCategory(Integer id) throws CategoryNotFoundException {
		try {
			Category category = categoryRepo.findById(id).get();

			return category;
		} catch (NoSuchElementException ex) {
			throw new CategoryNotFoundException("Couldn't find the category with ID = [" + id + "]");
		}
	}

	public void deleteCategory(Integer id) throws CategoryNotFoundException {
		Long countById = categoryRepo.countById(id);

		if (countById == null || countById == 0) {
			throw new CategoryNotFoundException("Couldn't find Category with ID = [" + id + "]");
		}

		categoryRepo.deleteById(id);
	}

	public void updateCategoryEnabledStatus(Integer id, boolean enabled) {
		categoryRepo.updateEnabledById(id, enabled);
	}
	
	public Category updateCategory(Category categoryInForm) {
		Category categoryInDB = categoryRepo.findById(categoryInForm.getId()).get();

		if (!categoryInForm.getImage().isEmpty()) {
			categoryInDB.setImage(categoryInForm.getImage());
		}

		categoryInDB.setName(categoryInForm.getName());
		categoryInDB.setAlias(categoryInForm.getAlias());

		return categoryRepo.save(categoryInDB);
	}

	private SortedSet<Category> sortChildren(Set<Category> children, String sortDir) {
		SortedSet<Category> sortedChildren = new TreeSet<>((Category catOne, Category catTwo) -> {
				if(sortDir.equals("desc")) 
					return catTwo.getName().compareTo(catOne.getName());  
				
				return catOne.getName().compareTo(catTwo.getName()); 
			}
		); 
		
		sortedChildren.addAll(children); 
		return sortedChildren; 
	}
}
