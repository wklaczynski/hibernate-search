/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.mapper.javabean.search.query.dsl.impl;

import org.hibernate.search.engine.search.predicate.dsl.SearchPredicateFactory;
import org.hibernate.search.engine.search.projection.dsl.SearchProjectionFactory;
import org.hibernate.search.engine.search.query.dsl.SearchQueryOptionsStep;
import org.hibernate.search.engine.search.query.dsl.SearchQuerySelectStep;
import org.hibernate.search.engine.search.query.dsl.spi.AbstractDelegatingSearchQuerySelectStep;
import org.hibernate.search.mapper.javabean.common.EntityReference;
import org.hibernate.search.mapper.javabean.search.loading.dsl.SearchLoadingOptionsStep;

public class JavaBeanSearchQuerySelectStep<E>
		extends AbstractDelegatingSearchQuerySelectStep<EntityReference, E, SearchLoadingOptionsStep>
		implements SearchQuerySelectStep<
		SearchQueryOptionsStep<?, E, SearchLoadingOptionsStep, ?, ?>,
		EntityReference,
		E,
		SearchLoadingOptionsStep,
		SearchProjectionFactory<EntityReference, E>,
		SearchPredicateFactory
		> {

	public JavaBeanSearchQuerySelectStep(
			SearchQuerySelectStep<?, EntityReference, E, SearchLoadingOptionsStep, ?, ?> delegate) {
		super( delegate );
	}
}
