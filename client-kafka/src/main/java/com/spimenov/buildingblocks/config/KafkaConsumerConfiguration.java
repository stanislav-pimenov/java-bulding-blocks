package com.spimenov.buildingblocks.config;

import com.spimenov.buildingblocks.exception.NREBatchErrorHandler;
import com.spimenov.buildingblocks.kafka.filter.EmptyMessagesFilterStrategy;
import com.spimenov.buildingblocks.kafka.listener.MessageEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.BatchErrorHandler;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer2;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.Map;

/**
 * @author Stanislav Pimenov
 */

@Configuration
@EnableKafka
@EnableConfigurationProperties(KafkaProperties.class)
@Slf4j
public class KafkaConsumerConfiguration {

  /**
   * Kafka container listener configuration.
   *
   * @param kafkaProperties application properties spring.kafka
   * @return kafka container listener
   */
  @Bean
  public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, MessageEvent>> kafkaListenerContainerFactory(
      KafkaProperties kafkaProperties) {

    log.info("Starting Kafka consumer with properties [{}]",
        kafkaProperties.buildConsumerProperties());

    ConcurrentKafkaListenerContainerFactory<String, MessageEvent> factory =
        new ConcurrentKafkaListenerContainerFactory<>();

    // set batch listener AckMode.BATCH will be used
    factory.setBatchListener(true);
    factory.setConsumerFactory(consumerFactory(kafkaProperties));
    factory.setRecordFilterStrategy(new EmptyMessagesFilterStrategy());
    factory.setAckDiscarded(true);
    factory.setBatchErrorHandler(errorHandler());

    return factory;
  }

  @Bean
  public DefaultKafkaConsumerFactory<String, MessageEvent> consumerFactory(KafkaProperties kafkaProperties) {
    Map<String, Object> configs = kafkaProperties.buildConsumerProperties();
    return new DefaultKafkaConsumerFactory<>(configs, new StringDeserializer(),
        new ErrorHandlingDeserializer2<>(new JsonDeserializer<>(MessageEvent.class)));
  }

  /**
   * Kafka listener error handler.
   *
   * @return kafka error handler
   */
  @Bean
  public BatchErrorHandler errorHandler() {
    final NREBatchErrorHandler errorHandler = new NREBatchErrorHandler();
    errorHandler.setBackOff(new FixedBackOff(1000, 5));
    return errorHandler;
  }
}
