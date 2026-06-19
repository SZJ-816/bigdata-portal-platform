<template>
  <div class="dashboard">
    <header class="top-bar">
      <div class="bar-inner">
        <div class="brand" @click="goHome" style="cursor:pointer">
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
          <button :class="['nav-link', { active: activeTab === 'ai' }]" @click="switchTab('ai')">
            <svg class="nav-icon" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <circle cx="12" cy="12" r="3"/>
              <path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z"/>
            </svg>AI 控制台
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
          <div class="kpi-item" v-for="stat in statsData" :key="stat.label">
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

      <div v-if="activeTab === 'ai'" class="ai-page">
        <div class="ai-layout">
          <aside class="ai-sidebar">
            <div class="ai-sidebar-title">AI 功能</div>
            <button :class="['ai-fn-btn', { active: aiMode === 'search' }]" @click="aiMode = 'search'">
              <span class="ai-fn-icon">🔍</span>智能搜索
            </button>
            <button :class="['ai-fn-btn', { active: aiMode === 'summary' }]" @click="aiMode = 'summary'">
              <span class="ai-fn-icon">📋</span>热点总结
            </button>
            <button :class="['ai-fn-btn', { active: aiMode === 'translate' }]" @click="aiMode = 'translate'">
              <span class="ai-fn-icon">🌐</span>智能翻译
            </button>
            <button :class="['ai-fn-btn', { active: aiMode === 'terminal' }]" @click="aiMode = 'terminal'">
              <span class="ai-fn-icon">⌨️</span>命令终端
            </button>
            <div class="ai-sidebar-footer">
              <div class="ai-status-dot" :class="{ online: aiOnline }"></div>
              <span>{{ aiOnline ? 'AI 在线' : 'AI 离线' }}</span>
            </div>
          </aside>
          <div class="ai-main">
            <div v-if="aiMode === 'search'" class="ai-panel">
              <div class="ai-panel-head">
                <h2>🔍 智能搜索</h2>
                <span class="ai-badge">NVIDIA Llama 3.1</span>
              </div>
              <div class="ai-input-row">
                <input v-model="aiSearchKeyword" class="ai-input" placeholder="输入关键词搜索新闻..." @keyup.enter="doAiSearch" :disabled="aiLoading" />
                <button class="ai-go-btn" @click="doAiSearch" :disabled="aiLoading">{{ aiLoading ? '分析中...' : '搜索' }}</button>
              </div>
              <div class="ai-output" ref="aiSearchOutput">
                <div v-if="aiSearchResult" class="ai-markdown" v-html="renderMarkdown(aiSearchResult)"></div>
                <div v-else class="ai-placeholder">
                  <div class="ai-placeholder-icon">🔍</div>
                  <p>输入关键词，AI 将基于平台新闻数据进行深度分析</p>
                </div>
              </div>
            </div>
            <div v-if="aiMode === 'summary'" class="ai-panel">
              <div class="ai-panel-head">
                <h2>📋 热点总结</h2>
                <span class="ai-badge">实时生成</span>
              </div>
              <div class="ai-input-row">
                <input v-model="aiSummaryInstruction" class="ai-input" placeholder="可选：输入额外指令（如：重点关注AI领域）" @keyup.enter="doAiSummary" :disabled="aiLoading" />
                <button class="ai-go-btn" @click="doAiSummary" :disabled="aiLoading">{{ aiLoading ? '生成中...' : '生成' }}</button>
              </div>
              <div class="ai-output" ref="aiSummaryOutput">
                <div v-if="aiSummaryResult" class="ai-markdown" v-html="renderMarkdown(aiSummaryResult)"></div>
                <div v-else class="ai-placeholder">
                  <div class="ai-placeholder-icon">📋</div>
                  <p>AI 将基于最新新闻数据生成热点总结报告</p>
                </div>
              </div>
            </div>
            <div v-if="aiMode === 'translate'" class="ai-panel">
              <div class="ai-panel-head">
                <h2>🌐 智能翻译</h2>
                <span class="ai-badge">英 → 中</span>
              </div>
              <div class="ai-input-row">
                <input v-model="aiTranslateText" class="ai-input" placeholder="输入英文文本进行翻译..." @keyup.enter="doAiTranslate" :disabled="aiLoading" />
                <button class="ai-go-btn" @click="doAiTranslate" :disabled="aiLoading">{{ aiLoading ? '翻译中...' : '翻译' }}</button>
              </div>
              <div class="ai-output" ref="aiTranslateOutput">
                <div v-if="aiTranslateResult" class="ai-markdown" v-html="renderMarkdown(aiTranslateResult)"></div>
                <div v-else class="ai-placeholder">
                  <div class="ai-placeholder-icon">🌐</div>
                  <p>输入英文文本，AI 将自动翻译为中文</p>
                </div>
              </div>
            </div>
            <div v-if="aiMode === 'terminal'" class="ai-panel ai-terminal-panel">
              <div class="ai-panel-head">
                <h2>⌨️ AI 命令终端</h2>
                <button class="ai-clear-btn" @click="aiTerminalLines = []">清屏</button>
              </div>
              <div class="ai-terminal" ref="aiTerminalRef" @click="focusTerminalInput">
                <div v-for="(line, i) in aiTerminalLines" :key="i" :class="['term-line', line.type]">
                  <span v-if="line.type === 'cmd'" class="term-prompt">$&nbsp;</span>
                  <span v-html="line.html || line.text"></span>
                </div>
                <div v-if="aiLoading" class="term-line system">
                  <span class="term-cursor">▌</span> AI 处理中...
                </div>
                <div class="term-input-line">
                  <span class="term-prompt">$&nbsp;</span>
                  <input ref="aiTerminalInput" v-model="aiTerminalCmd" class="term-input" @keyup.enter="execTerminalCmd" :disabled="aiLoading" placeholder="输入命令... (help 查看帮助)" />
                </div>
              </div>
            </div>
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
const timeRange = ref('today')

