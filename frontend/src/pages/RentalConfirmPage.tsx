import { useEffect, useState } from 'react'
import { Alert, Descriptions, Spin, Table, Typography } from 'antd'
import { useParams } from 'react-router-dom'
import { problemMessage } from '../api/client'
import { rentalApi } from '../api/services'
import type { RentalOrder } from '../types/api'

export default function RentalConfirmPage() {
  const { id } = useParams()
  const [order, setOrder] = useState<RentalOrder | null>(null)
  const [error, setError] = useState<string | null>(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    rentalApi
      .get(Number(id))
      .then(setOrder)
      .catch((err) => setError(problemMessage(err)))
      .finally(() => setLoading(false))
  }, [id])

  if (loading) return <Spin />
  if (error) return <Alert type="error" message={error} />
  if (!order) return null

  return (
    <>
      <Typography.Title level={2}>Rental confirmed</Typography.Title>
      <Typography.Paragraph>
        Pick up these discs at the store counter. Keep this confirmation for your records.
      </Typography.Paragraph>
      <Descriptions bordered size="small" column={1} style={{ marginBottom: 24 }}>
        <Descriptions.Item label="Order">{order.id}</Descriptions.Item>
        <Descriptions.Item label="Status">{order.status}</Descriptions.Item>
        <Descriptions.Item label="Total">${Number(order.totalAmount).toFixed(2)}</Descriptions.Item>
      </Descriptions>
      <Table
        rowKey="id"
        dataSource={order.items}
        pagination={false}
        columns={[
          { title: 'Movie', dataIndex: 'movieTitle' },
          { title: 'Barcode', dataIndex: 'barcode' },
          { title: 'Due', dataIndex: 'dueDate' },
          {
            title: 'Charge',
            dataIndex: 'lineAmount',
            render: (v: number) => `$${Number(v).toFixed(2)}`,
          },
        ]}
      />
    </>
  )
}
