package com.shopme.admin.user;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shopme.common.entity.User;

@Repository
public interface UserRepository extends CrudRepository<User, Integer>{
	public User getUserByEmail(@Param("email") String email); 
}