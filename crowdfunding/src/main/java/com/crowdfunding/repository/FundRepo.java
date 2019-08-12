package com.crowdfunding.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.crowdfunding.model.Fund;

public interface FundRepo extends JpaRepository<Fund, Long> {

	List<Fund> findByOwner(int i);


	
}
