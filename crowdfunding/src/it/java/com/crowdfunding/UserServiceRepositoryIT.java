package com.crowdfunding;


import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import com.crowdfunding.model.User;
import com.crowdfunding.repository.UserRepo;
import com.crowdfunding.service.UserService;

@RunWith(SpringRunner.class)
@DataJpaTest
@Import(UserService.class)
public class UserServiceRepositoryIT {

	@Autowired
	private UserService userService;
	
	@Autowired
	private UserRepo userRepo;
	
	@Test
	public void testUserServiceCanInsertIntoRepository() {
		
		User saved = userService
			.insertNewUser(new User(null, "aName", "thePass", 1));
		assertThat(userRepo.findById(saved.getId())).isPresent();
	}
	
	@Test
	public void testUserServiceCanUpdateRoleOfUserIntoRepository() {
		
		User saved = userRepo.save(new User(null, "admin", "adminpass", 1));
		userService.updateRoleToAdmin(saved.getId());
		assertThat(userRepo.findById(saved.getId()).get().getRole())
			.isEqualTo(2);
	}

}
