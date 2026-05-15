<template>
  <div class="dashboard">
    <header class="top-bar">
      <div class="bar-inner">
        <div class="brand">
          <span class="brand-icon">◆</span>
          <span class="brand-name">BigData Portal</span>
          <span class="brand-sub">数据管理平台</span>
        </div>
        <nav class="main-nav">
          <button :class="['nav-link', { active: activeTab === 'overview' }]" @click="switchTab('overview')">
            <span class="nav-icon">📊</span>数据总览
          </button>
          <button :class="['nav-link', { active: activeTab === 'news' }]" @click="switchTab('news')">
            <span class="nav-icon">📰</span>新闻管理
          </button>
          <button :class="['nav-link', { active: activeTab === 'users' }]" @click="switchTab('users')">
            <span class="nav-icon">👥</span>用户管理
          </button>
        </nav>
        <div class="bar-right">
          <span class="live-tag"><i class="dot"></i>LIVE</span>
          <span class="clock">{{ currentTime }}</span>
        </div>
      </div>
    </header>

    <main class="page-body">
      <div v-if="activeTab === 'overview'" class="overview-page">
        <section class="kpi-banner">
          <div class="kpi-item" v-for="stat in statsData" :key="stat.label">
            <div class="kpi-num" :style="{ color: stat.color }">{{ stat.value }}</div>
            <div class="kpi-label">{{ stat.label }}</div>
          </div>
        </section>

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
                <th class="col-act">操作</th>
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
                <td class="col-act">
                  <button class="btn-edit" @click="editNews(item)">编辑</button>
                  <button class="btn-del" @click="deleteNews(item.id)">删除</button>
                </td>
              </tr>
              <tr v-if="newsList.length === 0"><td colspan="8" class="empty-row">暂无数据</td></tr>
            </tbody>
          </table>
        </div>
        <div class="pager">
          <button :disabled="newsPage <= 1" @click="loadNews(newsPage - 1)">‹ 上一页</button>
          <span>{{ newsPage }} / {{ newsTotalPages || 1 }} 页 · 共 {{ newsTotal }} 条</span>
          <button :disabled="newsPage >= newsTotalPages" @click="loadNews(newsPage + 1)">下一页 ›</button>
        </div>

        <div v-if="editingNews" class="overlay" @click.self="editingNews = null">
          <div class="edit-modal">
            <h3>编辑新闻</h3>
            <div class="field"><label>标题</label><input v-model="editForm.title" /></div>
            <div class="field"><label>摘要</label><textarea v-model="editForm.summary" rows="3"></textarea></div>
            <div class="field-row">
              <div class="field"><label>频道</label><input v-model="editForm.channel" /></div>
              <div class="field"><label>来源</label><input v-model="editForm.source" /></div>
            </div>
            <div class="field"><label>图片URL</label><input v-model="editForm.imageUrl" /></div>
            <div class="field"><label class="check-label"><input type="checkbox" v-model="editForm.isBreaking" :true-value="1" :false-value="0" /><span>突发新闻</span></label></div>
            <div class="modal-foot">
              <button class="btn-primary" @click="saveNews">保存</button>
              <button class="btn-ghost" @click="editingNews = null">取消</button>
            </div>
          </div>
        </div>
      </div>

      <div v-if="activeTab === 'users'" class="manage-page">
        <div class="search-bar">
          <span class="result-count">共 {{ usersTotal }} 个注册用户</span>
        </div>
        <div class="table-card">
          <table class="news-table">
            <thead>
              <tr>
                <th style="width:60px">ID</th>
                <th style="width:160px">用户名</th>
                <th>邮箱</th>
                <th style="width:160px">注册时间</th>
                <th style="width:100px">操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="item in usersList" :key="item.id">
                <td>{{ item.id }}</td>
                <td>
                  <div class="user-cell">
                    <span class="avatar">{{ (item.username || '?')[0].toUpperCase() }}</span>
                    {{ item.username }}
                  </div>
                </td>
                <td>{{ item.email || '-' }}</td>
                <td>{{ formatDate(item.createdAt) }}</td>
                <td><button class="btn-del" @click="deleteUser(item.id, item.username)">删除</button></td>
              </tr>
              <tr v-if="usersList.length === 0"><td colspan="5" class="empty-row">暂无数据</td></tr>
            </tbody>
          </table>
        </div>
        <div class="pager">
          <button :disabled="usersPage <= 1" @click="loadUsers(usersPage - 1)">‹ 上一页</button>
          <span>{{ usersPage }} / {{ usersTotalPages || 1 }} 页</span>
          <button :disabled="usersPage >= usersTotalPages" @click="loadUsers(usersPage + 1)">下一页 ›</button>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed, nextTick, watch } from 'vue'
