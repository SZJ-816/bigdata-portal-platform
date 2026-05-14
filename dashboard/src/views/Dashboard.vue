<template>
  <div class="dashboard">
    <header class="header">
      <h1 class="title">科技新闻实时数据大屏</h1>
      <div class="header-right">
        <div class="live-indicator">
          <span class="live-dot"></span>
          <span class="live-text">LIVE</span>
        </div>
        <span class="time">{{ currentTime }}</span>
      </div>
    </header>

    <div class="stats-row">
      <div class="stat-card" v-for="stat in statsData" :key="stat.label">
        <div class="stat-icon" :style="{ color: stat.color }">
          <span v-html="stat.icon"></span>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stat.value }}</div>
          <div class="stat-label">{{ stat.label }}</div>
          <div class="stat-trend" :class="stat.trend > 0 ? 'up' : 'down'">
            <span>{{ stat.trend > 0 ? '↑' : '↓' }}</span>
            {{ Math.abs(stat.trend) }}%
          </div>
        </div>
      </div>
    </div>

    <div class="charts-row">
      <div class="chart-card chart-large">
        <h3 class="chart-title">实时阅读趋势</h3>
        <div ref="trendChartRef" class="chart-container"></div>
      </div>
      <div class="chart-card chart-large">
        <h3 class="chart-title">新闻热度TOP10</h3>
        <div ref="hotNewsChartRef" class="chart-container"></div>
      </div>
    </div>

    <div class="charts-row">
      <div class="chart-card">
        <h3 class="chart-title">频道分布</h3>
        <div ref="channelChartRef" class="chart-container"></div>
      </div>
      <div class="chart-card">
        <h3 class="chart-title">用户行为漏斗</h3>
        <div ref="funnelChartRef" class="chart-container"></div>
      </div>
      <div class="chart-card">
        <h3 class="chart-title">地域分布TOP10</h3>
        <div ref="regionChartRef" class="chart-container"></div>
      </div>
    </div>

    <div class="news-ticker">
      <div class="ticker-label">最新阅读</div>
      <div class="ticker-content">
        <span class="ticker-text" :style="{ animationDuration: tickerDuration + 's' }">
          {{ tickerText }}
        </span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import * as echarts from 'echarts'
import { analyticsApi } from '../api/index.js'

const trendChartRef = ref(null)
const hotNewsChartRef = ref(null)
const channelChartRef = ref(null)
const funnelChartRef = ref(null)
const regionChartRef = ref(null)

let trendChart = null
let hotNewsChart = null
let channelChart = null
let funnelChart = null
let regionChart = null

const currentTime = ref('')
const tickerText = ref('')
const tickerDuration = ref(30)

const statsData = ref([
  { label: '今日UV', value: '0', icon: '👁', color: '#00d4ff', trend: 0 },
  { label: '今日PV', value: '0', icon: '📊', color: '#7c3aed', trend: 0 },
  { label: '实时在线', value: '0', icon: '🔵', color: '#10b981', trend: 0 },
  { label: '平均阅读时长', value: '0s', icon: '⏱', color: '#f59e0b', trend: 0 }
])

const updateTime = () => {
  const now = new Date()
  currentTime.value = now.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
    hour12: false
  })
}

const updateRealtimeStats = async () => {
  try {
    const data = await analyticsApi.getRealtimeStats()
    statsData.value = [
      { label: '今日UV', value: formatNumber(data.uv), icon: '👁', color: '#00d4ff', trend: data.uvTrend },
      { label: '今日PV', value: formatNumber(data.pv), icon: '📊', color: '#7c3aed', trend: data.pvTrend },
      { label: '实时在线', value: formatNumber(data.online), icon: '🔵', color: '#10b981', trend: data.onlineTrend },
      { label: '平均阅读时长', value: formatDuration(data.avgDuration), icon: '⏱', color: '#f59e0b', trend: data.durationTrend }
    ]
    tickerText.value = data.latestNews || ''
  } catch (error) {
    console.error('Failed to fetch realtime stats:', error)
    generateMockStats()
  }
}

