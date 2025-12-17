# Investment Module Documentation

## Overview

The Investment Module provides comprehensive portfolio management and compound interest calculation capabilities for the Aero QuickFIX application. Users can create portfolios, manage multiple investments, and calculate returns using compound interest formulas.

## Features

### 1. Portfolio Management
- Create and manage multiple investment portfolios
- Track portfolio performance metrics
- View aggregate portfolio statistics

### 2. Investment Management
- Add various types of investments (stocks, bonds, savings accounts, etc.)
- Track individual investment performance
- Update investment parameters
- Remove investments from portfolios

### 3. Compound Interest Calculations
- Calculate compound interest with customizable parameters
- Support multiple compounding frequencies (daily, weekly, monthly, quarterly, semi-annually, annually)
- Project investment returns over time

## Data Models

### Portfolio
```java
- portfolioId: String (unique identifier)
- userId: String (owner of the portfolio)
- name: String (portfolio name)
- initialInvestment: BigDecimal
- currentValue: BigDecimal (aggregate value of all investments)
- totalGains: BigDecimal (total profit/loss)
- totalGainsPercentage: BigDecimal (ROI percentage)
- createdAt: long (timestamp)
- lastUpdatedAt: long (timestamp)
- investments: List<Investment>
- active: boolean
```

### Investment
```java
- investmentId: String (unique identifier)
- portfolioId: String (parent portfolio)
- assetSymbol: String (ticker symbol or asset identifier)
- assetType: String (e.g., STOCK, BOND, SAVINGS_ACCOUNT)
- principal: BigDecimal (initial investment amount)
- currentValue: BigDecimal (calculated final amount after compound interest)
- annualInterestRate: BigDecimal (percentage)
- compoundingFrequency: int (times per year: 1, 2, 4, 12, 52, 365)
- yearsOfInvestment: int
- investmentDate: long (timestamp)
- maturityDate: long (calculated expiration date)
- totalInterest: BigDecimal (earned interest)
- status: String (ACTIVE, MATURED, CLOSED)
```

## API Endpoints

### Portfolio Operations

#### Create Portfolio
```
POST /api/investments/portfolios
Headers: X-User-Id: <userId>
Body: {
  "name": "Retirement Fund",
  "initialInvestment": 50000
}
Response: Portfolio object
```

#### Get User Portfolios
```
GET /api/investments/portfolios
Headers: X-User-Id: <userId>
Response: List<PortfolioDto>
```

#### Get Portfolio Details
```
GET /api/investments/portfolios/{portfolioId}
Response: Portfolio object
```

#### Delete Portfolio
```
DELETE /api/investments/portfolios/{portfolioId}
Response: 204 No Content
```

### Investment Operations

#### Add Investment to Portfolio
```
POST /api/investments/portfolios/{portfolioId}/investments
Body: {
  "assetSymbol": "AAPL",
  "assetType": "STOCK",
  "principal": 10000,
  "annualInterestRate": 8.5,
  "compoundingFrequency": 12,
  "yearsOfInvestment": 10
}
Response: Investment object
```

#### Get Portfolio Investments
```
GET /api/investments/portfolios/{portfolioId}/investments
Response: List<Investment>
```

#### Get Investment Details
```
GET /api/investments/{investmentId}
Response: Investment object
```

#### Update Investment
```
PUT /api/investments/{investmentId}
Body: {
  "assetSymbol": "AAPL",
  "assetType": "STOCK",
  "principal": 10000,
  "annualInterestRate": 9.0,
  "compoundingFrequency": 12,
  "yearsOfInvestment": 10
}
Response: Investment object
```

#### Remove Investment
```
DELETE /api/investments/{investmentId}
Response: 204 No Content
```

### Compound Interest Calculations

#### Calculate Compound Interest (Direct Calculation)
```
POST /api/investments/calculate
Body: {
  "principal": 10000,
  "annualRate": 5,
  "years": 10,
  "compoundingFrequency": 12
}
Response: {
  "principal": 10000,
  "rate": 5,
  "years": 10,
  "compoundingFrequency": 12,
  "finalAmount": 12820.37,
  "totalInterest": 2820.37,
  "compoundingPeriod": "Monthly"
}
```

