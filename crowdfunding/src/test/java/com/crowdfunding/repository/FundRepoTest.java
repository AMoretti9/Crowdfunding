package com.crowdfunding.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

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
	
	@Test
	public void test_FindOpenFundByOwnerNot() {
		Fund fund1 = new Fund(1L, "test fund one", 10.0, 1, 1);
		Fund fund2 = new Fund(2L, "test fund two", 20.0, 1, 2);
		Fund fund3 = new Fund(3L, "test fund three", 30.0, 1, 1);
		Fund fund4 = new Fund(4L, "test fund four", 40.0, 2, 1);
		Fund saved1 = fundRepo.save(fund1);
		Fund saved2 = fundRepo.save(fund2);
		Fund saved3 = fundRepo.save(fund3);
		Fund saved4= fundRepo.save(fund4);
		
		List<Fund> founds = fundRepo.findOpenFundsByOwnerNot(2);
		
		assertThat(founds).containsExactly(saved1, saved3);
		assertThat(founds).doesNotContain(saved2, saved4);
	}

	
	@Test
	public void test_UserClosesFund() {
		Fund fund = new Fund(null, "test fund", 5.0, 1, 1);
		Fund saved=fundRepo.save(fund);
		fundRepo.userClosesFund(saved.getId_fund());
		Optional<Fund> found = fundRepo.findById(saved.getId_fund());
		assertThat(found.get().getState()).isEqualTo(2);
	}
	
	@Test
	public void test_AdminClosesFund() {
		Fund fund = new Fund(null, "test fund", 5.0, 1, 1);
		Fund saved = fundRepo.save(fund);
		fundRepo.adminClosesFund(saved.getId_fund());
		Optional<Fund> found = fundRepo.findById(saved.getId_fund());
		assertThat(found.get().getState()).isEqualTo(3);
	}
	
	@Test
	public void test_donateMoneyToFund() {
		Fund fund = new Fund(null, "test fund", 5.5, 1, 1);
		Fund saved = fundRepo.save(fund);
		fundRepo.donateMoneyToFund(3.5, saved.getId_fund());
		Optional<Fund> found = fundRepo.findById(saved.getId_fund());
		assertThat(found.get().getMoney()).isEqualTo(9.0);
	}

}
