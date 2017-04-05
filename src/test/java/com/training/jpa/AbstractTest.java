package com.training.jpa;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Before;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * @author Jakub Kubrynski
 */
abstract class AbstractTest {

	@PersistenceContext
	EntityManager em;

	@Autowired
	PlatformTransactionManager platformTransactionManager;

	TransactionTemplate transactionTemplate;

	@Before
	public void setUp() throws Exception {
		transactionTemplate = new TransactionTemplate(platformTransactionManager);
		transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
	}

	void doInTransaction(final Runnable runnable) {
		transactionTemplate.execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(final TransactionStatus transactionStatus) {
				runnable.run();
			}
		});
	}
}
