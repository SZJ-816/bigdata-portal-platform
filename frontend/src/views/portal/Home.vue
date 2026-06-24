<template>
  <div class="home-page">
    <section class="headline-section">
      <div class="headline-main" @click="goNews(headline.id)">
        <img
          v-if="headline.imageUrl"
          :src="headlineImageUrl"
          :alt="headline.title"
          :data-channel="headline.channel"
          class="headline-img"
          fetchpriority="high"
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
            v-lazy="item.thumbUrl || item.imageUrl"
            :alt="item.title"
            :data-channel="item.channel"
            data-priority="high"
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

    <NewsList
      :left-news="leftNews"
      :center-news="centerNews"
      :loading="newsLoading"
      :error="newsError"
      @click-news="goNews"
      @click-channel="goChannel"
      @retry="fetchNews"
      @load-more="loadMore"
    >
      <template #sidebar>
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
      </template>
    </NewsList>

    <div v-if="showNewNewsTip" class="new-news-tip" @click="refreshWithNewNews">
      📰 有 {{ newNewsCount }} 条新新闻，点击刷新
    </div>
    <section class="channel-sections">
      <div v-for="ch in displayChannels" :key="ch.name" class="channel-section">
        <div class="channel-section-header">
          <h2 class="channel-section-title">
            <span class="channel-section-icon">
              <img :src="resolveAssetUrl(channelIconMap[ch.name] || '/images/channel-ai.svg')" :alt="ch.label" />
            </span>
            {{ ch.label }}
          </h2>
          <span class="channel-section-more" @click="goChannel(ch.name)">更多 →</span>
        </div>
        <div v-if="channelNewsMap[ch.name]?.length" class="channel-section-grid">
          <div
            v-for="item in channelNewsMap[ch.name].slice(0, 4)"
            :key="item.id"
            class="channel-news-card"
            @click="goNews(item.id)"
          >
            <img v-if="item.imageUrl" v-lazy="item.thumbUrl || item.imageUrl" :alt="item.title" data-priority="low" class="channel-news-img" />
            <div v-else class="channel-news-img-placeholder"></div>
            <div class="channel-news-body">
              <h3 class="channel-news-title">{{ item.title }}</h3>
              <p class="channel-news-summary">{{ item.summary }}</p>
              <div class="channel-news-meta">
                <span>{{ item.source }}</span>
                <span>{{ formatRelativeTime(item.publishTime) }}</span>
              </div>
            </div>
          </div>
        </div>
        <div v-else class="channel-section-empty">暂无新闻</div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { newsApi, behaviorApi } from '../../api'
import { channels, formatRelativeTime } from '../../utils'
import { cleanText, formatViewCount, CHANNEL_LABEL_MAP, resolveImageUrl, resolveAssetUrl } from '../../utils'
import NewsList from '../../components/portal/NewsList.vue'

const channelLabelMap = CHANNEL_LABEL_MAP

const router = useRouter()
const allNews = ref([])
const newsLoading = ref(true)
const newsError = ref(false)
let abortController = null
const newNewsCount = ref(0)
const showNewNewsTip = ref(false)
let sseSource = null
let pollingTimer = null

const headline = computed(() => {
  if (!allNews.value[0]) {
    return {
      title: newsError.value ? '无法连接服务器' : '加载中...',
      summary: newsError.value ? '请检查网络或服务器设置' : '',
      channelName: '',
      source: '',
      createdAt: Date.now(),
      viewCount: 0,
      imageUrl: '',
      id: null
    }
  }
  return allNews.value[0]
})

const headlineImageUrl = computed(() => {
  return resolveImageUrl(headline.value.imageUrl) || ''
})

const sideHeadlines = computed(() => allNews.value.slice(1, 4))

const leftNews = computed(() => allNews.value.slice(4, 9))

const centerNews = computed(() => allNews.value.slice(9, 14))

const hotNews = computed(() => allNews.value.slice(0, 10))

const allChannels = computed(() => channels)

const displayChannels = computed(() => channels)

const channelIconMap = {
  'AI': '/images/channel-ai.svg',
  'ai': '/images/channel-ai.svg',
  '大数据': '/images/channel-bigdata.svg',
  'bigdata': '/images/channel-bigdata.svg',
  '云计算': '/images/channel-cloud.svg',
  'cloud': '/images/channel-cloud.svg',
  '互联网': '/images/channel-internet.svg',
  'internet': '/images/channel-internet.svg',
  '硬件': '/images/channel-hardware.svg',
  'hardware': '/images/channel-hardware.svg',
  '创业': '/images/channel-startup.svg',
  'startup': '/images/channel-startup.svg',
  '安全': '/images/channel-security.svg',
  'security': '/images/channel-security.svg',
  '区块链': '/images/channel-blockchain.svg',
  'blockchain': '/images/channel-blockchain.svg',
  '数码': '/images/channel-digital.svg',
  'digital': '/images/channel-digital.svg',
  '汽车科技': '/images/channel-auto.svg',
  'auto': '/images/channel-auto.svg'
}

