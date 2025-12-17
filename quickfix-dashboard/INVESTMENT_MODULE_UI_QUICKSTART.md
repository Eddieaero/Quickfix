# Investment Module UI - Quick Start Guide

## Overview

The investment and portfolio management UI has been successfully integrated into the QuickFIX Dashboard. This guide will help you get started using the new features.

## Getting Started

### 1. Start the Backend Server
```bash
cd aero
mvn spring-boot:run
```

The API will be available at: `http://localhost:8080`

### 2. Start the Frontend Dashboard
```bash
cd quickfix-dashboard
npm install  # if not already installed
npm run dev
```

The dashboard will be available at: `http://localhost:3000`

### 3. Login to the Dashboard
- Navigate to `http://localhost:3000`
- Enter your credentials to login
- You'll be redirected to the main dashboard

## Using the Investment Module

### Accessing Investment Features

The investment module can be accessed from the sidebar:

1. **Portfolios** - Main portfolio management page
2. **Compound Interest** - Compound interest calculator tool

### Workflow

#### Step 1: Create a Portfolio
1. Click "Portfolios" in the sidebar
2. Click "New Portfolio" button
3. Fill in:
   - **Portfolio Name**: e.g., "Retirement Fund"
   - **Initial Investment**: Amount in USD (e.g., 50000)
4. Click "Create"

#### Step 2: Add Investments
1. Click on a portfolio to view its details
2. Click "Add Investment" button
3. Fill in the investment details:
   - **Asset Symbol**: e.g., "AAPL", "BOND_001"
   - **Asset Type**: Choose from dropdown (Stock, Bond, Savings, etc.)
   - **Principal**: Initial investment amount
   - **Interest Rate**: Annual percentage rate (e.g., 5.5)
   - **Years**: Investment duration
   - **Compounding**: How often interest is calculated (Monthly, Quarterly, etc.)
4. Click "Add"

#### Step 3: View Portfolio Performance
The portfolio page displays:
- **Initial Investment**: Amount you invested
- **Current Value**: Current portfolio worth (with compound interest)
- **Total Gains**: Profit/loss in dollars
- **ROI**: Return on investment percentage

Each investment shows:
- Principal amount
- Current value after interest
- Interest earned
- Gains and return percentage
- Investment dates and compounding frequency

#### Step 4: Use the Compound Interest Calculator
1. Click "Compound Interest" in the sidebar
2. Enter:
   - **Principal Amount**: Starting amount
   - **Annual Rate**: Interest rate percentage
   - **Years**: Investment duration
   - **Compounding**: Frequency (Annual, Semi-Annual, Quarterly, Monthly, Weekly, Daily)
3. Click "Calculate"
4. View results with final amount and total interest earned
5. Results are saved to "Calculation History"

## Key Features

### Portfolio Management
✅ Create multiple portfolios  
✅ View portfolio summary metrics  
✅ Track portfolio performance  
✅ Manage multiple investments per portfolio  
✅ Delete portfolios  

### Investment Management
✅ Add investments to portfolios  
✅ Support multiple asset types (Stock, Bond, Savings, Fixed Deposit, Mutual Fund)  
✅ Edit investment parameters  
✅ Delete investments  
✅ View investment metrics  

### Compound Interest Calculator
✅ Calculate returns with custom parameters  
✅ Support 6 compounding frequencies  
✅ View calculation history  
✅ Reference formula and information  

## Menu Structure

```
Dashboard
├── Dashboard (Main trading view)
├── Portfolios (New!)
│   ├── View all portfolios
│   ├── Create new portfolio
│   └── View portfolio details → Manage investments
├── Compound Interest (New!)
│   ├── Calculator
│   └── Calculation history
├── Users (Admin)
└── Settings
```

## Example Scenario

### Building a Retirement Portfolio

**Step 1**: Create Portfolio
- Name: "Retirement 2040"
- Initial Investment: $100,000

**Step 2**: Add Investments

Investment 1: Stocks
- Symbol: "TECH_MIX"
- Type: Stock
- Principal: $40,000
- Rate: 10%
- Years: 10
- Compounding: Monthly

Investment 2: Bonds
- Symbol: "BOND_PORTFOLIO"
- Type: Bond
- Principal: $35,000
- Rate: 5%
- Years: 10
- Compounding: Semi-Annually

Investment 3: Savings
- Symbol: "SAVINGS"
- Type: Savings Account
- Principal: $25,000
- Rate: 3.5%
- Years: 10
- Compounding: Daily

**Step 3**: View Results
- Portfolio shows aggregate metrics
- See each investment's performance
- Total portfolio value and gains displayed

