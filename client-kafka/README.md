# Kafka project skeleton

## Adout this project

### Exception handling

Since default exception handles for batch doesn't support Not Retriable Exceptions (like it is possible for non batch listeners) the following handler was introduced:

```java
public class NREBatchErrorHandler extends SeekToCurrentBatchErrorHandler {

  // todo add more fatal exceptions here like ConstaintViolation etc
  private final static List<Class<?>> NOT_RETRIABLE_EXCEPTIONS =
      List.of(NotRetriableException.class);
```

### Tests

For the listener testing `spring-kafka-test` package was used with @EmbeddedKafka
client-kafka/src/test/java/com/spimenov/buildingblocks/kafka/listener/MessageListenerTest.java
[MessageListenerTest.java](client-kafka/src/test/java/com/spimenov/buildingblocks/kafka/listener/MessageListenerTest.java)

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

## Benchmarking
