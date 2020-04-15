/*
 * Copyright (C) 2020 adidas AG.
 */

package com.spimenov.buildingblocks.kafka.listener;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spimenov.buildingblocks.config.KafkaConsumerConfiguration;
import com.spimenov.buildingblocks.exception.NotRetriableException;
import com.spimenov.buildingblocks.kafka.listener.MessageEvent.Message;
import com.spimenov.buildingblocks.service.TestService;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.support.JacksonUtils;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * @author Stanislav Pimenov
 */

@SpringBootTest(classes = {MessageListener.class, KafkaConsumerConfiguration.class})
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@EmbeddedKafka(topics = {"${kafka.consumer.topic-name}"}, partitions = 1,
    bootstrapServersProperty = "spring.kafka.bootstrap-servers")
@ActiveProfiles("test-consumer")
public class MessageListenerTest {

  @Value("${kafka.consumer.topic-name}")
  public String topic;

  @Autowired
  private EmbeddedKafkaBroker embeddedKafka;

  @Autowired
  private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

  @Autowired
  private MessageListener sut;

  @MockBean
  private TestService testService;

  private KafkaTemplate<String, MessageEvent> kafkaTemplate;

  private ObjectMapper objectMapper = JacksonUtils.enhancedObjectMapper();

  @BeforeEach
  public void setUp() {

    // set up the Kafka producer properties
    Map<String, Object> senderProperties =
        KafkaTestUtils.producerProps(embeddedKafka.getBrokersAsString());

    // create a Kafka producer factory
    ProducerFactory<String, MessageEvent> producerFactory =
        new DefaultKafkaProducerFactory<>(senderProperties, new StringSerializer(),
            new JsonSerializer<>(objectMapper));

    // create a Kafka template
    kafkaTemplate = new KafkaTemplate<>(producerFactory);
    // set the default topic to send to
    kafkaTemplate.setDefaultTopic(topic);

    // wait until the partitions are assigned
    for (MessageListenerContainer messageListenerContainer :
        kafkaListenerEndpointRegistry.getListenerContainers()) {
      ContainerTestUtils.waitForAssignment(messageListenerContainer,
          embeddedKafka.getPartitionsPerTopic());
    }
  }

  @Test
  void shouldHandleBatchOfEvents() throws InterruptedException {
    // given
    int batchSize = MessageListener.BATCH_SIZE;
    // when
    IntStream
        .range(0, batchSize)
        .forEach(counter -> {
          final MessageEvent messageEvent = new MessageEvent();
          messageEvent.setMessages(List.of(new Message(counter, "message body of " + counter)));
          kafkaTemplate.sendDefault(messageEvent);
        });
    sut
        .getLatch()
        .await(10000, TimeUnit.MILLISECONDS);
    // then
    assertThat(sut
        .getLatch()
        .getCount()).isEqualTo(0);
  }

  @Test
  void shouldSkipEventsWithEmptyMessages() throws InterruptedException {
    // given
    int batchSize = MessageListener.BATCH_SIZE;
    final MessageEvent messageEvent = new MessageEvent();
    messageEvent.setMessages(List.of(new Message(1, "message body")));
    kafkaTemplate.sendDefault(messageEvent);

    final MessageEvent messageEventToBeDiscarded = new MessageEvent();
    messageEventToBeDiscarded.setMessages(Collections.emptyList());
    // when
    kafkaTemplate.sendDefault(messageEventToBeDiscarded);
    sut
        .getLatch()
        .await(10000, TimeUnit.MILLISECONDS);
    // then
    assertThat(sut
        .getLatch()
        .getCount()).isEqualTo(batchSize - 1);
  }

  @Test
  void shouldHandleErrorAsRetry() throws InterruptedException {
    // given
    int batchSize = MessageListener.BATCH_SIZE;
    // when
    IntStream
        .range(0, batchSize)
        .forEach(counter -> {
          final MessageEvent messageEvent = new MessageEvent();
          messageEvent.setMessages(List.of(new Message(counter, "message body of " + counter)));
          kafkaTemplate.sendDefault(messageEvent);
        });
    doThrow(new RuntimeException("opps"))
        .doNothing()
        .when(testService)
        .doSmth(any());
    sut
        .getLatch()
        .await(10000, TimeUnit.MILLISECONDS);
    // then
    assertThat(sut
        .getLatch()
        .getCount()).isEqualTo(0);
    verify(testService, times(2)).doSmth(any());
  }

  @Test
  void shouldSkipFatalErrorWithoutRetryAndProceedWithNextBatch() throws InterruptedException {
    // given
    int batchSize = MessageListener.BATCH_SIZE;
    // when
    IntStream
        .range(0, batchSize + 2)
        .forEach(counter -> {
          final MessageEvent messageEvent = new MessageEvent();
          messageEvent.setMessages(List.of(new Message(counter, "message body of " + counter)));
          kafkaTemplate.sendDefault(messageEvent);
        });
    doThrow(new NotRetriableException())
        .doNothing()
        .when(testService)
        .doSmth(any());
    sut
        .getLatch()
        .await(10000, TimeUnit.MILLISECONDS);
    // then
    assertThat(sut
        .getLatch()
        .getCount()).isEqualTo(8);
    //verify(testService, times(2)).doSmth(any());
  }
}
