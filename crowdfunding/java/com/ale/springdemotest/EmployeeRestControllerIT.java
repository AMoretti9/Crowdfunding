package com.ale.springdemotest;


import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import com.ale.springdemotest.model.Employee;
import com.ale.springdemotest.repo.EmployeeReposi;

import io.restassured.RestAssured;
import io.restassured.response.Response;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class EmployeeRestControllerIT {
	
	@Autowired
	private EmployeeReposi repo;
	
	@LocalServerPort
	private int port;
	
	@Before
	public void setup() {
		RestAssured.port=port;
		//always empty db
		repo.deleteAll();
		repo.flush();
	}
	
	@Test
	public void testNewEmployee() throws Exception {
		Response response = 
				given()
					.contentType(MediaType.APPLICATION_JSON_VALUE)
					.body(new Employee(null, "NewEmp", 1000))
				.when()
					.post("/api/employees/new");
		Employee saved = response.getBody().as(Employee.class);
		
		assertThat(repo.findById(saved.getId()).get())
			.isEqualTo(saved);
	}
	
	@Test
	public void testUpdateEmployee() throws Exception {
		Employee saved = repo.save(new Employee(null, "saved", 1000));
		
		given()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.body(new Employee(null, "newName", 2000))
		.when()
			.put("/api/employees/update/" + saved.getId())
		.then()
			.statusCode(200)
			.body(
					"id", equalTo(saved.getId().intValue()),
					"name", equalTo("newName"),
					"salary", equalTo(2000)
				);
	}

}
