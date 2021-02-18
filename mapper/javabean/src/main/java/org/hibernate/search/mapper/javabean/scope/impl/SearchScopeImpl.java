/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.mapper.javabean.scope.impl;

import java.util.Set;
import org.hibernate.search.engine.backend.session.spi.DetachedBackendSessionContext;

import org.hibernate.search.engine.search.aggregation.dsl.SearchAggregationFactory;
import org.hibernate.search.engine.search.predicate.dsl.SearchPredicateFactory;
import org.hibernate.search.engine.search.projection.dsl.SearchProjectionFactory;
import org.hibernate.search.engine.search.query.dsl.SearchQuerySelectStep;
import org.hibernate.search.engine.search.sort.dsl.SearchSortFactory;
import org.hibernate.search.mapper.javabean.common.EntityReference;
import org.hibernate.search.mapper.javabean.entity.SearchIndexedEntity;
import org.hibernate.search.mapper.javabean.massindexing.MassIndexer;
import org.hibernate.search.mapper.javabean.massindexing.impl.MassIndexerImpl;
import org.hibernate.search.mapper.javabean.scope.SearchScope;
import org.hibernate.search.mapper.javabean.search.loading.context.impl.JavaBeanLoadingContext;
import org.hibernate.search.mapper.javabean.search.loading.dsl.SearchLoadingOptionsStep;
import org.hibernate.search.mapper.javabean.search.query.dsl.impl.JavaBeanSearchQuerySelectStep;
import org.hibernate.search.mapper.pojo.scope.spi.PojoScopeDelegate;

public class SearchScopeImpl<E> implements SearchScope<E> {

	private final JavaBeanScopeMappingContext mappingContext;
	private final PojoScopeDelegate<EntityReference, E, JavaBeanScopeIndexedTypeContext<? extends E>> delegate;

	public SearchScopeImpl(JavaBeanScopeMappingContext mappingContext,
			PojoScopeDelegate<EntityReference, E, JavaBeanScopeIndexedTypeContext<? extends E>> delegate) {
		this.mappingContext = mappingContext;
		this.delegate = delegate;
	}

	@Override
	public SearchPredicateFactory predicate() {
		return delegate.predicate();
	}

	@Override
	public SearchSortFactory sort() {
		return delegate.sort();
	}

	@Override
	public SearchProjectionFactory<EntityReference, E> projection() {
		return delegate.projection();
	}

	@Override
	public SearchAggregationFactory aggregation() {
		return delegate.aggregation();
	}

	@Override
	public Set<? extends SearchIndexedEntity<? extends E>> includedTypes() {
		return delegate.includedIndexedTypes();
	}

	@Override
	public MassIndexer massIndexer() {
		return massIndexer( (String) null );
	}

	@Override
	public MassIndexer massIndexer(String tenantId) {
		return massIndexer( mappingContext.detachedBackendSessionContext( tenantId ) );
	}

	public MassIndexer massIndexer(DetachedBackendSessionContext detachedSessionContext) {
		return new MassIndexerImpl(
				mappingContext,
				delegate.includedIndexedTypes(),
				detachedSessionContext,
				delegate.schemaManager(),
				delegate.workspace( detachedSessionContext )
		);
	}

	public SearchQuerySelectStep<?, EntityReference, E, SearchLoadingOptionsStep<E>, ?, ?> search(
			JavaBeanScopeSessionContext sessionContext) {

		JavaBeanLoadingContext.Builder<E> loadingContextBuilder = new JavaBeanLoadingContext.Builder<>(
			mappingContext, sessionContext, delegate.includedIndexedTypes()
		);
		return new JavaBeanSearchQuerySelectStep(
			delegate.search( sessionContext.backendSessionContext(), loadingContextBuilder )
		);
	}
}
