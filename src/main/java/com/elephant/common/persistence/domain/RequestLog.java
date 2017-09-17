package com.elephant.common.persistence.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.elephant.common.domain.BasicEntity;
import com.elephant.common.persistence.convertor.LocalDateTimePersistenceConverter;

@Entity
@Table(name = "gw_request_log")
public class RequestLog extends BasicEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4324206017474497311L;
	
	@Column(name = "account_uid")
	private Long accountUid;
	
	@Column(name = "account_id")
	private String accountId;
	
	@Column(name = "api_code")
	private String apiCode;
	
	@Column(name = "api_name")
	private String apiName;
	
	@Column(name = "api_version")
	private Integer apiVersion;
	
	@Column(name = "ip")
	private String ip;
	
	@Column(name = "uri")
	private String uri;
	
	@Column(name = "body")
	private String body;
	
	@Column(name = "request_id")
	private String requestId;
	
	@Column(name = "response_code")
	private Integer responseCode;
	
	@Column(name = "response_msg")
	private String responseMsg;
	
	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name = "start_ts")
	private LocalDateTime startTime;
	
	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name = "end_ts")
	private LocalDateTime endTime;
	
	@Column(name = "remark")
	private String remark;

	public Long getAccountUid() {
		return accountUid;
	}

	public void setAccountUid(Long accountUid) {
		this.accountUid = accountUid;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getApiCode() {
		return apiCode;
	}

	public void setApiCode(String apiCode) {
		this.apiCode = apiCode;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public Integer getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(Integer responseCode) {
		this.responseCode = responseCode;
	}

	public String getResponseMsg() {
		return responseMsg;
	}

	public void setResponseMsg(String responseMsg) {
		this.responseMsg = responseMsg;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getApiName() {
		return apiName;
	}

	public void setApiName(String apiName) {
		this.apiName = apiName;
	}

	public Integer getApiVersion() {
		return apiVersion;
	}

	public void setApiVersion(Integer apiVersion) {
		this.apiVersion = apiVersion;
	}

	
	
	
}
