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
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import io.github.bonigarcia.wdm.WebDriverManager;

public class FundWebControllerE2E {

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
	
	
	@Test
	public void testUserOpenNewFund() throws Exception {
		driver.get(baseURL);
		
		//go to register page
		driver.findElement(By.linkText("Register")).click();
		Thread.sleep(1500);
		
		//insert User info and register
		driver.findElement(By.id("username")).sendKeys("user1");
		driver.findElement(By.id("password")).sendKeys("pass01");
		driver.findElement(By.id("password_two")).sendKeys("pass01");
		driver.findElement(By.name("btn_save")).click();
		Thread.sleep(1500);
		
		//now try to login in "Login Page"
		driver.findElement(By.name("username")).sendKeys("user1");
		driver.findElement(By.name("password")).sendKeys("pass01");
		driver.findElement(By.name("btn_login")).click();
		Thread.sleep(1500);
		
		//go to "My Funds" page
		driver.findElement(By.linkText("MY FUNDS")).click();
		Thread.sleep(1500);
		
		//insert new Fund
		driver.findElement(By.id("subject")).sendKeys("test fund number One");
		driver.findElement(By.name("btn_save_fund")).click();
		
		//verify that new Fund is present and OPEN
		assertThat(driver.findElement(By.id("myFund_table")).getText()).
			contains("test fund number One", "OPEN");
	}
	
	@Test
	public void testUserOpenNewFundThenUpdateTheSubject() throws Exception {
		driver.get(baseURL);
		
		//go to register page
		driver.findElement(By.linkText("Register")).click();
		Thread.sleep(1500);
		
		//insert User info and register
		driver.findElement(By.id("username")).sendKeys("user3");
		driver.findElement(By.id("password")).sendKeys("pass03");
		driver.findElement(By.id("password_two")).sendKeys("pass03");
		driver.findElement(By.name("btn_save")).click();
		Thread.sleep(1500);
		
		//now try to login in "Login Page"
		driver.findElement(By.name("username")).sendKeys("user3");
		driver.findElement(By.name("password")).sendKeys("pass03");
		driver.findElement(By.name("btn_login")).click();
		Thread.sleep(1500);
		
		//go to "My Funds" page
		driver.findElement(By.linkText("MY FUNDS")).click();
		Thread.sleep(1500);
		
		//insert new Fund
		driver.findElement(By.id("subject")).sendKeys("test fund number Three");
		driver.findElement(By.name("btn_save_fund")).click();
		
		//enter into fund created
		driver.findElement(By.linkText("Enter")).click();
		Thread.sleep(1500);
		
		//update the subject of fund
		driver.findElement(By.id("subject")).clear();
		driver.findElement(By.id("subject")).sendKeys("new text for number Three");
		driver.findElement(By.name("btn_edit_subject")).click();
		
		//go to My Fund and verify that the subject is updated
		driver.findElement(By.linkText("MY FUNDS")).click();
		Thread.sleep(1500);
		assertThat(driver.findElement(By.id("myFund_table")).getText()).
		contains("new text for number Three", "OPEN");
		
	}
	
	@Test
	public void testUserOpenNewFundThenClosesIt() throws Exception {
		driver.get(baseURL);
		
		//go to register page
		driver.findElement(By.linkText("Register")).click();
		Thread.sleep(1500);
		
		//insert User info and register
		driver.findElement(By.id("username")).sendKeys("user2");
		driver.findElement(By.id("password")).sendKeys("pass02");
		driver.findElement(By.id("password_two")).sendKeys("pass02");
		driver.findElement(By.name("btn_save")).click();
		Thread.sleep(1500);
		
		//now try to login in "Login Page"
		driver.findElement(By.name("username")).sendKeys("user2");
		driver.findElement(By.name("password")).sendKeys("pass02");
		driver.findElement(By.name("btn_login")).click();
		Thread.sleep(1500);
		
		//go to "My Funds" page
		driver.findElement(By.linkText("MY FUNDS")).click();
		Thread.sleep(1500);
		
		//insert new Fund
		driver.findElement(By.id("subject")).sendKeys("test fund number Two");
		driver.findElement(By.name("btn_save_fund")).click();
		
		//enter into fund created
		driver.findElement(By.linkText("Enter")).click();
		Thread.sleep(1500);
		
		//close the fund
		driver.findElement(By.name("btn_userCloses")).click();
		
		//go to My Fund and verify that fund is CLOSED
		driver.findElement(By.linkText("MY FUNDS")).click();
		Thread.sleep(1500);
		assertThat(driver.findElement(By.id("myFund_table")).getText()).
		contains("test fund number Two", "CLOSED");
	}
	
