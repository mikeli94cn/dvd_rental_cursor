import { Alert, Button, Form, Input, Typography } from 'antd'
import { Link, useNavigate } from 'react-router-dom'
import { useState } from 'react'
import { useAuth } from '../auth/AuthContext'
import { problemMessage } from '../api/client'

export default function RegisterPage() {
  const { register } = useAuth()
  const navigate = useNavigate()
  const [error, setError] = useState<string | null>(null)
  const [loading, setLoading] = useState(false)

  return (
    <div style={{ maxWidth: 480, margin: '40px auto' }}>
      <Typography.Title level={2}>Create customer account</Typography.Title>
      {error && <Alert type="error" message={error} style={{ marginBottom: 16 }} />}
      <Form
        layout="vertical"
        onFinish={async (values) => {
          setLoading(true)
          setError(null)
          try {
            await register(values)
            navigate('/movies')
          } catch (err) {
            setError(problemMessage(err, 'Registration failed'))
          } finally {
            setLoading(false)
          }
        }}
      >
        <Form.Item name="email" label="Email" rules={[{ required: true, type: 'email' }]}>
          <Input />
        </Form.Item>
        <Form.Item name="password" label="Password" rules={[{ required: true, min: 8 }]}>
          <Input.Password />
        </Form.Item>
        <Form.Item name="firstName" label="First name" rules={[{ required: true }]}>
          <Input />
        </Form.Item>
        <Form.Item name="lastName" label="Last name" rules={[{ required: true }]}>
          <Input />
        </Form.Item>
        <Form.Item name="phone" label="Phone">
          <Input />
        </Form.Item>
        <Button type="primary" htmlType="submit" loading={loading} block>
          Register
        </Button>
      </Form>
      <Typography.Paragraph style={{ marginTop: 16 }}>
        Already registered? <Link to="/login">Login</Link>
      </Typography.Paragraph>
    </div>
  )
}
