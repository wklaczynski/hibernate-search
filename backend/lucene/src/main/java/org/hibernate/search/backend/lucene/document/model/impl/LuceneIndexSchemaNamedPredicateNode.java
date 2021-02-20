/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.backend.lucene.document.model.impl;

import java.util.List;
import java.util.Map;

public class LuceneIndexSchemaNamedPredicateNode<F> {

	private final LuceneIndexSchemaObjectNode parent;
	private final String parentDocumentPath;
	private final String relativeNamedPredicateName;
	private final String absoluteNamedPredicatePath;
	private final List<String> nestedPathHierarchy;
	private String nestedDocumentPath;

	private final F factory;
	private final Map<String, Object> params;

	public LuceneIndexSchemaNamedPredicateNode(LuceneIndexSchemaObjectNode parent, String relativeNamedPredicateName,
			F factory, Map<String, Object> params) {
		this.parent = parent;
		this.relativeNamedPredicateName = relativeNamedPredicateName;
		this.parentDocumentPath = parent.absolutePath();
		this.absoluteNamedPredicatePath = parent.absolutePath( relativeNamedPredicateName );
		this.nestedPathHierarchy = parent.nestedPathHierarchy();
		this.factory = factory;
		this.params = params;

		if ( !nestedPathHierarchy.isEmpty() ) {
			nestedDocumentPath = nestedPathHierarchy
					.get( nestedPathHierarchy.size() - 1 );
		}

	}

	public LuceneIndexSchemaObjectNode parent() {
		return parent;
	}

	public String parentDocumentPath() {
		return parentDocumentPath;
	}

	public String nestedDocumentPath() {
		return nestedDocumentPath;
	}

	public List<String> nestedPathHierarchy() {
		return nestedPathHierarchy;
	}

	public String absoluteFilterPath() {
		return absoluteNamedPredicatePath;
	}

	public Map<String, Object> params() {
		return params;
	}

	public F factory() {
		return factory;
	}

	@Override
	public String toString() {
		String sb = getClass().getSimpleName() + "[" + "parent=" + parent + ", relativeFieldName=" + relativeNamedPredicateName + "]";
		return sb;
	}

}
