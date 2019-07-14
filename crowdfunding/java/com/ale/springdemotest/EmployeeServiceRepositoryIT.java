package com.ale.springdemotest;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import com.ale.springdemotest.model.Employee;
import com.ale.springdemotest.repo.EmployeeReposi;
import com.ale.springdemotest.service.EmployeeService;

@RunWith(SpringRunner.class)
@DataJpaTest
@Import(EmployeeService.class)
public class EmployeeServiceRepositoryIT {

	@Autowired
	private EmployeeService employeeService;
	
	@Autowired
	private EmployeeReposi repo;
	
	@Test
	public void testServiceCanInsertIntoRepo() {
		Employee saved = employeeService.insertNewEmployee(
				new Employee(null, "test", 1000));
		
		repo.findById(saved.getId()).isPresent();
	}
	
	@Test
	public void testServiceCanUpdateIntoRepo() {
		Employee saved = repo.save(new Employee(null, "name1", 1000));
		Employee newer = repo.save(new Employee(saved.getId(), "nameNew", 1500));
		employeeService.updateEmployeeById(saved.getId(), newer);
		//Employee newer = employeeService.updateEmployeeById(saved.getId(),
			//	new Employee(saved.getId(), "nameNew", 1500));
		assertThat(repo.findById(saved.getId()).get())
			.isEqualTo(newer);
	}

}
