export interface User {
  id: number
  email: string
  roles: string[]
  customerId: number | null
  firstName: string | null
  lastName: string | null
  phone: string | null
}

export interface Genre {
  id: number
  name: string
}

export interface Movie {
  id: number
  title: string
  description: string | null
  releaseYear: number | null
  dailyRate: number
  rentalDaysDefault: number
  genres: Genre[]
  availableCopies: number
}

export interface DvdCopy {
  id: number
  movieId: number
  movieTitle: string
  barcode: string
  status: string
  notes: string | null
}

export interface RentalItem {
  id: number
  dvdCopyId: number
  barcode: string
  movieId: number
  movieTitle: string
  dueDate: string
  returnedAt: string | null
  lineAmount: number
  overdueAmount: number
  status: string
}

export interface RentalOrder {
  id: number
  customerId: number
  customerEmail: string
  customerName: string
  status: string
  totalAmount: number
  createdAt: string
  items: RentalItem[]
}

export interface Customer {
  id: number
  userId: number
  email: string
  firstName: string
  lastName: string
  phone: string | null
}

export interface DashboardStats {
  totalMovies: number
  totalCopies: number
  availableCopies: number
  activeRentals: number
  overdueItems: number
}

export interface OverdueItem {
  rentalItemId: number
  rentalOrderId: number
  customerEmail: string
  customerName: string
  movieTitle: string
  barcode: string
  dueDate: string
  overdueDays: number
  estimatedOverdueCharge: number
}

export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  number: number
  size: number
}

export interface CartItem {
  movie: Movie
  dvdCopyId: number
  barcode: string
}
