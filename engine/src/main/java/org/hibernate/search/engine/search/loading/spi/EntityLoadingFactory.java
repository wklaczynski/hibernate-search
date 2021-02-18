/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.engine.search.loading.spi;

import org.hibernate.search.engine.cfg.spi.ConfigurationPropertySource;


public interface EntityLoadingFactory<C extends EntityLoadingStrategyContext, S extends EntityLoadingStrategy> {

	/**
	 * Configure entity loader factory after create reference.
	 *
	 * @param propertySource property source for the configure entity loader factory
	 */
	void configure(ConfigurationPropertySource propertySource);

	/**
	 * Load entity loader strategy from the factory.
	 *
	 * @param context property context for the load strategy
	 * @return loading strategy
	 */
	S loadStrategy(C context);

}
