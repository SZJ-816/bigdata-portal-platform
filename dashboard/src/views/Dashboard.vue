<template>
  <div class="dashboard">
    <header class="top-bar">
      <div class="bar-inner">
        <div class="brand" @click="goHome" style="cursor:pointer">
          <img src="/logo.jpg" alt="智讯" class="brand-icon" />
          <span class="brand-name">智讯</span>
          <span class="brand-sub">科技新闻数据大屏</span>
        </div>
        <nav class="main-nav">
          <button :class="['nav-link', { active: activeTab === 'overview' }]" @click="switchTab('overview')">
            <svg class="nav-icon" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <rect x="3" y="3" width="7" height="7" rx="1.5"/>
              <rect x="14" y="3" width="7" height="7" rx="1.5"/>
              <rect x="3" y="14" width="7" height="7" rx="1.5"/>
              <rect x="14" y="14" width="7" height="7" rx="1.5"/>
            </svg>数据总览
          </button>
          <button :class="['nav-link', { active: activeTab === 'news' }]" @click="switchTab('news')">
            <svg class="nav-icon" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
              <polyline points="14 2 14 8 20 8"/>
              <line x1="16" y1="13" x2="8" y2="13"/>
              <line x1="16" y1="17" x2="8" y2="17"/>
              <line x1="10" y1="9" x2="8" y2="9"/>
            </svg>新闻浏览
          </button>
          <button :class="['nav-link', { active: activeTab === 'behavior' }]" @click="switchTab('behavior')">
            <svg class="nav-icon" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <circle cx="12" cy="7" r="4"/>
              <path d="M5.5 21a6.5 6.5 0 0 1 13 0"/>
              <polyline points="17 11 19 14 22 12"/>
            </svg>实时行为
          </button>
        </nav>
        <div class="bar-right">
          <a href="javascript:void(0)" @click.prevent="goHome" class="home-btn">
            <svg class="home-icon" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/>
              <polyline points="9 22 9 12 15 12 15 22"/>
            </svg>回到主页
          </a>
          <span class="live-tag"><i class="dot"></i>LIVE</span>
          <span class="clock">{{ currentTime }}</span>
        </div>
      </div>
    </header>

    <main class="page-body">
      <div v-if="activeTab === 'overview'" class="overview-page">
        <section class="kpi-banner">
          <div class="kpi-item" v-for="stat in statsData" :key="stat.label" :style="{ '--kpi-color': stat.color }">
            <div class="kpi-num" :style="{ color: stat.color }">{{ stat.value }}</div>
            <div class="kpi-label">{{ stat.label }}</div>
          </div>
        </section>

        <div class="time-range-bar">
          <button :class="['range-btn', { active: timeRange === 'today' }]" @click="timeRange = 'today'">今日</button>
          <button :class="['range-btn', { active: timeRange === 'week' }]" @click="timeRange = 'week'">近7天</button>
          <button :class="['range-btn', { active: timeRange === 'month' }]" @click="timeRange = 'month'">近30天</button>
        </div>
        <div class="overview-grid">
          <section class="panel panel-trend">
            <div class="panel-head">
              <h2>今日阅读趋势</h2>
              <span class="panel-tag">实时</span>
            </div>
            <div ref="trendChartRef" class="panel-chart"></div>
          </section>

          <section class="panel panel-hot">
            <div class="panel-head">
              <h2>热门新闻 TOP10</h2>
              <span class="panel-tag">热度</span>
            </div>
            <div ref="hotNewsChartRef" class="panel-chart"></div>
          </section>

          <section class="panel panel-channel">
            <div class="panel-head">
              <h2>频道分布</h2>
            </div>
            <div ref="channelChartRef" class="panel-chart"></div>
          </section>

          <section class="panel panel-funnel">
            <div class="panel-head">
              <h2>行为漏斗</h2>
            </div>
            <div ref="funnelChartRef" class="panel-chart"></div>
          </section>

          <section class="panel panel-event">
            <div class="panel-head">
              <h2>行为类型分布</h2>
            </div>
            <div ref="regionChartRef" class="panel-chart"></div>
          </section>

          <section class="panel panel-source">
            <div class="panel-head">
              <h2>新闻来源分布</h2>
              <span class="panel-tag">来源</span>
            </div>
            <div ref="sourceChartRef" class="panel-chart"></div>
          </section>

          <section class="panel panel-active-hour">
            <div class="panel-head">
              <h2>用户活跃时段</h2>
              <span class="panel-tag">24h</span>
            </div>
            <div ref="activeHourChartRef" class="panel-chart"></div>
          </section>

          <section class="panel panel-publish-trend">
            <div class="panel-head">
              <h2>新闻发布趋势</h2>
              <span class="panel-tag">趋势</span>
            </div>
            <div ref="publishTrendChartRef" class="panel-chart"></div>
          </section>
        </div>
      </div>

      <div v-if="activeTab === 'news'" class="manage-page">
        <div class="search-bar">
          <input v-model="newsKeyword" class="search-input" placeholder="搜索新闻标题或摘要..." @keyup.enter="loadNews(1)" />
          <select v-model="newsChannel" class="search-select" @change="loadNews(1)">
            <option value="">全部频道</option>
            <option v-for="ch in channels" :key="ch" :value="ch">{{ ch }}</option>
          </select>
          <button class="btn-primary" @click="loadNews(1)">搜索</button>
          <span class="result-count">共 {{ newsTotal }} 条新闻</span>
        </div>
        <div class="table-card">
          <table class="news-table">
            <thead>
              <tr>
                <th class="col-id">ID</th>
                <th class="col-title">标题</th>
                <th class="col-ch">频道</th>
                <th class="col-src">来源</th>
                <th class="col-num">浏览</th>
                <th class="col-num">评论</th>
                <th class="col-time">发布时间</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="item in newsList" :key="item.id">
                <td class="col-id">{{ item.id }}</td>
                <td class="col-title">{{ item.title }}</td>
                <td class="col-ch"><span class="ch-tag">{{ item.channel }}</span></td>
                <td class="col-src">{{ item.source }}</td>
                <td class="col-num">{{ item.viewCount }}</td>
                <td class="col-num">{{ item.commentCount }}</td>
                <td class="col-time">{{ formatDate(item.publishTime) }}</td>
              </tr>
              <tr v-if="newsList.length === 0"><td colspan="7" class="empty-row">暂无数据</td></tr>
            </tbody>
          </table>
        </div>
        <div class="pager">
          <button :disabled="newsPage <= 1" @click="loadNews(newsPage - 1)">‹ 上一页</button>
          <span>{{ newsPage }} / {{ newsTotalPages || 1 }} 页 · 共 {{ newsTotal }} 条</span>
          <button :disabled="newsPage >= newsTotalPages" @click="loadNews(newsPage + 1)">下一页 ›</button>
        </div>
      </div>

      <!-- 实时行为页面 -->
      <div v-if="activeTab === 'behavior'" class="behavior-page">
        <div class="behavior-layout">
          <!-- 左侧：实时统计 + 行为事件流 -->
          <div class="behavior-left">
            <div class="behavior-stats">
              <div class="behavior-stat-card" v-for="s in behaviorStats" :key="s.label">
                <div class="behavior-stat-value" :style="{ color: s.color }">{{ s.value }}</div>
                <div class="behavior-stat-label">{{ s.label }}</div>
              </div>
            </div>
            <div class="behavior-event-panel">
              <div class="behavior-event-head">
                <h2>实时行为事件流</h2>
                <span class="panel-tag">自动刷新</span>
              </div>
              <div class="behavior-event-list">
                <div v-for="(evt, i) in behaviorEvents" :key="i" class="behavior-event-item">
                  <span class="behavior-event-time">{{ evt.time }}</span>
                  <span class="behavior-event-user">用户{{ evt.userId }}</span>
                  <span class="behavior-event-action">进行了</span>
                  <span :class="['behavior-event-type', evt.typeClass]">{{ evt.action }}</span>
                </div>
                <div v-if="behaviorEvents.length === 0" class="behavior-event-empty">等待行为数据...</div>
              </div>
            </div>
          </div>
          <!-- 右侧：分布图 + 趋势图 -->
          <div class="behavior-right">
            <section class="panel panel-behavior-dist">
              <div class="panel-head">
                <h2>行为类型分布</h2>
                <span class="panel-tag">实时</span>
              </div>
              <div ref="behaviorDistChartRef" class="panel-chart"></div>
            </section>
            <section class="panel panel-behavior-trend">
              <div class="panel-head">
                <h2>行为趋势</h2>
                <span class="panel-tag">趋势</span>
              </div>
              <div ref="behaviorTrendChartRef" class="panel-chart"></div>
            </section>
          </div>
        </div>
      </div>
    </main>

    <div v-if="toast.show" :class="['toast', toast.type]">
      {{ toast.message }}
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed, nextTick, watch } from 'vue'
import * as echarts from 'echarts'
import { analyticsApi } from '../api/index.js'

