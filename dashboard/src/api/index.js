import axios from 'axios'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

request.interceptors.request.use(config => {
  return config
})

request.interceptors.response.use(
  response => response.data,
  error => {
    return Promise.reject(error)
  }
)

export default request

export const analyticsApi = {
  getRealtimeStats: (range) => request.get('/analytics/realtime', { params: { range } }),
  getTrend: (range) => request.get('/analytics/trend', { params: { range } }),
  getHotNews: (range) => request.get('/analytics/hot-news', { params: { range } }),
  getChannelDist: (range) => request.get('/analytics/channel-dist', { params: { range } }),
  getRegionDist: (range) => request.get('/analytics/region-dist', { params: { range } }),
  getFunnel: (range) => request.get('/analytics/funnel', { params: { range } }),
  getOverview: (range) => request.get('/analytics/overview', { params: { range } })
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
  updateUserRole(id, role) {
    return request.put(`/admin/users/${id}/role`, { role })
  },
  updateUserActive(id, isActive) {
    return request.put(`/admin/users/${id}/active`, { isActive })
  },
  listChannels() {
    return request.get('/admin/channels')
  }
}
