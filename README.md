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

1. Configure your Kafka brokers and Elasticsearch endpoints in the properties files
2. Set test parameters in `config.properties` and `consumer.properties`
3. Compile and run the producer tests: `java Main`
4. Run consumer tests: `java ConsumerJ`

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

Download the following JAR files to the `lib/` directory:
- jna-4.5.2.jar
- gson-2.8.x.jar
- elasticsearch-rest-client-x.x.x.jar
- httpclient-x.x.x.jar
- httpcore-x.x.x.jar

## License

MIT License