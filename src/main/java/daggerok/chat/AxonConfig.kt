package daggerok.chat

import com.mongodb.MongoClient
import daggerok.AxonApp
import org.axonframework.commandhandling.model.Repository
import org.axonframework.eventsourcing.EventCountSnapshotTriggerDefinition
import org.axonframework.eventsourcing.EventSourcingRepository
import org.axonframework.eventsourcing.Snapshotter
import org.axonframework.eventsourcing.eventstore.EventStorageEngine
import org.axonframework.eventsourcing.eventstore.EventStore
import org.axonframework.mongo.DefaultMongoTemplate
import org.axonframework.mongo.MongoTemplate
import org.axonframework.mongo.eventsourcing.eventstore.MongoEventStorageEngine
import org.axonframework.mongo.eventsourcing.eventstore.documentpercommit.DocumentPerCommitStorageStrategy
import org.axonframework.mongo.eventsourcing.eventstore.documentperevent.DocumentPerEventStorageStrategy
import org.axonframework.serialization.Serializer
import org.axonframework.serialization.json.JacksonSerializer
import org.axonframework.spring.eventsourcing.SpringAggregateSnapshotterFactoryBean
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan(basePackageClasses = [AxonApp::class])
class AxonConfig {

  companion object {
    const val amount = 4
  }

//  @Bean
//  fun snapshotterFactoryBean() = SpringAggregateSnapshotterFactoryBean()
//
//  @Bean("axonChatRepository")
//  fun axonChatSnapshotterRepository(eventStore: EventStore, snapshotter: Snapshotter): Repository<ChatAggregator> =
//      EventSourcingRepository(ChatAggregator::class.java, eventStore, EventCountSnapshotTriggerDefinition(snapshotter, amount))

  @Bean("axonChatRepository")
  fun axonChatRepository(eventStore: EventStore): Repository<ChatAggregator> =
      EventSourcingRepository(ChatAggregator::class.java, eventStore)

  @Bean
  fun eventStorageEngine(serializer: Serializer, axonMongoTemplate: MongoTemplate): EventStorageEngine = MongoEventStorageEngine(
      serializer, null, axonMongoTemplate, DocumentPerEventStorageStrategy())
//      serializer, null, amount, axonMongoTemplate, DocumentPerCommitStorageStrategy())

  @Bean
  fun serializer(): Serializer = JacksonSerializer()

  @Bean("axonMongoTemplate")
  fun axonMongoTemplate(mongoClient: MongoClient, @Value("\${spring.datasource.name}") name: String): MongoTemplate =
      DefaultMongoTemplate(mongoClient, name)
          .withDomainEventsCollection("events")
          .withSnapshotCollection("snapshots")
}
