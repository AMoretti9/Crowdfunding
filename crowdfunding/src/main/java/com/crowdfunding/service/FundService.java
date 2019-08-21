package com.crowdfunding.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.crowdfunding.model.Fund;
import com.crowdfunding.repository.FundRepo;

@Service
public class FundService {

	private FundRepo fundRepo;
	
	public FundService(FundRepo fundRepo) {
		this.fundRepo=fundRepo;
	}
	
	public Fund getFundById(long id) {
		return fundRepo.findById(id).orElse(null);
	}
	
	public Fund insertNewFund(Fund fund) {
		
		fund.setId_fund(null);
		return fundRepo.save(fund);
		
	}

	public List<Fund> getFundByOwner(int owner) {
		return fundRepo.findByOwner(owner);
	}

	public List<Fund> getOpenFundsByOwnerNot(int notOwner) {
		return fundRepo.findOpenFundsByOwnerNot(notOwner);
	}
	
	public void userClosesFund (Long id) {
		fundRepo.userClosesFund(id);
	}
	
	public void adminClosesFund (Long id) {
		fundRepo.adminClosesFund(id);
	}
	
	public Fund updateFundById (long id, Fund replacement) {
		replacement.setId_fund(id);
		return fundRepo.save(replacement);
	}
	
}