const emit = defineEmits([])

const activeTab = ref('overview')
const trendChartRef = ref(null)
const hotNewsChartRef = ref(null)
const channelChartRef = ref(null)
const funnelChartRef = ref(null)
const regionChartRef = ref(null)
const sourceChartRef = ref(null)
const activeHourChartRef = ref(null)
const publishTrendChartRef = ref(null)
const behaviorDistChartRef = ref(null)
const behaviorTrendChartRef = ref(null)
let trendChart = null, hotNewsChart = null, channelChart = null, funnelChart = null, regionChart = null, sourceChart = null, activeHourChart = null, publishTrendChart = null, behaviorDistChart = null, behaviorTrendChart = null
const currentTime = ref('')

const statsData = ref([
  { label: '今日UV', value: '-', icon: '', color: '#2F6BFF' },
  { label: '今日PV', value: '-', icon: '', color: '#7C3AED' },
  { label: '实时在线', value: '-', icon: '', color: '#10B981' },
  { label: '平均阅读时长', value: '-', icon: '', color: '#F59E0B' },
  { label: '总用户数', value: '-', icon: '', color: '#EC4899' },
  { label: '总新闻数', value: '-', icon: '', color: '#06B6D4' },
  { label: '今日新增', value: '-', icon: '', color: '#8B5CF6' },
  { label: '总浏览量', value: '-', icon: '', color: '#F97316' }
])

const newsList = ref([])
const newsPage = ref(1)
const newsTotal = ref(0)
const newsTotalPages = computed(() => Math.max(1, Math.ceil(newsTotal.value / 20)))
const newsKeyword = ref('')
const newsChannel = ref('')
const channels = ref([])
const timeRange = ref('today')

// 实时行为相关变量
const behaviorEvents = ref([])
const behaviorStats = ref([
  { label: '今日UV', value: '-', color: '#2F6BFF' },
  { label: '今日PV', value: '-', color: '#7C3AED' },
  { label: '在线用户', value: '-', color: '#10B981' },
  { label: '平均时长', value: '-', color: '#F59E0B' }
])
let behaviorInterval = null

// 事件类型映射
const eventTypeMap = {
  page_view: { action: '浏览页面', typeClass: 'type-view' },
  news_click: { action: '点击新闻', typeClass: 'type-click' },
  news_read: { action: '阅读新闻', typeClass: 'type-read' },
  search: { action: '搜索', typeClass: 'type-search' },
  ai_search: { action: 'AI搜索', typeClass: 'type-ai' },
  click: { action: '点击', typeClass: 'type-click' },
  comment: { action: '评论', typeClass: 'type-comment' },
  share: { action: '分享', typeClass: 'type-share' },
  favorite: { action: '收藏', typeClass: 'type-fav' }
}

const switchTab = (tab) => {
  activeTab.value = tab
  if (tab === 'news') loadNews(1)
  if (tab === 'behavior') {
    loadBehaviorEvents()
    initBehaviorCharts()
  }
}

const toast = ref({ show: false, message: '', type: 'success' })
const showToast = (msg, type = 'success') => { toast.value = { show: true, message: msg, type }; setTimeout(() => { toast.value.show = false }, 3000) }

const updateTime = () => {
  const now = new Date()
  currentTime.value = now.toLocaleString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit', second: '2-digit', hour12: false })
}

const formatNumber = (num) => {
  if (num === null || num === undefined) return '0'
  if (num >= 10000) return (num / 10000).toFixed(1) + 'w'
  if (num >= 1000) return (num / 1000).toFixed(1) + 'k'
  return num.toString()
}

const formatDuration = (s) => {
  if (!s && s !== 0) return '0s'
  s = Math.round(s)
  if (s <= 0) return '0s'
  return s >= 60 ? `${Math.floor(s / 60)}m${s % 60}s` : `${s}s`
}

const formatDate = (d) => {
  if (!d) return '-'
  return new Date(d).toLocaleString('zh-CN', { month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
}

let realtimeLoading = false
const updateRealtimeStats = async () => {
  if (realtimeLoading) return
  realtimeLoading = true
  try {
    const res = await analyticsApi.getRealtimeStats(timeRange.value)
    const d = (res.data || res) || {}
    const prefix = timeRange.value === 'today' ? '今日' : timeRange.value === 'week' ? '近7天' : '近30天'
    statsData.value = [
      { label: prefix + 'UV', value: formatNumber(d.todayUsers || d.totalUsers), color: '#2F6BFF' },
      { label: prefix + 'PV', value: formatNumber(d.totalViews), color: '#7C3AED' },
      { label: '总行为数', value: formatNumber(d.totalBehaviors), color: '#10B981' },
      { label: '总用户数', value: formatNumber(d.totalUsers), color: '#F59E0B' },
      { label: '总新闻数', value: formatNumber(d.totalNews), color: '#EC4899' },
      { label: '今日新增', value: formatNumber(d.todayNews), color: '#06B6D4' },
      { label: '阅读记录', value: formatNumber(d.totalReadHistory), color: '#8B5CF6' },
      { label: '总浏览量', value: formatNumber(d.totalViews), color: '#F97316' }
    ]
  } catch (e) { console.error(e) }
  finally { realtimeLoading = false }
}

const chartFontFamily = "'Noto Sans SC', 'Microsoft YaHei', 'PingFang SC', 'Hiragino Sans GB', 'WenQuanYi Micro Hei', sans-serif"

const chartTheme = {
  textColor: '#64748B',
  splitColor: '#F1F5F9',
  tipBg: '#1E293B',
  tipText: '#F8FAFC',
  fontFamily: chartFontFamily
}

const initTrendChart = async () => {
  if (!trendChartRef.value) return
  if (trendChart) trendChart.dispose()
  trendChart = echarts.init(trendChartRef.value)
  try {
    const res = await analyticsApi.getTrend(timeRange.value)
    const data = res.data || res
    if (!Array.isArray(data) || data.length === 0) {
      trendChart.setOption({
        title: { text: '暂无数据', left: 'center', top: 'center', textStyle: { color: '#94a3b8', fontSize: 14, fontFamily: chartTheme.fontFamily } }
      })
      return
    }
    trendChart.setOption({
      backgroundColor: 'transparent',
      tooltip: { trigger: 'axis', backgroundColor: chartTheme.tipBg, borderColor: '#334155', textStyle: { color: chartTheme.tipText, fontSize: 12, fontFamily: chartTheme.fontFamily } },
      legend: { data: ['UV', 'PV'], textStyle: { color: chartTheme.textColor, fontSize: 11, fontFamily: chartTheme.fontFamily }, top: 0, right: 0 },
      grid: { left: 40, right: 16, bottom: 24, top: 32 },
      xAxis: { type: 'category', data: data.map(d => d.hour), axisLine: { lineStyle: { color: '#E2E8F0' } }, axisLabel: { color: chartTheme.textColor, fontSize: 10, fontFamily: chartTheme.fontFamily }, axisTick: { show: false } },
      yAxis: { type: 'value', axisLine: { show: false }, axisLabel: { color: chartTheme.textColor, fontSize: 10, fontFamily: chartTheme.fontFamily }, splitLine: { lineStyle: { color: chartTheme.splitColor } } },
      series: [
        { name: 'UV', type: 'line', smooth: true, data: data.map(d => d.uv), symbol: 'none', lineStyle: { color: '#2F6BFF', width: 2.5 }, areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: 'rgba(47,107,255,0.18)' }, { offset: 1, color: 'rgba(47,107,255,0)' }]) } },
        { name: 'PV', type: 'line', smooth: true, data: data.map(d => d.pv), symbol: 'none', lineStyle: { color: '#7C3AED', width: 2.5 }, areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: 'rgba(124,58,237,0.18)' }, { offset: 1, color: 'rgba(124,58,237,0)' }]) } }
      ]
    })
  } catch (e) { console.error(e) }
}

