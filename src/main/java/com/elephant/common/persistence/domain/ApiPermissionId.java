package com.elephant.common.persistence.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class ApiPermissionId implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1504022952151048049L;

	@Column(name = "account_uid")
	private Long accountUid;
	
	@Column(name = "api_uid")
	private Long apiUid; 
	
	
	
	public Long getAccountUid() {
		return accountUid;
	}

	public Long getApiUid() {
		return apiUid;
	}
	public boolean equals(Object other) {
        if (this == other)
            return true;
        if (!(other instanceof ApiPermissionId))
            return false;
        ApiPermissionId castOther = (ApiPermissionId) other;
        return accountUid.equals(castOther.accountUid) && apiUid.equals(castOther.apiUid);
    }

	@Override
	public int hashCode() {
		final int prime = 31;
        int hash = 17;
        hash = hash * prime + this.accountUid.hashCode();
        hash = hash * prime + this.apiUid.hashCode();
        return hash;
	}
    
    
	
	
}
