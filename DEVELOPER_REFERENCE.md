# Developer Quick Reference - Aero QuickFIX Quant Platform

## Quick Start (5 Minutes)

### 1. Start Database
```bash
cd quickfix-server
docker-compose up -d
sleep 30  # Wait for database to initialize
```

### 2. Build & Run Application
```bash
cd aero
mvn clean compile
mvn spring-boot:run
# Or: java -jar target/aero-quickfix-1.0.0.jar
```

### 3. Test API
```bash
# Health check
curl http://localhost:8080/api/quant/health

# List strategies
curl http://localhost:8080/api/quant/strategies
```

---

## Common Tasks

### Task: Add New Strategy

1. **Create Strategy Class**
```java
package com.aero.quickfix.quant.strategy.impl;

@Component
public class MyStrategy extends BaseStrategy {
    
    public MyStrategy() {
        super(
            "My Strategy",
            "Description of what the strategy does",
            50,  // Minimum bars required
            Arrays.asList("SMA_50", "RSI_14")  // Required indicators
        );
    }
    
    @Override
    public Signal generateSignal(List<OHLCVData> priceHistory, 
                                 Map<String, List<Double>> indicators) {
        // Your logic here
        if (/* condition */) {
            return createBuySignal(0.8, "Reason for buy");
        } else if (/* condition */) {
            return createSellSignal(0.8, "Reason for sell");
        }
        return createHoldSignal("Reason for hold");
    }
}
```

2. **Register in BacktestController**
```java
private Strategy getStrategy(String strategyName) {
    switch (strategyName.toLowerCase()) {
        case "sma crossover":
            return smaCrossoverStrategy;
        case "my strategy":
            return myStrategy;
        default:
            return null;
    }
}
```

3. **Test via API**
```bash
curl -X POST http://localhost:8080/api/quant/backtest \
  -H "Content-Type: application/json" \
  -d '{
    "strategyName": "My Strategy",
    "symbol": "AAPL.US",
    "startDate": "2023-01-01",
    "endDate": "2024-01-01",
    "initialCapital": 100000
  }'
```

### Task: Add New Indicator

1. **Add Method to IndicatorCalculator**
```java
public List<Double> calculateKeltnerChannel(List<Double> prices, int period) {
    List<Double> sma = calculateSMA(prices, period);
    List<Double> atr = calculateATR(/* params */);
    // Your calculation
    return result;
}
```

2. **Use in Strategy**
```java
Map<String, List<Double>> indicators = calculateIndicators(strategy, windowPrices);
List<Double> keltner = indicators.get("KELTNER_CHANNEL");
```

### Task: Query Backtest Results

```java
// In service
@Autowired
private BacktestResultsRepository backtestRepository;

// Find all backtests for a strategy
List<BacktestResults> results = backtestRepository
    .findByStrategyName("SMA Crossover");

// Find best performing backtest
BacktestResults best = backtestRepository
    .findBestByStrategyNameBySharpe("SMA Crossover");

// Find all trades in a backtest
List<TradeLog> trades = tradeLogRepository
    .findByBacktestId(results.get(0).getId());
```

### Task: Calculate Custom Metrics

```java
@Autowired
private MetricsCalculator metricsCalculator;

// Calculate from trades
BigDecimal winRate = metricsCalculator.calculateWinRate(trades);
BigDecimal profitFactor = metricsCalculator.calculateProfitFactor(trades);

// Calculate from equity curve
List<BigDecimal> equityCurve = /* ... */;
BigDecimal maxDD = metricsCalculator.calculateMaxDrawdown(equityCurve);
```

### Task: Load Historical Data

```java
@Autowired
private HistoricalDataService historicalDataService;

// Fetch and store data
HistoricalDataResult result = historicalDataService
    .fetchAndStoreHistoricalData(
        "AAPL.US",
        LocalDate.of(2023, 1, 1),
        LocalDate.of(2024, 1, 1)
    );

if (result.isSuccess()) {
    System.out.println("Inserted: " + result.getRecordsInserted());
}
```

### Task: Run Backtest Programmatically

```java
@Autowired
private BacktestEngine backtestEngine;

@Autowired
private SmaCrossoverStrategy strategy;

// Run backtest
BacktestResults results = backtestEngine.runBacktest(
    strategy,
    "AAPL.US",
    LocalDate.of(2023, 1, 1),
    LocalDate.of(2024, 1, 1),
    new BigDecimal("100000")
);

// Access results
System.out.println("Sharpe: " + results.getSharpeRatio());
System.out.println("Max DD: " + results.getMaxDrawdown());
System.out.println("Win Rate: " + results.getWinRate());
```

