/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.mapper.javabean.loading;

import org.hibernate.search.engine.search.loading.spi.EntityLoadingStrategyContext;
import org.hibernate.search.mapper.javabean.entity.SearchIndexedEntity;
import org.hibernate.search.mapper.pojo.model.spi.PojoPropertyModel;

/**
 * A start context for entity loading strategy.
 */
public class JavaBeanEntityLoadingStrategyContext implements EntityLoadingStrategyContext {
	private final SearchIndexedEntity indexedEntity;
	private final PojoPropertyModel<?> documentIdSourceProperty;

	public JavaBeanEntityLoadingStrategyContext(SearchIndexedEntity indexedEntity,
		PojoPropertyModel<?> documentIdSourceProperty) {
		this.indexedEntity = indexedEntity;
		this.documentIdSourceProperty = documentIdSourceProperty;
	}

	public SearchIndexedEntity indexedEntity() {
		return indexedEntity;
	}

	public PojoPropertyModel<?> documentIdSourceProperty() {
		return documentIdSourceProperty;
	}

}
