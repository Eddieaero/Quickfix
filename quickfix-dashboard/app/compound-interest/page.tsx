'use client'

import { useState } from 'react'
import { useRouter } from 'next/navigation'
import { ArrowLeft } from 'lucide-react'
import { CompoundInterestCalculator } from '@/components/CompoundInterestCalculator'
import { Card } from '@/components/ui/card'

export default function CompoundInterestPage() {
  const router = useRouter()
  const [calculationHistory, setCalculationHistory] = useState<Array<{
    id: string
    principal: number
    rate: number
    years: number
    frequency: number
    finalAmount: number
    totalInterest: number
    timestamp: number
  }>>([])

  const handleCalculate = (result: {
    principal: number
    rate: number
    years: number
    frequency: number
    finalAmount: number
    totalInterest: number
  }) => {
    setCalculationHistory([
      {
        id: Math.random().toString(36).substr(2, 9),
        ...result,
        timestamp: Date.now(),
      },
      ...calculationHistory,
    ])
  }

  const formatCurrency = (value: number) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
    }).format(value)
  }

  const formatDate = (timestamp: number) => {
    return new Date(timestamp).toLocaleDateString('en-US', {
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    })
  }

  const getFrequencyLabel = (frequency: number): string => {
    const labels: Record<number, string> = {
      1: 'Annually',
      2: 'Semi-Annually',
      4: 'Quarterly',
      12: 'Monthly',
      52: 'Weekly',
      365: 'Daily',
    }
    return labels[frequency] || `${frequency}x/year`
  }

  return (
    <div className="min-h-screen bg-background p-8">
      <div className="max-w-6xl mx-auto">
        {/* Header */}
        <div className="flex items-center gap-4 mb-8">
          <button
            onClick={() => router.push('/')}
            className="p-2 hover:bg-background rounded-lg transition-colors"
            title="Back to dashboard"
          >
            <ArrowLeft className="w-5 h-5" />
          </button>
          <h1 className="text-4xl font-bold">Compound Interest Calculator</h1>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Calculator */}
          <div className="lg:col-span-1">
            <CompoundInterestCalculator onCalculate={handleCalculate} />
          </div>

          {/* History */}
          <div className="lg:col-span-2">
            <Card className="p-8">
              <h2 className="text-2xl font-bold mb-6">Calculation History</h2>

              {calculationHistory.length === 0 ? (
                <div className="text-center py-12">
                  <p className="text-muted-foreground">
                    Your calculation history will appear here
                  </p>
                </div>
              ) : (
                <div className="space-y-4">
                  {calculationHistory.map((calc) => (
                    <div
                      key={calc.id}
                      className="p-4 border border-border rounded-lg hover:bg-background transition-colors"
                    >
                      <div className="flex justify-between items-start mb-3">
                        <div>
                          <p className="font-semibold text-lg">
                            {formatCurrency(calc.principal)} at {calc.rate}% for {calc.years} year
                            {calc.years !== 1 ? 's' : ''}
                          </p>
                          <p className="text-sm text-muted-foreground">
                            Compounding: {getFrequencyLabel(calc.frequency)}
                          </p>
                        </div>
                        <span className="text-xs text-muted-foreground">
                          {formatDate(calc.timestamp)}
                        </span>
                      </div>

                      <div className="grid grid-cols-3 gap-4">
                        <div className="bg-card p-3 rounded">
                          <p className="text-xs text-muted-foreground mb-1">Final Amount</p>
                          <p className="font-semibold text-gray-300">
                            {formatCurrency(calc.finalAmount)}
                          </p>
                        </div>
                        <div className="bg-card p-3 rounded">
                          <p className="text-xs text-muted-foreground mb-1">Interest Earned</p>
                          <p className="font-semibold text-gray-300">
                            {formatCurrency(calc.totalInterest)}
                          </p>
                        </div>
                        <div className="bg-card p-3 rounded">
                          <p className="text-xs text-muted-foreground mb-1">Return %</p>
                          <p className="font-semibold text-gray-300">
                            {(
                              ((calc.finalAmount - calc.principal) / calc.principal) *
                              100
                            ).toFixed(2)}
                            %
                          </p>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </Card>
          </div>
        </div>

        {/* Information */}
        <div className="mt-8 grid grid-cols-1 md:grid-cols-2 gap-6">
          <Card className="p-6">
            <h3 className="text-xl font-semibold mb-4">About Compound Interest</h3>
            <div className="space-y-3 text-sm text-muted-foreground">
              <p>
                Compound interest is the interest earned on both the principal and previously earned
                interest. It's calculated using the formula:
              </p>
              <p className="font-mono bg-background p-3 rounded border border-border">
                A = P(1 + r/n)^(nt)
              </p>
              <p>
                Where:
              </p>
              <ul className="list-disc list-inside space-y-1 ml-2">
                <li>A = Final Amount</li>
                <li>P = Principal (initial investment)</li>
                <li>r = Annual Interest Rate (as decimal)</li>
                <li>n = Compounding Frequency per year</li>
                <li>t = Time in years</li>
              </ul>
            </div>
          </Card>

          <Card className="p-6">
            <h3 className="text-xl font-semibold mb-4">Compounding Frequencies</h3>
            <div className="space-y-2 text-sm">
              <div className="flex justify-between">
                <span className="text-muted-foreground">Annually:</span>
                <span>1 time per year</span>
              </div>
              <div className="flex justify-between">
                <span className="text-muted-foreground">Semi-Annually:</span>
                <span>2 times per year</span>
              </div>
              <div className="flex justify-between">
                <span className="text-muted-foreground">Quarterly:</span>
                <span>4 times per year</span>
              </div>
              <div className="flex justify-between">
                <span className="text-muted-foreground">Monthly:</span>
                <span>12 times per year</span>
              </div>
              <div className="flex justify-between">
                <span className="text-muted-foreground">Weekly:</span>
                <span>52 times per year</span>
              </div>
              <div className="flex justify-between">
                <span className="text-muted-foreground">Daily:</span>
                <span>365 times per year</span>
              </div>
            </div>
          </Card>
        </div>
      </div>
    </div>
  )
}
