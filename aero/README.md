# Aero QuickFIX

A Spring Boot application for consuming and managing QuickFIX/FIX protocol messages with integrated market data services powered by Finnhub and Alpha Vantage APIs.

## Project Overview

This project integrates QuickFIX/J with Spring Boot to provide a robust framework for FIX protocol communication. It includes REST API endpoints for managing FIX connections, handling FIX messages, and fetching real-time and historical market data via free/freemium APIs.

## Prerequisites

- Java 21 or higher
- Maven 3.6+
- PostgreSQL (for data persistence)
- API Keys (optional, for full functionality):
  - Finnhub API key: https://finnhub.io (free tier available)
  - Alpha Vantage API key: https://www.alphavantage.co (free tier available)

## Project Structure

```
aero/
├── pom.xml                          # Maven configuration
├── src/
│   ├── main/
│   │   ├── java/com/aero/quickfix/
│   │   │   ├── AeroApplication.java # Spring Boot entry point
│   │   │   ├── client/              # Market data clients
│   │   │   │   └── FinvizMarketDataClient.java  # Finnhub/Alpha Vantage client
│   │   │   ├── config/              # QuickFIX and app configuration
│   │   │   ├── controller/          # REST API endpoints
│   │   │   ├── dto/                 # Data transfer objects
│   │   │   ├── service/             # Business logic
│   │   │   └── websocket/           # WebSocket support
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

Edit `src/main/resources/application.yml` for the following:

**QuickFIX Configuration** (`quickfix-client.cfg`):
- `SocketConnectHost`: FIX server hostname
- `SocketConnectPort`: FIX server port
- `SenderCompID`: Your sender ID
- `TargetCompID`: Target server ID

**Market Data Configuration** (`application.yml`):
```yaml
finviz:
  api:
    key: ${FINNHUB_API_KEY:}  # Set environment variable for real API key
  enabled: true

alpha-vantage:
  api:
    key: ${ALPHA_VANTAGE_API_KEY:}  # Set environment variable for real API key
```

### 3. Set Environment Variables (Optional - for real API access)

```bash
# For Finnhub API (https://finnhub.io)
export FINNHUB_API_KEY=your_finnhub_api_key_here

# For Alpha Vantage API (https://www.alphavantage.co)
export ALPHA_VANTAGE_API_KEY=your_alpha_vantage_api_key_here
```

### 4. Run the Application

```bash
# Using Maven
mvn spring-boot:run

# Or using Java directly after building
java -jar target/aero-quickfix-1.0.0.jar
```

The application will start on `http://localhost:8080`

## API Endpoints

### QuickFIX Management

#### Health Check
```
GET /api/quickfix/health
```
Returns the status of the QuickFIX service.

#### Start Connection
```
POST /api/quickfix/start
```
Initiates the FIX protocol connection.

#### Stop Connection
```
POST /api/quickfix/stop
```
Closes the FIX protocol connection.

#### Get Status
```
GET /api/quickfix/status
```
Returns current connection status (CONNECTED or DISCONNECTED).

### Market Data

#### Get Current Price
```
GET /api/market/price/{symbol}
```
Returns the current market price for a symbol.

**Example:**
```bash
curl http://localhost:8080/api/market/price/AAPL
```

**Response:**
```json
{
  "symbol": "AAPL",
  "price": 180.45,
  "lastUpdated": 1704377400000,
  "valid": true
}
```

#### Validate Symbol
```
GET /api/market/validate/{symbol}
```
Checks if a symbol is valid (attempts price lookup).

#### US Market Overview
```
GET /api/market/us/stocks
```
Returns overview of major US stock indices (S&P 500, NASDAQ, Dow Jones).

#### DSE Market Overview
```
GET /api/market/dse/stocks
```
Returns overview of Dhaka Stock Exchange stocks.

#### Get Multiple Prices
```
GET /api/market/prices?symbols=AAPL,GOOGL,MSFT
```
Returns prices for multiple symbols.

#### Price Cache Management
```
GET /api/market/cache/prices      # Get cached prices
GET /api/market/cache/stats       # Get cache statistics
POST /api/market/cache/clear      # Clear the cache
```

#### Debug Configuration
```
GET /api/market/debug/config
```
Returns current API configuration (for debugging).

## Market Data Sources

The application uses a dual API approach for market data:

### Primary Source: Finnhub
- **Endpoint:** https://finnhub.io/api/v1
- **Features:** Real-time prices, intraday data
- **Rate Limits:** 60 requests/minute (free tier)
- **Free Tier:** Yes

