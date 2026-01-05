'use client';

import { Card } from '@/components/ui/card';
import { BacktestResult } from '@/lib/api/quantApi';

interface BacktestMetricsProps {
  results: BacktestResult;
}

export default function BacktestMetrics({ results }: BacktestMetricsProps) {
  // Handle undefined results
  if (!results) {
    return (
      <Card className="bg-gray-900 border-gray-700 p-6">
        <h2 className="text-xl font-semibold text-gray-100 mb-4">Backtest Metrics</h2>
        <div className="flex items-center justify-center h-32 text-gray-400">
          <p>No backtest results available. Run a backtest to see performance metrics.</p>
        </div>
      </Card>
    );
  }

  const metrics = [
    {
      label: 'Total Return',
      value: `${((results.totalReturn || 0) * 100).toFixed(2)}%`,
      color: (results.totalReturn || 0) >= 0 ? 'text-green-400' : 'text-red-400',
    },
    {
      label: 'Sharpe Ratio',
      value: results.sharpeRatio?.toFixed(3) ?? 'N/A',
      color: 'text-gray-200',
    },
    {
      label: 'Sortino Ratio',
      value: results.sortinoRatio?.toFixed(3) ?? 'N/A',
      color: 'text-gray-200',
    },
    {
      label: 'Max Drawdown',
      value: `${(results.maxDrawdown * 100).toFixed(2)}%`,
      color: 'text-red-400',
    },
    {
      label: 'Win Rate',
      value: `${(results.winRate * 100).toFixed(2)}%`,
      color: results.winRate >= 0.5 ? 'text-green-400' : 'text-orange-400',
    },
    {
      label: 'Profit Factor',
      value: results.profitFactor?.toFixed(2) ?? 'N/A',
      color: results.profitFactor && results.profitFactor > 1.5 ? 'text-green-400' : 'text-gray-200',
    },
    {
      label: 'Avg Win',
      value: `${(results.avgWin * 100).toFixed(2)}%`,
      color: 'text-green-400',
    },
    {
      label: 'Avg Loss',
      value: `${(results.avgLoss * 100).toFixed(2)}%`,
      color: 'text-red-400',
    },
    {
      label: 'Consecutive Wins',
      value: results.consecutiveWins?.toString() ?? 'N/A',
      color: 'text-gray-200',
    },
    {
      label: 'Consecutive Losses',
      value: results.consecutiveLosses?.toString() ?? 'N/A',
      color: 'text-gray-200',
    },
    {
      label: 'Recovery Factor',
      value: results.recoveryFactor?.toFixed(2) ?? 'N/A',
      color: 'text-gray-200',
    },
    {
      label: 'Final Portfolio Value',
      value: `$${Math.round(results.finalPortfolioValue).toLocaleString()}`,
      color: results.finalPortfolioValue >= (results.initialCapital || 100000) ? 'text-green-400' : 'text-red-400',
    },
  ];

  return (
    <Card className="bg-gray-900 border-gray-700 p-6">
      <h2 className="text-xl font-semibold text-gray-100 mb-6">Performance Metrics</h2>
      
      <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
        {metrics.map((metric, idx) => (
          <div key={idx} className="bg-gray-800/50 border border-gray-700 rounded-lg p-4">
            <p className="text-xs font-medium text-gray-400 uppercase tracking-wider mb-2">
              {metric.label}
            </p>
            <p className={`text-xl font-bold ${metric.color}`}>
              {metric.value}
            </p>
          </div>
        ))}
      </div>

      {/* Additional Info */}
      <div className="mt-6 pt-6 border-t border-gray-700">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-sm">
          <div>
            <p className="text-gray-400">Strategy</p>
            <p className="text-gray-100 font-medium">{results.strategyName}</p>
          </div>
          <div>
            <p className="text-gray-400">Symbol</p>
            <p className="text-gray-100 font-medium">{results.symbol}</p>
          </div>
          <div>
            <p className="text-gray-400">Test Period</p>
            <p className="text-gray-100 font-medium">
              {new Date(results.startDate).toLocaleDateString()} - {new Date(results.endDate).toLocaleDateString()}
            </p>
          </div>
          <div>
            <p className="text-gray-400">Initial Capital</p>
            <p className="text-gray-100 font-medium">${results.initialCapital?.toLocaleString()}</p>
          </div>
        </div>
      </div>
    </Card>
  );
}