// 英文channel_key → 前端频道name的映射
const channelKeyToName = {
  'ai': 'AI',
  'bigdata': '大数据',
  'cloud': '云计算',
  'internet': '互联网',
  'hardware': '硬件',
  'startup': '创业',
  'security': '安全',
  'blockchain': '区块链',
  'digital': '数码',
  'auto': '汽车科技'
}

const channelNewsMap = ref({})
const channelSectionsLoaded = ref(false)
let channelObserver = null

function setupChannelObserver() {
  if (channelSectionsLoaded.value) return
  const el = document.querySelector('.channel-sections')
  if (!el) return
  channelObserver = new IntersectionObserver((entries) => {
    if (entries[0].isIntersecting) {
      if (!channelSectionsLoaded.value) {
        channelSectionsLoaded.value = true
        fetchChannelNews()
      }
      channelObserver?.disconnect()
    }
  }, { rootMargin: '200px' })
  channelObserver.observe(el)
}

function connectSSE() {
  // SSE not available, use polling directly
  startPolling()
}

function startPolling() {
  if (pollingTimer) return
  pollingTimer = setInterval(async () => {
    try {
      const res = await newsApi.getList()
      if (res.data.data) {
        const pageData = res.data.data
        const items = Array.isArray(pageData) ? pageData : (pageData.records || pageData.data || [])
        if (items.length > 0 && allNews.value.length > 0 && items[0].id !== allNews.value[0].id) {
          newNewsCount.value++
          showNewNewsTip.value = true
        }
      }
    } catch (e) {
      console.warn('Polling failed:', e)
    }
  }, 30000)
}

function refreshWithNewNews() {
  showNewNewsTip.value = false
  newNewsCount.value = 0
  fetchNews()
}

function stopPolling() {
  if (pollingTimer) {
    clearInterval(pollingTimer)
    pollingTimer = null
  }
}

async function fetchChannelNews() {
  try {
    const res = await newsApi.getChannelsNews(4)
    const body = res.data
    // 兼容 {data: {...}} 和 {code:200, data: {...}} 两种格式
    const channelData = body?.data?.data || body?.data || {}
    if (channelData && typeof channelData === 'object') {
      const map = {}
      for (const [channelKey, items] of Object.entries(channelData)) {
        const list = Array.isArray(items) ? items : []
        // 将英文channelKey映射为前端频道name（如"ai"→"AI"）
        const channelName = channelKeyToName[channelKey] || channelKey
        map[channelName] = list.map(item => ({
          ...item,
          id: item.id || item.articleId,
          title: cleanText(item.title),
          summary: cleanText(item.summary),
          channel: item.channel || channelKey,
          channelName: item.channelName || channelLabelMap[item.channel] || channelLabelMap[channelKey] || channelName,
          imageUrl: item.imageUrl || item.coverImage || '',
          thumbUrl: item.thumbUrl || item.coverImage || '',
          createdAt: item.createdAt || item.publishTime || item.createTime,
          viewCount: item.viewCount || 0,
          source: item.source || ''
        }))
      }
      channelNewsMap.value = map
    }
  } catch (err) {
    console.warn('Failed to load channel news:', err.message)
    const map = {}
    channels.forEach(ch => { map[ch.name] = [] })
    channelNewsMap.value = map
  }
}

function goNews(id) {
  if (id) router.push(`/news/${id}`)
}

function goChannel(name) {
  const displayName = channelLabelMap[name] || name
  router.push(`/channel/${displayName}`)
}

function loadMore() {
  router.push('/channel/互联网')
  behaviorApi.report({ eventType: 'click', targetId: 'load_more', targetType: 'button' })
}

