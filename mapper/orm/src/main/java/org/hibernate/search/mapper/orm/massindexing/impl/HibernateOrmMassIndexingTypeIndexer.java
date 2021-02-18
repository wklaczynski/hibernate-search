/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.mapper.orm.massindexing.impl;

import java.lang.invoke.MethodHandles;
import java.util.List;
import org.hibernate.CacheMode;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.search.mapper.orm.logging.impl.Log;
import org.hibernate.search.mapper.orm.massindexing.spi.MassIndexingMappingContext;
import org.hibernate.search.mapper.orm.massindexing.spi.MassIndexingTypeBatchLoader;
import org.hibernate.search.mapper.orm.massindexing.spi.MassIndexingTypeLoader;
import org.hibernate.search.mapper.orm.massindexing.spi.dsl.MassIndexingTypeBatchingStep;
import org.hibernate.search.mapper.orm.massindexing.spi.dsl.MassIndexingTypeIndexer;
import org.hibernate.search.util.common.logging.impl.LoggerFactory;

class HibernateOrmMassIndexingTypeIndexer<E, I> implements MassIndexingTypeIndexer<E, I> {

	private static final Log log = LoggerFactory.make( Log.class, MethodHandles.lookup() );
	private final MassIndexingMappingContext mappingContext;
	private final String tenantId;

	private final MassIndexingNotifier notifier;
	private final int batchSize;
	private final long objectsLimit;
	private final int idFetchSize;
	private MassIndexingTypeBatchLoader indexTypeBatchLoader;
	private final MassIndexingTypeLoader<E, I> typeGroupLoader;
	private final ProducerConsumerQueue<List<I>> destination;
	private final CacheMode cacheMode;
	private SessionImplementor session;

	public HibernateOrmMassIndexingTypeIndexer(
			MassIndexingMappingContext mappingContext,
			String tenantId,
			MassIndexingNotifier notifier,
			MassIndexingTypeLoader<E, I> typeGroupLoader,
			ProducerConsumerQueue<List<I>> destination,
			CacheMode cacheMode,
			int objectLoadingBatchSize, long objectsLimit,
			int idFetchSize) {
		this.mappingContext = mappingContext;
		this.tenantId = tenantId;
		this.notifier = notifier;

		this.batchSize = objectLoadingBatchSize;
		this.objectsLimit = objectsLimit;
		this.idFetchSize = idFetchSize;
		this.typeGroupLoader = typeGroupLoader;
		this.destination = destination;
		this.cacheMode = cacheMode;
	}

	public void loadAllIdentifiers(SessionImplementor session) throws InterruptedException {
		this.session = session;
		typeGroupLoader.loadIndex( this );
	}

	public MassIndexingTypeBatchLoader<E, I> indexTypeBatchLoader() {
		return indexTypeBatchLoader;
	}

	@Override
	public String tenantId() {
		return tenantId;
	}

	@Override
	public int batchSize() {
		return batchSize;
	}

	@Override
	public long objectsLimit() {
		return objectsLimit;
	}

	@Override
	public int fetchSize() {
		return idFetchSize;
	}

	@Override
	public MassIndexingTypeBatchingStep batching(MassIndexingTypeBatchLoader consumer) {
		this.indexTypeBatchLoader = consumer;
		return new TypeBatchingStep();
	}

	@Override
	public MassIndexingTypeIndexer<E, I> totalCount(long value) {
		notifier.notifyAddedTotalCount( value );
		return this;
	}

	private void enqueueList(final List<I> idsList) throws InterruptedException {
		if ( !idsList.isEmpty() ) {
			destination.put( idsList );
			log.tracef( "produced a list of ids %s", idsList );
		}
	}

	@Override
	public CacheMode cacheMode() {
		return cacheMode;
	}

	@Override
	public SessionImplementor session() {
		return session;
	}

	@Override
	public MassIndexingMappingContext mappingContext() {
		return mappingContext;
	}

	private class TypeBatchingStep implements MassIndexingTypeBatchingStep {

		@Override
		public MassIndexingTypeBatchingStep load(List ids) {
			try {
				enqueueList( ids );
			}
			catch (InterruptedException e) {
				// just quit
				Thread.currentThread().interrupt();
			}
			return this;
		}
	}
}
