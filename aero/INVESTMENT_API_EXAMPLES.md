# Investment Module - API Usage Examples

## Quick Start Examples

### 1. Create a Portfolio

```bash
curl -X POST http://localhost:8080/api/investments/portfolios \
  -H "Content-Type: application/json" \
  -H "X-User-Id: user123" \
  -d '{
    "name": "Retirement Savings",
    "initialInvestment": 100000
  }'
```

**Response:**
```json
{
  "portfolioId": "PORTFOLIO_1702809600000",
  "userId": "user123",
  "name": "Retirement Savings",
  "initialInvestment": 100000,
  "currentValue": 100000,
  "totalGains": 0,
  "totalGainsPercentage": 0,
  "createdAt": 1702809600000,
  "lastUpdatedAt": 1702809600000,
  "investments": [],
  "active": true
}
```

---

### 2. Add a Stock Investment

```bash
curl -X POST http://localhost:8080/api/investments/portfolios/PORTFOLIO_1702809600000/investments \
  -H "Content-Type: application/json" \
  -d '{
    "assetSymbol": "AAPL",
    "assetType": "STOCK",
    "principal": 25000,
    "annualInterestRate": 8.5,
    "compoundingFrequency": 12,
    "yearsOfInvestment": 10
  }'
```

**Response:**
```json
{
  "investmentId": "INV_1702809650000",
  "portfolioId": "PORTFOLIO_1702809600000",
  "assetSymbol": "AAPL",
  "assetType": "STOCK",
  "principal": 25000,
  "currentValue": 54762.30,
  "annualInterestRate": 8.5,
  "compoundingFrequency": 12,
  "yearsOfInvestment": 10,
  "investmentDate": 1702809650000,
  "maturityDate": 1734345650000,
  "totalInterest": 29762.30,
  "status": "ACTIVE"
}
```

---

### 3. Add a Bond Investment

```bash
curl -X POST http://localhost:8080/api/investments/portfolios/PORTFOLIO_1702809600000/investments \
  -H "Content-Type: application/json" \
  -d '{
    "assetSymbol": "BOND_GOVT",
    "assetType": "BOND",
    "principal": 30000,
    "annualInterestRate": 5.2,
    "compoundingFrequency": 4,
    "yearsOfInvestment": 5
  }'
```

---

### 4. Add a Savings Account

```bash
curl -X POST http://localhost:8080/api/investments/portfolios/PORTFOLIO_1702809600000/investments \
  -H "Content-Type: application/json" \
  -d '{
    "assetSymbol": "SAVINGS_ACC",
    "assetType": "SAVINGS_ACCOUNT",
    "principal": 20000,
    "annualInterestRate": 3.5,
    "compoundingFrequency": 12,
    "yearsOfInvestment": 3
  }'
```

---

### 5. Get All Portfolios for User

```bash
curl -X GET http://localhost:8080/api/investments/portfolios \
  -H "X-User-Id: user123"
```

**Response:**
```json
[
  {
    "portfolioId": "PORTFOLIO_1702809600000",
    "name": "Retirement Savings",
    "initialInvestment": 100000,
    "currentValue": 105725.50,
    "totalGains": 5725.50,
    "totalGainsPercentage": 5.73,
    "createdAt": 1702809600000,
    "lastUpdatedAt": 1702809700000,
    "investmentCount": 3,
    "active": true
  }
]
```

---

### 6. Get Portfolio Details

```bash
curl -X GET http://localhost:8080/api/investments/portfolios/PORTFOLIO_1702809600000
```

---

### 7. Get All Investments in Portfolio

```bash
curl -X GET http://localhost:8080/api/investments/portfolios/PORTFOLIO_1702809600000/investments
```

---

### 8. Calculate Compound Interest (No Investment Created)

```bash
curl -X POST http://localhost:8080/api/investments/calculate \
  -H "Content-Type: application/json" \
  -d '{
    "principal": 10000,
    "annualRate": 6.5,
    "years": 5,
    "compoundingFrequency": 12
  }'
```

**Response:**
```json
{
  "principal": 10000,
  "rate": 6.5,
  "years": 5,
  "compoundingFrequency": 12,
  "finalAmount": 13488.50,
  "totalInterest": 3488.50,
  "compoundingPeriod": "Monthly"
}
```

---

### 9. Get Investment Projection (Query Parameters)

```bash
curl -X GET "http://localhost:8080/api/investments/projection?principal=50000&rate=7&years=20&frequency=12"
```

---

### 10. Get Single Investment

