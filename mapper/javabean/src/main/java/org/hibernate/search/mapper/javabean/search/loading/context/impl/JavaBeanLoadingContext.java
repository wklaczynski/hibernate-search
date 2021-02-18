/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.mapper.javabean.search.loading.context.impl;

import java.lang.invoke.MethodHandles;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.hibernate.search.engine.search.loading.context.spi.LoadingContext;
import org.hibernate.search.engine.search.loading.context.spi.LoadingContextBuilder;
import org.hibernate.search.engine.search.loading.spi.ProjectionHitMapper;
import org.hibernate.search.engine.backend.common.spi.DocumentReferenceConverter;
import org.hibernate.search.engine.search.loading.spi.EntityLoader;
import org.hibernate.search.mapper.javabean.log.impl.Log;
import org.hibernate.search.mapper.javabean.search.loading.impl.JavaBeanProjectionHitMapper;
import org.hibernate.search.mapper.javabean.common.EntityReference;
import org.hibernate.search.mapper.javabean.scope.impl.JavaBeanScopeIndexedTypeContext;
import org.hibernate.search.mapper.javabean.search.loading.dsl.SearchLoadingOptionsStep;
import org.hibernate.search.mapper.javabean.search.loading.spi.MutableEntityLoadingOptions;
import org.hibernate.search.mapper.javabean.search.loading.impl.SearchEntityLoaderBuilder;
import org.hibernate.search.mapper.javabean.search.loading.impl.SearchLoadingMappingContext;
import org.hibernate.search.mapper.javabean.search.loading.impl.SearchLoadingSessionContext;
import org.hibernate.search.util.common.logging.impl.LoggerFactory;

public final class JavaBeanLoadingContext<E> implements LoadingContext<EntityReference, E> {

	private static final Log log = LoggerFactory.make( Log.class, MethodHandles.lookup() );

	private final DocumentReferenceConverter<EntityReference> documentReferenceConverter;
	private final EntityLoader<EntityReference, ? extends E> entityLoader;
	private final MutableEntityLoadingOptions loadingOptions;

	private JavaBeanLoadingContext(DocumentReferenceConverter<EntityReference> documentReferenceConverter,
			EntityLoader<EntityReference, ? extends E> entityLoader,
			MutableEntityLoadingOptions loadingOptions) {
		this.documentReferenceConverter = documentReferenceConverter;
		this.entityLoader = entityLoader;
		this.loadingOptions = loadingOptions;
	}

	@Override
	public ProjectionHitMapper<EntityReference, E> createProjectionHitMapper() {
		return new JavaBeanProjectionHitMapper( documentReferenceConverter, entityLoader );
	}

	public static final class Builder<E>
			implements LoadingContextBuilder<EntityReference, E, SearchLoadingOptionsStep>, SearchLoadingOptionsStep<E> {
		private final SearchLoadingSessionContext sessionContext;
		private final SearchEntityLoaderBuilder<E> entityLoaderBuilder;
		private final MutableEntityLoadingOptions loadingOptions;
		private final Map<String, Object> options = new LinkedHashMap<>();
		private EntityLoader<EntityReference, ? extends E> entityLoader;

		public Builder(SearchLoadingMappingContext mappingContext,
				SearchLoadingSessionContext sessionContext,
				Set<JavaBeanScopeIndexedTypeContext<? extends E>> indexedTypeContexts) {
			this.sessionContext = sessionContext;
			this.entityLoaderBuilder = new SearchEntityLoaderBuilder<>( mappingContext, sessionContext, indexedTypeContexts );
			this.loadingOptions = new MutableEntityLoadingOptions( mappingContext, options );
		}

		@Override
		public SearchLoadingOptionsStep<E> toAPI() {
			return this;
		}

		@Override
		public SearchLoadingOptionsStep<E> fetchSize(int fetchSize) {
			loadingOptions.fetchSize( fetchSize );
			return this;
		}

		@Override
		public SearchLoadingOptionsStep<E> option(String name, Object value) {
			options.put( name, value );
			return this;
		}

		@Override
		public SearchLoadingOptionsStep<E> loader(EntityLoader<EntityReference, ? extends E> loader) {
			this.entityLoader = loader;
			return this;
		}

		@Override
		public LoadingContext<EntityReference, E> build() {
			DocumentReferenceConverter<EntityReference> referenceHitMapper = sessionContext.referenceHitMapper();
			if ( entityLoader == null ) {
				entityLoader = entityLoaderBuilder.build( loadingOptions );
			}
			return new JavaBeanLoadingContext<>(
					referenceHitMapper, entityLoader,
					loadingOptions
			);
		}
	}
}
