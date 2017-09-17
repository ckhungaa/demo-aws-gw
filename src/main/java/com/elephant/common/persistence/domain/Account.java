package com.elephant.common.persistence.domain;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.elephant.common.domain.Modifiable;
import com.elephant.common.persistence.convertor.LocalDateTimePersistenceConverter;
import com.elephant.common.utils.NetworkUtil;
import com.google.common.collect.Lists;

@Entity
@Table(name = "gw_account")
public class Account extends Modifiable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7950117960627543347L;

	@Column(name = "account_id")
	private String accountId;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "email")
	private String email;
	
	@Column(name = "api_key")
	private String publicKey;
	
	@Column(name = "secret_key")
	private String privateKey;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private AccountStatus status;
	
	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name = "start_date")
	private LocalDateTime startDate;
	
	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name = "end_date")
	private LocalDateTime endDate;
	
	@OneToMany(fetch=FetchType.LAZY)
	@JoinColumn(name = "account_uid",referencedColumnName = "uid")
	private List<IpBinding> permittedIps = Lists.newArrayList();
	
	public boolean isActive(){
		return this.status == AccountStatus.ACTIVE;
	}

	public boolean isInValidPeriod(){
		LocalDateTime current = LocalDateTime.now();
		boolean isValid = true;
		if (isValid && startDate != null) {
			isValid = current.isAfter(startDate);
		}
		
		if (isValid && endDate != null) {
			isValid = current.isBefore(endDate);
		}
		return isValid;
	}
	
	public String getPublicKey() {
		return publicKey;
	}

	public String getPrivateKey() {
		return privateKey;
	}
	
	public String getAccountId(){
		return accountId;
	}
	
	public boolean isValidIp(String sourceIP){
		//no setting => allow all
		if (permittedIps.size() == 0) {
			return true;
		}
		
		for (IpBinding ipBind : permittedIps) {
            if (NetworkUtil.netMatch(ipBind.getAddress() + "/" + ipBind.getNetMask(), sourceIP)) {
            	return true;
            }
        }
		return false;
	}

}
