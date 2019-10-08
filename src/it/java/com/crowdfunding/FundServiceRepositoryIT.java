package com.crowdfunding;


import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import com.crowdfunding.model.Fund;
import com.crowdfunding.repository.FundRepo;
import com.crowdfunding.service.FundService;

@RunWith(SpringRunner.class)
@DataJpaTest
@Import(FundService.class)
public class FundServiceRepositoryIT {

	@Autowired
	private FundService fundService;
	
	@Autowired
	private FundRepo fundRepo;
	
	@Test
	public void testFundServiceCanInsertIntoRepository() {
		Fund saved = fundService.insertNewFund(
				new Fund(null, "test fund", 0.0, 1, 1));
		assertThat(fundRepo.findById(saved.getId_fund())).isPresent();
	}
	
	@Test
	public void testFundServiceCanUpdateStateInRepositoryAsUser() {
		
		Fund saved = fundRepo.save(new Fund(null, "test fund", 0.0, 1, 1));
		fundService.userClosesFund(saved.getId_fund());
		assertThat(fundRepo.findById(saved.getId_fund()).get().getState())
			.isEqualTo(2);
	}
	
	@Test
	public void testFundServiceCanUpdateStateInRepositoryAsAdmin() {
		Fund saved = fundRepo.save(new Fund(null, "test fund", 0.0, 1, 1));
		fundService.adminClosesFund(saved.getId_fund());
		assertThat(fundRepo.findById(saved.getId_fund()).get().getState())
			.isEqualTo(3);
	}

	@Test
	public void testFundServiceCanUpdateFundIntoRepository() {
		Fund saved = fundRepo
				.save(new Fund(null,"text fund", 0.0, 1, 1));
		Fund replacement = new Fund(saved.getId_fund(), "new text", 0.0, 1, 1);
		
		replacement.setVersion(saved.getVersion());
		
		Fund modified = fundService.updateFundById(saved.getId_fund(), replacement);
			
		assertThat(fundRepo.findById(saved.getId_fund()).get()).isEqualTo(modified);
	}
	
	@Test
	public void testFundServiceCanUpdateMoneyByDonation() {
		Fund saved = fundRepo.save(new Fund(null, "test fund", 0.0, 1, 1));
		fundService.donateMoneyToFund(5.0, saved.getId_fund());
		assertThat(fundRepo.findById(saved.getId_fund()).get().getMoney())
			.isEqualTo(5.0);
	}
}
