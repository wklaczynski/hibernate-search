/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.mapper.javabean.search.loading.spi;


import org.hibernate.search.mapper.javabean.search.loading.impl.SearchLoadingMappingContext;
import java.util.Map;
import org.hibernate.search.util.common.impl.Contracts;

public class MutableEntityLoadingOptions {
	private int fetchSize;
	private final Map<String, Object> options;

	public MutableEntityLoadingOptions(SearchLoadingMappingContext mappingContext, Map<String, Object> options) {
		this.fetchSize = mappingContext.fetchSize();
		this.options = options;
	}

	public int fetchSize() {
		return fetchSize;
}

	public void fetchSize(int fetchSize) {
		Contracts.assertStrictlyPositive( fetchSize, "fetchSize" );
		this.fetchSize = fetchSize;
	}

	public Object option(String name) {
		return options.get( name );
	}

	public void option(String name, Object value) {
		Contracts.assertNotNull( name, "option" );
		options.put( name, value );
	}

	public Map<String, Object> options() {
		return options;
	}

}
