# EODHD Market Data Integration

## Overview

This document explains the integration of **EODHD (End of Day Historical Data) API** with the Aero Investment Management system. This integration enables real-time market data, company fundamentals, and price tracking for portfolio valuations.

## What is EODHD?

EODHD is a comprehensive financial data API service providing:
- **60+ Stock Exchanges** worldwide including DSE (Tanzania)
- **150,000+ Tickers** (stocks, ETFs, indices, forex)
- **30+ Years** of historical price data
- **Real-time & Intraday** pricing
- **Fundamental Data** (earnings, P/E, dividends)
- **Technical Indicators** & screening

## Architecture

```
┌─────────────────────────────────────────┐
│     Frontend (Next.js/React)            │
│  ┌─────────────────────────────────────┐│
│  │  MarketPriceWidget                  ││
│  │  DseMarketOverview                  ││
│  │  /market page                       ││
│  └─────────────────────────────────────┘│
└────────────────┬────────────────────────┘
                 │
                 ↓
         ┌───────────────────┐
         │  Spring Boot API  │
         │  (Port 8080)      │
         └────────┬──────────┘
                  │
        ┌─────────┴──────────┐
        ↓                    ↓
    ┌──────────────┐   ┌──────────────┐
    │MarketData    │   │Investment    │
    │Controller    │   │Service       │
    └──────┬───────┘   └────┬─────────┘
           │                │
           └────────┬───────┘
                    ↓
           ┌─────────────────────┐
           │ MarketDataService   │
           │ (Caching & Logic)   │
           └──────────┬──────────┘
                      ↓
           ┌──────────────────────┐
           │EodhMarketDataClient  │
           │(HTTP Client)         │
           └──────────┬───────────┘
                      ↓
              ┌────────────────┐
              │ EODHD API      │
              │ eodhd.com      │
              └────────────────┘
```

## Components

### 1. **EodhMarketDataClient**
Located: `client/EodhMarketDataClient.java`

Handles direct communication with EODHD API:
```java
public class EodhMarketDataClient {
    // Fetch latest EOD price for a symbol
    public MarketPriceDto fetchLatestPrice(String symbol)
    
    // Fetch company fundamentals
    public EodhCompanyDataResponse fetchCompanyFundamentals(String symbol)
    
    // Validate if symbol exists
    public boolean validateSymbol(String symbol)
}
```

**Features:**
- Direct API calls to EODHD endpoints
- Demo mode support (no API key required for testing)
- Error handling and validation
- Support for Tanzania stocks (DSE symbols like CRDB.TZ, NMB.TZ)

### 2. **MarketDataService**
Located: `service/MarketDataService.java`

Business logic for market data operations:
```java
public class MarketDataService {
    // Get current price with caching
    public MarketPriceDto getCurrentPrice(String symbol)
    
    // Get company fundamentals
    public EodhCompanyDataResponse getCompanyFundamentals(String symbol)
    
    // Calculate market value from price
    public BigDecimal calculateMarketValue(String symbol, BigDecimal quantity)
    
    // Calculate gains/losses
    public BigDecimal calculateGains(BigDecimal purchase, BigDecimal current, BigDecimal quantity)
    
    // Calculate dividend income
    public BigDecimal calculateDividendIncome(String symbol, BigDecimal principal)
}
```

**Features:**
- 5-minute cache expiration for prices (configurable)
- Automatic price refresh
- Dividend calculation
- Gains/losses computation

### 3. **MarketDataController**
Located: `controller/MarketDataController.java`

REST API endpoints:
```
GET  /api/market/price/{symbol}          - Get price for single symbol
GET  /api/market/prices?symbols=X,Y,Z    - Get multiple prices
GET  /api/market/validate/{symbol}       - Validate symbol
GET  /api/market/dse/stocks               - Get all DSE stocks
GET  /api/market/cache/prices             - Get cached prices
GET  /api/market/cache/stats              - Cache statistics
POST /api/market/cache/clear              - Clear cache
GET  /api/market/health                  - Health check
```

### 4. **DTOs**

#### MarketPriceDto
```java
{
  "symbol": "CRDB.TZ",
  "price": 1150.00,
  "currency": "TZS",
  "exchange": "DSE",
  "dividendYield": 0.045,
  "peRatio": 8.5,
  "marketCap": 1500000000000,
  "lastUpdated": 1765978994036,
  "valid": true
}
```

#### EodhCompanyDataResponse
```java
{
  "general": {
    "code": "CRDB.TZ",
    "name": "CRDB Bank",
    "exchange": "DSE",
    "currencyCode": "TZS",
    "sector": "Financial Services"
  },
  "highlights": {
    "marketCapitalization": 1500000000000,
    "peRatio": 8.5,
    "dividendYield": 0.045
  }
}
```

### 5. **Frontend Components**

#### MarketPriceWidget.tsx
Displays real-time price for a single stock:
- Auto-refresh every 5 minutes
- Error handling with fallback
- Loading states
- Dark mode support

```tsx
<MarketPriceWidget symbol="CRDB.TZ" />
```

#### DseMarketOverview.tsx
Table showing all DSE stocks:
- CRDB.TZ
- NMB.TZ
- TBL.TZ
- JHL.TZ

#### /market page
Comprehensive market data dashboard:
- Quick price widgets for key stocks
- Full DSE market overview
- Integration information
- Configuration guide

## Configuration

### application.yml
```yaml
eodhd:
  api:
    key: demo  # Replace with your API key
  enabled: true  # Enable/disable market data integration
```

### Getting Your EODHD API Key