import * as echarts from 'echarts'
import { analyticsApi, adminApi } from '../api/index.js'

const activeTab = ref('overview')
const trendChartRef = ref(null)
const hotNewsChartRef = ref(null)
const channelChartRef = ref(null)
const funnelChartRef = ref(null)
const regionChartRef = ref(null)
let trendChart = null, hotNewsChart = null, channelChart = null, funnelChart = null, regionChart = null
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
const editingNews = ref(null)
const editForm = ref({})
const usersList = ref([])
const usersPage = ref(1)
const usersTotal = ref(0)
const usersTotalPages = computed(() => Math.max(1, Math.ceil(usersTotal.value / 20)))

const switchTab = (tab) => {
  activeTab.value = tab
  if (tab === 'news') loadNews(1)
  if (tab === 'users') loadUsers(1)
}

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
  if (!s) return '0s'
  return s >= 60 ? `${Math.floor(s / 60)}m${s % 60}s` : `${s}s`
}

const formatDate = (d) => {
  if (!d) return '-'
  return new Date(d).toLocaleString('zh-CN', { month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
}

const updateRealtimeStats = async () => {
  try {
    const res = await analyticsApi.getRealtimeStats()
    const d = res.data || res
    statsData.value = [
      { label: '今日UV', value: formatNumber(d.todayUV), color: '#2F6BFF' },
      { label: '今日PV', value: formatNumber(d.todayPV), color: '#7C3AED' },
      { label: '实时在线', value: formatNumber(d.onlineUsers), color: '#10B981' },
      { label: '平均阅读时长', value: formatDuration(d.avgDuration), color: '#F59E0B' },
      { label: '总用户数', value: formatNumber(d.totalUsers), color: '#EC4899' },
      { label: '总新闻数', value: formatNumber(d.totalNews), color: '#06B6D4' },
      { label: '今日新增', value: formatNumber(d.todayNews), color: '#8B5CF6' },
      { label: '总浏览量', value: formatNumber(d.totalViews), color: '#F97316' }
    ]
  } catch (e) { console.error(e) }
}

const chartTheme = {
  textColor: '#64748B',
  splitColor: '#F1F5F9',
  tipBg: '#1E293B',
  tipText: '#F8FAFC'
}

const initTrendChart = async () => {
  if (!trendChartRef.value) return
  if (trendChart) trendChart.dispose()
  trendChart = echarts.init(trendChartRef.value)
  try {
    const res = await analyticsApi.getTrend()
    const data = res.data || res
    trendChart.setOption({
      backgroundColor: 'transparent',
      tooltip: { trigger: 'axis', backgroundColor: chartTheme.tipBg, borderColor: '#334155', textStyle: { color: chartTheme.tipText, fontSize: 12 } },
      legend: { data: ['UV', 'PV'], textStyle: { color: chartTheme.textColor, fontSize: 11 }, top: 0, right: 0 },
      grid: { left: 40, right: 16, bottom: 24, top: 32 },
      xAxis: { type: 'category', data: data.map(d => d.hour), axisLine: { lineStyle: { color: '#E2E8F0' } }, axisLabel: { color: chartTheme.textColor, fontSize: 10 }, axisTick: { show: false } },
      yAxis: { type: 'value', axisLine: { show: false }, axisLabel: { color: chartTheme.textColor, fontSize: 10 }, splitLine: { lineStyle: { color: chartTheme.splitColor } } },
      series: [
        { name: 'UV', type: 'line', smooth: true, data: data.map(d => d.uv), symbol: 'none', lineStyle: { color: '#2F6BFF', width: 2.5 }, areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: 'rgba(47,107,255,0.18)' }, { offset: 1, color: 'rgba(47,107,255,0)' }]) } },
        { name: 'PV', type: 'line', smooth: true, data: data.map(d => d.pv), symbol: 'none', lineStyle: { color: '#7C3AED', width: 2.5 }, areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: 'rgba(124,58,237,0.18)' }, { offset: 1, color: 'rgba(124,58,237,0)' }]) } }
      ]
    })
  } catch (e) { console.error(e) }
}

