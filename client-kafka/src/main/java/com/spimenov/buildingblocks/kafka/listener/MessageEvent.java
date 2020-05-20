package com.spimenov.buildingblocks.kafka.listener;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Stanislav Pimenov
 */
@Data
@NoArgsConstructor
public class MessageEvent {

  private List<Message> messages;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Message {

    private Integer messageId;

    private String payload;

  }
}
