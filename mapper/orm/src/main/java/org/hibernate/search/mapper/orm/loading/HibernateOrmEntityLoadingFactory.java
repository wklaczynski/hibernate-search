/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.mapper.orm.loading;

import org.hibernate.search.engine.search.loading.spi.EntityLoadingFactory;
import org.hibernate.search.mapper.orm.search.loading.impl.SearchEntityLoadingStrategy;

/**
 * A Hibernate ORM loading factory as {@link EntityLoadingFactory}.
 */
public interface HibernateOrmEntityLoadingFactory extends EntityLoadingFactory<HibernateOrmEntityLoadingStrategyContext, SearchEntityLoadingStrategy> {

}
