package org.hibernate.test.id.uuid;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.dialect.SQLServerDialect;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;

import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.junit.Test;

import static org.hibernate.testing.transaction.TransactionUtil.doInJPA;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Vlad Mihalcea
 */
@RequiresDialect( SQLServerDialect.class )
@TestForIssue( jiraKey = "HHH-12943" )
public class UUID2GeneratorUniqueIdentifierIdTest extends BaseEntityManagerFunctionalTestCase {
	@Override
	protected Class<?>[] getAnnotatedClasses() {
		return new Class<?>[] { FooEntity.class };
	}

	@Test
	public void testPaginationQuery() {
		UUID id = doInJPA( this::entityManagerFactory, entityManager -> {
			FooEntity entity = new FooEntity();
			entity.getFooValues().add("one");
			entity.getFooValues().add("two");
			entity.getFooValues().add("three");

			entityManager.persist(entity);

			return entity.getId();
		} );

		assertNotNull(id);

		doInJPA( this::entityManagerFactory, entityManager -> {
			FooEntity entity = entityManager.find(FooEntity.class, id);
			assertNotNull(entity);

			assertTrue(entity.getFooValues().size() == 3);
		} );
	}

	@Entity
	@Table(name = "foo")
	public static class FooEntity {

		@Id
		@GenericGenerator(name = "uuid", strategy = "uuid2")
		@GeneratedValue(generator = "uuid")
		@Column(columnDefinition = "UNIQUEIDENTIFIER")
		private UUID id;

		@ElementCollection
		@JoinTable(name = "foo_values")
		@Column(name = "foo_value")
		private final Set<String> fooValues = new HashSet<>();

		public UUID getId() {
			return id;
		}

		public void setId(UUID id) {
			this.id = id;
		}

		public Set<String> getFooValues() {
			return fooValues;
		}

	}
}