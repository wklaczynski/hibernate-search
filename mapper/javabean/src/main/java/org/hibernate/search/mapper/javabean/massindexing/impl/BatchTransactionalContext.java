/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.mapper.javabean.massindexing.impl;

import java.lang.invoke.MethodHandles;
import javax.transaction.NotSupportedException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;
import org.hibernate.search.mapper.javabean.log.impl.Log;

import org.hibernate.search.util.common.logging.impl.LoggerFactory;
import org.hibernate.search.mapper.javabean.massindexing.spi.MassIndexingMappingContext;

/**
 * Valueholder for the services needed by the massindexer to wrap operations in transactions.
 *
 * @since 4.4
 * @see OptionallyWrapInJTATransaction
 * @author Sanne Grinovero
 */
public class BatchTransactionalContext {

	private static final Log log = LoggerFactory.make( Log.class, MethodHandles.lookup() );

	final TransactionManager transactionManager;

	public BatchTransactionalContext(MassIndexingMappingContext mappingContext) {
		this.transactionManager = mappingContext.transactionManager();
	}

	boolean wrapInTransaction() {
		if ( transactionManager == null ) {
			//no TM, nothing to do OR configuration mistake
			log.trace( "No TransactionManager found, do not start a surrounding JTA transaction" );
			return false;
		}
		try {
			if ( transactionManager.getStatus() == Status.STATUS_NO_TRANSACTION ) {
				log.trace( "No Transaction in progress, needs to start a JTA transaction" );
				return true;
			}
		}
		catch (SystemException e) {
			log.cannotGuessTransactionStatus( e );
			return false;
		}
		log.trace( "Transaction in progress, no need to start a JTA transaction" );
		return false;
	}

	public void beginTransaction(Integer transactionTimeout) throws SystemException, NotSupportedException {
		if ( transactionManager != null ) {
			if ( transactionTimeout != null ) {
				transactionManager.setTransactionTimeout( transactionTimeout );
			}

			transactionManager.begin();
		}
	}

	@SuppressWarnings("UseSpecificCatch")
	public void rollbackTransaction() {
		try {
			if ( transactionManager != null ) {
				transactionManager.rollback();
			}
		}
		catch (Exception e) {
			log.errorRollingBackTransaction( e.getMessage(), e );
		}
	}

	@SuppressWarnings("UseSpecificCatch")
	public void commitTransaction() {
		try {
			if ( transactionManager != null ) {
				transactionManager.rollback();
			}
		}
		catch (Exception e) {
			log.errorRollingBackTransaction( e.getMessage(), e );
		}
	}
}
