import { useEffect, useState } from 'react'
import { Alert, Spin, Table, Typography } from 'antd'
import { problemMessage } from '../../api/client'
import { reportApi } from '../../api/services'
import type { OverdueItem } from '../../types/api'

export default function OverdueAdminPage() {
  const [items, setItems] = useState<OverdueItem[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    reportApi
      .overdue()
      .then((page) => setItems(page.content))
      .catch((err) => setError(problemMessage(err)))
      .finally(() => setLoading(false))
  }, [])

  if (loading) return <Spin />
  if (error) return <Alert type="error" message={error} />

  return (
    <>
      <Typography.Title level={2}>Overdue rentals</Typography.Title>
      <Table
        rowKey="rentalItemId"
        dataSource={items}
        columns={[
          { title: 'Order', dataIndex: 'rentalOrderId' },
          { title: 'Customer', dataIndex: 'customerName' },
          { title: 'Email', dataIndex: 'customerEmail' },
          { title: 'Movie', dataIndex: 'movieTitle' },
          { title: 'Barcode', dataIndex: 'barcode' },
          { title: 'Due', dataIndex: 'dueDate' },
          { title: 'Days late', dataIndex: 'overdueDays' },
          {
            title: 'Est. charge',
            dataIndex: 'estimatedOverdueCharge',
            render: (v: number) => `$${Number(v).toFixed(2)}`,
          },
        ]}
      />
    </>
  )
}
