'use client'

import { useState } from 'react'
import { Card } from '@/components/ui/card'
import { Calculator } from 'lucide-react'

interface CompoundInterestCalculatorProps {
  onCalculate?: (result: {
    principal: number
    rate: number
    years: number
    frequency: number
    finalAmount: number
    totalInterest: number
  }) => void
}

const FREQUENCIES = [
  { value: 1, label: 'Annually' },
  { value: 2, label: 'Semi-Annually' },
  { value: 4, label: 'Quarterly' },
  { value: 12, label: 'Monthly' },
  { value: 52, label: 'Weekly' },
  { value: 365, label: 'Daily' },
]

export function CompoundInterestCalculator({ onCalculate }: CompoundInterestCalculatorProps) {
  const [principal, setPrincipal] = useState('10000')
  const [rate, setRate] = useState('5')
  const [years, setYears] = useState('10')
  const [frequency, setFrequency] = useState('12')
  const [result, setResult] = useState<{
    finalAmount: number
    totalInterest: number
    compoundingPeriod: string
  } | null>(null)
  const [isCalculating, setIsCalculating] = useState(false)

  const calculate = async () => {
    setIsCalculating(true)
    try {
      const p = parseFloat(principal)
      const r = parseFloat(rate)
      const y = parseInt(years)
      const f = parseInt(frequency)

      // Formula: A = P(1 + r/n)^(nt)
      const rateDecimal = r / 100
      const ratePerPeriod = rateDecimal / f
      const onePlusRate = 1 + ratePerPeriod
      const exponent = f * y
      const finalAmount = p * Math.pow(onePlusRate, exponent)
      const totalInterest = finalAmount - p

      const frequencyLabel = FREQUENCIES.find((freq) => freq.value === f)?.label || 'Unknown'

      const calculationResult = {
        finalAmount: Math.round(finalAmount * 100) / 100,
        totalInterest: Math.round(totalInterest * 100) / 100,
        compoundingPeriod: frequencyLabel,
      }

      setResult(calculationResult)

      if (onCalculate) {
        onCalculate({
          principal: p,
          rate: r,
          years: y,
          frequency: f,
          finalAmount: calculationResult.finalAmount,
          totalInterest: calculationResult.totalInterest,
        })
      }
    } finally {
      setIsCalculating(false)
    }
  }

  const formatCurrency = (value: number) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
    }).format(value)
  }

  return (
    <Card className="p-8 w-full max-w-md">
      <div className="flex items-center gap-3 mb-6">
        <Calculator className="w-6 h-6 text-accent" />
        <h2 className="text-2xl font-bold">Compound Interest Calculator</h2>
      </div>

      <div className="space-y-4 mb-6">
        <div>
          <label className="block text-sm font-medium mb-2">Principal Amount ($)</label>
          <input
            type="number"
            value={principal}
            onChange={(e) => setPrincipal(e.target.value)}
            className="w-full px-4 py-2 border border-border rounded-lg bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-accent focus:border-transparent"
            placeholder="10000"
            step="0.01"
          />
        </div>

        <div>
          <label className="block text-sm font-medium mb-2">Annual Interest Rate (%)</label>
          <input
            type="number"
            value={rate}
            onChange={(e) => setRate(e.target.value)}
            className="w-full px-4 py-2 border border-border rounded-lg bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-accent focus:border-transparent"
            placeholder="5"
            step="0.01"
          />
        </div>

        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium mb-2">Years</label>
            <input
              type="number"
              value={years}
              onChange={(e) => setYears(e.target.value)}
              className="w-full px-4 py-2 border border-border rounded-lg bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-accent focus:border-transparent"
              placeholder="10"
              min="1"
            />
          </div>

          <div>
            <label className="block text-sm font-medium mb-2">Compounding</label>
            <select
              value={frequency}
              onChange={(e) => setFrequency(e.target.value)}
              className="w-full px-4 py-2 border border-border rounded-lg bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-accent focus:border-transparent"
            >
              {FREQUENCIES.map((freq) => (
                <option key={freq.value} value={freq.value}>
                  {freq.label}
                </option>
              ))}
            </select>
          </div>
        </div>

        <button
          onClick={calculate}
          disabled={isCalculating}
          className="w-full px-4 py-2 bg-accent text-accent-foreground rounded-lg hover:bg-accent/90 transition-colors font-medium disabled:opacity-50"
        >
          {isCalculating ? 'Calculating...' : 'Calculate'}
        </button>
      </div>

      {result && (
        <div className="space-y-4 pt-6 border-t border-border">
          <h3 className="font-semibold text-lg mb-4">Results</h3>

          <div className="grid grid-cols-2 gap-4">
            <div className="bg-background p-4 rounded-lg">
              <p className="text-xs text-muted-foreground mb-1 uppercase tracking-wider">
                Initial Investment
              </p>
              <p className="text-lg font-semibold">{formatCurrency(parseFloat(principal))}</p>
            </div>

            <div className="bg-background p-4 rounded-lg">
              <p className="text-xs text-muted-foreground mb-1 uppercase tracking-wider">
                Final Amount
              </p>
              <p className="text-lg font-semibold text-gray-300">{formatCurrency(result.finalAmount)}</p>
            </div>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div className="bg-background p-4 rounded-lg">
              <p className="text-xs text-muted-foreground mb-1 uppercase tracking-wider">
                Total Interest
              </p>
              <p className="text-lg font-semibold text-gray-300">{formatCurrency(result.totalInterest)}</p>
            </div>

            <div className="bg-background p-4 rounded-lg">
              <p className="text-xs text-muted-foreground mb-1 uppercase tracking-wider">
                Return %
              </p>
              <p className="text-lg font-semibold text-gray-300">
                {(
                  ((result.finalAmount - parseFloat(principal)) / parseFloat(principal)) *
                  100
                ).toFixed(2)}
                %
              </p>
            </div>
          </div>

          <div className="bg-accent/10 p-4 rounded-lg">
            <p className="text-sm text-muted-foreground mb-1">Compounding Frequency</p>
            <p className="font-semibold">{result.compoundingPeriod}</p>
          </div>

          <div className="text-xs text-muted-foreground pt-2">
            <p>
              Starting with {formatCurrency(parseFloat(principal))} at {rate}% annual interest,
              compounded {result.compoundingPeriod.toLowerCase()}, you will have{' '}
              <span className="font-semibold text-gray-300">{formatCurrency(result.finalAmount)}</span> in{' '}
              {years} year{parseInt(years) !== 1 ? 's' : ''}.
            </p>
          </div>
        </div>
      )}
    </Card>
  )
}
