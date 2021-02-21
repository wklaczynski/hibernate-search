/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */

package org.hibernate.search.mapper.javabean.log.impl;

import java.util.Collection;
import java.util.List;
import org.hibernate.search.engine.environment.bean.spi.BeanProvider;
import org.hibernate.search.engine.search.loading.spi.EntityLoadingStrategy;
import org.hibernate.search.mapper.javabean.common.EntityReference;
import org.hibernate.search.util.common.SearchException;
import org.hibernate.search.util.common.logging.impl.MessageConstants;
import org.hibernate.search.util.common.logging.impl.ClassFormatter;

import org.jboss.logging.BasicLogger;
import org.jboss.logging.annotations.Cause;
import org.jboss.logging.annotations.FormatWith;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;
import org.jboss.logging.annotations.ValidIdRange;
import org.hibernate.search.mapper.javabean.loading.spi.JavaBeanEntityLoadingStrategy;
import org.jboss.logging.Logger;
import static org.jboss.logging.Logger.Level.ERROR;
import static org.jboss.logging.Logger.Level.INFO;
import static org.jboss.logging.Logger.Level.WARN;
import org.jboss.logging.annotations.LogMessage;
import org.hibernate.search.mapper.javabean.massindexing.spi.MassIndexingTypeLoader;

@MessageLogger(projectCode = MessageConstants.PROJECT_CODE)
@ValidIdRange(min = MessageConstants.MAPPER_JAVABEAN_ID_RANGE_MIN, max = MessageConstants.MAPPER_JAVABEAN_ID_RANGE_MAX)
public interface Log extends BasicLogger {

	int ID_OFFSET = MessageConstants.MAPPER_JAVABEAN_ID_RANGE_MIN;

	/*
	 * This is not an exception factory nor a logging statement.
	 * The returned string is passed to the FailureHandler,
	 * which is not necessarily using a logger but we still
	 * want to internationalize the message.
	 */
	@Message(value = "MassIndexer operation")
	String massIndexerOperation();

	@Message(value = "Indexing instance of entity '%s' during mass indexing")
	String massIndexerIndexingInstance(String entityName);

	@Message(value = "Fetching identifiers of entities to index for entity '%s' during mass indexing")
	String massIndexerFetchingIds(String entityName);

	@Message(value = "Loading and extracting entity data for entity '%s' during mass indexing")
	String massIndexingLoadingAndExtractingEntityData(String entityName);

	@Message(id = ID_OFFSET + 2,
			value = "Unexpected entity name for a query hit: '%1$s'. Expected one of %2$s.")
	SearchException unexpectedSearchHitEntityName(String entityName, Collection<String> expectedNames);

	@Message(id = ID_OFFSET + 3, value = "Unable to retrieve type model for class '%1$s'.")
	SearchException errorRetrievingTypeModel(@FormatWith(ClassFormatter.class) Class<?> clazz, @Cause Exception cause);

	@Message(id = ID_OFFSET + 5,
			value = "Unable to load the entity instance corresponding to document '%1$s':"
					+ " the JavaBean mapper does not haw defined entity loading."
					+ " There is probably an entity projection in the query definition: it should be removed."
	)
	SearchException cannotLoadEntity(EntityReference reference);

	@Message(id = ID_OFFSET + 6, value = "Multiple entity types configured with the same name '%1$s': '%2$s', '%3$s'")
	SearchException multipleEntityTypesWithSameName(String entityName, Class<?> previousType, Class<?> type);

	@Message(id = ID_OFFSET + 7,
			value = "Type with name '%1$s' does not exist: the JavaBean mapper does not support named types."
	)
	SearchException namedTypesNotSupported(String name);

	@Message(id = ID_OFFSET + 8,
			value = "Unable to configure entity loading:"
					+ " the JavaBean mapper does not support entity loading."
	)
	SearchException entityLoadingConfigurationNotSupported();

	@Message(id = ID_OFFSET + 9, value = "Type '%1$s' is not an entity type, or this entity type is not indexed.")
	SearchException notIndexedEntityType(@FormatWith(ClassFormatter.class) Class<?> type);

	@Message(id = ID_OFFSET + 10, value = "Entity type '%1$s' does not exist or is not indexed.")
	SearchException notIndexedEntityName(String name);

	@Message(id = ID_OFFSET + 11,
			value = "Invalid String value for the bean provider: '%s'. The bean provider must be an instance of '%s'.")
	SearchException invalidStringForBeanProvider(String value, Class<BeanProvider> expectedType);

	@Message(id = ID_OFFSET + 18,
			value = "Invalid automatic indexing synchronization strategy name: '%1$s'. Valid names are: %2$s.")
	SearchException invalidAutomaticIndexingSynchronizationStrategyName(String invalidRepresentation, List<String> validRepresentations);

