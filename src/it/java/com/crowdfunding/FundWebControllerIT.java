package com.crowdfunding;


import static org.assertj.core.api.Assertions.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import com.crowdfunding.model.Fund;
import com.crowdfunding.model.User;
import com.crowdfunding.repository.FundRepo;
import com.crowdfunding.repository.UserRepo;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class FundWebControllerIT {
	
	@Autowired
	FundRepo fundRepo;
	
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
		fundRepo.deleteAll();
		fundRepo.flush();
		userRepo.deleteAll();
		userRepo.flush();
	}

	@After
	public void tearDown() throws Exception {
		driver.quit();
	}

	@Test
	public void testInsertNewFund() throws Exception  {
		
		//insert new user
		User saved = new User(null, "user", "thePass", 1);
		userRepo.save(saved);
		
		//user login
		driver.get(baseURL);
		driver.findElement(By.name("username")).sendKeys("user");
		driver.findElement(By.name("password")).sendKeys("thePass");
		driver.findElement(By.name("btn_login")).click();
		Thread.sleep(500);
		
		//user insert new fund
		driver.findElement(By.linkText("MY FUNDS")).click();
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("subject")));
		driver.findElement(By.id("subject")).sendKeys("test fund");
		driver.findElement(By.name("btn_save_fund")).click();
		
		//verify that fund repository added the fund
		assertThat(fundRepo.findAll()).isNotNull();
		
	}
	
	@Test
	public void testEditFund() throws Exception {
		
		//insert new user and new fund of the user
		User saved = new User(null, "user", "thePass", 1);
		userRepo.save(saved);
		Fund fund1 = new Fund(null, "text fund", 0.0, 1, saved.getId().intValue());
		fundRepo.save(fund1);
		
		//user login
		driver.get(baseURL);
		driver.findElement(By.name("username")).sendKeys("user");
		driver.findElement(By.name("password")).sendKeys("thePass");
		driver.findElement(By.name("btn_login")).click();
		Thread.sleep(500);
		
		//user enter into own fund
		driver.findElement(By.linkText("MY FUNDS")).click();
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("myFund_table")));
		assertThat(driver.findElement(By.id("myFund_table")).getText())
		.contains(fund1.getIdFund().toString(), "text fund", "0.0", "OPEN", "Enter");
		driver.findElement(By.linkText("Enter")).click();
		
		//user modify the subject
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("subject")));
		driver.findElement(By.id("subject")).clear();
		driver.findElement(By.id("subject")).sendKeys("NEW text fund");
		driver.findElement(By.name("btn_edit_subject")).click();
		
		Thread.sleep(500);
		driver.findElement(By.linkText("MY FUNDS")).click();
		
		//verify the change into My Funds table
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("myFund_table")));
		assertThat(driver.findElement(By.id("myFund_table")).getText())
		.contains(fund1.getIdFund().toString(), "NEW text fund", "0.0", "OPEN", "Enter");
	}
	
	@Test
	public void makeDonationToUsersFund() throws Exception {
		
		//insert new user and new fund of another user
		User saved = new User(null, "user", "thePass", 1);
		userRepo.save(saved);
		Fund fund1 = new Fund(null, "text fund", 0.0, 1, 5);
		fundRepo.save(fund1);
		
		//user login
		driver.get(baseURL);
		driver.findElement(By.name("username")).sendKeys("user");
		driver.findElement(By.name("password")).sendKeys("thePass");
		driver.findElement(By.name("btn_login")).click();
		Thread.sleep(500);
		
		//user enter into other user's fund
		driver.findElement(By.linkText("USERS' FUNDS")).click();
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("usersFund_table")));
		assertThat(driver.findElement(By.id("usersFund_table")).getText())
		.contains(fund1.getIdFund().toString(), "text fund", "0.0", "Enter");
		driver.findElement(By.linkText("Enter")).click();
		
		//user make a donation
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("donation")));
		driver.findElement(By.id("donation")).sendKeys("10.5");
		driver.findElement(By.name("btn_donate")).click();
		
		Thread.sleep(500);
		driver.findElement(By.linkText("USERS' FUNDS")).click();
		
		//verify the change into Users Funds table
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("usersFund_table")));
		assertThat(driver.findElement(By.id("usersFund_table")).getText())
		.contains(fund1.getIdFund().toString(), "text fund", "10.5", "Enter");
	}
	
	@Test
	public void testUserClosesOwnFund() throws Exception {
		
		//insert new user and new fund of the user
		User saved = new User(null, "user", "thePass", 1);
		userRepo.save(saved);
		Fund fund1 = new Fund(null, "text fund", 0.0, 1, saved.getId().intValue());
		fundRepo.save(fund1);
		
		//user login
		driver.get(baseURL);
		driver.findElement(By.name("username")).sendKeys("user");
		driver.findElement(By.name("password")).sendKeys("thePass");
		driver.findElement(By.name("btn_login")).click();
		Thread.sleep(500);
		
		//user enter into own fund
		driver.findElement(By.linkText("MY FUNDS")).click();
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("myFund_table")));
		assertThat(driver.findElement(By.id("myFund_table")).getText())
		.contains(fund1.getIdFund().toString(), "text fund", "0.0", "OPEN", "Enter");
		driver.findElement(By.linkText("Enter")).click();
		
		//user closes own fund
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("btn_userCloses")));
		driver.findElement(By.name("btn_userCloses")).click();
		
		Thread.sleep(500);
		driver.findElement(By.linkText("MY FUNDS")).click();
		
		//verify that the fund is closed into My Funds table
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("myFund_table")));
		assertThat(driver.findElement(By.id("myFund_table")).getText())
		.contains(fund1.getIdFund().toString(), "text fund", "0.0", "CLOSED", "Enter");
	}
	
	@Test
	public void testAdminClosesUsersFund() throws Exception {
		
		//insert new user and new fund of the user, insert new admin
		User saved = new User(null, "user", "thePass", 1);
		userRepo.save(saved);
		User admin = new User(null, "admin", "adpass", 2);
		userRepo.save(admin);
		Fund fund1 = new Fund(null, "text fund", 0.0, 1, saved.getId().intValue());
		fundRepo.save(fund1);
		
		//admin login
		driver.get(baseURL);
		driver.findElement(By.name("username")).sendKeys("admin");
		driver.findElement(By.name("password")).sendKeys("adpass");
		driver.findElement(By.name("btn_login")).click();
		Thread.sleep(500);
		driver.findElement(By.linkText("USERS' FUNDS")).click();
		
		//admin enter into other user's fund
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("usersFund_table")));
		assertThat(driver.findElement(By.id("usersFund_table")).getText())
		.contains(fund1.getIdFund().toString(), "text fund", "0.0", "Enter");
		driver.findElement(By.linkText("Enter")).click();
		
		//admin closes other user's fund
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("btn_userCloses")));
		driver.findElement(By.name("btn_userCloses")).click();
		
		//admin logout
		Thread.sleep(500);
		driver.findElement(By.linkText("LOGOUT")).click();
		Thread.sleep(500);
		
		//user login
		driver.findElement(By.name("username")).sendKeys("user");
		driver.findElement(By.name("password")).sendKeys("thePass");
		driver.findElement(By.name("btn_login")).click();
		Thread.sleep(500);
		driver.findElement(By.linkText("MY FUNDS")).click();
		
		//user verify that own fund into My Funds table is Closed by Admin
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("myFund_table")));
		assertThat(driver.findElement(By.id("myFund_table")).getText())
		.contains(fund1.getIdFund().toString(), "text fund", "0.0", "CLOSED by Admin", "Enter");
		
	}

}
