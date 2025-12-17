import React, { useState, useEffect } from 'react';
import { RefreshCw, TrendingUp, AlertCircle } from 'lucide-react';
import axios from 'axios';

interface PriceData {
  symbol: string;
  price: number;
  currency: string;
  exchange: string;
  lastUpdated: number;
  valid: boolean;
  errorMessage?: string;
}

const MarketPriceWidget: React.FC<{ symbol: string }> = ({ symbol }) => {
  const [price, setPrice] = useState<PriceData | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchPrice = async () => {
    setLoading(true);
    try {
      const response = await axios.get(`http://localhost:8080/api/market/price/${symbol}`);
      setPrice(response.data);
      setError(null);
    } catch (err) {
      setError('Failed to fetch price');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPrice();
    // Refresh every 5 minutes
    const interval = setInterval(fetchPrice, 5 * 60 * 1000);
    return () => clearInterval(interval);
  }, [symbol]);

  if (loading) {
    return (
      <div className="bg-white dark:bg-gray-800 rounded-lg p-4 shadow animate-pulse">
        <div className="h-4 bg-gray-200 dark:bg-gray-700 rounded w-24 mb-2"></div>
        <div className="h-8 bg-gray-200 dark:bg-gray-700 rounded w-32"></div>
      </div>
    );
  }

  if (error || !price?.valid) {
    return (
      <div className="bg-red-50 dark:bg-red-900 border border-red-200 dark:border-red-700 rounded-lg p-4">
        <div className="flex items-center gap-2 text-red-700 dark:text-red-200">
          <AlertCircle size={16} />
          <span className="text-sm">{error || price?.errorMessage || 'Failed to load price'}</span>
        </div>
      </div>
    );
  }

  const formattedPrice = new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: price.currency === 'TZS' ? 'TZS' : 'USD',
  }).format(price.price);

  const lastUpdatedTime = new Date(price.lastUpdated).toLocaleTimeString();

  return (
    <div className="bg-gradient-to-br from-blue-50 to-indigo-50 dark:from-gray-800 dark:to-gray-700 rounded-lg p-4 shadow-md">
      <div className="flex justify-between items-start mb-3">
        <div>
          <p className="text-xs font-semibold text-gray-600 dark:text-gray-400 uppercase">
            {symbol}
          </p>
          <p className="text-2xl font-bold text-gray-900 dark:text-white mt-1">
            {formattedPrice}
          </p>
        </div>
        <TrendingUp className="text-green-500" size={20} />
      </div>
      <div className="flex justify-between items-center pt-2 border-t border-gray-200 dark:border-gray-600">
        <p className="text-xs text-gray-500 dark:text-gray-400">
          {price.exchange}
        </p>
        <button
          onClick={fetchPrice}
          className="text-xs text-indigo-600 dark:text-indigo-400 hover:text-indigo-700 flex items-center gap-1"
        >
          <RefreshCw size={12} />
          Updated {lastUpdatedTime}
        </button>
      </div>
    </div>
  );
};

export default MarketPriceWidget;
