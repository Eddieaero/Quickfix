'use client'

import { useState, useEffect } from 'react'
import { useRouter } from 'next/navigation'
import { ArrowLeft, AlertCircle } from 'lucide-react'
import axios from 'axios'
import { Card } from '@/components/ui/card'
import { PortfoliosList } from '@/components/PortfoliosList'
import { CreatePortfolioForm } from '@/components/CreatePortfolioForm'

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

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080'

export default function InvestmentsPage() {
  const router = useRouter()
  const [portfolios, setPortfolios] = useState<Portfolio[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [showCreateForm, setShowCreateForm] = useState(false)
  const [userId, setUserId] = useState<string | null>(null)

  useEffect(() => {
    const user = localStorage.getItem('username')
    if (!user) {
      router.push('/')
      return
    }
    setUserId(`USER_${user}`)
    fetchPortfolios(user)
  }, [router])

  const fetchPortfolios = async (user: string) => {
    try {
      setIsLoading(true)
      setError(null)
      const response = await axios.get(`${API_BASE_URL}/api/investments/portfolios`, {
        headers: {
          'X-User-Id': `USER_${user}`,
        },
      })
      setPortfolios(response.data || [])
    } catch (err) {
      console.error('Failed to fetch portfolios:', err)
      setError('Failed to load portfolios. Please try again.')
    } finally {
      setIsLoading(false)
    }
  }

  const handleCreatePortfolio = async (data: {
    name: string
    initialInvestment: number
  }) => {
    if (!userId) return

    try {
      setIsLoading(true)
      const response = await axios.post(
        `${API_BASE_URL}/api/investments/portfolios`,
        data,
        {
          headers: {
            'X-User-Id': userId,
          },
        }
      )
      setPortfolios([...portfolios, response.data])
      setShowCreateForm(false)
      setError(null)
    } catch (err) {
      console.error('Failed to create portfolio:', err)
      setError('Failed to create portfolio. Please try again.')
    } finally {
      setIsLoading(false)
    }
  }

  const handleDeletePortfolio = async (portfolioId: string) => {
    try {
      await axios.delete(`${API_BASE_URL}/api/investments/portfolios/${portfolioId}`)
      setPortfolios(portfolios.filter((p) => p.portfolioId !== portfolioId))
      setError(null)
    } catch (err) {
      console.error('Failed to delete portfolio:', err)
      setError('Failed to delete portfolio. Please try again.')
    }
  }

  const handleViewDetails = (portfolioId: string) => {
    router.push(`/investments/${portfolioId}`)
  }

  return (
    <div className="min-h-screen bg-background p-8">
      <div className="max-w-7xl mx-auto">
        {/* Header */}
        <div className="flex items-center gap-4 mb-8">
          <button
            onClick={() => router.push('/')}
            className="p-2 hover:bg-background rounded-lg transition-colors"
            title="Back to dashboard"
          >
            <ArrowLeft className="w-5 h-5" />
          </button>
          <h1 className="text-4xl font-bold">Portfolio Management</h1>
        </div>

        {/* Error Alert */}
        {error && (
          <Card className="p-4 mb-6 bg-gray-600/10 border border-gray-600/20">
            <div className="flex items-center gap-3">
              <AlertCircle className="w-5 h-5 text-gray-400" />
              <p className="text-gray-400">{error}</p>
            </div>
          </Card>
        )}

        {/* Create Form Modal */}
        {showCreateForm && (
          <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
            <CreatePortfolioForm
              onSubmit={handleCreatePortfolio}
              onCancel={() => setShowCreateForm(false)}
              isLoading={isLoading}
            />
          </div>
        )}

        {/* Portfolios List */}
        <div className="mt-8">
          <PortfoliosList
            portfolios={portfolios}
            onCreateNew={() => setShowCreateForm(true)}
            onViewDetails={handleViewDetails}
            onEdit={(portfolio) => {
              // TODO: Implement edit functionality
              console.log('Edit portfolio:', portfolio)
            }}
            onDelete={handleDeletePortfolio}
            isLoading={isLoading}
          />
        </div>
      </div>
    </div>
  )
}