const generateMockStats = () => {
  const uv = Math.floor(Math.random() * 50000) + 10000
  const pv = uv * (Math.random() * 3 + 2)
  const online = Math.floor(Math.random() * 5000) + 1000
  const avgDuration = Math.floor(Math.random() * 300) + 60
  const news = [
    'OpenAI发布GPT-5，引发行业热议',
    '苹果WWDC2026即将开幕，iOS 20要来了',
    '特斯拉全自动驾驶获批在华运营',
    '中国量子计算获重大突破',
    'SpaceX星舰完成首次商业发射',
    '华为发布鸿蒙5.0系统',
    '英伟达发布新一代AI芯片',
    '字节跳动推出AI搜索产品',
    '谷歌量子计算机实现重大突破',
    '小米汽车月销量突破2万台'
  ]
  statsData.value = [
    { label: '今日UV', value: formatNumber(uv), icon: '👁', color: '#00d4ff', trend: Math.floor(Math.random() * 30) - 10 },
    { label: '今日PV', value: formatNumber(pv), icon: '📊', color: '#7c3aed', trend: Math.floor(Math.random() * 30) - 10 },
    { label: '实时在线', value: formatNumber(online), icon: '🔵', color: '#10b981', trend: Math.floor(Math.random() * 20) - 5 },
    { label: '平均阅读时长', value: formatDuration(avgDuration), icon: '⏱', color: '#f59e0b', trend: Math.floor(Math.random() * 20) - 10 }
  ]
  tickerText.value = news.slice(0, 5).join(' | ')
  tickerDuration.value = 30
}

const formatNumber = (num) => {
  if (num >= 10000) return (num / 10000).toFixed(1) + 'w'
  if (num >= 1000) return (num / 1000).toFixed(1) + 'k'
  return num.toString()
}

const formatDuration = (seconds) => {
  if (seconds >= 60) {
    const mins = Math.floor(seconds / 60)
    const secs = seconds % 60
    return `${mins}m${secs}s`
  }
  return `${seconds}s`
}

const initTrendChart = async () => {
  if (!trendChartRef.value) return
  trendChart = echarts.init(trendChartRef.value)
  
  const updateChart = async () => {
    try {
      const data = await analyticsApi.getTrend()
      renderTrendChart(data)
    } catch (error) {
      console.error('Failed to fetch trend data:', error)
      renderTrendChart(generateMockTrendData())
    }
  }
  
  updateChart()
  setInterval(updateChart, 10000)
}

const generateMockTrendData = () => {
  const hours = []
  const uvData = []
  const pvData = []
  for (let i = 0; i < 24; i++) {
    hours.push(i + '时')
    uvData.push(Math.floor(Math.random() * 5000) + 1000)
    pvData.push(Math.floor(Math.random() * 15000) + 3000)
  }
  return { hours, uv: uvData, pv: pvData }
}

const renderTrendChart = (data) => {
  const option = {
    backgroundColor: 'transparent',
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(10, 22, 40, 0.9)',
      borderColor: '#1e3a5f',
      textStyle: { color: '#e0e6ed' }
    },
    legend: {
      data: ['UV', 'PV'],
      textStyle: { color: '#a0aec0' },
      top: 10
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      top: '15%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: data.hours,
      axisLine: { lineStyle: { color: '#2d4a6f' } },
      axisLabel: { color: '#a0aec0' }
    },
    yAxis: {
      type: 'value',
      axisLine: { lineStyle: { color: '#2d4a6f' } },
      axisLabel: { color: '#a0aec0' },
      splitLine: { lineStyle: { color: '#1a2a40' } }
    },
    series: [
      {
        name: 'UV',
        type: 'line',
        smooth: true,
        data: data.uv,
        lineStyle: { color: '#00d4ff', width: 2 },
        itemStyle: { color: '#00d4ff' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(0, 212, 255, 0.3)' },
            { offset: 1, color: 'rgba(0, 212, 255, 0)' }
          ])
        }
      },
      {
        name: 'PV',
        type: 'line',
        smooth: true,
        data: data.pv,
        lineStyle: { color: '#7c3aed', width: 2 },
        itemStyle: { color: '#7c3aed' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(124, 58, 237, 0.3)' },
            { offset: 1, color: 'rgba(124, 58, 237, 0)' }
          ])
        }
      }
    ]
  }
  trendChart.setOption(option)
}

