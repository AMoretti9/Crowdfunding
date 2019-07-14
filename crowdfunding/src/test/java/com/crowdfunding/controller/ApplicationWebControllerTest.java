package com.crowdfunding.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
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
	
	//HTML Unit Test
	
	@Test
	public void testHomePageTitle() throws Exception {
		HtmlPage page = webClient.getPage("/");
		assertThat(page.getTitleText()).isEqualTo("Login .::. Crowdfunding");
	}
	
	@Test
	public void test_HomePage_ShouldProvideLinkForRegister() throws Exception {
		HtmlPage page = this.webClient.getPage("/");
		
		assertThat(page
					.getAnchorByText("Register")
					.getHrefAttribute()
				).isEqualTo("/register");
	}
	
	//Web Controller Unit Test
	
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
	public void register_newUser() throws Exception {
		mvc.perform(get("/register"))
			.andExpect(view().name("register"))
			.andExpect(model().attribute("user", new User()));
			verifyZeroInteractions(userService);
	}
	
	@Test
	public void test_PostUserWithoutId_ShouldInsertNewUser() throws Exception {
		mvc.perform(post("/save-user")
				.param("username", "test")
				.param("password", "abc123")
				.param("role", "1"))
			.andExpect(view().name("redirect:/")); //back to the home page Index
		verify(userService).insertNewUser(new User(null, "test", "abc123", 1));
	}
	
	
	


}
