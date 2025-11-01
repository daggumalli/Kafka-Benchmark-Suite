#!/bin/bash

# Kafka Producer Performance Test Runner
# This script compiles and runs the producer performance tests

echo "Starting Kafka Producer Performance Tests..."

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "Java is not installed. Please install Java 8 or higher."
    exit 1
fi

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "Maven is not installed. Please install Maven to build the project."
    exit 1
fi

# Build the project
echo "Building the project..."
mvn clean compile

# Check if build was successful
if [ $? -ne 0 ]; then
    echo "Build failed. Please check the errors above."
    exit 1
fi

# Run the producer tests
echo "Running producer performance tests..."
java -cp "target/classes:target/dependency/*" com.kafka.benchmark.Main

echo "Producer performance tests completed."