const initHotNewsChart = async () => {
  if (!hotNewsChartRef.value) return
  hotNewsChart = echarts.init(hotNewsChartRef.value)
  
  const updateChart = async () => {
    try {
      const data = await analyticsApi.getHotNews()
      renderHotNewsChart(data)
    } catch (error) {
      console.error('Failed to fetch hot news:', error)
      renderHotNewsChart(generateMockHotNews())
    }
  }
  
  updateChart()
  setInterval(updateChart, 15000)
}

const generateMockHotNews = () => {
  const news = [
    'ChatGPT-5发布：AI新纪元开启',
    '苹果Vision Pro 2发布',
    '华为Mate70系列发布',
    'SpaceX火星计划新进展',
    '量子计算突破1000量子比特',
    '英伟达H200芯片供不应求',
    '小米汽车月销破2万',
    '字节跳动AI新产品发布',
    '特斯拉FSD入华获批',
    '中国6G技术重大突破'
  ]
  return news.map((title, i) => ({ title, views: 50000 - i * 4000 }))
}

const renderHotNewsChart = (data) => {
  const option = {
    backgroundColor: 'transparent',
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      backgroundColor: 'rgba(10, 22, 40, 0.9)',
      borderColor: '#1e3a5f',
      textStyle: { color: '#e0e6ed' }
    },
    grid: {
      left: '3%',
      right: '10%',
      bottom: '3%',
      top: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'value',
      axisLine: { lineStyle: { color: '#2d4a6f' } },
      axisLabel: { color: '#a0aec0' },
      splitLine: { lineStyle: { color: '#1a2a40' } }
    },
    yAxis: {
      type: 'category',
      data: data.map(d => d.title).reverse(),
      axisLine: { lineStyle: { color: '#2d4a6f' } },
      axisLabel: { color: '#a0aec0', fontSize: 10 }
    },
    series: [{
      type: 'bar',
      data: data.map(d => d.views).reverse(),
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
          { offset: 0, color: '#00d4ff' },
          { offset: 1, color: '#7c3aed' }
        ])
      },
      barWidth: 12
    }]
  }
  hotNewsChart.setOption(option)
}

const initChannelChart = async () => {
  if (!channelChartRef.value) return
  channelChart = echarts.init(channelChartRef.value)
  
  const updateChart = async () => {
    try {
      const data = await analyticsApi.getChannelDist()
      renderChannelChart(data)
    } catch (error) {
      console.error('Failed to fetch channel dist:', error)
      renderChannelChart(generateMockChannelData())
    }
  }
  
  updateChart()
  setInterval(updateChart, 20000)
}

const generateMockChannelData = () => [
  { name: 'AI', value: 35 },
  { name: '大数据', value: 25 },
  { name: '云计算', value: 18 },
  { name: '互联网', value: 12 },
  { name: '硬件', value: 6 },
  { name: '创业', value: 4 }
]

const renderChannelChart = (data) => {
  const option = {
    backgroundColor: 'transparent',
    tooltip: {
      trigger: 'item',
      backgroundColor: 'rgba(10, 22, 40, 0.9)',
      borderColor: '#1e3a5f',
      textStyle: { color: '#e0e6ed' }
    },
    legend: {
      orient: 'vertical',
      right: '5%',
      top: 'center',
      textStyle: { color: '#a0aec0' }
    },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['35%', '50%'],
      avoidLabelOverlap: false,
      label: { show: false },
      emphasis: {
        label: { show: true, fontSize: 14, fontWeight: 'bold', color: '#fff' }
      },
      labelLine: { show: false },
      data: data.map((item, i) => ({
        ...item,
        itemStyle: {
          color: ['#00d4ff', '#7c3aed', '#10b981', '#f59e0b', '#ef4444', '#ec4899'][i]
        }
      }))
    }]
  }
  channelChart.setOption(option)
}

const initFunnelChart = async () => {
  if (!funnelChartRef.value) return
  funnelChart = echarts.init(funnelChartRef.value)
  
  const updateChart = async () => {
    try {
      const data = await analyticsApi.getFunnel()
      renderFunnelChart(data)
    } catch (error) {
      console.error('Failed to fetch funnel:', error)
      renderFunnelChart(generateMockFunnelData())
    }
  }
  
  updateChart()
  setInterval(updateChart, 25000)
}

