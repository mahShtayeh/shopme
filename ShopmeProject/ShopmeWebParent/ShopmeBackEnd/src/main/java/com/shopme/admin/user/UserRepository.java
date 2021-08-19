package com.shopme.admin.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shopme.common.entity.User;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Integer>{
	
	public User findByEmail(@Param("email") String email); 
	
	public Page<User> findByFirstNameLikeOrLastNameLike(String firstNameFragment,String lastNameFragment, 
			Pageable pageable); 
	
	public Long countById(Integer id); 
	
	@Query("UPDATE User u SET u.enabled = ?2 WHERE u.id = ?1")
	@Modifying
	public void updateEnabledById(Integer id, boolean enabled); 
}
