import { useEffect, useState } from 'react'
import { Alert, Spin, Table, Typography } from 'antd'
import { problemMessage } from '../../api/client'
import { customerApi } from '../../api/services'
import type { Customer } from '../../types/api'

export default function CustomersAdminPage() {
  const [customers, setCustomers] = useState<Customer[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    customerApi
      .list()
      .then((page) => setCustomers(page.content))
      .catch((err) => setError(problemMessage(err)))
      .finally(() => setLoading(false))
  }, [])

  if (loading) return <Spin />
  if (error) return <Alert type="error" message={error} />

  return (
    <>
      <Typography.Title level={2}>Customers</Typography.Title>
      <Table
        rowKey="id"
        dataSource={customers}
        columns={[
          { title: 'ID', dataIndex: 'id', width: 80 },
          { title: 'Name', render: (_, r) => `${r.firstName} ${r.lastName}` },
          { title: 'Email', dataIndex: 'email' },
          { title: 'Phone', dataIndex: 'phone' },
        ]}
      />
    </>
  )
}
