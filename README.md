# Kafka Benchmark Suite

A comprehensive Kafka performance testing and analysis framework that automates producer and consumer benchmarking across multiple topics with real-time metrics collection and Elasticsearch integration.

## Features

- **Multi-threaded Performance Testing**: Concurrent producer and consumer performance tests across configurable topic ranges
- **Comprehensive Metrics Collection**: Captures throughput, latency percentiles (50th, 95th, 99th, 99.9th), fetch times, and rebalance metrics
- **Elasticsearch Integration**: Automatically indexes test results for analysis and visualization
- **Configurable Test Parameters**: Customizable record counts, sizes, throughput rates, and timeout settings
- **Production-Ready**: Designed for testing Kafka clusters under various load conditions

## Use Cases

- Kafka cluster capacity planning and sizing
- Performance regression testing
- Throughput and latency benchmarking
- Load testing before production deployments
- Performance monitoring and trend analysis

## Technical Stack

- Java-based with multi-threading support
- Leverages Confluent Kafka performance testing tools
- Elasticsearch for metrics storage and analysis
- JSON-based result formatting with Gson

## Prerequisites

- Java 8 or higher
- Confluent Kafka platform or Apache Kafka
- Elasticsearch cluster (for results storage)
- Required JAR dependencies (see lib/ directory)

## Quick Start

1. **Download dependencies:** `./lib/download-dependencies.sh`
2. **Configure your environment:** Edit `config/config.properties` and `config/consumer.properties`
3. **Run producer tests:** `./scripts/run-producer-tests.sh`
4. **Run consumer tests:** `./scripts/run-consumer-tests.sh`

See [QUICK-START.md](QUICK-START.md) for detailed instructions.

## Configuration

### Producer Configuration (`config.properties`)
```properties
numOfRecords=70000
recordSize=4000
throughputRate=20
indexname=your-producer-index
topicstart=1
topicend=10
```

### Consumer Configuration (`consumer.properties`)
```properties
numOfRecords=70000
indexname=your-consumer-index
topicstart=1
topicend=10
groupid=test-group
latest=no
timeout=60000
```

## Dependencies

All required JAR files are automatically downloaded:
- ✅ **9 JAR files** (3.8MB total) 
- ✅ **No Maven required**
- ✅ **One command setup:** `./lib/download-dependencies.sh`

**Included JARs:**
- elasticsearch-rest-client-7.17.0.jar
- gson-2.8.9.jar  
- httpclient-4.5.13.jar
- httpcore-4.4.15.jar
- httpcore-nio-4.4.15.jar
- httpasyncclient-4.1.5.jar
- jna-4.5.2.jar
- commons-logging-1.2.jar
- commons-codec-1.15.jar

## License

MIT License