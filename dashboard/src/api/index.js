import axios from 'axios'

const request = axios.create({
  baseURL: '/api',
  timeout: 30000
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
  getRealtimeStats: (range) => request.get('/analytics/overview', { params: { range } }),
  getTrend: (range) => request.get('/analytics/trend', { params: { range } }),
  getHotNews: (range) => request.get('/analytics/hot-news', { params: { range } }),
  getChannelDist: (range) => request.get('/analytics/channel-dist', { params: { range } }),
  getEventDist: (range) => request.get('/analytics/region-dist', { params: { range } }),
  getFunnel: (range) => request.get('/analytics/funnel', { params: { range } }),
  getOverview: (range) => request.get('/analytics/overview', { params: { range } }),
  getNewsChannels: () => request.get('/news/channels'),
  getNewsList: (page = 1, size = 500) => request.get('/news', { params: { page, size } })
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

export const aiApi = {
  search(keyword) {
    return request.get('/ai/search', { params: { keyword }, timeout: 60000 })
  },
  hotSummary(instruction) {
    const params = {}
    if (instruction) params.instruction = instruction
    return request.get('/ai/hot-summary', { params, timeout: 60000 })
  },
  translate(text) {
    return request.get('/ai/translate', { params: { text }, timeout: 60000 })
  }
}
