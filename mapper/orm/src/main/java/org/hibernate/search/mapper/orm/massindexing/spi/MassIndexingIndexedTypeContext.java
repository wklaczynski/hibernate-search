/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.mapper.orm.massindexing.spi;

import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.search.engine.search.loading.spi.EntityLoadingStrategy;
import org.hibernate.search.mapper.pojo.model.spi.PojoRawTypeIdentifier;

public interface MassIndexingIndexedTypeContext<E> {

	PojoRawTypeIdentifier<E> typeIdentifier();

	/**
	 * @return The name of the entity in the JPA metamodel.
	 */
	String jpaEntityName();

	/**
	 * @return The Hibernate ORM entity persister.
	 */
	EntityPersister entityPersister();

	/**
	 * @return A strategy for loading entities of this type.
	 */
	EntityLoadingStrategy loadingStrategy();

}
