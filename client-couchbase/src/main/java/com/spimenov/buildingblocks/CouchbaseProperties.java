package com.spimenov.buildingblocks;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Stanislav Pimenov
 */

@ConfigurationProperties(prefix = "couchbase")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouchbaseProperties {

  private String hostname;

  private String bucket;

  private String username;

  private String password;
}
