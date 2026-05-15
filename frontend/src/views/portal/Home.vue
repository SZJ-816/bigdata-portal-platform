<template>
  <div class="home-page">
    <section class="headline-section">
      <div class="headline-main" @click="goNews(headline.id)">
        <img
          v-if="headline.imageUrl"
          v-lazy="headline.imageUrl"
          :alt="headline.title"
          :data-channel="headline.channel"
          class="headline-img"
        />
        <div v-else class="headline-img-wrap">
          <span v-if="headline.isBreaking" class="breaking-tag">BREAKING</span>
        </div>
        <div class="headline-overlay">
          <h1 class="headline-title">{{ headline.title }}</h1>
          <p class="headline-summary">{{ headline.summary }}</p>
          <div class="headline-meta">
            <span class="channel-tag" @click.stop="goChannel(headline.channel)">{{ headline.channelName }}</span>
            <span class="meta-item">{{ headline.source }}</span>
            <span class="meta-item">{{ formatRelativeTime(headline.createdAt) }}</span>
            <span class="meta-item">{{ formatViewCount(headline.viewCount || 0) }} 阅读</span>
          </div>
        </div>
      </div>
      <div class="headline-side">
        <div v-for="item in sideHeadlines" :key="item.id" class="side-news" @click="goNews(item.id)">
          <img
            v-if="item.imageUrl"
            v-lazy="item.imageUrl"
            :alt="item.title"
            :data-channel="item.channel"
            class="side-img"
          />
          <div v-else class="side-img-wrap"></div>
          <div class="side-content">
            <span v-if="item.isBreaking" class="breaking-tag-sm">BREAKING</span>
            <h3 class="side-title">{{ item.title }}</h3>
            <div class="side-meta">
              <span class="channel-tag-sm" @click.stop="goChannel(item.channel)">{{ item.channelName }}</span>
              <span>{{ formatRelativeTime(item.createdAt) }}</span>
            </div>
          </div>
        </div>
      </div>
    </section>

    <div v-if="newsLoading" class="loading-state">加载中...</div>
    <div v-else-if="newsError" class="error-state">
      <p>加载失败</p>
      <button class="btn btn-outline" @click="fetchNews">重试</button>
    </div>
    <template v-else>
    <section class="main-content">
      <div class="content-left">
        <h2 class="section-title">最新资讯</h2>
        <div class="news-list">
          <div v-for="item in leftNews" :key="item.id" class="news-item" @click="goNews(item.id)">
            <img
              v-if="item.imageUrl"
              v-lazy="item.imageUrl"
              :alt="item.title"
              :data-channel="item.channel"
              class="thumb-img"
            />
            <div v-else class="thumb-wrap"></div>
            <div class="news-body">
              <div class="news-header">
                <h3 class="news-title">{{ item.title }}</h3>
                <span v-if="item.isBreaking" class="breaking-tag-sm">BREAKING</span>
              </div>
              <p class="news-summary">{{ item.summary }}</p>
              <div class="news-meta">
                <span class="channel-tag-sm" @click.stop="goChannel(item.channel)">{{ item.channelName }}</span>
                <span>{{ formatRelativeTime(item.createdAt) }}</span>
                <span>{{ formatViewCount(item.viewCount || 0) }} 阅读</span>
                <span>{{ item.commentCount || 0 }} 评论</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="content-center">
        <h2 class="section-title">资讯流</h2>
        <div class="news-stream">
          <div v-for="item in centerNews" :key="item.id" class="stream-item" @click="goNews(item.id)">
            <h3 class="stream-title">{{ item.title }}</h3>
            <p class="stream-summary">{{ item.summary }}</p>
            <div class="stream-meta">
              <span class="channel-tag-sm" @click.stop="goChannel(item.channel)">{{ item.channelName }}</span>
              <span>{{ formatRelativeTime(item.createdAt) }}</span>
              <span>{{ formatViewCount(item.viewCount || 0) }} 阅读</span>
            </div>
          </div>
        </div>
      </div>

      <div class="content-right">
        <div class="card ai-hot-card">
          <h3 class="sidebar-title">✦ AI 今日热点</h3>
          <div v-if="!hotSummary && !hotSummaryLoading" class="ai-hot-prompt">
            <input v-model="hotInstruction" class="ai-hot-input" placeholder="输入指示词（可选）" @keyup.enter="fetchHotSummary" />
            <button class="btn btn-ai-sm" @click="fetchHotSummary" :disabled="hotSummaryLoading">生成热点</button>
          </div>
          <div v-if="hotSummaryLoading" class="ai-hot-loading">
            <span class="ai-spin"></span> AI 正在总结今日热点...
          </div>
          <div v-if="hotSummary" class="ai-hot-content" v-html="renderMarkdown(hotSummary)"></div>
          <div v-if="hotSummary && !hotSummaryLoading" class="ai-hot-actions">
            <input v-model="hotInstruction" class="ai-hot-input-sm" placeholder="追加指示词..." @keyup.enter="fetchHotSummary" />
            <button class="btn btn-ai-sm" @click="fetchHotSummary">重新生成</button>
          </div>
        </div>
        <div class="card hot-ranking">
          <h3 class="sidebar-title">热点排行</h3>
          <div class="ranking-list">
            <div v-for="(item, idx) in hotNews" :key="item.id" class="ranking-item" @click="goNews(item.id)">
              <span :class="['rank-num', { 'rank-hot': idx < 3 }]">{{ idx + 1 }}</span>
              <span class="rank-title">{{ item.title }}</span>
            </div>
          </div>
        </div>
        <div class="card channel-recommend">
          <h3 class="sidebar-title">频道推荐</h3>
          <div class="channel-list">
            <span v-for="ch in allChannels" :key="ch.name" class="channel-card" @click="goChannel(ch.name)">{{ ch.label }}</span>
          </div>
        </div>
      </div>
    </section>

    <div class="load-more-wrap">
      <button class="btn btn-outline load-more-btn" @click="loadMore">更多新闻</button>
    </div>
    </template>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { newsApi, behaviorApi } from '../../api'
