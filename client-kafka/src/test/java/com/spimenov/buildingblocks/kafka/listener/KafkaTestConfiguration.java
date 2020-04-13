/*
 * Copyright (C) 2020 adidas AG.
 */

package com.spimenov.buildingblocks.kafka.listener;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;

/**
 * @author Stanislav Pimenov
 */
@Configuration
@ActiveProfiles("test-consumer")
public class KafkaTestConfiguration {

  @Value("${kafka.consumer.topic-name}")
  private String topic;

//  @Bean
//  public KafkaAdmin admin(KafkaProperties properties) {
//    Map<String, Object> configs = new HashMap<>();
//    configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getBootstrapServers());
//    return new KafkaAdmin(configs);
//  }

//  @Bean
//  public NewTopic topic() {
//    return TopicBuilder
//        .name(topic)
//        .partitions(2)
//        .replicas(1)
//        .compact()
//        .build();
//  }
}
