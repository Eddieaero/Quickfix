# Quantitative Analysis Dashboard - Integration Guide

## Overview

The Aero dashboard has been successfully integrated with the Phase 1 quantitative analysis backend, providing a complete backtesting and strategy analysis platform.

## Features Implemented

### 1. **Backtest Page** (`/backtest`)
- Strategy selection with descriptions and requirements
- Historical data date range selection
- Initial capital configuration
- Real-time backtest execution
- Comprehensive metrics display:
  - Total Return, Sharpe Ratio, Sortino Ratio
  - Max Drawdown, Win Rate, Profit Factor
  - Trade statistics and performance indicators
  
### 2. **Strategies Browser** (`/strategies`)
- View all available trading strategies
- Strategy descriptions and requirements
- Required indicators display
- Minimum bars configuration
- Direct link to test each strategy

### 3. **Backtest History** (`/backtest-history`)
- Filter by strategy
- View all past backtest results
- Quick metrics overview for each test
- Drill-down to detailed results

### 4. **Results Details** (`/backtest-results/:id`)
- Complete equity curve visualization
- Drawdown analysis charts
- Detailed trade-by-trade performance log
- Win/loss breakdown
- All 13+ performance metrics

### 5. **Visual Components**
- **Equity Curve Chart**: Portfolio value over time
- **Drawdown Analysis**: Peak-to-trough drawdowns
- **Metrics Grid**: 12+ performance indicators
- **Trade Log**: Individual trade entry/exit details

## Architecture

### API Client Layer
**File**: `lib/api/quantApi.ts`

Centralized API client providing:
- Type-safe methods for all backend endpoints
- Error handling with descriptive messages
- Configurable API base URL via environment variables
- Singleton pattern for instance sharing

```typescript
import { quantApi, BacktestResult } from '@/lib/api/quantApi';

// Example usage in components
const results = await quantApi.runBacktest({
  strategyName: 'SMA Crossover',
  symbol: 'AAPL.US',
  startDate: '2023-01-01',
  endDate: '2024-01-01',
  initialCapital: 100000,
});
```

### Backend Integration

**Base URL**: `http://localhost:8080/api/quant`

**Endpoints**:
1. `POST /backtest` - Execute a backtest
2. `GET /backtest/{id}` - Retrieve specific backtest results
3. `GET /backtest/strategy/{name}` - Get all backtests for a strategy
4. `GET /strategies` - List all available strategies
5. `GET /health` - Health check

### TypeScript Interfaces

All API request/response types are fully typed:

```typescript
interface BacktestRequest {
  strategyName: string;
  symbol: string;
  startDate: string;
  endDate: string;
  initialCapital: number;
}

interface BacktestResult {
  id: string;
  strategyName: string;
  symbol: string;
  startDate: string;
  endDate: string;
  initialCapital: number;
  finalPortfolioValue: number;
  totalReturn: number;
  sharpeRatio?: number;
  sortinoRatio?: number;
  maxDrawdown: number;
  winRate: number;
  profitFactor?: number;
  totalTrades: number;
  winningTrades: number;
  losingTrades: number;
  avgWin: number;
  avgLoss: number;
  portfolioValues: number[];
  trades: Trade[];
  // ... 3 more metrics
}
```

## New Pages and Components

### Pages
- `/backtest` - Main backtest execution page
- `/strategies` - Strategy browser
- `/backtest-history` - Results history and filtering
- `/backtest-results/[id]` - Detailed results view

### Components
- `BacktestMetrics.tsx` - Performance metrics grid
- `BacktestChart.tsx` - Equity curve and drawdown charts
- Both located in `components/quant/`

### Dependencies
- **recharts** (newly installed) - For data visualization
- **lucide-react** - Icons (already present)
- **@/components/ui/card** - Shared card component

## Environment Configuration

**File**: `.env.local`
```env
NEXT_PUBLIC_API_URL=http://localhost:8080/api/quant
```