const initHotNewsChart = async () => {
  if (!hotNewsChartRef.value) return
  if (!hotNewsChart) hotNewsChart = echarts.init(hotNewsChartRef.value)
  try {
    // 从新闻列表API获取真实数据，按view_count排序
    const res = await fetch('/api/news?page=1&size=10').then(r => r.json())
    const pageData = res.data || res
    const records = pageData?.data || pageData?.records || []
    let data = []
    if (Array.isArray(records) && records.length > 0) {
      // 检查view_count是否都为0
      const hasViews = records.some(r => (r.viewCount || r.view_count || 0) > 0)
      if (hasViews) {
        // 按view_count排序
        const sorted = [...records].sort((a, b) => (b.viewCount || b.view_count || 0) - (a.viewCount || a.view_count || 0))
        data = sorted.slice(0, 10).map(d => ({
          name: d.title || '',
          value: d.viewCount || d.view_count || 0
        }))
      } else {
        // view_count都是0时，按publishTime排序（最新发布）
        const sorted = [...records].sort((a, b) => {
          const ta = new Date(a.publishTime || a.publish_time || 0).getTime()
          const tb = new Date(b.publishTime || b.publish_time || 0).getTime()
          return tb - ta
        })
        data = sorted.slice(0, 10).map(d => ({
          name: d.title || '',
          value: d.viewCount || d.view_count || 0
        }))
      }
    }
    if (data.length === 0) {
      hotNewsChart.setOption({
        title: { text: '暂无数据', left: 'center', top: 'center', textStyle: { color: '#94a3b8', fontSize: 14, fontFamily: chartTheme.fontFamily } }
      })
      return
    }
    hotNewsChart.setOption({
      backgroundColor: 'transparent',
      tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' }, backgroundColor: chartTheme.tipBg, borderColor: '#334155', textStyle: { color: chartTheme.tipText, fontSize: 12, fontFamily: chartTheme.fontFamily } },
      grid: { left: 8, right: 40, bottom: 4, top: 4, containLabel: true },
      xAxis: { type: 'value', axisLine: { show: false }, axisLabel: { color: chartTheme.textColor, fontSize: 10, fontFamily: chartTheme.fontFamily }, splitLine: { lineStyle: { color: chartTheme.splitColor } } },
      yAxis: { type: 'category', data: data.map(d => (d.name || '').substring(0, 10)).reverse(), axisLine: { show: false }, axisTick: { show: false }, axisLabel: { color: '#475569', fontSize: 11, fontFamily: chartTheme.fontFamily } },
      series: [{ type: 'bar', data: data.map(d => d.value).reverse(), itemStyle: { color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [{ offset: 0, color: '#2F6BFF' }, { offset: 1, color: '#7C3AED' }]), borderRadius: [0, 4, 4, 0] }, barWidth: 14 }]
    })
  } catch (e) { console.error(e) }
}

const initChannelChart = async () => {
  if (!channelChartRef.value) return
  if (channelChart) channelChart.dispose()
  channelChart = echarts.init(channelChartRef.value)
  try {
    const res = await analyticsApi.getNewsChannels()
    const channelData = res.data || res
    let data = []
    if (channelData && typeof channelData === 'object') {
      data = Object.entries(channelData).map(([name, items]) => ({
        name,
        value: Array.isArray(items) ? items.length : (typeof items === 'number' ? items : 0)
      }))
    }
    if (data.length === 0) {
      channelChart.setOption({
        title: { text: '暂无数据', left: 'center', top: 'center', textStyle: { color: '#94a3b8', fontSize: 14, fontFamily: chartTheme.fontFamily } }
      })
      return
    }
    const colors = ['#2F6BFF', '#7C3AED', '#10B981', '#F59E0B', '#EF4444', '#EC4899', '#06B6D4', '#8B5CF6']
    channelChart.setOption({
      backgroundColor: 'transparent',
      tooltip: { trigger: 'item', backgroundColor: chartTheme.tipBg, borderColor: '#334155', textStyle: { color: chartTheme.tipText, fontSize: 12, fontFamily: chartTheme.fontFamily }, formatter: '{b}: {c}条 ({d}%)' },
      legend: { orient: 'vertical', right: 8, top: 'center', textStyle: { color: '#475569', fontSize: 11, fontFamily: chartTheme.fontFamily }, itemWidth: 10, itemHeight: 10, itemGap: 8 },
      series: [{ type: 'pie', radius: ['38%', '68%'], center: ['35%', '50%'], avoidLabelOverlap: false, label: { show: false }, emphasis: { label: { show: true, fontSize: 12, fontWeight: 'bold' } }, labelLine: { show: false }, data: data.map((item, i) => ({ ...item, itemStyle: { color: colors[i % colors.length] } })), itemStyle: { borderColor: '#fff', borderWidth: 2 } }]
    })
  } catch (e) { console.error(e) }
}

const initFunnelChart = async () => {
  if (!funnelChartRef.value) return
  if (funnelChart) funnelChart.dispose()
  funnelChart = echarts.init(funnelChartRef.value)
  try {
    const [overviewRes, eventRes] = await Promise.all([
      analyticsApi.getOverview(timeRange.value),
      analyticsApi.getEventDist(timeRange.value)
    ])
    const overview = (overviewRes.data || overviewRes) || {}
    const eventData = eventRes.data || eventRes

    const typeMap = { 'page_view': '页面浏览', 'news_click': '新闻点击', 'news_read': '新闻阅读', 'search': '搜索', 'ai_search': 'AI搜索', 'news_comment': '评论', 'share': '分享', 'favorite': '收藏', 'like': '点赞' }
    let data = []
    if (Array.isArray(eventData) && eventData.length > 0) {
      data = eventData.map(d => ({
        name: typeMap[d.event_type] || d.event_type || '其他',
        value: d.cnt || d.value || 0
      })).filter(d => d.value > 0).sort((a, b) => b.value - a.value)
    }

    if (data.length < 3) {
      data = [
        { name: '浏览', value: overview.totalViews || 0 },
        { name: '行为', value: overview.totalBehaviors || 0 },
        { name: '阅读记录', value: overview.totalReadHistory || 0 },
        { name: '用户', value: overview.totalUsers || 0 }
      ].filter(d => d.value > 0)
    }

    if (data.length === 0) {
      funnelChart.setOption({
        title: { text: '暂无数据', left: 'center', top: 'center', textStyle: { color: '#94a3b8', fontSize: 14, fontFamily: chartTheme.fontFamily } }
      })
      return
    }
    const colors = ['#2F6BFF', '#7C3AED', '#10B981', '#F59E0B', '#EF4444', '#EC4899']
    funnelChart.setOption({
      backgroundColor: 'transparent',
      tooltip: { trigger: 'item', backgroundColor: chartTheme.tipBg, borderColor: '#334155', textStyle: { color: chartTheme.tipText, fontSize: 12, fontFamily: chartTheme.fontFamily }, formatter: (p) => `${p.name}: ${formatNumber(p.value)}` },
      series: [{ type: 'funnel', left: '10%', top: 10, bottom: 10, width: '65%', min: 0, max: 100, minSize: '10%', maxSize: '100%', sort: 'descending', gap: 4, label: { show: true, position: 'inside', color: '#fff', fontSize: 11, fontWeight: 500, fontFamily: chartTheme.fontFamily, formatter: (p) => `${p.name}\n${formatNumber(p.value)}` }, labelLine: { show: false }, itemStyle: { borderColor: '#fff', borderWidth: 1 }, data: data.map((item, i) => ({ name: item.name, value: item.value, itemStyle: { color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [{ offset: 0, color: colors[i % colors.length] || colors[0] }, { offset: 1, color: (colors[i % colors.length] || colors[0]) + '88' }]) } })) }]
    })
  } catch (e) { console.error(e) }
}

