import { Layout, Menu, Badge, Button, Space, Typography, theme } from 'antd'
import {
  HomeOutlined,
  VideoCameraOutlined,
  ShoppingCartOutlined,
  UserOutlined,
  LoginOutlined,
  DashboardOutlined,
  LogoutOutlined,
} from '@ant-design/icons'
import { Link, Outlet, useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext'
import { useCart } from '../cart/CartContext'

const { Header, Content, Footer } = Layout

export default function AppLayout() {
  const { user, logout, isStaff, isCustomer } = useAuth()
  const { items } = useCart()
  const location = useLocation()
  const navigate = useNavigate()
  const { token } = theme.useToken()

  const selected = location.pathname.startsWith('/admin')
    ? '/admin'
    : location.pathname.startsWith('/movies')
      ? '/movies'
      : location.pathname

  return (
    <Layout style={{ minHeight: '100vh', background: `linear-gradient(180deg, ${token.colorBgLayout} 0%, #e8eef5 100%)` }}>
      <Header style={{ display: 'flex', alignItems: 'center', gap: 24, background: '#0f2744' }}>
        <Typography.Title level={4} style={{ color: '#f5f7fa', margin: 0, fontFamily: 'Georgia, serif' }}>
          <Link to="/" style={{ color: 'inherit' }}>DVD Rental System</Link>
        </Typography.Title>
        <Menu
          theme="dark"
          mode="horizontal"
          selectedKeys={[selected]}
          style={{ flex: 1, background: 'transparent', minWidth: 0 }}
          items={[
            { key: '/', icon: <HomeOutlined />, label: <Link to="/">Home</Link> },
            { key: '/movies', icon: <VideoCameraOutlined />, label: <Link to="/movies">Catalog</Link> },
            ...(isCustomer
              ? [
                  {
                    key: '/cart',
                    icon: (
                      <Badge count={items.length} size="small">
                        <ShoppingCartOutlined />
                      </Badge>
                    ),
                    label: <Link to="/cart">Cart</Link>,
                  },
                  { key: '/my-rentals', icon: <UserOutlined />, label: <Link to="/my-rentals">My Rentals</Link> },
                ]
              : []),
            ...(isStaff
              ? [{ key: '/admin', icon: <DashboardOutlined />, label: <Link to="/admin">Staff</Link> }]
              : []),
          ]}
        />
        <Space>
          {user ? (
            <>
              <Typography.Text style={{ color: '#d9e2ec' }}>{user.email}</Typography.Text>
              {isCustomer && (
                <Button type="link" style={{ color: '#fff' }} onClick={() => navigate('/profile')}>
                  Profile
                </Button>
              )}
              <Button
                icon={<LogoutOutlined />}
                onClick={async () => {
                  await logout()
                  navigate('/')
                }}
              >
                Logout
              </Button>
            </>
          ) : (
            <>
              <Button type="primary" icon={<LoginOutlined />} onClick={() => navigate('/login')}>
                Login
              </Button>
              <Button onClick={() => navigate('/register')}>Register</Button>
            </>
          )}
        </Space>
      </Header>
      <Content style={{ padding: '24px 32px', maxWidth: 1200, width: '100%', margin: '0 auto' }}>
        <Outlet />
      </Content>
      <Footer style={{ textAlign: 'center', background: 'transparent' }}>
        Physical DVD pickup and return at the store counter.
      </Footer>
    </Layout>
  )
}
