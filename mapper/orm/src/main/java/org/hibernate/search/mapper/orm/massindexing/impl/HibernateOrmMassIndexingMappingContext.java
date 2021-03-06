/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.mapper.orm.massindexing.impl;

import javax.persistence.EntityManager;

import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.search.engine.backend.common.spi.EntityReferenceFactory;
import org.hibernate.search.engine.reporting.FailureHandler;
import org.hibernate.search.mapper.orm.common.EntityReference;
import org.hibernate.search.mapper.orm.scope.impl.HibernateOrmScopeSessionContext;
import org.hibernate.search.engine.environment.thread.spi.ThreadPoolProvider;

public interface HibernateOrmMassIndexingMappingContext {

	SessionFactoryImplementor sessionFactory();

	ThreadPoolProvider threadPoolProvider();

	FailureHandler failureHandler();

	EntityReferenceFactory<EntityReference> entityReferenceFactory();

	HibernateOrmScopeSessionContext sessionContext(EntityManager entityManager);

}
