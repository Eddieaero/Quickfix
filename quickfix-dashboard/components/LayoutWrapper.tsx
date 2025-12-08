'use client'

import { useEffect, useState } from 'react'
import { Sidebar } from '@/components/Sidebar'

interface LayoutWrapperProps {
  children: React.ReactNode
}

export function LayoutWrapper({ children }: LayoutWrapperProps) {
  const [isAuthenticated, setIsAuthenticated] = useState(false)
  const [username, setUsername] = useState('')
  const [mounted, setMounted] = useState(false)

  useEffect(() => {
    setMounted(true)
    const token = localStorage.getItem('token')
    const user = localStorage.getItem('username')

    if (token && user) {
      setIsAuthenticated(true)
      setUsername(user)
    }
  }, [])

  const handleLogout = () => {
    localStorage.removeItem('token')
    localStorage.removeItem('username')
    window.location.href = '/'
  }

  if (!mounted) {
    return <div className="min-h-screen bg-background" />
  }

  if (!isAuthenticated) {
    return children
  }

  return (
    <div className="flex">
      <Sidebar username={username} onLogout={handleLogout} />
      <main className="flex-1 min-h-screen bg-background">
        {children}
      </main>
    </div>
  )
}
