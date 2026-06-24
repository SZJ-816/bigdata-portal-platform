import axios from 'axios'

const request = axios.create({
  baseURL: '/api',
  timeout: 30000
})

// 请求拦截：自动附加 JWT Token
request.interceptors.request.use(config => {
  const token = localStorage.getItem('admin_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// 响应拦截
request.interceptors.response.use(
  response => response.data,
  error => {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('admin_token')
      localStorage.removeItem('admin_user')
      window.dispatchEvent(new Event('admin-logout'))
    }
    return Promise.reject(error)
  }
)

export default request

// ======================== 登录认证 ========================
export const authApi = {
  login(username, password) {
    return request.post('/auth/login', { username, password })
  },
  logout() {
    localStorage.removeItem('admin_token')
    localStorage.removeItem('admin_user')
  },
  getAdminUser() {
    const raw = localStorage.getItem('admin_user')
    return raw ? JSON.parse(raw) : null
  },
  isLoggedIn() {
    return !!localStorage.getItem('admin_token')
  }
}

// ======================== 数据分析 ========================
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

// ======================== 新闻管理 ========================
export const newsApi = {
  list(page = 1, size = 20, channel, keyword) {
    const params = { page, size }
    if (channel) params.channel = channel
    if (keyword) params.keyword = keyword
    return request.get('/news', { params })
  },
  getById(id) {
    return request.get(`/news/${id}`)
  },
  add(data) {
    return request.post('/news', data)
  },
  update(id, data) {
    return request.put(`/news/${id}`, data)
  },
  delete(id) {
    return request.delete(`/news/${id}`)
  },
  channels() {
    return request.get('/news/channels')
  }
}

// ======================== 用户管理 ========================
export const userApi = {
  list() {
    return request.get('/users/list')
  },
  getById(id) {
    return request.get(`/users/${id}`)
  },
  add(data) {
    return request.post('/users', data)
  },
  update(id, data) {
    return request.put(`/users/${id}`, data)
  },
  delete(id) {
    return request.delete(`/users/${id}`)
  }
}

// ======================== 评论管理 ========================
export const commentApi = {
  list() {
    return request.get('/comments/list')
  },
  getByArticle(articleId) {
    return request.get(`/comments/article/${articleId}`)
  },
  update(id, data) {
    return request.put(`/comments/${id}`, data)
  },
  delete(id) {
    return request.delete(`/comments/${id}`)
  }
}

// ======================== AI 接口 ========================
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

// ======================== 图片上传 ========================
export const imageApi = {
  upload(file) {
    const formData = new FormData()
    formData.append('file', file)
    return request.post('/image/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
      timeout: 30000
    })
  }
}

// ======================== AI 模型配置 ========================
export const aiConfigApi = {
  list() {
    return request.get('/ai/config/list')
  },
  getDefault() {
    return request.get('/ai/config/default')
  },
  getById(id) {
    return request.get(`/ai/config/${id}`)
  },
  add(data) {
    return request.post('/ai/config', data)
  },
  update(id, data) {
    return request.put(`/ai/config/${id}`, data)
  },
  delete(id) {
    return request.delete(`/ai/config/${id}`)
  },
  setDefault(id) {
    return request.put(`/ai/config/${id}/default`)
  }
}
