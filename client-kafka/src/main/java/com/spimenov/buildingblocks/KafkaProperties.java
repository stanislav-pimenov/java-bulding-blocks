package com.spimenov.buildingblocks;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Stanislav Pimenov
 */
@Getter
@Setter
@NoArgsConstructor
@ConfigurationProperties(prefix = "kafka")
public class KafkaProperties {

  private String bootstrapAddress;

  private String groupId;

  @Getter
  @Setter
  @NoArgsConstructor
  public static class Listener {

    private String topic;

  }
}
