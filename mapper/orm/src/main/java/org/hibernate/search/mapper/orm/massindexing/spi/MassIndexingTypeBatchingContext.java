/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.mapper.orm.massindexing.spi;

import org.hibernate.CacheMode;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.search.util.common.annotation.Incubating;

/**
 * Contextual information about a indexed type to load or batching index a entities during mass indexing.
 *
 * @param <E> The resulting entity type (output)
 * @param <I> The expected reference type (identifier)
 */
@Incubating
public interface MassIndexingTypeBatchingContext<E, I> {

	/**
	 * Get the cache mode for this indexing query,
	 * i.e. the amount of entities to load for each query to the database.
	 * <p>
	 * Higher numbers mean fewer queries, but larger result sets.
	 *
	 * @return cashe mode.
	 */
	CacheMode cacheMode();

	SessionImplementor session();

}
