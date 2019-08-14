package com.crowdfunding.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.crowdfunding.model.Fund;
import com.crowdfunding.model.User;
import com.crowdfunding.service.FundService;
import com.crowdfunding.service.UserService;

@Controller
@RequestMapping("/")
@SessionAttributes("user")
public class ApplicationWebController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private FundService fundService;
	
	@ModelAttribute("user")
	public User  setupUserForm() {
		return new User();
	}
	
	@GetMapping("/")
	public String index(Model model) {
		return "index";
	}
	
	
	public String home(Model model, @ModelAttribute("user") User user) {
		model.addAttribute("MODE", "MODE_HOME");
		model.addAttribute("welcomeUser", "Welcome, " + user.getUsername());
		return "home";
	}
	
	public String myFunds(Model model, @ModelAttribute("user") User user) {
		
		List<Fund> myFunds = fundService.getFundByOwner((user.getId()).intValue());
		
		model.addAttribute("myFunds", myFunds);
		model.addAttribute("MODE", "MODE_MYFUNDS");
		model.addAttribute("activeId", user.getId());
		model.addAttribute("MyFundsUser", "MY FUNDS  -  (Personal ID: " + user.getId() + ", Username: " + user.getUsername() + ")");
		
		
		return "home";
	}
	
	public String usersFunds(Model model, @ModelAttribute("user") User user) {
		
		List<Fund> usersFunds = fundService.getOpenFundsByOwnerNot((user.getId()).intValue());
		
		model.addAttribute("usersFunds", usersFunds);
		model.addAttribute("MODE", "MODE_USERSFUNDS");
		
		
		return "home";
	}
	
	
	@PostMapping("/login-user")
	public String loginUser(Model model, @ModelAttribute("username") String username,
			@ModelAttribute("password") String password, @ModelAttribute("user") User user) {
		User UserFound = userService.getUserByUsernameAndPassword(username, password);
		if (UserFound != null) {
			user.setId(UserFound.getId());
			user.setUsername(UserFound.getUsername());
			user.setPassword(UserFound.getPassword());
			user.setRole(UserFound.getRole());
			return home(model, user);
		} else {
			model.addAttribute("messageLogin", "Login incorrect or Account not present");
			return "index";
		}
	}
	
	@GetMapping("/action/logout")
	public String actionLogout(Model model) {
		return "index";
	}
	
	@GetMapping("/action/home")
	public String actionHome(Model model, @ModelAttribute("user") User user) {
		return home(model, user);
	}
	
	@GetMapping("/my-funds")
	public String ActionMyFunds(Model model, @ModelAttribute("user") User user) {
		model.addAttribute("fund", new Fund());
		user.setId(user.getId());
		user.setUsername(user.getUsername());
		return myFunds(model, user);
	}
	
	@GetMapping("/users-funds")
	public String ActionUsersFunds(Model model, @ModelAttribute("user") User user) {
		user.setId(user.getId());
		return usersFunds(model, user);
	}
	
	@GetMapping("/register")
	public String register(Model model) {
		model.addAttribute("user", new User());
		model.addAttribute("messageRegister", "");
		return "register";
	}
	
	@PostMapping("/save-user")
	public String saveUser(User user, Model model) {
		final User presentUsername = userService.getUserByUsername(user.getUsername());
		if (presentUsername == null) {
			userService.insertNewUser(user);
			return "redirect:/";
		} else {
			model.addAttribute("messageRegister", "Username already in use! Please change it");
			return "register";
		}
	}
	
	@PostMapping("/save-fund")
	public String saveFund(Fund fund, Model model, @ModelAttribute("user") User user) {
		
		fundService.insertNewFund(fund);
		return myFunds(model, user);
		
	}
		
	
}