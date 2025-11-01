#!/bin/bash

# Kafka Producer Performance Test Runner
# This script compiles and runs the producer performance tests

echo "Starting Kafka Producer Performance Tests..."

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "Error: Java is not installed. Please install Java 8 or higher."
    exit 1
fi

# Check if JAR files exist
if [ ! -d "lib" ] || [ -z "$(ls -A lib/*.jar 2>/dev/null)" ]; then
    echo "Error: Required JAR files not found in lib/ directory."
    echo "Please run: ./lib/download-dependencies.sh"
    exit 1
fi

# Compile the project
echo "Compiling the project..."
./scripts/compile.sh

# Check if compilation was successful
if [ $? -ne 0 ]; then
    echo "Compilation failed. Please check the errors above."
    exit 1
fi

# Build classpath
CLASSPATH="target/classes"
for jar in lib/*.jar; do
    if [ -f "$jar" ]; then
        CLASSPATH="$CLASSPATH:$jar"
    fi
done

# Run the producer tests
echo "Running producer performance tests..."
java -cp "$CLASSPATH" com.kafka.benchmark.Main

echo "Producer performance tests completed."