const generateMockFunnelData = () => [
  { name: '浏览', value: 100 },
  { name: '点击', value: 75 },
  { name: '阅读', value: 50 },
  { name: '评论', value: 20 },
  { name: '分享', value: 8 }
]

const renderFunnelChart = (data) => {
  const option = {
    backgroundColor: 'transparent',
    tooltip: {
      trigger: 'item',
      backgroundColor: 'rgba(10, 22, 40, 0.9)',
      borderColor: '#1e3a5f',
      textStyle: { color: '#e0e6ed' }
    },
    legend: {
      orient: 'vertical',
      right: '5%',
      top: 'center',
      textStyle: { color: '#a0aec0' }
    },
    series: [{
      type: 'funnel',
      left: '5%',
      top: 20,
      bottom: 20,
      width: '55%',
      min: 0,
      max: 100,
      minSize: '0%',
      maxSize: '100%',
      sort: 'descending',
      gap: 2,
      label: {
        show: true,
        position: 'inside',
        color: '#fff',
        fontSize: 12
      },
      labelLine: { show: false },
      itemStyle: {
        borderColor: '#0a1628',
        borderWidth: 2
      },
      data: data.map((item, i) => ({
        name: item.name,
        value: item.value,
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: ['#00d4ff', '#7c3aed', '#10b981', '#f59e0b', '#ef4444'][i] },
            { offset: 1, color: ['#0099cc', '#5b21b6', '#059669', '#d97706', '#dc2626'][i] }
          ])
        }
      }))
    }]
  }
  funnelChart.setOption(option)
}

const initRegionChart = async () => {
  if (!regionChartRef.value) return
  regionChart = echarts.init(regionChartRef.value)
  
  const updateChart = async () => {
    try {
      const data = await analyticsApi.getRegionDist()
      renderRegionChart(data)
    } catch (error) {
      console.error('Failed to fetch region dist:', error)
      renderRegionChart(generateMockRegionData())
    }
  }
  
  updateChart()
  setInterval(updateChart, 20000)
}

const generateMockRegionData = () => [
  { name: '北京', value: 8500 },
  { name: '上海', value: 7200 },
  { name: '深圳', value: 6500 },
  { name: '广州', value: 5800 },
  { name: '杭州', value: 4900 },
  { name: '成都', value: 4200 },
  { name: '南京', value: 3600 },
  { name: '武汉', value: 3100 },
  { name: '西安', value: 2600 },
  { name: '苏州', value: 2200 }
]

const renderRegionChart = (data) => {
  const option = {
    backgroundColor: 'transparent',
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      backgroundColor: 'rgba(10, 22, 40, 0.9)',
      borderColor: '#1e3a5f',
      textStyle: { color: '#e0e6ed' }
    },
    grid: {
      left: '3%',
      right: '8%',
      bottom: '3%',
      top: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'value',
      axisLine: { lineStyle: { color: '#2d4a6f' } },
      axisLabel: { color: '#a0aec0' },
      splitLine: { lineStyle: { color: '#1a2a40' } }
    },
    yAxis: {
      type: 'category',
      data: data.map(d => d.name).reverse(),
      axisLine: { lineStyle: { color: '#2d4a6f' } },
      axisLabel: { color: '#a0aec0' }
    },
    series: [{
      type: 'bar',
      data: data.map(d => d.value).reverse(),
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
          { offset: 0, color: '#10b981' },
          { offset: 1, color: '#34d399' }
        ])
      },
      barWidth: 15
    }]
  }
  regionChart.setOption(option)
}

const handleResize = () => {
  trendChart?.resize()
  hotNewsChart?.resize()
  channelChart?.resize()
  funnelChart?.resize()
  regionChart?.resize()
}

let statsInterval = null
let timeInterval = null

