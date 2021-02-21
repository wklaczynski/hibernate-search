/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.mapper.javabean.scope.impl;

import org.hibernate.search.engine.backend.session.spi.BackendSessionContext;
import org.hibernate.search.mapper.javabean.search.loading.impl.SearchLoadingSessionContext;
import org.hibernate.search.mapper.javabean.spi.BatchSessionContext;
import org.hibernate.search.mapper.javabean.massindexing.impl.JavaBeanMassIndexingSessionContext;

public interface JavaBeanScopeSessionContext
		extends SearchLoadingSessionContext, JavaBeanMassIndexingSessionContext, BatchSessionContext {

	BackendSessionContext backendSessionContext();

}
