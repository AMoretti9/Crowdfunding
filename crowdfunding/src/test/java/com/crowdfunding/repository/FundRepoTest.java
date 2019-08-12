package com.crowdfunding.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.crowdfunding.model.Fund;

@DataJpaTest
@RunWith(SpringRunner.class)
public class FundRepoTest {

	@Autowired
	private FundRepo fundRepo;
	
	@Test
	public void test_FindFundByOwner() {
		Fund fund1 = new Fund(1L, "Test Fund", 10.0, 1, 1);
		Fund fund2 = new Fund(2L, "second Test", 20.5, 1, 1);
		Fund saved1 = fundRepo.save(fund1);
		Fund saved2 = fundRepo.save(fund2);
		List<Fund> founds = fundRepo.findByOwner(1);
		assertThat(founds).containsExactly(saved1, saved2);
	}

}
