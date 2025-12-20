'use client';

import React, { useEffect, useState } from 'react';

interface Stock {
  symbol: string;
  exchange: string | null;
  price: number;
  high: number | null;
  low: number | null;
  volume: number | null;
  currency: string | null;
  valid: boolean;
  errorMessage: string | null;
}

export default function UsMarketOverview() {
  const [stocks, setStocks] = useState<Stock[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchStocks = async () => {
      try {
        setLoading(true);
        const response = await fetch('/api/market/us/stocks');
        if (!response.ok) {
          throw new Error(`API error: ${response.status}`);
        }
        const data = await response.json();
        
        // Convert object to array
        const stocksArray = Object.entries(data).map(([symbol, stockData]: [string, any]) => ({
          symbol,
          ...stockData,
        }));
        
        setStocks(stocksArray);
        setError(null);
      } catch (err) {
        setError(err instanceof Error ? err.message : 'Failed to fetch US stocks');
        setStocks([]);
      } finally {
        setLoading(false);
      }
    };

    fetchStocks();
    
    // Refresh every 60 seconds
    const interval = setInterval(fetchStocks, 60000);
    return () => clearInterval(interval);
  }, []);

  if (loading) {
    return (
      <div className="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6">
        <h2 className="text-2xl font-bold text-gray-900 dark:text-white mb-4">
          📈 US Market Overview
        </h2>
        <p className="text-gray-500 dark:text-gray-400">Loading US stocks...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6">
        <h2 className="text-2xl font-bold text-gray-900 dark:text-white mb-4">
          📈 US Market Overview
        </h2>
        <p className="text-red-600 dark:text-red-400">Error: {error}</p>
      </div>
    );
  }

  return (
    <div className="bg-white dark:bg-gray-800 rounded-lg shadow-md overflow-hidden">
      <div className="p-6 border-b border-gray-200 dark:border-gray-700">
        <h2 className="text-2xl font-bold text-gray-900 dark:text-white">
          📈 US Market Overview
        </h2>
      </div>
      
      <div className="overflow-x-auto">
        <table className="w-full">
          <thead className="bg-gray-50 dark:bg-gray-700">
            <tr>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-700 dark:text-gray-300 uppercase tracking-wider">
                Symbol
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-700 dark:text-gray-300 uppercase tracking-wider">
                Exchange
              </th>
              <th className="px-6 py-3 text-right text-xs font-medium text-gray-700 dark:text-gray-300 uppercase tracking-wider">
                Price
              </th>
              <th className="px-6 py-3 text-right text-xs font-medium text-gray-700 dark:text-gray-300 uppercase tracking-wider">
                High
              </th>
              <th className="px-6 py-3 text-right text-xs font-medium text-gray-700 dark:text-gray-300 uppercase tracking-wider">
                Low
              </th>
              <th className="px-6 py-3 text-right text-xs font-medium text-gray-700 dark:text-gray-300 uppercase tracking-wider">
                Volume
              </th>
              <th className="px-6 py-3 text-center text-xs font-medium text-gray-700 dark:text-gray-300 uppercase tracking-wider">
                Status
              </th>
            </tr>
          </thead>
          <tbody className="bg-white dark:bg-gray-800 divide-y divide-gray-200 dark:divide-gray-700">
            {stocks.map((stock) => (
              <tr key={stock.symbol} className="hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors">
                <td className="px-6 py-4 whitespace-nowrap font-bold text-gray-900 dark:text-white">
                  {stock.symbol}
                </td>
                <td className="px-6 py-4 whitespace-nowrap">
                  <span className="inline-block px-3 py-1 bg-blue-100 dark:bg-blue-900 text-blue-700 dark:text-blue-200 text-xs font-medium rounded-full">
                    {stock.exchange || 'NASDAQ'}
                  </span>
                </td>
                <td className="px-6 py-4 text-right">
                  <span className="font-bold text-gray-900 dark:text-white">
                    {new Intl.NumberFormat('en-US', {
                      style: 'currency',
                      currency: stock.currency || 'USD',
                      minimumFractionDigits: 2,
                    }).format(stock.price || 0)}
                  </span>
                </td>
                <td className="px-6 py-4 text-right text-gray-600 dark:text-gray-400">
                  {stock.high ? (
                    new Intl.NumberFormat('en-US', {
                      style: 'currency',
                      currency: stock.currency || 'USD',
                      minimumFractionDigits: 2,
                    }).format(stock.high)
                  ) : (
                    '-'
                  )}
                </td>
                <td className="px-6 py-4 text-right text-gray-600 dark:text-gray-400">
                  {stock.low ? (
                    new Intl.NumberFormat('en-US', {
                      style: 'currency',
                      currency: stock.currency || 'USD',
                      minimumFractionDigits: 2,
                    }).format(stock.low)
                  ) : (
                    '-'
                  )}
                </td>
                <td className="px-6 py-4 text-right text-gray-600 dark:text-gray-400">
                  {stock.volume ? (
                    (stock.volume / 1000000).toFixed(2) + 'M'
                  ) : (
                    '-'
                  )}
                </td>
                <td className="px-6 py-4 text-center">
                  {stock.valid ? (
                    <span className="inline-flex px-3 py-1 text-xs font-semibold leading-5 rounded-full bg-green-100 dark:bg-green-900 text-green-800 dark:text-green-200">
                      ✓ Live
                    </span>
                  ) : (
                    <span className="inline-flex px-3 py-1 text-xs font-semibold leading-5 rounded-full bg-red-100 dark:bg-red-900 text-red-800 dark:text-red-200">
                      ✗ Error
                    </span>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
