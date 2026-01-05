# 🚀 Phase 1 Frontend Integration - Quick Reference

## ✨ What's New

### New Pages (4)
| Page | Route | Purpose |
|------|-------|---------|
| Backtest | `/backtest` | Execute backtests with real-time results |
| Strategies | `/strategies` | Browse and select trading strategies |
| Backtest History | `/backtest-history` | Filter and view past backtests |
| Results Details | `/backtest-results/:id` | Detailed metrics and trade analysis |

### New Components (2)
| Component | Location | Displays |
|-----------|----------|----------|
| BacktestMetrics | `components/quant/` | 12+ performance metrics |
| BacktestChart | `components/quant/` | Equity curve & drawdown charts |

### New Files (8)
```
✓ lib/api/quantApi.ts            (API client - 150+ lines)
✓ app/backtest/page.tsx          (Main execution page)
✓ app/strategies/page.tsx         (Strategy browser)
✓ app/backtest-history/page.tsx   (Results history)
✓ app/backtest-results/[id]/page.tsx (Details page)
✓ components/quant/BacktestMetrics.tsx
✓ components/quant/BacktestChart.tsx
✓ .env.local                      (Environment config)
```

## 🎯 Quick Start (5 minutes)

### 1️⃣ Start the Backend
```bash
cd /Users/pro/Documents/projects/project19-Aero/aero
java -jar target/aero-quickfix-1.0.0.jar
# Wait for: "Tomcat started on port(s): 8080"
```

### 2️⃣ Start the Frontend
```bash
cd /Users/pro/Documents/projects/project19-Aero/quickfix-dashboard
npm run dev
# Opens automatically at http://localhost:3000
```

### 3️⃣ Open Dashboard
```
Browser: http://localhost:3000
Navigate: Sidebar → Backtest
```

### 4️⃣ Run Your First Backtest
```
1. Select Strategy: "SMA Crossover" (or any available)
2. Symbol: "AAPL.US"
3. Dates: 2023-01-01 to 2024-01-01
4. Capital: 100000
5. Click: "Run Backtest"
6. View: Results & Charts (instant!)
```

## 📊 Performance Metrics (12+)

| Metric | What It Shows | Good Value |
|--------|---------------|-----------|
| Total Return | Overall profit/loss % | > 10% |
| Sharpe Ratio | Risk-adjusted return | > 1.0 |
| Sortino Ratio | Downside-adjusted return | > 1.5 |
| Max Drawdown | Worst peak-to-trough loss | < -20% |
| Win Rate | % of profitable trades | > 50% |
| Profit Factor | Gross profit / Gross loss | > 1.5 |
| Avg Win % | Average winning trade | Positive |
| Avg Loss % | Average losing trade | Negative |

## 🔧 API Endpoints

### Base URL
```
http://localhost:8080/api/quant
```

### Endpoints
```
POST   /backtest
  → Execute backtest
  → Input: BacktestRequest
  → Output: BacktestResult

GET    /strategies
  → List all strategies
  → Output: Strategy[]

GET    /backtest/{id}
  → Get specific results
  → Output: BacktestResult

GET    /backtest/strategy/{name}
  → Get all backtests for strategy
  → Output: BacktestResult[]

GET    /health
  → Health check
  → Output: { status: "UP" }
```

## 💻 Code Examples

### Import API Client
```typescript
import { quantApi, BacktestResult } from '@/lib/api/quantApi';
```

### Run Backtest Programmatically
```typescript
const results = await quantApi.runBacktest({
  strategyName: 'SMA Crossover',
  symbol: 'AAPL.US',
  startDate: '2023-01-01',
  endDate: '2024-01-01',
  initialCapital: 100000,
});
```

### Get All Strategies
```typescript
const strategies = await quantApi.getStrategies();
strategies.forEach(s => {
  console.log(`${s.name}: ${s.description}`);
});
```

### Fetch Specific Results
```typescript
const result = await quantApi.getBacktestResults('result-id-123');
console.log(`Return: ${(result.totalReturn * 100).toFixed(2)}%`);
console.log(`Sharpe: ${result.sharpeRatio?.toFixed(3)}`);
```

## 🎨 UI Navigation

### Sidebar Links (New!)
```
Dashboard           (old)
Portfolios          (old)
Market Data         (old)
Compound Interest   (old)
─────────────────────────
⚡ Backtest         (NEW) ← Click here first!
📈 Strategies       (NEW)
🕐 Backtest History (NEW)
─────────────────────────
Users               (old)
Settings            (old)
```

## 📝 File Structure

```
quickfix-dashboard/
├── app/
│   ├── backtest/
│   │   └── page.tsx              ← Main backtest page
│   ├── strategies/
│   │   └── page.tsx              ← Strategy browser
│   ├── backtest-history/
│   │   └── page.tsx              ← Results history
│   └── backtest-results/
│       └── [id]/
│           └── page.tsx          ← Details page
│
├── components/
│   ├── quant/                    ← New folder
│   │   ├── BacktestMetrics.tsx
│   │   └── BacktestChart.tsx
│   └── Sidebar.tsx               ← Updated
│
├── lib/
│   └── api/
│       └── quantApi.ts           ← New API client
│
├── .env.local                    ← New config
└── package.json                  ← recharts added
```

