package daggerok.chat

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.*
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.annotation.PropertyAccessor.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.SerializationFeature.*
import com.mongodb.MongoClient
import daggerok.AxonApp
import org.axonframework.eventsourcing.*
import org.axonframework.eventsourcing.eventstore.EventStore
import org.axonframework.mongo.DefaultMongoTemplate
import org.axonframework.mongo.MongoTemplate
import org.axonframework.mongo.eventsourcing.eventstore.MongoEventStorageEngine
import org.axonframework.mongo.eventsourcing.eventstore.documentperevent.DocumentPerEventStorageStrategy
import org.axonframework.serialization.Serializer
import org.axonframework.serialization.json.JacksonSerializer
import org.axonframework.serialization.upcasting.event.NoOpEventUpcaster.INSTANCE
import org.axonframework.serialization.xml.XStreamSerializer
import org.axonframework.spring.eventsourcing.SpringAggregateSnapshotterFactoryBean
import org.axonframework.spring.eventsourcing.SpringPrototypeAggregateFactory
import org.springframework.beans.factory.annotation.Qualifier
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

  @Bean // required by snapshotTriggerDefinition
  fun springAggregateSnapshotterFactoryBean() = SpringAggregateSnapshotterFactoryBean()

  @Bean
  fun snapshotTriggerDefinition(snapshotter: Snapshotter): SnapshotTriggerDefinition =
      EventCountSnapshotTriggerDefinition(snapshotter, amount)

  @Bean // required by axonChatRepository
  fun axonChatAggregatorFactoryBean(): AggregateFactory<ChatAggregator> {
    val prototypeAggregateFactory = SpringPrototypeAggregateFactory<ChatAggregator>()
    prototypeAggregateFactory.setPrototypeBeanName("axonChatAggregator")
    return prototypeAggregateFactory
  }

//  @Bean
//  fun cacheManager(): CacheManager = CacheManager.getInstance()
//
//  @Bean
//  fun axonCache(cacheManager: CacheManager) = cacheManager.addCacheIfAbsent("axonCache")
//
//  @Bean
//  fun ehCache(cacheManager: CacheManager): EhCacheAdapter =
//      EhCacheAdapter(cacheManager.getCache("axonCache"))
//
//  @Bean("axonChatRepository")
//  fun axonChatRepository(axonChatAggregatorFactoryBean: AggregateFactory<ChatAggregator>, eventStore: EventStore,
//                         cache: Cache, snapshotTriggerDefinition: SnapshotTriggerDefinition): Repository<ChatAggregator> =
//      CachingEventSourcingRepository<ChatAggregator>(
//          axonChatAggregatorFactoryBean, eventStore, cache, snapshotTriggerDefinition)

  @Bean("axonChatRepository")
  fun axonChatRepository(axonChatAggregatorFactoryBean: AggregateFactory<ChatAggregator>, eventStore: EventStore,
                         snapshotTriggerDefinition: SnapshotTriggerDefinition)//: Repository<ChatAggregator> =
      = EventSourcingRepository<ChatAggregator>(axonChatAggregatorFactoryBean, eventStore, snapshotTriggerDefinition)

  @Bean
  fun objectMapper(): ObjectMapper {
    val objectMapper = ObjectMapper()
    objectMapper.setVisibility(FIELD, ANY)
    return objectMapper
  }

  @Bean // required by eventStorageEngine
  fun serializer(): Serializer = JacksonSerializer(objectMapper()) //XStreamSerializer()

  @Bean("axonMongoTemplate")
  fun axonMongoTemplate(mongoClient: MongoClient, @Value("\${spring.datasource.name}") name: String): MongoTemplate =
      DefaultMongoTemplate(mongoClient, name)
          .withDomainEventsCollection("events")
          .withSnapshotCollection("snapshots")

  @Bean
  fun eventStorageEngine(serializer: Serializer, @Qualifier("axonMongoTemplate") axonMongoTemplate: MongoTemplate) =
//////////      MongoEventStorageEngine(serializer, INSTANCE, axonMongoTemplate, DocumentPerEventStorageStrategy()) // with JacksonSerializer application failing after snapshot created!
////////      MongoEventStorageEngine(axonMongoTemplate) // XStreamSerializer, same as
//////      MongoEventStorageEngine(null, null, axonMongoTemplate, DocumentPerEventStorageStrategy()) // XStreamSerializer
////      MongoEventStorageEngine(serializer, INSTANCE, axonMongoTemplate, DocumentPerCommitStorageStrategy())
//      MongoEventStorageEngine(serializer, INSTANCE, amount, axonMongoTemplate, DocumentPerCommitStorageStrategy())
      MongoEventStorageEngine(serializer, INSTANCE, amount, axonMongoTemplate, DocumentPerEventStorageStrategy())
}
