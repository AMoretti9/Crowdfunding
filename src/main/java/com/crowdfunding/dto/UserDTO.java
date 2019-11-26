package com.crowdfunding.dto;


public class UserDTO {
	private Long id;
	
	private String username;
	
	private Integer role;
	
	public UserDTO() {
		// required for serialization/deserialization
	}
	
	public UserDTO(Long id, String username, Integer role) {
		this.id=id;
		this.username=username;
		this.role=role;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id=id;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username=username;
	}
	
	public Integer getRole() {
		return role;
	}
	
	public void setRole(Integer role) {
		this.role=role;
	}

}
