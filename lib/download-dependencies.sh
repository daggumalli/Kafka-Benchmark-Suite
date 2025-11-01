#!/bin/bash

# Download Required JAR Dependencies for Kafka Benchmark Suite
# This script downloads all necessary JAR files for the project

echo "Downloading required JAR dependencies..."

# Create lib directory if it doesn't exist
mkdir -p lib

# Base URLs for Maven Central
MAVEN_BASE="https://repo1.maven.org/maven2"

# Define versions
ELASTICSEARCH_VERSION="7.17.0"
GSON_VERSION="2.8.9"
HTTPCLIENT_VERSION="4.5.13"
HTTPCORE_VERSION="4.4.15"
HTTPCORE_NIO_VERSION="4.4.15"
HTTPASYNCCLIENT_VERSION="4.1.5"
JNA_VERSION="4.5.2"
COMMONS_LOGGING_VERSION="1.2"
COMMONS_CODEC_VERSION="1.15"

# Download function
download_jar() {
    local url=$1
    local filename=$2
    
    if [ ! -f "lib/$filename" ]; then
        echo "Downloading $filename..."
        curl -L -o "lib/$filename" "$url"
        if [ $? -eq 0 ]; then
            echo "✓ Downloaded $filename"
        else
            echo "✗ Failed to download $filename"
        fi
    else
        echo "✓ $filename already exists"
    fi
}

# Download all required JARs
echo "Starting downloads..."

# Elasticsearch REST Client
download_jar "$MAVEN_BASE/org/elasticsearch/client/elasticsearch-rest-client/$ELASTICSEARCH_VERSION/elasticsearch-rest-client-$ELASTICSEARCH_VERSION.jar" "elasticsearch-rest-client-$ELASTICSEARCH_VERSION.jar"

# Google Gson
download_jar "$MAVEN_BASE/com/google/code/gson/gson/$GSON_VERSION/gson-$GSON_VERSION.jar" "gson-$GSON_VERSION.jar"

# Apache HTTP Client
download_jar "$MAVEN_BASE/org/apache/httpcomponents/httpclient/$HTTPCLIENT_VERSION/httpclient-$HTTPCLIENT_VERSION.jar" "httpclient-$HTTPCLIENT_VERSION.jar"

# Apache HTTP Core
download_jar "$MAVEN_BASE/org/apache/httpcomponents/httpcore/$HTTPCORE_VERSION/httpcore-$HTTPCORE_VERSION.jar" "httpcore-$HTTPCORE_VERSION.jar"

# Apache HTTP Core NIO
download_jar "$MAVEN_BASE/org/apache/httpcomponents/httpcore-nio/$HTTPCORE_NIO_VERSION/httpcore-nio-$HTTPCORE_NIO_VERSION.jar" "httpcore-nio-$HTTPCORE_NIO_VERSION.jar"

# Apache HTTP Async Client
download_jar "$MAVEN_BASE/org/apache/httpcomponents/httpasyncclient/$HTTPASYNCCLIENT_VERSION/httpasyncclient-$HTTPASYNCCLIENT_VERSION.jar" "httpasyncclient-$HTTPASYNCCLIENT_VERSION.jar"

# JNA
download_jar "$MAVEN_BASE/net/java/dev/jna/jna/$JNA_VERSION/jna-$JNA_VERSION.jar" "jna-$JNA_VERSION.jar"

# Commons Logging (dependency for HTTP Client)
download_jar "$MAVEN_BASE/commons-logging/commons-logging/$COMMONS_LOGGING_VERSION/commons-logging-$COMMONS_LOGGING_VERSION.jar" "commons-logging-$COMMONS_LOGGING_VERSION.jar"

# Commons Codec (dependency for HTTP Client)
download_jar "$MAVEN_BASE/commons-codec/commons-codec/$COMMONS_CODEC_VERSION/commons-codec-$COMMONS_CODEC_VERSION.jar" "commons-codec-$COMMONS_CODEC_VERSION.jar"

echo ""
echo "Download completed! All JAR files are now in the lib/ directory."
echo ""
echo "JAR files downloaded:"
ls -la lib/*.jar

echo ""
echo "You can now compile and run the project using the provided scripts."