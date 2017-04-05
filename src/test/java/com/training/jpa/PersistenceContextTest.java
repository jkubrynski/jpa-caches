package com.training.jpa;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;

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
public class PersistenceContextTest extends AbstractTest {

	@Test
	public void shouldSaveEntity() {
		Plane boeing = new Plane(1L, "boeing");

		doInTransaction(() -> em.persist(boeing));
		doInTransaction(() -> assertThat(em.find(Plane.class, 1L)).isNotNull());
	}

	@Test
	public void shouldNotSaveEntity() {
		Plane boeing = new Plane(2L, "boeing");

		doInTransaction(() -> {
			em.persist(boeing);
			em.clear();
		});
		doInTransaction(() -> assertThat(em.find(Plane.class, 2L)).isNull());
	}

	@Test
	public void shouldSaveEntityThanksToManualFlush() {
		Plane boeing = new Plane(3L, "boeing");

		doInTransaction(() -> {
			em.persist(boeing);
			em.flush();
			em.clear();
		});
		doInTransaction(() -> assertThat(em.find(Plane.class, 3L)).isNotNull());
	}

	@Test
	public void shouldSaveEntityThanksToAutoFlush() {
		Plane boeing = new Plane(4L, "boeing");

		doInTransaction(() -> {
			em.persist(boeing);
			em.createQuery("select p from Plane p").getResultList();
			em.clear();
		});
		doInTransaction(() -> assertThat(em.find(Plane.class, 4L)).isNotNull());
	}

	@Test
	public void shouldSaveEntityThanksToAutoFlushOnNativeQuery() {
		Plane boeing = new Plane(5L, "boeing");

		doInTransaction(() -> {
			em.persist(boeing);
			em.createNativeQuery("select * from Plane p").getResultList();
			em.clear();
		});
		doInTransaction(() -> assertThat(em.find(Plane.class, 5L)).isNotNull());
	}

	@Test
	public void shouldNotSaveEntityWithCommitFlush() {
		Plane boeing = new Plane(6L, "boeing");

		doInTransaction(() -> {
			em.persist(boeing);
			em.setFlushMode(FlushModeType.COMMIT);
			em.createQuery("select p from Plane p").getResultList();
			em.clear();
		});
		doInTransaction(() -> assertThat(em.find(Plane.class, 6L)).isNull());
	}

	@Test
	public void shouldChangeNameThanksToMerge() {
		Plane plane = new Plane(7L, "boeing");

		doInTransaction(() -> {
			em.persist(plane);
			em.flush();
			em.clear();
			plane.setName("airbus");
			em.merge(plane);
		});
		doInTransaction(() -> assertThat(em.find(Plane.class, 7L).getName()).isEqualTo("airbus"));
	}

	@Test
	public void shouldNotChangeNameDueToClear() {
		Plane plane = new Plane(8L, "boeing");

		doInTransaction(() -> {
			em.persist(plane);
			em.flush();
			em.clear();
			plane.setName("airbus");
			em.flush();
		});
		doInTransaction(() -> assertThat(em.find(Plane.class, 8L).getName()).isEqualTo("boeing"));
	}



}
