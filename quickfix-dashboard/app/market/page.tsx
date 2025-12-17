'use client';

import React from 'react';
import MarketPriceWidget from '@/components/MarketPriceWidget';
import DseMarketOverview from '@/components/DseMarketOverview';

export default function MarketPage() {
  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-50 to-gray-100 dark:from-gray-900 dark:to-gray-800 p-6">
      <div className="max-w-7xl mx-auto">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-4xl font-bold text-gray-900 dark:text-white mb-2">
            Market Data
          </h1>
          <p className="text-lg text-gray-600 dark:text-gray-400">
            Real-time market prices and portfolio valuations
          </p>
        </div>

        {/* Quick Price Widgets */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
          <MarketPriceWidget symbol="CRDB.TZ" />
          <MarketPriceWidget symbol="NMB.TZ" />
          <MarketPriceWidget symbol="TBL.TZ" />
          <MarketPriceWidget symbol="AAPL" />
        </div>

        {/* DSE Market Overview */}
        <div className="mb-8">
          <DseMarketOverview />
        </div>

        {/* Information Cards */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          {/* EODHD Integration Info */}
          <div className="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6 border-l-4 border-blue-500">
            <h3 className="text-lg font-bold text-gray-900 dark:text-white mb-3">
              📊 Market Data Source
            </h3>
            <p className="text-gray-700 dark:text-gray-300 mb-3">
              This application integrates with EODHD API to provide:
            </p>
            <ul className="space-y-2 text-sm text-gray-600 dark:text-gray-400">
              <li>✓ Real-time and historical stock prices</li>
              <li>✓ DSE (Tanzania) stock exchange data</li>
              <li>✓ Dividend yield information</li>
              <li>✓ Company fundamentals (P/E ratio, market cap)</li>
              <li>✓ Technical indicators and price analysis</li>
            </ul>
          </div>

          {/* API Configuration */}
          <div className="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6 border-l-4 border-green-500">
            <h3 className="text-lg font-bold text-gray-900 dark:text-white mb-3">
              ⚙️ Configuration
            </h3>
            <p className="text-gray-700 dark:text-gray-300 mb-3">
              To use live market data:
            </p>
            <ol className="space-y-2 text-sm text-gray-600 dark:text-gray-400">
              <li>1. Sign up at <a href="https://eodhd.com" className="text-blue-500 hover:underline" target="_blank">EODHD.com</a></li>
              <li>2. Get your API key (free or paid plans available)</li>
              <li>3. Update <code className="bg-gray-100 dark:bg-gray-700 px-2 py-1 rounded">application.yml</code></li>
              <li>4. Set <code className="bg-gray-100 dark:bg-gray-700 px-2 py-1 rounded">eodhd.api.key</code> with your key</li>
            </ol>
          </div>
        </div>

        {/* Demo Mode Notice */}
        <div className="mt-8 bg-yellow-50 dark:bg-yellow-900 border border-yellow-200 dark:border-yellow-700 rounded-lg p-4">
          <p className="text-yellow-800 dark:text-yellow-200 text-sm">
            <strong>ℹ️ Demo Mode:</strong> Currently running in demo mode with sample Tanzania stock prices. 
            To see real market data, add your EODHD API key to the configuration.
          </p>
        </div>
      </div>
    </div>
  );
}
