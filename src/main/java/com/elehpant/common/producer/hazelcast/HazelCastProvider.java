package com.elehpant.common.producer.hazelcast;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.hazelcast.core.HazelcastInstance;

@ApplicationScoped
public class HazelCastProvider {

	@Produces
	@HazelCast
	public HazelcastInstance getHazelcastInstance() throws NamingException{
		Context ctx = new InitialContext();  
		HazelcastInstance instance = (HazelcastInstance) ctx.lookup("payara/Hazelcast");
		return instance;  
	}
}
