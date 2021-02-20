/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.backend.elasticsearch.search.predicate.impl;

import java.util.Map;

import org.hibernate.search.backend.elasticsearch.search.impl.ElasticsearchSearchContext;
import org.hibernate.search.engine.search.predicate.SearchPredicate;

import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.LinkedHashMap;
import org.hibernate.search.backend.elasticsearch.document.model.impl.ElasticsearchIndexSchemaNamedPredicateNode;
import org.hibernate.search.backend.elasticsearch.search.impl.ElasticsearchSearchIndexesContext;
import org.hibernate.search.engine.backend.common.spi.FieldPaths;
import org.hibernate.search.engine.search.predicate.dsl.SearchPredicateFactory;
import org.hibernate.search.engine.search.predicate.factories.NamedPredicateFactoryContext;
import org.hibernate.search.engine.search.predicate.factories.NamedPredicateFactory;
import org.hibernate.search.engine.search.predicate.spi.NamedPredicateBuilder;

class ElasticsearchNamedPredicate extends AbstractElasticsearchSingleFieldPredicate {

	private final ElasticsearchSearchPredicate buildPredicate;

	private ElasticsearchNamedPredicate(Builder builder) {
		super( builder );
		this.buildPredicate = builder.buildPredicate;
		// Ensure illegal attempts to mutate the predicate will fail
		builder.namedPredicate = null;
		builder.params = null;
		builder.buildPredicate = null;
	}

	@Override
	public void checkNestableWithin(String expectedParentNestedPath) {
		buildPredicate.checkNestableWithin( expectedParentNestedPath );
		super.checkNestableWithin( expectedParentNestedPath );
	}

	@Override
	protected JsonObject doToJsonQuery(PredicateRequestContext context,
			JsonObject outerObject, JsonObject innerObject) {
		return buildPredicate.toJsonQuery( context );
	}

	static class Builder extends AbstractBuilder implements NamedPredicateBuilder {
		private Map<String, Object> params = new LinkedHashMap<>();
		private ElasticsearchIndexSchemaNamedPredicateNode namedPredicate;
		private ElasticsearchSearchPredicate buildPredicate;
		private final SearchPredicateFactory predicateFactory;

		Builder(ElasticsearchSearchContext searchContext, SearchPredicateFactory predicateFactory,
				ElasticsearchSearchIndexesContext indexes, ElasticsearchIndexSchemaNamedPredicateNode filter, Object... attrs) {
			super( searchContext, filter.absoluteFilterPath(), filter.nestedPathHierarchy() );
			this.namedPredicate = filter;
			this.predicateFactory = predicateFactory;
		}

		@Override
		public void param(String name, Object value) {
			params.put( name, value );
		}

		@Override
		public SearchPredicate build() {
			Map<String, Object> computedParams = new LinkedHashMap<>();
			computedParams.putAll( params );
			computedParams.putAll( namedPredicate.params() );

			ElasticsearchNamedPredicateFactoryContext ctx = new ElasticsearchNamedPredicateFactoryContext( predicateFactory,
					namedPredicate.parentDocumentPath(),
					namedPredicate.nestedDocumentPath(),
					namedPredicate.absoluteFilterPath(),
					computedParams );

			NamedPredicateFactory namedPredicateFactory = (NamedPredicateFactory) namedPredicate.getFactory();

			buildPredicate = (ElasticsearchSearchPredicate) namedPredicateFactory.create( ctx );

			return new ElasticsearchNamedPredicate( this );
		}
	}

	public static class ElasticsearchNamedPredicateFactoryContext implements NamedPredicateFactoryContext {

		private final SearchPredicateFactory predicate;
		private final String nestedPath;
		private final Map<String, Object> params;
		private final String parentPath;
		private final String absolutePath;

		public ElasticsearchNamedPredicateFactoryContext(SearchPredicateFactory predicate,
				String parentPath, String nestedPath, String absolutePath,
				Map<String, Object> params) {
			this.predicate = predicate;
			this.nestedPath = nestedPath;
			this.parentPath = parentPath;
			this.absolutePath = absolutePath;
			this.params = params;
		}

		@Override
		public SearchPredicateFactory predicate() {
			return predicate;
		}

		@Override
		public Object param(String name) {
			return params.get( name );
		}

		@Override
		public Collection<String> paramNames() {
			return params.keySet();
		}

		@Override
		public String parentPath() {
			return parentPath;
		}

		@Override
		public String absolutePath() {
			return absolutePath;
		}

		@Override
		public String resolvePath(String relativeFieldName) {
			return FieldPaths.compose( parentPath, relativeFieldName );
		}

		@Override
		public String nestedPath() {
			return nestedPath;
		}
	}
}
