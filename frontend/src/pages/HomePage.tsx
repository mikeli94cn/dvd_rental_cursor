import { Button, Space, Typography } from 'antd'
import { Link } from 'react-router-dom'

export default function HomePage() {
  return (
    <section
      style={{
        minHeight: '70vh',
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'center',
        gap: 16,
        background:
          'radial-gradient(circle at 20% 20%, rgba(15,39,68,0.12), transparent 40%), radial-gradient(circle at 80% 0%, rgba(196,92,38,0.18), transparent 35%)',
        borderRadius: 16,
        padding: 32,
      }}
    >
      <Typography.Title
        style={{
          fontFamily: 'Georgia, "Times New Roman", serif',
          fontSize: 'clamp(2.4rem, 6vw, 4rem)',
          marginBottom: 0,
          color: '#0f2744',
        }}
      >
        DVD Rental System
      </Typography.Title>
      <Typography.Paragraph style={{ fontSize: 18, maxWidth: 560, color: '#334155' }}>
        Browse physical discs, reserve a rental period online, then pick up and return at the counter.
      </Typography.Paragraph>
      <Space>
        <Button type="primary" size="large">
          <Link to="/movies" style={{ color: 'inherit' }}>Browse catalog</Link>
        </Button>
        <Button size="large">
          <Link to="/register" style={{ color: 'inherit' }}>Create account</Link>
        </Button>
      </Space>
    </section>
  )
}
