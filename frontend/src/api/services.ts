import api from './client'
import type {
  Customer,
  DashboardStats,
  DvdCopy,
  Genre,
  Movie,
  OverdueItem,
  PageResponse,
  RentalOrder,
  User,
} from '../types/api'

export const authApi = {
  register: (body: {
    email: string
    password: string
    firstName: string
    lastName: string
    phone?: string
  }) => api.post<User>('/auth/register', body).then((r) => r.data),
  login: (body: { email: string; password: string }) =>
    api.post<User>('/auth/login', body).then((r) => r.data),
  logout: () => api.post('/auth/logout'),
  me: () => api.get<User>('/auth/me').then((r) => r.data),
}

export const catalogApi = {
  movies: (params?: { q?: string; genreId?: number; page?: number; size?: number }) =>
    api.get<PageResponse<Movie>>('/movies', { params }).then((r) => r.data),
  movie: (id: number) => api.get<Movie>(`/movies/${id}`).then((r) => r.data),
  createMovie: (body: unknown) => api.post<Movie>('/movies', body).then((r) => r.data),
  updateMovie: (id: number, body: unknown) => api.put<Movie>(`/movies/${id}`, body).then((r) => r.data),
  deleteMovie: (id: number) => api.delete(`/movies/${id}`),
  genres: () => api.get<Genre[]>('/genres').then((r) => r.data),
  createGenre: (name: string) => api.post<Genre>('/genres', { name }).then((r) => r.data),
  availability: (movieId: number) =>
    api.get<PageResponse<DvdCopy>>(`/movies/${movieId}/availability`).then((r) => r.data),
}

export const rentalApi = {
  create: (dvdCopyIds: number[], rentalDays?: number) =>
    api.post<RentalOrder>('/rentals', { dvdCopyIds, rentalDays }).then((r) => r.data),
  mine: (page = 0) =>
    api.get<PageResponse<RentalOrder>>('/rentals/me', { params: { page, size: 20 } }).then((r) => r.data),
  get: (id: number) => api.get<RentalOrder>(`/rentals/${id}`).then((r) => r.data),
  staffList: (params?: { customerId?: number; status?: string; page?: number }) =>
    api.get<PageResponse<RentalOrder>>('/staff/rentals', { params }).then((r) => r.data),
  confirmReturn: (body: { rentalItemId: number; inspectionResult: string; notes?: string }) =>
    api.post<RentalOrder>('/staff/returns', body).then((r) => r.data),
}

export const inventoryApi = {
  search: (params?: { movieId?: number; status?: string; barcode?: string; page?: number; size?: number }) =>
    api.get<PageResponse<DvdCopy>>('/staff/inventory', { params: { size: 100, ...params } }).then((r) => r.data),
  create: (body: { movieId: number; barcode: string; notes?: string }) =>
    api.post<DvdCopy>('/staff/inventory', body).then((r) => r.data),
  updateStatus: (id: number, status: string, notes?: string) =>
    api.put<DvdCopy>(`/staff/inventory/${id}/status`, { status, notes }).then((r) => r.data),
}

export const customerApi = {
  me: () => api.get<Customer>('/customers/me').then((r) => r.data),
  updateMe: (body: { firstName: string; lastName: string; phone?: string }) =>
    api.put<Customer>('/customers/me', body).then((r) => r.data),
  list: (page = 0) =>
    api.get<PageResponse<Customer>>('/staff/customers', { params: { page, size: 20 } }).then((r) => r.data),
}

export const reportApi = {
  dashboard: () => api.get<DashboardStats>('/staff/reports/dashboard').then((r) => r.data),
  overdue: (page = 0) =>
    api.get<PageResponse<OverdueItem>>('/staff/reports/overdue', { params: { page, size: 20 } }).then((r) => r.data),
}
