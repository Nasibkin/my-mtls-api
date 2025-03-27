# My mTLS API

This is a simple Spring Boot API service configured with mutual TLS (mTLS) authentication. It demonstrates a secure connection using self-signed certificates, built with Maven and Java.

## Overview
- **Purpose**: A basic API endpoint (`/mtls/hello`) that returns a greeting, accessible only via mTLS.
- **Tech Stack**:
  - Java 23.0.2 (compatible with 21.0.5 as specified)
  - Spring Boot 3.2.4
  - Maven
- **Security**: mTLS ensures both client and server authenticate each other using self-signed certificates.

## Project Structure
```
.
├── README.md                   # This file
├── pom.xml                     # Maven configuration
├── src
│   └── main
│       ├── java
│       │   └── com/example/devops/maven/springboot
│       │       ├── HelloApplication.java  # Main application
│       │       └── controller
│       │           └── HelloController.java  # API controller
│       └── resources
│           ├── application.properties  # mTLS configuration
│           ├── keystore.p12           # Server keystore (ignored)
│           └── truststore.p12         # Truststore (ignored)
└── target                     # Build artifacts (ignored)
```

## Prerequisites
- **Java**: 21.0.5 or later (tested with 23.0.2)
- **Maven**: For building the project
- **OpenSSL**: For generating certificates
- **Keytool**: Part of JDK, for managing truststores
- **curl**: For testing the API

## Setup Instructions
Since certificate files are excluded via `.gitignore`, you’ll need to generate them locally.

### 1. Clone the Repository
```bash
git clone <repository-url>
cd my-mtls-api
```

### 2. Generate Certificates
Run these commands to create self-signed certificates and keystores:
```bash
# Server certificate and keystore
openssl req -x509 -newkey rsa:4096 -keyout server-key.pem -out server-cert.pem -days 365 -nodes -subj "/CN=localhost"
openssl pkcs12 -export -out src/main/resources/keystore.p12 -inkey server-key.pem -in server-cert.pem -password pass:changeit

# Client certificate
openssl req -x509 -newkey rsa:4096 -keyout client-key.pem -out client-cert.pem -days 365 -nodes -subj "/CN=client"

# Truststore (trusts both server and client certificates)
keytool -import -trustcacerts -file server-cert.pem -keystore src/main/resources/truststore.p12 -storepass changeit -noprompt -alias server-cert
keytool -import -trustcacerts -file client-cert.pem -keystore src/main/resources/truststore.p12 -storepass changeit -noprompt -alias client-cert
```

### 3. Build the Project
```bash
mvn clean package
```
- Outputs `target/my-mtls-api-0.0.1-SNAPSHOT.jar`.

### 4. Run the Application
```bash
java -jar target/my-mtls-api-0.0.1-SNAPSHOT.jar
```
- Starts the server on `https://localhost:8443`.

## Usage
Test the API with `curl`. The endpoint requires mTLS authentication.

### Successful Request (with Certificates)
```bash
curl -v --cert client-cert.pem --key client-key.pem --cacert server-cert.pem "https://localhost:8443/mtls/hello?name=Visitor"
```
- **Expected Output**: `"Hi, Visitor!"` (HTTP 200)
- **Details**: Uses client certificate for authentication and trusts the server certificate.

### Failed Request (without Certificates)
```bash
curl -v https://localhost:8443
```
- **Expected Output**: `curl: (60) SSL certificate problem: self signed certificate`
- **Details**: Fails due to missing client certificate and untrusted server certificate, proving mTLS enforcement.

## Configuration
- **mTLS Settings**: Defined in `src/main/resources/application.properties`:
  ```
  server.port=8443
  server.ssl.enabled=true
  server.ssl.key-store-type=PKCS12
  server.ssl.key-store=classpath:keystore.p12
  server.ssl.key-store-password=changeit
  server.ssl.trust-store=classpath:truststore.p12
  server.ssl.trust-store-password=changeit
  server.ssl.client-auth=need
  ```

## Notes
- **Certificates**: Excluded from the repo for security. Regenerate them as shown above.
- **Password**: Hardcoded as `changeit` for simplicity; in production, use environment variables or a secrets manager.
- **Browser Access**: Requires importing `client.p12` into the browser’s certificate store (not covered here).

## Task Completion
This project meets the following goals:
1. **Folder Structure**: Includes `pom.xml`, `application.properties`, and source files.
2. **mTLS**: Configured and enforced.
3. **Build**: Built locally with Maven.
4. **Curl Test**: Works with self-signed certificates.