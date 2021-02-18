/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.mapper.javabean.massindexing.spi;

import java.util.Set;

public interface MassIndexingTypeLoadingStrategy<E, I> {

	default MassIndexingTypeJoinMode calculateIndexingTypeGroupJoinMode(MassIndexingIndexedTypeGroup<?, ?> current, MassIndexingIndexedTypeGroup<?, ?> other) {
		return MassIndexingTypeJoinMode.NONE;
	}

	MassIndexingTypeLoader<E, I> createLoader(
			Set<MassIndexingIndexedTypeContext<E>> targetEntityTypeContexts);

}