const initRegionChart = async () => {
  if (!regionChartRef.value) return
  if (regionChart) regionChart.dispose()
  regionChart = echarts.init(regionChartRef.value)
  try {
    const res = await analyticsApi.getEventDist(timeRange.value)
    let data = res.data || res
    if (!Array.isArray(data)) data = []
    const typeMap = { 'page_view': '页面浏览', 'news_click': '新闻点击', 'news_read': '新闻阅读', 'news_comment': '新闻评论', 'search': '搜索', 'ai_search': 'AI搜索', 'share': '分享', 'favorite': '收藏', 'like': '点赞' }
    const mappedData = data.map(d => ({ name: typeMap[d.event_type] || d.event_type || d.name || '其他', value: d.cnt || d.value || 0 })).filter(d => d.value > 0)
    if (mappedData.length === 0) {
      regionChart.setOption({
        title: { text: '暂无数据', left: 'center', top: 'center', textStyle: { color: '#94a3b8', fontSize: 14, fontFamily: chartTheme.fontFamily } }
      })
      return
    }
    const colors = ['#2F6BFF', '#7C3AED', '#10B981', '#F59E0B', '#EF4444', '#EC4899', '#06B6D4', '#8B5CF6']
    regionChart.setOption({
      backgroundColor: 'transparent',
      tooltip: { trigger: 'item', backgroundColor: chartTheme.tipBg, borderColor: '#334155', textStyle: { color: chartTheme.tipText, fontSize: 12, fontFamily: chartTheme.fontFamily }, formatter: '{b}: {c}次 ({d}%)' },
      legend: { orient: 'vertical', right: 8, top: 'center', textStyle: { color: '#475569', fontSize: 11, fontFamily: chartTheme.fontFamily }, itemWidth: 10, itemHeight: 10, itemGap: 8 },
      series: [{ type: 'pie', radius: ['35%', '65%'], center: ['35%', '50%'], avoidLabelOverlap: false, label: { show: false }, emphasis: { label: { show: true, fontSize: 12, fontWeight: 'bold' } }, labelLine: { show: false }, data: mappedData.map((item, i) => ({ ...item, itemStyle: { color: colors[i % colors.length] } })), itemStyle: { borderColor: '#fff', borderWidth: 2 } }]
    })
  } catch (e) { console.error(e) }
}

const initSourceChart = async () => {
  if (!sourceChartRef.value) return
  if (sourceChart) sourceChart.dispose()
  sourceChart = echarts.init(sourceChartRef.value)
  try {
    const res = await analyticsApi.getNewsList(1, 500)
    const json = res.data || res
    const records = json?.data || json?.records || json?.data?.records || []
    const sourceMap = {}
    records.forEach(item => {
      const src = item.source || '未知来源'
      sourceMap[src] = (sourceMap[src] || 0) + 1
    })
    let data = Object.entries(sourceMap).map(([name, value]) => ({ name, value }))
      .sort((a, b) => b.value - a.value).slice(0, 10)
    if (data.length === 0) {
      sourceChart.setOption({
        title: { text: '暂无数据', left: 'center', top: 'center', textStyle: { color: '#94a3b8', fontSize: 14, fontFamily: chartTheme.fontFamily } }
      })
      return
    }
    const colors = ['#2F6BFF', '#7C3AED', '#10B981', '#F59E0B', '#EF4444', '#EC4899', '#06B6D4', '#8B5CF6', '#F97316', '#14B8A6']
    sourceChart.setOption({
      backgroundColor: 'transparent',
      tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' }, backgroundColor: chartTheme.tipBg, borderColor: '#334155', textStyle: { color: chartTheme.tipText, fontSize: 12, fontFamily: chartTheme.fontFamily } },
      grid: { left: 8, right: 40, bottom: 4, top: 4, containLabel: true },
      xAxis: { type: 'value', axisLine: { show: false }, axisLabel: { color: chartTheme.textColor, fontSize: 10, fontFamily: chartTheme.fontFamily }, splitLine: { lineStyle: { color: chartTheme.splitColor } } },
      yAxis: { type: 'category', data: data.map(d => d.name).reverse(), axisLine: { show: false }, axisTick: { show: false }, axisLabel: { color: '#475569', fontSize: 11, fontFamily: chartTheme.fontFamily } },
      series: [{ type: 'bar', data: data.map((d, i) => ({ value: d.value, itemStyle: { color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [{ offset: 0, color: colors[i % colors.length] }, { offset: 1, color: (colors[i % colors.length]) + '88' }]), borderRadius: [0, 4, 4, 0] } })).reverse(), barWidth: 14 }]
    })
  } catch (e) { console.error(e) }
}

const initActiveHourChart = async () => {
  if (!activeHourChartRef.value) return
  if (activeHourChart) activeHourChart.dispose()
  activeHourChart = echarts.init(activeHourChartRef.value)
  try {
    let hourData = []
    try {
      const res = await analyticsApi.getTrend('today')
      const data = res.data || res
      if (Array.isArray(data) && data.length > 0) {
        hourData = data.map(d => ({ hour: d.hour, value: (d.pv || 0) + (d.uv || 0) }))
      }
    } catch (e) { /* ignore */ }
    if (hourData.length === 0) {
      activeHourChart.setOption({
        title: { text: '暂无数据', left: 'center', top: 'center', textStyle: { color: '#94a3b8', fontSize: 14, fontFamily: chartTheme.fontFamily } }
      })
      return
    }
    const maxVal = Math.max(...hourData.map(d => d.value))
    activeHourChart.setOption({
      backgroundColor: 'transparent',
      tooltip: { trigger: 'axis', backgroundColor: chartTheme.tipBg, borderColor: '#334155', textStyle: { color: chartTheme.tipText, fontSize: 12, fontFamily: chartTheme.fontFamily }, formatter: (p) => `${p[0].name}<br/>活跃度: ${p[0].value}` },
      grid: { left: 40, right: 16, bottom: 24, top: 16 },
      xAxis: { type: 'category', data: hourData.map(d => d.hour), axisLine: { lineStyle: { color: '#E2E8F0' } }, axisLabel: { color: chartTheme.textColor, fontSize: 9, fontFamily: chartTheme.fontFamily, interval: 2 }, axisTick: { show: false } },
      yAxis: { type: 'value', axisLine: { show: false }, axisLabel: { show: false }, splitLine: { show: false } },
      series: [{
        type: 'bar',
        data: hourData.map(d => ({
          value: d.value,
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: d.value > maxVal * 0.7 ? '#2F6BFF' : d.value > maxVal * 0.4 ? '#60A5FA' : '#BFDBFE' },
              { offset: 1, color: d.value > maxVal * 0.7 ? 'rgba(47,107,255,0.3)' : d.value > maxVal * 0.4 ? 'rgba(96,165,250,0.2)' : 'rgba(191,219,254,0.1)' }
            ]),
            borderRadius: [3, 3, 0, 0]
          }
        })),
        barWidth: '60%'
      }]
    })
  } catch (e) { console.error(e) }
}