const initHotNewsChart = async () => {
  if (!hotNewsChartRef.value) return
  if (hotNewsChart) hotNewsChart.dispose()
  hotNewsChart = echarts.init(hotNewsChartRef.value)
  try {
    const res = await analyticsApi.getHotNews()
    const data = res.data || res
    hotNewsChart.setOption({
      backgroundColor: 'transparent',
      tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' }, backgroundColor: chartTheme.tipBg, borderColor: '#334155', textStyle: { color: chartTheme.tipText, fontSize: 12 } },
      grid: { left: 8, right: 40, bottom: 4, top: 4, containLabel: true },
      xAxis: { type: 'value', axisLine: { show: false }, axisLabel: { color: chartTheme.textColor, fontSize: 10 }, splitLine: { lineStyle: { color: chartTheme.splitColor } } },
      yAxis: { type: 'category', data: data.map(d => (d.name || '').substring(0, 10)).reverse(), axisLine: { show: false }, axisTick: { show: false }, axisLabel: { color: '#475569', fontSize: 11 } },
      series: [{ type: 'bar', data: data.map(d => d.value).reverse(), itemStyle: { color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [{ offset: 0, color: '#2F6BFF' }, { offset: 1, color: '#7C3AED' }]), borderRadius: [0, 4, 4, 0] }, barWidth: 14 }]
    })
  } catch (e) { console.error(e) }
}

const initChannelChart = async () => {
  if (!channelChartRef.value) return
  if (channelChart) channelChart.dispose()
  channelChart = echarts.init(channelChartRef.value)
  try {
    const res = await analyticsApi.getChannelDist()
    const data = res.data || res
    const colors = ['#2F6BFF', '#7C3AED', '#10B981', '#F59E0B', '#EF4444', '#EC4899', '#06B6D4', '#8B5CF6']
    channelChart.setOption({
      backgroundColor: 'transparent',
      tooltip: { trigger: 'item', backgroundColor: chartTheme.tipBg, borderColor: '#334155', textStyle: { color: chartTheme.tipText, fontSize: 12 }, formatter: '{b}: {c} ({d}%)' },
      legend: { orient: 'vertical', right: 8, top: 'center', textStyle: { color: '#475569', fontSize: 11 }, itemWidth: 10, itemHeight: 10, itemGap: 8 },
      series: [{ type: 'pie', radius: ['38%', '68%'], center: ['35%', '50%'], avoidLabelOverlap: false, label: { show: false }, emphasis: { label: { show: true, fontSize: 12, fontWeight: 'bold' } }, labelLine: { show: false }, data: data.map((item, i) => ({ ...item, itemStyle: { color: colors[i % colors.length] } })), itemStyle: { borderColor: '#fff', borderWidth: 2 } }]
    })
  } catch (e) { console.error(e) }
}

const initFunnelChart = async () => {
  if (!funnelChartRef.value) return
  if (funnelChart) funnelChart.dispose()
  funnelChart = echarts.init(funnelChartRef.value)
  try {
    const res = await analyticsApi.getFunnel()
    const data = res.data || res
    const colors = ['#2F6BFF', '#7C3AED', '#10B981', '#F59E0B', '#EF4444', '#EC4899']
    funnelChart.setOption({
      backgroundColor: 'transparent',
      tooltip: { trigger: 'item', backgroundColor: chartTheme.tipBg, borderColor: '#334155', textStyle: { color: chartTheme.tipText, fontSize: 12 } },
      series: [{ type: 'funnel', left: '10%', top: 10, bottom: 10, width: '65%', min: 0, max: 100, minSize: '10%', maxSize: '100%', sort: 'descending', gap: 4, label: { show: true, position: 'inside', color: '#fff', fontSize: 11, fontWeight: 500 }, labelLine: { show: false }, itemStyle: { borderColor: '#fff', borderWidth: 1 }, data: data.map((item, i) => ({ name: item.name, value: item.value, itemStyle: { color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [{ offset: 0, color: colors[i] || colors[0] }, { offset: 1, color: (colors[i] || colors[0]) + '88' }]) } })) }]
    })
  } catch (e) { console.error(e) }
}

