# Dependencies Directory

This directory is for JAR files if you prefer manual dependency management instead of Maven.

## Required JAR Files (if not using Maven)

If you're not using Maven, download these JAR files and place them in this directory:

1. **jna-4.5.2.jar**
   - Download from: https://github.com/java-native-access/jna/releases

2. **gson-2.8.9.jar**
   - Download from: https://github.com/google/gson/releases

3. **elasticsearch-rest-client-7.17.0.jar**
   - Download from Maven Central

4. **httpclient-4.5.13.jar**
   - Download from Maven Central

5. **httpcore-4.4.15.jar**
   - Download from Maven Central

## Using Maven (Recommended)

If you're using Maven (recommended), these dependencies will be automatically downloaded when you run:

```bash
mvn clean compile
```

The Maven approach is preferred as it handles transitive dependencies automatically.