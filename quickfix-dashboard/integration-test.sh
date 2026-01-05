#!/bin/bash

# Phase 1 Frontend Integration Test Script
# This script verifies that all frontend components are properly integrated

echo "=================================================="
echo "Aero Dashboard - Phase 1 Integration Test"
echo "=================================================="
echo ""

# Test 1: Check if all new files exist
echo "✓ Testing file structure..."
FILES=(
  "app/backtest/page.tsx"
  "app/strategies/page.tsx"
  "app/backtest-history/page.tsx"
  "app/backtest-results/[id]/page.tsx"
  "components/quant/BacktestMetrics.tsx"
  "components/quant/BacktestChart.tsx"
  "lib/api/quantApi.ts"
  ".env.local"
)

missing=0
for file in "${FILES[@]}"; do
  if [ -f "$file" ]; then
    echo "  ✓ $file"
  else
    echo "  ✗ MISSING: $file"
    ((missing++))
  fi
done

if [ $missing -eq 0 ]; then
  echo "✓ All files present!"
else
  echo "✗ $missing file(s) missing!"
fi
echo ""

# Test 2: Check dependencies
echo "✓ Checking dependencies..."
if npm list recharts > /dev/null 2>&1; then
  echo "  ✓ recharts installed"
else
  echo "  ✗ recharts NOT installed (run: npm install recharts)"
fi

if npm list lucide-react > /dev/null 2>&1; then
  echo "  ✓ lucide-react installed"
else
  echo "  ✗ lucide-react NOT installed"
fi
echo ""

# Test 3: Check environment configuration
echo "✓ Checking environment configuration..."
if grep -q "NEXT_PUBLIC_API_URL" .env.local; then
  api_url=$(grep "NEXT_PUBLIC_API_URL" .env.local)
  echo "  ✓ $api_url"
else
  echo "  ✗ NEXT_PUBLIC_API_URL not configured in .env.local"
fi
echo ""

# Test 4: Backend connectivity test
echo "✓ Testing backend connectivity..."
echo "  (Checking http://localhost:8080/api/quant/health)"

response=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/quant/health)
if [ "$response" = "200" ]; then
  echo "  ✓ Backend is running and responding (HTTP $response)"
else
  echo "  ✗ Backend not responding (HTTP $response)"
  echo "    Start backend with: cd aero && java -jar target/aero-quickfix-1.0.0.jar"
fi
echo ""

# Test 5: TypeScript compilation check
echo "✓ Checking TypeScript compilation..."
npm run build --quiet 2>/dev/null
if [ $? -eq 0 ]; then
  echo "  ✓ TypeScript compiles successfully"
else
  echo "  ✗ TypeScript compilation errors"
  echo "    Run 'npm run build' to see details"
fi
echo ""

# Test 6: API client imports
echo "✓ Checking API client exports..."
if grep -q "export const quantApi" lib/api/quantApi.ts; then
  echo "  ✓ quantApi singleton exported"
else
  echo "  ✗ quantApi singleton not exported"
fi

if grep -q "interface BacktestRequest" lib/api/quantApi.ts; then
  echo "  ✓ BacktestRequest interface defined"
else
  echo "  ✗ BacktestRequest interface missing"
fi

if grep -q "interface BacktestResult" lib/api/quantApi.ts; then
  echo "  ✓ BacktestResult interface defined"
else
  echo "  ✗ BacktestResult interface missing"
fi
echo ""

# Test 7: Navigation updates
echo "✓ Checking sidebar navigation..."
if grep -q "Backtest" components/Sidebar.tsx; then
  echo "  ✓ Backtest navigation item added"
else
  echo "  ✗ Backtest navigation item missing"
fi

if grep -q "Strategies" components/Sidebar.tsx; then
  echo "  ✓ Strategies navigation item added"
else
  echo "  ✗ Strategies navigation item missing"
fi

if grep -q "Backtest History" components/Sidebar.tsx; then
  echo "  ✓ Backtest History navigation item added"
else
  echo "  ✗ Backtest History navigation item missing"
fi
echo ""

echo "=================================================="
echo "Integration Test Complete!"
echo "=================================================="
echo ""
echo "Next steps:"
echo "1. Start backend:  cd aero && java -jar target/aero-quickfix-1.0.0.jar"
echo "2. Start frontend: npm run dev"
echo "3. Open browser:   http://localhost:3000"
echo "4. Navigate to:    http://localhost:3000/backtest"
echo ""
