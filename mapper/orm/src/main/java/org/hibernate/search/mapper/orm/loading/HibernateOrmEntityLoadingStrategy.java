/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.mapper.orm.loading;

import org.hibernate.search.mapper.orm.massindexing.impl.MassIndexingTypeLoadingStrategy;
import org.hibernate.search.mapper.orm.search.loading.impl.SearchEntityLoadingStrategy;

/**
 * A mixed Hibernate ORM entity loading strategy as {@link SearchEntityLoadingStrategy} with {@link MassIndexingTypeLoadingStrategy}.
 *
 * @param <E> The resulting entity type (output)
 * @param <I> The expected reference type (identifier)
 */
public interface HibernateOrmEntityLoadingStrategy<E, I>
		extends SearchEntityLoadingStrategy, MassIndexingTypeLoadingStrategy<E, I> {

}
