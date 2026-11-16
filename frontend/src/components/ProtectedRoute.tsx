import { Navigate, useLocation } from 'react-router-dom'
import { Spin } from 'antd'
import { useAuth } from '../auth/AuthContext'

export function RequireAuth({ children }: { children: React.ReactNode }) {
  const { user, loading } = useAuth()
  const location = useLocation()
  if (loading) {
    return <Spin style={{ display: 'block', margin: '80px auto' }} />
  }
  if (!user) {
    return <Navigate to="/login" replace state={{ from: location.pathname }} />
  }
  return <>{children}</>
}

export function RequireStaff({ children }: { children: React.ReactNode }) {
  const { isStaff, loading, user } = useAuth()
  if (loading) {
    return <Spin style={{ display: 'block', margin: '80px auto' }} />
  }
  if (!user) {
    return <Navigate to="/login" replace />
  }
  if (!isStaff) {
    return <Navigate to="/" replace />
  }
  return <>{children}</>
}

export function RequireCustomer({ children }: { children: React.ReactNode }) {
  const { isCustomer, loading, user } = useAuth()
  if (loading) {
    return <Spin style={{ display: 'block', margin: '80px auto' }} />
  }
  if (!user) {
    return <Navigate to="/login" replace />
  }
  if (!isCustomer) {
    return <Navigate to="/" replace />
  }
  return <>{children}</>
}
