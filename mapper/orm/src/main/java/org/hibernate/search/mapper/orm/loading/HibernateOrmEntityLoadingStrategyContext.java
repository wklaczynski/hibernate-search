/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.mapper.orm.loading;

import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.search.engine.search.loading.spi.EntityLoadingStrategyContext;
import org.hibernate.search.mapper.orm.entity.SearchIndexedEntity;
import org.hibernate.search.mapper.pojo.model.spi.PojoPropertyModel;

public class HibernateOrmEntityLoadingStrategyContext implements EntityLoadingStrategyContext {
	private final SessionFactoryImplementor sessionFactory;

	private final SearchIndexedEntity indexedEntity;
	private final EntityPersister entityPersister;
	private final PojoPropertyModel<?> documentIdSourceProperty;

	public HibernateOrmEntityLoadingStrategyContext(SessionFactoryImplementor sessionFactory,
		SearchIndexedEntity indexedEntity,
		EntityPersister entityPersister,
		PojoPropertyModel<?> documentIdSourceProperty) {
		this.sessionFactory = sessionFactory;
		this.indexedEntity = indexedEntity;
		this.entityPersister = entityPersister;
		this.documentIdSourceProperty = documentIdSourceProperty;
	}

	public SessionFactoryImplementor sessionFactory() {
		return sessionFactory;
	}

	public SearchIndexedEntity indexedEntity() {
		return indexedEntity;
	}

	public EntityPersister entityPersister() {
		return entityPersister;
	}

	public PojoPropertyModel<?> documentIdSourceProperty() {
		return documentIdSourceProperty;
	}

}
