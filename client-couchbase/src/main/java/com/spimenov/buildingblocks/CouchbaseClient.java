package com.spimenov.buildingblocks;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.query.QueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@EnableConfigurationProperties(CouchbaseProperties.class)
public class CouchbaseClient {

  private final Bucket bucket;

  private final Cluster cluster;

  @Autowired
  public CouchbaseClient(CouchbaseProperties couchbaseProperties) {
    this.cluster =
        Cluster.connect(couchbaseProperties.getHostname(), couchbaseProperties.getUsername(),
            couchbaseProperties.getPassword());
    this.bucket = this.cluster.bucket(couchbaseProperties.getBucket());
  }

  public Object testClient() {

    bucket
        .defaultCollection()
        .upsert("brewery_beername", new BeerSample(List.of("Kudrovo")));
    QueryResult queryResult =
        cluster.query("select br.* from `beer-sample` br USE KEYS [\"brewery_beername\"]");

    return queryResult.rowsAs(BeerSample.class);
  }

  public static void main(String[] args) {
    CouchbaseClient couchbaseClient = new CouchbaseClient(CouchbaseProperties
        .builder()
        .hostname("localhost")
        .username("Administrator")
        .password("password")
        .bucket("beer-sample")
        .build());
    System.out.println(couchbaseClient.testClient());
  }
}
