# Setup Guide

## Prerequisites

1. **Java 8 or higher**
   ```bash
   java -version
   ```

2. **Apache Maven**
   ```bash
   mvn -version
   ```

3. **Kafka Cluster**
   - Apache Kafka or Confluent Platform
   - Accessible broker endpoints

4. **Elasticsearch Cluster** (for results storage)
   - Version 7.x recommended
   - Accessible endpoint

## Installation Steps

### 1. Clone and Build
```bash
git clone <repository-url>
cd Kafka-Benchmark-Suite
mvn clean compile
```

### 2. Configure Kafka Settings

Edit `config/config.properties` for producer tests:
```properties
# Update these values for your environment
kafkaHome=/path/to/your/kafka
bootstrapServers=broker1:9092,broker2:9092,broker3:9092
elasticsearchHost=your-elasticsearch-host
elasticsearchPort=9200
```

Edit `config/consumer.properties` for consumer tests:
```properties
# Update these values for your environment
kafkaHome=/path/to/your/kafka
bootstrapServers=broker1:9092,broker2:9092,broker3:9092
elasticsearchHost=your-elasticsearch-host
elasticsearchPort=9200
```

### 3. Configure Security (if needed)

Update `config/producer.config` and `config/consumer.config` with your security settings:
```properties
security.protocol=SSL
ssl.truststore.location=/path/to/truststore.jks
ssl.truststore.password=password
# ... other SSL/SASL settings
```

### 4. Create Topics

Create the topics you want to test:
```bash
kafka-topics --create --topic benchmark-topic-1 --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1
kafka-topics --create --topic benchmark-topic-2 --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1
# ... create more topics as needed
```

### 5. Setup Elasticsearch Indices

The application will automatically create indices, but you can pre-create them:
```bash
curl -X PUT "localhost:9200/kafka-producer-benchmark"
curl -X PUT "localhost:9200/kafka-consumer-benchmark"
```

## Running Tests

### Producer Performance Tests
```bash
./scripts/run-producer-tests.sh
```

### Consumer Performance Tests
```bash
./scripts/run-consumer-tests.sh
```

### Manual Execution
```bash
# Compile first
mvn clean compile

# Run producer tests
java -cp "target/classes:target/dependency/*" com.kafka.benchmark.Main

# Run consumer tests
java -cp "target/classes:target/dependency/*" com.kafka.benchmark.ConsumerJ
```

## Viewing Results

Results are automatically indexed to Elasticsearch. You can:

1. **Query directly via REST API:**
   ```bash
   curl -X GET "localhost:9200/kafka-producer-benchmark/_search?pretty"
   ```

2. **Use Kibana for visualization:**
   - Import the results indices
   - Create dashboards for throughput, latency trends
   - Set up alerts for performance thresholds

3. **Export results:**
   ```bash
   curl -X GET "localhost:9200/kafka-producer-benchmark/_search" > producer-results.json
   ```

## Troubleshooting

### Common Issues

1. **Connection refused to Kafka:**
   - Verify `bootstrapServers` in config files
   - Check network connectivity
   - Ensure Kafka is running

2. **Elasticsearch connection issues:**
   - Verify `elasticsearchHost` and `elasticsearchPort`
   - Check Elasticsearch is running and accessible

3. **Permission denied on scripts:**
   ```bash
   chmod +x scripts/*.sh
   ```

4. **Java classpath issues:**
   - Ensure Maven dependencies are downloaded: `mvn dependency:copy-dependencies`
   - Check Java version compatibility

### Performance Tuning

1. **Increase JVM heap size:**
   ```bash
   export JAVA_OPTS="-Xmx2g -Xms1g"
   ```

2. **Adjust test parameters:**
   - Reduce `numOfRecords` for faster tests
   - Increase `throughputRate` for higher load
   - Adjust `recordSize` based on your use case

3. **Monitor system resources:**
   - CPU usage during tests
   - Network bandwidth utilization
   - Disk I/O on Kafka brokers