#!/bin/bash

# Compile Kafka Benchmark Suite
# This script compiles the Java source files with the required JAR dependencies

echo "Compiling Kafka Benchmark Suite..."

# Check if Java is installed
if ! command -v javac &> /dev/null; then
    echo "Error: javac (Java compiler) is not installed. Please install JDK 8 or higher."
    exit 1
fi

# Create output directory
mkdir -p target/classes

# Check if lib directory exists and has JAR files
if [ ! -d "lib" ] || [ -z "$(ls -A lib/*.jar 2>/dev/null)" ]; then
    echo "Error: lib directory is empty or doesn't contain JAR files."
    echo "Please run: ./lib/download-dependencies.sh"
    exit 1
fi

# Build classpath from lib directory
CLASSPATH=""
for jar in lib/*.jar; do
    if [ -f "$jar" ]; then
        CLASSPATH="$CLASSPATH:$jar"
    fi
done

# Remove leading colon
CLASSPATH=${CLASSPATH#:}

echo "Using classpath: $CLASSPATH"

# Compile Java files
echo "Compiling Java source files..."
javac -cp "$CLASSPATH" -d target/classes src/main/java/com/kafka/benchmark/*.java

# Check compilation result
if [ $? -eq 0 ]; then
    echo "✓ Compilation successful!"
    echo "Compiled classes are in target/classes/"
else
    echo "✗ Compilation failed!"
    exit 1
fi