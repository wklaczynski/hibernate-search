/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.mapper.javabean.scope.impl;

import java.util.Set;

import org.hibernate.search.engine.search.aggregation.dsl.SearchAggregationFactory;
import org.hibernate.search.engine.search.predicate.dsl.SearchPredicateFactory;
import org.hibernate.search.engine.search.projection.dsl.SearchProjectionFactory;
import org.hibernate.search.engine.search.query.dsl.SearchQuerySelectStep;
import org.hibernate.search.engine.search.sort.dsl.SearchSortFactory;
import org.hibernate.search.engine.backend.common.spi.DocumentReferenceConverter;
import org.hibernate.search.mapper.javabean.common.EntityReference;
import org.hibernate.search.mapper.javabean.entity.SearchIndexedEntity;
import org.hibernate.search.mapper.javabean.scope.SearchScope;
import org.hibernate.search.mapper.pojo.loading.spi.PojoLoadingContextBuilder;
import org.hibernate.search.mapper.pojo.scope.spi.PojoScopeDelegate;
import org.hibernate.search.mapper.pojo.scope.spi.PojoScopeSessionContext;

public class SearchScopeImpl<E> implements SearchScope<E> {

	private final PojoScopeDelegate<EntityReference, E, JavaBeanScopeIndexedTypeContext<? extends E>> delegate;

	public SearchScopeImpl(PojoScopeDelegate<EntityReference, E, JavaBeanScopeIndexedTypeContext<? extends E>> delegate) {
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
	public SearchProjectionFactory<EntityReference, ?> projection() {
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

	public SearchQuerySelectStep<?, EntityReference, E, ?, ?, ?> search(PojoScopeSessionContext sessionContext,
			DocumentReferenceConverter<EntityReference> documentReferenceConverter,
			PojoLoadingContextBuilder<?> loadingContextBuilder) {
		return delegate.search( sessionContext, documentReferenceConverter, loadingContextBuilder );
	}
}