import { channels, formatRelativeTime } from '../../utils'
import { cleanText, formatViewCount, CHANNEL_LABEL_MAP, renderMarkdown } from '../../utils'

const channelLabelMap = CHANNEL_LABEL_MAP

const router = useRouter()
const allNews = ref([])
const hotSummary = ref('')
const hotSummaryLoading = ref(false)
const hotInstruction = ref('')
const newsLoading = ref(true)
const newsError = ref(false)
let abortController = null
let hotSummaryAbortController = null

const headline = computed(() => {
  if (!allNews.value[0]) {
    return {
      title: '加载中...',
      summary: '',
      channelName: '',
      source: '',
      createdAt: Date.now(),
      viewCount: 0,
      imageUrl: ''
    }
  }
  return {
    ...allNews.value[0],
    channelName: channelLabelMap[allNews.value[0].channel] || allNews.value[0].channel,
    createdAt: allNews.value[0].publishTime
  }
})

const sideHeadlines = computed(() => allNews.value.slice(1, 4).map(item => ({
  ...item,
  channelName: channelLabelMap[item.channel] || item.channel,
  createdAt: item.publishTime
})))

const leftNews = computed(() => allNews.value.slice(4, 9).map(item => ({
  ...item,
  channelName: channelLabelMap[item.channel] || item.channel,
  createdAt: item.publishTime
})))

const centerNews = computed(() => allNews.value.slice(9, 14).map(item => ({
  ...item,
  channelName: channelLabelMap[item.channel] || item.channel,
  createdAt: item.publishTime
})))

const hotNews = computed(() => allNews.value.slice(0, 10))

const allChannels = computed(() => channels)

function goNews(id) {
  router.push(`/news/${id}`)
}

function goChannel(name) {
  const displayName = channelLabelMap[name] || name
  router.push(`/channel/${displayName}`)
}

function loadMore() {
  router.push('/channel/互联网')
}

