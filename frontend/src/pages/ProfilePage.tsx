import { useEffect, useState } from 'react'
import { Alert, Button, Form, Input, Spin, Typography, message } from 'antd'
import { problemMessage } from '../api/client'
import { customerApi } from '../api/services'

export default function ProfilePage() {
  const [form] = Form.useForm()
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    customerApi
      .me()
      .then((profile) => form.setFieldsValue(profile))
      .catch((err) => setError(problemMessage(err)))
      .finally(() => setLoading(false))
  }, [form])

  if (loading) return <Spin />

  return (
    <div style={{ maxWidth: 480 }}>
      <Typography.Title level={2}>Account profile</Typography.Title>
      {error && <Alert type="error" message={error} style={{ marginBottom: 16 }} />}
      <Form
        form={form}
        layout="vertical"
        onFinish={async (values) => {
          setSaving(true)
          try {
            await customerApi.updateMe(values)
            message.success('Profile updated')
          } catch (err) {
            setError(problemMessage(err))
          } finally {
            setSaving(false)
          }
        }}
      >
        <Form.Item name="email" label="Email">
          <Input disabled />
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
        <Button type="primary" htmlType="submit" loading={saving}>
          Save
        </Button>
      </Form>
    </div>
  )
}
