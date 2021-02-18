/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.mapper.orm.loading.impl;

import org.hibernate.search.engine.cfg.spi.ConfigurationPropertySource;
import org.hibernate.search.mapper.orm.loading.HibernateOrmEntityLoadingFactory;
import org.hibernate.search.mapper.orm.loading.HibernateOrmEntityLoadingStrategyContext;
import org.hibernate.search.mapper.orm.search.loading.impl.SearchEntityLoadingStrategy;

/**
 *
 * @author Waldemar Kłaczyński
 */
public class HibernateOrmDefaultEntityLoaderFactory implements HibernateOrmEntityLoadingFactory {

	@Override
	public void configure(ConfigurationPropertySource propertySource) {

	}

	@Override
	public SearchEntityLoadingStrategy loadStrategy(HibernateOrmEntityLoadingStrategyContext ctx) {
		SearchEntityLoadingStrategy loadingStrategy;
		if ( ctx.documentIdSourceProperty().name().equals( ctx.entityPersister().getIdentifierPropertyName() ) ) {
			loadingStrategy = HibernateOrmEntityIdEntityLoadingStrategy.create( ctx.sessionFactory(), ctx.entityPersister() );
		}
		else {
			loadingStrategy = HibernateOrmNonEntityIdPropertyEntityLoadingStrategy.create( ctx.sessionFactory(), ctx.entityPersister(),
				ctx.documentIdSourceProperty().name(), ctx.documentIdSourceProperty().handle() );
		}
		return loadingStrategy;
	}

}
