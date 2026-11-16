import { useEffect, useState } from 'react'
import {
  Alert,
  Button,
  Form,
  Input,
  InputNumber,
  Modal,
  Select,
  Space,
  Spin,
  Table,
  Typography,
  message,
} from 'antd'
import { problemMessage } from '../../api/client'
import { catalogApi } from '../../api/services'
import type { Genre, Movie } from '../../types/api'

export default function MoviesAdminPage() {
  const [movies, setMovies] = useState<Movie[]>([])
  const [genres, setGenres] = useState<Genre[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [open, setOpen] = useState(false)
  const [editing, setEditing] = useState<Movie | null>(null)
  const [form] = Form.useForm()

  const reload = () => {
    setLoading(true)
    Promise.all([catalogApi.movies({ size: 100 }), catalogApi.genres()])
      .then(([page, g]) => {
        setMovies(page.content)
        setGenres(g)
      })
      .catch((err) => setError(problemMessage(err)))
      .finally(() => setLoading(false))
  }

  useEffect(() => {
    reload()
  }, [])

  return (
    <Space direction="vertical" style={{ width: '100%' }} size="large">
      <Space style={{ width: '100%', justifyContent: 'space-between' }}>
        <Typography.Title level={2} style={{ margin: 0 }}>
          Movies
        </Typography.Title>
        <Button
          type="primary"
          onClick={() => {
            setEditing(null)
            form.resetFields()
            form.setFieldsValue({ rentalDaysDefault: 3, dailyRate: 2.99 })
            setOpen(true)
          }}
        >
          Add movie
        </Button>
      </Space>
      {error && <Alert type="error" message={error} />}
      {loading ? (
        <Spin />
      ) : (
        <Table
          rowKey="id"
          dataSource={movies}
          columns={[
            { title: 'Title', dataIndex: 'title' },
            { title: 'Year', dataIndex: 'releaseYear' },
            {
              title: 'Rate',
              dataIndex: 'dailyRate',
              render: (v: number) => `$${Number(v).toFixed(2)}`,
            },
            { title: 'Available', dataIndex: 'availableCopies' },
            {
              title: '',
              render: (_, row) => (
                <Button
                  onClick={() => {
                    setEditing(row)
                    form.setFieldsValue({
                      ...row,
                      genreIds: row.genres.map((g) => g.id),
                    })
                    setOpen(true)
                  }}
                >
                  Edit
                </Button>
              ),
            },
          ]}
        />
      )}

      <Modal
        title={editing ? 'Edit movie' : 'Add movie'}
        open={open}
        onCancel={() => setOpen(false)}
        onOk={() => form.submit()}
        destroyOnClose
      >
        <Form
          form={form}
          layout="vertical"
          onFinish={async (values) => {
            try {
              if (editing) {
                await catalogApi.updateMovie(editing.id, values)
              } else {
                await catalogApi.createMovie(values)
              }
              message.success('Saved')
              setOpen(false)
              reload()
            } catch (err) {
              message.error(problemMessage(err))
            }
          }}
        >
          <Form.Item name="title" label="Title" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="description" label="Description">
            <Input.TextArea rows={3} />
          </Form.Item>
          <Form.Item name="releaseYear" label="Release year">
            <InputNumber style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="dailyRate" label="Daily rate" rules={[{ required: true }]}>
            <InputNumber min={0} step={0.01} style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="rentalDaysDefault" label="Default rental days" rules={[{ required: true }]}>
            <InputNumber min={1} style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="genreIds" label="Genres">
            <Select mode="multiple" options={genres.map((g) => ({ value: g.id, label: g.name }))} />
          </Form.Item>
        </Form>
      </Modal>
    </Space>
  )
}
