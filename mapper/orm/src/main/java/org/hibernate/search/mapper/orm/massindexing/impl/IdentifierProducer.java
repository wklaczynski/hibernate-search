/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.mapper.orm.massindexing.impl;

import java.lang.invoke.MethodHandles;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.Transaction;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.search.mapper.orm.logging.impl.Log;
import org.hibernate.search.util.common.logging.impl.LoggerFactory;

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

	private final SessionFactory sessionFactory;
	private final HibernateOrmMassIndexingTypeIndexer<E, I> typeIndexer;
	private final MassIndexingNotifier notifier;
	private final String tenantId;

	private final MassIndexingIndexedTypeGroup<E, I> typeGroup;

	private final ProducerConsumerQueue<List<I>> destination;

	IdentifierProducer(SessionFactory sessionFactory, String tenantId,
			HibernateOrmMassIndexingTypeIndexer<E, I> typeIndexer,
			MassIndexingNotifier notifier,
			MassIndexingIndexedTypeGroup<E, I> typeGroup,
			ProducerConsumerQueue<List<I>> destination) {
		this.sessionFactory = sessionFactory;
		this.tenantId = tenantId;
		this.typeIndexer = typeIndexer;
		this.notifier = notifier;
		this.typeGroup = typeGroup;
		this.destination = destination;
		log.trace( "created" );
	}

	@Override
	public void run(StatelessSession upperSession) {
		log.trace( "started" );
		try {
			inTransactionWrapper( upperSession );
		}
		catch (RuntimeException exception) {
			notifier.notifyRunnableFailure( exception, log.massIndexerFetchingIds( typeGroup.includedEntityNames() ) );
		}
		finally {
			destination.producerStopping();
		}
		log.trace( "finished" );
	}

	private void inTransactionWrapper(StatelessSession upperSession) {
		StatelessSession session = upperSession;
		if ( upperSession == null ) {
			if ( tenantId == null ) {
				session = sessionFactory.openStatelessSession();
			}
			else {
				session = sessionFactory.withStatelessOptions().tenantIdentifier( tenantId ).openStatelessSession();
			}
		}
		try {
			Transaction transaction = ((SharedSessionContractImplementor) session).accessTransaction();
			final boolean controlTransactions = !transaction.isActive();
			if ( controlTransactions ) {
				transaction.begin();
			}
			try {
				typeIndexer.loadAllIdentifiers( (SessionImplementor) session );
			}
			finally {
				if ( controlTransactions ) {
					transaction.commit();
				}
			}
		}
		catch (InterruptedException e) {
			// just quit
			Thread.currentThread().interrupt();
		}
		finally {
			if ( upperSession == null ) {
				session.close();
			}
		}
	}

}
