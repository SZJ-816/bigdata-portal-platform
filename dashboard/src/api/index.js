import axios from 'axios'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

request.interceptors.response.use(
  response => response.data,
  error => {
    console.error('API Error:', error)
    return Promise.reject(error)
  }
)

export const analyticsApi = {
  getRealtimeStats() {
    return request.get('/analytics/realtime-stats')
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
  }
}
