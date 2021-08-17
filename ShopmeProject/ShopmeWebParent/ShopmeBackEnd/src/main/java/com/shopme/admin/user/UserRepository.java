package com.shopme.admin.user;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shopme.common.entity.User;

@Repository
public interface UserRepository extends CrudRepository<User, Integer>{
	public User getUserByEmail(@Param("email") String email); 
	public Long countById(Integer id); 
	
	@Query("UPDATE User u SET u.enabled = ?2 WHERE u.id = ?1")
	@Modifying
	public void updateEnabledById(Integer id, boolean enabled); 
}
