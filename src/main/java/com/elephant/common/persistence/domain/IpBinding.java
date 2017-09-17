package com.elephant.common.persistence.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.elephant.common.domain.Modifiable;

@Entity
@Table(name = "gw_account_ip_binding")
public class IpBinding extends Modifiable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4337292300211852711L;
	
	@Column(name = "address")
	private String address;
	
	@Column(name = "net_mask")
	private String netMask;

	public String getAddress() {
		return address;
	}

	public String getNetMask() {
		return netMask;
	}
	
}
