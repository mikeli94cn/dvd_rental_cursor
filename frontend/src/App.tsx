import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import { ConfigProvider } from 'antd'
import { AuthProvider } from './auth/AuthContext'
import { CartProvider } from './cart/CartContext'
import AppLayout from './components/AppLayout'
import { RequireAuth, RequireCustomer, RequireStaff } from './components/ProtectedRoute'
import HomePage from './pages/HomePage'
import CatalogPage from './pages/CatalogPage'
import MovieDetailPage from './pages/MovieDetailPage'
import LoginPage from './pages/LoginPage'
import RegisterPage from './pages/RegisterPage'
import CartPage from './pages/CartPage'
import RentalConfirmPage from './pages/RentalConfirmPage'
import MyRentalsPage from './pages/MyRentalsPage'
import ProfilePage from './pages/ProfilePage'
import AdminLayout from './pages/admin/AdminLayout'
import DashboardPage from './pages/admin/DashboardPage'
import MoviesAdminPage from './pages/admin/MoviesAdminPage'
import InventoryAdminPage from './pages/admin/InventoryAdminPage'
import RentalsAdminPage from './pages/admin/RentalsAdminPage'
import ReturnsAdminPage from './pages/admin/ReturnsAdminPage'
import CustomersAdminPage from './pages/admin/CustomersAdminPage'
import OverdueAdminPage from './pages/admin/OverdueAdminPage'

export default function App() {
  return (
    <ConfigProvider
      theme={{
        token: {
          colorPrimary: '#c45c26',
          colorLink: '#0f2744',
          borderRadius: 8,
          fontFamily: '"Source Sans 3", "Segoe UI", sans-serif',
        },
      }}
    >
      <AuthProvider>
        <CartProvider>
          <BrowserRouter>
            <Routes>
              <Route element={<AppLayout />}>
                <Route index element={<HomePage />} />
                <Route path="movies" element={<CatalogPage />} />
                <Route path="movies/:id" element={<MovieDetailPage />} />
                <Route path="login" element={<LoginPage />} />
                <Route path="register" element={<RegisterPage />} />
                <Route
                  path="cart"
                  element={
                    <RequireCustomer>
                      <CartPage />
                    </RequireCustomer>
                  }
                />
                <Route
                  path="rentals/:id/confirm"
                  element={
                    <RequireAuth>
                      <RentalConfirmPage />
                    </RequireAuth>
                  }
                />
                <Route
                  path="my-rentals"
                  element={
                    <RequireCustomer>
                      <MyRentalsPage />
                    </RequireCustomer>
                  }
                />
                <Route
                  path="profile"
                  element={
                    <RequireCustomer>
                      <ProfilePage />
                    </RequireCustomer>
                  }
                />
                <Route
                  path="admin"
                  element={
                    <RequireStaff>
                      <AdminLayout />
                    </RequireStaff>
                  }
                >
                  <Route index element={<DashboardPage />} />
                  <Route path="movies" element={<MoviesAdminPage />} />
                  <Route path="inventory" element={<InventoryAdminPage />} />
                  <Route path="rentals" element={<RentalsAdminPage />} />
                  <Route path="returns" element={<ReturnsAdminPage />} />
                  <Route path="customers" element={<CustomersAdminPage />} />
                  <Route path="overdue" element={<OverdueAdminPage />} />
                </Route>
                <Route path="*" element={<Navigate to="/" replace />} />
              </Route>
            </Routes>
          </BrowserRouter>
        </CartProvider>
      </AuthProvider>
    </ConfigProvider>
  )
}
