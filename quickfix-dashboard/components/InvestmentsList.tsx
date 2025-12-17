'use client'

import { useState } from 'react'
import { Plus, Edit2, Trash2, TrendingUp } from 'lucide-react'
import { Card } from '@/components/ui/card'

interface Investment {
  investmentId: string
  assetSymbol: string
  assetType: string
  principal: number
  currentValue: number
  annualInterestRate: number
  compoundingFrequency: number
  yearsOfInvestment: number
  totalInterest: number
  status: string
  investmentDate: number
  maturityDate: number
}

interface InvestmentsListProps {
  investments: Investment[]
  onAddNew: () => void
  onEdit: (investment: Investment) => void
  onDelete: (investmentId: string) => void
  portfolioName?: string
  isLoading?: boolean
}

export function InvestmentsList({
  investments,
  onAddNew,
  onEdit,
  onDelete,
  portfolioName,
  isLoading = false,
}: InvestmentsListProps) {
  const formatCurrency = (value: number) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
    }).format(value)
  }

  const formatDate = (timestamp: number) => {
    return new Date(timestamp).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
    })
  }

  const getCompoundingLabel = (frequency: number): string => {
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

  const getAssetTypeColor = (type: string): string => {
    const colors: Record<string, string> = {
      STOCK: 'bg-blue-500/10 text-blue-500',
      BOND: 'bg-purple-500/10 text-purple-500',
      SAVINGS_ACCOUNT: 'bg-green-500/10 text-green-500',
      FIXED_DEPOSIT: 'bg-orange-500/10 text-orange-500',
      MUTUAL_FUND: 'bg-pink-500/10 text-pink-500',
    }
    return colors[type] || 'bg-gray-500/10 text-gray-500'
  }

  if (isLoading) {
    return (
      <div className="space-y-4">
        {[1, 2].map((i) => (
          <Card key={i} className="p-6 animate-pulse">
            <div className="h-6 bg-muted rounded w-1/3 mb-4" />
            <div className="h-4 bg-muted rounded w-1/2" />
          </Card>
        ))}
      </div>
    )
  }

  if (investments.length === 0) {
    return (
      <Card className="p-12 text-center">
        <TrendingUp className="w-12 h-12 mx-auto text-muted-foreground mb-4" />
        <h3 className="text-lg font-semibold mb-2">No Investments</h3>
        <p className="text-muted-foreground mb-6">
          Add your first investment to this portfolio
        </p>
        <button
          onClick={onAddNew}
          className="inline-flex items-center gap-2 px-4 py-2 bg-accent text-accent-foreground rounded-lg hover:bg-accent/90 transition-colors"
        >
          <Plus className="w-4 h-4" />
          Add Investment
        </button>
      </Card>
    )
  }

  return (
    <div className="space-y-4">
      <div className="flex justify-between items-center">
        <h3 className="text-xl font-semibold">Investments</h3>
        <button
          onClick={onAddNew}
          className="inline-flex items-center gap-2 px-3 py-2 text-sm bg-accent text-accent-foreground rounded-lg hover:bg-accent/90 transition-colors"
        >
          <Plus className="w-4 h-4" />
          Add Investment
        </button>
      </div>

      <div className="grid gap-4">
        {investments.map((investment) => {
          const gainAmount = investment.currentValue - investment.principal
          const gainPercentage = (gainAmount / investment.principal) * 100

          return (
            <Card
              key={investment.investmentId}
              className="p-6 hover:shadow-lg transition-shadow"
            >
              <div className="flex items-start justify-between mb-4">
                <div className="flex-1">
                  <div className="flex items-center gap-3 mb-2">
                    <h4 className="text-lg font-semibold">{investment.assetSymbol}</h4>
                    <span className={`px-3 py-1 rounded-full text-xs font-medium ${getAssetTypeColor(investment.assetType)}`}>
                      {investment.assetType.replace(/_/g, ' ')}
                    </span>
                    <span className="px-3 py-1 rounded-full text-xs font-medium bg-secondary text-secondary-foreground">
                      {investment.status}
                    </span>
                  </div>
                  <p className="text-sm text-muted-foreground">
                    {getCompoundingLabel(investment.compoundingFrequency)} • {investment.yearsOfInvestment} year
                    {investment.yearsOfInvestment !== 1 ? 's' : ''} • {investment.annualInterestRate}% APR
                  </p>
                </div>
                <div className="flex gap-2">
                  <button
                    onClick={() => onEdit(investment)}
                    className="p-2 text-muted-foreground hover:bg-background rounded-lg transition-colors"
                    title="Edit investment"
                  >
                    <Edit2 className="w-4 h-4" />
                  </button>
                  <button
                    onClick={() => {
                      if (confirm('Are you sure you want to remove this investment?')) {
                        onDelete(investment.investmentId)
                      }
                    }}
                    className="p-2 text-muted-foreground hover:bg-destructive/10 hover:text-destructive rounded-lg transition-colors"
                    title="Delete investment"
                  >
                    <Trash2 className="w-4 h-4" />
                  </button>
                </div>
              </div>

              <div className="grid grid-cols-2 md:grid-cols-5 gap-4">
                <div>
                  <p className="text-xs text-muted-foreground mb-1 uppercase tracking-wider">Principal</p>
                  <p className="font-semibold">{formatCurrency(investment.principal)}</p>
                </div>
                <div>
                  <p className="text-xs text-muted-foreground mb-1 uppercase tracking-wider">Current Value</p>
                  <p className="font-semibold">{formatCurrency(investment.currentValue)}</p>
                </div>
                <div>
                  <p className="text-xs text-muted-foreground mb-1 uppercase tracking-wider">Interest Earned</p>
                  <p className={`font-semibold ${gainAmount >= 0 ? 'text-green-500' : 'text-red-500'}`}>
                    {formatCurrency(investment.totalInterest)}
                  </p>
                </div>
                <div>
                  <p className="text-xs text-muted-foreground mb-1 uppercase tracking-wider">Gain</p>
                  <p className={`font-semibold ${gainAmount >= 0 ? 'text-green-500' : 'text-red-500'}`}>
                    {gainAmount >= 0 ? '+' : ''}{formatCurrency(gainAmount)}
                  </p>
                </div>
                <div>
                  <p className="text-xs text-muted-foreground mb-1 uppercase tracking-wider">Return %</p>
                  <p className={`font-semibold ${gainPercentage >= 0 ? 'text-green-500' : 'text-red-500'}`}>
                    {gainPercentage >= 0 ? '+' : ''}{gainPercentage.toFixed(2)}%
                  </p>
                </div>
              </div>

              <div className="mt-4 pt-4 border-t border-border text-xs text-muted-foreground">
                Started: {formatDate(investment.investmentDate)} • Matures: {formatDate(investment.maturityDate)}
              </div>
            </Card>
          )
        })}
      </div>
    </div>
  )
}
