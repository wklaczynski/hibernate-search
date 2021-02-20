/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.integrationtest.backend.lucene.search;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.hibernate.search.engine.backend.document.IndexFieldReference;
import org.hibernate.search.engine.backend.document.model.dsl.IndexSchemaElement;
import org.hibernate.search.engine.backend.document.model.dsl.IndexSchemaObjectField;
import org.hibernate.search.engine.backend.types.ObjectStructure;
import org.hibernate.search.engine.search.predicate.SearchPredicate;
import org.hibernate.search.engine.search.predicate.factories.NamedPredicateFactory;
import org.hibernate.search.engine.search.predicate.factories.NamedPredicateFactoryContext;
import org.hibernate.search.integrationtest.backend.tck.testsupport.util.rule.SearchSetupHelper;
import org.hibernate.search.util.common.SearchException;
import org.hibernate.search.util.impl.integrationtest.mapper.stub.SimpleMappedIndex;
import org.hibernate.search.util.impl.integrationtest.mapper.stub.StubMappingScope;
import org.hibernate.search.util.impl.test.annotation.TestForIssue;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class LuceneNamedPredicateSearchPredicateIT {

	private static final String INDEX_NAME = "IndexName";

	@Rule
	public SearchSetupHelper setupHelper = new SearchSetupHelper();

	private final SimpleMappedIndex<IndexBinding> index = SimpleMappedIndex.of( IndexBinding::new );

	@Before
	public void setup() {
		setupHelper.start().withIndex( index ).setup();
	}

	@Test
	@TestForIssue(jiraKey = "HSEARCH-3535")
	public void minimumShouldMatch_outOfBounds() {
		StubMappingScope scope = index.createScope();

		assertThatThrownBy(
			() -> scope.query()
				.where( f -> {
					return f.bool()
						.minimumShouldMatchNumber( 3 )
						.should( f.named( "match_fieldName" ).param( "match", "blablabla" ) )
						.should( f.named( "nested.match_fieldName" ).param( "match", "blablabla" ) );
				} ).toQuery(),
			"bool() predicate with a minimumShouldMatch constraint providing an out-of-bounds value"
		)
			.isInstanceOf( SearchException.class )
			.hasMessageContaining( "Computed minimum for minimumShouldMatch constraint is out of bounds" );
	}

	private static class IndexBinding {
		final IndexFieldReference<String> field;
		final IndexFieldReference<String> nested_field;

		IndexBinding(IndexSchemaElement root) {
			root.namedPredicate( "match_fieldName", new TestFilterFactory() )
				.param( "test", 2 );

			field = root.field( "fieldName", c -> c.asString() ).toReference();

			IndexSchemaObjectField nest = root.objectField(
				"nested", ObjectStructure.NESTED ).multiValued();

			nest.namedPredicate( "match_fieldName", new TestFilterFactory() )
				.param( "other_value", "blablabla" );

			nested_field = nest.field( "fieldName", c -> c.asString() ).toReference();

			nest.toReference();
		}
	}

	public static class TestFilterFactory implements NamedPredicateFactory {

		@Override
		public SearchPredicate create(NamedPredicateFactoryContext ctx) {

			SearchPredicate filter;
			String nestedPath = ctx.nestedPath();
			String fieldPath = ctx.resolvePath( "fieldName" );
			if ( nestedPath != null ) {
				filter = ctx.predicate().nested()
					.objectField( nestedPath )
					.nest( f -> f
					.match().field( fieldPath )
					.matching( ctx.param( "match" ) ) )
					.toPredicate();
			}
			else {
				filter = ctx.predicate()
					.match().field( fieldPath )
					.matching( ctx.param( "match" ) )
					.toPredicate();
			}

			return filter;
		}
	}

}
