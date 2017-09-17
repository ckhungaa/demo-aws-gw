package com.elephant.common.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

import com.elephant.common.persistence.convertor.LocalDateTimePersistenceConverter;

@MappedSuperclass
public abstract class Modifiable extends BasicEntity{

	private static final long serialVersionUID = 7055071078408033652L;
	
	@Column(name = "modified_by")
	private String modifiedBy;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name = "modified_date")
	private LocalDateTime modifiedDate;
	
	@Version
	@Column(name = "version")
	private Long version;

	public String getModifiedBy() {
		return modifiedBy;
	}

	public LocalDateTime getModifiedDate() {
		return modifiedDate;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	
}
