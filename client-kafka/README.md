# Kafka project skeleton

## About this project

### Exception handling

Since default exception handles for batch doesn't support Not Retriable Exceptions (like it is possible for non batch listeners) the following handler was introduced:

```java
public class NREBatchErrorHandler extends SeekToCurrentBatchErrorHandler {

  // todo add more fatal exceptions here like ConstaintViolation etc
  private final static List<Class<?>> NOT_RETRIABLE_EXCEPTIONS =
      List.of(NotRetriableException.class);
```

### Tests

For the listener testing `spring-kafka-test` package was used with @EmbeddedKafka [MessageListenerTest.java](src/test/java/com/spimenov/buildingblocks/kafka/listener/MessageListenerTest.java)

```java
@EmbeddedKafka(topics = {"${kafka.consumer.topic-name}"}, partitions = 1,
    bootstrapServersProperty = "spring.kafka.bootstrap-servers")
public class MessageListenerTest {
  
}
```

Test scenarios covers success and common failure cases

| Test name | Description |
| --------- | ----------- |
| shouldHandleBatchOfEvents() | Checks success case. Batch of 10 events is processed |
| shouldSkipEventsWithEmptyMessages() | Verifies logic from EmptyMessagesFilterStrategy about discarding messages with not applicable data |
| shouldHandleErrorAsRetry() | Verifies retry is applied when runtime exception happens within listener code |
| shouldSkipFatalErrorWithoutRetryAndProceedWithNextBatch() | Verifies that exception included to the set of Not Retriable Exceptions doesn't lead to retry and the whole batch is discarded |

## Running the application 

1. Run kafka within docker
```shell script 
docker-compose up
```

2. Create topic 
```shell script
kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 2 --topic msg-test
```

3. Run the listener
```shell script
./gradlew client-kafka:bootRun
```

4. Send test message
```shell script
kafka-console-producer --broker-list localhost:9092 --topic msg-topic
> {"messages": [{"messageId": 1, "payload": "string"}, {"messageId": 2, "payload": "string2"}]}
```

## Benchmarking
