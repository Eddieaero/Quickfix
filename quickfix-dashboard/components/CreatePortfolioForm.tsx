'use client'

import { useState, useEffect } from 'react'
import { X } from 'lucide-react'
import { Card } from '@/components/ui/card'

interface CreatePortfolioFormProps {
  onSubmit: (data: { name: string; initialInvestment: number }) => void
  onCancel: () => void
  isLoading?: boolean
  initialData?: {
    name: string
    initialInvestment: number
  }
  isEditMode?: boolean
}

export function CreatePortfolioForm({
  onSubmit,
  onCancel,
  isLoading = false,
  initialData,
  isEditMode = false,
}: CreatePortfolioFormProps) {
  const [name, setName] = useState('')
  const [initialInvestment, setInitialInvestment] = useState('')
  const [errors, setErrors] = useState<Record<string, string>>({})

  useEffect(() => {
    if (initialData) {
      setName(initialData.name)
      setInitialInvestment(initialData.initialInvestment.toString())
    }
  }, [initialData])

  const validate = () => {
    const newErrors: Record<string, string> = {}

    if (!name.trim()) {
      newErrors.name = 'Portfolio name is required'
    }

    if (!initialInvestment) {
      newErrors.initialInvestment = 'Initial investment is required'
    } else if (isNaN(Number(initialInvestment)) || Number(initialInvestment) <= 0) {
      newErrors.initialInvestment = 'Initial investment must be a positive number'
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
      name: name.trim(),
      initialInvestment: Number(initialInvestment),
    })
  }

  return (
    <Card className="p-6 w-full max-w-md">
      <div className="flex justify-between items-center mb-6">
        <h2 className="text-2xl font-bold">
          {isEditMode ? 'Edit Portfolio' : 'Create Portfolio'}
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
          <label className="block text-sm font-medium mb-2">Portfolio Name</label>
          <input
            type="text"
            value={name}
            onChange={(e) => {
              setName(e.target.value)
              if (errors.name) {
                setErrors({ ...errors, name: '' })
              }
            }}
            className="w-full px-4 py-2 border border-border rounded-lg bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-accent focus:border-transparent"
            placeholder="e.g., Retirement Fund"
            disabled={isLoading}
          />
          {errors.name && <p className="text-xs text-red-500 mt-1">{errors.name}</p>}
        </div>

        <div>
          <label className="block text-sm font-medium mb-2">Initial Investment ($)</label>
          <input
            type="number"
            value={initialInvestment}
            onChange={(e) => {
              setInitialInvestment(e.target.value)
              if (errors.initialInvestment) {
                setErrors({ ...errors, initialInvestment: '' })
              }
            }}
            className="w-full px-4 py-2 border border-border rounded-lg bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-accent focus:border-transparent"
            placeholder="50000"
            step="0.01"
            disabled={isLoading}
          />
          {errors.initialInvestment && (
            <p className="text-xs text-red-500 mt-1">{errors.initialInvestment}</p>
          )}
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
            {isLoading ? 'Creating...' : isEditMode ? 'Update' : 'Create'}
          </button>
        </div>
      </form>
    </Card>
  )
}
