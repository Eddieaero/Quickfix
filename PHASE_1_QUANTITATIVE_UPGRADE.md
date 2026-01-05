# Aero QuickFIX Quantitative Platform - Phase 1 Implementation

## Overview
Phase 1 establishes the foundational infrastructure for the Aero QuickFIX platform to function as a production-grade quantitative trading system. This phase focuses on data infrastructure, technical indicators, backtesting framework, and the first strategy implementation.

## ✅ Completed Tasks

### 1. **TimescaleDB Setup** ✅
**Location**: `quickfix-server/docker-compose.yml` + `quickfix-server/init.sql`

**What was done**:
- Added TimescaleDB 15 PostgreSQL image to docker-compose
- Created hypertables for time-series data optimization
- Configured 8 core tables:
  - `ohlcv_data`: Historical price data (Open, High, Low, Close, Volume)
  - `technical_indicators`: Cached indicator calculations
  - `backtest_results`: Comprehensive backtest metrics
  - `trade_log`: Individual trade records
  - `portfolio_snapshot`: Portfolio value over time
  - `market_events`: Earnings, dividends, splits
  - `strategies`: Strategy metadata and parameters
  
**Indexes Created**:
- Symbol + Time composite indexes for fast lookups
- Strategy and backtest filtering indexes
- Trade analysis indexes

**Views Created**:
- `latest_prices`: Quick access to most recent prices
- `recent_trades`: Active open positions
- `backtest_stats`: Strategy performance aggregations

**Start with**:
```bash
cd quickfix-server
docker-compose up timescaledb
# Wait for health check: "pg_isready -U aero_user"
```

---

### 2. **Technical Indicators Library** ✅
**Location**: `aero/src/main/java/com/aero/quickfix/quant/indicators/IndicatorCalculator.java`

**Indicators Implemented**:

| Indicator | Use Case | Parameters | Output |
|-----------|----------|-----------|--------|
| **SMA** | Trend identification | period (20, 50, 200) | List<Double> |
| **EMA** | Trend with recency | period | List<Double> |
| **RSI** | Overbought/oversold | period=14 | 0-100 |
| **MACD** | Momentum crossovers | fast=12, slow=26, signal=9 | {macd, signal, histogram} |
| **Bollinger Bands** | Volatility breakouts | period=20, stdDevs=2 | {upper, middle, lower} |
| **ATR** | Volatility + stops | period=14 | List<Double> |
| **ROC** | Momentum rate | period | % change |
| **Returns** | Performance analysis | daily/cumulative | % daily returns |

**Usage Example**:
```java
List<Double> prices = /* historical close prices */;
List<Double> sma50 = IndicatorCalculator.calculateSMA(prices, 50);
List<Double> sma200 = IndicatorCalculator.calculateSMA(prices, 200);

// MACD for momentum
Map<String, List<Double>> macd = IndicatorCalculator.calculateMACD(prices, 12, 26, 9);
List<Double> macdLine = macd.get("macd");
List<Double> signalLine = macd.get("signal");
```

---

### 3. **JPA Entities & Database Models** ✅

#### **OHLCVData Entity**
**Location**: `aero/src/main/java/com/aero/quickfix/quant/model/OHLCVData.java`

```java
@Entity
@Table(name = "ohlcv_data")
public class OHLCVData {
    LocalDateTime time;      // Timestamp
    String symbol;           // Stock symbol
    BigDecimal open;         // Opening price
    BigDecimal high;         // High price
    BigDecimal low;          // Low price
    BigDecimal close;        // Closing price
    Long volume;             // Trading volume
    BigDecimal adjustedClose; // Split/dividend adjusted
    // ... timestamps
}
```

#### **BacktestResults Entity**
**Location**: `aero/src/main/java/com/aero/quickfix/quant/model/BacktestResults.java`

Stores comprehensive metrics for each backtest:
- Strategy name, symbol, date range
- Initial capital → Final value
- **Key metrics**:
  - Total return, Annual return
  - Sharpe ratio, Sortino ratio
  - Max drawdown, Win rate
  - Profit factor, Average win/loss

#### **TradeLog Entity**
**Location**: `aero/src/main/java/com/aero/quickfix/quant/model/TradeLog.java`

Individual trade records with:
- Entry/exit prices and dates
- Trade type (LONG/SHORT), quantity
- Profit/Loss in $ and %
- Entry/exit signal names
- Status (OPEN/CLOSED)

---

### 4. **Repository Layer** ✅

