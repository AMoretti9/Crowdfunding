package com.crowdfunding.controller;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.HashMap;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.ModelAndViewAssert;
import org.springframework.test.web.servlet.MockMvc;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.crowdfunding.service.FundService;
import com.crowdfunding.service.UserService;
import com.crowdfunding.model.User;
import com.crowdfunding.model.Fund;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = ApplicationWebController.class)
public class ApplicationWebControllerTest {

	@Autowired
	private MockMvc mvc;
	
	@Autowired
	private WebClient webClient;
	
	@MockBean
	private UserService userService;
	
	@MockBean
	private FundService fundService;
	
	////////////////////
	// HTML Unit Test //
	////////////////////
	
	@Test
	public void testHomePage() throws Exception {
		HtmlPage page = webClient.getPage("/");
		assertThat(page.getTitleText()).isEqualTo("Login .::. Crowdfunding");
		assertThat(page.getBody().getTextContent())
		.doesNotContain("Login incorrect or Account not present");
	}
	
	@Test
	public void test_IndexPage_ShouldProvideLinkForRegister() throws Exception {
		HtmlPage page = this.webClient.getPage("/");
		
		assertThat(page
					.getAnchorByText("Register")
					.getHrefAttribute()
				).isEqualTo("/register");
	}
	
	@Test
	public void test_RegisterPage_ShouldNotShowMessage() throws Exception {
		HtmlPage page = this.webClient.getPage("/register");
		assertThat(page.getTitleText()).isEqualTo("Sign In .::. Crowdfunding");
		assertThat(page.getBody().getTextContent())
		.doesNotContain("Username already in use! Please change it");
	}
	
	
	@Test
	public void test_insertNewUser() throws Exception {
		HtmlPage page = this.webClient.getPage("/register");
		final HtmlForm form = page.getFormByName("user_form");
		form.getInputByName("username").setValueAttribute("newName");
		form.getInputByName("password").setValueAttribute("newPass");
		form.getButtonByName("btn_save").click();
		
		when(userService.getUserByUsername("newName")).thenReturn(null);
		
		assertThat(page.getBody().getTextContent())
		.doesNotContain("Username already in use! Please change it");
		
	}
	
	@Test
	public void test_loginSuccesful() throws Exception {
		
		User userPresent = new User(1L, "aName", "thePass", 1);
		
		HtmlPage page = this.webClient.getPage("/");
		final HtmlForm form = page.getFormByName("userLogin_form");
		form.getInputByName("username").setValueAttribute("aName");
		form.getInputByName("password").setValueAttribute("thePass");
		
		when(userService.getUserByUsernameAndPassword("aName", "thePass"))
			.thenReturn(userPresent);
		
		assertThat(page.getBody().getTextContent())
		.doesNotContain("Login incorrect or Account not present");
		
	}
	
	@Test
	public void test_loginIncorrect() throws Exception {
				
		HtmlPage page = this.webClient.getPage("/");
		final HtmlForm form = page.getFormByName("userLogin_form");
		form.getInputByName("username").setValueAttribute("aName");
		form.getInputByName("password").setValueAttribute("thePass");
		
		when(userService.getUserByUsernameAndPassword("aName", "thePass"))
			.thenReturn(null);
		
		assertThat(page.getTitleText()).isEqualTo("Login .::. Crowdfunding");
		
	}
	
	
	//////////////////////////////
	//   Web Controller Test    //
	//////////////////////////////
	
	@Test
	public void testStatus200() throws Exception {
		mvc.perform(get("/")).
			andExpect(status().is2xxSuccessful());
	}
	
	@Test
	public void testReturnHomeView() throws Exception {
		ModelAndViewAssert.assertViewName(mvc.perform(get("/"))
				.andReturn().getModelAndView(), "index");
	}
	
	@Test
	public void testRegisterStatus200() throws Exception {
		mvc.perform(get("/register")).
			andExpect(status().is2xxSuccessful());
	}
	
