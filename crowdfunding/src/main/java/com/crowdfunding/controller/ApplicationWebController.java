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
		model.addAttribute("messageRegister", "");
		return "register";
	}
	
	@PostMapping("/save-user")
	public String saveUser(User user, Model model) {
		final Long id = user.getId();
		final User presentUsername = userService.getUserByUsername(user.getUsername());
		if (id == null && presentUsername == null) {
			userService.insertNewUser(user);
			return "redirect:/";
		} else {
			model.addAttribute("messageRegister", "Username already in use! Please change it");
			return "register";
		}
	}
	
}