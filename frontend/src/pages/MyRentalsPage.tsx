import { useEffect, useState } from 'react'
import { Alert, Empty, Spin, Table, Tag, Typography } from 'antd'
import { Link } from 'react-router-dom'
import { problemMessage } from '../api/client'
import { rentalApi } from '../api/services'
import type { RentalOrder } from '../types/api'

export default function MyRentalsPage() {
  const [orders, setOrders] = useState<RentalOrder[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    rentalApi
      .mine()
      .then((page) => setOrders(page.content))
      .catch((err) => setError(problemMessage(err)))
      .finally(() => setLoading(false))
  }, [])

  if (loading) return <Spin />
  if (error) return <Alert type="error" message={error} />
  if (orders.length === 0) return <Empty description="No rentals yet" />

  return (
    <>
      <Typography.Title level={2}>My rentals</Typography.Title>
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
                { title: 'Movie', dataIndex: 'movieTitle' },
                { title: 'Barcode', dataIndex: 'barcode' },
                { title: 'Due', dataIndex: 'dueDate' },
                {
                  title: 'Status',
                  dataIndex: 'status',
                  render: (s: string) => <Tag color={s === 'OVERDUE' ? 'red' : 'blue'}>{s}</Tag>,
                },
                {
                  title: 'Line',
                  dataIndex: 'lineAmount',
                  render: (v: number) => `$${Number(v).toFixed(2)}`,
                },
                {
                  title: 'Overdue',
                  dataIndex: 'overdueAmount',
                  render: (v: number) => `$${Number(v).toFixed(2)}`,
                },
              ]}
            />
          ),
        }}
        columns={[
          {
            title: 'Order',
            dataIndex: 'id',
            render: (id: number) => <Link to={`/rentals/${id}/confirm`}>#{id}</Link>,
          },
          { title: 'Status', dataIndex: 'status' },
          {
            title: 'Total',
            dataIndex: 'totalAmount',
            render: (v: number) => `$${Number(v).toFixed(2)}`,
          },
          {
            title: 'Created',
            dataIndex: 'createdAt',
            render: (v: string) => new Date(v).toLocaleString(),
          },
        ]}
      />
    </>
  )
}
