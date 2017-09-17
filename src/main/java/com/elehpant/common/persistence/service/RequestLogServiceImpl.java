package com.elehpant.common.persistence.service;

import java.time.LocalDateTime;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import com.elephant.common.persistence.domain.RequestLog;
import com.elephant.common.persistence.repository.RequestLogRepository;

@ApplicationScoped
public class RequestLogServiceImpl implements RequestLogService{

	@Inject
	private RequestLogRepository requestLogRepository;
	
	@Transactional(value = TxType.REQUIRES_NEW)
	@Override
	public void save(RequestLog log) {
		log.setEndTime(LocalDateTime.now());
		log.setCreatedBy(log.getAccountId());
		log.setCreatedDate(LocalDateTime.now());
		requestLogRepository.save(log);
	}
	
}