const initPublishTrendChart = async () => {
  if (!publishTrendChartRef.value) return
  if (publishTrendChart) publishTrendChart.dispose()
  publishTrendChart = echarts.init(publishTrendChartRef.value)
  try {
    const res = await analyticsApi.getNewsList(1, 500)
    const json = res.data || res
    const records = json?.data || json?.records || json?.data?.records || []
    const dayMap = {}
    const now = new Date()
    for (let i = 6; i >= 0; i--) {
      const d = new Date(now)
      d.setDate(d.getDate() - i)
      const key = `${d.getMonth() + 1}/${d.getDate()}`
      dayMap[key] = 0
    }
    records.forEach(item => {
      if (item.publishTime) {
        const d = new Date(item.publishTime)
        const key = `${d.getMonth() + 1}/${d.getDate()}`
        if (key in dayMap) dayMap[key] = (dayMap[key] || 0) + 1
      }
    })
    const dates = Object.keys(dayMap)
    const values = Object.values(dayMap)
    publishTrendChart.setOption({
      backgroundColor: 'transparent',
      tooltip: { trigger: 'axis', backgroundColor: chartTheme.tipBg, borderColor: '#334155', textStyle: { color: chartTheme.tipText, fontSize: 12, fontFamily: chartTheme.fontFamily } },
      grid: { left: 40, right: 16, bottom: 24, top: 16 },
      xAxis: { type: 'category', data: dates, axisLine: { lineStyle: { color: '#E2E8F0' } }, axisLabel: { color: chartTheme.textColor, fontSize: 10, fontFamily: chartTheme.fontFamily }, axisTick: { show: false } },
      yAxis: { type: 'value', axisLine: { show: false }, axisLabel: { color: chartTheme.textColor, fontSize: 10, fontFamily: chartTheme.fontFamily }, splitLine: { lineStyle: { color: chartTheme.splitColor } } },
      series: [{
        type: 'line', smooth: true, data: values, symbol: 'circle', symbolSize: 6,
        lineStyle: { color: '#10B981', width: 2.5 },
        itemStyle: { color: '#10B981', borderColor: '#fff', borderWidth: 2 },
        areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: 'rgba(16,185,129,0.2)' }, { offset: 1, color: 'rgba(16,185,129,0)' }]) }
      }]
    })
  } catch (e) { console.error(e) }
}

// 实时行为：加载行为事件
const loadBehaviorEvents = async () => {
  try {
    const [realtimeRes, trendRes] = await Promise.all([
      analyticsApi.getRealtimeStats('today'),
      analyticsApi.getTrend('today')
    ])
    const d = (realtimeRes.data || realtimeRes) || {}
    // 更新统计卡片
    behaviorStats.value = [
      { label: '今日UV', value: formatNumber(d.todayUsers || d.totalUsers), color: '#2F6BFF' },
      { label: '今日PV', value: formatNumber(d.totalViews), color: '#7C3AED' },
      { label: '在线用户', value: formatNumber(d.totalUsers), color: '#10B981' },
      { label: '平均时长', value: formatDuration(d.avgDuration || d.avgReadTime), color: '#F59E0B' }
    ]
    // 从 ClickHouse 查询真实行为事件
    try {
      const sql = encodeURIComponent('SELECT event_time, user_id, event_type, target_id, target_type FROM bigdata_portal.behavior_events ORDER BY event_time DESC LIMIT 30 FORMAT JSON')
      const chRes = await fetch(`/ch-query/?query=${sql}`)
      if (chRes.ok) {
        const chData = await chRes.json()
        const rows = chData.data || []
        const newEvents = rows.map(row => {
          const mapped = eventTypeMap[row.event_type] || { action: row.event_type, typeClass: 'type-other' }
          const time = new Date(row.event_time).toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit', second: '2-digit', hour12: false })
          return {
            time,
            userId: String(row.user_id).slice(-6),
            action: mapped.action,
            typeClass: mapped.typeClass,
            target: row.target_type || ''
          }
        })
        behaviorEvents.value = newEvents
      }
    } catch (e) {
      // ClickHouse不可用时，从MySQL查询
      try {
        const mysqlRes = await fetch('/api/analytics/realtime?range=today')
        const mysqlData = await mysqlRes.json()
        // 仅显示统计信息，不生成模拟事件
        if (behaviorEvents.value.length === 0) {
          behaviorEvents.value = [{ time: '--:--:--', userId: '-', action: '暂无行为数据', typeClass: 'type-other', target: '' }]
        }
      } catch (e2) { /* ignore */ }
    }
  } catch (e) { console.error(e) }
}

// 实时行为：初始化图表
const initBehaviorCharts = async () => {
  await nextTick()
  // 行为类型分布饼图
  if (behaviorDistChartRef.value) {
    if (behaviorDistChart) behaviorDistChart.dispose()
    behaviorDistChart = echarts.init(behaviorDistChartRef.value)
    try {
      const res = await analyticsApi.getEventDist('today')
      let data = res.data || res
      if (!Array.isArray(data)) data = []
      const typeMap = { 'page_view': '页面浏览', 'news_click': '新闻点击', 'news_read': '新闻阅读', 'news_comment': '新闻评论', 'search': '搜索', 'ai_search': 'AI搜索', 'share': '分享', 'favorite': '收藏', 'like': '点赞' }
      const mappedData = data.map(d => ({ name: typeMap[d.event_type] || d.event_type || '其他', value: d.cnt || d.value || 0 })).filter(d => d.value > 0)
      if (mappedData.length === 0) {
        behaviorDistChart.setOption({
          title: { text: '暂无数据', left: 'center', top: 'center', textStyle: { color: '#94a3b8', fontSize: 14, fontFamily: chartTheme.fontFamily } }
        })
      } else {
        const colors = ['#2F6BFF', '#7C3AED', '#10B981', '#F59E0B', '#EF4444', '#EC4899', '#06B6D4', '#8B5CF6']
        behaviorDistChart.setOption({
          backgroundColor: 'transparent',
          tooltip: { trigger: 'item', backgroundColor: chartTheme.tipBg, borderColor: '#334155', textStyle: { color: chartTheme.tipText, fontSize: 12, fontFamily: chartTheme.fontFamily }, formatter: '{b}: {c}次 ({d}%)' },
          legend: { orient: 'vertical', right: 8, top: 'center', textStyle: { color: '#475569', fontSize: 11, fontFamily: chartTheme.fontFamily }, itemWidth: 10, itemHeight: 10, itemGap: 8 },
          series: [{ type: 'pie', radius: ['35%', '65%'], center: ['35%', '50%'], avoidLabelOverlap: false, label: { show: false }, emphasis: { label: { show: true, fontSize: 12, fontWeight: 'bold' } }, labelLine: { show: false }, data: mappedData.map((item, i) => ({ ...item, itemStyle: { color: colors[i % colors.length] } })), itemStyle: { borderColor: '#fff', borderWidth: 2 } }]
        })
      }
    } catch (e) { console.error(e) }
  }
  // 行为趋势折线图
  if (behaviorTrendChartRef.value) {
    if (behaviorTrendChart) behaviorTrendChart.dispose()
    behaviorTrendChart = echarts.init(behaviorTrendChartRef.value)
    try {
      const res = await analyticsApi.getTrend('today')
      const data = res.data || res
      if (!Array.isArray(data) || data.length === 0) {
        behaviorTrendChart.setOption({
          title: { text: '暂无数据', left: 'center', top: 'center', textStyle: { color: '#94a3b8', fontSize: 14, fontFamily: chartTheme.fontFamily } }
        })
      } else {
        behaviorTrendChart.setOption({
          backgroundColor: 'transparent',
          tooltip: { trigger: 'axis', backgroundColor: chartTheme.tipBg, borderColor: '#334155', textStyle: { color: chartTheme.tipText, fontSize: 12, fontFamily: chartTheme.fontFamily } },
          legend: { data: ['UV', 'PV'], textStyle: { color: chartTheme.textColor, fontSize: 11, fontFamily: chartTheme.fontFamily }, top: 0, right: 0 },
          grid: { left: 40, right: 16, bottom: 24, top: 32 },
          xAxis: { type: 'category', data: data.map(d => d.hour), axisLine: { lineStyle: { color: '#E2E8F0' } }, axisLabel: { color: chartTheme.textColor, fontSize: 10, fontFamily: chartTheme.fontFamily }, axisTick: { show: false } },
          yAxis: { type: 'value', axisLine: { show: false }, axisLabel: { color: chartTheme.textColor, fontSize: 10, fontFamily: chartTheme.fontFamily }, splitLine: { lineStyle: { color: chartTheme.splitColor } } },
          series: [
            { name: 'UV', type: 'line', smooth: true, data: data.map(d => d.uv), symbol: 'none', lineStyle: { color: '#2F6BFF', width: 2.5 }, areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: 'rgba(47,107,255,0.18)' }, { offset: 1, color: 'rgba(47,107,255,0)' }]) } },
            { name: 'PV', type: 'line', smooth: true, data: data.map(d => d.pv), symbol: 'none', lineStyle: { color: '#7C3AED', width: 2.5 }, areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: 'rgba(124,58,237,0.18)' }, { offset: 1, color: 'rgba(124,58,237,0)' }]) } }
          ]
        })
      }
    } catch (e) { console.error(e) }
  }
}

