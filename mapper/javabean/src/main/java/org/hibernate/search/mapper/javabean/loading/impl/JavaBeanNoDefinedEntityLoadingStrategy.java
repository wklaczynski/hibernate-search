/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.mapper.javabean.loading.impl;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Map;
import org.hibernate.search.engine.common.timing.spi.Deadline;
import org.hibernate.search.engine.search.loading.spi.EntityLoader;
import org.hibernate.search.mapper.javabean.common.EntityReference;
import org.hibernate.search.mapper.javabean.log.impl.Log;
import org.hibernate.search.mapper.javabean.search.loading.spi.MutableEntityLoadingOptions;
import org.hibernate.search.mapper.javabean.search.loading.spi.SearchLoadingIndexedTypeContext;
import org.hibernate.search.util.common.logging.impl.LoggerFactory;
import org.hibernate.search.mapper.javabean.search.loading.spi.SearchEntityLoadingStrategy;

public class JavaBeanNoDefinedEntityLoadingStrategy implements SearchEntityLoadingStrategy<Void> {

	private static final Log log = LoggerFactory.make( Log.class, MethodHandles.lookup() );

	public static SearchEntityLoadingStrategy create() {
		return new JavaBeanNoDefinedEntityLoadingStrategy();
	}

	private JavaBeanNoDefinedEntityLoadingStrategy() {
	}

	@Override
	public boolean equals(Object obj) {
		if ( obj == null || !(getClass().equals( obj.getClass() )) ) {
			return false;
		}
		JavaBeanNoDefinedEntityLoadingStrategy other = (JavaBeanNoDefinedEntityLoadingStrategy) obj;
		return true;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public EntityLoader<EntityReference, Void> createLoader(SearchLoadingIndexedTypeContext targetEntityTypeContext, MutableEntityLoadingOptions loadingOptions) {
		return new FailLoadEntityLoader();
	}

	@Override
	public EntityLoader<EntityReference, Void> createLoader(List<SearchLoadingIndexedTypeContext> targetEntityTypeContexts, MutableEntityLoadingOptions loadingOptions) {
		return new FailLoadEntityLoader();
	}

	private class FailLoadEntityLoader implements EntityLoader<EntityReference, Void> {

		@Override
		public void loadBlocking(List<EntityReference> references, Map<EntityReference, Void> entitymap, Deadline deadline) {
			for ( EntityReference reference : references ) {
				throw log.cannotLoadEntity( reference );
			}
		}
	}
}
