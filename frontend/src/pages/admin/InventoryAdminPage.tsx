import { useEffect, useState } from 'react'
import { Alert, Button, Form, Input, Modal, Select, Space, Spin, Table, Typography, message } from 'antd'
import { problemMessage } from '../../api/client'
import { catalogApi, inventoryApi } from '../../api/services'
import type { DvdCopy, Movie } from '../../types/api'

export default function InventoryAdminPage() {
  const [copies, setCopies] = useState<DvdCopy[]>([])
  const [movies, setMovies] = useState<Movie[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [open, setOpen] = useState(false)
  const [form] = Form.useForm()

  const reload = () => {
    setLoading(true)
    Promise.all([inventoryApi.search({ page: 0 }), catalogApi.movies({ size: 100 })])
      .then(([page, moviesPage]) => {
        setCopies(page.content)
        setMovies(moviesPage.content)
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
          DVD inventory
        </Typography.Title>
        <Button type="primary" onClick={() => { form.resetFields(); setOpen(true) }}>
          Add copy
        </Button>
      </Space>
      {error && <Alert type="error" message={error} />}
      {loading ? (
        <Spin />
      ) : (
        <Table
          rowKey="id"
          dataSource={copies}
          columns={[
            { title: 'Barcode', dataIndex: 'barcode' },
            { title: 'Movie', dataIndex: 'movieTitle' },
            { title: 'Status', dataIndex: 'status' },
            {
              title: 'Update status',
              render: (_, row) => (
                <Select
                  style={{ width: 220 }}
                  value={row.status}
                  options={[
                    'AVAILABLE',
                    'MAINTENANCE',
                    'DAMAGED',
                    'LOST',
                  ].map((s) => ({ value: s, label: s }))}
                  onChange={async (status) => {
                    try {
                      await inventoryApi.updateStatus(row.id, status)
                      message.success('Status updated')
                      reload()
                    } catch (err) {
                      message.error(problemMessage(err))
                    }
                  }}
                />
              ),
            },
          ]}
        />
      )}

      <Modal
        title="Add DVD copy"
        open={open}
        onCancel={() => setOpen(false)}
        onOk={() => form.submit()}
      >
        <Form
          form={form}
          layout="vertical"
          onFinish={async (values) => {
            try {
              await inventoryApi.create(values)
              message.success('Copy created')
              setOpen(false)
              reload()
            } catch (err) {
              message.error(problemMessage(err))
            }
          }}
        >
          <Form.Item name="movieId" label="Movie" rules={[{ required: true }]}>
            <Select options={movies.map((m) => ({ value: m.id, label: m.title }))} />
          </Form.Item>
          <Form.Item name="barcode" label="Barcode" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="notes" label="Notes">
            <Input.TextArea />
          </Form.Item>
        </Form>
      </Modal>
    </Space>
  )
}