const handleResize = () => { trendChart?.resize(); hotNewsChart?.resize(); channelChart?.resize(); funnelChart?.resize(); regionChart?.resize(); sourceChart?.resize(); activeHourChart?.resize(); publishTrendChart?.resize(); behaviorDistChart?.resize(); behaviorTrendChart?.resize() }

const loadNews = async (page) => {
  if (page) newsPage.value = page
  try {
    const params = new URLSearchParams({ page: newsPage.value, size: 20 })
    if (newsChannel.value) params.set('channel', newsChannel.value)
    if (newsKeyword.value) params.set('keyword', newsKeyword.value)
    const res = await fetch(`/api/news?${params}`).then(r => r.json())
    if (res.success && res.data) {
      const pageData = res.data
      newsList.value = pageData.data || pageData.records || []
      newsTotal.value = pageData.total || 0
    }
  } catch (e) { console.error(e) }
}
const loadChannels = async () => { try { const res = await fetch('/api/news/channels').then(r => r.json()); const d = res.data || {}; channels.value = Array.isArray(d) ? d.map(item => item.channel || item) : Object.keys(d) } catch (e) { console.error(e) } }

const loadOverviewData = async () => {
  await updateRealtimeStats()
  await initCharts()
}

let statsInterval = null, timeInterval = null, hotNewsInterval = null
const initCharts = async () => { await nextTick(); await initTrendChart(); await initHotNewsChart(); await initChannelChart(); await initFunnelChart(); await initRegionChart(); await initSourceChart(); await initActiveHourChart(); await initPublishTrendChart() }
watch(activeTab, async (val) => {
  if (val === 'overview') { await nextTick(); initCharts() }
  if (val === 'behavior') {
    loadBehaviorEvents()
    initBehaviorCharts()
  }
})
watch(timeRange, () => {
  loadOverviewData()
})
onMounted(() => {
  updateTime(); timeInterval = setInterval(updateTime, 1000); updateRealtimeStats(); statsInterval = setInterval(updateRealtimeStats, 10000); initCharts(); loadChannels(); hotNewsInterval = setInterval(() => { initHotNewsChart(); initTrendChart() }, 30000);
  // 实时行为定时刷新
  behaviorInterval = setInterval(() => {
    if (activeTab.value === 'behavior') loadBehaviorEvents()
  }, 5000)
  window.addEventListener('resize', handleResize)
})
onUnmounted(() => {
  if (statsInterval) clearInterval(statsInterval); if (timeInterval) clearInterval(timeInterval); if (hotNewsInterval) clearInterval(hotNewsInterval); if (behaviorInterval) clearInterval(behaviorInterval)
  trendChart?.dispose(); hotNewsChart?.dispose(); channelChart?.dispose(); funnelChart?.dispose(); regionChart?.dispose(); sourceChart?.dispose(); activeHourChart?.dispose(); publishTrendChart?.dispose(); behaviorDistChart?.dispose(); behaviorTrendChart?.dispose()
  window.removeEventListener('resize', handleResize)
})

const goHome = () => {
  const homeUrl = window.__SERVER_URL__ || window.HOME_URL || '/'
  window.location.href = homeUrl
}
</script>

<style scoped>
@import url('https://fonts.googleapis.cn/css2?family=DM+Sans:wght@400;500;600;700&family=Noto+Sans+SC:wght@400;500;600;700&display=swap');

.dashboard {
  width: 100vw;
  height: 100vh;
  background: #F8FAFC;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  font-family: 'DM Sans', 'Noto Sans SC', -apple-system, BlinkMacSystemFont, sans-serif;
  color: #1E293B;
}

