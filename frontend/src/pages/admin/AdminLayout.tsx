import { Layout, Menu, Typography } from 'antd'
import {
  DashboardOutlined,
  VideoCameraOutlined,
  DatabaseOutlined,
  ShoppingOutlined,
  RollbackOutlined,
  TeamOutlined,
  WarningOutlined,
} from '@ant-design/icons'
import { Link, Outlet, useLocation } from 'react-router-dom'

const { Sider, Content } = Layout

export default function AdminLayout() {
  const location = useLocation()
  return (
    <Layout style={{ background: 'transparent', minHeight: 560 }}>
      <Sider width={220} theme="light" style={{ borderRadius: 12, marginRight: 16 }}>
        <Typography.Title level={5} style={{ padding: '16px 16px 0' }}>
          Staff console
        </Typography.Title>
        <Menu
          mode="inline"
          selectedKeys={[location.pathname]}
          items={[
            { key: '/admin', icon: <DashboardOutlined />, label: <Link to="/admin">Dashboard</Link> },
            { key: '/admin/movies', icon: <VideoCameraOutlined />, label: <Link to="/admin/movies">Movies</Link> },
            { key: '/admin/inventory', icon: <DatabaseOutlined />, label: <Link to="/admin/inventory">Inventory</Link> },
            { key: '/admin/rentals', icon: <ShoppingOutlined />, label: <Link to="/admin/rentals">Rentals</Link> },
            { key: '/admin/returns', icon: <RollbackOutlined />, label: <Link to="/admin/returns">Returns</Link> },
            { key: '/admin/customers', icon: <TeamOutlined />, label: <Link to="/admin/customers">Customers</Link> },
            { key: '/admin/overdue', icon: <WarningOutlined />, label: <Link to="/admin/overdue">Overdue</Link> },
          ]}
        />
      </Sider>
      <Content>
        <Outlet />
      </Content>
    </Layout>
  )
}
