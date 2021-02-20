/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.engine.backend.document.model.dsl;

import java.util.Map;

/**
 * The final step in the definition of a named predicate in the index schema,
 * where a reference to the named predicate can be retrieved,
 * optionally setting some parameters beforehand.
 *
 * @param <S> The "self" type (the actual exposed type of this step).
 */
public interface IndexSchemaNamedPredicateOptionsStep<S extends IndexSchemaNamedPredicateOptionsStep<?>> {

	/**
	 * Insert parametr to this named predicate definition.
	 *
	 * @param <T> The type of the named predicate parametr value.
	 * @param name The name of the named predicate parametr value.
	 * @param value The value of the named predicate parametr.
	 * @return {@code this}, for method chaining.
	 */
	<T> S param(String name, T value);

	/**
	 * Insert params to this filter definition.
	 *
	 * @param params The params of the named predicate.
	 * @return {@code this}, for method chaining.
	 */
	S params(Map<String, Object> params);
}
