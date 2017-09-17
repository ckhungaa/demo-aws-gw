package com.elephant.common.persistence.repository;

import java.util.Optional;

import com.elephant.common.persistence.domain.Account;
import com.elephant.common.persistence.domain.ApiPermission;

public interface AccountRepository {

	public Optional<Account> findByPublicKey(String publicKey);
	
	public Optional<ApiPermission> findByAccountAndCode(Account account,String code);
	
	
}
