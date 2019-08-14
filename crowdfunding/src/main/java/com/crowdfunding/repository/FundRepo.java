package com.crowdfunding.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.crowdfunding.model.Fund;

public interface FundRepo extends JpaRepository<Fund, Long> {

	List<Fund> findByOwner(int i);
	
	@Query("Select f from Fund f where f.state=1 and f.owner <> :notOwner")
	List<Fund> findOpenFundsByOwnerNot(@Param("notOwner") int no);

	
}
