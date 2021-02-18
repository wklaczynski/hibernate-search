/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.engine.cfg;

import static java.lang.String.join;
import org.hibernate.search.engine.search.loading.spi.EntityLoadingFactory;

/**
 * Configuration properties common to all Hibernate Search backends regardless of the underlying technology.
 * <p>
 * Constants in this class are to be appended to a prefix to form a property key.
 * The exact prefix will depend on the integration, but should generally look like
 * "{@code hibernate.search.backends.<backend name>.}".
 */
public final class EntityLoadingSettings {

	private EntityLoadingSettings() {
	}

	/**
	 * The type of the entity loading factory.
	 * <p>
	 * Only useful if you have more than one loading factory technology in the classpath;
	 * otherwise the entity loading factory type is automatically detected.
	 * <p>
	 * Expects a instance, such as {@link EntityLoadingFactory}.
	 * See the documentation of your entity loading factory to find the appropriate value.
	 * <p>
	 * No default: this property must be set.
	 */
	public static final String TYPE = "type";

	/**
	 * The beckent of the entity loading factory.
	 * <p>
	 * Only useful if you have more than one backend technology in the classpath;
	 * otherwise the entity loading strategy type is automatically detected.
	 * <p>
	 * Expects a instance, such as {@link EntityLoadingFactory}.
	 * See the documentation of your entity loading strategy to find the appropriate value.
	 * <p>
	 * No default: this property must be set.
	 */
	public static final String BECKEND = "beckend";

	/**
	 * Builds a configuration property key for the default entity loading strategy, with the given radical.
	 * <p>
	 * See the javadoc of your entity loading strategy for available radicals.
	 * </p>
	 * Example result: "{@code hibernate.search.loading.thread_pool.size}"
	 *
	 * @param radical The radical of the configuration property (see constants in
	 * 	 * {@code CustomLoadingSettings}, etc.)
	 * @return the concatenated prefix + radical
	 */
	public static String loadingKey(String radical) {
		return join( ".", EngineSettings.LOADING, radical );
	}

	/**
	 * Builds a configuration property key for the given loading, with the given radical.
	 * <p>
	 * See the javadoc of your backend for available entity loadings factories.
	 * </p>
	 * Example result: "{@code hibernate.search.loadings.myLoading.thread_pool.size}"
	 *
	 * @param loadingName The name of the entity loading to configure.
	 * @param radical The radical of the configuration property (see constants in
	 * 	 * {@code CustomLoadingSettings}, etc.)
	 * @return the concatenated prefix + backend name + radical
	 */
	public static String loadingKey(String loadingName, String radical) {
		if ( loadingName == null ) {
			return loadingKey( radical );
		}
		return join( ".", EngineSettings.LOADINGS, loadingName, radical );
	}
}
