/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.mapper.pojo.mapping.definition.annotation.processing.impl;

import java.lang.invoke.MethodHandles;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import org.hibernate.search.engine.environment.bean.BeanReference;
import org.hibernate.search.engine.search.predicate.factories.NamedPredicateFactory;
import org.hibernate.search.mapper.pojo.logging.impl.Log;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.processing.TypeMappingAnnotationProcessor;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.processing.TypeMappingAnnotationProcessorContext;
import org.hibernate.search.mapper.pojo.mapping.definition.programmatic.TypeMappingStep;
import org.hibernate.search.util.common.logging.impl.LoggerFactory;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.processing.MappingAnnotationProcessorContext;
import org.hibernate.search.mapper.pojo.bridge.mapping.annotation.NamedPredicateFactoryRef;
import org.hibernate.search.mapper.pojo.bridge.mapping.programmatic.TypeBinder;
import org.hibernate.search.mapper.pojo.mapping.building.impl.NamedPredicateTypeBinder;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.NamedPredicateParam;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.NamedPredicate;

public final class NamedPredicateProcessor implements TypeMappingAnnotationProcessor<NamedPredicate> {

	private static final Log log = LoggerFactory.make( Log.class, MethodHandles.lookup() );

	@Override
	public void process(TypeMappingStep mapping, NamedPredicate annotation,
			TypeMappingAnnotationProcessorContext context) {
		String name = annotation.name();

		Map<String, Object> filterParams = new LinkedHashMap<>();
		NamedPredicateParam[] params = annotation.params();
		if ( params != null ) {
			for ( NamedPredicateParam param : params ) {
				filterParams.put( param.name(), param.value() );
			}
		}

		BeanReference<? extends NamedPredicateFactory> factoryReference = createFactoryReference( annotation.factory(), context );

		TypeBinder binder = new NamedPredicateTypeBinder( name, factoryReference, filterParams );
		mapping.binder( binder );
	}

	private BeanReference<? extends NamedPredicateFactory> createFactoryReference(NamedPredicateFactoryRef factoryReferenceAnnotation,
			MappingAnnotationProcessorContext context) {

		Optional<BeanReference<? extends NamedPredicateFactory>> factoryReference = context.toBeanReference(
				NamedPredicateFactory.class,
				NamedPredicateFactoryRef.PojoUndefinedFactoryImplementationType.class,
				factoryReferenceAnnotation.type(), factoryReferenceAnnotation.name(),
				factoryReferenceAnnotation.retrieval()
		);

		if ( !factoryReference.isPresent() ) {
			throw log.missingFactoryReferenceInBinding();
		}

		return factoryReference.get();
	}

}