.top-bar {
  background: linear-gradient(135deg, #0F172A 0%, #1E293B 100%);
  border-bottom: 1px solid rgba(255,255,255,0.08);
  flex-shrink: 0;
  height: 56px;
  display: flex;
  align-items: center;
  box-shadow: 0 4px 20px rgba(0,0,0,0.15);
  position: relative;
  z-index: 10;
}

.bar-inner {
  width: 100%;
  max-width: 1440px;
  margin: 0 auto;
  padding: 0 28px;
  display: flex;
  align-items: center;
  gap: 32px;
}

.brand {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-shrink: 0;
}

.brand-icon {
  width: 34px;
  height: 34px;
}

.brand-name {
  font-size: 20px;
  font-weight: 800;
  color: #F8FAFC;
  letter-spacing: 1px;
}

.brand-sub {
  font-size: 11px;
  color: #94A3B8;
  background: rgba(255,255,255,0.08);
  padding: 3px 10px;
  border-radius: 10px;
  font-weight: 500;
}

.main-nav {
  display: flex;
  gap: 4px;
  flex: 1;
}

.nav-link {
  padding: 8px 18px;
  border: none;
  background: transparent;
  color: #94A3B8;
  font-size: 13.5px;
  font-weight: 500;
  cursor: pointer;
  border-radius: 8px;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 6px;
}

.nav-link:hover { background: rgba(255,255,255,0.08); color: #E2E8F0; }
.nav-link.active { background: rgba(47,107,255,0.2); color: #60A5FA; font-weight: 600; }
.nav-icon {
  width: 18px;
  height: 18px;
  flex-shrink: 0;
}

.home-icon {
  width: 16px;
  height: 16px;
  flex-shrink: 0;
}

.bar-right {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-shrink: 0;
}

.home-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 14px;
  background: rgba(255,255,255,0.08);
  border: 1px solid rgba(255,255,255,0.15);
  border-radius: 6px;
  color: #94A3B8;
  font-size: 12px;
  font-weight: 500;
  text-decoration: none;
  transition: all 0.2s;
}
.home-btn:hover {
  background: rgba(255,255,255,0.12);
  color: #fff;
  border-color: rgba(255,255,255,0.25);
}

.live-tag {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 11px;
  font-weight: 700;
  color: #EF4444;
  letter-spacing: 0.5px;
}

.dot {
  width: 7px;
  height: 7px;
  background: #EF4444;
  border-radius: 50%;
  animation: blink 1.5s ease-in-out infinite;
}

@keyframes blink { 0%,100% { opacity: 1; } 50% { opacity: 0.3; } }

.clock {
  font-size: 12px;
  color: #94A3B8;
  font-family: 'DM Sans', monospace;
  font-weight: 500;
  background: rgba(255,255,255,0.06);
  padding: 4px 12px;
  border-radius: 6px;
}

.page-body {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  overflow-x: hidden;
}

.page-body::-webkit-scrollbar { width: 6px; }
.page-body::-webkit-scrollbar-track { background: transparent; }
.page-body::-webkit-scrollbar-thumb { background: #CBD5E1; border-radius: 3px; }

.overview-page {
  max-width: 1440px;
  margin: 0 auto;
  padding: 24px 28px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.kpi-banner {
  display: grid;
  grid-template-columns: repeat(8, 1fr);
  gap: 12px;
}

.kpi-item {
  background: #fff;
  border: 1px solid #E2E8F0;
  border-radius: 12px;
  padding: 18px 14px;
  text-align: center;
  transition: all 0.2s;
  position: relative;
  overflow: hidden;
}

.kpi-item:hover { box-shadow: 0 4px 16px rgba(0,0,0,0.06); transform: translateY(-1px); }

.kpi-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: var(--kpi-color, #2F6BFF);
  border-radius: 12px 12px 0 0;
}

.kpi-num {
  font-size: 24px;
  font-weight: 700;
  line-height: 1.1;
  letter-spacing: -0.5px;
}

.kpi-label {
  font-size: 11.5px;
  color: #94A3B8;
  margin-top: 6px;
  font-weight: 500;
}

.time-range-bar {
  display: flex;
  gap: 8px;
  margin-bottom: 20px;
}
.range-btn {
  padding: 6px 16px;
  border: 1px solid #E2E8F0;
  border-radius: 6px;
  background: #fff;
  color: #64748B;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
}
.range-btn:hover {
  border-color: #2F6BFF;
  color: #2F6BFF;
}
.range-btn.active {
  background: #2F6BFF;
  color: #fff;
  border-color: #2F6BFF;
}
.overview-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  grid-template-rows: auto auto;
  gap: 16px;
}

.panel {
  background: #fff;
  border: 1px solid #E2E8F0;
  border-radius: 12px;
  padding: 18px;
  display: flex;
  flex-direction: column;
  transition: box-shadow 0.2s;
}

.panel:hover {
  box-shadow: 0 4px 20px rgba(0,0,0,0.05);
}

.panel-trend, .panel-hot { height: 280px; }
.panel-channel { height: 280px; }
.panel-funnel { height: 280px; }
.panel-event { height: 280px; }
.panel-source { height: 280px; }
.panel-active-hour { height: 280px; }
.panel-publish-trend { height: 280px; }

.panel-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
  flex-shrink: 0;
}

.panel-head h2 {
  font-size: 14px;
  font-weight: 600;
  color: #0F172A;
  margin: 0;
}

.panel-tag {
  font-size: 10px;
  font-weight: 600;
  color: #2F6BFF;
  background: #EFF6FF;
  padding: 3px 10px;
  border-radius: 10px;
  letter-spacing: 0.3px;
}

.panel-chart { flex: 1; min-height: 0; }

.manage-page {
  max-width: 1440px;
  margin: 0 auto;
  padding: 24px 28px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  max-width: 900px;
  margin: 20px auto;
}
.stat-card {
  background: rgba(30,41,59,0.6);
  border: 1px solid rgba(255,255,255,0.08);
  border-radius: 16px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  transition: transform 0.2s, box-shadow 0.2s;
}
.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(0,0,0,0.3);
}
.stat-icon {
  width: 52px;
  height: 52px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  flex-shrink: 0;
}
.stat-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #F1F5F9;
  font-family: 'DM Sans', sans-serif;
}
.stat-label {
  font-size: 13px;
  color: #94A3B8;
}

.search-bar {
  display: flex;
  gap: 10px;
  align-items: center;
  flex-wrap: wrap;
}

.search-input {
  flex: 1;
  max-width: 380px;
  padding: 9px 16px;
  border: 1px solid #E2E8F0;
  border-radius: 8px;
  font-size: 13px;
  outline: none;
  background: #fff;
  color: #1E293B;
  transition: border-color 0.2s;
}

