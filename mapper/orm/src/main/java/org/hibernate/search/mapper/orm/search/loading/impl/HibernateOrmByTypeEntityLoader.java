/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.mapper.orm.search.loading.impl;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.search.mapper.orm.logging.impl.Log;
import org.hibernate.search.mapper.orm.common.EntityReference;
import org.hibernate.search.engine.search.loading.spi.EntityLoader;
import org.hibernate.search.engine.common.timing.spi.Deadline;
import org.hibernate.search.util.common.logging.impl.LoggerFactory;

class HibernateOrmByTypeEntityLoader<T> implements EntityLoader<EntityReference, T> {

	private static final Log log = LoggerFactory.make( Log.class, MethodHandles.lookup() );

	private final Map<String, EntityLoader<EntityReference, T>> delegatesByEntityName;

	HibernateOrmByTypeEntityLoader(Map<String, EntityLoader<EntityReference, T>> delegatesByEntityName) {
		this.delegatesByEntityName = delegatesByEntityName;
	}

	@Override
	public void loadBlocking(List<EntityReference> references, Map<EntityReference, T> entitymap, Deadline timeoutManager) {
		Map<EntityLoader<EntityReference, T>, List<EntityReference>> referencesByDelegate = new HashMap<>();

		// Split references by delegate (by entity type)
		// Note that multiple entity types may share the same loader
		for ( EntityReference reference : references ) {
			entitymap.put( reference, null );
			EntityLoader<EntityReference, T> delegate = delegateForType( reference.name() );
			referencesByDelegate.computeIfAbsent( delegate, ignored -> new ArrayList<>() )
					.add( reference );
		}

		// Load all references
		for ( Map.Entry<EntityLoader<EntityReference, T>, List<EntityReference>> entry :
				referencesByDelegate.entrySet() ) {
			EntityLoader<EntityReference, T> delegate = entry.getKey();
			List<EntityReference> referencesForDelegate = entry.getValue();

			delegate.loadBlocking( referencesForDelegate, entitymap, timeoutManager );
		}
	}

	private EntityLoader<EntityReference, T> delegateForType(String entityName) {
		EntityLoader<EntityReference, T> delegate = delegatesByEntityName.get( entityName );
		if ( delegate == null ) {
			throw log.unexpectedSearchHitEntityName( entityName, delegatesByEntityName.keySet() );
		}
		return delegate;
	}
}
