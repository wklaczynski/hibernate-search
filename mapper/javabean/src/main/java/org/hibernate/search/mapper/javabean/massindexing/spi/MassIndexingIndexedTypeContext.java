/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.mapper.javabean.massindexing.spi;

import org.hibernate.search.engine.search.loading.spi.EntityLoadingStrategy;
import org.hibernate.search.mapper.pojo.model.spi.PojoRawTypeIdentifier;

/**
 * Contextual information about a indexed type to entity loading strategy or index a entities during mass indexing.
 *
 * @param <E> The resulting entity type (output)
 */
public interface MassIndexingIndexedTypeContext<E> {

	/**
	 * @return The {@link PojoRawTypeIdentifier} of the entity in the entity metamodel.
	 */
	PojoRawTypeIdentifier<E> typeIdentifier();

	/**
	 * @return The name of the entity in the entity metamodel.
	 */
	String entityName();

	/**
	 * @return A strategy for loading entities of this type.
	 */
	EntityLoadingStrategy loadingStrategy();

}
