'use client'

import { useState } from 'react'
import Image from 'next/image'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Lock, User } from 'lucide-react'
import axios from 'axios'

interface LoginPageProps {
  onLoginSuccess: (token: string, username: string) => void
}

export default function LoginPage({ onLoginSuccess }: LoginPageProps) {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState('')
  const [isRegister, setIsRegister] = useState(false)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    setIsLoading(true)

    try {
      const endpoint = isRegister ? '/api/auth/register' : '/api/auth/login'
      const response = await axios.post(endpoint, {
        username,
        password,
      })

      if (response.data.success) {
        localStorage.setItem('token', response.data.token)
        localStorage.setItem('username', response.data.username)
        onLoginSuccess(response.data.token, response.data.username)
      } else {
        setError(response.data.message || 'Authentication failed')
      }
    } catch (err) {
      setError('Connection failed. Make sure the server is running.')
      console.error('Auth error:', err)
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <div className="min-h-screen bg-background flex items-center justify-center p-4">
      <Card className="w-full max-w-md">
        <CardHeader className="space-y-4 text-center">
          <div className="flex justify-center mb-2">
            <Image
              src="/aero-logo.svg"
              alt="Aero Logo"
              width={48}
              height={48}
              className="w-12 h-12"
            />
          </div>
          <div className="space-y-2">
            <CardTitle className="text-2xl font-bold">Aero QuickFix</CardTitle>
            <CardDescription>
              {isRegister ? 'Create a new account' : 'Sign in to your account'}
            </CardDescription>
          </div>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-4">
            {/* Username Input */}
            <div className="space-y-2">
              <label className="text-sm font-medium text-foreground">Username</label>
              <div className="relative">
                <User className="absolute left-3 top-3 w-4 h-4 text-muted-foreground" />
                <input
                  type="text"
                  placeholder="Enter username"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  disabled={isLoading}
                  className="w-full pl-10 pr-4 py-2 border border-border rounded-md bg-background text-foreground placeholder-muted-foreground focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent disabled:opacity-50"
                  required
                />
              </div>
            </div>

            {/* Password Input */}
            <div className="space-y-2">
              <label className="text-sm font-medium text-foreground">Password</label>
              <div className="relative">
                <Lock className="absolute left-3 top-3 w-4 h-4 text-muted-foreground" />
                <input
                  type="password"
                  placeholder="Enter password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  disabled={isLoading}
                  className="w-full pl-10 pr-4 py-2 border border-border rounded-md bg-background text-foreground placeholder-muted-foreground focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent disabled:opacity-50"
                  required
                  minLength={6}
                />
              </div>
            </div>

            {/* Error Message */}
            {error && (
              <div className="p-3 rounded-md bg-red-900/20 border border-red-800/40 text-red-200 text-sm">
                {error}
              </div>
            )}

            {/* Submit Button */}
            <Button
              type="submit"
              disabled={isLoading}
              className="w-full"
            >
              {isLoading ? 'Loading...' : isRegister ? 'Create Account' : 'Sign In'}
            </Button>

            {/* Toggle Register/Login */}
            <div className="text-center text-sm text-muted-foreground">
              {isRegister ? 'Already have an account?' : 'Don\'t have an account?'}{' '}
              <button
                type="button"
                onClick={() => {
                  setIsRegister(!isRegister)
                  setError('')
                }}
                disabled={isLoading}
                className="text-primary hover:underline font-medium disabled:opacity-50"
              >
                {isRegister ? 'Sign In' : 'Create Account'}
              </button>
            </div>
          </form>
        </CardContent>
      </Card>
    </div>
  )
}
