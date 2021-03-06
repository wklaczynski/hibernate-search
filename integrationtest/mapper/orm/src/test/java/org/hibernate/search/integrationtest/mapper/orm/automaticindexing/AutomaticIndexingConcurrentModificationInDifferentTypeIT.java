/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.integrationtest.mapper.orm.automaticindexing;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.hibernate.SessionFactory;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.util.impl.integrationtest.common.rule.BackendMock;
import org.hibernate.search.util.impl.integrationtest.mapper.orm.OrmSetupHelper;
import org.hibernate.search.util.impl.integrationtest.mapper.orm.OrmUtils;
import org.hibernate.search.util.impl.test.annotation.TestForIssue;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class AutomaticIndexingConcurrentModificationInDifferentTypeIT {
	@Rule
	public BackendMock backendMock = new BackendMock();

	@Rule
	public OrmSetupHelper ormSetupHelper = OrmSetupHelper.withBackendMock( backendMock );

	private SessionFactory sessionFactory;

	@Before
	public void setup() {
		backendMock.expectSchema( ParentEntity.NAME, b -> b
				.field( "name", String.class )
				.objectField( "child", b2 -> b2
						.field( "name", String.class )
				)
		);
		backendMock.expectSchema( ChildEntity.NAME, b -> b
				.field( "name", String.class )
		);
		backendMock.expectSchema( OtherEntity.NAME, b -> b
				.field( "name", String.class )
		);

		sessionFactory = ormSetupHelper.start()
				.setup( ParentEntity.class, ChildEntity.class, OtherEntity.class );

		backendMock.verifyExpectationsMet();

		// Data init
		OrmUtils.withinTransaction( sessionFactory, session -> {
			ChildEntity entity1 = new ChildEntity();
			entity1.setId( 1 );
			entity1.setName( "edouard" );

			ParentEntity entity2 = new ParentEntity();
			entity2.setId( 2 );
			entity2.setName( "yann" );

			entity2.setChild( entity1 );
			entity1.setParent( entity2 );

			OtherEntity entity3 = new OtherEntity();
			entity3.setId( 3 );
			entity3.setName( "king" );

			session.persist( entity1 );
			session.persist( entity2 );
			session.persist( entity3 );

			backendMock.expectWorks( ParentEntity.NAME )
					.add( String.valueOf( 2 ), b -> b
							.field( "name", "yann" )
							.objectField( "child", b2 -> b2
									.field( "name", "edouard" )
							) )
					.createdThenExecuted();
			backendMock.expectWorks( ChildEntity.NAME )
					.add( String.valueOf( 1 ), b -> b
							.field( "name", "edouard" ) )
					.createdThenExecuted();
			backendMock.expectWorks( OtherEntity.NAME )
					.add( String.valueOf( 3 ), b -> b
							.field( "name", "king" ) )
					.createdThenExecuted();
		} );
		backendMock.verifyExpectationsMet();
	}

	@Test
	@TestForIssue(jiraKey = "HSEARCH-3857")
	public void updateTriggeringReindexingOfPreviouslyUnknownEntityType() {
		OrmUtils.withinTransaction( sessionFactory, session -> {
			ChildEntity entity1 = session.load( ChildEntity.class, 1 );
			entity1.setName( "updated" );
			// Add another type to the indexing plan so that we're not done iterating over all types
			// when ParentEntity is added to the indexing plan due to the change in the child.
			OtherEntity entity3 = session.load( OtherEntity.class, 3 );
			entity3.setName( "updated" );

			backendMock.expectWorks( ParentEntity.NAME )
					.addOrUpdate( String.valueOf( 2 ), b -> b
							.field( "name", "yann" )
							.objectField( "child", b2 -> b2
									.field( "name", "updated" )
							) )
					.createdThenExecuted();
			backendMock.expectWorks( ChildEntity.NAME )
					.addOrUpdate( String.valueOf( 1 ), b -> b
							.field( "name", "updated" ) )
					.createdThenExecuted();
			backendMock.expectWorks( OtherEntity.NAME )
					.addOrUpdate( String.valueOf( 3 ), b -> b
							.field( "name", "updated" ) )
					.createdThenExecuted();
		} );
		backendMock.verifyExpectationsMet();
	}

	@Entity(name = ParentEntity.NAME)
	@Indexed
	public static class ParentEntity {
		static final String NAME = "Parent";

		@Id
		private Integer id;

		@GenericField
		@Basic
		private String name;

		@IndexedEmbedded
		@OneToOne(mappedBy = "parent")
		private ChildEntity child;

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public ChildEntity getChild() {
			return child;
		}

		public void setChild(ChildEntity child) {
			this.child = child;
		}
	}

	@Entity(name = ChildEntity.NAME)
	@Indexed
	public static class ChildEntity {
		static final String NAME = "Child";

		@Id
		private Integer id;

		@GenericField
		@Basic
		private String name;

		@OneToOne
		private ParentEntity parent;

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public ParentEntity getParent() {
			return parent;
		}

		public void setParent(ParentEntity parent) {
			this.parent = parent;
		}
	}

	@Entity(name = OtherEntity.NAME)
	@Indexed
	public static class OtherEntity {
		static final String NAME = "Other";

		@Id
		private Integer id;

		@GenericField
		@Basic
		private String name;

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}
}