#### Get Investment Projection
```
GET /api/investments/projection?principal=10000&rate=5&years=10&frequency=12
Response: CompoundInterestCalculationResponse
```

## Compounding Frequencies

| Frequency Value | Period | Times Per Year |
|-----------------|--------|-----------------|
| 1 | Annually | 1 |
| 2 | Semi-Annually | 2 |
| 4 | Quarterly | 4 |
| 12 | Monthly | 12 |
| 52 | Weekly | 52 |
| 365 | Daily | 365 |

## Compound Interest Formula

The module uses the standard compound interest formula:

**A = P(1 + r/n)^(nt)**

Where:
- **A** = Final Amount
- **P** = Principal (initial investment)
- **r** = Annual Interest Rate (as decimal, e.g., 5% = 0.05)
- **n** = Compounding Frequency per year
- **t** = Time in years
- **Total Interest** = A - P

## Example Usage

### Creating a Portfolio and Adding Investments

```bash
# 1. Create a portfolio
curl -X POST http://localhost:8080/api/investments/portfolios \
  -H "Content-Type: application/json" \
  -H "X-User-Id: USER_123" \
  -d '{
    "name": "Tech Portfolio",
    "initialInvestment": 100000
  }'

# Response:
# {
#   "portfolioId": "PORTFOLIO_1702806400000",
#   "userId": "USER_123",
#   "name": "Tech Portfolio",
#   "initialInvestment": 100000,
#   "currentValue": 100000,
#   "totalGains": 0,
#   "totalGainsPercentage": 0,
#   ...
# }

# 2. Add an investment
curl -X POST http://localhost:8080/api/investments/portfolios/PORTFOLIO_1702806400000/investments \
  -H "Content-Type: application/json" \
  -d '{
    "assetSymbol": "TECH_BOND",
    "assetType": "BOND",
    "principal": 50000,
    "annualInterestRate": 6.5,
    "compoundingFrequency": 12,
    "yearsOfInvestment": 5
  }'

# 3. Calculate returns
curl -X POST http://localhost:8080/api/investments/calculate \
  -H "Content-Type: application/json" \
  -d '{
    "principal": 50000,
    "annualRate": 6.5,
    "years": 5,
    "compoundingFrequency": 12
  }'

# Response:
# {
#   "principal": 50000,
#   "rate": 6.5,
#   "years": 5,
#   "compoundingFrequency": 12,
#   "finalAmount": 65640.18,
#   "totalInterest": 15640.18,
#   "compoundingPeriod": "Monthly"
# }
```

## Architecture

### Components

1. **Models**
   - `Portfolio.java` - Represents a user's investment portfolio
   - `Investment.java` - Represents an individual investment

2. **Repositories**
   - `PortfolioRepository.java` - In-memory storage for portfolios
   - `InvestmentRepository.java` - In-memory storage for investments

3. **Services**
   - `InvestmentService.java` - Business logic for portfolio and investment operations

4. **Controllers**
   - `InvestmentController.java` - REST API endpoints

5. **DTOs**
   - `CreatePortfolioRequest.java` - Portfolio creation request
   - `CreateInvestmentRequest.java` - Investment creation request
   - `CompoundInterestCalculationResponse.java` - Calculation result
   - `PortfolioDto.java` - Portfolio overview response

## Precision and Rounding

- All financial calculations use `BigDecimal` for precision
- Interest calculations maintain 10 decimal places during computation
- Final amounts are rounded to 2 decimal places using `HALF_UP` rounding mode
- Percentages are rounded to 2 decimal places

## Error Handling

- Portfolio not found: Returns 404 Not Found
- Invalid investment parameters: Returns 400 Bad Request
- Missing required headers (X-User-Id): Returns 400 Bad Request

## Future Enhancements

- Database persistence (JPA/Hibernate)
- Real-time investment valuation
- Risk analysis and portfolio optimization
- Historical performance tracking
- Tax calculations
- Dividend management
- Integration with financial market APIs
