/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.mapper.javabean.search.loading.impl;

import org.hibernate.search.mapper.javabean.search.loading.spi.MutableEntityLoadingOptions;
import org.hibernate.search.mapper.javabean.search.loading.spi.SearchLoadingIndexedTypeContext;
import org.hibernate.search.mapper.javabean.search.loading.spi.SearchEntityLoadingStrategy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.search.engine.search.loading.spi.EntityLoader;
import org.hibernate.search.mapper.javabean.common.EntityReference;

public class SearchEntityLoaderBuilder<E> {

	private final Set<? extends SearchLoadingIndexedTypeContext> concreteIndexedTypes;

	public SearchEntityLoaderBuilder(SearchLoadingMappingContext mappingContext,
			SearchLoadingSessionContext sessionContext,
			Set<? extends SearchLoadingIndexedTypeContext> concreteIndexedTypes) {
		this.concreteIndexedTypes = concreteIndexedTypes;
	}

	public EntityLoader<EntityReference, E> build(MutableEntityLoadingOptions mutableLoadingOptions) {
		if ( concreteIndexedTypes.size() == 1 ) {
			SearchLoadingIndexedTypeContext typeContext = concreteIndexedTypes.iterator().next();
			return single( typeContext, mutableLoadingOptions );
		}

		/*
		 * First, group the types by their loading strategy.
		 * If multiple types are in the same entity hierarchy and are loaded the same way,
		 * this will allow running a single query to load entities of all these types,
		 * instead of one query per type.
		 */
		Map<SearchEntityLoadingStrategy, List<SearchLoadingIndexedTypeContext>> typesBySearchEntityLoadingStrategy =
				new HashMap<>( concreteIndexedTypes.size() );
		for ( SearchLoadingIndexedTypeContext typeContext : concreteIndexedTypes ) {
			SearchEntityLoadingStrategy loadingStrategyForType = (SearchEntityLoadingStrategy) typeContext.loadingStrategy();
			typesBySearchEntityLoadingStrategy.computeIfAbsent( loadingStrategyForType, ignored -> new ArrayList<>() )
					.add( typeContext );
		}

		/*
		 * Then create the loaders.
		 */
		if ( typesBySearchEntityLoadingStrategy.size() == 1 ) {
			// Optimization: we only need one loader, so skip the "by type" wrapper.
			Map.Entry<SearchEntityLoadingStrategy, List<SearchLoadingIndexedTypeContext>> entry =
					typesBySearchEntityLoadingStrategy.entrySet().iterator().next();
			SearchEntityLoadingStrategy loadingStrategy = entry.getKey();
			List<SearchLoadingIndexedTypeContext> types = entry.getValue();
			return multiple( loadingStrategy, types, mutableLoadingOptions );
		}
		else {
			Map<String, EntityLoader<EntityReference, E>> delegateByEntityName =
					new HashMap<>( concreteIndexedTypes.size() );
			for ( Map.Entry<SearchEntityLoadingStrategy, List<SearchLoadingIndexedTypeContext>> entry :
					typesBySearchEntityLoadingStrategy.entrySet() ) {
				SearchEntityLoadingStrategy loadingStrategy = entry.getKey();
				List<SearchLoadingIndexedTypeContext> types = entry.getValue();
				EntityLoader loader =
						multiple( loadingStrategy, types, mutableLoadingOptions );
				for ( SearchLoadingIndexedTypeContext type : types ) {
					delegateByEntityName.put( type.entityName(), loader );
				}
			}
			return new JavaBeanByTypeEntityLoader<>( delegateByEntityName );
		}
	}

	private EntityLoader<EntityReference, E> single(
			SearchLoadingIndexedTypeContext typeContext,
			MutableEntityLoadingOptions mutableLoadingOptions) {

		SearchEntityLoadingStrategy loadingStrategy = (SearchEntityLoadingStrategy) typeContext.loadingStrategy();
		return loadingStrategy.createLoader(
				typeContext,
				mutableLoadingOptions
		);
	}

	private EntityLoader<EntityReference, E> multiple(
			SearchEntityLoadingStrategy loadingStrategy,
			List<SearchLoadingIndexedTypeContext> types,
			MutableEntityLoadingOptions mutableLoadingOptions) {
		return loadingStrategy.createLoader(
				types,
				mutableLoadingOptions
		);
	}
}