async function fetchNews() {
  const cached = localStorage.getItem('cached_news')
  if (cached && allNews.value.length === 0) {
    try {
      allNews.value = JSON.parse(cached)
      newsLoading.value = false
    } catch (e) { /* ignore */ }
  }
  if (allNews.value.length === 0) {
    newsLoading.value = true
  }
  newsError.value = false
  try {
    const res = await newsApi.getList()
    // 网络错误兜底：拦截器返回空响应时设置错误状态
    if (res._isNetworkError || (res.data && res.data.success === false && res.status === 0)) {
      if (allNews.value.length === 0) {
        newsError.value = true
      }
      newsLoading.value = false
      return
    }
    const body = res.data
    // 兼容多种 API 返回格式: {data:{records:[]}} / {records:[]} / {data:[]}
    let items = []
    if (body?.data?.records) {
      items = body.data.records
    } else if (body?.data?.data) {
      items = body.data.data
    } else if (Array.isArray(body?.data)) {
      items = body.data
    } else if (Array.isArray(body)) {
      items = body
    } else if (body?.records) {
      items = body.records
    }
    if (items.length > 0) {
      const mapped = items.map(item => ({
        ...item,
        id: item.id || item.articleId,
        title: cleanText(item.title),
        summary: cleanText(item.summary),
        channel: item.channel || '',
        channelName: item.channelName || channelLabelMap[item.channel] || item.channel || '',
        imageUrl: item.imageUrl || item.coverImage || '',
        thumbUrl: item.thumbUrl || item.coverImage || '',
        createdAt: item.createdAt || item.publishTime || item.createTime,
        viewCount: item.viewCount || 0,
        commentCount: item.commentCount || 0,
        source: item.source || '',
        isBreaking: item.isBreaking || item.isTop === 1
      }))
      allNews.value = mapped
      try { localStorage.setItem('cached_news', JSON.stringify(mapped)) } catch (e) { /* ignore */ }
    }
  } catch (err) {
    console.error('Failed to load news:', err)
    if (allNews.value.length === 0) {
      newsError.value = true
    }
  }
  newsLoading.value = false
}

onMounted(async () => {
  await fetchNews()
  nextTick(() => setupChannelObserver())
  connectSSE()
  behaviorApi.report({ eventType: 'page_view', targetId: 'home', targetType: 'page' })
})

onUnmounted(() => {
  behaviorApi.flush()
  if (channelObserver) {
    channelObserver.disconnect()
    channelObserver = null
  }
  if (sseSource) {
    sseSource.close()
    sseSource = null
  }
  stopPolling()
})
</script>

