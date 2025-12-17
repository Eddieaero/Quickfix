'use client'

import { useState, useEffect } from 'react'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Users, Trash2, Power, CheckCircle, XCircle, Plus } from 'lucide-react'
import axios from 'axios'

interface User {
  userId: string
  username: string
  email: string
  enabled: boolean
  createdAt: number
}

interface ManagementResponse {
  success: boolean
  message?: string
  users?: User[]
  user?: User
}

export default function UserManagementPage() {
  const [users, setUsers] = useState<User[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const [isAuthenticated, setIsAuthenticated] = useState(false)

  // Form state for new user
  const [showNewUserForm, setShowNewUserForm] = useState(false)
  const [newUsername, setNewUsername] = useState('')
  const [newEmail, setNewEmail] = useState('')
  const [newPassword, setNewPassword] = useState('')

  // Check authentication
  useEffect(() => {
    const token = localStorage.getItem('token')
    const username = localStorage.getItem('username')
    
    if (!token || !username) {
      window.location.href = '/'
      return
    }

    setIsAuthenticated(true)
    loadUsers()
  }, [])

  // Load all users
  const loadUsers = async () => {
    try {
      setLoading(true)
      const response = await axios.get<ManagementResponse>(
        'http://localhost:8080/api/users'
      )
      
      if (response.data.success && response.data.users) {
        setUsers(response.data.users)
        setError('')
      }
    } catch (err) {
      setError('Failed to load users')
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  // Create new user
  const handleCreateUser = async (e: React.FormEvent) => {
    e.preventDefault()
    
    if (!newUsername || !newEmail || !newPassword) {
      setError('All fields are required')
      return
    }

    try {
      setLoading(true)
      const response = await axios.post<ManagementResponse>(
        'http://localhost:8080/api/auth/register',
        {
          username: newUsername,
          password: newPassword,
          email: newEmail
        }
      )

      if (response.data.success) {
        setSuccess('User created successfully')
        setNewUsername('')
        setNewEmail('')
        setNewPassword('')
        setShowNewUserForm(false)
        loadUsers()
        setTimeout(() => setSuccess(''), 3000)
      }
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to create user')
    } finally {
      setLoading(false)
    }
  }

  // Delete user
  const handleDeleteUser = async (username: string) => {
    if (username === 'admin') {
      setError('Cannot delete admin user')
      return
    }

    if (!confirm(`Are you sure you want to delete user "${username}"?`)) {
      return
    }

    try {
      setLoading(true)
      await axios.delete(`http://localhost:8080/api/users/${username}`)
      setSuccess(`User "${username}" deleted`)
      loadUsers()
      setTimeout(() => setSuccess(''), 3000)
    } catch (err) {
      setError(`Failed to delete user "${username}"`)
    } finally {
      setLoading(false)
    }
  }

  // Toggle user status
  const handleToggleStatus = async (username: string, currentStatus: boolean) => {
    if (username === 'admin' && currentStatus) {
      setError('Cannot disable admin user')
      return
    }

    try {
      setLoading(true)
      await axios.patch(
        `http://localhost:8080/api/users/${username}/toggle-status`,
        {},
        { params: { enabled: !currentStatus } }
      )
      setSuccess(`User "${username}" status updated`)
      loadUsers()
      setTimeout(() => setSuccess(''), 3000)
    } catch (err) {
      setError(`Failed to toggle user status`)
    } finally {
      setLoading(false)
    }
  }

  if (!isAuthenticated) {
    return <div className="flex items-center justify-center h-screen">Loading...</div>
  }

  return (
    <div className="min-h-screen bg-background p-6">
      {/* Page Title */}
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-foreground flex items-center gap-2">
          <Users className="w-8 h-8" />
          User Management
        </h1>
        <p className="text-muted-foreground mt-2">Manage system users and permissions</p>
      </div>

      {/* Messages */}
      {error && (
        <Card className="mb-6 border-red-900/50 bg-red-900/10">
          <CardContent className="pt-6 text-red-200">{error}</CardContent>
        </Card>
      )}
      {success && (
        <Card className="mb-6 border-green-900/50 bg-green-900/10">
          <CardContent className="pt-6 text-green-200">{success}</CardContent>
        </Card>
      )}

      {/* Create New User */}
      <Card className="mb-6">
        <CardHeader>
          <CardTitle>Create New User</CardTitle>
          <CardDescription>Add a new user to the system</CardDescription>
        </CardHeader>
        <CardContent>
          {!showNewUserForm ? (
            <Button onClick={() => setShowNewUserForm(true)} className="flex items-center gap-2">
              <Plus className="w-4 h-4" />
              New User
            </Button>
          ) : (
            <form onSubmit={handleCreateUser} className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-foreground mb-1">Username</label>
                <input
                  type="text"
                  value={newUsername}
                  onChange={(e) => setNewUsername(e.target.value)}
                  className="w-full px-3 py-2 bg-input border border-border rounded text-foreground"
                  placeholder="Enter username"
                  disabled={loading}
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-foreground mb-1">Email</label>
                <input
                  type="email"
                  value={newEmail}
                  onChange={(e) => setNewEmail(e.target.value)}
                  className="w-full px-3 py-2 bg-input border border-border rounded text-foreground"
                  placeholder="Enter email"
                  disabled={loading}
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-foreground mb-1">Password</label>
                <input
                  type="password"
                  value={newPassword}
                  onChange={(e) => setNewPassword(e.target.value)}
                  className="w-full px-3 py-2 bg-input border border-border rounded text-foreground"
                  placeholder="Enter password"
                  disabled={loading}
                />
              </div>
              <div className="flex gap-2">
                <Button type="submit" disabled={loading}>
                  {loading ? 'Creating...' : 'Create User'}
                </Button>
                <Button
                  type="button"
                  variant="outline"
                  onClick={() => setShowNewUserForm(false)}
                  disabled={loading}
                >
                  Cancel
                </Button>
              </div>
            </form>
          )}
        </CardContent>
      </Card>

      {/* Users List */}
      <Card>
        <CardHeader>
          <CardTitle>Users ({users.length})</CardTitle>
          <CardDescription>Manage system users and permissions</CardDescription>
        </CardHeader>
        <CardContent>
          {loading && users.length === 0 ? (
            <p className="text-muted-foreground">Loading...</p>
          ) : users.length === 0 ? (
            <p className="text-muted-foreground">No users found</p>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead>
                  <tr className="border-b border-border">
                    <th className="text-left py-3 px-4 text-foreground font-medium">Username</th>
                    <th className="text-left py-3 px-4 text-foreground font-medium">Email</th>
                    <th className="text-left py-3 px-4 text-foreground font-medium">Status</th>
                    <th className="text-left py-3 px-4 text-foreground font-medium">Created</th>
                    <th className="text-left py-3 px-4 text-foreground font-medium">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {users.map((user) => (
                    <tr key={user.userId} className="border-b border-border/50 hover:bg-card/50">
                      <td className="py-3 px-4 text-foreground font-mono">{user.username}</td>
                      <td className="py-3 px-4 text-muted-foreground">{user.email}</td>
                      <td className="py-3 px-4">
                        {user.enabled ? (
                          <span className="flex items-center gap-1 text-green-200">
                            <CheckCircle className="w-4 h-4" />
                            Enabled
                          </span>
                        ) : (
                          <span className="flex items-center gap-1 text-red-200">
                            <XCircle className="w-4 h-4" />
                            Disabled
                          </span>
                        )}
                      </td>
                      <td className="py-3 px-4 text-muted-foreground text-sm">
                        {new Date(user.createdAt).toLocaleDateString()}
                      </td>
                      <td className="py-3 px-4">
                        <div className="flex gap-2">
                          <Button
                            size="sm"
                            variant="outline"
                            onClick={() => handleToggleStatus(user.username, user.enabled)}
                            disabled={loading}
                            className="flex items-center gap-1"
                          >
                            <Power className="w-3 h-3" />
                            {user.enabled ? 'Disable' : 'Enable'}
                          </Button>
                          <Button
                            size="sm"
                            variant="destructive"
                            onClick={() => handleDeleteUser(user.username)}
                            disabled={loading || user.username === 'admin'}
                            className="flex items-center gap-1"
                          >
                            <Trash2 className="w-3 h-3" />
                            Delete
                          </Button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  )
}
