'use client'

import { useEffect, useState } from 'react'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Settings as SettingsIcon, Database, Shield, Bell } from 'lucide-react'

export default function SettingsPage() {
  const [isAuthenticated, setIsAuthenticated] = useState(false)
  const [mounted, setMounted] = useState(false)

  useEffect(() => {
    setMounted(true)
    const token = localStorage.getItem('token')
    const username = localStorage.getItem('username')

    if (!token || !username) {
      window.location.href = '/'
      return
    }

    setIsAuthenticated(true)
  }, [])

  if (!mounted || !isAuthenticated) {
    return <div className="flex items-center justify-center h-screen">Loading...</div>
  }

  return (
    <div className="min-h-screen bg-background p-6">
      {/* Page Title */}
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-foreground flex items-center gap-2">
          <SettingsIcon className="w-8 h-8" />
          Settings
        </h1>
        <p className="text-muted-foreground mt-2">Configure application preferences</p>
      </div>

      <div className="grid gap-6 grid-cols-1 lg:grid-cols-2">
        {/* Left Column */}
        <div className="space-y-6">
          {/* General Settings */}
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <SettingsIcon className="w-5 h-5" />
                General Settings
              </CardTitle>
              <CardDescription>Application general settings and preferences</CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-foreground mb-2">
                  Application Name
                </label>
                <input
                  type="text"
                  value="Aero QuickFIX"
                  disabled
                  className="w-full px-3 py-2 bg-input border border-border rounded text-muted-foreground"
                />
                <p className="text-xs text-muted-foreground mt-1">Version 1.0.0</p>
              </div>
              <div>
                <label className="block text-sm font-medium text-foreground mb-2">
                  Environment
                </label>
                <input
                  type="text"
                  value="Development"
                  disabled
                  className="w-full px-3 py-2 bg-input border border-border rounded text-muted-foreground"
                />
              </div>
            </CardContent>
          </Card>

          {/* Security Settings */}
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <Shield className="w-5 h-5" />
                Security Configuration
              </CardTitle>
              <CardDescription>Authentication and security settings</CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-foreground mb-2">
                  Authentication Method
                </label>
                <div className="px-3 py-2 bg-input border border-border rounded text-foreground">
                  JWT (JSON Web Tokens)
                </div>
              </div>
              <div>
                <label className="block text-sm font-medium text-foreground mb-2">
                  Token Expiration
                </label>
                <div className="px-3 py-2 bg-input border border-border rounded text-foreground">
                  24 Hours
                </div>
              </div>
              <div>
                <label className="block text-sm font-medium text-foreground mb-2">
                  CORS Enabled
                </label>
                <div className="px-3 py-2 bg-input border border-border rounded text-gray-300">
                  ✓ Enabled (localhost:3000, localhost:3001)
                </div>
              </div>
            </CardContent>
          </Card>
        </div>

        {/* Right Column */}
        <div className="space-y-6">
          {/* Database Settings */}
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <Database className="w-5 h-5" />
                Database Configuration
              </CardTitle>
              <CardDescription>Current database setup and storage</CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-foreground mb-2">
                  Database Type
                </label>
                <div className="px-3 py-2 bg-input border border-border rounded text-foreground">
                  In-Memory (ConcurrentHashMap)
                </div>
                <p className="text-xs text-muted-foreground mt-2">
                  ⚠️ Data is not persisted. Using in-memory storage for development.
                </p>
              </div>
              <div className="p-3 bg-gray-700/20 border border-gray-700/50 rounded-lg">
                <p className="text-sm text-gray-300">
                  💡 For production, consider switching to PostgreSQL, MySQL, or MongoDB
                </p>
              </div>
            </CardContent>
          </Card>

          {/* System Information */}
          <Card>
            <CardHeader>
              <CardTitle>System Information</CardTitle>
              <CardDescription>Backend and frontend details</CardDescription>
            </CardHeader>
            <CardContent className="space-y-3 text-sm">
              <div className="flex justify-between">
                <span className="text-muted-foreground">Backend Framework:</span>
                <span className="text-foreground font-mono">Spring Boot 3.2.0</span>
              </div>
              <div className="flex justify-between">
                <span className="text-muted-foreground">Backend Language:</span>
                <span className="text-foreground font-mono">Java 17</span>
              </div>
              <div className="flex justify-between">
                <span className="text-muted-foreground">Frontend Framework:</span>
                <span className="text-foreground font-mono">Next.js 13</span>
              </div>
              <div className="flex justify-between">
                <span className="text-muted-foreground">UI Framework:</span>
                <span className="text-foreground font-mono">React 18 + shadcn/ui</span>
              </div>
              <div className="flex justify-between">
                <span className="text-muted-foreground">FIX Protocol:</span>
                <span className="text-foreground font-mono">QuickFIX/J 2.3.1</span>
              </div>
              <div className="flex justify-between">
                <span className="text-muted-foreground">Real-time Protocol:</span>
                <span className="text-foreground font-mono">WebSocket</span>
              </div>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  )
}
