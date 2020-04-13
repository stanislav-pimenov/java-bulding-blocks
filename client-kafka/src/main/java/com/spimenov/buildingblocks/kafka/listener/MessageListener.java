package com.spimenov.buildingblocks.kafka.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CountDownLatch;

@Component
@Slf4j
public class MessageListener {

  public static final int BATCH_SIZE = 10;

  private CountDownLatch latch = new CountDownLatch(BATCH_SIZE);

  public CountDownLatch getLatch() {
    return latch;
  }

  @KafkaListener(id = "batch-listener", topics = "${kafka.consumer.topic-name}",
      containerGroup = "${spring.kafka.consumer.group-id}")
  public void receive(List<MessageEvent> batchData,
      @Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions,
      @Header(KafkaHeaders.OFFSET) List<Long> offsets) {

    log.info("start of batch receive");
    for (int i = 0; i < batchData.size(); i++) {
      log.info("received message='{}' with partition-offset='{}-{}'", batchData.get(i),
          partitions.get(i), offsets.get(i));
      // todo handle message

      latch.countDown();
    }
    log.info("end of batch receive");
  }
}
