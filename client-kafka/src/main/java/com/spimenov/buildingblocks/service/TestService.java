/*
 * Copyright (C) 2020 adidas AG.
 */

package com.spimenov.buildingblocks.service;

import com.spimenov.buildingblocks.kafka.listener.MessageEvent;
import com.spimenov.buildingblocks.kafka.listener.MessageEvent.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author Stanislav Pimenov
 */
@Service
@Slf4j
public class TestService {

  private static final Random rnd = new Random();

  public void doSmth(List<Message> messages) {
    try {
      final int interval = (int) (rnd.nextGaussian() * 15.0d) + 50;
      TimeUnit.MILLISECONDS.sleep(interval);
      log.info("processing took: {} ms", interval);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

}
