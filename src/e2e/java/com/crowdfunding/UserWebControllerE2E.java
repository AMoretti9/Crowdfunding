package com.crowdfunding;


import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import io.github.bonigarcia.wdm.WebDriverManager;

public class UserWebControllerE2E {

	private static int port = Integer.parseInt(
			System.getProperty("server.port", "8080"));
	
	private static String baseURL = "http://localhost:" + port;
	
	private WebDriver driver;
	
	@BeforeClass
	public static void setupClass() {
		WebDriverManager.chromedriver().setup();
	}
	
	@Before
	public void setUp() throws Exception {
		baseURL = "http://localhost:" + port;
		driver = new ChromeDriver();
		try {
			Class.forName("org.h2.Driver");
			System.out.println("connecting to db");
			Connection conn = DriverManager.getConnection("jdbc:h2:~/mydb;AUTO_SERVER=TRUE;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE", "sa", "");
			Statement stmt = conn.createStatement();
			String createUser = "create table if not exists user";
			stmt.executeUpdate(createUser);
			String createFund = "create table if not exists fund";
			stmt.executeUpdate(createFund);
			String strDeleteUser = "delete from user";
			stmt.executeUpdate(strDeleteUser);
			String strDeleteFund = "delete from fund";
			stmt.executeUpdate(strDeleteFund);
			
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	@After
	public void tearDown() throws Exception {
		driver.quit();
	}

	//to verify a single behavior, two assertion are written in the same method
	@Test
	public void testRegisterNewUserAndVerifyThatIsNotPossibleInsertAnotherWithSameUsername() throws Exception {
		driver.get(baseURL);
		
		//go to register page
		driver.findElement(By.linkText("Register")).click();
		Thread.sleep(1500);
		
		//insert User info and register
		driver.findElement(By.id("username")).sendKeys("user");
		driver.findElement(By.id("password")).sendKeys("userPass");
		driver.findElement(By.id("password_two")).sendKeys("userPass");
		driver.findElement(By.name("btn_save")).click();
		
		//verify to return to "Login Page"
		assertThat(driver.getTitle()).isEqualTo("Login .::. Crowdfunding");
		Thread.sleep(1500);
		
		//now try to insert another user with the same username "user"
		driver.findElement(By.linkText("Register")).click();
		Thread.sleep(1500);
		driver.findElement(By.id("username")).sendKeys("user");
		driver.findElement(By.id("password")).sendKeys("userPass");
		driver.findElement(By.id("password_two")).sendKeys("userPass");
		driver.findElement(By.name("btn_save")).click();
		
		//verify that Username result in use and the system doesn't insert and redirect
		assertThat(driver.getTitle()).isEqualTo("Sign In .::. Crowdfunding");
	}
	
	@Test
	public void testRegisterNewAdmin() throws Exception {
		driver.get(baseURL);
		
		//go to register page
		driver.findElement(By.linkText("Register")).click();
		
		Thread.sleep(1500);
		
		//insert User info and register
		driver.findElement(By.id("username")).sendKeys("admin");
		driver.findElement(By.id("password")).sendKeys("admin1");
		driver.findElement(By.id("password_two")).sendKeys("admin1");
		driver.findElement(By.name("btn_save")).click();
		
		//verify to return to "Login Page"
		assertThat(driver.getTitle()).isEqualTo("Login .::. Crowdfunding");
	}

	@Test
	public void testIncorrectInputAreRefused() throws Exception {
		driver.get(baseURL);
		
		//go to register page
		driver.findElement(By.linkText("Register")).click();
		
		Thread.sleep(1500);
		
		//insert User info with NoMatch password
		driver.findElement(By.id("username")).sendKeys("newUser");
		driver.findElement(By.id("password")).sendKeys("pass01");
		driver.findElement(By.id("password_two")).sendKeys("pass05");
		driver.findElement(By.name("btn_save")).click();
		
		//verify that password not match and the system doesn't insert and redirect
		assertThat(driver.getTitle()).isEqualTo("Sign In .::. Crowdfunding");
	}
	
	@Test
	public void testInsertNewUserAndLoginSuccessfully() throws Exception {
		driver.get(baseURL);
		
		//go to register page
		driver.findElement(By.linkText("Register")).click();
		Thread.sleep(1500);
		
		//insert User info and register
		driver.findElement(By.id("username")).sendKeys("newUser");
		driver.findElement(By.id("password")).sendKeys("pass123");
		driver.findElement(By.id("password_two")).sendKeys("pass123");
		driver.findElement(By.name("btn_save")).click();
		Thread.sleep(1500);
		
		//now try to login in "Login Page"
		driver.findElement(By.name("username")).sendKeys("newUser");
		driver.findElement(By.name("password")).sendKeys("pass123");
		driver.findElement(By.name("btn_login")).click();
		Thread.sleep(1500);
		
		//verify that user login succesfully
		assertThat(driver.getTitle()).isEqualTo(".::. Crowdfunding .::.");
		
		assertThat(driver.findElement(By.id("div_welcome")).getText()).
		contains("Welcome, newUser");
	}
	
	@Test
	public void testNotPresentUserCannotLogin() throws Exception {
		driver.get(baseURL);
		
		//insert not existent info
		driver.findElement(By.name("username")).sendKeys("NotPresent");
		driver.findElement(By.name("password")).sendKeys("aPassword");
		driver.findElement(By.name("btn_login")).click();
		Thread.sleep(1500);
		
		//verify that page not change
		assertThat(driver.getTitle()).isEqualTo("Login .::. Crowdfunding");
	}

}
