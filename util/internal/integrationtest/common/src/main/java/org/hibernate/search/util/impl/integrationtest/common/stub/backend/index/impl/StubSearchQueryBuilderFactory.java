/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.util.impl.integrationtest.common.stub.backend.index.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.hibernate.search.engine.backend.session.spi.BackendSessionContext;
import org.hibernate.search.engine.search.projection.SearchProjection;
import org.hibernate.search.engine.search.loading.spi.SearchLoadingContextBuilder;
import org.hibernate.search.engine.search.query.spi.SearchQueryBuilder;
import org.hibernate.search.engine.search.query.spi.SearchQueryBuilderFactory;
import org.hibernate.search.util.impl.integrationtest.common.stub.backend.search.StubQueryElementCollector;
import org.hibernate.search.util.impl.integrationtest.common.stub.backend.search.StubSearchQueryBuilder;
import org.hibernate.search.util.impl.integrationtest.common.stub.backend.search.StubSearchWork;
import org.hibernate.search.util.impl.integrationtest.common.stub.backend.search.impl.StubScopeModel;
import org.hibernate.search.util.impl.integrationtest.common.stub.backend.search.projection.impl.StubCompositeListSearchProjection;
import org.hibernate.search.util.impl.integrationtest.common.stub.backend.search.projection.impl.StubEntitySearchProjection;
import org.hibernate.search.util.impl.integrationtest.common.stub.backend.search.projection.impl.StubReferenceSearchProjection;
import org.hibernate.search.util.impl.integrationtest.common.stub.backend.search.projection.impl.StubSearchProjection;

class StubSearchQueryBuilderFactory implements SearchQueryBuilderFactory<StubQueryElementCollector> {
	private final StubBackend backend;
	private final StubScopeModel scopeModel;

	StubSearchQueryBuilderFactory(StubBackend backend, StubScopeModel scopeModel) {
		this.backend = backend;
		this.scopeModel = scopeModel;
	}

	@Override
	public <E> SearchQueryBuilder<E, StubQueryElementCollector> selectEntity(BackendSessionContext sessionContext,
			SearchLoadingContextBuilder<?, E, ?> loadingContextBuilder) {
		return new StubSearchQueryBuilder<>(
				backend, scopeModel, StubSearchWork.ResultType.OBJECTS,
				sessionContext,
				loadingContextBuilder,
				StubEntitySearchProjection.get()
		);
	}

	@Override
	public <R> SearchQueryBuilder<R, StubQueryElementCollector> selectEntityReference(BackendSessionContext sessionContext,
			SearchLoadingContextBuilder<R, ?, ?> loadingContextBuilder) {
		return new StubSearchQueryBuilder<>(
				backend, scopeModel, StubSearchWork.ResultType.REFERENCES,
				sessionContext,
				loadingContextBuilder,
				StubReferenceSearchProjection.get()
		);
	}

	@Override
	public <P> SearchQueryBuilder<P, StubQueryElementCollector> select(BackendSessionContext sessionContext,
			SearchLoadingContextBuilder<?, ?, ?> loadingContextBuilder, SearchProjection<P> projection) {
		return new StubSearchQueryBuilder<>(
				backend, scopeModel, StubSearchWork.ResultType.PROJECTIONS,
				sessionContext,
				loadingContextBuilder,
				(StubSearchProjection<P>) projection
		);
	}

	@Override
	public SearchQueryBuilder<List<?>, StubQueryElementCollector> select(BackendSessionContext sessionContext,
			SearchLoadingContextBuilder<?, ?, ?> loadingContextBuilder, SearchProjection<?>... projections) {
		return new StubSearchQueryBuilder<>(
				backend, scopeModel, StubSearchWork.ResultType.PROJECTIONS,
				sessionContext,
				loadingContextBuilder,
				createRootProjection( projections )
		);
	}

	private StubSearchProjection<List<?>> createRootProjection(SearchProjection<?>[] projections) {
		List<StubSearchProjection<?>> children = new ArrayList<>( projections.length );

		for ( SearchProjection<?> projection : projections ) {
			children.add( (StubSearchProjection<?>) projection );
		}

		return new StubCompositeListSearchProjection<>( Function.identity(), children );
	}
}
