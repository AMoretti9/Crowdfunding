package com.ale.springdemotest;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import com.ale.springdemotest.model.Employee;
import com.ale.springdemotest.repo.EmployeeReposi;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class EmployeeWebControllerIT {
	
	@Autowired
	private EmployeeReposi repo;
	
	@LocalServerPort
	private int port;
	
	private WebDriver driver;
	private String baseURL;
	
	@Before
	public void setup() {
		baseURL = "http://localhost:" + port;
		driver = new HtmlUnitDriver();
		repo.deleteAll();
		repo.flush();
	}
	
	@After
	public void tearDown() {
		driver.quit();
	}
	
	@Test
	public void testHomePage() {
		Employee testEmp = 
				repo.save(new Employee(null, "name1", 1000));
		driver.get(baseURL);
		
		assertThat(driver.findElement(By.id("employee_table")).getText())
			.contains("name1", "1000", "Edit");
		
		driver.findElement(By.cssSelector(
				"a[href*='/edit/" + testEmp.getId() + "']"));
	}
	
	
	@Test
	public void testEditPageNewEmployee() throws Exception {
		driver.get(baseURL + "/new");

		driver.findElement(By.name("name")).sendKeys("newEmp");
		driver.findElement(By.name("salary")).sendKeys("2000");
		driver.findElement(By.name("btn_submit")).click();

		assertThat(repo.findByName("newEmp").getSalary())
			.isEqualTo(2000L);
	}
	
	@Test
	public void testEditPageUpdateEmployee() throws Exception {
		Employee testEmp =
			repo.save(new Employee(null, "testEmp", 1000));
		driver.get(baseURL + "/edit/" + testEmp.getId());
		
		final WebElement nameField = driver.findElement(By.name("name"));
		nameField.clear();
		nameField.sendKeys("NewEmp");
		final WebElement salaryField = driver.findElement(By.name("salary"));
		salaryField.clear();
		salaryField.sendKeys("3000");
		driver.findElement(By.name("btn_submit")).click();
		
		assertThat(repo.findByName("NewEmp").getSalary())
			.isEqualTo(3000L);
	}
}
