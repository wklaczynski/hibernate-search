/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.mapper.javabean.mapping.impl;

import org.hibernate.search.engine.backend.index.IndexManager;
import org.hibernate.search.engine.mapper.mapping.spi.MappedIndexManager;
import org.hibernate.search.engine.search.loading.spi.EntityLoadingFactory;
import org.hibernate.search.engine.search.loading.spi.EntityLoadingStrategy;
import org.hibernate.search.engine.search.loading.spi.EntityLoadingStrategyContext;
import org.hibernate.search.mapper.javabean.loading.JavaBeanEntityLoadingStrategyContext;
import org.hibernate.search.mapper.javabean.loading.impl.JavaBeanNoDefinedEntityLoadingStrategy;
import org.hibernate.search.mapper.javabean.scope.impl.JavaBeanScopeIndexedTypeContext;
import org.hibernate.search.mapper.javabean.session.impl.JavaBeanSessionIndexedTypeContext;
import org.hibernate.search.mapper.pojo.bridge.runtime.spi.IdentifierMapping;
import org.hibernate.search.mapper.pojo.mapping.building.spi.PojoIndexedTypeExtendedMappingCollector;
import org.hibernate.search.mapper.pojo.model.spi.PojoPropertyModel;
import org.hibernate.search.mapper.pojo.model.spi.PojoRawTypeIdentifier;
import org.hibernate.search.mapper.javabean.search.loading.spi.SearchEntityLoadingStrategy;

class JavaBeanIndexedTypeContext<E> extends AbstractJavaBeanTypeContext<E>
		implements JavaBeanScopeIndexedTypeContext<E>, JavaBeanSessionIndexedTypeContext<E> {
	private final IdentifierMapping identifierMapping;
	private final MappedIndexManager indexManager;
	private final SearchEntityLoadingStrategy loadingStrategy;
	private final EntityLoadingFactory entityLoadingFactory;
	private final PojoPropertyModel<?> documentIdSourceProperty;

	private JavaBeanIndexedTypeContext(Builder<E> builder) {
		super( builder );
		this.identifierMapping = builder.identifierMapping;
		this.indexManager = builder.indexManager;
		this.entityLoadingFactory = builder.entityLoadingFactory;
		this.documentIdSourceProperty = builder.documentIdSourceProperty;

		if ( entityLoadingFactory != null ) {
			EntityLoadingStrategyContext context = new JavaBeanEntityLoadingStrategyContext( this, documentIdSourceProperty );
			this.loadingStrategy = (SearchEntityLoadingStrategy) entityLoadingFactory.loadStrategy( context );
		}
		else {
			this.loadingStrategy = JavaBeanNoDefinedEntityLoadingStrategy.create();
		}
	}

	@Override
	public IdentifierMapping identifierMapping() {
		return identifierMapping;
	}

	@Override
	public IndexManager indexManager() {
		return indexManager.toAPI();
	}

	@Override
	public EntityLoadingStrategy loadingStrategy() {
		return loadingStrategy;
	}

	@Override
	public String entityName() {
		return name();
	}

	static class Builder<E> extends AbstractBuilder<E> implements PojoIndexedTypeExtendedMappingCollector {
		private IdentifierMapping identifierMapping;
		private MappedIndexManager indexManager;
		private EntityLoadingFactory entityLoadingFactory;
		private PojoPropertyModel<?> documentIdSourceProperty;

		Builder(PojoRawTypeIdentifier<E> typeIdentifier, String entityName) {
			super( typeIdentifier, entityName );
		}

		@Override
		public void documentIdSourceProperty(PojoPropertyModel<?> documentIdSourceProperty) {
			this.documentIdSourceProperty = documentIdSourceProperty;
		}

		@Override
		public void identifierMapping(IdentifierMapping identifierMapping) {
			this.identifierMapping = identifierMapping;
		}

		@Override
		public void indexManager(MappedIndexManager indexManager) {
			this.indexManager = indexManager;
		}

		@Override
		public void entityLoadingFactory(EntityLoadingFactory entityLoadingFactory) {
			this.entityLoadingFactory = entityLoadingFactory;
		}

		JavaBeanIndexedTypeContext<E> build() {
			return new JavaBeanIndexedTypeContext<>( this );
		}
	}
}
