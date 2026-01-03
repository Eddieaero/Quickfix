'use client'

import { useState, useEffect, useRef } from 'react'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { StatusBadge } from '@/components/ui/status-badge'
import { Activity, Power, Wifi, WifiOff, BarChart3, TrendingUp, Package } from 'lucide-react'
import axios from 'axios'
import LoginPage from '@/components/LoginPage'

interface TradeData {
  orderId: string
  symbol: string
  side: 'BUY' | 'SELL'
  quantity: number
  price: number
  status: string
  executedQty: number
  executedPrice: number
  timestamp: number
  messageType: string
}

interface TradeStatsDto {
  totalTrades: number
  totalVolume: number
  averagePrice: number
  recentTrades: TradeData[]
}

export default function Dashboard() {
  const [isAuthenticated, setIsAuthenticated] = useState(false)
  const [currentUsername, setCurrentUsername] = useState('')
  const [connectionStatus, setConnectionStatus] = useState<'CONNECTED' | 'DISCONNECTED'>('DISCONNECTED')
  const [wsConnected, setWsConnected] = useState(false)
  const [loading, setLoading] = useState(false)
  const [tradeStats, setTradeStats] = useState<TradeStatsDto>({
    totalTrades: 0,
    totalVolume: 0,
    averagePrice: 0,
    recentTrades: []
  })
  const [lastUpdate, setLastUpdate] = useState<Date | null>(null)
  const wsRef = useRef<WebSocket | null>(null)

  // Check authentication on mount
  useEffect(() => {
    const token = localStorage.getItem('token')
    const username = localStorage.getItem('username')
    
    if (token && username) {
      setIsAuthenticated(true)
      setCurrentUsername(username)
    }
  }, [])

  // Initialize WebSocket for real-time trade updates
  useEffect(() => {
    if (!isAuthenticated) return

    const connectWebSocket = () => {
      try {
        const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:'
        // Connect to backend WebSocket - use localhost:8080 for local dev, adjust as needed
        const wsUrl = typeof window !== 'undefined' && window.location.hostname === 'localhost'
          ? `${protocol}//localhost:8080/ws/trades`
          : `${protocol}//${window.location.host}/ws/trades`
        
        console.log('Connecting to WebSocket:', wsUrl)
        const ws = new WebSocket(wsUrl)
        
        ws.onopen = () => {
          console.log('WebSocket connected')
          setWsConnected(true)
        }
        
        ws.onmessage = (event) => {
          try {
            const data = JSON.parse(event.data) as TradeStatsDto
            setTradeStats(data)
            setLastUpdate(new Date())
          } catch (error) {
            console.error('Failed to parse WebSocket message:', error)
          }
        }
        
        ws.onerror = (error) => {
          console.error('WebSocket error:', error)
          setWsConnected(false)
        }
        
        ws.onclose = () => {
          console.log('WebSocket disconnected')
          setWsConnected(false)
          // Attempt to reconnect after 3 seconds
          setTimeout(connectWebSocket, 3000)
        }
        
        wsRef.current = ws
      } catch (error) {
        console.error('Failed to connect WebSocket:', error)
      }
    }

    connectWebSocket()

    return () => {
      if (wsRef.current) {
        wsRef.current.close()
      }
    }
  }, [])

  // Check FIX connection status and fetch trade data via HTTP fallback
  useEffect(() => {
    const checkStatus = async () => {
      try {
        const response = await axios.get('/api/quickfix/status')
        const statusText = response.data
        const isConnected = statusText.includes('CONNECTED') && !statusText.includes('DISCONNECTED')
        setConnectionStatus(isConnected ? 'CONNECTED' : 'DISCONNECTED')
        
        // If WebSocket is disconnected, fetch trades via HTTP as fallback
        if (!wsConnected) {
          try {
            const tradesResponse = await axios.get('/api/quickfix/trades')
            setTradeStats(tradesResponse.data)
            setLastUpdate(new Date())
          } catch (error) {
            console.debug('Failed to fetch trades via HTTP:', error)
          }
        }
      } catch (error) {
        setConnectionStatus('DISCONNECTED')
      }
    }

    // Check status immediately and then every 5 seconds
    checkStatus()
    const interval = setInterval(checkStatus, 5000)
    return () => clearInterval(interval)
  }, [wsConnected])

  const handleConnect = async () => {
    setLoading(true)
    try {
      await axios.post('/api/quickfix/start')
      setConnectionStatus('CONNECTED')
    } catch (error) {
      console.error('Failed to connect:', error)
    } finally {
      setLoading(false)
    }
  }

  const handleDisconnect = async () => {
    setLoading(true)
    try {
      await axios.post('/api/quickfix/stop')
      setConnectionStatus('DISCONNECTED')
    } catch (error) {
      console.error('Failed to disconnect:', error)
    } finally {
      setLoading(false)
    }
  }

  const handleLogout = () => {
    localStorage.removeItem('token')
    localStorage.removeItem('username')
    setIsAuthenticated(false)
    setCurrentUsername('')
  }

  const handleLoginSuccess = (token: string, username: string) => {
    setIsAuthenticated(true)
    setCurrentUsername(username)
  }

  const formatPrice = (price: number) => `$${price.toFixed(2)}`
  const formatDate = (timestamp: number) => new Date(timestamp).toLocaleTimeString()

  // Show login page if not authenticated
  if (!isAuthenticated) {
    return <LoginPage onLoginSuccess={handleLoginSuccess} />
  }

  return (
    <div className="min-h-screen bg-background p-8">
      <div className="max-w-7xl mx-auto">
        <div className="mb-8">
          <h1 className="text-4xl font-bold text-foreground mb-2">QuickFIX Dashboard</h1>
          <p className="text-muted-foreground">Real-time FIX protocol monitoring with WebSocket streaming</p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
          <Card className="md:col-span-1">
            <CardHeader>
              <CardTitle className="flex items-center gap-2 text-lg">
                <Activity className="w-5 h-5" />
                FIX Status
              </CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="flex items-center justify-center">
                <StatusBadge status={connectionStatus === 'CONNECTED' ? 'connected' : 'disconnected'} />
              </div>
              <div className="flex gap-2">
                <Button onClick={handleConnect} disabled={loading || connectionStatus === 'CONNECTED'} className="flex-1" variant="default">
                  <Wifi className="w-4 h-4 mr-2" />
                  Connect
                </Button>
                <Button onClick={handleDisconnect} disabled={loading || connectionStatus === 'DISCONNECTED'} className="flex-1" variant="destructive">
                  <WifiOff className="w-4 h-4 mr-2" />
                  Disconnect
                </Button>
              </div>
              <div className={`text-xs px-2 py-1 rounded text-center ${wsConnected ? 'bg-gray-700/50 text-gray-200' : 'bg-gray-600/50 text-gray-300'}`}>
                {wsConnected ? '● WebSocket Connected' : '○ WebSocket Disconnected'}
              </div>
              {lastUpdate && <p className="text-xs text-muted-foreground text-center">Updated: {lastUpdate.toLocaleTimeString()}</p>}
            </CardContent>
          </Card>

          <Card className="md:col-span-1">
            <CardHeader>
              <CardTitle className="flex items-center gap-2 text-lg">
                <Package className="w-5 h-5" />
                Total Trades
              </CardTitle>
            </CardHeader>
            <CardContent>
              <div className="text-4xl font-bold text-gray-300">{tradeStats.totalTrades}</div>
              <p className="text-sm text-muted-foreground mt-2">Orders processed</p>
            </CardContent>
          </Card>

          <Card className="md:col-span-1">
            <CardHeader>
              <CardTitle className="flex items-center gap-2 text-lg">
                <BarChart3 className="w-5 h-5" />
                Total Volume
              </CardTitle>
            </CardHeader>
            <CardContent>
              <div className="text-4xl font-bold text-gray-300">{tradeStats.totalVolume.toLocaleString()}</div>
              <p className="text-sm text-muted-foreground mt-2">Shares traded</p>
            </CardContent>
          </Card>

          <Card className="md:col-span-1">
            <CardHeader>
              <CardTitle className="flex items-center gap-2 text-lg">
                <TrendingUp className="w-5 h-5" />
                Avg Price
              </CardTitle>
            </CardHeader>
            <CardContent>
              <div className="text-4xl font-bold text-gray-300">{formatPrice(tradeStats.averagePrice)}</div>
              <p className="text-sm text-muted-foreground mt-2">Average execution price</p>
            </CardContent>
          </Card>
        </div>

        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <Activity className="w-5 h-5" />
              Recent Trades
            </CardTitle>
            <CardDescription>Last 20 executed trades</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="overflow-x-auto">
              <table className="w-full text-sm">
                <thead>
                  <tr className="border-b border-border">
                    <th className="text-left py-3 px-4 font-semibold text-muted-foreground">Time</th>
                    <th className="text-left py-3 px-4 font-semibold text-muted-foreground">Order ID</th>
                    <th className="text-left py-3 px-4 font-semibold text-muted-foreground">Symbol</th>
                    <th className="text-left py-3 px-4 font-semibold text-muted-foreground">Side</th>
                    <th className="text-right py-3 px-4 font-semibold text-muted-foreground">Qty</th>
                    <th className="text-right py-3 px-4 font-semibold text-muted-foreground">Price</th>
                    <th className="text-right py-3 px-4 font-semibold text-muted-foreground">Exec Qty</th>
                    <th className="text-center py-3 px-4 font-semibold text-muted-foreground">Status</th>
                  </tr>
                </thead>
                <tbody>
                  {tradeStats.recentTrades && tradeStats.recentTrades.length > 0 ? (
                    tradeStats.recentTrades.map((trade) => (
                      <tr key={trade.orderId} className="border-b border-border hover:bg-muted/50">
                        <td className="py-3 px-4 text-muted-foreground">{formatDate(trade.timestamp)}</td>
                        <td className="py-3 px-4 font-mono text-foreground text-xs">{trade.orderId}</td>
                        <td className="py-3 px-4 font-bold text-foreground">{trade.symbol}</td>
                        <td className="py-3 px-4">
                          <span className={`px-2 py-1 rounded text-xs font-semibold ${trade.side === 'BUY' ? 'bg-gray-700/50 text-gray-200' : 'bg-gray-600/50 text-gray-300'}`}>
                            {trade.side}
                          </span>
                        </td>
                        <td className="py-3 px-4 text-right text-foreground">{trade.quantity.toLocaleString()}</td>
                        <td className="py-3 px-4 text-right text-foreground">{formatPrice(trade.price)}</td>
                        <td className="py-3 px-4 text-right text-foreground">{trade.executedQty.toLocaleString()}</td>
                        <td className="py-3 px-4 text-center">
                          <span className={`px-2 py-1 rounded text-xs font-semibold ${trade.status === 'FILLED' ? 'bg-gray-700/50 text-gray-200' : trade.status === 'PARTIALLY_FILLED' ? 'bg-gray-600/50 text-gray-300' : 'bg-gray-700/50 text-gray-200'}`}>
                            {trade.status}
                          </span>
                        </td>
                      </tr>
                    ))
                  ) : (
                    <tr>
                      <td colSpan={8} className="py-8 text-center text-muted-foreground">
                        {connectionStatus === 'CONNECTED' ? 'Waiting for trade data...' : 'Connect to FIX server to see trades'}
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  )
}
