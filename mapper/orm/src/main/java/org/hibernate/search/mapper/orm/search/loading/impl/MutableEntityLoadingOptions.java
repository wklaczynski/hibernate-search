/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.mapper.orm.search.loading.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.search.util.common.impl.Contracts;

public class MutableEntityLoadingOptions {
	private int fetchSize;

	private List<EntityGraphHint> entityGraphHints;
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

	public EntityGraphHint<?> entityGraphHintOrNullForType(EntityPersister entityPersister) {
		if ( entityGraphHints == null ) {
			return null;
		}
		String hibernateOrmEntityName = entityPersister.getEntityName();
		for ( EntityGraphHint entityGraphHint : entityGraphHints ) {
			if ( entityGraphHint.graph.appliesTo( hibernateOrmEntityName ) ) {
				return entityGraphHint;
			}
		}
		return null;
	}

	public void entityGraphHint(EntityGraphHint<?> entityGraphHint, boolean replaceExisting) {
		if ( entityGraphHints == null ) {
			entityGraphHints = new ArrayList<>();
		}
		else if ( replaceExisting ) {
			entityGraphHints.clear();
		}
		this.entityGraphHints.add( entityGraphHint );
	}
}
