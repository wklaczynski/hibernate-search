/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.mapper.javabean.massindexing.spi;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.search.engine.search.loading.spi.EntityLoadingStrategy;
import org.hibernate.search.mapper.javabean.log.impl.Log;
import org.hibernate.search.util.common.logging.impl.LoggerFactory;

public class MassIndexingIndexedTypeGroup<E, I> {

	private static final Log log = LoggerFactory.make( Log.class, MethodHandles.lookup() );

	/**
	 * Group indexed types by their closest common supertype,
	 * ensuring returned groups are disjoint
	 * (i.e. no two groups have any common indexed subtype among those provided).
	 * <p>
	 * This is necessary to avoid duplicate indexing.
	 * <p>
	 * For example, without this, we could end up reindexing type B in one thread,
	 * and its superclass A (which will include all instances of B) in another.
	 *
	 * @param indexedTypeContexts A set of indexed types to group together.
	 * @return One or more type groups that are guaranteed to be disjoint.
	 */
	public static List<MassIndexingIndexedTypeGroup<?, ?>> disjoint(
			Set<? extends MassIndexingIndexedTypeContext<?>> indexedTypeContexts) {
		List<MassIndexingIndexedTypeGroup<?, ?>> typeGroups = new ArrayList<>();
		for ( MassIndexingIndexedTypeContext<?> typeContext : indexedTypeContexts ) {
			MassIndexingIndexedTypeGroup<?, ?> typeGroup = single( typeContext );
			// First try to merge this new type group with an existing one
			ListIterator<MassIndexingIndexedTypeGroup<?, ?>> iterator = typeGroups.listIterator();
			while ( iterator.hasNext() ) {
				MassIndexingIndexedTypeGroup<?, ?> mergeResult = iterator.next().mergeOrNull( typeGroup );
				if ( mergeResult != null ) {
					// We found an existing group that can be merged with this one.
					// Remove that group, we'll add the merge result to the list later.
					typeGroup = mergeResult;
					iterator.remove();
					// Continue iterating through existing groups, as we may be able to merge with multiple groups.
				}
			}
			typeGroups.add( typeGroup );
		}
		return typeGroups;
	}

	private static <E> MassIndexingIndexedTypeGroup<E, ?> single(MassIndexingIndexedTypeContext<E> typeContext) {
		EntityLoadingStrategy loadingStrategy = typeContext.loadingStrategy();
		if ( !(loadingStrategy instanceof MassIndexingTypeLoadingStrategy) ) {
			throw log.cannotLoadMassIndexingStrategyImpementationNotDefined( loadingStrategy );
		}
		return new MassIndexingIndexedTypeGroup<>( typeContext, (MassIndexingTypeLoadingStrategy) loadingStrategy,
				Collections.singleton( typeContext ) );
	}

	private final MassIndexingIndexedTypeContext<E> commonSuperType;
	private final MassIndexingTypeLoadingStrategy<E, I> loadingStrategy;
	private final Set<MassIndexingIndexedTypeContext<E>> includedTypes;

	private MassIndexingIndexedTypeGroup(MassIndexingIndexedTypeContext<E> commonSuperType,
			MassIndexingTypeLoadingStrategy<E, I> loadingStrategy,
			Set<MassIndexingIndexedTypeContext<E>> includedTypes) {
		this.commonSuperType = commonSuperType;
		this.loadingStrategy = loadingStrategy;
		this.includedTypes = includedTypes;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "["
				+ "commonSuperType=" + commonSuperType
				+ ", loadingStrategy=" + includedTypes
				+ ", includedSubTypes=" + includedTypes
				+ "]";
	}

	public MassIndexingIndexedTypeContext<E> commonSuperType() {
		return commonSuperType;
	}

	public String includedEntityNames() {
		return includedTypes.stream().map( MassIndexingIndexedTypeContext::entityName )
				.collect( Collectors.joining( "," ) );
	}

	public MassIndexingTypeLoader<E, I> createLoader() {
		MassIndexingTypeLoader<E, I> loader = loadingStrategy.createLoader( includedTypes );
		if ( loader == null ) {
			throw log.cannotLoadMassIndexingStrategyNotDefined( loader );
		}
		return loader;
	}

	/**
	 * Merge this group with the other group if
	 * the other group uses the same loading strategy and
	 * @param other The other group to merge with (if possible).
	 * @return The merged group, or {@code null} if
	 * the other group uses a different loading strategy or
	 * the other group's {@code commonSuperType} does <strong>not</strong> represent
	 * a supertype or subtype of this group's {@code commonSuperType}.
	 */
	@SuppressWarnings("unchecked") // The casts are guarded by reflection checks
	private MassIndexingIndexedTypeGroup<?, ?> mergeOrNull(MassIndexingIndexedTypeGroup<?, ?> other) {
		if ( !loadingStrategy.equals( other.loadingStrategy ) ) {
			return null;
		}
		MassIndexingTypeJoinMode joinMode = loadingStrategy.calculateIndexingTypeGroupJoinMode( this, other );
		switch ( joinMode ) {
			case FIRST:
				return withAdditionalTypes( ((MassIndexingIndexedTypeGroup<E, I>) other).includedTypes );
			case NEXT:
				return ((MassIndexingIndexedTypeGroup<E, I>) other).withAdditionalTypes( includedTypes );
		}
		return null;
	}

	private MassIndexingIndexedTypeGroup<E, I> withAdditionalTypes(
			Set<? extends MassIndexingIndexedTypeContext<E>> otherIncludedSubTypes) {
		Set<MassIndexingIndexedTypeContext<E>> mergedIncludedSubTypes =
				new HashSet<>( includedTypes );
		mergedIncludedSubTypes.addAll( otherIncludedSubTypes );
		return new MassIndexingIndexedTypeGroup<>( commonSuperType, loadingStrategy, mergedIncludedSubTypes );
	}
}
