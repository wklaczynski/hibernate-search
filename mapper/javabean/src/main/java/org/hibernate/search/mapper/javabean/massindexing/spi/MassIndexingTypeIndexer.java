/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.mapper.javabean.massindexing.spi;

import java.util.function.Consumer;
import org.hibernate.search.mapper.javabean.massindexing.spi.dsl.MassIndexingTypeBatchingStep;

/**
 * The DSL entry point passed to consumers in
 * {@link org.hibernate.search.engine.search.query.dsl.SearchQueryOptionsStep#loading(Consumer)},
 * allowing the definition of loading options (fetch size, cache lookups, ...).
 *
 * @param <E> The resulting entity type (output)
 * @param <I> The expected reference type (identifier)
 */
public interface MassIndexingTypeIndexer<E, I> {

	/**
	 * Get the cache mode for this indexing query,
	 * i.e. the amount of entities to load for each query to the database.
	 * <p>
	 * Higher numbers mean fewer queries, but larger result sets.
	 *
	 * @return cashe mode.
	 */
	String tenantId();

	/**
	 * Get the cache mode for this indexing query,
	 * i.e. the amount of entities to load for each query to the database.
	 * <p>
	 * Higher numbers mean fewer queries, but larger result sets.
	 *
	 * @return cashe mode.
	 */
	int batchSize();

	/**
	 * Get the cache mode for this indexing query,
	 * i.e. the amount of entities to load for each query to the database.
	 * <p>
	 * Higher numbers mean fewer queries, but larger result sets.
	 *
	 * @return cashe mode.
	 */
	long objectsLimit();

	/**
	 * Get the fetch size for this indexing query,
	 * i.e. the amount of entities to load for each query to the database.
	 * <p>
	 * Higher numbers mean fewer queries, but larger result sets.
	 *
	 * @return fetch size.
	 */
	int fetchSize();

	/**
	 * Get the cache mode for this indexing query,
	 * i.e. the amount of entities to load for each query to the database.
	 * <p>
	 * Higher numbers mean fewer queries, but larger result sets.
	 *
	 * @return cashe mode.
	 */
	MassIndexingMappingContext mappingContext();

	/**
	 * Set the option for inxexing process.
	 *
	 * @param value The param value.
	 * @return {@code this} for method chaining.
	 */
	MassIndexingTypeIndexer<E, I> totalCount(long value);

	/**
	 * Get the cache mode for this indexing query,
	 * i.e.the amount of entities to load for each query to the database.<p>
	 * Higher numbers mean fewer queries, but larger result sets.
	 *
	 * @param consumer loader consumer function {@link MassIndexingTypeBatchLoader} for consume indexed identifier
	 * @return {@link MassIndexingTypeBatchLoader}.
	 */
	MassIndexingTypeBatchingStep<E, I> batching(MassIndexingTypeBatchLoader<E, I> consumer);

}