	@Test
	public void testReturnRegisterView() throws Exception {
		ModelAndViewAssert.assertViewName(mvc.perform(get("/register"))
				.andReturn().getModelAndView(), "register");
	}
	
	@Test
	public void testLogoutStatus200() throws Exception {
		mvc.perform(get("/action/logout")).
			andExpect(status().is2xxSuccessful());
	}
	
	@Test
	public void testBackToHomeStatus200() throws Exception {
		ModelAndViewAssert.assertViewName(mvc.perform(get("/action/home"))
				.andReturn().getModelAndView(), "home");
	}
	
	@Test
	public void testReturnLogoutAction() throws Exception {
		ModelAndViewAssert.assertViewName(mvc.perform(get("/action/logout"))
				.andReturn().getModelAndView(), "index");
	}
	
	@Test
	public void testReturnHomePageAction() throws Exception {
		User activeUser = new User(1L, "myName", "password", 1);
		
		HashMap<String, Object> sessionattr = new HashMap<String, Object>();
		
		sessionattr.put("user", activeUser);
		
		mvc.perform(get("/action/home").sessionAttrs(sessionattr))
		.andExpect(view().name("home"))
		.andExpect(model().attributeExists("user"))
		.andExpect(model().attribute("welcomeUser", "Welcome, myName"))
		.andExpect(model().attribute("MODE", "MODE_HOME"));
	}
	
	
	@Test
	public void testMyFunds_shouldShowMyFunds_whenMyFundsArePresent() throws Exception{
		
		User activeUser = new User(1L, "myName", "password", 1);
		int activeId = (activeUser.getId()).intValue();
		
		HashMap<String, Object> sessionattr = new HashMap<String, Object>();
		sessionattr.put("user", activeUser);
		
		List<Fund> funds = asList(new Fund(1L, "test fund", 10.0, 1, 1));
		
		when(fundService.getFundByOwner(activeId)).thenReturn(funds);
		
		mvc.perform(get("/my-funds").sessionAttrs(sessionattr))
		.andExpect(view().name("home"))
		.andExpect(model().attributeExists("user"))
		.andExpect(model().attribute("MyFundsUser", "MY FUNDS  -  (Personal ID: 1, Username: myName)"))
		.andExpect(model().attribute("myFunds", funds))
		.andExpect(model().attribute("MODE", "MODE_MYFUNDS"));
		
	}
	
	@Test
	public void testUsersFunds_shouldShowUsersFunds_whenUsersFundsArePresent() throws Exception {
		User activeUser = new User(1L, "myName", "password", 1);
		int activeId = (activeUser.getId()).intValue();
		
		HashMap<String, Object> sessionattr = new HashMap<String, Object>();
		sessionattr.put("user", activeUser);
		
		List<Fund> usersFunds = asList(
				new Fund(1L, "test fund", 10.0, 1, 2), 
				new Fund(2L, "test two", 5.0, 1, 3));
		
		when(fundService.getOpenFundsByOwnerNot(activeId)).thenReturn(usersFunds);
		
		mvc.perform(get("/users-funds").sessionAttrs(sessionattr))
		.andExpect(view().name("home"))
		.andExpect(model().attributeExists("user"))
		.andExpect(model().attribute("usersFunds", usersFunds))
		.andExpect(model().attribute("MODE", "MODE_USERSFUNDS"));
	}
	
	@Test
	public void register_newUser() throws Exception {
		mvc.perform(get("/register"))
			.andExpect(view().name("register"))
			.andExpect(model().attribute("user", new User()))
			.andExpect(model().attribute("messageRegister", ""));
			verifyZeroInteractions(userService);
	}
	
