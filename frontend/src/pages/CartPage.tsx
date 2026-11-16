import { Alert, Button, Empty, InputNumber, Space, Table, Typography, message } from 'antd'
import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { problemMessage } from '../api/client'
import { rentalApi } from '../api/services'
import { useCart } from '../cart/CartContext'

export default function CartPage() {
  const { items, remove, clear } = useCart()
  const [rentalDays, setRentalDays] = useState<number | null>(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const navigate = useNavigate()

  if (items.length === 0) {
    return <Empty description="Your rental cart is empty" />
  }

  return (
    <Space direction="vertical" size="large" style={{ width: '100%' }}>
      <Typography.Title level={2}>Rental cart</Typography.Title>
      {error && <Alert type="error" message={error} />}
      <Table
        rowKey="dvdCopyId"
        dataSource={items}
        pagination={false}
        columns={[
          { title: 'Movie', render: (_, row) => row.movie.title },
          { title: 'Barcode', dataIndex: 'barcode' },
          {
            title: 'Daily rate',
            render: (_, row) => `$${Number(row.movie.dailyRate).toFixed(2)}`,
          },
          {
            title: '',
            render: (_, row) => (
              <Button danger onClick={() => remove(row.dvdCopyId)}>
                Remove
              </Button>
            ),
          },
        ]}
      />
      <Space>
        <Typography.Text>Rental days (optional override):</Typography.Text>
        <InputNumber min={1} value={rentalDays ?? undefined} onChange={(v) => setRentalDays(v)} />
      </Space>
      <Button
        type="primary"
        loading={loading}
        onClick={async () => {
          setLoading(true)
          setError(null)
          try {
            const order = await rentalApi.create(
              items.map((i) => i.dvdCopyId),
              rentalDays ?? undefined,
            )
            clear()
            message.success('Rental created')
            navigate(`/rentals/${order.id}/confirm`)
          } catch (err) {
            setError(problemMessage(err))
          } finally {
            setLoading(false)
          }
        }}
      >
        Confirm rental
      </Button>
    </Space>
  )
}
