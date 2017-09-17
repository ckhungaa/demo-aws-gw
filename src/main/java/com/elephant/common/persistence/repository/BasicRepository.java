package com.elephant.common.persistence.repository;

import java.util.Optional;

import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BasicRepository {
	
	private static final Logger logger = LoggerFactory.getLogger(BasicRepository.class);
	
	/**
	 * get single result without exception throw
	 * this will print the error message and return empty optional object
	 * @param typeQuery
	 * @return
	 */
	protected <T> Optional<T> getSingleResult(TypedQuery<T> typeQuery){
		T result = null;
		try{
			result = typeQuery.getSingleResult();
		}catch(Exception e){
			logger.error("Excepted single result, but found error");
		}
		
		return Optional.ofNullable(result);
	}
	
}
