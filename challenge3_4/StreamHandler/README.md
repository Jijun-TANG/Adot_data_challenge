# Kafka streaming processing with Spark/Scala on Windows!

### Kafka version: `2.13-3.0.0`

### 1st step:
`wsl`

### 2nd step:
`cd <your location>/kafka_2.13-3.0.0`


### 3rd step:
Run `bin/zookeeper-server-start.sh config/zookeeper.properties`


### 4th step:

open another cmd
`wsl`
`cd <your route>/kafka_2.13-3.0.0`
Run `bin/kafka-server-start.sh config/server.properties`

(Create topics, suppose topic name is 'test-topic'):
bin/kafka-topics.sh --create --topic test-topic --partitions 3 --replication-factor 3 --bootstrap-server localhost:9092

(Describe topics):
bin/kafka-topics.sh --describe --topic test-topic --bootstrap-server localhost:9092

### 5th step:
open antoher wsl prompt
`cd <your location>/challenge3_4/StreamHandler`, set the "name" variable of "build.sbt" file to "StreamHandler" and run `sbt package`

### 6th step:
Run `spark-submit --class StreamHandler --master local[*] --packages "org.apache.spark:spark-sql-kafka-0-10_2.12:3.3.0" target/streamhandler_2.12-0.0.1.jar`

### 7th step:
#### Inject data bidrequests_exercice.json into Kafka producer console on a new WSL prompt where the location is `<your route>/kafka_2.13-3.0.0`:
bin/kafka-console-producer.sh --broker-list localhost:9092 --topic test-topic < bidrequests_exercice.json

### And your data will be processed into the parquet_path!

## Steps for setting up challenge 4:

### 1st step:
another `wsl` prompt
`cd <your location>/challenge3_4/StreamHandler`, set the "name" variable of "build.sbt" file to "CalculateData" and run `sbt package`

### 2nd step:
Run `spark-submit --class CalculateData --master local[*] --packages org.apache.spark:spark-sql-kafka-0-10_2.12:3.3.0 target/scala-2.12/calculatedata_2.12-0.0.1.jar`

### And voila, you get your result!