<style scoped>
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
.headline-img, .side-img {
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
.breaking-tag {
  position: absolute;
  top: 16px;
  left: 16px;
  background: var(--color-red);
  color: var(--color-text-white);
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
  background: linear-gradient(transparent, var(--color-overlay-dark));
  color: var(--color-text-white);
}
.headline-title {
  font-size: 24px;
  font-weight: 600;
  margin: 0 0 8px 0;
  line-height: 1.3;
  color: var(--color-text-white);
  text-shadow: 0 1px 3px rgba(0, 0, 0, 0.4);
}
.headline-summary {
  font-size: 14px;
  opacity: 0.9;
  margin: 0 0 12px 0;
  line-height: 1.5;
  color: var(--color-text-white);
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.4);
}
.headline-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 12px;
  color: var(--color-text-white);
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
  background: var(--color-border-light);
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
  color: var(--color-text-secondary);
}
.channel-tag-sm {
  background: var(--color-tag-bg);
  color: var(--color-tag-text);
  padding: 1px 6px;
  border-radius: 3px;
  font-size: 11px;
}
.breaking-tag-sm {
  background: var(--color-red);
  color: var(--color-text-white);
  padding: 1px 6px;
  border-radius: 3px;
  font-size: 11px;
}
.card {
  background: var(--color-bg-white);
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 1px 3px var(--color-shadow-lg);
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
  background: var(--color-red);
  color: var(--color-text-white);
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
  background: var(--color-blue-bg);
  color: var(--color-blue-dark);
  border-radius: 16px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
}
.channel-card:hover {
  background: var(--color-blue-dark);
  color: var(--color-text-white);
}
.new-news-tip {
  position: sticky;
  top: 60px;
  z-index: 10;
  background: var(--color-primary);
  color: var(--color-text-white);
  text-align: center;
  padding: 10px 16px;
  border-radius: var(--radius-md, 8px);
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 16px;
  transition: background 0.2s;
  box-shadow: 0 2px 8px var(--color-shadow-md, rgba(0,0,0,0.12));
}
.new-news-tip:hover {
  opacity: 0.9;
}
.channel-sections {
  margin-top: 40px;
}
.channel-section {
  margin-bottom: 32px;
}
.channel-section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
  padding-bottom: 10px;
  border-bottom: 2px solid var(--color-border-light);
}
.channel-section-title {
  font-size: 18px;
  font-weight: 700;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 8px;
}
.channel-section-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
}
.channel-section-icon img {
  width: 24px;
  height: 24px;
  object-fit: contain;
}
.channel-section-more {
  font-size: 13px;
  color: var(--color-text-secondary);
  cursor: pointer;
  transition: color 0.2s;
}
.channel-section-more:hover {
  color: var(--color-primary);
}
.channel-section-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}
.channel-news-card {
  display: flex;
  gap: 12px;
  padding: 12px;
  background: var(--color-bg-white);
  border-radius: 8px;
  box-shadow: 0 1px 3px var(--color-card-shadow);
  cursor: pointer;
  transition: box-shadow 0.2s;
  height: 104px;
  max-height: 104px;
  box-sizing: border-box;
  overflow: hidden;
}
.channel-news-card:hover {
  box-shadow: 0 3px 10px var(--color-shadow-md);
}
.channel-news-img {
  width: 120px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
  flex-shrink: 0;
}
.channel-news-img-placeholder {
  width: 120px;
  height: 80px;
  border-radius: 4px;
  flex-shrink: 0;
  background: var(--color-skeleton);
}
.channel-news-body {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}
.channel-news-title {
  font-size: 14px;
  font-weight: 600;
  margin: 0 0 6px 0;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.channel-news-summary {
  font-size: 12px;
  color: var(--color-text-secondary);
  margin: 0 0 6px 0;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.channel-news-meta {
  display: flex;
  gap: 10px;
  font-size: 11px;
  color: var(--color-text-light);
}
.channel-section-empty {
  text-align: center;
  padding: 24px;
  color: var(--color-text-light);
  font-size: 14px;
  background: var(--color-bg-secondary);
  border-radius: 8px;
}
@media (max-width: 1024px) {
  .ranking-list {
    gap: 8px;
  }
  .channel-section-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }
}
@media (max-width: 768px) {
  .headline-section {
    flex-direction: column;
    gap: 12px;
    padding: 0 4px;
    overflow-x: hidden;
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
  .headline-img, .side-img {
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
  .headline-overlay {
    padding: 16px;
    border-radius: var(--radius-lg);
    background: linear-gradient(transparent, var(--color-overlay-medium));
  }
  .headline-title {
    font-size: 19px;
    font-weight: 700;
    line-height: 1.45;
    letter-spacing: 0.2px;
    color: var(--color-text-white);
  }
  .headline-summary {
    display: none;
  }
  .headline-meta {
    flex-wrap: wrap;
    gap: 8px;
    font-size: 11.5px;
    opacity: 0.9;
    color: var(--color-text-white);
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
    background: var(--color-bg-white);
    box-shadow: 0 1px 4px var(--color-card-shadow);
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
  .card {
    padding: 16px;
    border-radius: var(--radius-lg);
    margin-bottom: 14px;
    box-shadow: 0 1px 4px var(--color-card-shadow);
    overflow-x: hidden;
  }
  .sidebar-title {
    font-size: 16px;
    margin-bottom: 12px;
    font-weight: 600;
    padding-bottom: 8px;
    border-bottom-width: 2px;
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
  .breaking-tag-sm {
    padding: 2px 6px;
    font-size: 10px;
    font-weight: 700;
  }
  .channel-sections {
    margin-top: 24px;
  }
  .channel-section {
    margin-bottom: 20px;
  }
  .channel-section-header {
    margin-bottom: 12px;
    padding-bottom: 8px;
  }
  .channel-section-title {
    font-size: 16px;
    font-weight: 600;
  }
  .channel-section-grid {
    grid-template-columns: 1fr;
    gap: 10px;
  }
  .channel-news-card {
    padding: 12px;
    border-radius: var(--radius-md);
    gap: 10px;
    height: 104px;
    max-height: 104px;
  }
  .channel-news-img {
    width: 90px;
    height: 65px;
    border-radius: var(--radius-sm);
  }
  .channel-news-img-placeholder {
    width: 90px;
    height: 65px;
    border-radius: var(--radius-sm);
  }
  .channel-news-title {
    font-size: 14px;
  }
  .channel-news-summary {
    font-size: 12px;
  }
}
@media (max-width: 480px) {
  .headline-section {
    padding: 0;
  }
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
  .channel-card {
    padding: 5px 12px;
    font-size: 12px;
  }
}
@media (max-width: 375px) {
  .headline-main {
    max-height: 170px;
  }
  .headline-img-wrap {
    max-height: 170px;
  }
  .headline-title {
    font-size: 16px;
  }
  .side-news {
    min-width: 145px;
    padding: 10px;
  }
  .side-img {
    width: 60px;
    height: 45px;
  }
  .side-img-wrap {
    width: 60px;
    height: 45px;
  }
  .side-title {
    font-size: 12.5px;
  }
  .ranking-item {
    padding: 8px 10px;
  }
  .rank-title {
    font-size: 12.5px;
  }
}
</style>
