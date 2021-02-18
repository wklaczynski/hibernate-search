/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.engine.search.loading.spi;

import java.util.List;
import java.util.Map;

import org.hibernate.search.engine.common.timing.spi.Deadline;

/**
 * Loads objects into memory using a reference and implementation-specific context.
 *
 * @param <R> The expected reference type (input)
 * @param <E> The resulting entity type (output)
 */
public interface EntityLoader<R, E> {

	/**
	 * Loads the entities corresponding to the given references, blocking the current thread while doing so.
	 *
	 * @param references A list of references to the objects to load.
	 * @param entitymap A map with references as keys and loaded entities as values.
	 * Initial values are undefined and the loader must not rely on them.
	 * @param deadline The deadline for loading the entities, or null if there is no deadline.
	 */
	void loadBlocking(List<R> references, Map<R, E> entitymap, Deadline deadline);

	static <T> EntityLoader<T, T> identity() {
		return IdentityEntityLoader.get();
	}

}
