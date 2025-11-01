# Dependencies Directory

This directory contains all required JAR files for the Kafka Benchmark Suite.

## Automatic Download (Recommended)

Run the provided script to automatically download all required dependencies:

```bash
./lib/download-dependencies.sh
```

This will download all necessary JAR files to the `lib/` directory.

## Required JAR Files

The following JAR files are required for the project:

### Core Dependencies
1. **elasticsearch-rest-client-7.17.0.jar** - Elasticsearch REST client
2. **gson-2.8.9.jar** - JSON processing library
3. **jna-4.5.2.jar** - Java Native Access library

### HTTP Client Dependencies
4. **httpclient-4.5.13.jar** - Apache HTTP Client
5. **httpcore-4.4.15.jar** - Apache HTTP Core
6. **httpcore-nio-4.4.15.jar** - Apache HTTP Core NIO
7. **httpasyncclient-4.1.5.jar** - Apache HTTP Async Client

### Utility Dependencies
8. **commons-logging-1.2.jar** - Commons Logging
9. **commons-codec-1.15.jar** - Commons Codec

## Manual Download

If you prefer to download manually, get these files from Maven Central:
- Base URL: https://repo1.maven.org/maven2/

## Using Maven (Alternative)

If you prefer Maven dependency management:

```bash
mvn clean compile
mvn dependency:copy-dependencies -DoutputDirectory=lib
```

## Verification

After downloading, verify all JARs are present:

```bash
ls -la lib/*.jar
```

You should see 9 JAR files in the lib/ directory.