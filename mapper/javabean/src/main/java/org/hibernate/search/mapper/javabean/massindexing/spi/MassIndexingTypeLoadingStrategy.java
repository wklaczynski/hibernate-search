/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.mapper.javabean.massindexing.spi;

import java.util.Set;
import org.hibernate.search.mapper.javabean.loading.spi.JavaBeanEntityLoadingStrategy;

/**
 * A mass indexing entity loading strategy. See {@link JavaBeanEntityLoadingStrategy}.
 *
 * @param <E> The resulting entity type (output)
 * @param <I> The expected reference type (identifier)
 */
public interface MassIndexingTypeLoadingStrategy<E, I> {

	default MassIndexingTypeJoinMode calculateIndexingTypeGroupJoinMode(MassIndexingIndexedTypeGroup<?, ?> current, MassIndexingIndexedTypeGroup<?, ?> other) {
		return MassIndexingTypeJoinMode.NONE;
	}

	MassIndexingTypeLoader<E, I> createLoader(
			Set<MassIndexingIndexedTypeContext<E>> targetEntityTypeContexts);

}