.search-input:focus { border-color: #2F6BFF; box-shadow: 0 0 0 3px rgba(47,107,255,0.1); }
.search-input::placeholder { color: #94A3B8; }

.search-select {
  padding: 9px 16px;
  border: 1px solid #E2E8F0;
  border-radius: 8px;
  font-size: 13px;
  background: #fff;
  color: #1E293B;
  outline: none;
  cursor: pointer;
}

.search-select option { background: #fff; }

.btn-primary {
  padding: 9px 22px;
  background: #2F6BFF;
  color: #fff;
  border: none;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-primary:hover { background: #1E5AE0; box-shadow: 0 2px 8px rgba(47,107,255,0.3); }

.btn-ghost {
  padding: 9px 22px;
  background: transparent;
  color: #64748B;
  border: 1px solid #E2E8F0;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
}

.btn-ghost:hover { border-color: #CBD5E1; background: #F8FAFC; }

.result-count { font-size: 13px; color: #64748B; font-weight: 500; }

.table-card {
  background: #fff;
  border: 1px solid #E2E8F0;
  border-radius: 12px;
  overflow: hidden;
  flex: 1;
  min-height: 0;
  overflow-y: auto;
}

.table-card::-webkit-scrollbar { width: 6px; }
.table-card::-webkit-scrollbar-track { background: transparent; }
.table-card::-webkit-scrollbar-thumb { background: #CBD5E1; border-radius: 3px; }

.news-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;
}

.news-table th {
  background: #F8FAFC;
  color: #64748B;
  padding: 11px 16px;
  text-align: left;
  font-weight: 600;
  font-size: 11.5px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  border-bottom: 1px solid #E2E8F0;
  position: sticky;
  top: 0;
  z-index: 1;
}

.news-table td {
  padding: 12px 16px;
  color: #334155;
  border-bottom: 1px solid #F1F5F9;
}

.news-table tr:hover td { background: #FAFBFE; }

.col-id { width: 55px; }
.col-title { max-width: 340px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.col-ch { width: 80px; }
.col-src { width: 100px; }
.col-num { width: 65px; text-align: right; }
.col-time { width: 120px; }

.ch-tag {
  display: inline-block;
  padding: 2px 10px;
  background: #EFF6FF;
  border-radius: 10px;
  color: #2F6BFF;
  font-size: 11px;
  font-weight: 600;
}

.toast {
  position: fixed;
  top: 64px;
  left: 50%;
  transform: translateX(-50%);
  padding: 8px 20px;
  border-radius: 6px;
  font-size: 13px;
  font-weight: 500;
  z-index: 9999;
  pointer-events: none;
  animation: fadeInDown 0.2s ease;
}
.toast.success { background: #10B981; color: #fff; }
.toast.error { background: #EF4444; color: #fff; }
@keyframes fadeInDown { from { opacity: 0; transform: translateX(-50%) translateY(-8px); } to { opacity: 1; transform: translateX(-50%) translateY(0); } }

.empty-row { text-align: center; color: #94A3B8; padding: 60px 0 !important; font-size: 14px; }

.pager {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  padding: 14px 0;
  font-size: 13px;
  color: #64748B;
  flex-wrap: wrap;
}

.pager button {
  padding: 7px 18px;
  background: #fff;
  border: 1px solid #E2E8F0;
  border-radius: 8px;
  color: #334155;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.15s;
}

.pager button:hover:not(:disabled) { border-color: #2F6BFF; color: #2F6BFF; }
.pager button:disabled { opacity: 0.4; cursor: not-allowed; }

/* 实时行为页面样式 */
.behavior-page {
  max-width: 1440px;
  margin: 0 auto;
  padding: 24px 28px;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.behavior-layout {
  display: flex;
  gap: 16px;
  flex: 1;
  min-height: 0;
}

.behavior-left {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
  min-width: 0;
}

.behavior-right {
  width: 420px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.behavior-stats {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
}

.behavior-stat-card {
  background: #fff;
  border: 1px solid #E2E8F0;
  border-radius: 12px;
  padding: 16px 14px;
  text-align: center;
  transition: all 0.2s;
  position: relative;
  overflow: hidden;
}

.behavior-stat-card:hover { box-shadow: 0 4px 16px rgba(0,0,0,0.06); transform: translateY(-1px); }

.behavior-stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: var(--kpi-color, #2F6BFF);
  border-radius: 12px 12px 0 0;
}

.behavior-stat-value {
  font-size: 22px;
  font-weight: 700;
  line-height: 1.1;
  letter-spacing: -0.5px;
}

.behavior-stat-label {
  font-size: 11px;
  color: #94A3B8;
  margin-top: 6px;
  font-weight: 500;
}

.behavior-event-panel {
  background: #fff;
  border: 1px solid #E2E8F0;
  border-radius: 12px;
  padding: 18px;
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.behavior-event-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
  flex-shrink: 0;
}

.behavior-event-head h2 {
  font-size: 14px;
  font-weight: 600;
  color: #0F172A;
  margin: 0;
}

.behavior-event-list {
  flex: 1;
  overflow-y: auto;
  min-height: 0;
}

.behavior-event-list::-webkit-scrollbar { width: 4px; }
.behavior-event-list::-webkit-scrollbar-track { background: transparent; }
.behavior-event-list::-webkit-scrollbar-thumb { background: #CBD5E1; border-radius: 2px; }

.behavior-event-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  border-bottom: 1px solid #F1F5F9;
  font-size: 13px;
  color: #475569;
  transition: background 0.15s;
}

.behavior-event-item:hover { background: #FAFBFE; }
.behavior-event-item:last-child { border-bottom: none; }

.behavior-event-time {
  font-family: 'DM Sans', monospace;
  font-size: 11px;
  color: #94A3B8;
  flex-shrink: 0;
  min-width: 70px;
}

.behavior-event-user {
  font-weight: 600;
  color: #334155;
  flex-shrink: 0;
}

.behavior-event-action {
  color: #94A3B8;
  flex-shrink: 0;
}

.behavior-event-type {
  font-weight: 600;
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 10px;
  flex-shrink: 0;
}

.type-view { color: #2F6BFF; background: #EFF6FF; }
.type-click { color: #7C3AED; background: #F5F3FF; }
.type-read { color: #10B981; background: #ECFDF5; }
.type-search { color: #F59E0B; background: #FFFBEB; }
.type-ai { color: #8B5CF6; background: #F5F3FF; }
.type-comment { color: #EC4899; background: #FDF2F8; }
.type-share { color: #06B6D4; background: #ECFEFF; }
.type-fav { color: #EF4444; background: #FEF2F2; }

.behavior-event-empty {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #94A3B8;
  font-size: 14px;
  min-height: 200px;
}

.panel-behavior-dist { height: 280px; }
.panel-behavior-trend { height: 280px; }

@media (max-width: 1200px) {
  .kpi-banner { grid-template-columns: repeat(4, 1fr); }
  .bar-inner { padding: 0 16px; }
  .stats-grid { grid-template-columns: repeat(2, 1fr); }
  .behavior-layout { flex-direction: column; }
  .behavior-right { width: 100%; flex-direction: row; }
  .behavior-right .panel { flex: 1; }
}

@media (max-width: 900px) {
  .kpi-banner { grid-template-columns: repeat(2, 1fr); }
  .overview-grid { grid-template-columns: 1fr; }
  .bar-inner { padding: 0 12px; gap: 16px; }
  .brand-sub { display: none; }
  .live-tag { display: none; }
  .behavior-stats { grid-template-columns: repeat(2, 1fr); }
  .behavior-layout { flex-direction: column; }
  .behavior-right { width: 100%; flex-direction: column; }
}

@media (max-width: 600px) {
  .top-bar { height: auto; min-height: 48px; padding: 6px 0; }
  .bar-inner { flex-wrap: wrap; padding: 0 10px; gap: 8px; }
  .brand { order: 1; gap: 6px; }
  .brand-icon { width: 26px; height: 26px; }
  .brand-name { font-size: 15px; }
  .bar-right { order: 2; margin-left: auto; gap: 8px; }
  .main-nav { order: 3; width: 100%; justify-content: space-between; gap: 2px; }
  .nav-link { padding: 6px 10px; font-size: 11px; gap: 4px; }
  .nav-icon { width: 14px; height: 14px; }
  .home-btn { padding: 4px 8px; font-size: 11px; }
  .home-icon { width: 14px; height: 14px; }
  .clock { font-size: 10px; padding: 2px 8px; }
  .overview-page { padding: 12px 8px; gap: 12px; }
  .manage-page { padding: 12px 8px; }
  .behavior-page { padding: 12px 8px; }
  .kpi-banner { grid-template-columns: repeat(2, 1fr); gap: 8px; }
  .kpi-item { padding: 10px 8px; }
  .kpi-num { font-size: 18px; }
  .kpi-label { font-size: 10px; margin-top: 4px; }
  .time-range-bar { gap: 6px; margin-bottom: 12px; }
  .range-btn { padding: 5px 12px; font-size: 12px; }
  .overview-grid { grid-template-columns: 1fr; gap: 10px; }
  .panel { padding: 12px; }
  .panel-trend, .panel-hot { height: 220px; }
  .panel-channel, .panel-funnel, .panel-event { height: 220px; }
  .panel-source, .panel-active-hour, .panel-publish-trend { height: 220px; }
  .panel-head h2 { font-size: 13px; }
  .search-bar { flex-direction: column; align-items: stretch; gap: 8px; }
  .search-input { max-width: 100%; padding: 8px 12px; }
  .search-select { width: 100%; padding: 8px 12px; }
  .news-table th, .news-table td { padding: 8px 6px; font-size: 11px; }
  .col-title { max-width: 120px; }
  .col-src, .col-time { display: none; }
  .pager { gap: 10px; font-size: 12px; }
  .pager button { padding: 6px 14px; font-size: 12px; }
  .field-row { flex-direction: column; gap: 0; }
  .behavior-stats { grid-template-columns: repeat(2, 1fr); }
  .behavior-stat-card { padding: 10px 8px; }
  .behavior-stat-value { font-size: 16px; }
  .behavior-stat-label { font-size: 10px; }
  .panel-behavior-dist, .panel-behavior-trend { height: 220px; }
}

@media (max-width: 400px) {
  .bar-inner { padding: 0 6px; gap: 4px; }
  .brand-name { font-size: 13px; }
  .nav-link { padding: 4px 8px; font-size: 10px; }
  .kpi-num { font-size: 16px; }
  .kpi-label { font-size: 9px; }
  .overview-page { padding: 8px 6px; gap: 8px; }
  .panel-trend, .panel-hot { height: 180px; }
  .panel-channel, .panel-funnel, .panel-event { height: 180px; }
  .panel-source, .panel-active-hour, .panel-publish-trend { height: 180px; }
}
</style>
