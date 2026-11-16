import { useEffect, useState } from 'react'
import { Alert, Button, Descriptions, Empty, Space, Spin, Table, Tag, Typography, message } from 'antd'
import { useNavigate, useParams } from 'react-router-dom'
import { catalogApi } from '../api/services'
import { problemMessage } from '../api/client'
import { useAuth } from '../auth/AuthContext'
import { useCart } from '../cart/CartContext'
import type { DvdCopy, Movie } from '../types/api'

export default function MovieDetailPage() {
  const { id } = useParams()
  const [movie, setMovie] = useState<Movie | null>(null)
  const [copies, setCopies] = useState<DvdCopy[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const { isCustomer } = useAuth()
  const { add } = useCart()
  const navigate = useNavigate()

  useEffect(() => {
    const movieId = Number(id)
    if (!movieId) {
      return
    }
    setLoading(true)
    Promise.all([catalogApi.movie(movieId), catalogApi.availability(movieId)])
      .then(([m, page]) => {
        setMovie(m)
        setCopies(page.content)
      })
      .catch((err) => setError(problemMessage(err)))
      .finally(() => setLoading(false))
  }, [id])

  if (loading) {
    return <Spin />
  }
  if (error) {
    return <Alert type="error" message={error} />
  }
  if (!movie) {
    return <Empty description="Movie not found" />
  }

  return (
    <Space direction="vertical" size="large" style={{ width: '100%' }}>
      <Typography.Title level={2}>{movie.title}</Typography.Title>
      <Descriptions bordered size="small" column={1}>
        <Descriptions.Item label="Year">{movie.releaseYear ?? '—'}</Descriptions.Item>
        <Descriptions.Item label="Daily rate">${Number(movie.dailyRate).toFixed(2)}</Descriptions.Item>
        <Descriptions.Item label="Default days">{movie.rentalDaysDefault}</Descriptions.Item>
        <Descriptions.Item label="Genres">
          {movie.genres.map((g) => (
            <Tag key={g.id}>{g.name}</Tag>
          ))}
        </Descriptions.Item>
        <Descriptions.Item label="Description">{movie.description || '—'}</Descriptions.Item>
      </Descriptions>

      <Typography.Title level={4}>Available copies</Typography.Title>
      {copies.length === 0 ? (
        <Empty description="No copies available right now" />
      ) : (
        <Table
          rowKey="id"
          dataSource={copies}
          pagination={false}
          columns={[
            { title: 'Barcode', dataIndex: 'barcode' },
            { title: 'Status', dataIndex: 'status' },
            {
              title: 'Action',
              render: (_, row) =>
                isCustomer ? (
                  <Button
                    type="primary"
                    onClick={() => {
                      add(movie, row.id, row.barcode)
                      message.success('Added to cart')
                      navigate('/cart')
                    }}
                  >
                    Add to cart
                  </Button>
                ) : (
                  <Button onClick={() => navigate('/login')}>Login to rent</Button>
                ),
            },
          ]}
        />
      )}
    </Space>
  )
}
