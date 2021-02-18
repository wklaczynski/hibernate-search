/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.mapper.javabean.loading.spi;

import java.util.List;
import java.util.Map;
import org.hibernate.search.engine.common.timing.spi.Deadline;
import org.hibernate.search.engine.search.loading.spi.EntityLoader;
import org.hibernate.search.mapper.javabean.common.EntityReference;

public interface JavaBeanEntityLoader<E> extends EntityLoader<EntityReference, E> {

	@Override
	void loadBlocking(List<EntityReference> references, Map<EntityReference, E> entitymap, Deadline deadline);

}
