package com.spimenov.buildingblocks.kafka.listener;

import static org.springframework.util.CollectionUtils.isEmpty;

import com.spimenov.buildingblocks.kafka.listener.MessageEvent.Message;
import com.spimenov.buildingblocks.service.TestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@Slf4j
public class MessageListener {

  public static final int BATCH_SIZE = 10;

  private CountDownLatch latch = new CountDownLatch(BATCH_SIZE);

  public CountDownLatch getLatch() {
    return latch;
  }

  @Autowired
  private TestService testService;

  @KafkaListener(id = "batch-listener", topics = "${kafka.consumer.topic-name}",
      containerGroup = "${spring.kafka.consumer.group-id}")
  public void receive(List<MessageEvent> batchData,
      @Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions,
      @Header(KafkaHeaders.OFFSET) List<Long> offsets) {

    log.info("batch receive start --> {} events", offsets.size());

    IntStream
        .range(0, batchData.size())
        .forEach(i -> log.debug("batch receive detailed: partition-offset='{}-{}', message='{}'",
            partitions.get(i), offsets.get(i), batchData.get(i)));

    final List<Message> messages = batchData
        .stream()
        .flatMap(event -> event
            .getMessages()
            .stream())
        .collect(Collectors.toList());
    if (!isEmpty(messages)) {
      testService.doSmth(messages);
    }

    offsets.forEach(o -> latch.countDown());

    log.info("batch receive end <-- ");
  }
}
