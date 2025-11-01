# Quick Start Guide

## Prerequisites
- Java 8 or higher
- Kafka cluster running
- Elasticsearch cluster (for results storage)

## 1. Download Dependencies (One-time setup)
```bash
./lib/download-dependencies.sh
```

This downloads all required JAR files:
- ✅ **9 JAR files** (3.8MB total)
- ✅ **No Maven required**
- ✅ **No complex setup**

## 2. Configure Your Environment

### Edit Producer Settings
```bash
nano config/config.properties
```
Update these key settings:
```properties
# Your Kafka brokers
bootstrapServers=your-broker1:9092,your-broker2:9092

# Your Elasticsearch
elasticsearchHost=your-elasticsearch-host
elasticsearchPort=9200

# Test parameters
numOfRecords=10000
recordSize=1000
throughputRate=100
```

### Edit Consumer Settings
```bash
nano config/consumer.properties
```
Update these key settings:
```properties
# Your Kafka brokers
bootstrapServers=your-broker1:9092,your-broker2:9092

# Your Elasticsearch
elasticsearchHost=your-elasticsearch-host
elasticsearchPort=9200

# Test parameters
numOfRecords=10000
```

## 3. Run Tests

### Producer Performance Test
```bash
./scripts/run-producer-tests.sh
```

### Consumer Performance Test
```bash
./scripts/run-consumer-tests.sh
```

## 4. View Results

Results are automatically stored in Elasticsearch:

### Query Results via REST API
```bash
# Producer results
curl -X GET "your-elasticsearch-host:9200/kafka-producer-benchmark/_search?pretty"

# Consumer results
curl -X GET "your-elasticsearch-host:9200/kafka-consumer-benchmark/_search?pretty"
```

### Sample Producer Metrics
```json
{
  "start time": "2024-11-01T17:30:00.000",
  "End time": "2024-11-01T17:31:00.000",
  "records sent": 70000,
  "records/sec": 1166.67,
  "MB/sec": 4.67,
  "avg latency ms": 2.5,
  "max latency ms": 45.0,
  "50th percentile ms": 2.0,
  "95th percentile ms": 8.0,
  "99th percentile ms": 15.0,
  "99.9th percentile ms": 25.0
}
```

### Sample Consumer Metrics
```json
{
  "topic": 1,
  "start.time": "2024-11-01T17:30:00.000",
  "end.time": "2024-11-01T17:31:00.000",
  "data.consumed.in.MB": 280.0,
  "MB.sec": 4.67,
  "data.consumed.in.nMsg": 70000,
  "nMsg.sec": 1166.67,
  "rebalance.time.ms": 100.0,
  "fetch.time.ms": 59900.0,
  "fetch.MB.sec": 4.68,
  "fetch.nMsg.sec": 1168.33
}
```

## Troubleshooting

### Common Issues

**1. "JAR files not found"**
```bash
./lib/download-dependencies.sh
```

**2. "Java not found"**
```bash
java -version  # Should show Java 8+
```

**3. "Connection refused to Kafka"**
- Check `bootstrapServers` in config files
- Verify Kafka is running: `kafka-topics --list --bootstrap-server your-broker:9092`

**4. "Connection refused to Elasticsearch"**
- Check `elasticsearchHost` and `elasticsearchPort` in config files
- Verify Elasticsearch: `curl your-elasticsearch-host:9200`

### Test with Local Setup

If you want to test locally:

1. **Start Kafka locally:**
   ```bash
   # Start Zookeeper
   bin/zookeeper-server-start.sh config/zookeeper.properties
   
   # Start Kafka
   bin/kafka-server-start.sh config/server.properties
   ```

2. **Start Elasticsearch locally:**
   ```bash
   # Using Docker
   docker run -d -p 9200:9200 -e "discovery.type=single-node" elasticsearch:7.17.0
   ```

3. **Use default config:**
   - `bootstrapServers=localhost:9092`
   - `elasticsearchHost=localhost`

## What Gets Tested

### Producer Tests
- **Throughput**: Records/sec, MB/sec
- **Latency**: Average, Max, Percentiles (50th, 95th, 99th, 99.9th)
- **Timing**: Start/end timestamps

### Consumer Tests  
- **Consumption Rate**: Messages/sec, MB/sec
- **Fetch Performance**: Fetch time, fetch rate
- **Rebalance Metrics**: Rebalance time
- **Timing**: Start/end timestamps

Perfect for capacity planning, performance regression testing, and production readiness validation!