This allows:
- Easy environment-specific configuration
- Support for different API endpoints (dev/staging/prod)
- No hardcoded URLs in application code

## Navigation Updates

The sidebar has been updated with three new navigation items:
1. **Backtest** (⚡) - Run new backtests
2. **Strategies** (📈) - Browse available strategies
3. **Backtest History** (🕐) - View past results

## Usage Workflow

### Running a Backtest

1. Navigate to `/backtest`
2. Select a strategy from dropdown
3. Choose symbol (e.g., AAPL.US, BTC.USD)
4. Select date range
5. Set initial capital
6. Click "Run Backtest"
7. View instant results with charts and metrics

### Analyzing Results

1. View equity curve growth over time
2. Analyze drawdown periods
3. Compare key metrics:
   - Sharpe Ratio (risk-adjusted returns)
   - Win Rate (% profitable trades)
   - Profit Factor (gross profit / gross loss)
4. Drill into individual trades
5. Filter by strategy in history view

## Performance Metrics Explained

| Metric | Description |
|--------|-------------|
| **Total Return** | % gain/loss over the entire period |
| **Sharpe Ratio** | Risk-adjusted return (higher = better) |
| **Sortino Ratio** | Downside risk-adjusted return |
| **Max Drawdown** | Largest peak-to-trough decline |
| **Win Rate** | % of profitable trades |
| **Profit Factor** | Gross profit ÷ Gross loss |
| **Avg Win/Loss** | Average % per winning/losing trade |

## Backend Requirements

The Phase 1 backend must be running:
- Port: 8080
- Status: Check with `/api/quant/health` endpoint
- Database: PostgreSQL + TimescaleDB for portfolio history

Verify with:
```bash
curl http://localhost:8080/api/quant/health
```

## Styling

All components follow the existing Aero design system:
- **Color Scheme**: Dark theme (gray-900 base)
- **Accent Color**: Blue (blue-600 for CTAs)
- **Status Colors**: Green for wins, Red for losses
- **Tailwind CSS**: All styling via utility classes
- **Responsive**: Mobile-first design for all screen sizes

## Next Steps / Future Enhancements

Potential additions:
- Real-time strategy optimization
- Monte Carlo analysis
- Parameter optimization (GridSearch)
- Trade deviation analysis
- Multi-strategy comparison
- Risk metrics dashboard
- Export results (CSV, PDF)
- Scheduled backtests

## Testing

To test the integration locally:

1. **Start the backend**:
   ```bash
   cd aero
   java -jar target/aero-quickfix-1.0.0.jar
   ```

2. **Start the dashboard**:
   ```bash
   cd quickfix-dashboard
   npm install
   npm run dev
   ```

3. **Access at**: http://localhost:3000

4. **Navigate to**: `/backtest` to test

## Files Modified/Created

### New Files
- `app/backtest/page.tsx`
- `app/strategies/page.tsx`
- `app/backtest-history/page.tsx`
- `app/backtest-results/[id]/page.tsx`
- `components/quant/BacktestMetrics.tsx`
- `components/quant/BacktestChart.tsx`
- `lib/api/quantApi.ts`
- `.env.local`

### Modified Files
- `components/Sidebar.tsx` - Added navigation items

## Troubleshooting

### API Connection Issues
- Verify backend is running on port 8080
- Check `.env.local` has correct `NEXT_PUBLIC_API_URL`
- Check browser console for network errors
- Ensure CORS is enabled on backend

### Chart Not Rendering
- Verify recharts is installed: `npm list recharts`
- Check portfolio values array has data points
- Inspect browser DevTools > Network/Console for errors

### Empty Results
- Verify strategies exist in backend database
- Check date range is valid (start < end)
- Ensure symbol format matches backend expectations (e.g., AAPL.US)

## Support

For issues or questions about the integration:
1. Check backend logs at `aero/logs/`
2. Review API client implementation in `lib/api/quantApi.ts`
3. Verify TypeScript interfaces match backend responses
