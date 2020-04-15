/*
 * Copyright (C) 2020 adidas AG.
 */

package com.spimenov.buildingblocks.service;

import com.spimenov.buildingblocks.kafka.listener.MessageEvent.Message;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Stanislav Pimenov
 */
@Service
@Slf4j
public class TestService {

  private static final NormalDistribution NORMAL_DISTRIBUTION = new NormalDistribution(50d, 10d);

  public void doSmth(List<Message> messages) {
    try {
      final int interval = (int) NORMAL_DISTRIBUTION.sample();
      TimeUnit.MILLISECONDS.sleep(interval);
      log.info("processing took: {} ms", interval);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

}
