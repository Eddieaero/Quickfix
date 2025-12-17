'use client'

import { useState } from 'react'
import Link from 'next/link'
import Image from 'next/image'
import { usePathname } from 'next/navigation'
import {
  Users,
  LogOut,
  Menu,
  X,
  Activity,
  Settings,
  TrendingUp,
  Calculator,
  BarChart3,
} from 'lucide-react'

interface SidebarProps {
  username?: string
  onLogout?: () => void
}

export function Sidebar({ username, onLogout }: SidebarProps) {
  const [isOpen, setIsOpen] = useState(true)
  const pathname = usePathname()

  const navItems = [
    {
      name: 'Dashboard',
      href: '/',
      icon: Activity,
      description: 'Real-time trading view',
    },
    {
      name: 'Portfolios',
      href: '/investments',
      icon: TrendingUp,
      description: 'Manage investments',
    },
    {
      name: 'Market Data',
      href: '/market',
      icon: BarChart3,
      description: 'Live market prices',
    },
    {
      name: 'Compound Interest',
      href: '/compound-interest',
      icon: Calculator,
      description: 'Calculate returns',
    },
    {
      name: 'Users',
      href: '/admin',
      icon: Users,
      description: 'Manage users',
    },
    {
      name: 'Settings',
      href: '/settings',
      icon: Settings,
      description: 'Application settings',
    },
  ]

  const isActive = (href: string) => {
    if (href === '/') {
      return pathname === '/'
    }
    return pathname.startsWith(href)
  }

  return (
    <>
      {/* Sidebar */}
      <aside
        className={`fixed left-0 top-0 h-screen bg-card border-r border-border transition-all duration-300 z-40 ${
          isOpen ? 'w-64' : 'w-20'
        }`}
      >
        {/* Header */}
        <div className="flex items-center justify-between h-16 px-4 border-b border-border">
          {isOpen && (
            <div className="flex items-center gap-3">
              <Image
                src="/aero-logo.svg"
                alt="Aero Logo"
                width={32}
                height={32}
                className="w-8 h-8"
              />
              <span className="text-lg font-bold text-foreground">Aero</span>
            </div>
          )}
          {!isOpen && (
            <Image
              src="/aero-logo.svg"
              alt="Aero Logo"
              width={32}
              height={32}
              className="w-8 h-8"
            />
          )}
          <button
            onClick={() => setIsOpen(!isOpen)}
            className="p-2 hover:bg-background rounded-lg transition-colors"
            aria-label="Toggle sidebar"
          >
            {isOpen ? (
              <X className="w-5 h-5 text-muted-foreground" />
            ) : (
              <Menu className="w-5 h-5 text-muted-foreground" />
            )}
          </button>
        </div>

        {/* Navigation Items */}
        <nav className="flex-1 px-3 py-6 space-y-2 overflow-y-auto">
          {navItems.map((item) => {
            const Icon = item.icon
            const active = isActive(item.href)

            return (
              <Link
                key={item.href}
                href={item.href}
                className={`flex items-center gap-3 px-3 py-3 rounded-lg transition-all ${
                  active
                    ? 'bg-primary text-primary-foreground shadow-md'
                    : 'text-muted-foreground hover:bg-background hover:text-foreground'
                }`}
                title={!isOpen ? item.name : undefined}
              >
                <Icon className="w-5 h-5 flex-shrink-0" />
                {isOpen && (
                  <div className="flex-1">
                    <div className="font-medium text-sm">{item.name}</div>
                    <div className="text-xs opacity-75">{item.description}</div>
                  </div>
                )}
              </Link>
            )
          })}
        </nav>

        {/* User Section */}
        <div className="border-t border-border p-4 space-y-3">
          {username && isOpen && (
            <div className="px-3 py-2 bg-background rounded-lg">
              <p className="text-xs text-muted-foreground">Logged in as</p>
              <p className="text-sm font-semibold text-foreground truncate">
                {username}
              </p>
            </div>
          )}
          {isOpen && (
            <button
              onClick={onLogout}
              className="w-full flex items-center gap-2 px-3 py-2 text-sm text-destructive hover:bg-destructive/10 rounded-lg transition-colors"
            >
              <LogOut className="w-4 h-4" />
              Logout
            </button>
          )}
          {!isOpen && (
            <button
              onClick={onLogout}
              className="w-full flex items-center justify-center px-3 py-2 text-destructive hover:bg-destructive/10 rounded-lg transition-colors"
              title="Logout"
            >
              <LogOut className="w-4 h-4" />
            </button>
          )}
        </div>
      </aside>

      {/* Main content spacer */}
      <div className={`transition-all duration-300 ${isOpen ? 'ml-64' : 'ml-20'}`} />
    </>
  )
}
