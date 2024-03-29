package com.crowdfunding.service;

import org.springframework.stereotype.Service;

import com.crowdfunding.model.User;
import com.crowdfunding.repository.UserRepo;

@Service
public class UserService {

	private UserRepo userRepo;
	
	public UserService(UserRepo userRepo) {
		this.userRepo=userRepo;
	}

	public User insertNewUser(User user) {
		
		user.setId(null);
		return userRepo.save(user);
		
	}

	public User getUserByUsername(String username) {
		return userRepo.findByUsername(username);
	}

	public User getUserByUsernameAndPassword(String username, String password) {
		return userRepo.findByUsernameAndPassword(username, password);
	}

	public void updateRoleToAdmin(Long id) {
		userRepo.updateRoleToAdmin(id);
	}
	
	
}
