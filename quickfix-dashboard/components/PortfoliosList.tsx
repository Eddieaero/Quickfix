'use client'

import { useState } from 'react'
import { TrendingUp, Plus, Edit2, Trash2, BarChart3 } from 'lucide-react'
import { Card } from '@/components/ui/card'

interface Portfolio {
  portfolioId: string
  name: string
  initialInvestment: number
  currentValue: number
  totalGains: number
  totalGainsPercentage: number
  investmentCount: number
  createdAt: number
  active: boolean
}

interface PortfoliosListProps {
  portfolios: Portfolio[]
  onCreateNew: () => void
  onViewDetails: (portfolioId: string) => void
  onEdit: (portfolio: Portfolio) => void
  onDelete: (portfolioId: string) => void
  isLoading?: boolean
}

export function PortfoliosList({
  portfolios,
  onCreateNew,
  onViewDetails,
  onEdit,
  onDelete,
  isLoading = false,
}: PortfoliosListProps) {
  const [expandedId, setExpandedId] = useState<string | null>(null)

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

  if (isLoading) {
    return (
      <div className="space-y-4">
        {[1, 2, 3].map((i) => (
          <Card key={i} className="p-6 animate-pulse">
            <div className="h-6 bg-muted rounded w-1/4 mb-4" />
            <div className="h-4 bg-muted rounded w-1/3" />
          </Card>
        ))}
      </div>
    )
  }

  if (portfolios.length === 0) {
    return (
      <Card className="p-12 text-center">
        <BarChart3 className="w-12 h-12 mx-auto text-muted-foreground mb-4" />
        <h3 className="text-lg font-semibold mb-2">No Portfolios</h3>
        <p className="text-muted-foreground mb-6">
          Create your first investment portfolio to get started
        </p>
        <button
          onClick={onCreateNew}
          className="inline-flex items-center gap-2 px-4 py-2 bg-accent text-accent-foreground rounded-lg hover:bg-accent/90 transition-colors"
        >
          <Plus className="w-4 h-4" />
          Create Portfolio
        </button>
      </Card>
    )
  }

  return (
    <div className="space-y-4">
      <div className="flex justify-between items-center">
        <h2 className="text-2xl font-bold">My Portfolios</h2>
        <button
          onClick={onCreateNew}
          className="inline-flex items-center gap-2 px-4 py-2 bg-accent text-accent-foreground rounded-lg hover:bg-accent/90 transition-colors"
        >
          <Plus className="w-4 h-4" />
          New Portfolio
        </button>
      </div>

      {portfolios.map((portfolio) => (
        <Card
          key={portfolio.portfolioId}
          className="p-6 hover:shadow-lg transition-all cursor-pointer"
          onClick={() => setExpandedId(expandedId === portfolio.portfolioId ? null : portfolio.portfolioId)}
        >
          <div className="flex items-start justify-between mb-4">
            <div className="flex-1">
              <h3 className="text-xl font-semibold">{portfolio.name}</h3>
              <p className="text-sm text-muted-foreground">
                Created {formatDate(portfolio.createdAt)} • {portfolio.investmentCount} investment
                {portfolio.investmentCount !== 1 ? 's' : ''}
              </p>
            </div>
            <div className="flex gap-2">
              <button
                onClick={(e) => {
                  e.stopPropagation()
                  onEdit(portfolio)
                }}
                className="p-2 text-muted-foreground hover:bg-background rounded-lg transition-colors"
                title="Edit portfolio"
              >
                <Edit2 className="w-4 h-4" />
              </button>
              <button
                onClick={(e) => {
                  e.stopPropagation()
                  if (confirm('Are you sure you want to delete this portfolio?')) {
                    onDelete(portfolio.portfolioId)
                  }
                }}
                className="p-2 text-muted-foreground hover:bg-destructive/10 hover:text-destructive rounded-lg transition-colors"
                title="Delete portfolio"
              >
                <Trash2 className="w-4 h-4" />
              </button>
            </div>
          </div>

          <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-4">
            <div>
              <p className="text-sm text-muted-foreground mb-1">Initial Investment</p>
              <p className="text-lg font-semibold">{formatCurrency(portfolio.initialInvestment)}</p>
            </div>
            <div>
              <p className="text-sm text-muted-foreground mb-1">Current Value</p>
              <p className="text-lg font-semibold">{formatCurrency(portfolio.currentValue)}</p>
            </div>
            <div>
              <p className="text-sm text-muted-foreground mb-1">Total Gains</p>
              <p className={`text-lg font-semibold flex items-center gap-1 ${
                portfolio.totalGains >= 0 ? 'text-green-500' : 'text-red-500'
              }`}>
                {portfolio.totalGains >= 0 ? '+' : ''}{formatCurrency(portfolio.totalGains)}
                {portfolio.totalGains >= 0 && <TrendingUp className="w-4 h-4" />}
              </p>
            </div>
            <div>
              <p className="text-sm text-muted-foreground mb-1">ROI</p>
              <p className={`text-lg font-semibold ${
                portfolio.totalGainsPercentage >= 0 ? 'text-green-500' : 'text-red-500'
              }`}>
                {portfolio.totalGainsPercentage >= 0 ? '+' : ''}{portfolio.totalGainsPercentage.toFixed(2)}%
              </p>
            </div>
          </div>

          {expandedId === portfolio.portfolioId && (
            <div className="pt-4 border-t border-border">
              <button
                onClick={(e) => {
                  e.stopPropagation()
                  onViewDetails(portfolio.portfolioId)
                }}
                className="w-full px-4 py-2 bg-secondary text-secondary-foreground rounded-lg hover:bg-secondary/90 transition-colors"
              >
                View Details & Investments
              </button>
            </div>
          )}
        </Card>
      ))}
    </div>
  )
}
