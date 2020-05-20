package com.spimenov.buildingblocks.exception;

import static java.util.Objects.nonNull;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.listener.SeekToCurrentBatchErrorHandler;

import java.util.List;

/**
 * @author Stanislav Pimenov
 */
public class NREBatchErrorHandler extends SeekToCurrentBatchErrorHandler {

  // todo add more fatal exceptions here like ConstaintViolation etc
  private final static List<Class<?>> NOT_RETRIABLE_EXCEPTIONS =
      List.of(NotRetriableException.class);

  public NREBatchErrorHandler() {
  }

  @Override
  public void handle(Exception thrownException,
      ConsumerRecords<?, ?> data,
      Consumer<?, ?> consumer,
      MessageListenerContainer container) {

    if (nonNull(thrownException.getCause()) && NOT_RETRIABLE_EXCEPTIONS
        .stream()
        .anyMatch(nte -> nte.isAssignableFrom(thrownException
            .getCause()
            .getClass()))) {
      throw new KafkaException("Seek to current after exception", thrownException);
    } else {
      super.handle(thrownException, data, consumer, container);
    }
  }
}
