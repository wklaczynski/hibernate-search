/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.mapper.javabean.mapping.impl;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.transaction.TransactionManager;
import org.hibernate.search.engine.backend.session.spi.DetachedBackendSessionContext;

import org.hibernate.search.engine.common.spi.SearchIntegration;
import org.hibernate.search.engine.environment.thread.spi.ThreadPoolProvider;
import org.hibernate.search.engine.reporting.FailureHandler;
import org.hibernate.search.mapper.javabean.entity.SearchIndexedEntity;
import org.hibernate.search.mapper.javabean.log.impl.Log;
import org.hibernate.search.mapper.javabean.mapping.CloseableSearchMapping;
import org.hibernate.search.mapper.javabean.mapping.SearchMapping;
import org.hibernate.search.mapper.javabean.scope.SearchScope;
import org.hibernate.search.mapper.javabean.scope.impl.JavaBeanScopeMappingContext;
import org.hibernate.search.mapper.javabean.scope.impl.JavaBeanScopeSessionContext;
import org.hibernate.search.mapper.javabean.scope.impl.SearchScopeImpl;
import org.hibernate.search.mapper.javabean.session.SearchSessionBuilder;
import org.hibernate.search.mapper.javabean.session.SearchSession;
import org.hibernate.search.mapper.javabean.session.impl.JavaBeanSearchSession;
import org.hibernate.search.mapper.javabean.session.impl.JavaBeanSearchSessionMappingContext;
import org.hibernate.search.mapper.pojo.mapping.spi.PojoMappingDelegate;
import org.hibernate.search.mapper.pojo.mapping.spi.AbstractPojoMappingImplementor;
import org.hibernate.search.mapper.pojo.model.spi.PojoRawTypeIdentifier;
import org.hibernate.search.mapper.pojo.scope.spi.PojoScopeDelegate;
import org.hibernate.search.util.common.logging.impl.LoggerFactory;

public class JavaBeanMapping extends AbstractPojoMappingImplementor<SearchMapping>
		implements CloseableSearchMapping, JavaBeanSearchSessionMappingContext, JavaBeanScopeMappingContext {

	private static final Log log = LoggerFactory.make( Log.class, MethodHandles.lookup() );

	private final JavaBeanTypeContextContainer typeContextContainer;

	private SearchIntegration integration;

	private TransactionManager transactionManager;

	private int fetchSize;

	JavaBeanMapping(PojoMappingDelegate mappingDelegate, JavaBeanTypeContextContainer typeContextContainer) {
		super( mappingDelegate );
		this.typeContextContainer = typeContextContainer;
		this.fetchSize = 100;
	}

	@Override
	public void close() {
		if ( integration != null ) {
			integration.close();
		}
	}

	@Override
	public <T> SearchScope<T> scope(Collection<? extends Class<? extends T>> targetedTypes) {
		return createScope( targetedTypes );
	}

	@Override
	public SearchMapping toConcreteType() {
		return this;
	}

	@Override
	public SearchSession createSession() {
		return createSearchManagerBuilder().build();
	}

	@Override
	public SearchSessionBuilder createSessionWithOptions() {
		return createSearchManagerBuilder();
	}

	@Override
	public DetachedBackendSessionContext detachedBackendSessionContext(String tenantId) {
		return DetachedBackendSessionContext.of( this, tenantId );
	}

	@Override
	public ThreadPoolProvider threadPoolProvider() {
		return delegate().threadPoolProvider();
	}

	@Override
	public FailureHandler failureHandler() {
		return delegate().failureHandler();
	}

	@Override
	public JavaBeanScopeSessionContext sessionContext() {
		return (JavaBeanScopeSessionContext) createSession();
	}

	@Override
	public int fetchSize() {
		return fetchSize;
	}

	public void setFetchSize(int fetchSize) {
		this.fetchSize = fetchSize;
	}

	@Override
	public TransactionManager transactionManager() {
		return transactionManager;
	}

	public void transactionManager(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	@Override
	public <T> SearchScopeImpl<T> createScope(Collection<? extends Class<? extends T>> classes) {
		List<PojoRawTypeIdentifier<? extends T>> typeIdentifiers = new ArrayList<>( classes.size() );
		for ( Class<? extends T> clazz : classes ) {
			typeIdentifiers.add( PojoRawTypeIdentifier.of( clazz ) );
		}

		PojoScopeDelegate<T, T, JavaBeanIndexedTypeContext<? extends T>> delegate = delegate().createPojoScope( this, typeIdentifiers,
			typeContextContainer::indexedForExactType );

		// Explicit type parameter is necessary here for ECJ (Eclipse compiler)
		return new SearchScopeImpl( this, delegate );
	}

	@Override
	public <E> SearchIndexedEntity<E> indexedEntity(Class<E> entityType) {
		SearchIndexedEntity<E> type = typeContextContainer.indexedForExactClass( entityType );
		if ( type == null ) {
			throw log.notIndexedEntityType( entityType );
		}
		return type;
	}

	@Override
	public SearchIndexedEntity<?> indexedEntity(String entityName) {
		SearchIndexedEntity<?> type = typeContextContainer.indexedForEntityName( entityName );
		if ( type == null ) {
			throw log.notIndexedEntityName( entityName );
		}
		return type;
	}

	@Override
	public Collection<SearchIndexedEntity<?>> allIndexedEntities() {
		return Collections.unmodifiableCollection( typeContextContainer.allIndexed() );
	}

	public void setIntegration(SearchIntegration integration) {
		this.integration = integration;
	}

	private SearchSessionBuilder createSearchManagerBuilder() {
		return new JavaBeanSearchSession.Builder(
				this, typeContextContainer
		);
	}
}
