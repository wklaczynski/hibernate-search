/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.mapper.javabean.massindexing.spi.dsl;

import java.util.function.Consumer;
import org.hibernate.search.mapper.javabean.massindexing.spi.MassIndexingMappingContext;
import org.hibernate.search.mapper.javabean.massindexing.spi.MassIndexingTypeBatchLoader;

/**
 * The DSL entry point passed to consumers in
 * {@link org.hibernate.search.engine.search.query.dsl.SearchQueryOptionsStep#loading(Consumer)},
 * allowing the definition of loading options (fetch size, cache lookups, ...).
 * @param <E>
 * @param <I>
 */
public interface MassIndexingTypeIndexer<E, I> {

	String tenantId();

	int batchSize();

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

	MassIndexingMappingContext mappingContext();

	/**
	 * Set the option for inxexing process.
	 *
	 * @param value The param value.
	 * @return {@code this} for method chaining.
	 */
	MassIndexingTypeIndexer<E, I> totalCount(long value);

	MassIndexingTypeBatchingStep<E, I> batching(MassIndexingTypeBatchLoader<E, I> consumer);

}