const switchTab = (tab) => {
  activeTab.value = tab
  if (tab === 'news') loadNews(1)
  if (tab === 'ai') checkAiStatus()
}

const aiMode = ref('search')
const aiLoading = ref(false)
const aiOnline = ref(false)
const aiSearchKeyword = ref('')
const aiSearchResult = ref('')
const aiSummaryInstruction = ref('')
const aiSummaryResult = ref('')
const aiTranslateText = ref('')
const aiTranslateResult = ref('')
const aiTerminalLines = ref([])
const aiTerminalCmd = ref('')
const aiTerminalInput = ref(null)
const aiTerminalRef = ref(null)
const aiSearchOutput = ref(null)
const aiSummaryOutput = ref(null)
const aiTranslateOutput = ref(null)
let aiAbortController = null

const checkAiStatus = async () => {
  try {
    const res = await fetch('/api/ai/search?keyword=test')
    aiOnline.value = !!(res.ok || res.status === 200)
  } catch { aiOnline.value = false }
}

const renderMarkdown = (text) => {
  if (!text) return ''
  return text
    .replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
    .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
    .replace(/\*(.*?)\*/g, '<em>$1</em>')
    .replace(/^### (.*$)/gm, '<h4>$1</h4>')
    .replace(/^## (.*$)/gm, '<h3>$1</h3>')
    .replace(/^# (.*$)/gm, '<h2>$1</h2>')
    .replace(/^\* (.*$)/gm, '<li>$1</li>')
    .replace(/^- (.*$)/gm, '<li>$1</li>')
    .replace(/^\d+\. (.*$)/gm, '<li>$1</li>')
    .replace(/\n{2,}/g, '<br/><br/>')
    .replace(/\n/g, '<br/>')
}

const doAiSearch = async () => {
  if (!aiSearchKeyword.value.trim() || aiLoading.value) return
  aiLoading.value = true
  aiSearchResult.value = ''
  try {
    const res = await fetch(`/api/ai/search/stream?keyword=${encodeURIComponent(aiSearchKeyword.value)}`)
    const reader = res.body.getReader()
    const decoder = new TextDecoder()
    let buffer = ''
    while (true) {
      const { done, value } = await reader.read()
      if (done) break
      buffer += decoder.decode(value, { stream: true })
      const lines = buffer.split('\n')
      buffer = lines.pop() || ''
      for (const line of lines) {
        if (line.startsWith('data:')) {
          const token = line.slice(5).trim()
          if (token === '[DONE]') break
          aiSearchResult.value += token
        }
      }
      if (aiSearchOutput.value) aiSearchOutput.value.scrollTop = aiSearchOutput.value.scrollHeight
    }
    if (!aiSearchResult.value) {
      const fallback = await fetch(`/api/ai/search?keyword=${encodeURIComponent(aiSearchKeyword.value)}`)
      const data = await fallback.json()
      if (data.success) aiSearchResult.value = data.data
    }
  } catch (e) {
    try {
      const fallback = await fetch(`/api/ai/search?keyword=${encodeURIComponent(aiSearchKeyword.value)}`)
      const data = await fallback.json()
      if (data.success) aiSearchResult.value = data.data
      else aiSearchResult.value = '搜索失败: ' + (data.message || e.message)
    } catch { aiSearchResult.value = 'AI 搜索服务暂不可用' }
  } finally { aiLoading.value = false }
}

const doAiSummary = async () => {
  aiLoading.value = true
  aiSummaryResult.value = ''
  try {
    const url = `/api/ai/hot-summary/stream` + (aiSummaryInstruction.value ? `?instruction=${encodeURIComponent(aiSummaryInstruction.value)}` : '')
    const res = await fetch(url)
    const reader = res.body.getReader()
    const decoder = new TextDecoder()
    let buffer = ''
    while (true) {
      const { done, value } = await reader.read()
      if (done) break
      buffer += decoder.decode(value, { stream: true })
      const lines = buffer.split('\n')
      buffer = lines.pop() || ''
      for (const line of lines) {
        if (line.startsWith('data:')) {
          const token = line.slice(5).trim()
          if (token === '[DONE]') break
          aiSummaryResult.value += token
        }
      }
      if (aiSummaryOutput.value) aiSummaryOutput.value.scrollTop = aiSummaryOutput.value.scrollHeight
    }
    if (!aiSummaryResult.value) {
      const fallback = await fetch(`/api/ai/hot-summary` + (aiSummaryInstruction.value ? `?instruction=${encodeURIComponent(aiSummaryInstruction.value)}` : ''))
      const data = await fallback.json()
      if (data.success) aiSummaryResult.value = data.data
    }
  } catch (e) {
    try {
      const fallback = await fetch(`/api/ai/hot-summary` + (aiSummaryInstruction.value ? `?instruction=${encodeURIComponent(aiSummaryInstruction.value)}` : ''))
      const data = await fallback.json()
      if (data.success) aiSummaryResult.value = data.data
      else aiSummaryResult.value = '生成失败: ' + (data.message || e.message)
    } catch { aiSummaryResult.value = 'AI 热点总结服务暂不可用' }
  } finally { aiLoading.value = false }
}

const doAiTranslate = async () => {
  if (!aiTranslateText.value.trim() || aiLoading.value) return
  aiLoading.value = true
  aiTranslateResult.value = ''
  try {
    const res = await fetch(`/api/ai/translate?text=${encodeURIComponent(aiTranslateText.value)}`)
    const data = await res.json()
    if (data.success) aiTranslateResult.value = data.data
    else aiTranslateResult.value = '翻译失败: ' + (data.message || '未知错误')
  } catch { aiTranslateResult.value = 'AI 翻译服务暂不可用' }
  finally { aiLoading.value = false }
}

const focusTerminalInput = () => { aiTerminalInput.value?.focus() }

const termLog = (text, type = 'output', html = '') => {
  aiTerminalLines.value.push({ text, type, html })
  nextTick(() => { if (aiTerminalRef.value) aiTerminalRef.value.scrollTop = aiTerminalRef.value.scrollHeight })
}

const execTerminalCmd = async () => {
  const cmd = aiTerminalCmd.value.trim()
  if (!cmd) return
  aiTerminalCmd.value = ''
  termLog(cmd, 'cmd')
  const parts = cmd.split(/\s+/)
  const action = parts[0].toLowerCase()
  const arg = parts.slice(1).join(' ')

  const cmdMap = {
    help: 'help', 帮助: 'help',
    clear: 'clear', 清屏: 'clear', 清除: 'clear',
    status: 'status', 状态: 'status',
    stats: 'stats', 统计: 'stats', 数据: 'stats',
    search: 'search', 搜索: 'search', 查找: 'search', 找: 'search',
    summary: 'summary', 总结: 'summary', 热点: 'summary', 概要: 'summary',
    translate: 'translate', 翻译: 'translate', 译: 'translate',
  }
  const resolved = cmdMap[action]
  const finalAction = resolved || 'search'
  const finalArg = resolved ? arg : cmd

  if (finalAction === 'help') {
    termLog(`可用命令:
  search/搜索 &lt;关键词&gt;   - AI 智能搜索新闻
  summary/总结 [指令]     - 生成热点总结
  translate/翻译 &lt;文本&gt;  - 智能翻译
  status/状态              - 查看 AI 服务状态
  stats/统计               - 查看平台数据统计
  clear/清屏               - 清屏
  help/帮助                - 显示帮助
  
  💡 直接输入任意文字也可触发 AI 搜索`, 'system')
  } else if (finalAction === 'clear') {
    aiTerminalLines.value = []
  } else if (finalAction === 'status') {
    termLog('正在检测 AI 服务状态...', 'system')
    try {
      const res = await fetch('/api/ai/search?keyword=ping')
      const data = await res.json()
      aiOnline.value = data.success
      termLog(data.success ? '✅ AI 服务在线 | 模型: NVIDIA Llama 3.1 8B Instruct' : '❌ AI 服务异常', data.success ? 'success' : 'error')
    } catch { aiOnline.value = false; termLog('❌ AI 服务离线', 'error') }
  } else if (finalAction === 'stats') {
    try {
      const res = await fetch('/api/analytics/overview')
      const data = await res.json()
      const d = data.data
      termLog(`📊 平台数据统计:
  总用户: ${d.totalUsers} | 总新闻: ${d.totalNews} | 今日新增: ${d.todayNews}
  总浏览: ${d.totalViews} | 总行为: ${d.totalBehaviors} | 阅读记录: ${d.totalReadHistory}`, 'success')
    } catch { termLog('❌ 获取统计失败', 'error') }
  } else if (finalAction === 'search') {
    if (!finalArg) { termLog('用法: search/搜索 <关键词>', 'error'); return }
    aiLoading.value = true
    termLog(`🔍 搜索: "${finalArg}" ...`, 'system')
    try {
      const res = await fetch(`/api/ai/search?keyword=${encodeURIComponent(finalArg)}`)
      const data = await res.json()
      if (data.success) termLog(data.data, 'output')
      else termLog('❌ ' + (data.message || '搜索失败'), 'error')
    } catch { termLog('❌ AI 搜索服务不可用', 'error') }
    finally { aiLoading.value = false }
  } else if (finalAction === 'summary') {
    aiLoading.value = true
    termLog('📋 生成热点总结...', 'system')
    try {
      const url = `/api/ai/hot-summary` + (finalArg ? `?instruction=${encodeURIComponent(finalArg)}` : '')
      const res = await fetch(url)
      const data = await res.json()
      if (data.success) termLog(data.data, 'output')
      else termLog('❌ ' + (data.message || '生成失败'), 'error')
    } catch { termLog('❌ AI 服务不可用', 'error') }
    finally { aiLoading.value = false }
  } else if (finalAction === 'translate') {
    if (!finalArg) { termLog('用法: translate/翻译 <文本>', 'error'); return }
    aiLoading.value = true
    termLog('🌐 翻译中...', 'system')
    try {
      const res = await fetch(`/api/ai/translate?text=${encodeURIComponent(finalArg)}`)
      const data = await res.json()
      if (data.success) termLog(data.data, 'output')
      else termLog('❌ ' + (data.message || '翻译失败'), 'error')
    } catch { termLog('❌ AI 翻译服务不可用', 'error') }
    finally { aiLoading.value = false }
  } else {
    termLog(`未知命令: ${action}，输入 help 查看帮助`, 'error')
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
    const d = res.data || res
    const prefix = timeRange.value === 'today' ? '今日' : timeRange.value === 'week' ? '近7天' : '近30天'
    statsData.value = [
      { label: prefix + 'UV', value: formatNumber(d.todayUV), color: '#2F6BFF' },
      { label: prefix + 'PV', value: formatNumber(d.todayPV), color: '#7C3AED' },
      { label: '实时在线', value: formatNumber(d.onlineUsers), color: '#10B981' },
      { label: '平均阅读时长', value: formatDuration(d.avgDuration), color: '#F59E0B' },
      { label: '总用户数', value: formatNumber(d.totalUsers), color: '#EC4899' },
      { label: '总新闻数', value: formatNumber(d.totalNews), color: '#06B6D4' },
      { label: '今日新增', value: formatNumber(d.todayNews), color: '#8B5CF6' },
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
  if (hotNewsChart) hotNewsChart.dispose()
  hotNewsChart = echarts.init(hotNewsChartRef.value)
  try {
    const res = await analyticsApi.getHotNews(timeRange.value)
    const data = res.data || res
    if (!Array.isArray(data) || data.length === 0) {
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
    const res = await analyticsApi.getChannelDist(timeRange.value)
    let data = res.data || res
    if (!Array.isArray(data)) data = []
    // Fallback: fetch news from MySQL and count by channel
    if (data.length === 0) {
      try {
        const newsRes = await fetch('/api/news?page=1&size=500')
        const newsJson = await newsRes.json()
        const records = newsJson?.data?.records || newsJson?.data || []
        const channelMap = {}
        records.forEach(item => {
          const ch = item.channel || '其他'
          channelMap[ch] = (channelMap[ch] || 0) + 1
        })
        data = Object.entries(channelMap).map(([name, value]) => ({ name, value }))
      } catch (e) { /* ignore */ }
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
      tooltip: { trigger: 'item', backgroundColor: chartTheme.tipBg, borderColor: '#334155', textStyle: { color: chartTheme.tipText, fontSize: 12, fontFamily: chartTheme.fontFamily }, formatter: '{b}: {c} ({d}%)' },
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
    const res = await analyticsApi.getFunnel(timeRange.value)
    const data = res.data || res
    if (!Array.isArray(data) || data.length === 0) {
      funnelChart.setOption({
        title: { text: '暂无数据', left: 'center', top: 'center', textStyle: { color: '#94a3b8', fontSize: 14, fontFamily: chartTheme.fontFamily } }
      })
      return
    }
    const colors = ['#2F6BFF', '#7C3AED', '#10B981', '#F59E0B', '#EF4444', '#EC4899']
    funnelChart.setOption({
      backgroundColor: 'transparent',
      tooltip: { trigger: 'item', backgroundColor: chartTheme.tipBg, borderColor: '#334155', textStyle: { color: chartTheme.tipText, fontSize: 12, fontFamily: chartTheme.fontFamily } },
      series: [{ type: 'funnel', left: '10%', top: 10, bottom: 10, width: '65%', min: 0, max: 100, minSize: '10%', maxSize: '100%', sort: 'descending', gap: 4, label: { show: true, position: 'inside', color: '#fff', fontSize: 11, fontWeight: 500, fontFamily: chartTheme.fontFamily }, labelLine: { show: false }, itemStyle: { borderColor: '#fff', borderWidth: 1 }, data: data.map((item, i) => ({ name: item.name, value: item.value, itemStyle: { color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [{ offset: 0, color: colors[i] || colors[0] }, { offset: 1, color: (colors[i] || colors[0]) + '88' }]) } })) }]
    })
  } catch (e) { console.error(e) }
}

const initRegionChart = async () => {
  if (!regionChartRef.value) return
  if (regionChart) regionChart.dispose()
  regionChart = echarts.init(regionChartRef.value)
  try {
    const res = await analyticsApi.getRegionDist(timeRange.value)
    const data = res.data || res
    if (!Array.isArray(data) || data.length === 0) {
      regionChart.setOption({
        title: { text: '暂无数据', left: 'center', top: 'center', textStyle: { color: '#94a3b8', fontSize: 14, fontFamily: chartTheme.fontFamily } }
      })
      return
    }
    regionChart.setOption({
      backgroundColor: 'transparent',
      tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' }, backgroundColor: chartTheme.tipBg, borderColor: '#334155', textStyle: { color: chartTheme.tipText, fontSize: 12, fontFamily: chartTheme.fontFamily } },
      grid: { left: 8, right: 30, bottom: 4, top: 4, containLabel: true },
      xAxis: { type: 'value', axisLine: { show: false }, axisLabel: { color: chartTheme.textColor, fontSize: 10, fontFamily: chartTheme.fontFamily }, splitLine: { lineStyle: { color: chartTheme.splitColor } } },
      yAxis: { type: 'category', data: data.map(d => d.event_type || d.name || '').reverse(), axisLine: { show: false }, axisTick: { show: false }, axisLabel: { color: '#475569', fontSize: 11, fontFamily: chartTheme.fontFamily } },
      series: [{ type: 'bar', data: data.map(d => d.cnt || d.value || 0).reverse(), itemStyle: { color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [{ offset: 0, color: '#10B981' }, { offset: 1, color: '#34D399' }]), borderRadius: [0, 4, 4, 0] }, barWidth: 16 }]
    })
  } catch (e) { console.error(e) }
}

const handleResize = () => { trendChart?.resize(); hotNewsChart?.resize(); channelChart?.resize(); funnelChart?.resize(); regionChart?.resize() }

const loadNews = async (page) => {
  if (page) newsPage.value = page
  try {
    const params = new URLSearchParams({ page: newsPage.value, size: 20 })
    if (newsChannel.value) params.set('channel', newsChannel.value)
    if (newsKeyword.value) params.set('keyword', newsKeyword.value)
    const res = await fetch(`/api/news?${params}`).then(r => r.json())
    if (res.success && res.data) {
      const pageData = res.data
      newsList.value = pageData.records || pageData.data || []
      newsTotal.value = pageData.total || 0
    }
  } catch (e) { console.error(e) }
}
const loadChannels = async () => { try { const res = await fetch('/api/news/channels').then(r => r.json()); channels.value = (res.data || []).map(d => d.channel) } catch (e) { console.error(e) } }

const loadOverviewData = async () => {
  await updateRealtimeStats()
  await initCharts()
}

let statsInterval = null, timeInterval = null
const initCharts = async () => { await nextTick(); await initTrendChart(); await initHotNewsChart(); await initChannelChart(); await initFunnelChart(); await initRegionChart() }
watch(activeTab, async (val) => { if (val === 'overview') { await nextTick(); initCharts() } })
watch(timeRange, () => {
  loadOverviewData()
})
onMounted(() => { updateTime(); timeInterval = setInterval(updateTime, 1000); updateRealtimeStats(); statsInterval = setInterval(updateRealtimeStats, 10000); initCharts(); loadChannels(); window.addEventListener('resize', handleResize) })
onUnmounted(() => { if (statsInterval) clearInterval(statsInterval); if (timeInterval) clearInterval(timeInterval); trendChart?.dispose(); hotNewsChart?.dispose(); channelChart?.dispose(); funnelChart?.dispose(); regionChart?.dispose(); window.removeEventListener('resize', handleResize) })

const goHome = () => {
  const homeUrl = window.HOME_URL || '/'
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
  color: #0F172A;
  letter-spacing: 1px;
}

.brand-sub {
  font-size: 11px;
  color: #64748B;
  background: #F1F5F9;
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

@media (max-width: 1200px) {
  .kpi-banner { grid-template-columns: repeat(4, 1fr); }
  .bar-inner { padding: 0 16px; }
  .stats-grid { grid-template-columns: repeat(2, 1fr); }
}

@media (max-width: 900px) {
  .kpi-banner { grid-template-columns: repeat(2, 1fr); }
  .overview-grid { grid-template-columns: 1fr; }
  .bar-inner { padding: 0 12px; gap: 16px; }
  .brand-sub { display: none; }
  .live-tag { display: none; }
}

@media (max-width: 600px) {
  .top-bar { height: auto; min-height: 56px; padding: 8px 0; }
  .bar-inner { flex-wrap: wrap; }
  .brand { order: 1; }
  .bar-right { order: 2; margin-left: auto; }
  .main-nav { order: 3; width: 100%; justify-content: space-between; }
  .nav-link { padding: 8px 12px; font-size: 12px; }
  .overview-page { padding: 16px 12px; }
  .manage-page { padding: 16px 12px; }
  .search-bar { flex-direction: column; align-items: stretch; }
  .search-input { max-width: 100%; }
  .search-select { width: 100%; }
  .news-table th, .news-table td { padding: 10px 8px; font-size: 12px; }
  .col-title { max-width: 150px; }
  .col-src, .col-time { display: none; }
  .field-row { flex-direction: column; gap: 0; }
}

.ai-page { height: 100%; }
.ai-layout { display: flex; height: 100%; max-width: 1440px; margin: 0 auto; }

.ai-sidebar {
  width: 200px; flex-shrink: 0; background: #0F172A; color: #E2E8F0;
  display: flex; flex-direction: column; padding: 16px 0;
}
.ai-sidebar-title {
  font-size: 11px; font-weight: 700; color: #64748B; text-transform: uppercase;
  letter-spacing: 1px; padding: 0 16px; margin-bottom: 12px;
}
.ai-fn-btn {
  display: flex; align-items: center; gap: 10px; width: 100%;
  padding: 11px 16px; border: none; background: transparent;
  color: #94A3B8; font-size: 13px; font-weight: 500; cursor: pointer;
  transition: all 0.15s; text-align: left;
}
.ai-fn-btn:hover { background: rgba(47,107,255,0.08); color: #E2E8F0; }
.ai-fn-btn.active { background: rgba(47,107,255,0.15); color: #2F6BFF; font-weight: 600; }
.ai-fn-icon { font-size: 15px; }
.ai-sidebar-footer {
  margin-top: auto; padding: 12px 16px; display: flex; align-items: center;
  gap: 8px; font-size: 12px; color: #64748B;
}
.ai-status-dot {
  width: 8px; height: 8px; border-radius: 50%; background: #EF4444; flex-shrink: 0;
}
.ai-status-dot.online { background: #10B981; box-shadow: 0 0 8px rgba(16,185,129,0.5); }

.ai-main { flex: 1; display: flex; flex-direction: column; min-width: 0; background: #F8FAFC; }

.ai-panel {
  flex: 1; display: flex; flex-direction: column; padding: 24px; min-height: 0;
}
.ai-panel-head {
  display: flex; align-items: center; justify-content: space-between;
  margin-bottom: 16px; flex-shrink: 0;
}
.ai-panel-head h2 { font-size: 16px; font-weight: 700; color: #0F172A; margin: 0; }
.ai-badge {
  font-size: 10px; font-weight: 700; color: #7C3AED; background: #F5F3FF;
  padding: 4px 12px; border-radius: 12px; letter-spacing: 0.3px;
}
.ai-clear-btn {
  padding: 5px 14px; border: 1px solid #E2E8F0; border-radius: 6px;
  background: #fff; color: #64748B; font-size: 12px; font-weight: 600; cursor: pointer;
}
.ai-clear-btn:hover { border-color: #EF4444; color: #EF4444; }

.ai-input-row { display: flex; gap: 10px; margin-bottom: 16px; flex-shrink: 0; }
.ai-input {
  flex: 1; padding: 10px 16px; border: 1px solid #E2E8F0; border-radius: 8px;
  font-size: 13px; outline: none; background: #fff; color: #1E293B;
}
.ai-input:focus { border-color: #2F6BFF; box-shadow: 0 0 0 3px rgba(47,107,255,0.1); }
.ai-go-btn {
  padding: 10px 24px; background: linear-gradient(135deg, #2F6BFF, #7C3AED);
  color: #fff; border: none; border-radius: 8px; font-size: 13px;
  font-weight: 600; cursor: pointer; white-space: nowrap;
}
.ai-go-btn:hover { box-shadow: 0 4px 16px rgba(47,107,255,0.3); }
.ai-go-btn:disabled { opacity: 0.5; cursor: not-allowed; }

.ai-output {
  flex: 1; background: #fff; border: 1px solid #E2E8F0; border-radius: 12px;
  padding: 20px; overflow-y: auto; min-height: 200px;
}
.ai-markdown { font-size: 13.5px; line-height: 1.8; color: #334155; }
.ai-markdown h2 { font-size: 16px; color: #0F172A; margin: 16px 0 8px; }
.ai-markdown h3 { font-size: 15px; color: #0F172A; margin: 14px 0 6px; }
.ai-markdown h4 { font-size: 14px; color: #1E293B; margin: 12px 0 4px; }
.ai-markdown strong { color: #0F172A; }
.ai-markdown li { margin: 4px 0; padding-left: 4px; }
.ai-placeholder {
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  height: 100%; color: #94A3B8;
}
.ai-placeholder-icon { font-size: 48px; margin-bottom: 12px; opacity: 0.4; }
.ai-placeholder p { font-size: 14px; }

.ai-terminal-panel { padding: 16px; }
.ai-terminal {
  flex: 1; background: #0D1117; border-radius: 12px; padding: 16px;
  font-family: 'JetBrains Mono', 'Fira Code', 'Consolas', monospace;
  font-size: 13px; line-height: 1.7; overflow-y: auto; min-height: 400px;
  color: #C9D1D9;
}
.term-line { white-space: pre-wrap; word-break: break-word; }
.term-line.cmd { color: #58A6FF; }
.term-line.system { color: #8B949E; }
.term-line.success { color: #3FB950; }
.term-line.error { color: #F85149; }
.term-line.output { color: #C9D1D9; }
.term-prompt { color: #3FB950; font-weight: 700; }
.term-cursor { animation: blink 1s step-end infinite; color: #58A6FF; }
.term-input-line { display: flex; align-items: center; margin-top: 4px; }
.term-input {
  flex: 1; background: transparent; border: none; color: #C9D1D9;
  font-family: inherit; font-size: 13px; outline: none; caret-color: #58A6FF;
}
.term-input::placeholder { color: #484F58; }

@media (max-width: 900px) {
  .ai-layout { flex-direction: column; }
  .ai-sidebar {
    width: 100%; flex-direction: row; padding: 8px 12px;
    overflow-x: auto; gap: 4px;
  }
  .ai-sidebar-title, .ai-sidebar-footer { display: none; }
  .ai-fn-btn { padding: 8px 14px; white-space: nowrap; font-size: 12px; }
  .ai-panel { padding: 16px; }
}
</style>