async function fetchHotSummary() {
  hotSummaryLoading.value = true
  hotSummary.value = ''
  if (hotSummaryAbortController) hotSummaryAbortController.abort()
  hotSummaryAbortController = new AbortController()
  let streamSuccess = false
  try {
    const params = hotInstruction.value.trim() ? `?instruction=${encodeURIComponent(hotInstruction.value.trim())}` : ''
    const headers = {}
    const token = localStorage.getItem('token')
    if (token) headers['Authorization'] = `Bearer ${token}`
    const response = await fetch(`/api/ai/hot-summary/stream${params}`, {
      signal: hotSummaryAbortController.signal,
      headers
    })
    if (!response.ok) throw new Error('Stream failed')
    const reader = response.body.getReader()
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
          const data = line.substring(5).trim()
          if (data === '[DONE]') continue
          if (data.startsWith('[ERROR]')) continue
          hotSummary.value += data
          streamSuccess = true
        }
      }
    }
  } catch (err) {
    console.warn('Hot summary stream failed:', err.message)
  }
  if (!streamSuccess) {
    try {
      const res = await newsApi.getHot(false)
      if (res.data && res.data.length > 0) {
        const top = res.data.slice(0, 5)
        hotSummary.value = '【今日热点】\n\n' + top.map((n, i) => `${i + 1}. ${n.title}`).join('\n')
        streamSuccess = true
      }
    } catch (err2) {
      console.error('Hot summary fallback failed:', err2)
      hotSummary.value = 'AI 热点总结暂时不可用，请稍后再试。'
    }
  }
  hotSummaryLoading.value = false
}

async function fetchNews() {
  newsLoading.value = true
  newsError.value = false
  try {
    const res = await newsApi.getList()
    if (res.data.data) {
      allNews.value = res.data.data.map(item => ({
        ...item,
        title: cleanText(item.title),
        summary: cleanText(item.summary)
      }))
    }
  } catch (err) {
    console.error('Failed to load news:', err)
    newsError.value = true
  }
  newsLoading.value = false
}

onMounted(async () => {
  await fetchNews()
  behaviorApi.report({ eventType: 'page_view', targetId: 'home', targetType: 'page' })
})

onUnmounted(() => {
  behaviorApi.flush()
  if (hotSummaryAbortController) hotSummaryAbortController.abort()
})
</script>

