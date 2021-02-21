/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.mapper.javabean.loading.impl;

import org.hibernate.search.engine.cfg.spi.ConfigurationPropertySource;
import org.hibernate.search.mapper.javabean.loading.spi.JavaBeanEntityLoadingFactory;
import org.hibernate.search.mapper.javabean.loading.JavaBeanEntityLoadingStrategyContext;
import org.hibernate.search.mapper.javabean.search.loading.spi.SearchEntityLoadingStrategy;

public class JavaBeanDefaultEntityLoaderFactory implements JavaBeanEntityLoadingFactory {

	@Override
	public void configure(ConfigurationPropertySource propertySource) {

	}

	@Override
	public SearchEntityLoadingStrategy loadStrategy(JavaBeanEntityLoadingStrategyContext context) {
		return JavaBeanNoDefinedEntityLoadingStrategy.create();
	}

}
