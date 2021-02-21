/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.mapper.javabean.massindexing.impl;

import org.hibernate.search.mapper.javabean.massindexing.spi.MassIndexingIndexedTypeGroup;
import java.lang.invoke.MethodHandles;
import java.util.List;
import org.hibernate.search.mapper.javabean.log.impl.Log;

import org.hibernate.search.util.common.logging.impl.LoggerFactory;
import org.hibernate.search.mapper.javabean.massindexing.spi.MassIndexingTypeLoader;

/**
 * This Runnable is going to feed the indexing queue
 * with the identifiers of all the entities going to be indexed.
 * This step in the indexing process is not parallel (should be
 * done by one thread per type) so that a single transaction is used
 * to define the group of entities to be indexed.
 * Produced identifiers are put in the destination queue grouped in List
 * instances: the reason for this is to load them in batches
 * in the next step and reduce contention on the queue.
 *
 * @param <E> The entity type
 * @param <I> The identifier type
 *
 * @author Sanne Grinovero
 */
public class IdentifierProducer<E, I> implements StatelessSessionAwareRunnable {

	private static final Log log = LoggerFactory.make( Log.class, MethodHandles.lookup() );

	private final MassIndexingNotifier notifier;
	private final JavaBeanMassIndexingTypeIndexer<E, I> typeIndexer;
	private final String tenantId;

	private final MassIndexingIndexedTypeGroup<E, I> typeGroup;
	private final MassIndexingTypeLoader<E, I> typeGroupLoader;

	private final ProducerConsumerQueue<List<I>> destination;

	IdentifierProducer(
			String tenantId,
			JavaBeanMassIndexingTypeIndexer<E, I> typeIndexer,
			MassIndexingNotifier notifier,
			MassIndexingIndexedTypeGroup<E, I> typeGroup,
			MassIndexingTypeLoader<E, I> typeGroupLoader,
			ProducerConsumerQueue<List<I>> destination) {
		this.typeIndexer = typeIndexer;
		this.tenantId = tenantId;
		this.notifier = notifier;
		this.typeGroup = typeGroup;
		this.typeGroupLoader = typeGroupLoader;
		this.destination = destination;
		log.trace( "created" );
	}

	@Override
	public void run() {
		log.trace( "started" );
		try {
			inTransactionWrapper();
		}
		catch (RuntimeException exception) {
			notifier.notifyRunnableFailure( exception, log.massIndexerFetchingIds( typeGroup.includedEntityNames() ) );
		}
		finally {
			destination.producerStopping();
		}
		log.trace( "finished" );
	}

	public void inTransactionWrapper() {
		try {
//			transactionContext.wrapInTransaction();
//			Transaction transaction = transactionManager.getTransaction();
//			final boolean controlTransactions = !  == Status.STATUS_NO_TRANSACTION;
//			if ( controlTransactions ) {
//				transaction.begin();
//			}
//			try {
			typeIndexer.loadAllIdentifiers();
//			}
//			finally {
//				if ( controlTransactions ) {
//					transaction.commit();
//				}
//			}
		}
		catch (InterruptedException e) {
			// just quit
			Thread.currentThread().interrupt();
		}
	}

}
