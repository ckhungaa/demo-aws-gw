package com.elephant.common.persistence.repository;

import com.elephant.common.persistence.domain.RequestLog;

public interface RequestLogRepository {

	public void save(RequestLog log);
}
