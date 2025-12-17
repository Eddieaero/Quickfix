'use client'

import { useState, useEffect } from 'react'
import { useRouter, useParams } from 'next/navigation'
import { ArrowLeft, AlertCircle, BarChart3 } from 'lucide-react'
import axios from 'axios'
import { Card } from '@/components/ui/card'
import { InvestmentsList } from '@/components/InvestmentsList'
import { CreateInvestmentForm } from '@/components/CreateInvestmentForm'

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

interface Portfolio {
  portfolioId: string
  name: string
  initialInvestment: number
  currentValue: number
  totalGains: number
  totalGainsPercentage: number
  investments: Investment[]
}

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080'

export default function PortfolioDetailsPage() {
  const router = useRouter()
  const params = useParams()
  const portfolioId = params?.id as string

  const [portfolio, setPortfolio] = useState<Portfolio | null>(null)
  const [investments, setInvestments] = useState<Investment[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [showInvestmentForm, setShowInvestmentForm] = useState(false)

  useEffect(() => {
    if (portfolioId) {
      fetchPortfolioDetails()
      fetchInvestments()
    }
  }, [portfolioId])

  const fetchPortfolioDetails = async () => {
    try {
      const response = await axios.get(
        `${API_BASE_URL}/api/investments/portfolios/${portfolioId}`
      )
      setPortfolio(response.data)
    } catch (err) {
      console.error('Failed to fetch portfolio:', err)
      setError('Failed to load portfolio details.')
    }
  }

  const fetchInvestments = async () => {
    try {
      setIsLoading(true)
      const response = await axios.get(
        `${API_BASE_URL}/api/investments/portfolios/${portfolioId}/investments`
      )
      setInvestments(response.data || [])
    } catch (err) {
      console.error('Failed to fetch investments:', err)
      setError('Failed to load investments.')
    } finally {
      setIsLoading(false)
    }
  }

  const handleAddInvestment = async (data: {
    assetSymbol: string
    assetType: string
    principal: number
    annualInterestRate: number
    compoundingFrequency: number
    yearsOfInvestment: number
  }) => {
    try {
      const response = await axios.post(
        `${API_BASE_URL}/api/investments/portfolios/${portfolioId}/investments`,
        data
      )
      setInvestments([...investments, response.data])
      setShowInvestmentForm(false)
      setError(null)
      await fetchPortfolioDetails()
    } catch (err) {
      console.error('Failed to add investment:', err)
      setError('Failed to add investment. Please try again.')
    }
  }

  const handleDeleteInvestment = async (investmentId: string) => {
    try {
      await axios.delete(`${API_BASE_URL}/api/investments/${investmentId}`)
      setInvestments(investments.filter((inv) => inv.investmentId !== investmentId))
      setError(null)
      await fetchPortfolioDetails()
    } catch (err) {
      console.error('Failed to delete investment:', err)
      setError('Failed to delete investment. Please try again.')
    }
  }

  const formatCurrency = (value: number) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
    }).format(value)
  }

  if (isLoading && !portfolio) {
    return (
      <div className="min-h-screen bg-background p-8">
        <div className="max-w-7xl mx-auto">
          <div className="animate-pulse space-y-4">
            <div className="h-8 bg-muted rounded w-1/4" />
            <div className="h-32 bg-muted rounded" />
          </div>
        </div>
      </div>
    )
  }

  if (!portfolio) {
    return (
      <div className="min-h-screen bg-background p-8">
        <div className="max-w-7xl mx-auto">
          <button
            onClick={() => router.push('/investments')}
            className="flex items-center gap-2 text-muted-foreground hover:text-foreground mb-8"
          >
            <ArrowLeft className="w-5 h-5" />
            Back to Portfolios
          </button>
          <Card className="p-8 text-center">
            <AlertCircle className="w-12 h-12 mx-auto text-red-500 mb-4" />
            <h2 className="text-xl font-semibold mb-2">Portfolio Not Found</h2>
            <p className="text-muted-foreground">The portfolio you're looking for doesn't exist.</p>
          </Card>
        </div>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-background p-8">
      <div className="max-w-7xl mx-auto">
        {/* Header */}
        <button
          onClick={() => router.push('/investments')}
          className="flex items-center gap-2 text-muted-foreground hover:text-foreground mb-8"
        >
          <ArrowLeft className="w-5 h-5" />
          Back to Portfolios
        </button>

        {/* Error Alert */}
        {error && (
          <Card className="p-4 mb-6 bg-red-500/10 border border-red-500/20">
            <div className="flex items-center gap-3">
              <AlertCircle className="w-5 h-5 text-red-500" />
              <p className="text-red-500">{error}</p>
            </div>
          </Card>
        )}

        {/* Portfolio Summary */}
        <Card className="p-8 mb-8">
          <h1 className="text-3xl font-bold mb-2">{portfolio.name}</h1>
          
          <div className="grid grid-cols-2 md:grid-cols-4 gap-6 mt-6">
            <div>
              <p className="text-sm text-muted-foreground mb-2">Initial Investment</p>
              <p className="text-2xl font-bold">{formatCurrency(portfolio.initialInvestment)}</p>
            </div>
            <div>
              <p className="text-sm text-muted-foreground mb-2">Current Value</p>
              <p className="text-2xl font-bold text-green-500">{formatCurrency(portfolio.currentValue)}</p>
            </div>
            <div>
              <p className="text-sm text-muted-foreground mb-2">Total Gains</p>
              <p className={`text-2xl font-bold ${
                portfolio.totalGains >= 0 ? 'text-green-500' : 'text-red-500'
              }`}>
                {portfolio.totalGains >= 0 ? '+' : ''}{formatCurrency(portfolio.totalGains)}
              </p>
            </div>
            <div>
              <p className="text-sm text-muted-foreground mb-2">ROI</p>
              <p className={`text-2xl font-bold ${
                portfolio.totalGainsPercentage >= 0 ? 'text-green-500' : 'text-red-500'
              }`}>
                {portfolio.totalGainsPercentage >= 0 ? '+' : ''}{portfolio.totalGainsPercentage.toFixed(2)}%
              </p>
            </div>
          </div>
        </Card>

        {/* Investment Form Modal */}
        {showInvestmentForm && (
          <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
            <CreateInvestmentForm
              portfolioId={portfolioId}
              onSubmit={handleAddInvestment}
              onCancel={() => setShowInvestmentForm(false)}
              isLoading={isLoading}
            />
          </div>
        )}

        {/* Investments List */}
        <div>
          <InvestmentsList
            investments={investments}
            onAddNew={() => setShowInvestmentForm(true)}
            onEdit={(investment) => {
              // TODO: Implement edit functionality
              console.log('Edit investment:', investment)
            }}
            onDelete={handleDeleteInvestment}
            portfolioName={portfolio.name}
            isLoading={isLoading}
          />
        </div>
      </div>
    </div>
  )
}
