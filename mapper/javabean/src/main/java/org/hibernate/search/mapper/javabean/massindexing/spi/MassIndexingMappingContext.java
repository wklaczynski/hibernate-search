/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.mapper.javabean.massindexing.spi;

import javax.transaction.TransactionManager;
import org.hibernate.search.engine.reporting.FailureHandler;
import org.hibernate.search.engine.environment.thread.spi.ThreadPoolProvider;
import org.hibernate.search.mapper.javabean.scope.impl.JavaBeanScopeSessionContext;

/**
 * Contextual information about a maped context to load or index a entities during mass indexing.
 */
public interface MassIndexingMappingContext {

	ThreadPoolProvider threadPoolProvider();

	FailureHandler failureHandler();

	JavaBeanScopeSessionContext sessionContext();

	TransactionManager transactionManager();
}
