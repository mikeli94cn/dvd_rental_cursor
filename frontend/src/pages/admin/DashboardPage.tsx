import { useEffect, useState } from 'react'
import { Alert, Card, Col, Row, Spin, Statistic, Typography } from 'antd'
import { problemMessage } from '../../api/client'
import { reportApi } from '../../api/services'
import type { DashboardStats } from '../../types/api'

export default function DashboardPage() {
  const [stats, setStats] = useState<DashboardStats | null>(null)
  const [error, setError] = useState<string | null>(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    reportApi
      .dashboard()
      .then(setStats)
      .catch((err) => setError(problemMessage(err)))
      .finally(() => setLoading(false))
  }, [])

  if (loading) return <Spin />
  if (error) return <Alert type="error" message={error} />
  if (!stats) return null

  return (
    <>
      <Typography.Title level={2}>Dashboard</Typography.Title>
      <Row gutter={[16, 16]}>
        <Col xs={24} sm={12} md={8}><Card><Statistic title="Movies" value={stats.totalMovies} /></Card></Col>
        <Col xs={24} sm={12} md={8}><Card><Statistic title="DVD copies" value={stats.totalCopies} /></Card></Col>
        <Col xs={24} sm={12} md={8}><Card><Statistic title="Available" value={stats.availableCopies} /></Card></Col>
        <Col xs={24} sm={12} md={8}><Card><Statistic title="Active rentals" value={stats.activeRentals} /></Card></Col>
        <Col xs={24} sm={12} md={8}><Card><Statistic title="Overdue items" value={stats.overdueItems} /></Card></Col>
      </Row>
    </>
  )
}