onMounted(() => {
  updateTime()
  timeInterval = setInterval(updateTime, 1000)
  
  updateRealtimeStats()
  statsInterval = setInterval(updateRealtimeStats, 5000)
  
  initTrendChart()
  initHotNewsChart()
  initChannelChart()
  initFunnelChart()
  initRegionChart()
  
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  if (statsInterval) clearInterval(statsInterval)
  if (timeInterval) clearInterval(timeInterval)
  
  trendChart?.dispose()
  hotNewsChart?.dispose()
  channelChart?.dispose()
  funnelChart?.dispose()
  regionChart?.dispose()
  
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
.dashboard {
  width: 100vw;
  height: 100vh;
  background: linear-gradient(135deg, #0a1628 0%, #0f2744 50%, #0a1628 100%);
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 15px;
  overflow: hidden;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 30px;
  background: linear-gradient(90deg, rgba(0, 212, 255, 0.1) 0%, rgba(124, 58, 237, 0.1) 100%);
  border: 1px solid rgba(0, 212, 255, 0.3);
  border-radius: 10px;
  flex-shrink: 0;
}

.title {
  font-size: 28px;
  font-weight: bold;
  background: linear-gradient(90deg, #00d4ff, #7c3aed);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-shadow: 0 0 30px rgba(0, 212, 255, 0.5);
}

.header-right {
  display: flex;
  align-items: center;
  gap: 30px;
}

.live-indicator {
  display: flex;
  align-items: center;
  gap: 8px;
}

.live-dot {
  width: 12px;
  height: 12px;
  background: #ef4444;
  border-radius: 50%;
  animation: pulse 1.5s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { box-shadow: 0 0 0 0 rgba(239, 68, 68, 0.7); }
  50% { box-shadow: 0 0 0 10px rgba(239, 68, 68, 0); }
}

.live-text {
  color: #ef4444;
  font-size: 16px;
  font-weight: bold;
}

.time {
  color: #a0aec0;
  font-size: 16px;
  font-family: 'Courier New', monospace;
}

.stats-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 15px;
  flex-shrink: 0;
}

.stat-card {
  background: linear-gradient(135deg, rgba(30, 58, 95, 0.6) 0%, rgba(15, 39, 68, 0.8) 100%);
  border: 1px solid rgba(0, 212, 255, 0.2);
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 15px;
  transition: all 0.3s ease;
}

.stat-card:hover {
  border-color: rgba(0, 212, 255, 0.5);
  box-shadow: 0 0 20px rgba(0, 212, 255, 0.2);
}

.stat-icon {
  font-size: 36px;
  opacity: 0.9;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #fff;
  line-height: 1.2;
}

.stat-label {
  font-size: 14px;
  color: #a0aec0;
  margin: 5px 0;
}

.stat-trend {
  font-size: 12px;
  font-weight: bold;
}

.stat-trend.up {
  color: #10b981;
}

.stat-trend.down {
  color: #ef4444;
}

.charts-row {
  display: grid;
  gap: 15px;
  flex: 1;
  min-height: 0;
}

.charts-row:first-of-type {
  grid-template-columns: 1fr 1fr;
}

.charts-row:nth-of-type(2) {
  grid-template-columns: 1fr 1fr 1fr;
}

.chart-card {
  background: linear-gradient(135deg, rgba(30, 58, 95, 0.5) 0%, rgba(15, 39, 68, 0.7) 100%);
  border: 1px solid rgba(0, 212, 255, 0.15);
  border-radius: 12px;
  padding: 15px;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.chart-title {
  color: #e0e6ed;
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 10px;
  padding-left: 10px;
  border-left: 3px solid #00d4ff;
  flex-shrink: 0;
}

.chart-container {
  flex: 1;
  min-height: 0;
}

.news-ticker {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 15px 30px;
  background: linear-gradient(90deg, rgba(124, 58, 237, 0.2) 0%, rgba(0, 212, 255, 0.2) 100%);
  border: 1px solid rgba(124, 58, 237, 0.3);
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
}

.ticker-label {
  color: #00d4ff;
  font-size: 14px;
  font-weight: bold;
  white-space: nowrap;
}

.ticker-content {
  flex: 1;
  overflow: hidden;
  mask-image: linear-gradient(to right, transparent, black 10%, black 90%, transparent);
}

.ticker-text {
  display: inline-block;
  white-space: nowrap;
  color: #e0e6ed;
  font-size: 14px;
  animation: ticker linear infinite;
}

@keyframes ticker {
  0% { transform: translateX(100%); }
  100% { transform: translateX(-100%); }
}
</style>
