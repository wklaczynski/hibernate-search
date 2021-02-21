/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.mapper.javabean.loading.spi;

import java.util.Set;
import org.hibernate.search.mapper.javabean.massindexing.spi.MassIndexingTypeLoadingStrategy;
import org.hibernate.search.mapper.javabean.search.loading.spi.SearchEntityLoadingStrategy;
import org.hibernate.search.mapper.javabean.massindexing.spi.MassIndexingIndexedTypeContext;
import org.hibernate.search.mapper.javabean.massindexing.spi.MassIndexingTypeLoader;

/**
 * A mixed JavaBean entity loading strategy as {@link SearchEntityLoadingStrategy} with {@link MassIndexingTypeLoadingStrategy}.
 *
 * @param <E> The resulting entity type (output)
 * @param <I> The expected reference type (identifier)
 */
public interface JavaBeanEntityLoadingStrategy<E, I>
		extends SearchEntityLoadingStrategy<E>, MassIndexingTypeLoadingStrategy<E, I> {

	@Override
	default MassIndexingTypeLoader<E, I> createLoader(Set<MassIndexingIndexedTypeContext<E>> typeContexts) {
		return null;
	}

}
