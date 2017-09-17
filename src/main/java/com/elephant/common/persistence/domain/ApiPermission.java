package com.elephant.common.persistence.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.elephant.common.persistence.convertor.LocalDateTimePersistenceConverter;
import com.google.common.base.MoreObjects;

@Entity 
@Table(name = "account_permitted_api_view")
public class ApiPermission {

	@EmbeddedId
	private ApiPermissionId apiId;
	
	@Column(name = "code")
	private String code;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "api_version")
	private Integer apiVersion;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name = "start_date")
	private LocalDateTime startDate;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name = "end_date")
	private LocalDateTime endDate;
	
	@Column(name = "enable")
	private Boolean isEnable;
	
	@Column(name = "rate_limit")
	private Double rateLimit;
	
	public boolean isEnabled(){
		return MoreObjects.firstNonNull(isEnable, false);
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

	public Double getRateLimit() {
		return rateLimit;
	}

	public String getCode(){
		return code;
	}

	public String getName() {
		return name;
	}

	public Integer getApiVersion() {
		return apiVersion;
	}
	
	
	
}
