'use client';

import { useState, useEffect } from 'react';
import { Card } from '@/components/ui/card';
import { quantApi, Strategy } from '@/lib/api/quantApi';

export default function StrategiesPage() {
  const [strategies, setStrategies] = useState<Strategy[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const loadStrategies = async () => {
      try {
        const data = await quantApi.getStrategies();
        setStrategies(data);
      } catch (err) {
        setError(err instanceof Error ? err.message : 'Failed to load strategies');
      } finally {
        setLoading(false);
      }
    };

    loadStrategies();
  }, []);

  if (loading) {
    return (
      <div className="space-y-6 p-6">
        <h1 className="text-3xl font-bold text-gray-100">Trading Strategies</h1>
        <p className="text-gray-400">Loading strategies...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="space-y-6 p-6">
        <h1 className="text-3xl font-bold text-gray-100">Trading Strategies</h1>
        <div className="bg-red-900/20 border border-red-700 rounded-md p-4">
          <p className="text-red-400">{error}</p>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6 p-6">
      {/* Header */}
      <div>
        <h1 className="text-3xl font-bold text-gray-100">Trading Strategies</h1>
        <p className="text-gray-400 mt-2">Available strategies for backtesting</p>
      </div>

      {/* Strategies Grid */}
      {strategies.length === 0 ? (
        <Card className="bg-gray-900 border-gray-700 p-6">
          <p className="text-gray-400">No strategies available</p>
        </Card>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {strategies.map((strategy) => (
            <Card 
              key={strategy.name}
              className="bg-gray-900 border-gray-700 p-6 hover:border-blue-600 transition-colors cursor-pointer"
            >
              {/* Name */}
              <h3 className="text-lg font-semibold text-gray-100 mb-2">
                {strategy.name}
              </h3>

              {/* Description */}
              <p className="text-sm text-gray-400 mb-4">
                {strategy.description}
              </p>

              {/* Details */}
              <div className="space-y-3 mb-4 pb-4 border-b border-gray-700">
                {strategy.minimumBars && (
                  <div className="flex justify-between text-sm">
                    <span className="text-gray-400">Minimum Bars</span>
                    <span className="text-gray-200 font-medium">{strategy.minimumBars}</span>
                  </div>
                )}
                
                {strategy.requiredIndicators && strategy.requiredIndicators.length > 0 && (
                  <div>
                    <p className="text-sm text-gray-400 mb-2">Required Indicators</p>
                    <div className="flex flex-wrap gap-2">
                      {strategy.requiredIndicators.map((indicator) => (
                        <span
                          key={indicator}
                          className="inline-block bg-blue-900/30 border border-blue-700 rounded-full px-3 py-1 text-xs text-blue-300"
                        >
                          {indicator}
                        </span>
                      ))}
                    </div>
                  </div>
                )}
              </div>

              {/* Action */}
              <a
                href="/backtest"
                className="inline-block w-full text-center bg-blue-600 hover:bg-blue-700 text-white font-medium py-2 px-4 rounded-md transition-colors"
              >
                Test Strategy
              </a>
            </Card>
          ))}
        </div>
      )}
    </div>
  );
}
