'use client'

import { useEffect, useState } from 'react'
import { usePathname } from 'next/navigation'
import { Sidebar } from '@/components/Sidebar'

interface LayoutWrapperProps {
  children: React.ReactNode
}

export function LayoutWrapper({ children }: LayoutWrapperProps) {
  const [isAuthenticated, setIsAuthenticated] = useState(false)
  const [username, setUsername] = useState('')
  const [mounted, setMounted] = useState(false)
  const pathname = usePathname()

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

  // Only show sidebar on authenticated routes (dashboard and sub-routes)
  const isAuthenticatedRoute = pathname.startsWith('/dashboard') || 
                               pathname.startsWith('/investments') ||
                               pathname.startsWith('/market') ||
                               pathname.startsWith('/compound-interest') ||
                               pathname.startsWith('/admin') ||
                               pathname.startsWith('/settings')

  if (!isAuthenticated || !isAuthenticatedRoute) {
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