#### **OHLCVDataRepository**
```java
// Key methods:
findBySymbolAndDateRange(symbol, startDate, endDate);
findLatestBySymbol(symbol);
findAllSymbols();
countBySymbol(symbol);
countBySymbolAndDate(symbol, date);
```

#### **BacktestResultsRepository**
```java
// Key methods:
findByStrategyName(strategyName);
findBySymbol(symbol);
findBestByStrategyNameBySharpe(strategyName);
findRecentBacktests(since);
```

#### **TradeLogRepository**
```java
// Key methods:
findByBacktestId(backtestId);
findClosedTradesByBacktestId(backtestId);
countWinningTrades(backtestId);
countLosingTrades(backtestId);
```

---

### 5. **Database Configuration** ✅
**Location**: `aero/src/main/resources/application.yml`

Added PostgreSQL/JPA configuration:
```yaml
datasource:
  url: jdbc:postgresql://localhost:5432/aero_quant
  username: aero_user
  password: aero_password
  
jpa:
  hibernate:
    ddl-auto: validate
  properties:
    dialect: org.hibernate.dialect.PostgreSQLDialect
```

**Maven Dependencies Added**:
- `spring-boot-starter-data-jpa`
- `postgresql` driver
- `apache-commons-math3` (for calculations)

---

## 🔄 In Progress / Next Tasks

### **Task 3: Implement EODHD Historical Data Fetcher**
**Estimate**: 2-3 hours

**What needs to be done**:
1. Extend `EodhMarketDataClient` to fetch historical daily OHLCV data
2. Create method: `fetchHistoricalData(symbol, startDate, endDate)`
3. Parse EODHD JSON response → OHLCVData entities
4. Implement data validation:
   - Check for gaps (missing trading days)
   - Validate OHLC relationships (low ≤ close ≤ high)
   - Remove duplicates
5. Batch insert to database using repository
6. Create service: `HistoricalDataService`
7. Add REST endpoint: `POST /api/market/fetch-historical`

**Code Structure**:
```java
@Service
public class HistoricalDataService {
    public void fetchAndStoreData(String symbol, LocalDate from, LocalDate to) {
        // 1. Call EodhMarketDataClient.fetchHistoricalOHLCV()
        // 2. Validate data
        // 3. Save to repository
        // 4. Log results
    }
}
```

---

### **Task 4: Build Backtesting Engine Core**
**Estimate**: 4-5 hours

**Architecture**:
```java
@Service
public class BacktestEngine {
    // Initialize backtest with parameters
    void initializeBacktest(String strategyName, String symbol, 
                           LocalDate startDate, LocalDate endDate, 
                           BigDecimal initialCapital);
    
    // Simulate a single day's trading
    void simulateDay(LocalDate date, List<OHLCVData> dayData);
    
    // Generate signals from strategy
    void generateSignals();
    
    // Execute trades
    void executeTrades(Signal signal, OHLCVData price);
    
    // Track portfolio
    void updatePortfolio(Trade trade);
    
    // Generate results
    BacktestResults finalize();
}
```

---

### **Task 5: Performance Metrics Calculator**
**Estimate**: 3-4 hours

**Metrics to Implement**:
```java
public class MetricsCalculator {
    double calculateSharpeRatio(List<Double> returns, double riskFreeRate = 0.02);
    double calculateSortinoRatio(List<Double> returns, double targetReturn = 0);
    double calculateMaxDrawdown(List<Double> equityCurve);
    double calculateWinRate(List<TradeLog> trades);
    double calculateProfitFactor(List<TradeLog> trades);
    double calculateCAGR(BigDecimal startValue, BigDecimal endValue, int years);
    double calculateRecoveryFactor(double totalProfit, double maxDrawdown);
}
```

**Formulas**:
- **Sharpe**: (Return - RiskFreeRate) / StdDev(Returns)
- **Max Drawdown**: (Trough - Peak) / Peak
- **Win Rate**: WinningTrades / TotalTrades
- **Profit Factor**: GrossProfit / GrossLoss

---

### **Task 6: Strategy Interface & Base Class**
**Estimate**: 2 hours

**Design**:
```java
public interface Strategy {
    Signal generateSignal(List<OHLCVData> priceHistory, 
                         Map<String, List<Double>> indicators);
    void onTrade(TradeLog trade);
    void onDayClose(LocalDate date);
}

public abstract class BaseStrategy implements Strategy {
    protected List<Double> prices;
    protected Map<String, List<Double>> indicators;
    
    protected Signal createBuySignal(String reason);
    protected Signal createSellSignal(String reason);
    protected void updateIndicators();
}
```

