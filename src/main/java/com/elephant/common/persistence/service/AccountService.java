package com.elephant.common.persistence.service;

import java.util.Optional;

import com.elephant.common.persistence.domain.Account;
import com.elephant.common.persistence.domain.ApiPermission;

public interface AccountService {

	public Optional<Account> findByPublicKey(String publicKey);

	public Optional<ApiPermission> findByAccountAndCode(Account account, String code);
	

}
