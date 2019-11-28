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
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import com.crowdfunding.dto.UserDTO;
import com.crowdfunding.model.Fund;
import com.crowdfunding.model.User;
import com.crowdfunding.service.FundService;
import com.crowdfunding.service.UserService;

@Controller
@RequestMapping("/")
@SessionAttributes("userdto")
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
	
	
	@ModelAttribute("userdto")
	public UserDTO  setupUserForm() {
		return new UserDTO();
	}
	
	@GetMapping("/")
	public String index(Model model) {
		return INDEX;
	}
	
	
	public String home(Model model, UserDTO userdto) {
		model.addAttribute("MODE", "MODE_HOME");
		model.addAttribute("welcomeUser", "Welcome, " + userdto.getUsername());
		return HOME;
	}
	
	public String myFunds(Model model, UserDTO userdto) {
		
		List<Fund> myFunds = fundService.getFundByOwner((userdto.getId()).intValue());
		
		model.addAttribute("myFunds", myFunds);
		model.addAttribute("MODE", "MODE_MYFUNDS");
		model.addAttribute("activeId", userdto.getId());
		model.addAttribute("MyFundsUser", "MY FUNDS  -  (Personal ID: " + userdto.getId() + ", Username: " + userdto.getUsername() + ")");
		
		
		return HOME;
	}
	
	public String usersFunds(Model model, UserDTO userdto) {
		
		List<Fund> usersFunds = fundService.getOpenFundsByOwnerNot((userdto.getId()).intValue());
		
		model.addAttribute("usersFunds", usersFunds);
		model.addAttribute("MODE", "MODE_USERSFUNDS");
		
		
		return HOME;
	}
	
	
	@PostMapping("/login-user")
	public String loginUser(Model model, @ModelAttribute("username") String username,
			@ModelAttribute("password") String password, @ModelAttribute("userdto") UserDTO userdto) {
		User userFound = userService.getUserByUsernameAndPassword(username, password);
		if (userFound != null) {
			userdto.setId(userFound.getId());
			userdto.setUsername(userFound.getUsername());
			userdto.setRole(userFound.getRole());
			return home(model, userdto);
		} else {
			model.addAttribute("messageLogin", "Login incorrect or Account not present");
			return INDEX;
		}
	}
	
	@GetMapping("/action/logout")
	public String actionLogout(Model model, SessionStatus status) {
		status.setComplete();
		return INDEX;
	}
	
	@GetMapping("/action/home")
	public String actionHome(Model model,@SessionAttribute("userdto") UserDTO userdto) {
		return home(model, userdto);
	}
	
	@GetMapping("/my-funds")
	public String actionMyFunds(Model model, @SessionAttribute("userdto") UserDTO userdto) {
		model.addAttribute("fund", new Fund());
		return myFunds(model, userdto);
	}
	
	@GetMapping("/users-funds")
	public String actionUsersFunds(Model model,@SessionAttribute("userdto") UserDTO userdto) {
		
		return usersFunds(model, userdto);
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
	public String saveFund(Fund fund, Model model,@SessionAttribute("userdto") UserDTO userdto) {
		
		fundService.insertNewFund(fund);
		return myFunds(model, userdto);
		
	}
		
	@GetMapping("/myfund/{idFund}")
	public String editMyFund(@PathVariable long idFund, Model model) {
		
		Fund fundById = fundService.getFundById(idFund);
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
	
	@GetMapping("/userfund/{idFund}")
	public String editUserFund(@PathVariable Long idFund,@SessionAttribute("userdto") UserDTO userdto, Model model) {
		Fund fundById = fundService.getFundById(idFund);
		model.addAttribute("fundAttribute", fundById);
		model.addAttribute(EDITABLE, "NO");
		model.addAttribute("donate", "YES");
		if(userdto.getRole() == 1) {
			model.addAttribute(CLOSABLE, "NO");
		} else {
			model.addAttribute(CLOSABLE, "YES");
		}
		
		return FUND;
	}

	@PostMapping("/closes")
	public String userClosesFund(Model model, Fund fund,@SessionAttribute("userdto") UserDTO userdto) {
		if(userdto.getRole() == 1) {
			fundService.userClosesFund(fund.getIdFund());
		} else {
			fundService.adminClosesFund(fund.getIdFund());
		}
		return home(model, userdto);
	}
	
	@PostMapping("/edit-subject")
	public String editFundSubject(Model model, Fund fund,@SessionAttribute("userdto") UserDTO userdto) {
		final Long id = fund.getIdFund();
		fundService.updateFundById(id, fund);
		return home(model, userdto);
	}
	
	@PostMapping("/action/donate")
	public String donateMoney(Model model, @ModelAttribute("donation") Double donation, Fund fund,@SessionAttribute("userdto") UserDTO userdto) {
		
		fundService.donateMoneyToFund(donation, fund.getIdFund());
		
		return home(model, userdto);
	}
	
}