	@Message(id = ID_OFFSET + 19,
			value = "Invalid String value for the entity loading strategy: '%s'. The entity loading strategy must be an instance of '%s'.")
	SearchException invalidStringForEntityLoadingStrategy(String value, Class<JavaBeanEntityLoadingStrategy> expectedType);

	@Message(id = ID_OFFSET + 20, value = "Multiple loading entity strategy configured with the same name '%1$s': '%2$s', '%3$s'")
	SearchException multipleEntityLoadingStrategyWithSameName(String entityName, Class<?> previousType, Class<?> type);

	@Message(id = ID_OFFSET + 21,
			value = "Invalid String value for the entity loader strategy: '%s'. The entity loader strategy must be an instance of '%s'.")
	SearchException invalidStringEntityLoadingStrategy(String value, Class<JavaBeanEntityLoadingStrategy> expectedType);

	@Message(id = ID_OFFSET + 22,
			value = "Unable to resolve entity loader strategy:"
					+ " configuration property '%1$s' is not set, and there isn't any entity loader strategy in the classpath."
					+ " Check that you added the desired backend to your project's dependencies.")
	SearchException noEntityLoadingStrategyRegistered(String propertyKey);

	@Message(id = ID_OFFSET + 23,
			value = "Ambiguous entity loader strategy type:"
					+ " configuration property '%1$s' is not set, and multiple entity loader strategy types are present in the classpath."
					+ " Set property '%1$s' to one of the following to select the entity loader strategy type: %2$s")
	SearchException multipleEntityLoadingStrategyRegistered(String propertyKey, Collection<String> loadingTypeNames);

	@Message(id = ID_OFFSET + 24,
			value = "Unable to load the mas indexing instance corresponding to entity loader strategy '%1$s':"
					+ " the entity loading strategy does not have defined entity loading mass indexing interface."
					+ " There is probably an entity projection in the query definition: it should be removed."
	)
	SearchException cannotLoadMassIndexingStrategyImpementationNotDefined(EntityLoadingStrategy loadingStrategy);

	@Message(id = ID_OFFSET + 25,
			value = "Unable to load the mas indexing instance corresponding to entity loader strategy '%1$s':"
					+ " the entity loading strategy does not have defined entity loading mass indexing loader."
					+ " There is probably an entity projection in the query definition: it should be removed."
	)
	SearchException cannotLoadMassIndexingStrategyNotDefined(MassIndexingTypeLoader loader);

	@Message(id = ID_OFFSET + 26, value = "Mass indexing received interrupt signal. The index is left in an unknown state!")
	SearchException massIndexingThreadInterrupted(@Cause InterruptedException e);

	@LogMessage(level = Logger.Level.ERROR)
	@Message(id = ID_OFFSET + 27,
			value = "The mass indexing failure handler threw an exception while handling a previous failure."
					+ " The failure may not have been reported.")
	void failureInMassIndexingFailureHandler(@Cause Throwable t);

	@LogMessage(level = INFO)
	@Message(id = ID_OFFSET + 28, value = "Mass indexing is going to index %d entities.")
	void indexingEntities(long count);

	@LogMessage(level = INFO)
	@Message(id = ID_OFFSET + 29, value = "Mass indexing complete. Indexed %1$d entities.")
	void indexingEntitiesCompleted(long nbrOfEntities);

	@LogMessage(level = INFO)
	@Message(id = ID_OFFSET + 30, value = "Mass indexing progress: indexed %1$d entities in %2$d ms.")
	void indexingProgressRaw(long doneCount, long elapsedMs);

	@LogMessage(level = INFO)
	@Message(id = ID_OFFSET + 31, value = "Mass indexing progress: %2$.2f%% [%1$f documents/second].")
	void indexingProgressStats(float estimateSpeed, float estimatePercentileComplete);

	@LogMessage(level = ERROR)
	@Message(id = ID_OFFSET + 32, value = "Mass indexing received interrupt signal: aborting.")
	void interruptedBatchIndexing();

	@Message(id = ID_OFFSET + 33, value = "%1$s entities could not be indexed. See the logs for details."
			+ " First failure on entity '%2$s': %3$s")
	SearchException massIndexingEntityFailures(long finalFailureCount,
			EntityReference firstFailureEntity, String firstFailureMessage,
			@Cause Throwable firstFailure);

	@LogMessage(level = WARN)
	@Message(id = ID_OFFSET + 34, value = "Unable to guess the transaction status: not starting a JTA transaction.")
	void cannotGuessTransactionStatus(@Cause Exception e);

	@LogMessage(level = ERROR)
	@Message(id = ID_OFFSET + 35, value = "Transaction rollback failure: %1$s")
	void errorRollingBackTransaction(String message, @Cause Exception e1);

	@Message(id = ID_OFFSET + 36, value = "Unable to handle transaction: %1$s")
	SearchException massIndexingTransactionHandlingException(String causeMessage, @Cause Throwable cause);
}
