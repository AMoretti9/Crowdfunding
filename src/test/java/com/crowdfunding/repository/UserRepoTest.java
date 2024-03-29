package com.crowdfunding.repository;

import static org.assertj.core.api.Assertions.assertThat;



import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.crowdfunding.model.User;

@DataJpaTest
@RunWith(SpringRunner.class)
public class UserRepoTest {
	
	@Autowired
	private UserRepo userRepo;

	
	@Test
	public void test_FindUserByUsername() {
		User user = new User(null, "name", "password", 1);
		User saved = userRepo.save(user);
		User found = userRepo.findByUsername("name");
		assertThat(found).isEqualTo(saved);
	}
	
	@Test
	public void test_FindUserByUsernameAndPassword() {
		User user = new User(null, "name", "password", 1);
		User saved = userRepo.save(user);
		User found = userRepo.findByUsernameAndPassword("name", "password");
		assertThat(found).isEqualTo(saved);
	}
	
	@Test
	public void test_UpdateRoleToAdmin() {
		User user = new User(null, "admin", "password", 1);
		User saved = userRepo.save(user);
		userRepo.updateRoleToAdmin(saved.getId());
		User found = userRepo.findByUsername("admin");
		assertThat(found.getRole()).isEqualTo(2);
	}
}
