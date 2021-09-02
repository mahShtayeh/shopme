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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Category;

@Service
@Transactional
public class CategoryService {

	public static final int Category_PER_PAGE = 10;

	@Autowired
	private CategoryRepository categoryRepo;

	public List<Category> listCategories(String keyword, String sortField, String sortDir) {
		Sort sort = Sort.by(sortField); 
		sort = (sortDir.equals("desc") ? sort.descending() : sort.ascending()); 
		
		if(keyword == null) 
			return (List<Category>) categoryRepo.findAll(sort);
		
		return categoryRepo.findAll(keyword); 
	}

	public List<Category> listHierarchicalCategories(String keyword, String sortField, String sortDir) throws CloneNotSupportedException {
		List<Category> listHierarchey = new ArrayList<>(); 
		
		Iterable<Category> categories = this.listCategories(keyword, sortField, sortDir);

		for (Category category : categories) {
			if (category.getParent() == null) {
				listHierarchey.add(category.clone()); 

				SortedSet<Category> sortedChildren = this.sortChildren(category.getChildren(), "asc");

				for (Category child : sortedChildren) {
					Category childClone = child.clone(); 
					childClone.setName("--" + childClone.getName()); 
					
					listHierarchey.add(childClone);
					
					ListChildren(child, 2, listHierarchey, "asc");
				}
			}
		}

		return listHierarchey;
	}
	
	public List<Category> getDropDownlistCategories() throws CloneNotSupportedException {
		return listHierarchicalCategories(null, "name", "asc"); 
	}

	private void ListChildren(Category parent, int level, List<Category> listHierarchey, String sortDir) throws CloneNotSupportedException {
		SortedSet<Category> sortedChildren = this.sortChildren(parent.getChildren(), sortDir);

		for (Category child : sortedChildren) {
			StringBuilder hyphens = new StringBuilder();

			for (int i = 0; i < level; i++)
				hyphens.append("--");

			Category childClone = child.clone(); 
			childClone.setName(hyphens + child.getName());

			listHierarchey.add(childClone);

			ListChildren(child, level + 1, listHierarchey, sortDir);
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
