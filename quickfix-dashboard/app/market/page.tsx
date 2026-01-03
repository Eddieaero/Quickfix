'use client';

import React, { useState } from 'react';
import MarketPriceWidget from '@/components/MarketPriceWidget';
import DseMarketOverview from '@/components/DseMarketOverview';
import UsMarketOverview from '@/components/UsMarketOverview';

export default function MarketPage() {
  const [showDse, setShowDse] = useState(false);
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

        {/* Quick Price Widgets - US Stocks */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
          <MarketPriceWidget symbol="AAPL" />
          <MarketPriceWidget symbol="MSFT" />
          <MarketPriceWidget symbol="GOOGL" />
          <MarketPriceWidget symbol="AMZN" />
        </div>

        {/* US Market Overview - Primary */}
        <div className="mb-8">
          <UsMarketOverview />
        </div>

        {/* DSE Market Overview - Collapsible */}
        <div className="mb-8">
          <div 
            onClick={() => setShowDse(!showDse)}
            className="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6 cursor-pointer hover:shadow-lg transition-shadow border-l-4 border-gray-400"
          >
            <div className="flex items-center justify-between">
              <h2 className="text-2xl font-bold text-gray-900 dark:text-white">
                📊 DSE Market Overview (Tanzania)
              </h2>
              <span className="text-2xl text-gray-600 dark:text-gray-400">
                {showDse ? '▼' : '▶'}
              </span>
            </div>
            <p className="text-sm text-gray-500 dark:text-gray-400 mt-2">
              ⚠️ Note: EODHD API does not currently support Tanzania Stock Exchange. Symbols will show "not supported" status.
            </p>
          </div>
          
          {showDse && (
            <div className="mt-4">
              <DseMarketOverview />
            </div>
          )}
        </div>

        {/* Information Cards */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          {/* EODHD Integration Info */}
          <div className="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6 border-l-4 border-gray-500">
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
          <div className="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6 border-l-4 border-gray-400">
            <h3 className="text-lg font-bold text-gray-900 dark:text-white mb-3">
              ⚙️ Configuration
            </h3>
            <p className="text-gray-700 dark:text-gray-300 mb-3">
              To use live market data:
            </p>
            <ol className="space-y-2 text-sm text-gray-600 dark:text-gray-400">
              <li>1. Sign up at <a href="https://eodhd.com" className="text-gray-400 hover:underline" target="_blank">EODHD.com</a></li>
              <li>2. Get your API key (free or paid plans available)</li>
              <li>3. Update <code className="bg-gray-100 dark:bg-gray-700 px-2 py-1 rounded">application.yml</code></li>
              <li>4. Set <code className="bg-gray-100 dark:bg-gray-700 px-2 py-1 rounded">eodhd.api.key</code> with your key</li>
            </ol>
          </div>
        </div>

        {/* Demo Mode Notice */}
        <div className="mt-8 bg-gray-100 dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg p-4">
          <p className="text-gray-700 dark:text-gray-300 text-sm">
            <strong>ℹ️ Demo Mode:</strong> Currently running in demo mode with sample Tanzania stock prices. 
            To see real market data, add your EODHD API key to the configuration.
          </p>
        </div>
      </div>
    </div>
  );
}