	@Test
	public void test_PostUserWithUsernameAndPasswordPresent_ShouldLogin () throws Exception {
		User user1 = new User(1L, "NameOne", "PassOne", 1);
		
		when(userService.getUserByUsernameAndPassword("NameOne", "PassOne"))
			.thenReturn(user1);
		
		mvc.perform(post("/login-user")
				.param("username", "NameOne")
				.param("password", "PassOne"))
				.andExpect(model().attribute("MODE", "MODE_HOME"))
				.andExpect(model().attribute("welcomeUser", "Welcome, NameOne"))
			.andExpect(view().name("home"));
	}
	
	@Test
	public void test_PostUserWithUsernameAndPasswordNotPresent_ShouldNotLogin () throws Exception {
		when(userService.getUserByUsernameAndPassword("aName", "thePass"))
			.thenReturn(null);
				
		mvc.perform(post("/login-user")
				.param("username", "aName")
				.param("password", "thePass"))
				.andExpect(model().attribute("messageLogin", "Login incorrect or Account not present"))
			.andExpect(view().name("index"));
	}
	
	@Test
	public void test_PostUserWithoutIdAndNotPresentUsername_ShouldInsertNewUser() throws Exception {
		
		when(userService.getUserByUsername("newUsername")).thenReturn(null);
		
		
		mvc.perform(post("/save-user")
				.param("username", "test")
				.param("password", "abc123")
				.param("role", "1"))
			.andExpect(view().name("redirect:/")); //back to the home page Index
		verify(userService).insertNewUser(new User(null, "test", "abc123", 1));
	}
	
	@Test
	public void test_PostUserWithoutIdAndAlreadyPresentUsername_ShouldNotInsertNewUser() throws Exception {
		User userInserted = new User(1L, "namePresent", "mypass", 1);
		
		when(userService.getUserByUsername("namePresent")).thenReturn(userInserted);
		
		
		mvc.perform(post("/save-user")
				.param("id", "1")
				.param("username", "namePresent")
				.param("password", "newpass")
				.param("role", "1"))
				.andExpect(model().attribute("messageRegister", "Username already in use! Please change it"))
			.andExpect(view().name("register"));
		
	}
	
	@Test
	public void test_TrimUsernameAndPasswordWhenUserSignUpWithBlankSpace () throws Exception {
		mvc.perform(post("/save-user")
				.param("username", "   test")
				.param("password", "abc123\t")
				.param("role", "1"))
			.andExpect(view().name("redirect:/")); //back to the home page Index
		verify(userService).insertNewUser(new User(null, "test", "abc123", 1));
	}
	
	@Test
	public void test_whenInsertedUserIsAdmin_DoUpdateOnRole() throws Exception {
		
		User userInserted = new User(1L, "admin", "password", 1);		
		
		mvc.perform(post("/save-user")
				.param("id", "1")
				.param("username", "admin")
				.param("password", "password")
				.param("role", "1"));
		
		if (userInserted.getUsername() == "admin") {
			verify(userService).updateRoleToAdmin(1L);
		}
		
	}
	
	@Test
	public void test_PostFund_ShouldInsertNew_WithOpenStateAndOwner() throws Exception {
		
		User activeUser = new User(1L, "myName", "password", 1);
		Integer activeId = (activeUser.getId()).intValue();
		
		HashMap<String, Object> sessionattr = new HashMap<String, Object>();
		sessionattr.put("user", activeUser);
		
		
		
		mvc.perform(post("/save-fund").sessionAttrs(sessionattr)
				.param("subject", "test fund inserted")
				.param("money", "0")
				.param("state", "1")
				.param("owner", activeId.toString()))
			.andExpect(view().name("home"));
		verify(fundService).insertNewFund(new Fund(null, "test fund inserted", 0.0, 1, 1));
	}
	
	
	@Test
	public void test_EditFund_whenFundIsFound() throws Exception {
		Fund fund = new Fund(2L, "test fund", 0.0, 1, 1);
		
		when(fundService.getFundById(2L)).thenReturn(fund);
		
		mvc.perform(get("/myfund/2"))
			.andExpect(view().name("fund"))
			.andExpect(model().attribute("fundAttribute", fund))
			.andExpect(model().attribute("closable", "YES"));
		
	}
	
