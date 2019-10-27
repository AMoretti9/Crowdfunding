package com.crowdfunding.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.crowdfunding.model.Fund;

public interface FundRepo extends JpaRepository<Fund, Long> {

	List<Fund> findByOwner(int i);
	
	@Query("Select f from Fund f where f.state=1 and f.owner <> :notOwner")
	List<Fund> findOpenFundsByOwnerNot(@Param("notOwner") int no);

	@Modifying(flushAutomatically = true, clearAutomatically = true)
	@Transactional
	@Query("update Fund f set f.state = 2 where f.idFund = :fundid")
	void userClosesFund(@Param("fundid") Long id);

	@Modifying(flushAutomatically = true, clearAutomatically = true)
	@Transactional
	@Query("update Fund f set f.state = 3 where f.idFund = :fundid")
	void adminClosesFund(@Param("fundid") Long id);
	
	@Modifying(flushAutomatically = true, clearAutomatically = true)
	@Transactional
	@Query("update Fund f set f.money = (f.money + :donation) where f.idFund = :fundid")
	void donateMoneyToFund(@Param("donation") Double donation, @Param("fundid") Long id);
}