### Secondary Source: Alpha Vantage
- **Endpoint:** https://www.alphavantage.co/query
- **Features:** Historical OHLCV data, time series
- **Rate Limits:** 5 requests/minute (free tier)
- **Free Tier:** Yes

### Fallback Strategy
1. Application attempts Finnhub API first
2. On failure, falls back to Alpha Vantage
3. Returns error message if both APIs fail
4. Works in demo mode with empty API keys (limited functionality)

## Example Usage

### QuickFIX Examples

#### Health Check
```bash
curl http://localhost:8080/api/quickfix/health
```

#### Start FIX Connection
```bash
curl -X POST http://localhost:8080/api/quickfix/start
```

#### Check Connection Status
```bash
curl http://localhost:8080/api/quickfix/status
```

### Market Data Examples

#### Get Apple Stock Price
```bash
curl http://localhost:8080/api/market/price/AAPL
```

#### Get Multiple Stock Prices
```bash
curl "http://localhost:8080/api/market/prices?symbols=AAPL,GOOGL,MSFT"
```

#### Validate a Symbol
```bash
curl http://localhost:8080/api/market/validate/TSLA
```

#### Get US Market Overview
```bash
curl http://localhost:8080/api/market/us/stocks
```

## Logging

Logs are configured in `application.yml`. By default:
- Root level: INFO
- Application level: DEBUG
- QuickFIX level: INFO
- Market Data level: DEBUG

Check the console or log files for detailed information about API calls and FIX protocol communication.

## Configuration Properties

| Property | Default | Description |
|----------|---------|-------------|
| `quickfix.enabled` | true | Enable/disable QuickFIX functionality |
| `quickfix.config.file` | classpath:quickfix-client.cfg | QuickFIX configuration file |
| `finviz.enabled` | true | Enable Finviz-based market data services |
| `spring.jpa.hibernate.ddl-auto` | validate | Hibernate DDL mode |
| `server.port` | 8080 | Spring Boot server port |

## Dependencies

- **Spring Boot 3.2.0**: Web framework and dependency injection
- **QuickFIX/J 2.3.1**: FIX protocol implementation
- **Spring Data JPA**: Database persistence
- **PostgreSQL Driver**: Database connectivity
- **Jackson**: JSON serialization/deserialization
- **Lombok**: Reducing boilerplate code
- **JUnit 5**: Testing framework

## Development

### Adding New Features

1. Create new message handlers in `config/QuickFixApplicationAdapter.java`
2. Add new endpoints in `controller/` package
3. Implement business logic in `service/` package
4. Add corresponding DTOs in `dto/` package

### Running Tests

```bash
mvn test
```

All tests should pass with green status:
```
Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
```

## Troubleshooting

### QuickFIX Connection Issues
- Ensure the FIX server is running on the configured host and port
- Check firewall settings
- Verify `quickfix-client.cfg` settings

### Market Data API Not Working
- Check environment variables: `FINNHUB_API_KEY`, `ALPHA_VANTAGE_API_KEY`
- Check API key validity on https://finnhub.io and https://www.alphavantage.co
- Verify rate limits haven't been exceeded (API calls are logged)
- Check `/api/market/debug/config` endpoint for current configuration

### Missing Messages
- Check the log level (should be DEBUG or INFO for message details)
- Verify SenderCompID and TargetCompID match the server configuration
- Ensure the FIX message format is compatible with your server

## API Key Registration

### Getting a Free Finnhub API Key
1. Visit https://finnhub.io
2. Click "Get Free API Key"
3. Sign up with email
4. Copy your API key
5. Set environment variable: `export FINNHUB_API_KEY=your_key_here`

### Getting a Free Alpha Vantage API Key
1. Visit https://www.alphavantage.co
2. Click "Get Free API Key"
3. Enter your email
4. Check email for API key
5. Set environment variable: `export ALPHA_VANTAGE_API_KEY=your_key_here`

## Migration Notes

This application previously used EODHD (paid API). It has been migrated to use free APIs:
- ✅ Replaced EodhMarketDataClient with FinvizMarketDataClient
- ✅ Integrated Finnhub for real-time prices
- ✅ Integrated Alpha Vantage for historical data
- ✅ All tests passing (5/5)
- ✅ Full backward compatibility maintained

See `EODHD_TO_FINVIZ_MIGRATION_SUMMARY.md` for detailed migration information.

## License

This project is part of the Aero Portfolio project.

## Support

For issues or questions:
1. Check the application logs
2. Verify API keys and configuration
3. Ensure network connectivity to API endpoints
4. Review the troubleshooting section above
