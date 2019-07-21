package com.crowdfunding.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

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

import com.crowdfunding.service.UserService;
import com.crowdfunding.model.User;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = ApplicationWebController.class)
public class ApplicationWebControllerTest {

	@Autowired
	private MockMvc mvc;
	
	@Autowired
	private WebClient webClient;
	
	@MockBean
	private UserService userService;
	
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
	public void testReturnLogoutAction() throws Exception {
		ModelAndViewAssert.assertViewName(mvc.perform(get("/action/logout"))
				.andReturn().getModelAndView(), "index");
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
		User userPresent = new User(1L, "aName", "thePass", 1);
		
		when(userService.getUserByUsernameAndPassword("aName", "thePass"))
			.thenReturn(userPresent);
		
		mvc.perform(post("/login-user")
				.param("username", "aName")
				.param("password", "thePass"))
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
				.param("username", "namePresent")
				.param("password", "newpass")
				.param("role", "1"))
				.andExpect(model().attribute("messageRegister", "Username already in use! Please change it"))
			.andExpect(view().name("register"));
		
	}


}
