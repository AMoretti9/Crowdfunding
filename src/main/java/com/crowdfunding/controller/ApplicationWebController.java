package com.crowdfunding.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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
	
	private static final String HOME = "home";
	private static final String INDEX = "index";
	private static final String REGISTER = "register";
	private static final String FUND = "fund";
	private static final String REDIRECT = "redirect:/";
	
	private static final String CLOSABLE = "closable";
	private static final String EDITABLE = "myFundEditable";


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
		return INDEX;
	}
	
	
	public String home(Model model, @ModelAttribute("user") User user) {
		model.addAttribute("MODE", "MODE_HOME");
		model.addAttribute("welcomeUser", "Welcome, " + user.getUsername());
		return HOME;
	}
	
	public String myFunds(Model model, @ModelAttribute("user") User user) {
		
		List<Fund> myFunds = fundService.getFundByOwner((user.getId()).intValue());
		
		model.addAttribute("myFunds", myFunds);
		model.addAttribute("MODE", "MODE_MYFUNDS");
		model.addAttribute("activeId", user.getId());
		model.addAttribute("MyFundsUser", "MY FUNDS  -  (Personal ID: " + user.getId() + ", Username: " + user.getUsername() + ")");
		
		
		return HOME;
	}
	
	public String usersFunds(Model model, @ModelAttribute("user") User user) {
		
		List<Fund> usersFunds = fundService.getOpenFundsByOwnerNot((user.getId()).intValue());
		
		model.addAttribute("usersFunds", usersFunds);
		model.addAttribute("MODE", "MODE_USERSFUNDS");
		
		
		return HOME;
	}
	
	
	@PostMapping("/login-user")
	public String loginUser(Model model, @ModelAttribute("username") String username,
			@ModelAttribute("password") String password, @ModelAttribute("user") User user) {
		User userFound = userService.getUserByUsernameAndPassword(username, password);
		if (userFound != null) {
			user.setId(userFound.getId());
			user.setUsername(userFound.getUsername());
			user.setPassword(userFound.getPassword());
			user.setRole(userFound.getRole());
			return home(model, user);
		} else {
			model.addAttribute("messageLogin", "Login incorrect or Account not present");
			return INDEX;
		}
	}
	
	@GetMapping("/action/logout")
	public String actionLogout(Model model) {
		return INDEX;
	}
	
	@GetMapping("/action/home")
	public String actionHome(Model model, @ModelAttribute("user") User user) {
		return home(model, user);
	}
	
	@GetMapping("/my-funds")
	public String actionMyFunds(Model model, @ModelAttribute("user") User user) {
		model.addAttribute("fund", new Fund());
		user.setId(user.getId());
		user.setUsername(user.getUsername());
		return myFunds(model, user);
	}
	
	@GetMapping("/users-funds")
	public String actionUsersFunds(Model model, @ModelAttribute("user") User user) {
		user.setId(user.getId());
		return usersFunds(model, user);
	}
	
	@GetMapping("/register")
	public String register(Model model) {
		model.addAttribute("user", new User());
		model.addAttribute("messageRegister", "");
		return REGISTER;
	}
	
	@PostMapping("/save-user")
	public String saveUser(User user, Model model) {
		final User presentUsername = userService.getUserByUsername(user.getUsername());
		user.setUsername(user.getUsername().trim());
		user.setPassword(user.getPassword().trim());
		if (presentUsername == null) {
			userService.insertNewUser(user);
			if((user.getUsername()).equals("admin")) {
				userService.updateRoleToAdmin(user.getId());
			}
			return REDIRECT;
		} else {
			model.addAttribute("messageRegister", "Username already in use! Please change it");
			return REGISTER;
		}
	}
	
	@PostMapping("/save-fund")
	public String saveFund(Fund fund, Model model, @ModelAttribute("user") User user) {
		
		fundService.insertNewFund(fund);
		return myFunds(model, user);
		
	}
		
	@GetMapping("/myfund/{id_fund}")
	public String editMyFund(@PathVariable long id_fund, Model model) {
		
		Fund fundById = fundService.getFundById(id_fund);
		model.addAttribute("fundAttribute", fundById);
		model.addAttribute(CLOSABLE, "YES");
		//donate no
		if(fundById.getMoney()== 0.0) {
			model.addAttribute(EDITABLE, "YES");
		}else {
			model.addAttribute(EDITABLE, "NO");
		}
		return FUND;
	}
	
	@GetMapping("/userfund/{id_fund}")
	public String editUserFund(@PathVariable Long id_fund, @ModelAttribute("user") User user, Model model) {
		Fund fundById = fundService.getFundById(id_fund);
		model.addAttribute("fundAttribute", fundById);
		model.addAttribute(EDITABLE, "NO");
		model.addAttribute("donate", "YES");
		if(user.getRole() == 1) {
			model.addAttribute(CLOSABLE, "NO");
		} else {
			model.addAttribute(CLOSABLE, "YES");
		}
		
		return FUND;
	}

	@PostMapping("/closes")
	public String userClosesFund(Model model, Fund fund, @ModelAttribute("user") User user) {
		if(user.getRole() == 1) {
			fundService.userClosesFund(fund.getIdFund());
		} else {
			fundService.adminClosesFund(fund.getIdFund());
		}
		return home(model, user);
	}
	
	@PostMapping("/edit-subject")
	public String editFundSubject(Model model, Fund fund, @ModelAttribute("user") User user) {
		final Long id = fund.getIdFund();
		fundService.updateFundById(id, fund);
		return home(model, user);
	}
	
	@PostMapping("/action/donate")
	public String donateMoney(Model model, @ModelAttribute("donation") Double donation, Fund fund, @ModelAttribute("user") User user) {
		
		fundService.donateMoneyToFund(donation, fund.getIdFund());
		
		return home(model, user);
	}
	
}