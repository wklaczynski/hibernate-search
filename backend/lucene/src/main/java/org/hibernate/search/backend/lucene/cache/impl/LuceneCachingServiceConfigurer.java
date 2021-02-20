/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.backend.lucene.cache.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.hibernate.search.backend.lucene.cache.QueryCachingConfigurationContext;
import org.hibernate.search.backend.lucene.cache.QueryCachingConfigurer;
import org.hibernate.search.engine.backend.spi.BackendBuildContext;
import org.hibernate.search.engine.environment.classpath.spi.ServiceResolver;

public class LuceneCachingServiceConfigurer {

	private LuceneCachingServiceConfigurer() {
	}

	private static synchronized Collection<QueryCachingConfigurer> getQueryCasheConfigurers(BackendBuildContext context) {
		List<QueryCachingConfigurer> queryProviders = new ArrayList<>();
		ServiceResolver serviceResolver = context.serviceResolver();
		Iterable<QueryCachingConfigurer> iterator = serviceResolver.loadJavaServices( QueryCachingConfigurer.class );
		for ( QueryCachingConfigurer provider : iterator ) {
			queryProviders.add( provider );
		}
		return queryProviders;
	}

	public static void configure(BackendBuildContext buildContext, QueryCachingConfigurationContext configurationContext) {
		Collection<QueryCachingConfigurer> providers = getQueryCasheConfigurers( buildContext );
		for ( QueryCachingConfigurer provider : providers ) {
			provider.configure( configurationContext );
		}
	}
}

