package com.crowdfunding.model;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class User {
	
	@Id @GeneratedValue
	private Long id;
	
	private String username;
	
	private String password;
	
	private Integer role;
	
	public User() {
		// required for serialization/deserialization
	}
	
	public User(Long id, String username, String password, Integer role) {
		this.id=id;
		this.username=username;
		this.password=password;
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
	
	@Override
	public int hashCode() {
		return Objects.hash(id, username, password, role);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		User other = (User) obj;
		return Objects.equals(id, other.id) && Objects.equals(username, other.username) && 
				Objects.equals(password,  other.password) && Objects.equals(role,  other.role);
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", password=" + password + ", role=" + role + "]";
	}
}
