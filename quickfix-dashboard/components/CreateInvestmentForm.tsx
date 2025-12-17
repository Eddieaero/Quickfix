'use client'

import { useState, useEffect } from 'react'
import { X } from 'lucide-react'
import { Card } from '@/components/ui/card'

interface CreateInvestmentFormProps {
  portfolioId: string
  onSubmit: (data: {
    assetSymbol: string
    assetType: string
    principal: number
    annualInterestRate: number
    compoundingFrequency: number
    yearsOfInvestment: number
  }) => void
  onCancel: () => void
  isLoading?: boolean
  initialData?: {
    assetSymbol: string
    assetType: string
    principal: number
    annualInterestRate: number
    compoundingFrequency: number
    yearsOfInvestment: number
  }
  isEditMode?: boolean
}

const ASSET_TYPES = [
  { value: 'STOCK', label: 'Stock' },
  { value: 'BOND', label: 'Bond' },
  { value: 'SAVINGS_ACCOUNT', label: 'Savings Account' },
  { value: 'FIXED_DEPOSIT', label: 'Fixed Deposit' },
  { value: 'MUTUAL_FUND', label: 'Mutual Fund' },
]

const COMPOUNDING_FREQUENCIES = [
  { value: 1, label: 'Annually' },
  { value: 2, label: 'Semi-Annually' },
  { value: 4, label: 'Quarterly' },
  { value: 12, label: 'Monthly' },
  { value: 52, label: 'Weekly' },
  { value: 365, label: 'Daily' },
]

