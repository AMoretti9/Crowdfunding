package com.crowdfunding.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.crowdfunding.model.User;

public interface UserRepo extends JpaRepository<User, Long> {

	User findByUsername(String string);

	public User findByUsernameAndPassword(String username, String password);

	@Modifying(flushAutomatically = true, clearAutomatically = true)
	@Transactional
	@Query("update User u set u.role = 2 where u.id = :userid")
	void updateRoleToAdmin(@Param("userid") Long id);

}
