/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.mapper.pojo.mapping.building.impl;

import java.util.Map;
import org.hibernate.search.engine.environment.bean.BeanReference;
import org.hibernate.search.engine.search.predicate.factories.NamedPredicateFactory;
import org.hibernate.search.mapper.pojo.bridge.binding.TypeBindingContext;
import org.hibernate.search.mapper.pojo.bridge.mapping.programmatic.TypeBinder;

public class NamedPredicateTypeBinder implements TypeBinder {

	private final String name;
	private final BeanReference<? extends NamedPredicateFactory> factoryReference;
	private final Map<String, Object> params;

	public NamedPredicateTypeBinder(String name, BeanReference<? extends NamedPredicateFactory> factoryReference, Map<String, Object> params) {
		this.name = name;
		this.factoryReference = factoryReference;
		this.params = params;
	}

	@Override
	public void bind(TypeBindingContext context) {
		context.dependencies().useRootOnly();
		NamedPredicateFactory factory = factoryReference.resolve( context.beanResolver() ).get();
		context.indexSchemaElement().namedPredicate( name, factory ).params( params );

		context.bridge( (target, bridgedElement, bcontext) -> {
		} );
	}
}