---

### **Task 7: SMA Crossover Strategy**
**Estimate**: 1-2 hours

**Logic**:
```
- BUY: When SMA50 crosses above SMA200
- SELL: When SMA50 crosses below SMA200
- Position sizing: Risk 2% per trade
- Stop-loss: 2% below entry
```

**Implementation**:
```java
@Component
public class SMAcrossoverStrategy extends BaseStrategy {
    private static final int FAST_PERIOD = 50;
    private static final int SLOW_PERIOD = 200;
    
    @Override
    public Signal generateSignal(List<OHLCVData> history, 
                                Map<String, List<Double>> indicators) {
        List<Double> sma50 = IndicatorCalculator.calculateSMA(closes, 50);
        List<Double> sma200 = IndicatorCalculator.calculateSMA(closes, 200);
        
        // Check crossover on latest bar
        if (sma50.get(n) > sma200.get(n) && sma50.get(n-1) <= sma200.get(n-1)) {
            return new BuySignal("SMA50 > SMA200 Bullish Cross");
        }
        // ... similar for sell
        return new NoSignal();
    }
}
```

---

## 🚀 Quick Start Commands

### 1. **Start TimescaleDB**
```bash
cd quickfix-server
docker-compose up timescaledb
# In another terminal, verify:
psql -h localhost -U aero_user -d aero_quant -c "\dt"
```

### 2. **Build Backend with Maven**
```bash
cd aero
mvn clean package
# or run directly
mvn spring-boot:run
```

### 3. **Verify Database Connection**
```bash
curl -X GET http://localhost:8080/actuator/health
# Should show PostgreSQL datasource status
```

### 4. **Test Indicator Calculation**
```java
// In unit tests
List<Double> prices = Arrays.asList(100.0, 101.5, 99.8, 102.3, ...);
List<Double> sma20 = IndicatorCalculator.calculateSMA(prices, 20);
Assert.assertNotNull(sma20);
Assert.assertEquals(sma20.size(), prices.size());
```

---

## 📊 Data Schema Reference

### OHLCV Table Structure
```sql
SELECT * FROM ohlcv_data 
WHERE symbol = 'AAPL' 
  AND time BETWEEN '2023-01-01' AND '2024-01-01'
ORDER BY time DESC;
```

### Backtest Query
```sql
SELECT strategy_name, 
       COUNT(*) as backtests,
       AVG(sharpe_ratio) as avg_sharpe,
       MAX(total_return) as best_return
FROM backtest_results
GROUP BY strategy_name;
```

---

## 📋 Testing Checklist

- [ ] TimescaleDB starts and stays healthy
- [ ] Can insert 100 OHLCV records
- [ ] SMA(20), EMA(12), RSI(14) calculate correctly
- [ ] Backtest runs from start to finish without errors
- [ ] Results save to database with all metrics
- [ ] Can retrieve backtest results by ID
- [ ] Sharpe ratio calculation matches expected values
- [ ] Trade logs show entry/exit signals

---

## 🔐 Security Notes

- **Database**: Uses simple auth for dev. Switch to strong passwords for production
- **API Keys**: EODHD key stored in environment variables
- **Data**: TimescaleDB runs in Docker with persistent volume

---

## 💾 Backup & Restore

```bash
# Backup database
pg_dump -h localhost -U aero_user aero_quant > backup.sql

# Restore
psql -h localhost -U aero_user aero_quant < backup.sql
```

---

## 📈 Next Phase Preview (Phase 2)

After Phase 1 completion, Phase 2 will add:
1. **Portfolio optimization** using mean-variance analysis
2. **Risk management**: Position sizing, stop-losses, correlations
3. **Multi-asset strategies**: Trade multiple symbols simultaneously
4. **Advanced orders**: Stop-limit, trailing stops, scale-in/out
5. **Performance attribution**: What drove returns?

---

## 📞 Troubleshooting

**PostgreSQL connection refused**:
```bash
docker ps | grep timescaledb
# If not running: docker-compose up -d timescaledb
```

**Out of memory during backtest**:
- Reduce date range
- Use daily data instead of minute
- Batch process by symbol

**Indicators returning nulls**:
- Ensure enough data points (period + 1 minimum)
- Check for data gaps
- Validate price list isn't empty

---

**Status**: Phase 1 setup complete - 5/7 tasks done
**Current Focus**: Task 3 (Historical data fetcher)
**Estimated Completion**: 1 week for full Phase 1
