/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.engine.search.predicate.factories;

import java.util.Collection;
import org.hibernate.search.engine.search.predicate.dsl.NamedPredicateOptionsStep;
import org.hibernate.search.engine.search.predicate.dsl.SearchPredicateFactory;

/**
 * The context provided to the named predicate factory {@link SearchPredicateFactory#named(String)} method.
 */
public interface NamedPredicateFactoryContext {

	/**
	 * Return the building of a search predicate.
	 * <p>
	 * The predicate will only be valid for used this named predicate search queries.
	 * <p>
	 *
	 * @return A predicate factory.
	 * @see SearchPredicateFactory
	 */
	SearchPredicateFactory predicate();

	/**
	 * @param name a name of parametr
	 * @return parametr of the named predicate factory {@link NamedPredicateOptionsStep#param(java.lang.String, java.lang.Object)}.
	 */
	Object param(String name);

	/**
	 * @return parent path.
	 */
	String parentPath();

	/**
	 * @return nested path.
	 */
	String nestedPath();

	/**
	 * @return absolute path.
	 */
	String absolutePath();

	/**
	 * @return names parametr of the named predicate {@link NamedPredicateOptionsStep#param(java.lang.String, java.lang.Object)}.
	 */
	Collection<String> paramNames();

	/**
	 * @param relative relative name
	 * @return absolute path.
	 */
	String resolvePath(String relative);
}
