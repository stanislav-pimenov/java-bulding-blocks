/*
 * Copyright (C) 2020 adidas AG.
 */

package com.spimenov.buildingblocks.kafka.filter;

import com.spimenov.buildingblocks.kafka.listener.MessageEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.adapter.RecordFilterStrategy;

import java.util.Optional;

/**
 * @author Stanislav Pimenov
 */

@Slf4j
public class EmptyMessagesFilterStrategy implements RecordFilterStrategy<String, MessageEvent> {

  @Override
  public boolean filter(ConsumerRecord<String, MessageEvent> event) {
    boolean discarded = Optional
        .ofNullable(event.value())
        .map(MessageEvent::getMessages)
        .map(messages -> !(messages.size() > 0))
        .orElse(true);
    if (discarded)  {
      log.info("Discarded event {}", event);
    }
    return discarded;
  }
}
