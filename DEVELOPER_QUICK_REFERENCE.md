# 📚 Aero QuickFIX Quantitative Platform - Developer Quick Reference

## 🎯 Project Goals
Transform Aero QuickFIX from a trading dashboard into a **professional quantitative trading platform** with:
- Historical backtesting
- Technical analysis
- Automated strategy execution
- Performance analytics
- Risk management

---

## 📦 Architecture Overview

```
┌─────────────────────────────────────────────────────────┐
│                   Next.js Frontend                      │
│        (Backtest UI, Results, Charts, Settings)         │
└────────────┬────────────────────────────────────────────┘
             │ REST API
┌────────────▼────────────────────────────────────────────┐
│              Spring Boot Backend (8080)                 │
├─────────────────────────────────────────────────────────┤
│  ┌─────────────────┐  ┌─────────────────────────────┐  │
│  │ REST Controllers│  │   WebSocket (Real-time)     │  │
│  │ /api/backtest   │  │   /ws/prices, /ws/trades    │  │
│  │ /api/strategies │  └─────────────────────────────┘  │
│  └────────┬────────┘                                   │
│           │                                            │
│  ┌────────▼──────────────────────────────────────────┐ │
│  │     Quantitative Services Layer                  │ │
│  │  ┌──────────┐  ┌──────────┐  ┌──────────────┐   │ │
│  │  │ Backtest │  │Indicators│  │  Strategies  │   │ │
│  │  │  Engine  │  │Calculator│  │  Framework   │   │ │
│  │  └──────────┘  └──────────┘  └──────────────┘   │ │
│  │                                                 │ │
│  │  ┌──────────┐  ┌──────────────────────────┐   │ │
│  │  │ Metrics  │  │  Risk Management         │   │ │
│  │  │Calculator│  │  - Position Sizing      │   │ │
│  │  └──────────┘  │  - Stop-Loss Logic      │   │ │
│  │                │  - Portfolio Tracking   │   │ │
│  │                └──────────────────────────┘   │ │
│  └─────────────────────────────────────────────────┘ │
│           │                                          │
│  ┌────────▼──────────────────────────────────────────┐ │
│  │        Data Access Layer (JPA)                   │ │
│  │  ┌──────────────┐  ┌──────────────────────┐    │ │
│  │  │OHLCVData     │  │BacktestResults       │    │ │
│  │  │Repository    │  │Repository            │    │ │
│  │  └──────────────┘  └──────────────────────┘    │ │
│  │  ┌──────────────────────────────────────┐      │ │
│  │  │TradeLog Repository                   │      │ │
│  │  └──────────────────────────────────────┘      │ │
│  └─────────────────────────────────────────────────┘ │
│           │                                          │
└───────────┼──────────────────────────────────────────┘
            │
┌───────────▼──────────────────────────────────────────┐
│        External Integrations                         │
│  ┌──────────────────┐   ┌────────────────────────┐  │
│  │   EODHD API      │   │   QuickFIX/J Protocol  │  │
│  │ (Market Data)    │   │   (Trade Execution)    │  │
│  └──────────────────┘   └────────────────────────┘  │
│  ┌──────────────────────────────────────────────┐  │
│  │      TimescaleDB/PostgreSQL (5432)           │  │
│  │  Tables: ohlcv_data, backtest_results,       │  │
│  │           trade_log, strategies              │  │
│  └──────────────────────────────────────────────┘  │
└────────────────────────────────────────────────────┘
```

---

## 📁 Directory Structure

### Backend (Java)
```
aero/
├── src/main/java/com/aero/quickfix/
│   ├── quant/                          # NEW: Quantitative module
│   │   ├── indicators/
│   │   │   └── IndicatorCalculator.java    # SMA, EMA, RSI, MACD, Bollinger, ATR
│   │   ├── model/                          # JPA Entities
│   │   │   ├── OHLCVData.java
│   │   │   ├── BacktestResults.java
│   │   │   └── TradeLog.java
│   │   ├── repository/                     # Data Access
│   │   │   ├── OHLCVDataRepository.java
│   │   │   ├── BacktestResultsRepository.java
│   │   │   └── TradeLogRepository.java
│   │   ├── service/                        # Business Logic (TODO)
│   │   │   ├── BacktestEngine.java
│   │   │   ├── HistoricalDataService.java
│   │   │   ├── MetricsCalculator.java
│   │   │   └── StrategyEngine.java
│   │   └── strategy/                       # Strategy Implementations (TODO)
│   │       ├── Strategy.java               # Interface
│   │       ├── BaseStrategy.java           # Abstract base
│   │       └── impl/
│   │           └── SMAcrossoverStrategy.java
│   │
│   ├── client/
│   │   └── EodhMarketDataClient.java   # Enhanced with historical data fetch
│   │
│   ├── controller/                     # REST Endpoints
│   │   └── BacktestController.java     # (TODO)
│   │
│   └── dto/                            # Data Transfer Objects
│       ├── BacktestRequest.java
│       └── BacktestResponse.java
│
├── src/main/resources/
│   └── application.yml                 # PostgreSQL config added
│
└── pom.xml                            # Maven dependencies updated
```