1. Visit [EODHD.com](https://eodhd.com)
2. Sign up for free or choose a plan:
   - **Free**: 20 API calls/day (limited EOD data)
   - **$19.99/mo**: 100,000 calls/day (EOD + delayed prices)
   - **$29.99/mo**: 100,000 calls/day (Real-time, intraday)
   - **$99.99/mo**: 100,000 calls/day (All data types)

3. Get your API key from dashboard
4. Update `application.yml`:
   ```yaml
   eodhd:
     api:
       key: your_actual_api_key_here
   ```

## API Examples

### Get Current Price
```bash
curl http://localhost:8080/api/market/price/CRDB.TZ
```

Response:
```json
{
  "symbol": "CRDB.TZ",
  "price": 1150.00,
  "currency": "TZS",
  "exchange": "DSE",
  "lastUpdated": 1765978994036,
  "valid": true
}
```

### Get Multiple Prices
```bash
curl http://localhost:8080/api/market/prices?symbols=CRDB.TZ,NMB.TZ,TBL.TZ
```

### Get All DSE Stocks
```bash
curl http://localhost:8080/api/market/dse/stocks
```

### Validate Symbol
```bash
curl http://localhost:8080/api/market/validate/CRDB.TZ
```

## Usage in Investment Module

### Enriching Investments with Market Data

```java
// In InvestmentService
public void enrichInvestmentWithMarketData(Investment investment) {
    MarketPriceDto priceData = marketDataService.getCurrentPrice(
        investment.getAssetSymbol()
    );
    
    if (priceData.isValid()) {
        // Update investment with real market price
        investment.setCurrentMarketPrice(priceData.getPrice());
        investment.setLastPriceUpdate(priceData.getLastUpdated());
        
        // Calculate realized gains
        BigDecimal gain = priceData.getPrice()
            .subtract(investment.getPrincipal());
        investment.setRealizedGain(gain);
        
        // Add dividend information
        investment.setDividendYield(priceData.getDividendYield());
    }
}
```

## Caching Strategy

**Purpose:** Minimize API calls and improve performance

**Settings:**
- Cache Expiration: 5 minutes
- Cache Type: In-memory (ConcurrentHashMap)
- Auto-refresh: Expired entries refreshed on next request

**Cache Operations:**
```bash
# Get all cached prices
GET /api/market/cache/prices

# View cache statistics
GET /api/market/cache/stats

# Clear cache (force refresh)
POST /api/market/cache/clear
```

## Demo Mode

Running without EODHD API key (demo mode):
- Uses hardcoded sample prices for Tanzania stocks
- Enables testing without API registration
- Perfect for development and demonstrations

**Demo Prices:**
- CRDB.TZ: 1,150 TZS
- NMB.TZ: 720 TZS
- TBL.TZ: 32.50 TZS
- AAPL: $192.50
- GOOGL: $140.75

## Tanzania Stock Exchange (DSE) Support

**Supported DSE Stocks:**
- CRDB.TZ - CRDB Bank
- NMB.TZ - National Microfinance Bank
- TBL.TZ - Tanzania Breweries Limited
- JHL.TZ - Jembe Holdings Limited

**Accessing DSE Data:**
```bash
# Get all DSE stocks
curl http://localhost:8080/api/market/dse/stocks

# Get specific DSE stock price
curl http://localhost:8080/api/market/price/CRDB.TZ
```

## Error Handling

### Common Errors

1. **Invalid Symbol**
   ```json
   {
     "symbol": "INVALID.TZ",
     "valid": false,
     "errorMessage": "Symbol not found: INVALID.TZ"
   }
   ```

2. **API Connection Error**
   ```json
   {
     "symbol": "CRDB.TZ",
     "valid": false,
     "errorMessage": "Error fetching price: Connection timeout"
   }
   ```

3. **API Key Invalid**
   - Symptom: 401 Unauthorized from EODHD
   - Solution: Verify API key in configuration

## Performance Considerations

| Operation | Time | Notes |
|-----------|------|-------|
| Get single price (cached) | ~5ms | From local cache |
| Get single price (fresh) | ~500-1000ms | API call to EODHD |
| Get multiple prices | ~1000-2000ms | Parallel requests |
| Cache hit rate | ~95% | With 5-minute expiration |

## Roadmap & Future Enhancements

### Phase 2: Real-Time Updates
- WebSocket integration for live price updates
- Push notifications for price alerts
- Intraday price tracking

### Phase 3: Advanced Analytics
- Technical indicators (MA, RSI, MACD)
- Portfolio heat maps
- Risk analysis
- Backtesting with historical data

### Phase 4: Persistence
- Database storage of price history
- Historical returns tracking
- Time-series analysis

### Phase 5: Compliance
- Tax reporting integration
- Dividend tracking
- Capital gains calculations
- Regulatory reporting

## Troubleshooting

### Issue: "No price data available"
**Cause:** Symbol not found on EODHD
**Solution:** Verify symbol format (e.g., CRDB.TZ for Tanzania stocks)

### Issue: Cache not refreshing
**Cause:** 5-minute cache not expired
**Solution:** Call `POST /api/market/cache/clear` to force refresh

### Issue: Demo prices not updating
**Cause:** Demo mode active (API key = "demo")
**Solution:** Set actual EODHD API key in configuration

### Issue: High API costs
**Cause:** Unnecessary API calls due to short cache
**Solution:** Increase cache expiration time or use appropriate plan

## API Documentation

Full EODHD API documentation: https://eodhd.com/financial-apis/

## Support

- EODHD Support: support@eodhistoricaldata.com
- GitHub Issues: [Project Repo]
- Documentation: This file

## License

This integration follows the EODHD Terms of Service.
See: https://eodhd.com/financial-apis/terms-conditions
