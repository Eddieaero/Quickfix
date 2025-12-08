# Aero QuickFIX

A Spring Boot application for consuming and managing QuickFIX/FIX protocol messages.

## Project Overview

This project integrates QuickFIX/J with Spring Boot to provide a robust framework for FIX protocol communication. It includes REST API endpoints for managing connections and handling FIX messages.

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- A running FIX server (for actual communication)

## Project Structure

```
aero/
├── pom.xml                          # Maven configuration
├── src/
│   ├── main/
│   │   ├── java/com/aero/quickfix/
│   │   │   ├── AeroApplication.java # Spring Boot entry point
│   │   │   ├── config/              # QuickFIX configuration
│   │   │   ├── controller/          # REST API endpoints
│   │   │   └── service/             # Business logic
│   │   └── resources/
│   │       ├── application.yml      # Spring Boot configuration
│   │       └── quickfix-client.cfg  # FIX protocol configuration
│   └── test/
│       └── java/com/aero/quickfix/  # Unit tests
└── README.md                        # This file
```

## Setup Instructions

### 1. Clone and Build

```bash
# Build the project
mvn clean package

# Run tests
mvn test
```

### 2. Configuration

Edit `src/main/resources/quickfix-client.cfg` to match your FIX server settings:
- `SocketConnectHost`: FIX server hostname
- `SocketConnectPort`: FIX server port
- `SenderCompID`: Your sender ID
- `TargetCompID`: Target server ID

### 3. Run the Application

```bash
# Using Maven
mvn spring-boot:run

# Or using Java directly after building
java -jar target/aero-quickfix-1.0.0.jar
```

The application will start on `http://localhost:8080`

## API Endpoints

### Health Check
```
GET /api/quickfix/health
```
Returns the status of the QuickFIX service.

### Start Connection
```
POST /api/quickfix/start
```
Initiates the FIX protocol connection.

### Stop Connection
```
POST /api/quickfix/stop
```
Closes the FIX protocol connection.

### Get Status
```
GET /api/quickfix/status
```
Returns current connection status (CONNECTED or DISCONNECTED).

## Example Usage

### Health Check
```bash
curl http://localhost:8080/api/quickfix/health
```

### Start FIX Connection
```bash
curl -X POST http://localhost:8080/api/quickfix/start
```

### Check Connection Status
```bash
curl http://localhost:8080/api/quickfix/status
```

### Stop FIX Connection
```bash
curl -X POST http://localhost:8080/api/quickfix/stop
```

## Logging

Logs are configured in `application.yml`. By default:
- Root level: INFO
- Application level: DEBUG
- QuickFIX level: INFO

Check the console or log files for detailed information about FIX protocol communication.

## Configuration Properties

| Property | Default | Description |
|----------|---------|-------------|
| `quickfix.enabled` | true | Enable/disable QuickFIX functionality |
| `quickfix.config.file` | classpath:quickfix-client.cfg | QuickFIX configuration file |
| `server.port` | 8080 | Spring Boot server port |

## Dependencies

- **Spring Boot 3.2.0**: Web framework and dependency injection
- **QuickFIX/J 2.3.1**: FIX protocol implementation
- **Lombok**: Reducing boilerplate code
- **JUnit 5**: Testing framework

## Development

### Adding New Features

1. Create new message handlers in the `config/QuickFixApplicationAdapter.java`
2. Add new endpoints in `controller/QuickFixController.java`
3. Implement business logic in `service/QuickFixService.java`

### Running Tests

```bash
mvn test
```

## Troubleshooting

### Connection Timeout
- Ensure the FIX server is running on the configured host and port
- Check firewall settings
- Verify `quickfix-client.cfg` settings

### Missing Messages
- Check the log level (should be DEBUG or INFO for message details)
- Verify SenderCompID and TargetCompID match the server configuration
- Ensure the FIX message format is compatible with your server

## License

This project is part of the Aero Portfolio project.

## Support

For issues or questions, please check the logs and verify your QuickFIX configuration.
