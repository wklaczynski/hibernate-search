/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.mapper.orm.massindexing.spi;

import java.util.List;
import org.hibernate.search.util.common.annotation.Incubating;

/**
 * Represents an operation that accepts a single input argument and returns no
 * result.Unlike most other functional interfaces, {@code MassIndexingTypeBatchLoader} is expected
 * to operate via side-effects.<p>
 * This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #load(MassIndexingTypeBatchingContext, List)}.
 *
 * @param <E> The resulting entity type (output)
 * @param <I> The expected reference type (identifier)
 */
@Incubating
public interface MassIndexingTypeBatchLoader<E, I> {

	/**
	 * Performs this operation on the given argument.
	 *
	 * @param ctx contextual information about a indexed type to load
	 * @param ids list of identifiers to load
	 * @return lis of loaded entities by type
	 */
	List<? super E> load(MassIndexingTypeBatchingContext<E, I> ctx, List<I> ids);

}
