/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.mapper.javabean.loading.dsl;

import java.util.function.Consumer;

/**
 * The DSL entry point passed to consumers in
 * {@link org.hibernate.search.engine.search.query.dsl.SearchQueryOptionsStep#loading(Consumer)},
 * allowing the definition of loading options (fetch size, cache lookups, ...).
 */
public interface LoadingConfigureOptionsStep {

	/**
	 * Set the fetch size for this query,
	 * i.e. the amount of entities to load for each query to the database.
	 * <p>
	 * Higher numbers mean fewer queries, but larger result sets.
	 *
	 * @param fetchSize The fetch size. Must be positive or zero.
	 * @return {@code this} for method chaining.
	 */
	LoadingConfigureOptionsStep fetchSize(int fetchSize);

	/**
	 * Set the option for entity loader before loader are loaded.
	 *
	 * @param name The param name.
	 * @param value The param value.
	 * @return {@code this} for method chaining.
	 */
	LoadingConfigureOptionsStep option(String name, Object value);
}