	@Test
	public void testUserOpenNewFundThenAnotherUserMakeADonation() throws Exception {
		driver.get(baseURL);
		
		// USER 1 OPERATION
		//go to register page
		driver.findElement(By.linkText("Register")).click();
		Thread.sleep(1500);
		
		//insert User info and register
		driver.findElement(By.id("username")).sendKeys("user10");
		driver.findElement(By.id("password")).sendKeys("pass10");
		driver.findElement(By.id("password_two")).sendKeys("pass10");
		driver.findElement(By.name("btn_save")).click();
		Thread.sleep(1500);
		
		//now try to login in "Login Page"
		driver.findElement(By.name("username")).sendKeys("user10");
		driver.findElement(By.name("password")).sendKeys("pass10");
		driver.findElement(By.name("btn_login")).click();
		Thread.sleep(1500);
		
		//go to "My Funds" page
		driver.findElement(By.linkText("MY FUNDS")).click();
		Thread.sleep(1500);
		
		//insert new Fund, then logout
		driver.findElement(By.id("subject")).sendKeys("fund to test donation");
		driver.findElement(By.name("btn_save_fund")).click();
		
		//find 'id' of inserted fund
		WebElement cell = driver.findElement(By.xpath("//*[@id=\"myFund_table\"]/tbody/tr/td[1]"));
		String idNewFund = cell.getText();

		driver.findElement(By.linkText("LOGOUT")).click();
		
		
		
		//USER 2 OPERATION
		//go to register page
		driver.findElement(By.linkText("Register")).click();
		Thread.sleep(1500);
				
		//insert User info and register
		driver.findElement(By.id("username")).sendKeys("user11");
		driver.findElement(By.id("password")).sendKeys("pass11");
		driver.findElement(By.id("password_two")).sendKeys("pass11");
		driver.findElement(By.name("btn_save")).click();
		Thread.sleep(1500);
		
		//now try to login in "Login Page"
		driver.findElement(By.name("username")).sendKeys("user11");
		driver.findElement(By.name("password")).sendKeys("pass11");
		driver.findElement(By.name("btn_login")).click();
		Thread.sleep(1500);
		
		//go to "Users' Funds" page
		driver.findElement(By.linkText("USERS' FUNDS")).click();
		Thread.sleep(1500);
		
		//verify that there is a fund and Enter into that
		assertThat(driver.findElement(By.id("usersFund_table")).getText()).
		contains("fund to test donation", "0.0");
		driver.findElement(By.cssSelector("a[href*='/userfund/" + idNewFund + "']")).click();
		Thread.sleep(1500);
		
		//insert the amount and make donation
		driver.findElement(By.name("donation")).sendKeys("25.0");
		driver.findElement(By.name("btn_donate")).click();
		
		//return in Users' Funds and verify the donation
		driver.findElement(By.linkText("USERS' FUNDS")).click();
		Thread.sleep(1500);
		assertThat(driver.findElement(By.id("usersFund_table")).getText()).
		contains("fund to test donation", "25.0");
		
	}

	@Test
	public void testUserOpenNewFundThenAdminClosesThat() throws Exception {
		driver.get(baseURL);
		
		// USER 1 OPERATION
		//go to register page
		driver.findElement(By.linkText("Register")).click();
		Thread.sleep(1500);
		
		//insert User info and register
		driver.findElement(By.id("username")).sendKeys("user12");
		driver.findElement(By.id("password")).sendKeys("pass12");
		driver.findElement(By.id("password_two")).sendKeys("pass12");
		driver.findElement(By.name("btn_save")).click();
		Thread.sleep(1500);
			
		//now try to login in "Login Page"
		driver.findElement(By.name("username")).sendKeys("user12");
		driver.findElement(By.name("password")).sendKeys("pass12");
		driver.findElement(By.name("btn_login")).click();
		Thread.sleep(1500);
		
		//go to "My Funds" page
		driver.findElement(By.linkText("MY FUNDS")).click();
		Thread.sleep(1500);
		
		//insert new Fund, then logout
		driver.findElement(By.id("subject")).sendKeys("fund closed by admin");
		driver.findElement(By.name("btn_save_fund")).click();
		
		//find 'id' of inserted fund
		WebElement cell = driver.findElement(By.xpath("//*[@id=\"myFund_table\"]/tbody/tr/td[1]"));
		String idNewFund = cell.getText();
		
		driver.findElement(By.linkText("LOGOUT")).click();
		Thread.sleep(1500);
		
		//ADMIN OPERATION
		//go to register page
		driver.findElement(By.linkText("Register")).click();
		Thread.sleep(1500);
						
		//insert ADMIN info and register
		driver.findElement(By.id("username")).sendKeys("admin");
		driver.findElement(By.id("password")).sendKeys("adminpsw");
		driver.findElement(By.id("password_two")).sendKeys("adminpsw");
		driver.findElement(By.name("btn_save")).click();
		Thread.sleep(1500);
				
		//now try to login in "Login Page"
		driver.findElement(By.name("username")).sendKeys("admin");
		driver.findElement(By.name("password")).sendKeys("adminpsw");
		driver.findElement(By.name("btn_login")).click();
		Thread.sleep(1500);
				
		//go to "Users' Funds" page
		driver.findElement(By.linkText("USERS' FUNDS")).click();
		Thread.sleep(1500);
				
		//verify that there is a fund and Enter into that
		assertThat(driver.findElement(By.id("usersFund_table")).getText()).
			contains("fund closed by admin", "0.0");
		driver.findElement(By.cssSelector("a[href*='/userfund/" + idNewFund + "']")).click();
		Thread.sleep(1500);
				
		//close the fund
		driver.findElement(By.name("btn_userCloses")).click();
		
		driver.findElement(By.linkText("LOGOUT")).click();
		Thread.sleep(1500);
		
		//USER 1 OPERATION
		//user login
		driver.findElement(By.name("username")).sendKeys("user12");
		driver.findElement(By.name("password")).sendKeys("pass12");
		driver.findElement(By.name("btn_login")).click();
		Thread.sleep(1500);
		
		//go to "My Funds" page
		driver.findElement(By.linkText("MY FUNDS")).click();
		Thread.sleep(1500);
		
		//verify that the Fund was CLOSED by Admin
		assertThat(driver.findElement(By.id("myFund_table")).getText()).
		contains("fund closed by admin", "CLOSED by Admin");
	}
	
}
