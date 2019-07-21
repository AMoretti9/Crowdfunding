package com.crowdfunding.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.crowdfunding.model.User;
import com.crowdfunding.repository.UserRepo;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {
	
	@Mock
	private UserRepo userRepo;
	
	@InjectMocks
	private UserService userService;

	@Test
	public void test_getUserByUsername_notFound() {
		when(userRepo.findByUsername(anyString()))
			.thenReturn(null);
		assertThat(userService.getUserByUsername("aName"))
			.isNull();
	}
	
	@Test
	public void test_getUserByUsername_Found() {
		User user = new User(1L, "aName", "thePass", 1);
		when(userRepo.findByUsername("aName"))
			.thenReturn(user);
		assertThat(userService.getUserByUsername("aName"))
			.isSameAs(user);
	}
	
	@Test
	public void test_insertNewUser_setsIdToNull_andReturnSavedUser() {
		User toSave = spy(new User(1L, "newUser", "newPass", 1));
		User saved = new User(99L, "theUser", "thePass", 1);
		
		when(userRepo.save(any(User.class)))
			.thenReturn(saved);
		
		User result = userService.insertNewUser(toSave);
		
		assertThat(result).isSameAs(saved);
		
		InOrder inOrder = inOrder(toSave, userRepo);
		inOrder.verify(toSave).setId(null);
		inOrder.verify(userRepo).save(toSave);
	}
	
	@Test
	public void test_getUserByUsernameAndPassword_NotFound() {
		when(userRepo.findByUsernameAndPassword(anyString(), anyString()))
			.thenReturn(null);
		assertThat(userService.getUserByUsernameAndPassword("aName", "aPassword")).isNull();
	}
	
	@Test
	public void test_getUserByUsernameAndPassword_Found() {
		User user = new User(1L, "name", "password", 1);
		when(userRepo.findByUsernameAndPassword("name", "password"))
			.thenReturn(user);
		assertThat(userService.getUserByUsernameAndPassword("name", "password"))
			.isSameAs(user);
	}

}
