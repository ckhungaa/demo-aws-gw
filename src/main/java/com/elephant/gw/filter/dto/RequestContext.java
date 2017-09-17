package com.elephant.gw.filter.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;

import com.elephant.common.persistence.domain.Account;
import com.elephant.common.persistence.domain.ApiPermission;
import com.elephant.common.persistence.domain.RequestLog;
import com.elephant.gw.filter.api.ApiCode;

@RequestScoped
public class RequestContext {

	private String requestId;
	private ApiCode invokedApi;
	private Account currentAccount;
	private ApiPermission currentApiPermission;
	private List<String> userRequestSites;
	
	private RequestLog requestLog = new RequestLog();
	
	public RequestContext(){
		requestLog.setStartTime(LocalDateTime.now());
	}
	
	public ApiCode getInvokedApi() {
		return invokedApi;
	}

	public void setInvokedApi(ApiCode invokedApi,ApiPermission apiPermission) {
		this.invokedApi = invokedApi;
		this.requestLog.setApiCode(apiPermission.getCode());
		this.requestLog.setApiName(apiPermission.getName());
		this.requestLog.setApiVersion(apiPermission.getApiVersion());
	}

	public String getRequestId() {
		return requestId;
	}
	
	public void setRequestId(String requestId){
		this.requestId = requestId;
		this.requestLog.setRequestId(requestId);

	}

	public Optional<Account> getCurrentAccount() {
		return Optional.ofNullable(currentAccount);
	}

	public void setCurrentAccount(Account currentAccount) {
		this.currentAccount = currentAccount;
		this.requestLog.setAccountId(currentAccount.getAccountId());
		this.requestLog.setAccountUid(currentAccount.getUid());
	}

	public RequestLog getRequestLog() {
		return requestLog;
	}

	public ApiPermission getCurrentApiPermission() {
		return currentApiPermission;
	}

	public void setCurrentApiPermission(ApiPermission currentApiPermission) {
		this.currentApiPermission = currentApiPermission;
	}

	public List<String> getUserRequestSites() {
		return userRequestSites;
	}

	public void setUserRequestSites(List<String> userRequestSites) {
		this.userRequestSites = userRequestSites;
	}
	
}
