# EODHD Integration - Quick Start Guide

## What You Get

✅ Real-time market prices for Tanzania stocks (DSE)  
✅ Company fundamentals (dividends, P/E ratios)  
✅ Automatic price caching (5-minute expiration)  
✅ Portfolio valuation with live prices  
✅ Demo mode for testing without API key  

## Files Added

### Backend (Java)
- `client/EodhMarketDataClient.java` - API client
- `service/MarketDataService.java` - Business logic
- `controller/MarketDataController.java` - REST endpoints
- `config/AppConfig.java` - RestTemplate configuration
- DTOs:
  - `dto/MarketPriceDto.java`
  - `dto/EodhPriceResponse.java`
  - `dto/EodhCompanyDataResponse.java`

### Frontend (React/TypeScript)
- `components/MarketPriceWidget.tsx` - Price widget component
- `components/DseMarketOverview.tsx` - Market overview table
- `app/market/page.tsx` - Market data page
- Updated: `components/Sidebar.tsx` - Added market menu

### Configuration
- Updated: `src/main/resources/application.yml`

### Documentation
- `EODHD_INTEGRATION.md` - Full integration guide
- This file

## Quick Setup (5 minutes)

### Step 1: Start Backend
Backend is already built and running:
```bash
# Verify server is running
curl http://localhost:8080/api/market/health

# Response:
# {"status":"healthy","service":"market-data","timestamp":...}
```

### Step 2: Test Demo Mode
Try fetching prices (demo mode enabled):
```bash
# Get CRDB price
curl http://localhost:8080/api/market/price/CRDB.TZ

# Get all DSE stocks
curl http://localhost:8080/api/market/dse/stocks

# Get multiple prices
curl http://localhost:8080/api/market/prices?symbols=CRDB.TZ,NMB.TZ,TBL.TZ
```

### Step 3: View in Frontend
Start the frontend and navigate to `/market`:
```bash
cd quickfix-dashboard
npm run dev
# Open http://localhost:3000/market
```

### Step 4: Optional - Add Real EODHD API Key

1. **Sign up at EODHD**
   - Visit: https://eodhd.com/register
   - Free plan includes 20 API calls/day
   - Paid plans from $19.99/month

2. **Get your API key**
   - From dashboard at: https://eodhd.com/cp/dashboard
   - API key format: Long alphanumeric string

3. **Update configuration**
   Edit `aero/src/main/resources/application.yml`:
   ```yaml
   eodhd:
     api:
       key: YOUR_API_KEY_HERE  # Replace with actual key
     enabled: true
   ```

4. **Rebuild and restart**
   ```bash
   cd aero
   mvn clean package -DskipTests
   kill -9 $(lsof -ti:8080)  # Kill old process
   java -jar target/aero-quickfix-1.0.0.jar &
   ```

## API Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/market/price/{symbol}` | GET | Get single price |
| `/api/market/prices` | GET | Get multiple prices |
| `/api/market/validate/{symbol}` | GET | Validate symbol |
| `/api/market/dse/stocks` | GET | All DSE stocks |
| `/api/market/cache/prices` | GET | All cached prices |
| `/api/market/cache/stats` | GET | Cache statistics |
| `/api/market/cache/clear` | POST | Clear cache |
| `/api/market/health` | GET | Health check |

## Usage Examples

### Get Price
```javascript
// In React component
import axios from 'axios';

const response = await axios.get('http://localhost:8080/api/market/price/CRDB.TZ');
const price = response.data.price; // 1150.00
const currency = response.data.currency; // "TZS"
```

### In Investment Service
```java
// Enrich investment with market data
MarketPriceDto priceData = marketDataService.getCurrentPrice("CRDB.TZ");

investment.setCurrentMarketPrice(priceData.getPrice());
investment.setLastPriceUpdate(priceData.getLastUpdated());
investment.setMarketDataAvailable(priceData.isValid());
```

## Tanzania Stocks Supported

| Symbol | Company | Exchange |
|--------|---------|----------|
| CRDB.TZ | CRDB Bank | DSE |
| NMB.TZ | National Microfinance Bank | DSE |
| TBL.TZ | Tanzania Breweries Limited | DSE |
| JHL.TZ | Jembe Holdings Limited | DSE |

## Demo Mode Features

When `eodhd.api.key = "demo"`:
- ✅ All API endpoints work
- ✅ Returns sample prices
- ✅ No EODHD API calls
- ✅ Perfect for testing
- ❌ Prices are static (not real-time)

## Troubleshooting

### Issue: 404 on /api/market endpoints
**Solution:** 
- Ensure backend is running: `curl http://localhost:8080/api/market/health`
- Rebuild if needed: `mvn clean package`

### Issue: No prices in demo mode
**Solution:** 
- Demo prices only exist for specific symbols (CRDB.TZ, NMB.TZ, etc.)
- Check symbol format

### Issue: Too many API calls with real key
**Solution:** 
- Increase cache expiration: Edit `MarketDataService` → `CACHE_EXPIRATION_MS`
- Default is 5 minutes

## Next Steps

1. ✅ Deploy backend with EODHD integration
2. ✅ View market data in frontend
3. ⏭️ **Next:** Integrate portfolio valuations with live prices
4. ⏭️ Add buy/sell order simulation with current prices
5. ⏭️ Create portfolio performance dashboard

## Integration with Investments

The market data service automatically enriches investments:

```java
// In InvestmentService.addInvestment()
Investment investment = new Investment(...);
enrichInvestmentWithMarketData(investment);  // Adds live price
investmentRepository.save(investment);
```

Now investments show:
- **Current Market Price** (from EODHD)
- **Realized Gains** (current price vs cost)
- **Dividend Yield** (from company data)
- **Portfolio Value** (calculated from live prices)

## Cost Analysis

| Scenario | Cost/Month | API Calls/Day |
|----------|-----------|---------------|
| Demo (no API key) | $0 | Unlimited |
| Free plan | $0 | 20 |
| Basic ($19.99) | $19.99 | 100,000 |
| Standard ($29.99) | $29.99 | 100,000 |
| Premium ($99.99) | $99.99 | 100,000 |

For MVP: **Free plan is sufficient**  
With 20 calls/day, can fetch prices for ~5 stocks daily

## Support Resources

- EODHD Docs: https://eodhd.com/financial-apis/
- API Examples: https://eodhd.com/financial-apis/api-examples/
- DSE Coverage: https://eodhd.com/list-of-stock-markets
- Help: support@eodhistoricaldata.com

## What's Next?

After integrating market data, consider:

1. **Database Persistence** - Store price history
2. **Real-time Updates** - WebSocket for live prices
3. **Alerts** - Notify when prices hit targets
4. **Advanced Analytics** - Technical indicators, charts
5. **Portfolio Rebalancing** - Auto-rebalance based on prices
6. **Compliance** - Tax and regulatory reporting

---

**Status:** ✅ Backend complete and running  
**Frontend:** ✅ Components created and ready  
**Documentation:** ✅ Complete  
**Ready for:** Production testing with real EODHD API key
