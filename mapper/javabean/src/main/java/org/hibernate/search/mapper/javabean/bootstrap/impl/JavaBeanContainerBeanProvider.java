/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.mapper.javabean.bootstrap.impl;

import java.lang.invoke.MethodHandles;

import org.hibernate.search.engine.environment.bean.BeanHolder;
import org.hibernate.search.engine.environment.bean.spi.BeanProvider;
import org.hibernate.search.mapper.javabean.log.impl.Log;
import org.hibernate.search.util.common.logging.impl.LoggerFactory;

/**
 * A {@link BeanProvider} relying on a JavaBean {@link BeanContainer} to resolve beans.
 */
public final class JavaBeanContainerBeanProvider implements BeanProvider {

	private static final Log log = LoggerFactory.make( Log.class, MethodHandles.lookup() );


	public JavaBeanContainerBeanProvider() {
	}

	@Override
	public void close() {
		// Nothing to do
	}

	@Override
	@SuppressWarnings("UseSpecificCatch")
	public <T> BeanHolder<T> forType(Class<T> typeReference) {
		try {
			return BeanHolder.of( typeReference.getConstructor( ).newInstance( ) );
		}
		catch (Exception ex) {
			log.debugf( ex, "Error resolving bean of type [%s] - using fallback", typeReference );
			RuntimeException th = new RuntimeException();
			th.addSuppressed( th );
			throw th;
		}
	}

	@Override
	@SuppressWarnings("UseSpecificCatch")
	public <T> BeanHolder<T> forTypeAndName(Class<T> typeReference, String nameReference) {
		try {
			return BeanHolder.of( typeReference.getConstructor( ).newInstance( ) );
		}
		catch (Exception ex) {
			log.debugf( ex, "Error resolving bean of type [%s] - using fallback", typeReference );
			RuntimeException th = new RuntimeException();
			th.addSuppressed( th );
			throw th;
		}
	}

}