---

## Architecture Reference

### Class Hierarchy
```
Strategy (interface)
    ↑
    | implements
    |
BaseStrategy (abstract, 20+ helper methods)
    ↑
    | extends
    |
SmaCrossoverStrategy (concrete)
RSIOscillatorStrategy (future)
BollingerBandsStrategy (future)
```

### Service Layer Dependencies
```
BacktestEngine
    ├─ OHLCVDataRepository (price history)
    ├─ IndicatorCalculator (8 indicators)
    ├─ Strategy (signal generation)
    ├─ MetricsCalculator (performance metrics)
    ├─ BacktestResultsRepository (results storage)
    └─ TradeLogRepository (trade records)

HistoricalDataService
    ├─ EodhMarketDataClient (API calls)
    └─ OHLCVDataRepository (storage)
```

### Database Schema
```
ohlcv_data (hypertable)
├─ time, symbol (PK)
├─ open, high, low, close, volume
└─ Index: (symbol, time DESC)

backtest_results
├─ id (PK)
├─ strategy_name, symbol
├─ start_date, end_date
├─ Metrics: sharpe_ratio, max_drawdown, etc.
└─ Index: (strategy_name), (symbol)

trade_log
├─ id (PK)
├─ backtest_id (FK)
├─ entry_price, exit_price, quantity
├─ profit_loss, profit_loss_pct
└─ Index: (backtest_id), (symbol)
```

---

## API Reference

### Backtest Endpoint
```
POST /api/quant/backtest

Request:
{
  "strategyName": "SMA Crossover",
  "symbol": "AAPL.US",
  "startDate": "2023-01-01",
  "endDate": "2024-01-01",
  "initialCapital": 100000.00
}

Response: BacktestResultDTO
{
  "id": "uuid",
  "strategyName": "SMA Crossover",
  "symbol": "AAPL.US",
  "totalReturn": 15.25,        // percentage
  "sharpeRatio": 1.45,
  "sortinoRatio": 2.10,
  "maxDrawdown": -12.50,       // percentage
  "totalTrades": 45,
  "winRate": 55.56,            // percentage
  "profitFactor": 1.85,
  "avgWin": 1250.50,
  "avgLoss": 675.25
}
```

### Get Strategies Endpoint
```
GET /api/quant/strategies

Response:
{
  "count": 1,
  "strategies": [
    {
      "name": "SMA Crossover",
      "description": "Simple Moving Average Crossover Strategy",
      "minimumBars": 201,
      "requiredIndicators": ["SMA_50", "SMA_200"]
    }
  ]
}
```

### Get Backtest Results Endpoint
```
GET /api/quant/backtest/{id}

Response: BacktestResultDTO (same as POST response)
```

### List Backtests by Strategy
```
GET /api/quant/backtest/strategy/{strategyName}

Response:
{
  "count": 5,
  "results": [BacktestResultDTO, ...]
}
```

### Health Check
```
GET /api/quant/health

Response:
{
  "status": "UP",
  "component": "Quantitative Analysis Engine",
  "features": [
    "Technical Indicators",
    "Strategy Framework",
    "Backtesting Engine",
    "Performance Metrics"
  ]
}
```

---

## Testing Scenarios

### Scenario 1: Test SMA Crossover Strategy
```bash
# 1. Ensure data is loaded
# 2. Run backtest
curl -X POST http://localhost:8080/api/quant/backtest \
  -H "Content-Type: application/json" \
  -d '{
    "strategyName": "SMA Crossover",
    "symbol": "AAPL.US",
    "startDate": "2023-01-01",
    "endDate": "2023-12-31",
    "initialCapital": 100000
  }'

# 3. Check response for metrics
# Expected: Sharpe > 0.5, Win Rate > 40%
```

### Scenario 2: Compare Multiple Symbols
```bash
for symbol in AAPL.US MSFT.US GOOGL.US; do
  curl -X POST http://localhost:8080/api/quant/backtest \
    -H "Content-Type: application/json" \
    -d '{
      "strategyName": "SMA Crossover",
      "symbol": "'$symbol'",
      "startDate": "2023-01-01",
      "endDate": "2023-12-31",
      "initialCapital": 100000
    }'
done
```

