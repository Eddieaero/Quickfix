'use client';

import { Card } from '@/components/ui/card';
import { BacktestResult } from '@/lib/api/quantApi';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';

interface BacktestChartProps {
  results: BacktestResult;
}

export default function BacktestChart({ results }: BacktestChartProps) {
  // Handle undefined or missing data
  if (!results || !results.portfolioValues || results.portfolioValues.length === 0) {
    return (
      <Card className="bg-gray-900 border-gray-700 p-6">
        <h2 className="text-xl font-semibold text-gray-100 mb-4">Equity Curve</h2>
        <div className="flex items-center justify-center h-300 text-gray-400">
          <p>No backtest results available. Run a backtest to see the equity curve.</p>
        </div>
      </Card>
    );
  }

  // Transform portfolio values into chart data
  const chartData = results.portfolioValues.map((value, index) => ({
    date: index,
    value: Math.round(value),
    percentage: (((value - (results.initialCapital || 100000)) / (results.initialCapital || 100000)) * 100).toFixed(2),
  }));

  // Calculate drawdown data
  let peak = results.initialCapital || 100000;
  const drawdownData = results.portfolioValues.map((value, index) => {
    if (value > peak) peak = value;
    const dd = ((peak - value) / peak) * 100;
    return {
      date: index,
      value: Math.round(value),
      drawdown: Math.max(0, dd),
      peak: Math.round(peak),
    };
  });

  return (
    <>
      {/* Equity Curve */}
      <Card className="bg-gray-900 border-gray-700 p-6">
        <h2 className="text-xl font-semibold text-gray-100 mb-4">Equity Curve</h2>
        
        <ResponsiveContainer width="100%" height={300}>
          <LineChart data={chartData} margin={{ top: 5, right: 30, left: 0, bottom: 5 }}>
            <CartesianGrid strokeDasharray="3 3" stroke="#374151" />
            <XAxis 
              dataKey="date" 
              stroke="#9CA3AF"
              label={{ value: 'Trading Days', position: 'insideBottomRight', offset: -5 }}
            />
            <YAxis 
              stroke="#9CA3AF"
              label={{ value: 'Portfolio Value ($)', angle: -90, position: 'insideLeft' }}
            />
            <Tooltip 
              contentStyle={{ backgroundColor: '#1F2937', border: '1px solid #4B5563' }}
              labelStyle={{ color: '#F3F4F6' }}
              formatter={(value) => `$${value?.toLocaleString()}`}
              labelFormatter={() => `Portfolio Value`}
            />
            <Legend />
            <Line 
              type="monotone" 
              dataKey="value" 
              stroke="#3B82F6" 
              dot={false} 
              name="Portfolio Value"
              strokeWidth={2}
            />
          </LineChart>
        </ResponsiveContainer>
      </Card>

      {/* Drawdown Chart */}
      <Card className="bg-gray-900 border-gray-700 p-6">
        <h2 className="text-xl font-semibold text-gray-100 mb-4">Drawdown Analysis</h2>
        
        <ResponsiveContainer width="100%" height={250}>
          <LineChart data={drawdownData} margin={{ top: 5, right: 30, left: 0, bottom: 5 }}>
            <CartesianGrid strokeDasharray="3 3" stroke="#374151" />
            <XAxis 
              dataKey="date" 
              stroke="#9CA3AF"
              label={{ value: 'Trading Days', position: 'insideBottomRight', offset: -5 }}
            />
            <YAxis 
              stroke="#9CA3AF"
              label={{ value: 'Drawdown (%)', angle: -90, position: 'insideLeft' }}
            />
            <Tooltip 
              contentStyle={{ backgroundColor: '#1F2937', border: '1px solid #4B5563' }}
              labelStyle={{ color: '#F3F4F6' }}
              formatter={(value) => `${(value as number).toFixed(2)}%`}
            />
            <Legend />
            <Line 
              type="monotone" 
              dataKey="drawdown" 
              stroke="#EF4444" 
              dot={false}
              name="Drawdown"
              strokeWidth={2}
              fill="#EF4444"
              isAnimationActive={false}
            />
          </LineChart>
        </ResponsiveContainer>

        <div className="mt-4 p-4 bg-gray-800/50 rounded-lg">
          <p className="text-sm text-gray-400">
            <span className="font-medium text-gray-300">Max Drawdown: </span>
            {(results.maxDrawdown * 100).toFixed(2)}% from peak
          </p>
        </div>
      </Card>
    </>
  );
}