**Step 4**: Plan with Calculator
Use the compound interest calculator to test different scenarios:
- What if I invest $5,000 more?
- What if interest rates increase?
- How much will I have in 20 years?

## Common Tasks

### How to Edit an Investment
1. Go to portfolio details
2. Find the investment
3. Click the edit icon (pencil)
4. Update the details
5. Click "Update"

### How to Delete an Investment
1. Go to portfolio details
2. Find the investment
3. Click the delete icon (trash)
4. Confirm the deletion
5. Portfolio metrics will update automatically

### How to Compare Scenarios
1. Open Compound Interest calculator
2. Enter scenario 1 parameters and calculate
3. Enter scenario 2 parameters and calculate
4. Compare results in the calculation history

## Tips & Best Practices

1. **Use Realistic Interest Rates**
   - Stocks: 8-12% annually
   - Bonds: 3-6% annually
   - Savings Accounts: 2-4% annually
   - Fixed Deposits: 4-7% annually

2. **Choose Appropriate Compounding Frequency**
   - Bonds: Quarterly or Semi-Annually
   - Savings/Fixed Deposits: Monthly
   - Stocks/Mutual Funds: Quarterly

3. **Update Investments Regularly**
   - Review performance monthly
   - Rebalance if needed
   - Adjust interest rates based on market

4. **Use the Calculator for Planning**
   - Test different scenarios
   - Plan retirement goals
   - Compare investment options

## Troubleshooting

### Issue: "Failed to load portfolios"
**Solution**: 
- Ensure backend server is running
- Check API URL in environment variables
- Verify network connectivity

### Issue: Portfolio metrics not updating
**Solution**:
- Refresh the page
- Verify investments are properly saved
- Check backend logs for errors

### Issue: Form validation errors
**Solution**:
- Ensure all required fields are filled
- Check that numbers are positive
- Asset symbol should not be empty

### Issue: Can't login
**Solution**:
- Verify you have valid credentials
- Check that backend is running
- Clear browser cache and try again

## API Details

The UI communicates with these API endpoints:

**Portfolio Endpoints**
- `GET /api/investments/portfolios` - List portfolios
- `POST /api/investments/portfolios` - Create portfolio
- `GET /api/investments/portfolios/{id}` - Get portfolio details
- `DELETE /api/investments/portfolios/{id}` - Delete portfolio

**Investment Endpoints**
- `GET /api/investments/portfolios/{id}/investments` - List investments
- `POST /api/investments/portfolios/{id}/investments` - Add investment
- `PUT /api/investments/{id}` - Update investment
- `DELETE /api/investments/{id}` - Delete investment

**Calculation Endpoint**
- `POST /api/investments/calculate` - Calculate compound interest

## Environment Configuration

Set in `.env.local`:
```
NEXT_PUBLIC_API_URL=http://localhost:8080
```

## Component Architecture

```
Pages
├── /investments → PortfoliosList
├── /investments/[id] → InvestmentsList + Portfolio Details
└── /compound-interest → CompoundInterestCalculator

Components
├── PortfoliosList → Display portfolios with actions
├── InvestmentsList → Display investments in portfolio
├── CreatePortfolioForm → Modal for creating portfolios
├── CreateInvestmentForm → Modal for creating investments
└── CompoundInterestCalculator → Standalone calculator

Sidebar Navigation
├── Link to /investments
└── Link to /compound-interest
```

## Next Steps

1. **Create your first portfolio** - Start with a realistic amount
2. **Add some investments** - Test different asset types
3. **View the results** - See how compound interest grows money
4. **Use the calculator** - Plan different financial scenarios
5. **Explore features** - Edit, delete, and manage investments

## Support

For issues or questions:
1. Check the troubleshooting section above
2. Review the INVESTMENT_UI_GUIDE.md for detailed documentation
3. Check browser console for error messages
4. Verify backend API is responding

## Files Created

**Components** (5 new, 1 updated)
- `components/PortfoliosList.tsx`
- `components/InvestmentsList.tsx`
- `components/CreatePortfolioForm.tsx`
- `components/CreateInvestmentForm.tsx`
- `components/CompoundInterestCalculator.tsx`
- `components/Sidebar.tsx` (updated with menu items)

**Pages** (3 new)
- `app/investments/page.tsx`
- `app/investments/[id]/page.tsx`
- `app/compound-interest/page.tsx`

**Documentation**
- `INVESTMENT_UI_GUIDE.md` (detailed guide)
- `INVESTMENT_MODULE_UI_QUICKSTART.md` (this file)

---

**Ready to manage your investments!** 🚀

For more detailed information, see [INVESTMENT_UI_GUIDE.md](./INVESTMENT_UI_GUIDE.md)
