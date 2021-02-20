/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.util.impl.integrationtest.common.stub.backend.document.model.impl;

import java.util.Map;
import org.hibernate.search.engine.backend.document.model.dsl.IndexSchemaNamedPredicateOptionsStep;
import org.hibernate.search.engine.search.predicate.factories.NamedPredicateFactory;
import org.hibernate.search.util.impl.integrationtest.common.stub.backend.document.model.StubIndexSchemaNode;

class StubIndexSchemaFilterBuilder<F extends NamedPredicateFactory>
	implements IndexSchemaNamedPredicateOptionsStep<StubIndexSchemaFilterBuilder<F>> {

	private final StubIndexSchemaNode.Builder builder;

	StubIndexSchemaFilterBuilder(StubIndexSchemaNode.Builder builder, boolean included) {
		this.builder = builder;
	}

	@Override
	public <T> StubIndexSchemaFilterBuilder param(String name, T value) {
		builder.namedPredicateParam( name, value );
		return this;
	}

	@Override
	public StubIndexSchemaFilterBuilder<F> params(Map<String, Object> params) {
		params.putAll( params );
		return this;
	}
}
