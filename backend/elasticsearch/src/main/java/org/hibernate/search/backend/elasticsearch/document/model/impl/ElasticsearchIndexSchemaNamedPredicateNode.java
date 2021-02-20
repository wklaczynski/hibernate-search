/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.backend.elasticsearch.document.model.impl;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Map;
import org.hibernate.search.backend.elasticsearch.logging.impl.Log;

import org.hibernate.search.engine.reporting.spi.EventContexts;
import org.hibernate.search.util.common.reporting.EventContext;
import org.hibernate.search.util.common.logging.impl.LoggerFactory;

public class ElasticsearchIndexSchemaNamedPredicateNode<F> {

	private static final Log log = LoggerFactory.make( Log.class, MethodHandles.lookup() );

	private final ElasticsearchIndexSchemaObjectNode parent;
	private final String parentDocumentPath;
	private final String relativeFilterName;
	private final String absoluteFilterPath;
	private final List<String> nestedPathHierarchy;

	private final F factory;
	private final Map<String, Object> params;

	public ElasticsearchIndexSchemaNamedPredicateNode(ElasticsearchIndexSchemaObjectNode parent, String relativeNamedPredicateName,
		F factory, Map<String, Object> params) {
		this.parent = parent;
		this.relativeFilterName = relativeNamedPredicateName;
		this.parentDocumentPath = parent.absolutePath();
		this.absoluteFilterPath = parent.absolutePath( relativeNamedPredicateName );
		this.nestedPathHierarchy = parent.nestedPathHierarchy();
		this.factory = factory;
		this.params = params;
	}

	public ElasticsearchIndexSchemaObjectNode parent() {
		return parent;
	}

	public String parentDocumentPath() {
		return parentDocumentPath;
	}

	public String nestedDocumentPath() {
		return (nestedPathHierarchy.isEmpty()) ? null
			: nestedPathHierarchy.get( nestedPathHierarchy.size() - 1 );
	}

	public List<String> nestedPathHierarchy() {
		return nestedPathHierarchy;
	}

	public String absoluteFilterPath() {
		return absoluteFilterPath;
	}

	public Map<String, Object> params() {
		return params;
	}

	public F getFactory() {
		return factory;
	}

	@Override
	public String toString() {
		String sb = getClass().getSimpleName() + "[" + "parent=" + parent + ", relativeFieldName=" + relativeFilterName + "]";
		return sb;
	}

	private EventContext getEventContext() {
		return EventContexts.fromIndexFieldAbsolutePath( absoluteFilterPath );
	}
}
