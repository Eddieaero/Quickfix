/**
 * Quantitative Analysis API Client
 * Connects to backend: http://localhost:8080/api/quant
 */

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api/quant';

export interface BacktestRequest {
  strategyName: string;
  symbol: string;
  startDate: string; // YYYY-MM-DD
  endDate: string;   // YYYY-MM-DD
  initialCapital: number;
}

export interface BacktestResult {
  id: string;
  strategyName: string;
  symbol: string;
  startDate: string;
  endDate: string;
  initialCapital: number;
  finalValue: number;
  totalReturn: number;
  annualReturn: number;
  sharpeRatio: number;
  sortinoRatio: number;
  maxDrawdown: number;
  totalTrades: number;
  winningTrades: number;
  losingTrades: number;
  winRate: number;
  averageWin: number;
  averageLoss: number;
  profitFactor: number;
  createdAt: string;
}

export interface Strategy {
  name: string;
  description: string;
  minimumBars: number;
  requiredIndicators: string[];
}

export interface StrategiesResponse {
  count: number;
  strategies: Strategy[];
}

export interface BacktestResultsResponse {
  count: number;
  results: BacktestResult[];
}

export interface HealthResponse {
  status: string;
  component: string;
  features: string[];
}

class QuantApi {
  private baseUrl: string;

  constructor(baseUrl: string = API_BASE_URL) {
    this.baseUrl = baseUrl;
  }

  /**
   * Health check
   */
  async health(): Promise<HealthResponse> {
    const response = await fetch(`${this.baseUrl}/health`);
    if (!response.ok) throw new Error(`Health check failed: ${response.statusText}`);
    return response.json();
  }

  /**
   * Get available strategies
   */
  async getStrategies(): Promise<Strategy[]> {
    const response = await fetch(`${this.baseUrl}/strategies`);
    if (!response.ok) throw new Error(`Failed to fetch strategies: ${response.statusText}`);
    const data: StrategiesResponse = await response.json();
    return data.strategies;
  }

  /**
   * Run a new backtest
   */
  async runBacktest(request: BacktestRequest): Promise<BacktestResult> {
    const response = await fetch(`${this.baseUrl}/backtest`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(request),
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.error || `Backtest failed: ${response.statusText}`);
    }

    return response.json();
  }

  /**
   * Get backtest results by ID
   */
  async getBacktestResults(id: string): Promise<BacktestResult> {
    const response = await fetch(`${this.baseUrl}/backtest/${id}`);
    if (!response.ok) throw new Error(`Backtest not found: ${response.statusText}`);
    return response.json();
  }

  /**
   * Get all backtests for a strategy
   */
  async getBacktestsByStrategy(strategyName: string): Promise<BacktestResult[]> {
    const response = await fetch(`${this.baseUrl}/backtest/strategy/${encodeURIComponent(strategyName)}`);
    if (!response.ok) throw new Error(`Failed to fetch backtests: ${response.statusText}`);
    const data: BacktestResultsResponse = await response.json();
    return data.results;
  }
}

export const quantApi = new QuantApi();
