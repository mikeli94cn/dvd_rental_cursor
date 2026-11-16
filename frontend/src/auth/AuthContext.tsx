import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useState,
  type ReactNode,
} from 'react'
import { authApi } from '../api/services'
import { clearCsrf, ensureCsrf } from '../api/client'
import type { User } from '../types/api'

interface AuthContextValue {
  user: User | null
  loading: boolean
  login: (email: string, password: string) => Promise<User>
  register: (payload: {
    email: string
    password: string
    firstName: string
    lastName: string
    phone?: string
  }) => Promise<User>
  logout: () => Promise<void>
  refresh: () => Promise<void>
  isStaff: boolean
  isAdmin: boolean
  isCustomer: boolean
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null)
  const [loading, setLoading] = useState(true)

  const refresh = useCallback(async () => {
    try {
      await ensureCsrf()
      const me = await authApi.me()
      setUser(me)
    } catch {
      setUser(null)
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    void refresh()
  }, [refresh])

  const login = useCallback(async (email: string, password: string) => {
    clearCsrf()
    await ensureCsrf()
    const me = await authApi.login({ email, password })
    setUser(me)
    return me
  }, [])

  const register = useCallback(
    async (payload: {
      email: string
      password: string
      firstName: string
      lastName: string
      phone?: string
    }) => {
      clearCsrf()
      await ensureCsrf()
      const me = await authApi.register(payload)
      const loggedIn = await authApi.login({ email: payload.email, password: payload.password })
      setUser(loggedIn)
      return me
    },
    [],
  )

  const logout = useCallback(async () => {
    await authApi.logout()
    clearCsrf()
    setUser(null)
  }, [])

  const value = useMemo<AuthContextValue>(
    () => ({
      user,
      loading,
      login,
      register,
      logout,
      refresh,
      isStaff: !!user?.roles.some((r) => r === 'STAFF' || r === 'ADMIN'),
      isAdmin: !!user?.roles.includes('ADMIN'),
      isCustomer: !!user?.roles.includes('CUSTOMER'),
    }),
    [user, loading, login, register, logout, refresh],
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) {
    throw new Error('useAuth must be used within AuthProvider')
  }
  return ctx
}
