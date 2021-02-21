/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.mapper.javabean.massindexing.impl;

import java.lang.invoke.MethodHandles;
import java.util.List;
import org.hibernate.search.mapper.javabean.log.impl.Log;
import org.hibernate.search.mapper.javabean.massindexing.spi.MassIndexingMappingContext;
import org.hibernate.search.mapper.javabean.massindexing.spi.MassIndexingTypeBatchLoader;
import org.hibernate.search.mapper.javabean.massindexing.spi.MassIndexingTypeLoader;
import org.hibernate.search.mapper.javabean.massindexing.spi.dsl.MassIndexingTypeBatchingStep;
import org.hibernate.search.mapper.javabean.massindexing.spi.MassIndexingTypeIndexer;
import org.hibernate.search.util.common.logging.impl.LoggerFactory;

class JavaBeanMassIndexingTypeIndexer<E, I> implements MassIndexingTypeIndexer<E, I> {

	private static final Log log = LoggerFactory.make( Log.class, MethodHandles.lookup() );
	private final MassIndexingMappingContext mappingContext;

	private final String tenantId;
	private final MassIndexingNotifier notifier;
	private final int batchSize;
	private final long objectsLimit;
	private final int idFetchSize;
	private MassIndexingTypeBatchLoader indexTypeBatchLoader;
	private final MassIndexingTypeLoader<E, I> typeGroupLoader;
	private final ProducerConsumerQueue<List<?>> destination;

	public JavaBeanMassIndexingTypeIndexer(
			MassIndexingMappingContext mappingContext,
			String tenantId,
			MassIndexingNotifier notifier,
			MassIndexingTypeLoader<E, I> typeGroupLoader,
			ProducerConsumerQueue<List<?>> destination,
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
	}

	public void loadAllIdentifiers() throws InterruptedException {
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

	@Override
	public MassIndexingMappingContext mappingContext() {
		return mappingContext;
	}

	private void enqueueList(final List<I> idsList) throws InterruptedException {
		if ( !idsList.isEmpty() ) {
			destination.put( idsList );
			log.tracef( "produced a list of ids %s", idsList );
		}
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
