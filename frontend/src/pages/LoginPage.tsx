import { Alert, Button, Form, Input, Typography } from 'antd'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { useState } from 'react'
import { useAuth } from '../auth/AuthContext'
import { problemMessage } from '../api/client'

export default function LoginPage() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  const [error, setError] = useState<string | null>(null)
  const [loading, setLoading] = useState(false)

  return (
    <div style={{ maxWidth: 420, margin: '40px auto' }}>
      <Typography.Title level={2}>Login</Typography.Title>
      {error && <Alert type="error" message={error} style={{ marginBottom: 16 }} />}
      <Form
        layout="vertical"
        onFinish={async (values) => {
          setLoading(true)
          setError(null)
          try {
            const user = await login(values.email, values.password)
            const from = (location.state as { from?: string } | null)?.from
            if (from) {
              navigate(from)
            } else if (user.roles.includes('STAFF') || user.roles.includes('ADMIN')) {
              navigate('/admin')
            } else {
              navigate('/movies')
            }
          } catch (err) {
            setError(problemMessage(err, 'Login failed'))
          } finally {
            setLoading(false)
          }
        }}
      >
        <Form.Item name="email" label="Email" rules={[{ required: true, type: 'email' }]}>
          <Input />
        </Form.Item>
        <Form.Item name="password" label="Password" rules={[{ required: true }]}>
          <Input.Password />
        </Form.Item>
        <Button type="primary" htmlType="submit" loading={loading} block>
          Sign in
        </Button>
      </Form>
      <Typography.Paragraph style={{ marginTop: 16 }}>
        No account? <Link to="/register">Register</Link>
      </Typography.Paragraph>
    </div>
  )
}
