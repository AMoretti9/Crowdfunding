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
	
	public Fund insertNewFund(Fund fund) {
		
		fund.setId(null);
		return fundRepo.save(fund);
		
	}

	public List<Fund> getFundByOwner(int owner) {
		return fundRepo.findByOwner(owner);
	}
	
	
	
}
