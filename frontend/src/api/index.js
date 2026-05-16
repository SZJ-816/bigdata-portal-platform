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

let vueRouterInstance = null

export function setVueRouter(router) {
  vueRouterInstance = router
}

request.interceptors.response.use(
  response => response,
  error => {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('userId')
      localStorage.removeItem('username')
      if (window.location.pathname !== '/login') {
        if (vueRouterInstance) {
          vueRouterInstance.push('/login')
        } else {
          window.location.href = '/login'
        }
      }
    }
    return Promise.reject(error)
  }
)

const newsCache = new Map()
const CACHE_TTL = 300000

function cachedGet(url, params = {}, useCache = true) {
  const cacheKey = url + JSON.stringify(params)
  if (useCache) {
    const cached = newsCache.get(cacheKey)
    if (cached && Date.now() - cached.time < CACHE_TTL) {
      return Promise.resolve(cached.data)
    }
  }
  return request.get(url, { params }).then(res => {
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
  getById: (id, useCache = true) => cachedGet(`/news/${id}`, {}, useCache),
  getByChannel: (channel, page = 1, size = 20, useCache = true) => cachedGet('/news', { channel, page, size }, useCache),
  search: (keyword) => request.get('/news', { params: { keyword, page: 1, size: 20 } }),
  getHot: (useCache = true) => cachedGet('/news/hot', {}, useCache),
  getRelated: (id) => request.get(`/news/${id}/related`),
  translate: (id) => request.post(`/news/${id}/translate`),
  clearCache: () => newsCache.clear(),
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
    behaviorQueue.push({ ...data, timestamp: Date.now() })
    if (behaviorQueue.length >= 10) {
      flushBehaviors()
    } else if (!behaviorTimer) {
      behaviorTimer = setTimeout(flushBehaviors, 5000)
    }
  },
  flush: flushBehaviors,
}

export const analyticsApi = {
  getRealtimeStats: () => request.get('/analytics/realtime'),
  getTrend: (params) => request.get('/analytics/trend', { params }),
  getHotNews: () => request.get('/analytics/hot-news'),
  getCategoryDist: () => request.get('/analytics/category-dist'),
  getRegionDist: () => request.get('/analytics/region-dist'),
  getFunnel: () => request.get('/analytics/funnel'),
}
