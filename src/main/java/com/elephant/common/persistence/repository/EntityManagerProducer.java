package com.elephant.common.persistence.repository;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;



@ApplicationScoped
public class EntityManagerProducer {

	@PersistenceContext(unitName = "default")
	private EntityManager defaultEntityManager;
	
	@Produces
	@RequestScoped
	public EntityManager getPortalEntityManager(){
		return defaultEntityManager;
	}
}
