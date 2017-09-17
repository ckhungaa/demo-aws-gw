package com.elephant.common.persistence.repository;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.elephant.common.persistence.domain.Account;
import com.elephant.common.persistence.domain.ApiPermission;

@ApplicationScoped
public class AccountRepositorySqlImpl extends BasicRepository implements AccountRepository{

	@Inject
	private EntityManager em;
	
	@Override
	public Optional<Account> findByPublicKey(String publicKey) {
		String query = "select a from Account a where a.publicKey = :publicKey";
		TypedQuery<Account> tq = em.createQuery(query,Account.class);
		tq.setParameter("publicKey", publicKey);
		return getSingleResult(tq);
	}

	@Override
	public Optional<ApiPermission> findByAccountAndCode(Account account, String code) {
		String query = "select p from ApiPermission p where p.apiId.accountUid = :accountUid and p.code = :code";
		TypedQuery<ApiPermission> tq = em.createQuery(query,ApiPermission.class);
		tq.setParameter("accountUid", account.getUid());
		tq.setParameter("code", code);
		return getSingleResult(tq);
	}
	
}