### Frontend (TypeScript/Next.js)
```
quickfix-dashboard/
├── app/
│   ├── backtest/
│   │   ├── page.tsx                   # (TODO) Backtest form
│   │   └── [id]/
│   │       └── page.tsx               # (TODO) Results view
│   ├── strategies/
│   │   └── page.tsx                   # (TODO) Manage strategies
│   └── ... (existing pages)
│
└── components/
    └── BacktestChart.tsx              # (TODO) Chart component
```

### Infrastructure
```
quickfix-server/
├── docker-compose.yml                 # Updated with TimescaleDB
├── init.sql                           # Database initialization
└── server.py/cfg
```

---

## 🔧 Technical Stack

| Layer | Technology | Version |
|-------|-----------|---------|
| **Frontend** | Next.js | 13+ |
| **Backend** | Spring Boot | 3.2.0 |
| **Java** | OpenJDK | 21 |
| **Database** | PostgreSQL + TimescaleDB | 15 |
| **Time-Series DB** | TimescaleDB | Latest |
| **ORM** | JPA/Hibernate | Spring Data |
| **Trade Execution** | QuickFIX/J | 2.3.1 |
| **Market Data** | EODHD API | REST |
| **Real-Time** | WebSocket | Spring |
| **Math** | Apache Commons Math | 3.6.1 |

---

## 🔐 Database Connection

```yaml
# application.yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/aero_quant
    username: aero_user
    password: aero_password
```

**Start database**:
```bash
cd quickfix-server
docker-compose up -d timescaledb
```

**Verify connection**:
```bash
docker exec aero-timescaledb psql -U aero_user -d aero_quant -c "\dt"
```

---

## 📊 Key Classes & Methods

### IndicatorCalculator
```java
// Static methods - no instance needed
IndicatorCalculator.calculateSMA(prices, 50)          // returns List<Double>
IndicatorCalculator.calculateEMA(prices, 12)
IndicatorCalculator.calculateRSI(prices, 14)          // returns 0-100
IndicatorCalculator.calculateMACD(prices, 12, 26, 9)  // returns Map
IndicatorCalculator.calculateBollingerBands(prices, 20, 2.0)
IndicatorCalculator.calculateATR(highs, lows, closes, 14)
```

### Repository Access
```java
@Autowired
private OHLCVDataRepository ohlcvRepo;

// Find historical data
List<OHLCVData> data = ohlcvRepo.findBySymbolAndDateRange(
    "AAPL", 
    LocalDateTime.of(2023, 1, 1, 0, 0),
    LocalDateTime.of(2024, 1, 1, 0, 0)
);

// Find latest price
Optional<OHLCVData> latest = ohlcvRepo.findLatestBySymbol("AAPL");
```

### Backtest Entity
```java
BacktestResults backtest = new BacktestResults();
backtest.setStrategyName("SMA Crossover");
backtest.setSymbol("AAPL");
backtest.setInitialCapital(BigDecimal.valueOf(10000));
backtest.setFinalValue(BigDecimal.valueOf(12500));
backtest.setSharpeRatio(BigDecimal.valueOf(1.45));
// ... save to database
resultsRepo.save(backtest);
```

---

## 🚀 Typical Workflow

### 1. **Data Preparation**
```
User → Frontend: Request historical data
Frontend → Backend: POST /api/market/fetch-historical?symbol=AAPL&from=2023-01-01&to=2024-01-01
Backend → EODHD API: Fetch daily OHLCV
Backend → TimescaleDB: Store in ohlcv_data table
Response: "Loaded 252 trading days"
```

### 2. **Backtest Execution**
```
User → Frontend: Run backtest with parameters
    strategy: "SMA Crossover"
    symbol: "AAPL"
    startDate: "2023-01-01"
    endDate: "2024-01-01"
    capital: 10000

Frontend → Backend: POST /api/backtest (JSON payload)

Backend:
  1. Load OHLCV data from database
  2. Calculate indicators (SMA50, SMA200)
  3. Initialize BacktestEngine
  4. For each trading day:
     - Calculate signals
     - Execute trades
     - Update portfolio
  5. Calculate metrics (Sharpe, drawdown, win rate)
  6. Save results to database
  
Response: { backtestId: "uuid", status: "COMPLETED", ... }
```

