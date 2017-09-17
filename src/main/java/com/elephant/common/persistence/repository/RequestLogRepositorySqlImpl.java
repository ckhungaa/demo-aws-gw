package com.elephant.common.persistence.repository;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.elephant.common.persistence.domain.RequestLog;

@Stateless
public class RequestLogRepositorySqlImpl implements RequestLogRepository{

	@Inject 
	private EntityManager em;
	
	@Override
	public void save(RequestLog log) {
		em.persist(log);
	}

	
	
}
