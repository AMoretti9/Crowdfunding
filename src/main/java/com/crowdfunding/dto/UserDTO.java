package com.crowdfunding.dto;

import com.crowdfunding.model.User;

public class UserDTO {
	
	private Long id;
	
	private String username;
	
	private String password;
	
	private Integer role;
	
	public UserDTO() {
		
	}
	
	public UserDTO(Long id, String username, String password, Integer role) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.role=role;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username=username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password=password;
	}
	
	public Integer getRole() {
		return role;
	}
	
	public void setRole(Integer role) {
		this.role=role;
	}
	
	public User getUser() {
		return new User(this.id, this.username, this.password, this.role);
	}
}
