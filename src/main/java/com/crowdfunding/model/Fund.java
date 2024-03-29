package com.crowdfunding.model;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;

@Entity
public class Fund {
	
	@Id @GeneratedValue
	private Long idFund;
	
	@Version
    private Long version;
	
	private String subject;
	
	private Double money;
	
	private Integer state;
	
	private Integer owner;
	
	public Fund() {
		// required for serialization/deserialization
	}
	
	public Fund(Long idFund, String subject, Double money, Integer state, Integer owner) {
		this.idFund=idFund;
		this.subject=subject;
		this.money=money;
		this.state=state;
		this.owner=owner;
	}
	
	public Long getIdFund() {
		return idFund;
	}
	
	public void setIdFund(Long idFund) {
		this.idFund=idFund;
	}
	
	public Long getVersion() {
        return version;
    }
    public void setVersion(Long version) {
        this.version = version;
    }
	
	public String getSubject() {
		return subject;
	}
	
	public void setSubject(String subject) {
		this.subject=subject;
	}
	
	public Double getMoney() {
		return money;
	}
	
	public void setMoney(Double money) {
		this.money=money;
	}
	
	public Integer getState() {
		return state;
	}
	
	public void setState(Integer state) {
		this.state=state;
	}
	
	public Integer getOwner() {
		return owner;
	}
	
	public void setOwner(Integer owner) {
		this.owner=owner;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(idFund, subject, money, state, owner);
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
		Fund other = (Fund) obj;
		return Objects.equals(idFund, other.idFund) && Objects.equals(subject, other.subject) && Objects.equals(money,  other.money) &&
				Objects.equals(state,  other.state) && Objects.equals(owner,  other.owner);
	}

	@Override
	public String toString() {
		return "Fund [id=" + idFund + ", subject=" + subject + ", money=" + money + ", state=" + state + ", owner=" + owner + "]";
	}
}
