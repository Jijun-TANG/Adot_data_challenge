<<<<<<< HEAD
# Program running instructions for windows:

## 1st step: make sure you have wsl2 set up:

`wsl`


## 2nd step: install kafka version 2.13-3.0.0 and go to its' proper location:

`cd /mnt/<your location for kafka>/kafka_2.13-3.0.0`


## 3rd step: Run zookeeper and create a new topic for this specific purpose:

`bin/zookeeper-server-start.sh config/zookeeper.properties`


(Create topics):
`bin/kafka-topics.sh --create --topic test-topic --partitions 3 --replication-factor 3 --bootstrap-server localhost:9092`

(Optional, Describe topics):
`bin/kafka-topics.sh --describe --topic test-topic --bootstrap-server localhost:9092`

## 4th step: Run kafka and inject data inside:

### Open another cmd

`wsl`
`cd /mnt/d/programming_tools/kafka_2.13-3.0.0`
`bin/kafka-server-start.sh config/server.properties`

### Inject data bidrequests_exercice.json into Kafka producer console:

`bin/kafka-console-producer.sh --broker-list localhost:9092 --topic test-topic < bidrequests_exercice.json`

### (Optional, for testing purpose) Consume data from the producer:

`bin/kafka-console-consumer.sh --topic test-topic --from-beginning --bootstrap-server localhost:9092`


## 5th step: Prepare input topic and start kafka producer

### Open another cmd

```
wsl
cd /mnt/<your location for kafka>/kafka_2.13-3.0.0
bin/kafka-topics.sh
```

## 6th step: Compile and running the codes

### Go to file location

`cd /mnt/d/adotmissions/official_files/challenge3/StreamHandler`

### Open vscode:

`code .`

### Compile the file of scala source code:

`sbt package` 

### Submit to spark class for challenge3, handle stream:

`spark-submit --class StreamHandler --master local[*] --packages "org.apache.spark:spark-sql-kafka-0-10_2.12:3.3.0" target/streamhandler_2.12-0.0.1.jar`

### Submit to spark class for challenge4, doing spark sql and generate table:

`spark-submit --class CalculateData --master local[*] --packages org.apache.spark:spark-sql-kafka-0-10_2.12:3.3.0 target/scala-2.12/calculatedata_2.12-0.0.1.jar`
=======
# Kafka streaming processing with Spark/Scala on Windows!

### Kafka version: `2.13-3.0.0`
### Spark version: `3.3.0`
### Scala version: `2.12.15`

### 1st step:
`wsl`

### 2nd step:
`cd <your location>/kafka_2.13-3.0.0`


### 3rd step:
Run 
`bin/zookeeper-server-start.sh config/zookeeper.properties`


### 4th step:

open another cmd
`wsl`
`cd <your route>/kafka_2.13-3.0.0`
Run `bin/kafka-server-start.sh config/server.properties`

(Create topics, suppose topic name is 'test-topic'):
`bin/kafka-topics.sh --create --topic test-topic --partitions 3 --replication-factor 3 --bootstrap-server localhost:9092`

(Describe topics):
`bin/kafka-topics.sh --describe --topic test-topic --bootstrap-server localhost:9092`

### 5th step:
open antoher wsl prompt
`cd <your location>/challenge3_4/StreamHandler`, set the "name" variable of "build.sbt" file to "StreamHandler" and run 
`sbt package`

### 6th step:
Run
`spark-submit --class StreamHandler --master local[*] --packages "org.apache.spark:spark-sql-kafka-0-10_2.12:3.3.0" target/streamhandler_2.12-0.0.1.jar`

### 7th step:
Inject data bidrequests_exercice.json into Kafka producer console on a new WSL prompt where the location is `<your route>/kafka_2.13-3.0.0`:
`bin/kafka-console-producer.sh --broker-list localhost:9092 --topic test-topic < bidrequests_exercice.json`

### And your data will be processed into the parquet_path!

## Steps for setting up challenge 4:

### 1st step:
another `wsl` prompt
`cd <your location>/challenge3_4/StreamHandler`, set the "name" variable of "build.sbt" file to "CalculateData" and run 
`sbt package`

### 2nd step:
Run
`spark-submit --class CalculateData --master local[*] --packages org.apache.spark:spark-sql-kafka-0-10_2.12:3.3.0 target/scala-2.12/calculatedata_2.12-0.0.1.jar`

### And voila, you get your result!
>>>>>>> a8dd4f12ddd4263f992ac9a576d1333fe9bf471e
