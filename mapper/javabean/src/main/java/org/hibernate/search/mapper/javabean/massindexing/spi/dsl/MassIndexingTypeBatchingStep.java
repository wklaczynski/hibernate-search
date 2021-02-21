/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.mapper.javabean.massindexing.spi.dsl;

import java.util.List;
import org.hibernate.search.mapper.javabean.massindexing.spi.MassIndexingTypeBatchLoader;
import org.hibernate.search.mapper.javabean.massindexing.spi.MassIndexingTypeIndexer;

/**
 * The DSL entry point passed to consumers in
 * {@link MassIndexingTypeIndexer#batching(MassIndexingTypeBatchLoader)},
 * allowing the definition of batching options (fetch size, cache lookups, ...).
 *
 * @param <E> The resulting entity type (output)
 * @param <I> The expected reference type (identifier)
 */
public interface MassIndexingTypeBatchingStep<E, I> {

	MassIndexingTypeBatchingStep<E, I> load(List<I> ids);

}
