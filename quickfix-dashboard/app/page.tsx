'use client'

import { Button } from '@/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { 
  BarChart3, 
  TrendingUp, 
  Zap, 
  Shield, 
  Wifi, 
  Code2, 
  ArrowRight,
  CheckCircle2,
  Activity,
  PieChart
} from 'lucide-react'
import Link from 'next/link'
import { useState, useEffect } from 'react'

export default function LandingPage() {
  const [scrolled, setScrolled] = useState(false)

  useEffect(() => {
    const handleScroll = () => {
      setScrolled(window.scrollY > 10)
    }
    window.addEventListener('scroll', handleScroll)
    return () => window.removeEventListener('scroll', handleScroll)
  }, [])
        
  return (
    <div className="min-h-screen bg-gradient-to-b from-slate-950 via-slate-900 to-slate-950">
      {/* Navigation */}
      <nav className={`fixed w-full top-0 z-50 transition-all ${scrolled ? 'bg-slate-950/80 backdrop-blur border-b border-slate-800' : 'bg-transparent'}`}>
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4 flex justify-between items-center">
          <div className="flex items-center gap-2">
            <Zap className="w-8 h-8 text-gray-400" />
            <span className="text-xl font-bold text-white">Aero</span>
          </div>
          <Link href="/login">
            <Button className="bg-gray-700 hover:bg-gray-600">Sign In</Button>
          </Link>
        </div>
      </nav>

      {/* Hero Section */}
      <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pt-32 pb-20 text-center">
        <div className="mb-8 inline-block">
          <span className="px-4 py-2 bg-gray-500/10 border border-gray-500/30 rounded-full text-gray-300 text-sm font-medium">
            🚀 Real-Time FIX Trading Platform
          </span>
        </div>
        
        <h1 className="text-5xl sm:text-6xl font-bold text-white mb-6 leading-tight">
          Trading Intelligence <br />
          <span className="bg-gradient-to-r from-gray-300 to-gray-400 bg-clip-text text-transparent">
            Powered by FIX Protocol
          </span>
        </h1>
        
        <p className="text-xl text-slate-400 max-w-2xl mx-auto mb-8">
          Execute algorithmic trading strategies with enterprise-grade FIX protocol connectivity. Real-time market data, advanced analytics, and intelligent risk management.
        </p>
        
        <div className="flex flex-col sm:flex-row gap-4 justify-center items-center">
          <Link href="/dashboard">
            <Button size="lg" className="bg-gray-700 hover:bg-gray-600 text-white px-8">
              Launch Dashboard <ArrowRight className="ml-2 w-4 h-4" />
            </Button>
          </Link>
          <Button variant="outline" size="lg" className="border-slate-700 text-white hover:bg-slate-800 px-8">
            View Documentation
          </Button>
        </div>
      </section>

      {/* Features Grid */}
      <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-20">
        <div className="text-center mb-16">
          <h2 className="text-3xl sm:text-4xl font-bold text-white mb-4">Core Features</h2>
          <p className="text-lg text-slate-400">Everything you need for professional trading</p>
        </div>

        <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-8">
          {/* Feature 1 */}
          <Card className="bg-slate-900/50 border-slate-800 hover:border-gray-600/50 transition-all">
            <CardHeader>
              <Wifi className="w-8 h-8 text-gray-400 mb-2" />
              <CardTitle className="text-white">Real-Time Connectivity</CardTitle>
            </CardHeader>
            <CardContent>
              <CardDescription className="text-slate-400">
                WebSocket-based trade streaming with sub-millisecond latency. Instant updates on order status and executions.
              </CardDescription>
            </CardContent>
          </Card>

          {/* Feature 2 */}
          <Card className="bg-slate-900/50 border-slate-800 hover:border-gray-600/50 transition-all">
            <CardHeader>
              <BarChart3 className="w-8 h-8 text-gray-400 mb-2" />
              <CardTitle className="text-white">Market Analytics</CardTitle>
            </CardHeader>
            <CardContent>
              <CardDescription className="text-slate-400">
                Real-time market data integration with multiple sources. Technical indicators and price analysis tools.
              </CardDescription>
            </CardContent>
          </Card>

          {/* Feature 3 */}
          <Card className="bg-slate-900/50 border-slate-800 hover:border-gray-600/50 transition-all">
            <CardHeader>
              <Shield className="w-8 h-8 text-gray-400 mb-2" />
              <CardTitle className="text-white">Risk Management</CardTitle>
            </CardHeader>
            <CardContent>
              <CardDescription className="text-slate-400">
                Advanced position monitoring, stop-loss mechanisms, and portfolio-level risk limits with real-time alerts.
              </CardDescription>
            </CardContent>
          </Card>

          {/* Feature 4 */}
          <Card className="bg-slate-900/50 border-slate-800 hover:border-gray-600/50 transition-all">
            <CardHeader>
              <TrendingUp className="w-8 h-8 text-gray-400 mb-2" />
              <CardTitle className="text-white">Strategy Execution</CardTitle>
            </CardHeader>
            <CardContent>
              <CardDescription className="text-slate-400">
                Deploy algorithmic trading strategies with FIX protocol execution. Support for multiple order types and conditions.
              </CardDescription>
            </CardContent>
          </Card>

          {/* Feature 5 */}
          <Card className="bg-slate-900/50 border-slate-800 hover:border-gray-600/50 transition-all">
            <CardHeader>
              <PieChart className="w-8 h-8 text-gray-400 mb-2" />
              <CardTitle className="text-white">Portfolio Tracking</CardTitle>
            </CardHeader>
            <CardContent>
              <CardDescription className="text-slate-400">
                Monitor positions, P&L, and performance metrics in real-time. Detailed trade history and analytics.
              </CardDescription>
            </CardContent>
          </Card>

          {/* Feature 6 */}
          <Card className="bg-slate-900/50 border-slate-800 hover:border-gray-600/50 transition-all">
            <CardHeader>
              <Code2 className="w-8 h-8 text-gray-400 mb-2" />
              <CardTitle className="text-white">Developer Ready</CardTitle>
            </CardHeader>
            <CardContent>
              <CardDescription className="text-slate-400">
                REST APIs and WebSocket interfaces for custom integrations. Comprehensive documentation and examples.
              </CardDescription>
            </CardContent>
          </Card>
        </div>
      </section>

      {/* How It Works */}
      <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-20">
        <div className="text-center mb-16">
          <h2 className="text-3xl sm:text-4xl font-bold text-white mb-4">How It Works</h2>
          <p className="text-lg text-slate-400">Seamless integration from market data to trade execution</p>
        </div>

        <div className="grid md:grid-cols-4 gap-8">
          {[
            { num: '01', title: 'Market Data', desc: 'Real-time price feeds from multiple sources via EODHD API' },
            { num: '02', title: 'Analysis', desc: 'Technical indicators and quantitative signals' },
            { num: '03', title: 'Execution', desc: 'Order generation and FIX protocol transmission' },
            { num: '04', title: 'Monitoring', desc: 'Real-time tracking and performance analytics' }
          ].map((step, idx) => (
            <div key={idx} className="text-center">
              <div className="w-16 h-16 bg-gray-700/20 border border-gray-600/50 rounded-lg flex items-center justify-center mx-auto mb-4">
                <span className="text-2xl font-bold text-gray-400">{step.num}</span>
              </div>
              <h3 className="text-lg font-semibold text-white mb-2">{step.title}</h3>
              <p className="text-slate-400">{step.desc}</p>
            </div>
          ))}
        </div>
      </section>

      {/* Technology Stack */}
      <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-20">
        <div className="text-center mb-16">
          <h2 className="text-3xl sm:text-4xl font-bold text-white mb-4">Built on Proven Technology</h2>
          <p className="text-lg text-slate-400">Enterprise-grade components for reliability</p>
        </div>

        <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-6">
          {[
            { name: 'QuickFIX/J', desc: 'Open-source FIX engine' },
            { name: 'Spring Boot', desc: 'Enterprise Java framework' },
            { name: 'Next.js', desc: 'Modern React framework' },
            { name: 'WebSocket', desc: 'Real-time communication' },
            { name: 'EODHD API', desc: 'Market data provider' },
            { name: 'PostgreSQL', desc: 'Data persistence' },
            { name: 'React', desc: 'UI library' },
            { name: 'Tailwind CSS', desc: 'Styling framework' }
          ].map((tech, idx) => (
            <div key={idx} className="bg-slate-900/50 border border-slate-800 rounded-lg p-4 text-center hover:border-gray-600/50 transition-all">
              <h3 className="font-semibold text-white mb-1">{tech.name}</h3>
              <p className="text-sm text-slate-400">{tech.desc}</p>
            </div>
          ))}
        </div>
      </section>

      {/* Capabilities */}
      <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-20">
        <div className="text-center mb-16">
          <h2 className="text-3xl sm:text-4xl font-bold text-white mb-4">Quantitative Capabilities</h2>
          <p className="text-lg text-slate-400">Tools for the modern quant trader</p>
        </div>

        <div className="grid md:grid-cols-2 gap-8">
          <div className="bg-slate-900/50 border border-slate-800 rounded-lg p-8">
            <h3 className="text-xl font-bold text-white mb-6">Current Features</h3>
            <ul className="space-y-3">
              {[
                'Real-time FIX protocol connectivity',
                'WebSocket-based trade streaming',
                'Market price data aggregation',
                'Order execution and tracking',
                'Trade history and analytics',
                'Position monitoring dashboard'
              ].map((feature, idx) => (
                <li key={idx} className="flex items-center gap-3 text-slate-300">
                  <CheckCircle2 className="w-5 h-5 text-green-500 flex-shrink-0" />
                  {feature}
                </li>
              ))}
            </ul>
          </div>

          <div className="bg-slate-900/50 border border-slate-800 rounded-lg p-8">
            <h3 className="text-xl font-bold text-white mb-6">Roadmap</h3>
            <ul className="space-y-3">
              {[
                'Backtesting framework for strategy validation',
                'Technical indicator library and signals',
                'Portfolio optimization engine',
                'Machine learning prediction models',
                'Advanced risk metrics (VaR, Sortino, etc.)',
                'Multi-asset class support'
              ].map((feature, idx) => (
                <li key={idx} className="flex items-center gap-3 text-slate-400">
                  <Activity className="w-5 h-5 text-gray-500/50 flex-shrink-0" />
                  {feature}
                </li>
              ))}
            </ul>
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-20 text-center">
        <div className="bg-gradient-to-r from-gray-900/20 to-gray-800/10 border border-gray-500/30 rounded-lg p-12">
          <h2 className="text-3xl sm:text-4xl font-bold text-white mb-4">Ready to Get Started?</h2>
          <p className="text-lg text-slate-400 mb-8">
            Access your trading dashboard and start executing strategies with real-time market data.
          </p>
          <Link href="/dashboard">
            <Button size="lg" className="bg-gray-700 hover:bg-gray-600 text-white px-8">
              Enter Dashboard <ArrowRight className="ml-2 w-4 h-4" />
            </Button>
          </Link>
        </div>
      </section>

      {/* Footer */}
      <footer className="border-t border-slate-800 mt-20">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
          <div className="grid md:grid-cols-4 gap-8 mb-8">
            <div>
              <div className="flex items-center gap-2 mb-4">
                <Zap className="w-6 h-6 text-gray-400" />
                <span className="font-bold text-white">Aero</span>
              </div>
              <p className="text-slate-400 text-sm">Enterprise trading platform powered by FIX protocol</p>
            </div>
            <div>
              <h3 className="font-semibold text-white mb-4">Product</h3>
              <ul className="space-y-2 text-sm text-slate-400">
                <li><a href="#" className="hover:text-white transition">Features</a></li>
                <li><a href="#" className="hover:text-white transition">Pricing</a></li>
                <li><a href="#" className="hover:text-white transition">Documentation</a></li>
              </ul>
            </div>
            <div>
              <h3 className="font-semibold text-white mb-4">Company</h3>
              <ul className="space-y-2 text-sm text-slate-400">
                <li><a href="#" className="hover:text-white transition">About</a></li>
                <li><a href="#" className="hover:text-white transition">Blog</a></li>
                <li><a href="#" className="hover:text-white transition">Contact</a></li>
              </ul>
            </div>
            <div>
              <h3 className="font-semibold text-white mb-4">Legal</h3>
              <ul className="space-y-2 text-sm text-slate-400">
                <li><a href="#" className="hover:text-white transition">Privacy</a></li>
                <li><a href="#" className="hover:text-white transition">Terms</a></li>
                <li><a href="#" className="hover:text-white transition">Security</a></li>
              </ul>
            </div>
          </div>
          <div className="border-t border-slate-800 pt-8">
            <p className="text-center text-slate-400 text-sm">
              © 2026 Aero Trading Platform. All rights reserved.
            </p>
          </div>
        </div>
      </footer>
    </div>
  )
}