	@Test
	public void test_FundNotClosable_whenUserIsNotAdmin() throws Exception {
		
		User activeUser = new User(1L, "myName", "password", 1);
		
		HashMap<String, Object> sessionattr = new HashMap<String, Object>();
		sessionattr.put("user", activeUser);
		
		Fund fund = new Fund(3L, "test fund", 0.0, 1, 1);
		when(fundService.getFundById(3L)).thenReturn(fund);
		
		if (activeUser.getRole() == 1) {
		mvc.perform(get("/userfund/3").sessionAttrs(sessionattr))
			.andExpect(view().name("fund"))
			.andExpect(model().attribute("fundAttribute", fund))
			.andExpect(model().attribute("myFundEditable", "NO"))
			.andExpect(model().attribute("closable", "NO"))
			.andExpect(model().attribute("donate", "YES"));
		}
	}
	
	@Test
	public void test_FundClosable_whenUserIsAdmin() throws Exception {
		
		User activeAdmin = new User(1L, "myName", "password", 2);
		
		HashMap<String, Object> sessionattr = new HashMap<String, Object>();
		sessionattr.put("user", activeAdmin);
		
		Fund fund = new Fund(3L, "test fund", 0.0, 1, 1);
		when(fundService.getFundById(3L)).thenReturn(fund);
		
		if (activeAdmin.getRole() == 2) {
		mvc.perform(get("/userfund/3").sessionAttrs(sessionattr))
			.andExpect(view().name("fund"))
			.andExpect(model().attribute("fundAttribute", fund))
			.andExpect(model().attribute("myFundEditable", "NO"))
			.andExpect(model().attribute("closable", "YES"))
			.andExpect(model().attribute("donate", "YES"));
		}
	}
	
	@Test
	public void test_EditFund_isEditable() throws Exception {
		Fund fund = new Fund(2L, "test fund", 0.0, 1, 1);
		
		when(fundService.getFundById(2L)).thenReturn(fund);
		
		if(fund.getMoney() == 0.0) {
			mvc.perform(get("/myfund/2"))
			.andExpect(model().attribute("myFundEditable", "YES"));
		}
	}

	@Test
	public void test_EditFund_isNotEditable() throws Exception {
		Fund fund = new Fund(2L, "test fund", 5.0, 1, 1);
		
		when(fundService.getFundById(2L)).thenReturn(fund);
		
		if(fund.getMoney() != 0.0) {
			mvc.perform(get("/myfund/2"))
			.andExpect(model().attribute("myFundEditable", "NO"));
		}
	}
	
	@Test
	public void test_userClosesFund() throws Exception{
		User activeUser = new User(1L, "myName", "password", 1);
		
		HashMap<String, Object> sessionattr = new HashMap<String, Object>();
		sessionattr.put("user", activeUser);
		Fund fund = new Fund(1L, "test fund", 0.0, 1, 1);
		fundService.insertNewFund(fund);
		
		if(activeUser.getRole() == 1) {
		mvc.perform(post("/closes").sessionAttrs(sessionattr)
				.param("id_fund", "1")
				.param("subject", "test fund")
				.param("money", "0.0")
				.param("state", "1")
				.param("owner", "1"))
			.andExpect(view().name("home"));
		verify(fundService).userClosesFund(1L);
		}
	}
	
	//Here test when admin close
	
	@Test
	public void test_updateSubjectFund() throws Exception{
		mvc.perform(post("/edit-subject")
				.param("id_fund", "1")
				.param("subject", "test newSub")
				.param("money", "0.0")
				.param("state", "1")
				.param("owner", "1"))
			.andExpect(view().name("home"));
		verify(fundService).updateFundById(1L, new Fund(1L, "test newSub", 0.0, 1, 1));

	}
	
	
}
