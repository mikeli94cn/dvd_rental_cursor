import { useEffect, useState } from 'react'
import { Alert, Button, Form, Input, Modal, Select, Space, Spin, Table, Typography, message } from 'antd'
import { problemMessage } from '../../api/client'
import { rentalApi } from '../../api/services'
import type { RentalItem, RentalOrder } from '../../types/api'

export default function ReturnsAdminPage() {
  const [orders, setOrders] = useState<RentalOrder[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [selected, setSelected] = useState<RentalItem | null>(null)
  const [form] = Form.useForm()

  const reload = () => {
    setLoading(true)
    rentalApi
      .staffList({ status: 'ACTIVE', page: 0 })
      .then((page) => setOrders(page.content))
      .catch((err) => setError(problemMessage(err)))
      .finally(() => setLoading(false))
  }

  useEffect(() => {
    reload()
  }, [])

  const openItems = orders.flatMap((order) =>
    order.items
      .filter((i) => i.status === 'RENTED' || i.status === 'OVERDUE')
      .map((item) => ({ ...item, orderId: order.id, customerName: order.customerName })),
  )

  return (
    <Space direction="vertical" style={{ width: '100%' }} size="large">
      <Typography.Title level={2}>Return processing</Typography.Title>
      {error && <Alert type="error" message={error} />}
      {loading ? (
        <Spin />
      ) : (
        <Table
          rowKey="id"
          dataSource={openItems}
          columns={[
            { title: 'Order', dataIndex: 'orderId' },
            { title: 'Customer', dataIndex: 'customerName' },
            { title: 'Movie', dataIndex: 'movieTitle' },
            { title: 'Barcode', dataIndex: 'barcode' },
            { title: 'Due', dataIndex: 'dueDate' },
            { title: 'Status', dataIndex: 'status' },
            {
              title: '',
              render: (_, row) => (
                <Button
                  type="primary"
                  onClick={() => {
                    setSelected(row)
                    form.setFieldsValue({ inspectionResult: 'AVAILABLE', notes: '' })
                  }}
                >
                  Confirm return
                </Button>
              ),
            },
          ]}
        />
      )}

      <Modal
        title="Confirm return & inspection"
        open={!!selected}
        onCancel={() => setSelected(null)}
        onOk={() => form.submit()}
      >
        <Form
          form={form}
          layout="vertical"
          onFinish={async (values) => {
            if (!selected) return
            try {
              await rentalApi.confirmReturn({
                rentalItemId: selected.id,
                inspectionResult: values.inspectionResult,
                notes: values.notes,
              })
              message.success('Return processed')
              setSelected(null)
              reload()
            } catch (err) {
              message.error(problemMessage(err))
            }
          }}
        >
          <Form.Item name="inspectionResult" label="Inspection result" rules={[{ required: true }]}>
            <Select
              options={['AVAILABLE', 'DAMAGED', 'MAINTENANCE', 'LOST'].map((s) => ({
                value: s,
                label: s,
              }))}
            />
          </Form.Item>
          <Form.Item name="notes" label="Notes">
            <Input.TextArea />
          </Form.Item>
        </Form>
      </Modal>
    </Space>
  )
}
