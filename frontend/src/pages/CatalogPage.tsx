import { useEffect, useState } from 'react'
import { Alert, Empty, Input, Select, Space, Spin, Table, Tag, Typography } from 'antd'
import { Link } from 'react-router-dom'
import { catalogApi } from '../api/services'
import { problemMessage } from '../api/client'
import type { Genre, Movie } from '../types/api'

export default function CatalogPage() {
  const [movies, setMovies] = useState<Movie[]>([])
  const [genres, setGenres] = useState<Genre[]>([])
  const [q, setQ] = useState('')
  const [genreId, setGenreId] = useState<number | undefined>()
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    catalogApi.genres().then(setGenres).catch(() => undefined)
  }, [])

  useEffect(() => {
    setLoading(true)
    setError(null)
    catalogApi
      .movies({ q: q || undefined, genreId, page: 0, size: 50 })
      .then((page) => setMovies(page.content))
      .catch((err) => setError(problemMessage(err)))
      .finally(() => setLoading(false))
  }, [q, genreId])

  return (
    <Space direction="vertical" size="large" style={{ width: '100%' }}>
      <Typography.Title level={2}>Movie catalog</Typography.Title>
      <Space wrap>
        <Input.Search
          placeholder="Search titles"
          allowClear
          onSearch={setQ}
          style={{ width: 280 }}
        />
        <Select
          allowClear
          placeholder="Genre"
          style={{ width: 180 }}
          options={genres.map((g) => ({ value: g.id, label: g.name }))}
          onChange={(v) => setGenreId(v)}
        />
      </Space>
      {error && <Alert type="error" message={error} />}
      {loading ? (
        <Spin />
      ) : movies.length === 0 ? (
        <Empty description="No movies found" />
      ) : (
        <Table
          rowKey="id"
          dataSource={movies}
          pagination={false}
          columns={[
            {
              title: 'Title',
              dataIndex: 'title',
              render: (title, row) => <Link to={`/movies/${row.id}`}>{title}</Link>,
            },
            { title: 'Year', dataIndex: 'releaseYear', width: 90 },
            {
              title: 'Genres',
              dataIndex: 'genres',
              render: (gs: Movie['genres']) => gs.map((g) => <Tag key={g.id}>{g.name}</Tag>),
            },
            {
              title: 'Daily rate',
              dataIndex: 'dailyRate',
              render: (v: number) => `$${Number(v).toFixed(2)}`,
            },
            {
              title: 'Available',
              dataIndex: 'availableCopies',
              render: (v: number) => (v > 0 ? <Tag color="green">{v}</Tag> : <Tag>0</Tag>),
            },
          ]}
        />
      )}
    </Space>
  )
}