### Scenario 3: Retrieve All Backtests
```bash
curl http://localhost:8080/api/quant/backtest/strategy/SMA%20Crossover
# Returns list of all SMA Crossover backtests
```

---

## Performance Tips

### Optimize Indicator Calculation
```java
// Cache results if running multiple times
Map<String, List<Double>> indicatorCache = new HashMap<>();
List<Double> sma50 = indicatorCalculator.calculateSMA(prices, 50);
indicatorCache.put("SMA_50", sma50);

// Reuse in multiple strategies
signal1 = strategy1.generateSignal(prices, indicatorCache);
signal2 = strategy2.generateSignal(prices, indicatorCache);
```

### Batch Load Historical Data
```java
// Load once, use multiple times
List<OHLCVData> allData = ohlcvRepository
    .findBySymbolAndDateRange("AAPL.US", from, to);

// Run multiple backtests
for (Strategy strategy : strategies) {
    backtestEngine.runBacktest(strategy, "AAPL.US", from, to, capital);
}
```

### Use Database Indexes
```java
// Fast (uses index on symbol, time)
List<OHLCVData> data = ohlcvRepository
    .findBySymbolAndDateRange("AAPL.US", from, to);

// Slow (table scan)
List<OHLCVData> data = ohlcvRepository.findAll();
```

---

## Debugging

### Enable SQL Logging
In `application.yml`:
```yaml
logging:
  level:
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
```

### Debug Strategy Signal Generation
```java
// Add logging in strategy
@Override
public Signal generateSignal(List<OHLCVData> prices, Map<String, List<Double>> indicators) {
    double latestSMA50 = getLatestIndicatorValue(indicators, "SMA_50");
    double latestSMA200 = getLatestIndicatorValue(indicators, "SMA_200");
    
    logger.info("SMA50: {}, SMA200: {}", latestSMA50, latestSMA200);
    logger.info("Crossover detected: {}", latestSMA50 > latestSMA200);
    
    // ... rest of logic
}
```

### Check Database
```bash
# Connect to PostgreSQL
psql -U aero_user -d aero_quant -h localhost

# List tables
\dt

# Check hypertables
SELECT * FROM timescaledb_information.hypertable;

# Query OHLCV data
SELECT * FROM ohlcv_data WHERE symbol = 'AAPL.US' LIMIT 10;

# Query backtest results
SELECT id, strategy_name, symbol, total_return, sharpe_ratio 
FROM backtest_results 
ORDER BY created_at DESC LIMIT 5;
```

### Check Application Logs
```bash
# If running with spring-boot:run
tail -f /var/log/aero-quant/application.log

# Or in console output
grep -E "BacktestEngine|Strategy|Signal" application.log
```

---

## Common Errors & Solutions

### Error: "Unknown strategy: SMA Crossover"
**Cause**: Strategy not registered in BacktestController  
**Solution**: Add strategy to getStrategy() method switch statement

### Error: "Insufficient price history"
**Cause**: Not enough bars in database for indicator calculation  
**Solution**: Load more historical data via HistoricalDataService

### Error: "No price data found"
**Cause**: No OHLCV data in database for symbol/date range  
**Solution**: Run HistoricalDataService.fetchAndStoreHistoricalData() first

### Error: "Database connection failed"
**Cause**: PostgreSQL/TimescaleDB not running  
**Solution**: `docker-compose up -d` in quickfix-server directory

### Error: "Port 8080 already in use"
**Cause**: Another application using port 8080  
**Solution**: `mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"`

---

## Configuration Reference

### Java/Spring Properties
```yaml
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/aero_quant
spring.datasource.username=aero_user
spring.datasource.password=aero_password

# JPA
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.format_sql=true

# Server
server.port=8080
server.servlet.context-path=/

# Logging
logging.level.org.springframework.boot=INFO
logging.level.com.aero.quickfix=DEBUG
```

### Docker Environment
```bash
# Environment variables
POSTGRES_USER=aero_user
POSTGRES_PASSWORD=aero_password
POSTGRES_DB=aero_quant
TIMESCALEDB_POSTGRESQL_SUPERUSER_PASSWORD=postgres
```

---

## Resources

- **Spring Boot Docs**: https://spring.io/projects/spring-boot
- **Spring Data JPA**: https://spring.io/projects/spring-data-jpa
- **TimescaleDB**: https://docs.timescale.com
- **PostgreSQL**: https://www.postgresql.org/docs

---

*Last Updated: 2024-01-04*  
*Aero QuickFIX - Quantitative Trading Platform*
