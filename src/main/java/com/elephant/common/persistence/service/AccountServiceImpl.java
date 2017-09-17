package com.elephant.common.persistence.service;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.elephant.common.persistence.domain.Account;
import com.elephant.common.persistence.domain.ApiPermission;
import com.elephant.common.persistence.repository.AccountRepository;

@ApplicationScoped
public class AccountServiceImpl implements AccountService{

	/**
	 * TODO: Consider to user distributed cache for performance tuning
	 * 
	 */
	
	@Inject
	private AccountRepository accountRepository;
	
	@Override
	public Optional<Account> findByPublicKey(String publicKey) {
		return accountRepository.findByPublicKey(publicKey);
	}

	@Override
	public Optional<ApiPermission> findByAccountAndCode(Account account, String code) {
		return accountRepository.findByAccountAndCode(account, code);
	}

	
}
