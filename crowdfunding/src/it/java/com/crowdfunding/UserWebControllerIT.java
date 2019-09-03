package com.crowdfunding;

import static org.assertj.core.api.Assertions.assertThat;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import com.crowdfunding.model.User;
import com.crowdfunding.repository.UserRepo;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class UserWebControllerIT {

	@Autowired
	UserRepo userRepo;
	
	@LocalServerPort
	private int port;
	
	private WebDriver driver;
	private String baseURL;

	
	@Before
	public void setUp() throws Exception {
		baseURL = "http://localhost:" + port;
		driver = new HtmlUnitDriver();
		userRepo.deleteAll();
		userRepo.flush();
	}

	@After
	public void tearDown() throws Exception {
		driver.quit();
	}


	@Test
	public void testRegisterUser() {
		driver.get(baseURL + "/register");
		driver.findElement(By.id("username")).sendKeys("user");
		driver.findElement(By.id("password")).sendKeys("userPass");
		driver.findElement(By.id("password_two")).sendKeys("userPass");
		driver.findElement(By.name("btn_save")).click();
		
		assertThat(userRepo.findByUsername("user").getRole()).isEqualTo(1);
	}
	
	@Test
	public void testRegisterAdmin() {
		
		driver.get(baseURL + "/register");
		driver.findElement(By.id("username")).sendKeys("admin");
		driver.findElement(By.id("password")).sendKeys("admPass");
		driver.findElement(By.id("password_two")).sendKeys("admPass");
		driver.findElement(By.name("btn_save")).click();
		
		assertThat(userRepo.findByUsername("admin").getRole()).isEqualTo(2);	
	}
	
	@Test
	public void testLogin() {
		User user = new User(null, "user", "thePass", 1);
		User saved = userRepo.save(user);
		
		driver.get(baseURL);
		driver.findElement(By.name("username")).sendKeys("user");
		driver.findElement(By.name("password")).sendKeys("thePass");
		driver.findElement(By.name("btn_login")).click();
		
		User found = userRepo.findByUsernameAndPassword("user", "thePass");
		
		assertThat(found).isEqualTo(saved);
	}
	
}
