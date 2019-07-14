package com.crowdfunding.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.crowdfunding.model.User;
import com.crowdfunding.service.UserService;

@Controller
public class ApplicationWebController {

	@Autowired
	private UserService userService;
	
	@GetMapping("/register")
	public String register(Model model) {
		model.addAttribute("user", new User());
		return "register";
	}
	
	@PostMapping("/save-user")
	public String saveUser(User user) {
		final Long id = user.getId();
		if (id == null)
			userService.insertNewUser(user);
		return "redirect:/";
	}
	
}