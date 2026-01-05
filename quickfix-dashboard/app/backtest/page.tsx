'use client';

import { useState, useEffect } from 'react';
import { Card } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { quantApi, BacktestRequest, BacktestResult, Strategy } from '@/lib/api/quantApi';
import BacktestMetrics from '@/components/quant/BacktestMetrics';
import BacktestChart from '@/components/quant/BacktestChart';

export default function BacktestPage() {
  const [strategies, setStrategies] = useState<Strategy[]>([]);
  const [loading, setLoading] = useState(false);
  const [results, setResults] = useState<BacktestResult | null>(null);
  const [error, setError] = useState<string | null>(null);

  const [formData, setFormData] = useState<BacktestRequest>({
    strategyName: 'SMA Crossover',
    symbol: 'AAPL.US',
    startDate: '2023-01-01',
    endDate: '2024-01-01',
    initialCapital: 100000,
  });

  // Load available strategies
  useEffect(() => {
    const loadStrategies = async () => {
      try {
        const strats = await quantApi.getStrategies();
        setStrategies(strats);
      } catch (err) {
        setError(`Failed to load strategies: ${err instanceof Error ? err.message : 'Unknown error'}`);
      }
    };
    loadStrategies();
  }, []);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: name === 'initialCapital' ? parseFloat(value) : value,
    }));
  };

  const handleRunBacktest = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    setResults(null);

    try {
      const result = await quantApi.runBacktest(formData);
      setResults(result);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to run backtest');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="space-y-6 p-6">
      {/* Header */}
      <div>
        <h1 className="text-3xl font-bold text-gray-100">Backtest Strategy</h1>
        <p className="text-gray-400 mt-2">Test trading strategies on historical data</p>
      </div>

      {/* Form Card */}
      <Card className="bg-gray-900 border-gray-700 p-6">
        <h2 className="text-xl font-semibold text-gray-100 mb-4">Backtest Parameters</h2>
        
        <form onSubmit={handleRunBacktest} className="space-y-4">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {/* Strategy Selection */}
            <div>
              <label className="block text-sm font-medium text-gray-300 mb-2">
                Strategy
              </label>
              <select
                name="strategyName"
                value={formData.strategyName}
                onChange={handleInputChange}
                className="w-full px-3 py-2 bg-gray-800 border border-gray-600 rounded-md text-gray-100 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                {strategies.map(strategy => (
                  <option key={strategy.name} value={strategy.name}>
                    {strategy.name}
                  </option>
                ))}
              </select>
              {strategies.find(s => s.name === formData.strategyName) && (
                <p className="text-xs text-gray-400 mt-1">
                  {strategies.find(s => s.name === formData.strategyName)?.description}
                </p>
              )}
            </div>

            {/* Symbol */}
            <div>
              <label className="block text-sm font-medium text-gray-300 mb-2">
                Symbol
              </label>
              <input
                type="text"
                name="symbol"
                value={formData.symbol}
                onChange={handleInputChange}
                placeholder="e.g., AAPL.US"
                className="w-full px-3 py-2 bg-gray-800 border border-gray-600 rounded-md text-gray-100 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>

            {/* Start Date */}
            <div>
              <label className="block text-sm font-medium text-gray-300 mb-2">
                Start Date
              </label>
              <input
                type="date"
                name="startDate"
                value={formData.startDate}
                onChange={handleInputChange}
                className="w-full px-3 py-2 bg-gray-800 border border-gray-600 rounded-md text-gray-100 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>

            {/* End Date */}
            <div>
              <label className="block text-sm font-medium text-gray-300 mb-2">
                End Date
              </label>
              <input
                type="date"
                name="endDate"
                value={formData.endDate}
                onChange={handleInputChange}
                className="w-full px-3 py-2 bg-gray-800 border border-gray-600 rounded-md text-gray-100 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>

            {/* Initial Capital */}
            <div className="md:col-span-2">
              <label className="block text-sm font-medium text-gray-300 mb-2">
                Initial Capital ($)
              </label>
              <input
                type="number"
                name="initialCapital"
                value={formData.initialCapital}
                onChange={handleInputChange}
                min="1000"
                step="1000"
                className="w-full px-3 py-2 bg-gray-800 border border-gray-600 rounded-md text-gray-100 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
          </div>

          {/* Error Message */}
          {error && (
            <div className="bg-red-900/20 border border-red-700 rounded-md p-3">
              <p className="text-sm text-red-400">{error}</p>
            </div>
          )}

          {/* Submit Button */}
          <div className="flex justify-end">
            <Button
              type="submit"
              disabled={loading}
              className="bg-blue-600 hover:bg-blue-700 text-white px-6 py-2"
            >
              {loading ? 'Running Backtest...' : 'Run Backtest'}
            </Button>
          </div>
        </form>
      </Card>

      {/* Results */}
      {results && (
        <>
          {/* Metrics Card */}
          <BacktestMetrics results={results} />

          {/* Chart Card */}
          <BacktestChart results={results} />

          {/* Summary Stats */}
          <Card className="bg-gray-900 border-gray-700 p-6">
            <h2 className="text-xl font-semibold text-gray-100 mb-4">Summary</h2>
            <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
              <div>
                <p className="text-sm text-gray-400">Total Trades</p>
                <p className="text-2xl font-bold text-gray-100">{results.totalTrades}</p>
              </div>
              <div>
                <p className="text-sm text-gray-400">Winning Trades</p>
                <p className="text-2xl font-bold text-green-400">{results.winningTrades}</p>
              </div>
              <div>
                <p className="text-sm text-gray-400">Losing Trades</p>
                <p className="text-2xl font-bold text-red-400">{results.losingTrades}</p>
              </div>
              <div>
                <p className="text-sm text-gray-400">Profit Factor</p>
                <p className="text-2xl font-bold text-gray-100">{results.profitFactor?.toFixed(2) ?? 'N/A'}</p>
              </div>
            </div>
          </Card>
        </>
      )}
    </div>
  );
}
