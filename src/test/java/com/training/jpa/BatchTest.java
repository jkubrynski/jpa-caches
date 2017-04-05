package com.training.jpa;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.google.common.base.Stopwatch;
import com.training.jpa.config.DatabaseConfig;
import com.training.jpa.model.Plane;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration(classes = DatabaseConfig.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class BatchTest extends AbstractTest {

	AtomicLong counter = new AtomicLong(10L);

	@Test
	public void shouldDo() throws Exception {

		// warmup
		doInTransaction(() -> {
			for (int i = 0; i < 40_000; i++) {
				em.persist(new Plane(counter.getAndIncrement(), "test"));
			}
		});

		System.gc();

		Stopwatch stopwatch = Stopwatch.createStarted();

		// with em.clear()
		doInTransaction(() -> {
			for (int i = 0; i < 30_000; i++) {
				em.persist(new Plane(counter.getAndIncrement(), "test"));
				if (i % 100 == 0) {
					em.flush();
					em.clear();
				}
			}
		});
		long withClear = stopwatch.elapsed(TimeUnit.MILLISECONDS);

		stopwatch.reset();
		stopwatch.start();

		// without em.clear()
		doInTransaction(() -> {
			for (int i = 0; i < 30_000; i++) {
				em.persist(new Plane(counter.getAndIncrement(), "test"));
				if (i % 100 == 0) {
					em.flush();
				}
			}
		});
		long withoutClear = stopwatch.elapsed(TimeUnit.MILLISECONDS);

		System.out.format("Processing with clear() took %d while without clear() took %d", withClear, withoutClear);

		assertThat(withClear).isLessThan(withoutClear/3);
	}

}
