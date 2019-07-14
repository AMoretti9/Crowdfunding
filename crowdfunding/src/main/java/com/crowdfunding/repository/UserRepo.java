package com.crowdfunding.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.crowdfunding.model.User;

public interface UserRepo extends JpaRepository<User, Long> {

}
