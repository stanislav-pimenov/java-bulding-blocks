package com.spimenov.kafka.test

import io.gatling.core.Predef._
import org.apache.kafka.clients.producer.ProducerConfig
import scala.concurrent.duration._
import scala.util.Random

import com.github.mnogu.gatling.kafka.Predef._

class ShakeKafka extends Simulation {

  val kafkaConf = kafka
    // Kafka topic name
    .topic("msg-topic")
    // Kafka producer configs
    .properties(
      Map(
        ProducerConfig.ACKS_CONFIG -> "1",
        // list of Kafka broker hostname and port pairs
        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG -> "localhost:9092",

        // in most cases, StringSerializer or ByteArraySerializer
        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG ->
          "org.apache.kafka.common.serialization.StringSerializer",
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG ->
          "org.apache.kafka.common.serialization.StringSerializer"))

  val feeder = Iterator.continually(Map("messageId" -> Random.nextInt(Integer.MAX_VALUE)))
  //val feeder = jsonFile("gatling-simulations/event-feeder.json").circular

  val scn = scenario("Kafka Test")
    .feed(feeder)
    .exec(
      kafka("request")
        // message to send
        .send[String]("""{"messages": [{"messageId": ${messageId}, "payload": "some payload"}, {"messageId": ${messageId}, "payload": "some additional payload"}]}""".stripMargin))

  setUp(scn.inject(constantUsersPerSec(100) during (30 minutes))).throttle(
    reachRps(100) in (10 seconds),
    holdFor(1 minute)
  ).protocols(kafkaConf)
}
