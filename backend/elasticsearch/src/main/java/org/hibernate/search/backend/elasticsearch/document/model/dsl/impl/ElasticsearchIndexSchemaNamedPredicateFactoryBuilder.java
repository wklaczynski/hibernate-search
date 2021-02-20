/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.backend.elasticsearch.document.model.dsl.impl;

import java.lang.invoke.MethodHandles;
import java.util.LinkedHashMap;
import java.util.Map;
import org.hibernate.search.backend.elasticsearch.document.model.impl.AbstractElasticsearchIndexSchemaFieldNode;
import org.hibernate.search.backend.elasticsearch.document.model.impl.ElasticsearchIndexSchemaNamedPredicateNode;
import org.hibernate.search.backend.elasticsearch.document.model.impl.ElasticsearchIndexSchemaNodeCollector;
import org.hibernate.search.backend.elasticsearch.document.model.impl.ElasticsearchIndexSchemaNodeContributor;
import org.hibernate.search.backend.elasticsearch.document.model.impl.ElasticsearchIndexSchemaObjectNode;
import org.hibernate.search.backend.elasticsearch.logging.impl.Log;
import org.hibernate.search.backend.elasticsearch.lowlevel.index.mapping.impl.AbstractTypeMapping;
import org.hibernate.search.engine.backend.common.spi.FieldPaths;
import org.hibernate.search.engine.backend.document.model.dsl.spi.IndexSchemaBuildContext;
import org.hibernate.search.engine.backend.document.model.spi.IndexFieldInclusion;
import org.hibernate.search.engine.reporting.spi.EventContexts;
import org.hibernate.search.util.common.logging.impl.LoggerFactory;
import org.hibernate.search.util.common.reporting.EventContext;
import org.hibernate.search.engine.backend.document.model.dsl.IndexSchemaNamedPredicateOptionsStep;

public class ElasticsearchIndexSchemaNamedPredicateFactoryBuilder<F> implements IndexSchemaNamedPredicateOptionsStep<ElasticsearchIndexSchemaNamedPredicateFactoryBuilder<F>>,
	ElasticsearchIndexSchemaNodeContributor, IndexSchemaBuildContext {
	private static final Log log = LoggerFactory.make( Log.class, MethodHandles.lookup() );

	private final AbstractElasticsearchIndexSchemaObjectNodeBuilder parent;
	private final String relativeFilterName;
	private final String absoluteFilterPath;
	private final Map<String, Object> params = new LinkedHashMap<>();
	private final F factory;

	ElasticsearchIndexSchemaNamedPredicateFactoryBuilder(AbstractElasticsearchIndexSchemaObjectNodeBuilder parent, String relativeFilterName,
		IndexFieldInclusion inclusion, F factory) {
		this.parent = parent;
		this.relativeFilterName = relativeFilterName;
		this.absoluteFilterPath = FieldPaths.compose( parent.getAbsolutePath(), relativeFilterName );
		this.factory = factory;
	}

	@Override
	public <T> ElasticsearchIndexSchemaNamedPredicateFactoryBuilder param(String name, T value) {
		this.params.put( name, value );
		return this;
	}

	@Override
	public ElasticsearchIndexSchemaNamedPredicateFactoryBuilder<F> params(Map<String, Object> params) {
		this.params.putAll( params );
		return this;
	}

	@Override
	public void contribute(ElasticsearchIndexSchemaNodeCollector collector,
		ElasticsearchIndexSchemaObjectNode parentNode,
		Map<String, AbstractElasticsearchIndexSchemaFieldNode> staticChildrenByNameForParent,
		AbstractTypeMapping parentMapping) {

		ElasticsearchIndexSchemaNamedPredicateNode<F> namedPredicateNode = new ElasticsearchIndexSchemaNamedPredicateNode<>(
			parentNode, relativeFilterName, factory, params
		);

		collector.collect( absoluteFilterPath, namedPredicateNode );
	}

	@Override
	public EventContext eventContext() {
		return parent.getRootNodeBuilder().getIndexEventContext()
			.append( EventContexts.fromIndexFieldAbsolutePath( absoluteFilterPath ) );
	}

}