```bash
curl -X GET http://localhost:8080/api/investments/INV_1702809650000
```

---

### 11. Update Investment

```bash
curl -X PUT http://localhost:8080/api/investments/INV_1702809650000 \
  -H "Content-Type: application/json" \
  -d '{
    "assetSymbol": "AAPL",
    "assetType": "STOCK",
    "principal": 25000,
    "annualInterestRate": 9.0,
    "compoundingFrequency": 12,
    "yearsOfInvestment": 10
  }'
```

---

### 12. Remove Investment

```bash
curl -X DELETE http://localhost:8080/api/investments/INV_1702809650000
```

---

### 13. Delete Portfolio

```bash
curl -X DELETE http://localhost:8080/api/investments/portfolios/PORTFOLIO_1702809600000
```

---

## Common Investment Scenarios

### Scenario 1: Education Fund (5 Years, Fixed Deposits)

Create a portfolio and add fixed deposits with monthly compounding:

```bash
# Create portfolio
PORTFOLIO_ID=$(curl -s -X POST http://localhost:8080/api/investments/portfolios \
  -H "Content-Type: application/json" \
  -H "X-User-Id: education_user" \
  -d '{
    "name": "Child Education Fund",
    "initialInvestment": 100000
  }' | jq -r '.portfolioId')

# Add fixed deposit (5-year plan, 5.5% annual interest, monthly compounding)
curl -X POST http://localhost:8080/api/investments/portfolios/$PORTFOLIO_ID/investments \
  -H "Content-Type: application/json" \
  -d '{
    "assetSymbol": "FD_5YR",
    "assetType": "FIXED_DEPOSIT",
    "principal": 100000,
    "annualInterestRate": 5.5,
    "compoundingFrequency": 12,
    "yearsOfInvestment": 5
  }'
```

Expected return: ~$129,389 (interest: ~$29,389)

---

### Scenario 2: Emergency Fund (Liquid, Daily Compounding)

```bash
curl -X POST http://localhost:8080/api/investments/portfolios/YOUR_PORTFOLIO_ID/investments \
  -H "Content-Type: application/json" \
  -d '{
    "assetSymbol": "SAVINGS",
    "assetType": "SAVINGS_ACCOUNT",
    "principal": 50000,
    "annualInterestRate": 3.0,
    "compoundingFrequency": 365,
    "yearsOfInvestment": 1
  }'
```

Expected return: ~$51,521 (interest: ~$1,521)

---

### Scenario 3: Long-term Growth (15 Years)

```bash
curl -X POST http://localhost:8080/api/investments/portfolios/YOUR_PORTFOLIO_ID/investments \
  -H "Content-Type: application/json" \
  -d '{
    "assetSymbol": "MF_EQUITY",
    "assetType": "MUTUAL_FUND",
    "principal": 500000,
    "annualInterestRate": 12.0,
    "compoundingFrequency": 4,
    "yearsOfInvestment": 15
  }'
```

Expected return: ~$2,873,615 (interest: ~$2,373,615)

---

## Error Handling

### Portfolio Not Found
```
Status: 404 Not Found
```

### Invalid Parameters
```
Status: 400 Bad Request
Response: (typically from malformed JSON or invalid data types)
```

### Missing User ID Header
```
Status: 400 Bad Request
Message: Missing required header 'X-User-Id'
```

---

## Tips for Using the Investment Module

1. **Always provide X-User-Id header** when creating/retrieving portfolios
2. **Use appropriate compounding frequencies** based on investment type:
   - Bonds: Quarterly (4) or Semi-annual (2)
   - Savings: Monthly (12) or Daily (365)
   - Mutual Funds: Quarterly (4)
   - Fixed Deposits: Monthly (12)

3. **Annual Interest Rate** should be provided as a percentage (e.g., 5 for 5%)
4. **Review portfolio gains** regularly using the portfolio details endpoint
5. **Use the calculate endpoint** for quick projections without creating investments

---

## Integration with Frontend

The frontend can use these endpoints to:
- Create new portfolios
- Display portfolio summary and individual investments
- Calculate "what-if" scenarios using the calculate endpoint
- Update investment parameters
- Remove investments and portfolios

Example React hook:
```typescript
const [portfolios, setPortfolios] = useState<PortfolioDto[]>([]);

useEffect(() => {
  const fetchPortfolios = async () => {
    const response = await fetch('http://localhost:8080/api/investments/portfolios', {
      headers: {
        'X-User-Id': userId,
      },
    });
    const data = await response.json();
    setPortfolios(data);
  };
  fetchPortfolios();
}, [userId]);
```