## 🔍 Testing Checklist

```
✅ Backend running on port 8080
✅ Frontend running on port 3000
✅ API health check: http://localhost:8080/api/quant/health
✅ Sidebar navigation appears
✅ /backtest page loads
✅ Strategy dropdown populated
✅ Form inputs work
✅ Backtest executes and shows results
✅ Charts render properly
✅ Metrics display correctly
✅ Error handling works (try invalid date)
✅ /strategies page shows all strategies
✅ /backtest-history filters by strategy
```

## 🐛 Troubleshooting

### "Cannot GET /backtest"
- ❌ Frontend not running
- ✅ Start: `cd quickfix-dashboard && npm run dev`

### "Connection refused: localhost:8080"
- ❌ Backend not running
- ✅ Start: `cd aero && java -jar target/aero-quickfix-1.0.0.jar`

### "Strategy dropdown is empty"
- ❌ Backend strategies table empty or API failing
- ✅ Check: `curl http://localhost:8080/api/quant/strategies`

### "Charts not showing"
- ❌ recharts not installed
- ✅ Run: `npm install recharts`

### "TypeScript error in component"
- ❌ Type mismatch between frontend and backend
- ✅ Check: `lib/api/quantApi.ts` interfaces match API response

### "Results show but chart is blank"
- ❌ portfolioValues array is empty
- ✅ Check: Backend is calculating and returning values

## 📦 Dependencies Added

```json
{
  "recharts": "^2.10.0"  // Charts library
}
```

**All other dependencies were already present:**
- next.js
- react
- typescript
- tailwindcss
- lucide-react

## 🌐 Environment Configuration

### File: `.env.local`
```bash
NEXT_PUBLIC_API_URL=http://localhost:8080/api/quant
```

### Usage in Code
```typescript
const baseUrl = process.env.NEXT_PUBLIC_API_URL;
// Returns: "http://localhost:8080/api/quant"
```

## 🚀 Deployment Tips

### Development
```bash
npm run dev              # Start dev server with hot reload
npm run build            # Check for TypeScript errors
```

### Production
```bash
npm run build            # Creates .next/ optimized build
npm run start            # Serves production build
NODE_ENV=production npm run start
```

### Environment Variables
Update `NEXT_PUBLIC_API_URL` for:
- **Local**: `http://localhost:8080/api/quant`
- **Staging**: `https://staging-api.example.com/api/quant`
- **Production**: `https://api.example.com/api/quant`

## 📚 Documentation Files

| File | Purpose |
|------|---------|
| QUANT_INTEGRATION_GUIDE.md | Detailed architecture & usage |
| PHASE1_FRONTEND_COMPLETE.md | Implementation summary |
| ARCHITECTURE_DIAGRAM.md | System diagrams & flow |
| This file | Quick reference & troubleshooting |

## 🎓 Learning Resources

### Understanding the Architecture
1. Read: QUANT_INTEGRATION_GUIDE.md
2. Look at: ARCHITECTURE_DIAGRAM.md
3. Browse: `lib/api/quantApi.ts`

### Creating New Features
1. Import: `quantApi` from `lib/api/quantApi.ts`
2. Use TypeScript: All interfaces are defined
3. Handle errors: Use try-catch pattern
4. Style: Use Tailwind CSS classes (dark theme)

### Modifying Existing Pages
1. Edit: `app/*/page.tsx`
2. Change: Component JSX and logic
3. Update: States and handlers
4. Test: In browser at http://localhost:3000

## ⚡ Performance Notes

- **Load Time**: Charts lazy-load with Recharts
- **Type Safety**: Full TypeScript prevents runtime errors
- **API Calls**: Minimal - only what's needed
- **Bundle Size**: No heavy dependencies added
- **Responsiveness**: Mobile-first design works on all screens

## 🔐 Security Notes

- ✅ No API keys in frontend code
- ✅ CORS configured on backend
- ✅ Environment variables for sensitive config
- ✅ Type checking prevents injection attacks
- ✅ Error messages don't expose internals

## 📞 Support Matrix

| Issue | Where | How |
|-------|-------|-----|
| Frontend won't load | Browser console | Check `/backtest` route exists |
| API not responding | Network tab | Verify backend on port 8080 |
| Metrics not showing | React DevTools | Check BacktestResult interface |
| Charts broken | Console | Verify recharts installation |
| Styles missing | Inspector | Check Tailwind classes applied |

---

**🎉 You're all set! Start with step 1 above and enjoy backtesting!**

**Need help?** Check the detailed guides:
- 📖 QUANT_INTEGRATION_GUIDE.md (full documentation)
- 📊 ARCHITECTURE_DIAGRAM.md (system design)
- ❓ PHASE1_FRONTEND_COMPLETE.md (implementation details)
