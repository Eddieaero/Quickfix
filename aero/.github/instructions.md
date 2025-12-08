# Copilot Instructions

## Spring Boot QuickFIX Project

This project is a Spring Boot application that integrates with QuickFIX/J to consume and handle FIX protocol messages.

### Project Structure

-   **pom.xml**: Maven configuration with Spring Boot and QuickFIX/J dependencies
-   **src/main/java/com/aero/quickfix/**: Main application code
    -   `AeroApplication.java`: Spring Boot entry point
    -   `config/`: QuickFIX configuration classes
    -   `controller/`: REST API endpoints
    -   `service/`: Business logic for QuickFIX operations
-   **src/main/resources/**: Configuration files
    -   `application.yml`: Spring Boot configuration
    -   `quickfix-client.cfg`: QuickFIX protocol settings

### Key Features

-   Spring Boot REST API for managing FIX connections
-   QuickFIX/J client implementation
-   Configurable FIX protocol settings
-   Logging and monitoring endpoints