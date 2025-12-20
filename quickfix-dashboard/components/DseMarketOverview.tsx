import React, { useState, useEffect } from 'react';
import { RefreshCw, AlertCircle } from 'lucide-react';
import axios from 'axios';

interface DseStock {
  symbol: string;
  price: number;
  currency: string;
  exchange: string;
}

const DseMarketOverview: React.FC = () => {
  const [stocks, setStocks] = useState<DseStock[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchDseStocks = async () => {
    setLoading(true);
    try {
      const response = await axios.get('http://localhost:8080/api/market/dse/stocks');
      const stockList = Object.values(response.data) as DseStock[];
      setStocks(stockList);
      setError(null);
    } catch (err) {
      setError('Failed to fetch DSE stocks');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDseStocks();
  }, []);

  if (loading) {
    return (
      <div className="bg-white dark:bg-gray-800 rounded-lg p-6 shadow">
        <div className="space-y-3">
          {[1, 2, 3, 4].map((i) => (
            <div key={i} className="h-12 bg-gray-200 dark:bg-gray-700 rounded animate-pulse"></div>
          ))}
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="bg-red-50 dark:bg-red-900 border border-red-200 dark:border-red-700 rounded-lg p-4">
        <div className="flex items-center gap-2 text-red-700 dark:text-red-200">
          <AlertCircle size={16} />
          <span>{error}</span>
        </div>
      </div>
    );
  }

  return (
    <div className="bg-white dark:bg-gray-800 rounded-lg shadow-md overflow-hidden">
      <div className="bg-gradient-to-r from-blue-600 to-indigo-600 px-6 py-4">
        <div className="flex justify-between items-center">
          <h3 className="text-lg font-bold text-white">DSE Market Overview</h3>
          <button
            onClick={fetchDseStocks}
            className="text-white hover:bg-white hover:bg-opacity-20 p-2 rounded-lg transition"
            title="Refresh"
          >
            <RefreshCw size={18} />
          </button>
        </div>
      </div>

      <div className="overflow-x-auto">
        <table className="w-full">
          <thead className="bg-gray-50 dark:bg-gray-700 border-b border-gray-200 dark:border-gray-600">
            <tr>
              <th className="px-6 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase">
                Symbol
              </th>
              <th className="px-6 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase">
                Exchange
              </th>
              <th className="px-6 py-3 text-right text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase">
                Price
              </th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-200 dark:divide-gray-600">
            {stocks.map((stock) => (
              <tr
                key={stock.symbol}
                className="hover:bg-gray-50 dark:hover:bg-gray-700 transition"
              >
                <td className="px-6 py-4">
                  <span className="font-semibold text-gray-900 dark:text-white">
                    {stock.symbol}
                  </span>
                </td>
                <td className="px-6 py-4">
                  <span className="inline-block px-3 py-1 bg-blue-100 dark:bg-blue-900 text-blue-700 dark:text-blue-200 text-xs font-medium rounded-full">
                    {stock.exchange}
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
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <div className="bg-gray-50 dark:bg-gray-700 px-6 py-3 border-t border-gray-200 dark:border-gray-600">
        <p className="text-xs text-gray-600 dark:text-gray-400">
          Last updated: {new Date().toLocaleTimeString()}
        </p>
      </div>
    </div>
  );
};

export default DseMarketOverview;
