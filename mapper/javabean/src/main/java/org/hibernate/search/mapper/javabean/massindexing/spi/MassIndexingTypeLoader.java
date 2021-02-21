/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.mapper.javabean.massindexing.spi;


/**
 * A start loader for entity loading entities during mass indexing.
 *
 * @param <E> The resulting entity type (output)
 * @param <I> The expected reference type (identifier)
 */
public interface MassIndexingTypeLoader<E, I> {

	void loadIndex(MassIndexingTypeIndexer<E, I> indexer);

}
