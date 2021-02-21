/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.mapper.orm.loading.impl;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.LockModeType;
import org.hibernate.FlushMode;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;

import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.query.Query;
import org.hibernate.search.mapper.orm.logging.impl.Log;
import org.hibernate.search.mapper.orm.massindexing.impl.HibernateOrmMassIndexingIndexedTypeContext;
import org.hibernate.search.mapper.orm.massindexing.impl.MassIndexingTypeGroupLoader;
import org.hibernate.search.mapper.orm.massindexing.spi.dsl.MassIndexingTypeBatchingStep;
import org.hibernate.search.mapper.orm.massindexing.spi.MassIndexingTypeIndexer;
import org.hibernate.search.util.common.logging.impl.LoggerFactory;

class HibernateOrmMassIndexingTypeLoaderImpl<E, I> implements MassIndexingTypeGroupLoader<E, I> {

	private static final Log log = LoggerFactory.make( Log.class, MethodHandles.lookup() );

	private static final String ID_PARAMETER_NAME = "ids";

	private final TypeQueryFactory<E, I> queryFactory;
	private final Set<HibernateOrmMassIndexingIndexedTypeContext<? extends E>> includedTypes;
	private final Set<Class<? extends E>> includedTypesFilter;

	public HibernateOrmMassIndexingTypeLoaderImpl(TypeQueryFactory<E, I> queryFactory,
		Set<HibernateOrmMassIndexingIndexedTypeContext<? extends E>> includedTypes,
		Set<Class<? extends E>> includedTypesFilter) {
		this.queryFactory = queryFactory;
		this.includedTypes = includedTypes;
		this.includedTypesFilter = includedTypesFilter;
	}

	@Override
	public void loadIndex(MassIndexingTypeIndexer<E, I> indexer) {
		long totalCount = createTotalCountQuery( indexer.session() ).uniqueResult();
		indexer.totalCount( totalCount );

		MassIndexingTypeBatchingStep<E, I> batching = indexer.batching( (ctx, ids) -> {
			Query<? super E> query = createLoadingQuery( ctx.session(), ID_PARAMETER_NAME )
				.setParameter( ID_PARAMETER_NAME, ids )
				.setCacheMode( ctx.cacheMode() )
				.setLockMode( LockModeType.NONE )
				.setCacheable( false )
				.setHibernateFlushMode( FlushMode.MANUAL )
				.setFetchSize( ids.size() );

			return query.getResultList();
		} );

		ArrayList<I> destinationList = new ArrayList<>( indexer.batchSize() );
		long counter = 0;
		try ( ScrollableResults results = createIdentifiersQuery( indexer.session() )
			.setFetchSize( indexer.fetchSize() ).scroll( ScrollMode.FORWARD_ONLY ) ) {

			while ( results.next() ) {
				@SuppressWarnings("unchecked")
				I id = (I) results.get( 0 );
				destinationList.add( id );
				if ( destinationList.size() == indexer.batchSize() ) {
					// Explicitly checking whether the TX is still open; Depending on the driver implementation new ids
					// might be produced otherwise if the driver fetches all rows up-front
					if ( !indexer.session().isTransactionInProgress() ) {
						throw log.transactionNotActiveWhileProducingIdsForBatchIndexing( includedEntityNames() );
					}

					batching.load( destinationList );
					destinationList = new ArrayList<>( indexer.batchSize() );
				}
				counter++;
				if ( counter == totalCount ) {
					break;
				}
			}
		}
		batching.load( destinationList );
	}

	private Query<Long> createTotalCountQuery(SharedSessionContractImplementor session) {
		return queryFactory.createQueryForCount( session, includedTypesFilter )
			.setCacheable( false );
	}

	private Query<I> createIdentifiersQuery(SharedSessionContractImplementor session) {
		return queryFactory.createQueryForIdentifierListing( session, includedTypesFilter )
			.setCacheable( false );
	}

	public String includedEntityNames() {
		return includedTypes.stream().map( HibernateOrmMassIndexingIndexedTypeContext::jpaEntityName )
			.collect( Collectors.joining( "," ) );
	}

	public Query<E> createLoadingQuery(SessionImplementor session, String idParameterName) {
		return queryFactory.createQueryForLoadByUniqueProperty( session, idParameterName );
	}

}
