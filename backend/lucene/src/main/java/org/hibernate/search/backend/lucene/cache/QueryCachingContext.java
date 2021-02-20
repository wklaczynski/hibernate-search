/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.backend.lucene.cache;

import java.util.Optional;
import org.apache.lucene.search.QueryCache;
import org.apache.lucene.search.QueryCachingPolicy;

/**
 * A context allowing the configuration of query caching in a Lucene backend.
 */
public interface QueryCachingContext {

	/**
	 * @return The corresponding {@link QueryCache}, or {@link Optional#empty()} if it doesn't exist.
	 *
	 */
	Optional<QueryCache> queryCache();

	/**
	 * @return The corresponding {@link QueryCachingPolicy}, or {@link Optional#empty()} if it doesn't exist.
	 *
	 */
	Optional<QueryCachingPolicy> queryCachingPolicy();

}
