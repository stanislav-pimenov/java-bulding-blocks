# java-bulding-blocks
contains samples for different types of java projects

## Environment

### Java 11

`brew cask install adoptopenjdk11`

### Couchbase

`brew cask install couchbase-server-enterprise`

add couchbase-cli to PATH with ~/.bash_profile

```
PATH="$PATH:/Applications/Couchbase Server.app/Contents/Resources/couchbase-core/bin"
```

create cluster with default bucket `beer-sample`
```
// todo
```


### Kafka

install kafka, start zookeeper in the background, launch kafka

```
brew install kafka
brew services start zookeeper
kafka-server-start /usr/local/etc/kafka/server.properties
```

another terminal: create topic, connect with console produces

```
kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic beer-test
kafka-console-producer --broker-list localhost:9092 --topic beer-test
```