### 3. **Results Visualization**
```
User → Frontend: View backtest results
Frontend → Backend: GET /api/backtest/:id
Backend: Query BacktestResults + TradeLog tables
Response: JSON with metrics, trades, equity curve
Frontend: Render charts (equity curve, drawdown, monthly returns)
```

---

## ✅ Implementation Checklist - Phase 1

### Core (Completed ✅)
- [x] TimescaleDB setup in Docker
- [x] OHLCV data schema
- [x] Technical indicators library
- [x] JPA entities (OHLCVData, BacktestResults, TradeLog)
- [x] Repository interfaces
- [x] Database configuration

### Services (In Progress 🔄)
- [ ] HistoricalDataService (fetch & store OHLCV)
- [ ] BacktestEngine (core engine)
- [ ] MetricsCalculator (Sharpe, drawdown, etc.)
- [ ] StrategyBase classes
- [ ] SMA Crossover Strategy

### API Endpoints (Pending 📋)
- [ ] POST /api/backtest/run
- [ ] GET /api/backtest/:id
- [ ] GET /api/backtest/history
- [ ] GET /api/strategies
- [ ] POST /api/market/fetch-historical

### Frontend (Pending 📋)
- [ ] /app/backtest/page.tsx (form)
- [ ] /app/backtest/[id]/page.tsx (results)
- [ ] BacktestChart component (recharts)
- [ ] Trade list view

---

## 💡 Common Patterns

### Adding a New Indicator
```java
// 1. Add to IndicatorCalculator
public static List<Double> calculateMyIndicator(List<Double> prices, int period) {
    List<Double> result = new ArrayList<>();
    // Implementation
    return result;
}

// 2. Use in strategy
List<Double> myIndicator = IndicatorCalculator.calculateMyIndicator(closes, 14);
if (myIndicator.get(index) > threshold) {
    signal = BUY;
}
```

### Adding a New Strategy
```java
// 1. Create class
@Component
public class MyStrategy extends BaseStrategy {
    @Override
    public Signal generateSignal(...) {
        // Logic here
        return new BuySignal("My reason");
    }
}

// 2. Register in StrategyFactory
strategyFactory.register("MyStrategy", new MyStrategy());

// 3. Use in backtest
BacktestEngine engine = new BacktestEngine("MyStrategy", ...);
```

### Querying Backtest Results
```java
// Get all SMA Crossover backtests
List<BacktestResults> results = repo.findByStrategyName("SMA Crossover");

// Get best performance
BacktestResults best = repo.findBestByStrategyNameBySharpe("SMA Crossover");

// Get recent backtests (last 7 days)
List<BacktestResults> recent = repo.findRecentBacktests(
    LocalDateTime.now().minusDays(7)
);
```

---

## 🐛 Debugging Tips

**Check if database is running**:
```bash
docker ps | grep timescaledb
# or
psql -h localhost -U aero_user -d aero_quant -c "SELECT version();"
```

**Test indicators manually**:
```bash
curl http://localhost:8080/api/test-indicator?prices=100,101,99,102&period=3
```

**Check backtest logs**:
```bash
# In application.yml set:
logging.level.com.aero.quickfix.quant: DEBUG
tail -f logs/application.log
```

**Query database directly**:
```sql
-- Count OHLCV records
SELECT symbol, COUNT(*) as days FROM ohlcv_data GROUP BY symbol;

-- View latest backtest
SELECT * FROM backtest_results ORDER BY created_at DESC LIMIT 1;

-- See all trades from last backtest
SELECT * FROM trade_log WHERE backtest_id = 'uuid-here' ORDER BY trade_date;
```

---

## 📚 Resources

- **TimescaleDB Docs**: https://docs.timescale.com/
- **Spring Data JPA**: https://spring.io/projects/spring-data-jpa
- **Apache Commons Math**: https://commons.apache.org/proper/commons-math/
- **QuickFIX/J**: http://www.quickfixj.org/

---

## 🎓 Learning Path

1. **Understand indicators** → Read IndicatorCalculator source
2. **Learn backtesting flow** → Study BacktestEngine logic
3. **Master strategies** → Create simple SMA Crossover
4. **Add complexity** → Portfolio management, risk limits
5. **Optimize** → Performance tuning, ML integration

---

## 📞 Questions?

For specific implementation details, refer to:
- `PHASE_1_QUANTITATIVE_UPGRADE.md` - Comprehensive overview
- Source code comments in relevant classes
- Test files for usage examples

**Next phase preview**: Phase 2 will add portfolio optimization, risk management, and multi-asset strategies!