const initRegionChart = async () => {
  if (!regionChartRef.value) return
  if (regionChart) regionChart.dispose()
  regionChart = echarts.init(regionChartRef.value)
  try {
    const res = await analyticsApi.getRegionDist()
    const data = res.data || res
    regionChart.setOption({
      backgroundColor: 'transparent',
      tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' }, backgroundColor: chartTheme.tipBg, borderColor: '#334155', textStyle: { color: chartTheme.tipText, fontSize: 12 } },
      grid: { left: 8, right: 30, bottom: 4, top: 4, containLabel: true },
      xAxis: { type: 'value', axisLine: { show: false }, axisLabel: { color: chartTheme.textColor, fontSize: 10 }, splitLine: { lineStyle: { color: chartTheme.splitColor } } },
      yAxis: { type: 'category', data: data.map(d => d.event_type || d.name || '').reverse(), axisLine: { show: false }, axisTick: { show: false }, axisLabel: { color: '#475569', fontSize: 11 } },
      series: [{ type: 'bar', data: data.map(d => d.cnt || d.value || 0).reverse(), itemStyle: { color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [{ offset: 0, color: '#10B981' }, { offset: 1, color: '#34D399' }]), borderRadius: [0, 4, 4, 0] }, barWidth: 16 }]
    })
  } catch (e) { console.error(e) }
}

const handleResize = () => { trendChart?.resize(); hotNewsChart?.resize(); channelChart?.resize(); funnelChart?.resize(); regionChart?.resize() }

const loadNews = async (page) => {
  if (page) newsPage.value = page
  try { const res = await adminApi.listNews(newsPage.value, 20, newsChannel.value || undefined, newsKeyword.value || undefined); newsList.value = res.data || []; newsTotal.value = res.total || 0 } catch (e) { console.error(e) }
}
const editNews = (item) => { editingNews.value = item.id; editForm.value = { ...item } }
const saveNews = async () => { try { await adminApi.updateNews(editingNews.value, editForm.value); editingNews.value = null; loadNews() } catch (e) { console.error(e) } }
const deleteNews = async (id) => { if (!confirm('确定删除该新闻？')) return; try { await adminApi.deleteNews(id); loadNews() } catch (e) { console.error(e) } }
const loadUsers = async (page) => {
  if (page) usersPage.value = page
  try { const res = await adminApi.listUsers(usersPage.value, 20); usersList.value = res.data || []; usersTotal.value = res.total || 0 } catch (e) { console.error(e) }
}
const deleteUser = async (id, username) => { if (!confirm(`确定删除用户 "${username}"？`)) return; try { await adminApi.deleteUser(id); loadUsers() } catch (e) { console.error(e) } }
const loadChannels = async () => { try { const res = await adminApi.listChannels(); channels.value = (res.data || []).map(d => d.channel) } catch (e) { console.error(e) } }

let statsInterval = null, timeInterval = null
const initCharts = async () => { await nextTick(); initTrendChart(); initHotNewsChart(); initChannelChart(); initFunnelChart(); initRegionChart() }
watch(activeTab, async (val) => { if (val === 'overview') { await nextTick(); initCharts() } })
onMounted(() => { updateTime(); timeInterval = setInterval(updateTime, 1000); updateRealtimeStats(); statsInterval = setInterval(updateRealtimeStats, 10000); initCharts(); loadChannels(); window.addEventListener('resize', handleResize) })
onUnmounted(() => { if (statsInterval) clearInterval(statsInterval); if (timeInterval) clearInterval(timeInterval); trendChart?.dispose(); hotNewsChart?.dispose(); channelChart?.dispose(); funnelChart?.dispose(); regionChart?.dispose(); window.removeEventListener('resize', handleResize) })
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=DM+Sans:wght@400;500;600;700&family=Noto+Sans+SC:wght@400;500;600;700&display=swap');

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
  background: #fff;
  border-bottom: 1px solid #E2E8F0;
  flex-shrink: 0;
  height: 56px;
  display: flex;
  align-items: center;
  box-shadow: 0 1px 3px rgba(0,0,0,0.04);
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
  gap: 8px;
  flex-shrink: 0;
}

