'use client';

import { useState, useEffect } from 'react';
import { useParams } from 'next/navigation';
import { Card } from '@/components/ui/card';
import { quantApi, BacktestResult } from '@/lib/api/quantApi';
import BacktestMetrics from '@/components/quant/BacktestMetrics';
import BacktestChart from '@/components/quant/BacktestChart';

export default function BacktestResultsPage() {
  const params = useParams();
  const resultId = params.id as string;
  
  const [result, setResult] = useState<BacktestResult | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const loadResult = async () => {
      try {
        const data = await quantApi.getBacktestResults(resultId);
        setResult(data);
      } catch (err) {
        setError(err instanceof Error ? err.message : 'Failed to load backtest result');
      } finally {
        setLoading(false);
      }
    };

    loadResult();
  }, [resultId]);

  if (loading) {
    return (
      <div className="space-y-6 p-6">
        <h1 className="text-3xl font-bold text-gray-100">Backtest Results</h1>
        <p className="text-gray-400">Loading...</p>
      </div>
    );
  }

  if (error || !result) {
    return (
      <div className="space-y-6 p-6">
        <h1 className="text-3xl font-bold text-gray-100">Backtest Results</h1>
        <div className="bg-red-900/20 border border-red-700 rounded-md p-4">
          <p className="text-red-400">{error || 'Backtest result not found'}</p>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6 p-6">
      {/* Header */}
      <div>
        <h1 className="text-3xl font-bold text-gray-100">Backtest Results</h1>
        <p className="text-gray-400 mt-2">
          {result.strategyName} on {result.symbol}
        </p>
      </div>

      {/* Metrics */}
      <BacktestMetrics results={result} />

      {/* Charts */}
      <BacktestChart results={result} />

      {/* Trades Table */}
      {result.trades && result.trades.length > 0 && (
        <Card className="bg-gray-900 border-gray-700 p-6">
          <h2 className="text-xl font-semibold text-gray-100 mb-4">Trade Log</h2>
          
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead className="border-b border-gray-700">
                <tr>
                  <th className="text-left px-4 py-3 text-gray-400 font-medium">Entry Date</th>
                  <th className="text-left px-4 py-3 text-gray-400 font-medium">Entry Price</th>
                  <th className="text-left px-4 py-3 text-gray-400 font-medium">Exit Date</th>
                  <th className="text-left px-4 py-3 text-gray-400 font-medium">Exit Price</th>
                  <th className="text-left px-4 py-3 text-gray-400 font-medium">Return</th>
                  <th className="text-left px-4 py-3 text-gray-400 font-medium">Status</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-700">
                {result.trades.map((trade, idx) => {
                  const returnPct = ((trade.exitPrice - trade.entryPrice) / trade.entryPrice) * 100;
                  const isWin = returnPct >= 0;

                  return (
                    <tr key={idx} className="hover:bg-gray-800/50 transition-colors">
                      <td className="px-4 py-3 text-gray-300">
                        {new Date(trade.entryDate).toLocaleDateString()}
                      </td>
                      <td className="px-4 py-3 text-gray-300">
                        ${trade.entryPrice.toFixed(2)}
                      </td>
                      <td className="px-4 py-3 text-gray-300">
                        {new Date(trade.exitDate).toLocaleDateString()}
                      </td>
                      <td className="px-4 py-3 text-gray-300">
                        ${trade.exitPrice.toFixed(2)}
                      </td>
                      <td className={`px-4 py-3 font-medium ${isWin ? 'text-green-400' : 'text-red-400'}`}>
                        {isWin ? '+' : ''}{returnPct.toFixed(2)}%
                      </td>
                      <td className="px-4 py-3">
                        <span className={`inline-block px-2 py-1 text-xs font-medium rounded-full ${
                          isWin 
                            ? 'bg-green-900/30 text-green-400 border border-green-700' 
                            : 'bg-red-900/30 text-red-400 border border-red-700'
                        }`}>
                          {isWin ? 'Win' : 'Loss'}
                        </span>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>

          <div className="mt-4 text-xs text-gray-400">
            Showing {result.trades.length} trades
          </div>
        </Card>
      )}
    </div>
  );
}
