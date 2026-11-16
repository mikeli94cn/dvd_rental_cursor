import { useEffect, useState } from 'react'
import { Alert, Select, Space, Spin, Table, Typography } from 'antd'
import { problemMessage } from '../../api/client'
import { rentalApi } from '../../api/services'
import type { RentalOrder } from '../../types/api'

export default function RentalsAdminPage() {
  const [orders, setOrders] = useState<RentalOrder[]>([])
  const [status, setStatus] = useState<string | undefined>('ACTIVE')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    setLoading(true)
    rentalApi
      .staffList({ status, page: 0 })
      .then((page) => setOrders(page.content))
      .catch((err) => setError(problemMessage(err)))
      .finally(() => setLoading(false))
  }, [status])

  return (
    <Space direction="vertical" style={{ width: '100%' }} size="large">
      <Typography.Title level={2}>Rentals</Typography.Title>
      <Select
        allowClear
        placeholder="Status"
        style={{ width: 200 }}
        value={status}
        onChange={setStatus}
        options={['ACTIVE', 'COMPLETED', 'CANCELLED', 'PENDING'].map((s) => ({ value: s, label: s }))}
      />
      {error && <Alert type="error" message={error} />}
      {loading ? (
        <Spin />
      ) : (
        <Table
          rowKey="id"
          dataSource={orders}
          expandable={{
            expandedRowRender: (order) => (
              <Table
                rowKey="id"
                pagination={false}
                dataSource={order.items}
                columns={[
                  { title: 'Item', dataIndex: 'id' },
                  { title: 'Movie', dataIndex: 'movieTitle' },
                  { title: 'Barcode', dataIndex: 'barcode' },
                  { title: 'Due', dataIndex: 'dueDate' },
                  { title: 'Status', dataIndex: 'status' },
                ]}
              />
            ),
          }}
          columns={[
            { title: 'Order', dataIndex: 'id' },
            { title: 'Customer', dataIndex: 'customerName' },
            { title: 'Email', dataIndex: 'customerEmail' },
            { title: 'Status', dataIndex: 'status' },
            {
              title: 'Total',
              dataIndex: 'totalAmount',
              render: (v: number) => `$${Number(v).toFixed(2)}`,
            },
          ]}
        />
      )}
    </Space>
  )
}
