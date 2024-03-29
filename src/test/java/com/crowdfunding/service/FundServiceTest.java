package com.crowdfunding.service;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.crowdfunding.model.Fund;
import com.crowdfunding.repository.FundRepo;

@RunWith(MockitoJUnitRunner.class)
public class FundServiceTest {

	@Mock
	private FundRepo fundRepo;
	
	@InjectMocks
	private FundService fundService;
	
	@Test
	public void test_insertNewFund_setsIdToNull_andReturnSavedFund() {
		Fund toSave = spy(new Fund(1L, "toSave fund", 5.0, 1, 1));
		Fund saved = new Fund(99L, "saved fund", 10.0, 1, 1);
		
		when(fundRepo.save(any(Fund.class)))
			.thenReturn(saved);
		
		Fund result = fundService.insertNewFund(toSave);
		
		assertThat(result).isSameAs(saved);
		
		InOrder inOrder = inOrder(toSave, fundRepo);
		inOrder.verify(toSave).setIdFund(null);
		inOrder.verify(fundRepo).save(toSave);
	}
	
	@Test
	public void test_getFundByOwner_NotFound() {
		when(fundRepo.findByOwner(anyInt())).thenReturn(null);
		assertThat(fundService.getFundByOwner(1)).isNull();
	}
	
	@Test
	public void test_getFundByOwner_Found() {
		Fund fund1 = new Fund(1L, "first test fund", 10.0, 1, 2);
		Fund fund2 = new Fund(2L, "second test fund", 5.0, 1, 2);
		when(fundRepo.findByOwner(2))
			.thenReturn(asList(fund1, fund2));
		assertThat(fundService.getFundByOwner(2))
			.containsExactly(fund1, fund2);
	}
	
	@Test 
	public void test_getOpenFundByOwnerNot_NotFound() {
		when(fundRepo.findOpenFundsByOwnerNot(anyInt())).thenReturn(null);
		assertThat(fundService.getOpenFundsByOwnerNot(1)).isNull();
	}
	
	@Test
	public void test_getOpenFundByOwnerNot_Found() {
		Fund fund1 = new Fund(1L, "test fund one", 10.0, 1, 1);
		Fund fund2 = new Fund(2L, "test fund two", 20.0, 1, 2);
		Fund fund3 = new Fund(3L, "test fund three", 30.0, 1, 1);
		Fund fund4 = new Fund(4L, "test fund four", 40.0, 2, 1);
		when(fundRepo.findOpenFundsByOwnerNot(2))
			.thenReturn(asList(fund1, fund3));
		assertThat(fundService.getOpenFundsByOwnerNot(2))
			.containsExactly(fund1, fund3);
		assertThat(fundService.getOpenFundsByOwnerNot(2))
			.doesNotContain(fund2, fund4);
		
	}
	
	@Test
	public void test_getFundById_found() {
		Fund fund = new Fund(1L, "test", 0.0, 1, 1);
		when(fundRepo.findById(1L)).thenReturn(Optional.of(fund));
		assertThat(fundService.getFundById(1)).isSameAs(fund);
	}
	
	@Test
	public void test_getFundById_NotFound() {
		when(fundRepo.findById(anyLong())).thenReturn(Optional.empty());
		assertThat(fundService.getFundById(1)).isNull();
	}
	
	@Test
	public void test_userClosesFund() {
		fundService.userClosesFund(anyLong());
		verify(fundRepo, times(1)).userClosesFund(anyLong());
	}
	
	@Test
	public void test_adminClosesFund() {
		fundService.adminClosesFund(anyLong());
		verify(fundRepo, times(1)).adminClosesFund(anyLong());
	}
	
	@Test
	public void test_donateMoneyToFund() {
		fundService.donateMoneyToFund(anyDouble(), anyLong());
		verify(fundRepo, times(1)).donateMoneyToFund(anyDouble(), anyLong());
	}
	
	@Test
	public void test_updateFundById_setsIdToArgument_and_returnsSavedFund() {
		Fund replacement = spy(new Fund(null, "replacement fund", 0.0, 1, 1));
		Fund replaced = new Fund(1L, "fund to replace", 0.0, 1, 1);

		when(fundRepo.save(any(Fund.class)))
			.thenReturn(replaced);

		Fund result = fundService.updateFundById(1L, replacement);

		assertThat(result).isSameAs(replaced);

		InOrder inOrder = inOrder(replacement, fundRepo);
		inOrder.verify(replacement).setIdFund(1L);
		inOrder.verify(fundRepo).save(replacement);
	}
}
