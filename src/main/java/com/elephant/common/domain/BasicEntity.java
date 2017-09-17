package com.elephant.common.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import com.elephant.common.persistence.convertor.LocalDateTimePersistenceConverter;

@MappedSuperclass
public abstract class BasicEntity implements Serializable {

	private static final long serialVersionUID = 7055071078408033652L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "uid")
	protected Long uid;
	
	@Column(name = "created_by")
	protected String createdBy;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name = "created_date")
	protected LocalDateTime createdDate;
	
	public Long getUid() {
		return uid;
	}
	
	public String getCreatedBy() {
		return createdBy;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setUid(Long uid) {
		this.uid = uid;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	
	
	
}
