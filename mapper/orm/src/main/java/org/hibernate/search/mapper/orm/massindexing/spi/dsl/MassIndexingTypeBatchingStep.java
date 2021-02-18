/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.mapper.orm.massindexing.spi.dsl;

import java.util.List;
import java.util.function.Consumer;

/**
 * The DSL entry point passed to consumers in
 * {@link org.hibernate.search.engine.search.query.dsl.SearchQueryOptionsStep#loading(Consumer)},
 * allowing the definition of loading options (fetch size, cache lookups, ...).
 * @param <E>
 * @param <I>
 */
public interface MassIndexingTypeBatchingStep<E, I> {

	MassIndexingTypeBatchingStep<E, I> load(List<I> ids);

}
