import axios from 'axios'

function getApiBaseURL() {
  const saved = localStorage.getItem('apiServerUrl')
  if (saved) {
    const url = saved.trim().replace(/\/$/, '')
    return url.startsWith('http') ? url + '/api' : '/api'
  }
  return window.__API_BASE_URL__ || '/api'
}

const request = axios.create({
  baseURL: getApiBaseURL(),
  timeout: 30000
})

request.interceptors.request.use(config => {
  request.defaults.baseURL = getApiBaseURL()
  // 检查 baseURL 是否有效（必须以 http 开头）
  const base = request.defaults.baseURL
  if (base && !base.startsWith('http') && !base.startsWith('/')) {
    return Promise.reject({
      isNetworkError: true,
      message: '服务器地址未配置',
      config
    })
  }
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

let vueRouterInstance = null

export function setVueRouter(router) {
  vueRouterInstance = router
}

request.interceptors.response.use(
  response => {
    return response
  },
  error => {
    // 网络错误、超时或无响应：返回友好空响应，不抛异常
    if (error.isNetworkError || error.code === 'ECONNABORTED' ||
        error.code === 'ERR_NETWORK' || !error.response) {
      return Promise.resolve({
        data: { success: false, data: [], error: error.message || '网络连接失败' },
        status: 0,
        _isNetworkError: true
      })
    }
    if (error.response && error.response.status === 401) {
      const currentPath = vueRouterInstance?.currentRoute?.value?.path
      // 非认证页面静默忽略 401，不弹 Console 错误
      const silentPages = ['/login', '/register', '/']
      if (currentPath && silentPages.some(p => currentPath === p || currentPath.startsWith(p + '/'))) {
        return Promise.resolve({ data: { success: false, error: 'Unauthorized' } })
      }
      if (currentPath !== '/dashboard') {
        localStorage.removeItem('token')
        localStorage.removeItem('userId')
        localStorage.removeItem('username')
        if (vueRouterInstance && currentPath !== '/login') {
          vueRouterInstance.push('/login')
        }
      }
    }
    return Promise.reject(error)
  }
)

export function updateApiBaseURL() {
  const newBase = getApiBaseURL()
  request.defaults.baseURL = newBase
  window.__API_BASE_URL__ = newBase
}

const newsCache = new Map()
const CACHE_TTL = 300000

function cachedGet(url, params = {}, useCache = true, signal = null) {
  const cacheKey = url + JSON.stringify(params)
  if (useCache) {
    const cached = newsCache.get(cacheKey)
    if (cached && Date.now() - cached.time < CACHE_TTL) {
      return Promise.resolve(cached.data)
    }
  }
  const config = { params }
  if (signal) config.signal = signal
  return request.get(url, config).then(res => {
    newsCache.set(cacheKey, { data: res, time: Date.now() })
    if (newsCache.size > 60) {
      const keysToDelete = []
      for (const [key, val] of newsCache) {
        if (Date.now() - val.time > CACHE_TTL) {
          keysToDelete.push(key)
        }
      }
      if (keysToDelete.length === 0) {
        let count = 0
        for (const [key] of newsCache) {
          keysToDelete.push(key)
          count++
          if (count >= 20) break
        }
      }
      keysToDelete.forEach(k => newsCache.delete(k))
    }
    return res
  })
}

export function createAbortController() {
  return new AbortController()
}

export default request

export const newsApi = {
  getList: (params = {}, useCache = true) => {
    const p = { page: 1, size: 20, ...params }
    return cachedGet('/news', p, useCache)
  },
  getById: (id, useCacheOrOpts = true) => {
    let useCache = true
    let signal = null
    if (typeof useCacheOrOpts === 'object' && useCacheOrOpts !== null) {
      signal = useCacheOrOpts.signal || null
      useCache = useCacheOrOpts.useCache !== undefined ? useCacheOrOpts.useCache : true
    } else {
      useCache = useCacheOrOpts
    }
    return cachedGet(`/news/${id}`, {}, useCache, signal)
  },
  getByChannel: (channel, page = 1, size = 20, useCache = true) => cachedGet('/news', { channel, page, size }, useCache),
  getChannelsNews: (size = 4, useCache = true) => cachedGet('/news/channels', { size }, useCache),
  search: (keyword) => request.get('/news', { params: { keyword, page: 1, size: 20 } }),
  getHot: (useCache = true) => cachedGet('/news/hot', {}, useCache),
  getRelated: (id) => request.get(`/news/${id}/related`),
  translate: (id) => request.post(`/news/${id}/translate`),
  clearCache: () => newsCache.clear(),
}

export const userApi = {
  login: (data) => request.post('/users/login', data),
  register: (data) => request.post('/users/register', data),
  sendCode: (email) => request.post('/users/send-code', { email }),
  getProfile: () => request.get('/users/profile'),
  getFavorites: () => request.get('/users/favorites'),
  addFavorite: (newsId) => request.post(`/users/favorites/${newsId}`),
  removeFavorite: (newsId) => request.delete(`/users/favorites/${newsId}`),
  getHistory: () => request.get('/users/history'),
  addHistory: (newsId, duration = 0) => request.post('/users/history', { newsId, duration }),
  getChannelPreference: () => request.get('/users/channel-preference'),
  logout: () => {
    localStorage.removeItem('token')
    localStorage.removeItem('userId')
    localStorage.removeItem('username')
  },
  isLoggedIn: () => !!localStorage.getItem('token'),
}

export const commentApi = {
  getList: (newsId) => request.get(`/news/${newsId}/comments`),
  add: (newsId, data = {}) => request.post(`/news/${newsId}/comment`, data),
}

const behaviorQueue = []
let behaviorTimer = null

function getDeviceId() {
  let id = localStorage.getItem('device_id')
  if (!id) {
    id = String(Date.now()) + String(Math.floor(Math.random() * 1000000))
    localStorage.setItem('device_id', id)
  }
  return Number(id)
}

function flushBehaviors() {
  if (!behaviorQueue.length) return
  const events = [...behaviorQueue]
  behaviorQueue.length = 0
  clearTimeout(behaviorTimer)
  behaviorTimer = null
  request.post('/behaviors', { events }).catch(() => {})
}

export const behaviorApi = {
  report: (data) => {
    behaviorQueue.push({ ...data, userId: getDeviceId(), timestamp: Date.now() })
    if (behaviorQueue.length >= 10) {
      flushBehaviors()
    } else if (!behaviorTimer) {
      behaviorTimer = setTimeout(flushBehaviors, 5000)
    }
  },
  flush: flushBehaviors,
}

export const analyticsApi = {
  getRealtimeStats: (range) => request.get('/analytics/realtime', { params: range ? { range } : {} }),
  getTrend: (range) => request.get('/analytics/trend', { params: range ? { range } : {} }),
  getHotNews: (range) => request.get('/analytics/hot-news', { params: range ? { range } : {} }),
  getCategoryDist: (range) => request.get('/analytics/channel-dist', { params: range ? { range } : {} }),
  getChannelDist: (range) => request.get('/analytics/channel-dist', { params: range ? { range } : {} }),
  getRegionDist: (range) => request.get('/analytics/region-dist', { params: range ? { range } : {} }),
  getFunnel: (range) => request.get('/analytics/funnel', { params: range ? { range } : {} }),
  getOverview: (range) => request.get('/analytics/overview', { params: range ? { range } : {} }),
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
  chat(message, context) {
    const params = { message }
    if (context) params.context = context
    return request.post('/ai/chat', null, { params, timeout: 60000 })
  },
  generateSummary(content, maxLength) {
    const params = { content }
    if (maxLength) params.maxLength = maxLength
    return request.post('/ai/summary', null, { params, timeout: 60000 })
  },
  generateTags(content) {
    return request.post('/ai/tags', null, { params: { content }, timeout: 60000 })
  }
}