.brand-icon {
  color: #2F6BFF;
  font-size: 20px;
  font-weight: 700;
}

.brand-name {
  font-size: 17px;
  font-weight: 700;
  color: #0F172A;
  letter-spacing: -0.3px;
}

.brand-sub {
  font-size: 11px;
  color: #94A3B8;
  background: #F1F5F9;
  padding: 2px 8px;
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
  color: #64748B;
  font-size: 13.5px;
  font-weight: 500;
  cursor: pointer;
  border-radius: 8px;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 6px;
}

.nav-link:hover { background: #F1F5F9; color: #334155; }
.nav-link.active { background: #EFF6FF; color: #2F6BFF; font-weight: 600; }
.nav-icon { font-size: 14px; }

.bar-right {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-shrink: 0;
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
}

.kpi-item:hover { box-shadow: 0 4px 16px rgba(0,0,0,0.06); transform: translateY(-1px); }

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
}

.panel-trend, .panel-hot { height: 280px; }
.panel-channel { height: 280px; }
.panel-funnel { height: 280px; }
.panel-event { height: 280px; }

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

.search-bar {
  display: flex;
  gap: 10px;
  align-items: center;
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
.col-act { width: 120px; white-space: nowrap; }

.ch-tag {
  display: inline-block;
  padding: 2px 10px;
  background: #EFF6FF;
  border-radius: 10px;
  color: #2F6BFF;
  font-size: 11px;
  font-weight: 600;
}

.user-cell { display: flex; align-items: center; gap: 8px; }

.avatar {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: linear-gradient(135deg, #2F6BFF, #7C3AED);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 700;
  flex-shrink: 0;
}

.btn-edit {
  padding: 5px 14px;
  background: #EFF6FF;
  color: #2F6BFF;
  border: none;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  margin-right: 6px;
  transition: all 0.15s;
}

.btn-edit:hover { background: #DBEAFE; }

.btn-del {
  padding: 5px 14px;
  background: #FEF2F2;
  color: #EF4444;
  border: none;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.15s;
}

.btn-del:hover { background: #FEE2E2; }

.empty-row { text-align: center; color: #94A3B8; padding: 60px 0 !important; font-size: 14px; }

.pager {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  padding: 14px 0;
  font-size: 13px;
  color: #64748B;
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

.overlay {
  position: fixed;
  inset: 0;
  background: rgba(15,23,42,0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  backdrop-filter: blur(4px);
}

.edit-modal {
  background: #fff;
  border-radius: 16px;
  padding: 28px;
  width: 520px;
  max-height: 80vh;
  overflow-y: auto;
  box-shadow: 0 20px 60px rgba(0,0,0,0.15);
}

.edit-modal h3 {
  font-size: 17px;
  font-weight: 700;
  color: #0F172A;
  margin: 0 0 20px 0;
}

.field { margin-bottom: 14px; }
.field label { display: block; font-size: 12px; color: #64748B; margin-bottom: 5px; font-weight: 600; }

.field input, .field textarea {
  width: 100%;
  padding: 9px 12px;
  border: 1px solid #E2E8F0;
  border-radius: 8px;
  font-size: 13px;
  outline: none;
  color: #1E293B;
  transition: border-color 0.2s;
  box-sizing: border-box;
  font-family: inherit;
}

.field input:focus, .field textarea:focus { border-color: #2F6BFF; box-shadow: 0 0 0 3px rgba(47,107,255,0.08); }
.field textarea { resize: vertical; }

.field-row { display: flex; gap: 14px; }
.field-row .field { flex: 1; }

.check-label {
  display: flex !important;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: #334155 !important;
  font-size: 13px !important;
}

.check-label input[type="checkbox"] { width: 16px; height: 16px; accent-color: #2F6BFF; cursor: pointer; }

.modal-foot { display: flex; gap: 10px; margin-top: 20px; justify-content: flex-end; }

@media (max-width: 1200px) {
  .kpi-banner { grid-template-columns: repeat(4, 1fr); }
}

@media (max-width: 900px) {
  .kpi-banner { grid-template-columns: repeat(2, 1fr); }
  .overview-grid { grid-template-columns: 1fr; }
}
</style>