<style scoped>
.loading-state {
  text-align: center;
  padding: 60px 20px;
  color: var(--color-text-secondary);
  font-size: 16px;
}
.error-state {
  text-align: center;
  padding: 60px 20px;
  color: var(--color-text-secondary);
}
.error-state p {
  font-size: 16px;
  margin-bottom: 16px;
}
.headline-section {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
}
.headline-main {
  flex: 2;
  position: relative;
  cursor: pointer;
  overflow: hidden;
  border-radius: 4px;
  aspect-ratio: 16 / 9;
  max-height: 400px;
}
.headline-img-wrap {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  background-color: var(--color-primary);
}
.headline-img, .side-img, .thumb-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: inherit;
  transition: opacity 0.3s ease-in-out;
}
.headline-img {
  border-radius: 4px;
}
.side-img {
  border-radius: 4px;
  width: 100px;
  height: 68px;
  position: relative;
  flex-shrink: 0;
  object-fit: cover;
  object-position: center;
}
.thumb-img {
  width: 120px;
  height: 80px;
  position: relative;
  flex-shrink: 0;
  border-radius: 4px;
}
.breaking-tag {
  position: absolute;
  top: 16px;
  left: 16px;
  background: #ff3b30;
  color: white;
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
  z-index: 2;
}
.headline-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 20px;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.9));
  color: white;
}
.headline-title {
  font-size: 24px;
  font-weight: 600;
  margin: 0 0 8px 0;
  line-height: 1.3;
}
.headline-summary {
  font-size: 14px;
  opacity: 0.8;
  margin: 0 0 12px 0;
  line-height: 1.5;
}
.headline-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 12px;
}
.channel-tag {
  background: var(--color-tag-bg);
  padding: 2px 8px;
  border-radius: 4px;
}
.meta-item {
  opacity: 0.7;
}
.headline-side {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.side-news {
  flex: 1;
  display: flex;
  gap: 12px;
  cursor: pointer;
  padding: 12px;
  background: var(--color-bg-secondary);
  border-radius: 8px;
  transition: all 0.2s;
}
.side-news:hover {
  background: #e9ecef;
}
.side-img-wrap {
  width: 100px;
  height: 68px;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  background-color: var(--color-bg-secondary);
  border-radius: 4px;
  flex-shrink: 0;
}
.side-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}
.side-title {
  font-size: 14px;
  font-weight: 500;
  margin: 0 0 4px 0;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.side-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: #666;
}
.channel-tag-sm {
  background: var(--color-tag-bg);
  color: var(--color-tag-text);
  padding: 1px 6px;
  border-radius: 3px;
  font-size: 11px;
}
.breaking-tag-sm {
  background: #ff3b30;
  color: white;
  padding: 1px 6px;
  border-radius: 3px;
  font-size: 11px;
}
.main-content {
  display: grid;
  grid-template-columns: 1fr 1fr 280px;
  gap: 24px;
}
.section-title {
  font-size: 18px;
  font-weight: 600;
  margin: 0 0 16px 0;
  padding-bottom: 8px;
  border-bottom: 2px solid #3b82f6;
}
.news-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.news-item {
  display: flex;
  gap: 12px;
  cursor: pointer;
  padding: 12px;
  border-radius: 8px;
  transition: all 0.2s;
}
.news-item:hover {
  background: #f8f9fa;
}
.thumb-wrap {
  width: 120px;
  height: 80px;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  background-color: var(--color-bg-secondary);
  border-radius: 4px;
  flex-shrink: 0;
}
.news-body {
  flex: 1;
}
.news-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}
.news-title {
  font-size: 15px;
  font-weight: 500;
  margin: 0;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.news-summary {
  font-size: 13px;
  color: var(--color-text-secondary);
  margin: 4px 0 8px 0;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.news-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 12px;
  color: var(--color-text-light);
}
.news-stream {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.stream-item {
  padding: 12px;
  background: #f8f9fa;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}
.stream-item:hover {
  background: #e9ecef;
}
.stream-title {
  font-size: 14px;
  font-weight: 500;
  margin: 0 0 6px 0;
  line-height: 1.4;
}
.stream-summary {
  font-size: 12px;
  color: #666;
  margin: 0 0 8px 0;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.stream-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 11px;
  color: var(--color-text-light);
}
.card {
  background: var(--color-bg-white);
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  margin-bottom: 16px;
}
.sidebar-title {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 12px 0;
}
.ranking-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.ranking-item {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 8px;
  border-radius: 4px;
  transition: all 0.2s;
}
.ranking-item:hover {
  background: var(--color-bg-hover);
}
.rank-num {
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-bg-secondary);
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
}
.rank-hot {
  background: #ff3b30;
  color: white;
}
.rank-title {
  flex: 1;
  font-size: 13px;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.channel-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
.channel-card {
  padding: 6px 12px;
  background: #e3f2fd;
  color: #1976d2;
  border-radius: 16px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
}
.channel-card:hover {
  background: #1976d2;
  color: white;
}
.load-more-wrap {
  text-align: center;
  margin-top: 32px;
  padding-bottom: 32px;
}
.load-more-btn {
  padding: 10px 32px;
}
.ai-hot-card {
  background: var(--color-bg-white);
  border: 1px solid var(--color-border);
}
.ai-hot-card .sidebar-title {
  border-bottom-color: var(--color-primary);
  color: var(--color-text);
}
.ai-hot-prompt {
  display: flex;
  gap: 6px;
  margin-bottom: 8px;
}
.ai-hot-input {
  flex: 1;
  padding: 6px 10px;
  border: 1px solid var(--color-border);
  border-radius: 4px;
  font-size: 12px;
  outline: none;
}
.ai-hot-input:focus {
  border-color: var(--color-primary);
}
.btn-ai-sm {
  padding: 6px 12px;
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  color: #fff;
  border: none;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
  white-space: nowrap;
  transition: all 0.2s;
}
.btn-ai-sm:hover:not(:disabled) {
  background: linear-gradient(135deg, #4f46e5, #7c3aed);
}
.btn-ai-sm:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
.ai-hot-loading {
  font-size: 13px;
  color: #6366f1;
  padding: 8px 0;
  display: flex;
  align-items: center;
  gap: 8px;
}
.ai-spin {
  display: inline-block;
  width: 14px;
  height: 14px;
  border: 2px solid rgba(99, 102, 241, 0.3);
  border-top-color: #6366f1;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}
@keyframes spin {
  to { transform: rotate(360deg); }
}
.ai-hot-content {
  font-size: 13px;
  line-height: 1.7;
  color: var(--color-text);
  padding: 8px 0;
}
.ai-hot-content :deep(strong) {
  color: #4338ca;
}
.ai-hot-content :deep(p) {
  margin-bottom: 6px;
}
.ai-hot-actions {
  display: flex;
  gap: 6px;
  margin-top: 8px;
  padding-top: 8px;
  border-top: 1px solid var(--color-border);
}
.ai-hot-input-sm {
  flex: 1;
  padding: 4px 8px;
  border: 1px solid var(--color-border);
  border-radius: 4px;
  font-size: 11px;
  outline: none;
  background: var(--color-bg-white);
  color: var(--color-text);
}
.ai-hot-input-sm:focus {
  border-color: var(--color-primary);
}
@media (max-width: 1024px) {
  .main-content {
    grid-template-columns: 1fr 1fr;
  }
  .content-right {
    grid-column: span 2;
  }
}
@media (max-width: 768px) {
  .headline-section {
    flex-direction: column;
    gap: 12px;
  }
  .headline-main {
    aspect-ratio: 16 / 9;
    max-height: 280px;
    flex: none;
    border-radius: var(--radius-lg);
    overflow: hidden;
  }
  .headline-img-wrap {
    border-radius: var(--radius-lg);
    aspect-ratio: 16 / 9;
    max-height: 280px;
  }
  .headline-img, .side-img, .thumb-img {
    position: relative;
  }
  .headline-img {
    border-radius: var(--radius-lg);
    object-fit: cover;
  }
  .side-img {
    width: 80px;
    height: 56px;
    border-radius: var(--radius-sm);
    flex-shrink: 0;
    object-fit: cover;
  }
  .thumb-img {
    width: 85px;
    height: 65px;
    border-radius: var(--radius-sm);
    flex-shrink: 0;
  }
  .headline-overlay {
    padding: 16px;
    border-radius: var(--radius-lg);
    background: linear-gradient(transparent, rgba(0, 0, 0, 0.85));
  }
  .headline-title {
    font-size: 19px;
    font-weight: 700;
    line-height: 1.45;
    letter-spacing: 0.2px;
  }
  .headline-summary {
    display: none;
  }
  .headline-meta {
    flex-wrap: wrap;
    gap: 8px;
    font-size: 11.5px;
    opacity: 0.9;
  }
  .channel-tag {
    padding: 2px 8px;
    font-size: 10.5px;
    font-weight: 500;
  }
  .headline-side {
    flex-direction: row;
    overflow-x: auto;
    gap: 10px;
    padding-bottom: 6px;
    scrollbar-width: none;
    overscroll-behavior-x: contain;
  }
  .headline-side::-webkit-scrollbar {
    display: none;
  }
  .side-news {
    min-width: 170px;
    flex-shrink: 0;
    padding: 12px;
    border-radius: var(--radius-md);
    background: #fff;
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
    gap: 10px;
  }
  .side-news:active {
    transform: scale(0.98);
  }
  .side-img-wrap {
    width: 80px;
    height: 56px;
    border-radius: var(--radius-sm);
    flex-shrink: 0;
  }
  .side-content {
    justify-content: flex-start;
    padding-top: 2px;
  }
  .side-title {
    font-size: 13.5px;
    font-weight: 500;
    line-height: 1.4;
  }
  .side-meta {
    font-size: 11px;
    gap: 6px;
  }
  .main-content {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  .content-right {
    grid-column: span 1;
  }
  .section-title {
    font-size: 17px;
    padding-bottom: 8px;
    margin-bottom: 14px;
    font-weight: 600;
    border-bottom-width: 3px;
  }
  .news-list {
    gap: 10px;
  }
  .news-item {
    padding: 12px;
    background: var(--color-bg-white);
    border-radius: var(--radius-md);
    box-shadow: 0 1px 4px var(--color-card-shadow);
    gap: 12px;
    transition: box-shadow 0.15s;
  }
  .news-item:active {
    transform: scale(0.99);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  }
  .thumb-wrap {
    width: 85px;
    height: 65px;
    border-radius: var(--radius-sm);
    flex-shrink: 0;
  }
  .news-body {
    flex: 1;
    min-width: 0;
  }
  .news-header {
    gap: 6px;
    margin-bottom: 4px;
  }
  .news-title {
    font-size: 15px;
    font-weight: 600;
    line-height: 1.45;
    color: #1a2a4a;
  }
  .news-summary {
    -webkit-line-clamp: 1;
    font-size: 13px;
    color: #666;
    margin: 4px 0 6px 0;
    line-height: 1.5;
  }
  .news-meta {
    flex-wrap: wrap;
    gap: 8px;
    font-size: 11.5px;
    color: #999;
  }
  .news-stream {
    gap: 8px;
  }
  .stream-item {
    padding: 12px;
    border-radius: var(--radius-md);
    background: var(--color-bg-white);
    box-shadow: 0 1px 4px var(--color-card-shadow);
  }
  .stream-item:active {
    transform: scale(0.99);
  }
  .stream-title {
    font-size: 14.5px;
    font-weight: 500;
    line-height: 1.4;
  }
  .stream-summary {
    font-size: 12.5px;
    margin: 5px 0 7px 0;
    line-height: 1.5;
  }
  .stream-meta {
    font-size: 11px;
    gap: 6px;
  }
  .card {
    padding: 16px;
    border-radius: var(--radius-lg);
    margin-bottom: 14px;
    box-shadow: 0 1px 4px var(--color-card-shadow);
  }
  .sidebar-title {
    font-size: 16px;
    margin-bottom: 12px;
    font-weight: 600;
    padding-bottom: 8px;
    border-bottom-width: 2px;
  }
  .ai-hot-card {
    background: linear-gradient(135deg, #f8f7ff, #f0efff);
    border: 1px solid #e8e5f0;
  }
  .ai-hot-card .sidebar-title {
    color: #4338ca;
    border-bottom-color: #a78bfa;
  }
  .ai-hot-prompt {
    flex-direction: column;
    gap: 10px;
  }
  .ai-hot-input {
    padding: 10px 14px;
    font-size: 14px;
    border-radius: 8px;
    border: 1px solid #ddd6fe;
  }
  .ai-hot-input:focus {
    border-color: #8b5cf6;
    box-shadow: 0 0 0 3px rgba(139, 92, 246, 0.1);
  }
  .btn-ai-sm {
    padding: 10px 20px;
    font-size: 14px;
    border-radius: 8px;
    font-weight: 500;
  }
  .ai-hot-actions {
    flex-direction: column;
    gap: 8px;
    padding-top: 12px;
  }
  .ai-hot-input-sm {
    padding: 8px 12px;
    font-size: 13px;
    border-radius: 6px;
  }
  .ai-hot-content {
    font-size: 13px;
    line-height: 1.7;
    color: #1a2a4a;
    padding: 10px 0;
  }
  .ranking-list {
    gap: 6px;
  }
  .ranking-item {
    padding: 10px 12px;
    border-radius: var(--radius-md);
    transition: background 0.15s;
  }
  .ranking-item:active {
    background: var(--color-bg-hover);
  }
  .rank-num {
    width: 20px;
    height: 20px;
    font-size: 11.5px;
    font-weight: 700;
    border-radius: 4px;
  }
  .rank-title {
    font-size: 13.5px;
    line-height: 1.4;
  }
  .channel-list {
    gap: 8px;
  }
  .channel-card {
    padding: 6px 14px;
    font-size: 13px;
    border-radius: 16px;
    font-weight: 500;
    transition: all 0.15s;
  }
  .channel-card:active {
    transform: scale(0.95);
  }
  .load-more-wrap {
    margin-top: 24px;
    padding-bottom: 24px;
  }
  .load-more-btn {
    padding: 12px 32px;
    font-size: 15px;
    border-radius: 8px;
    font-weight: 500;
  }
  .content-center {
    order: 3;
  }
  .content-right {
    order: 2;
  }
  .breaking-tag-sm {
    padding: 2px 6px;
    font-size: 10px;
    font-weight: 700;
  }
}

@media (max-width: 480px) {
  .headline-main {
    aspect-ratio: 16 / 9;
    max-height: 190px;
  }
  .headline-img-wrap {
    aspect-ratio: 16 / 9;
    max-height: 190px;
  }
  .headline-title {
    font-size: 17px;
    line-height: 1.4;
  }
  .side-news {
    min-width: 155px;
    padding: 10px;
  }
  .side-img {
    width: 70px;
    height: 50px;
  }
  .side-img-wrap {
    width: 70px;
    height: 50px;
  }
  .news-title {
    font-size: 14px;
  }
  .news-summary {
    font-size: 12px;
  }
  .thumb-img {
    width: 75px;
    height: 55px;
  }
  .thumb-wrap {
    width: 75px;
    height: 55px;
  }
}
</style>
