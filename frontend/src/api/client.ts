import axios, { type AxiosError } from 'axios'

const api = axios.create({
  baseURL: '/api/v1',
  withCredentials: true,
  headers: { 'Content-Type': 'application/json' },
})

let csrfToken: string | null = null

export async function ensureCsrf(): Promise<string> {
  if (csrfToken) {
    return csrfToken
  }
  const { data } = await api.get<{ token: string; headerName: string }>('/auth/csrf')
  csrfToken = data.token
  return csrfToken
}

api.interceptors.request.use(async (config) => {
  const method = (config.method || 'get').toLowerCase()
  if (['post', 'put', 'patch', 'delete'].includes(method)) {
    const token = await ensureCsrf()
    config.headers['X-XSRF-TOKEN'] = token
  }
  return config
})

api.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    if (error.response?.status === 403) {
      csrfToken = null
    }
    return Promise.reject(error)
  },
)

export function problemMessage(error: unknown, fallback = 'Request failed'): string {
  const ax = error as AxiosError<{ detail?: string; title?: string }>
  return ax.response?.data?.detail || ax.response?.data?.title || ax.message || fallback
}

export function clearCsrf() {
  csrfToken = null
}

export default api
