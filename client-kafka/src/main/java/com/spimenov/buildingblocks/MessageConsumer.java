package com.spimenov.buildingblocks;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class MessageConsumer {

  @KafkaListener(topics = "beer-test", containerFactory = "greetingKafkaListenerContainerFactory")
  public void greetingListener(BeerSample greeting) {
    // process greeting message
  }
}
