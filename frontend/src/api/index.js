import axios from 'axios'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

export default request

export const newsApi = {
  getList: (params) => request.get('/news', { params }),
  getById: (id) => request.get(`/news/${id}`),
  getByChannel: (channel) => request.get('/news', { params: { channel } }),
  search: (keyword) => request.get('/news', { params: { keyword } }),
  getHot: () => request.get('/news/hot'),
  getRelated: (id) => request.get(`/news/${id}/related`),
}

export const userApi = {
  login: async (data) => {
    const res = await request.post('/users/login', data)
    if (res.data.success && res.data.data.token) {
      localStorage.setItem('token', res.data.data.token)
      localStorage.setItem('userId', res.data.data.userId)
      localStorage.setItem('username', res.data.data.username)
    }
    return res
  },
  register: async (data) => {
    const res = await request.post('/users/register', data)
    if (res.data.success && res.data.data.token) {
      localStorage.setItem('token', res.data.data.token)
      localStorage.setItem('userId', res.data.data.userId)
      localStorage.setItem('username', res.data.data.username)
    }
    return res
  },
  sendCode: (email) => request.post('/users/send-code', { email }),
  getProfile: () => request.get('/users/profile'),
  getFavorites: () => request.get('/users/favorites'),
  addFavorite: (newsId) => request.post(`/users/favorites/${newsId}`),
  removeFavorite: (newsId) => request.delete(`/users/favorites/${newsId}`),
  getHistory: () => request.get('/users/history'),
  addHistory: (newsId, duration = 0) => request.post('/users/history', { newsId, duration }),
  logout: () => {
    localStorage.removeItem('token')
    localStorage.removeItem('userId')
    localStorage.removeItem('username')
  },
  isLoggedIn: () => !!localStorage.getItem('token'),
}

export const commentApi = {
  getList: (newsId) => request.get(`/news/${newsId}/comments`),
  add: (newsId, data) => request.post(`/news/${newsId}/comment`, data),
}

export const behaviorApi = {
  report: (data) => request.post('/behaviors', data),
}

export const analyticsApi = {
  getRealtimeStats: () => request.get('/analytics/realtime'),
  getTrend: (params) => request.get('/analytics/trend', { params }),
  getHotNews: () => request.get('/analytics/hot-news'),
  getCategoryDist: () => request.get('/analytics/category-dist'),
  getRegionDist: () => request.get('/analytics/region-dist'),
  getFunnel: () => request.get('/analytics/funnel'),
}
