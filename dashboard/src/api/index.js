import axios from 'axios'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

request.interceptors.request.use(config => {
  const token = localStorage.getItem('dashboard_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

request.interceptors.response.use(
  response => response.data,
  error => {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('dashboard_token')
      localStorage.removeItem('dashboard_user')
      window.location.reload()
    }
    return Promise.reject(error)
  }
)

export const analyticsApi = {
  getRealtimeStats() {
    return request.get('/analytics/realtime')
  },
  getTrend() {
    return request.get('/analytics/trend')
  },
  getHotNews() {
    return request.get('/analytics/hot-news')
  },
  getChannelDist() {
    return request.get('/analytics/channel-dist')
  },
  getRegionDist() {
    return request.get('/analytics/region-dist')
  },
  getFunnel() {
    return request.get('/analytics/funnel')
  },
  getOverview() {
    return request.get('/analytics/overview')
  }
}

export const adminApi = {
  listNews(page = 1, size = 20, channel, keyword) {
    const params = { page, size }
    if (channel) params.channel = channel
    if (keyword) params.keyword = keyword
    return request.get('/admin/news', { params })
  },
  getNews(id) {
    return request.get(`/admin/news/${id}`)
  },
  updateNews(id, data) {
    return request.put(`/admin/news/${id}`, data)
  },
  deleteNews(id) {
    return request.delete(`/admin/news/${id}`)
  },
  listUsers(page = 1, size = 20) {
    return request.get('/admin/users', { params: { page, size } })
  },
  deleteUser(id) {
    return request.delete(`/admin/users/${id}`)
  },
  listChannels() {
    return request.get('/admin/channels')
  }
}