export function CreateInvestmentForm({
  portfolioId,
  onSubmit,
  onCancel,
  isLoading = false,
  initialData,
  isEditMode = false,
}: CreateInvestmentFormProps) {
  const [formData, setFormData] = useState({
    assetSymbol: '',
    assetType: 'STOCK',
    principal: '',
    annualInterestRate: '',
    compoundingFrequency: 12,
    yearsOfInvestment: 5,
  })

  const [errors, setErrors] = useState<Record<string, string>>({})

  useEffect(() => {
    if (initialData) {
      setFormData(initialData)
    }
  }, [initialData])

  const validate = () => {
    const newErrors: Record<string, string> = {}

    if (!formData.assetSymbol.trim()) {
      newErrors.assetSymbol = 'Asset symbol is required'
    }

    if (!formData.principal) {
      newErrors.principal = 'Principal is required'
    } else if (isNaN(Number(formData.principal)) || Number(formData.principal) <= 0) {
      newErrors.principal = 'Principal must be a positive number'
    }

    if (!formData.annualInterestRate) {
      newErrors.annualInterestRate = 'Interest rate is required'
    } else if (isNaN(Number(formData.annualInterestRate)) || Number(formData.annualInterestRate) < 0) {
      newErrors.annualInterestRate = 'Interest rate must be a non-negative number'
    }

    if (!formData.yearsOfInvestment || Number(formData.yearsOfInvestment) <= 0) {
      newErrors.yearsOfInvestment = 'Years must be a positive number'
    }

    setErrors(newErrors)
    return Object.keys(newErrors).length === 0
  }

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()

    if (!validate()) {
      return
    }

    onSubmit({
      assetSymbol: formData.assetSymbol.trim(),
      assetType: formData.assetType,
      principal: Number(formData.principal),
      annualInterestRate: Number(formData.annualInterestRate),
      compoundingFrequency: formData.compoundingFrequency,
      yearsOfInvestment: Number(formData.yearsOfInvestment),
    })
  }

  const handleChange = (field: string, value: any) => {
    setFormData((prev) => ({ ...prev, [field]: value }))
    if (errors[field]) {
      setErrors({ ...errors, [field]: '' })
    }
  }

  return (
    <Card className="p-6 w-full max-w-md">
      <div className="flex justify-between items-center mb-6">
        <h2 className="text-2xl font-bold">
          {isEditMode ? 'Edit Investment' : 'Add Investment'}
        </h2>
        <button
          onClick={onCancel}
          className="p-1 hover:bg-background rounded-lg transition-colors"
        >
          <X className="w-5 h-5" />
        </button>
      </div>

      <form onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label className="block text-sm font-medium mb-2">Asset Symbol</label>
          <input
            type="text"
            value={formData.assetSymbol}
            onChange={(e) => handleChange('assetSymbol', e.target.value)}
            className="w-full px-4 py-2 border border-border rounded-lg bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-accent focus:border-transparent"
            placeholder="e.g., AAPL"
            disabled={isLoading}
          />
          {errors.assetSymbol && <p className="text-xs text-red-500 mt-1">{errors.assetSymbol}</p>}
        </div>

        <div>
          <label className="block text-sm font-medium mb-2">Asset Type</label>
          <select
            value={formData.assetType}
            onChange={(e) => handleChange('assetType', e.target.value)}
            className="w-full px-4 py-2 border border-border rounded-lg bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-accent focus:border-transparent"
            disabled={isLoading}
          >
            {ASSET_TYPES.map((type) => (
              <option key={type.value} value={type.value}>
                {type.label}
              </option>
            ))}
          </select>
        </div>

        <div>
          <label className="block text-sm font-medium mb-2">Principal ($)</label>
          <input
            type="number"
            value={formData.principal}
            onChange={(e) => handleChange('principal', e.target.value)}
            className="w-full px-4 py-2 border border-border rounded-lg bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-accent focus:border-transparent"
            placeholder="10000"
            step="0.01"
            disabled={isLoading}
          />
          {errors.principal && <p className="text-xs text-red-500 mt-1">{errors.principal}</p>}
        </div>

        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium mb-2">Annual Interest Rate (%)</label>
            <input
              type="number"
              value={formData.annualInterestRate}
              onChange={(e) => handleChange('annualInterestRate', e.target.value)}
              className="w-full px-4 py-2 border border-border rounded-lg bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-accent focus:border-transparent"
              placeholder="5.5"
              step="0.01"
              disabled={isLoading}
            />
            {errors.annualInterestRate && (
              <p className="text-xs text-red-500 mt-1">{errors.annualInterestRate}</p>
            )}
          </div>

          <div>
            <label className="block text-sm font-medium mb-2">Years</label>
            <input
              type="number"
              value={formData.yearsOfInvestment}
              onChange={(e) => handleChange('yearsOfInvestment', e.target.value)}
              className="w-full px-4 py-2 border border-border rounded-lg bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-accent focus:border-transparent"
              placeholder="5"
              min="1"
              disabled={isLoading}
            />
            {errors.yearsOfInvestment && (
              <p className="text-xs text-red-500 mt-1">{errors.yearsOfInvestment}</p>
            )}
          </div>
        </div>

        <div>
          <label className="block text-sm font-medium mb-2">Compounding Frequency</label>
          <select
            value={formData.compoundingFrequency}
            onChange={(e) => handleChange('compoundingFrequency', Number(e.target.value))}
            className="w-full px-4 py-2 border border-border rounded-lg bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-accent focus:border-transparent"
            disabled={isLoading}
          >
            {COMPOUNDING_FREQUENCIES.map((freq) => (
              <option key={freq.value} value={freq.value}>
                {freq.label}
              </option>
            ))}
          </select>
        </div>

        <div className="flex gap-3 pt-4">
          <button
            type="button"
            onClick={onCancel}
            disabled={isLoading}
            className="flex-1 px-4 py-2 border border-border rounded-lg hover:bg-background transition-colors disabled:opacity-50"
          >
            Cancel
          </button>
          <button
            type="submit"
            disabled={isLoading}
            className="flex-1 px-4 py-2 bg-accent text-accent-foreground rounded-lg hover:bg-accent/90 transition-colors disabled:opacity-50"
          >
            {isLoading ? 'Saving...' : isEditMode ? 'Update' : 'Add'}
          </button>
        </div>
      </form>
    </Card>
  )
}
