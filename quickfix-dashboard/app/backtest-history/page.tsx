'use client';

import { useState, useEffect } from 'react';
import { Card } from '@/components/ui/card';
import { quantApi, BacktestResult, Strategy } from '@/lib/api/quantApi';
import Link from 'next/link';

export default function BacktestHistoryPage() {
  const [strategies, setStrategies] = useState<Strategy[]>([]);
  const [selectedStrategy, setSelectedStrategy] = useState<string>('');
  const [results, setResults] = useState<BacktestResult[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Load available strategies
  useEffect(() => {
    const loadStrategies = async () => {
      try {
        const strats = await quantApi.getStrategies();
        setStrategies(strats);
        if (strats.length > 0) {
          setSelectedStrategy(strats[0].name);
        }
      } catch (err) {
        setError(err instanceof Error ? err.message : 'Failed to load strategies');
      }
    };
    loadStrategies();
  }, []);

  // Load backtest results for selected strategy
  useEffect(() => {
    if (!selectedStrategy) return;

    const loadResults = async () => {
      setLoading(true);
      setError(null);
      try {
        const data = await quantApi.getBacktestsByStrategy(selectedStrategy);
        setResults(data);
      } catch (err) {
        setError(err instanceof Error ? err.message : 'Failed to load backtest results');
        setResults([]);
      } finally {
        setLoading(false);
      }
    };

    loadResults();
  }, [selectedStrategy]);

  return (
    <div className="space-y-6 p-6">
      {/* Header */}
      <div>
        <h1 className="text-3xl font-bold text-gray-100">Backtest History</h1>
        <p className="text-gray-400 mt-2">View past backtest results</p>
      </div>

      {/* Strategy Filter */}
      <Card className="bg-gray-900 border-gray-700 p-6">
        <label className="block text-sm font-medium text-gray-300 mb-3">
          Select Strategy
        </label>
        <select
          value={selectedStrategy}
          onChange={(e) => setSelectedStrategy(e.target.value)}
          className="w-full md:w-64 px-3 py-2 bg-gray-800 border border-gray-600 rounded-md text-gray-100 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
        >
          {strategies.map(strategy => (
            <option key={strategy.name} value={strategy.name}>
              {strategy.name}
            </option>
          ))}
        </select>
      </Card>

      {/* Error Message */}
      {error && (
        <div className="bg-red-900/20 border border-red-700 rounded-md p-4">
          <p className="text-red-400">{error}</p>
        </div>
      )}

      {/* Results */}
      {loading ? (
        <Card className="bg-gray-900 border-gray-700 p-6">
          <p className="text-gray-400">Loading backtest results...</p>
        </Card>
      ) : results.length === 0 ? (
        <Card className="bg-gray-900 border-gray-700 p-6">
          <p className="text-gray-400">No backtest results for {selectedStrategy}</p>
        </Card>
      ) : (
        <div className="space-y-4">
          {results.map((result, idx) => (
            <Card
              key={result.id || idx}
              className="bg-gray-900 border-gray-700 p-6 hover:border-blue-600 transition-colors"
            >
              <div className="grid grid-cols-1 md:grid-cols-6 gap-6 items-center">
                {/* Symbol & Dates */}
                <div>
                  <p className="text-sm text-gray-400">Symbol</p>
                  <p className="text-lg font-semibold text-gray-100">{result.symbol}</p>
                  <p className="text-xs text-gray-500 mt-1">
                    {new Date(result.startDate).toLocaleDateString()} - {new Date(result.endDate).toLocaleDateString()}
                  </p>
                </div>

                {/* Total Return */}
                <div>
                  <p className="text-sm text-gray-400">Total Return</p>
                  <p className={`text-lg font-semibold ${result.totalReturn >= 0 ? 'text-green-400' : 'text-red-400'}`}>
                    {(result.totalReturn * 100).toFixed(2)}%
                  </p>
                </div>

                {/* Sharpe Ratio */}
                <div>
                  <p className="text-sm text-gray-400">Sharpe Ratio</p>
                  <p className="text-lg font-semibold text-gray-100">
                    {result.sharpeRatio?.toFixed(3) ?? 'N/A'}
                  </p>
                </div>

                {/* Max Drawdown */}
                <div>
                  <p className="text-sm text-gray-400">Max Drawdown</p>
                  <p className="text-lg font-semibold text-red-400">
                    -{(result.maxDrawdown * 100).toFixed(2)}%
                  </p>
                </div>

                {/* Win Rate */}
                <div>
                  <p className="text-sm text-gray-400">Win Rate</p>
                  <p className={`text-lg font-semibold ${result.winRate >= 0.5 ? 'text-green-400' : 'text-orange-400'}`}>
                    {(result.winRate * 100).toFixed(2)}%
                  </p>
                </div>

                {/* View Button */}
                <div className="flex justify-end">
                  <Link
                    href={`/backtest-results/${result.id}`}
                    className="bg-blue-600 hover:bg-blue-700 text-white font-medium py-2 px-4 rounded-md transition-colors text-sm"
                  >
                    View Details
                  </Link>
                </div>
              </div>
            </Card>
          ))}
        </div>
      )}
    </div>
  );
}
