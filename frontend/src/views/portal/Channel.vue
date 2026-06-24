<template>
  <div class="channel-page">
    <div class="channel-header">
      <h1 class="channel-title">{{ channelInfo?.label || channelName }}</h1>
      <p class="channel-desc">{{ channelDesc }}</p>
    </div>
    <div class="channel-content">
      <div class="content-main">
        <div v-if="loading" class="loading-skeleton">
          <div v-for="i in 5" :key="i" class="skeleton-item">
            <div class="skeleton-thumb"></div>
            <div class="skeleton-content">
              <div class="skeleton-title"></div>
              <div class="skeleton-meta"></div>
            </div>
          </div>
        </div>
        <div v-else-if="error" class="error-state">
          <div class="error-icon"><svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M12 2L2 20h20L12 2z"/><path d="M12 9v4"/><circle cx="12" cy="16" r="0.5" fill="currentColor"/></svg></div>
          <p>{{ error }}</p>
          <button class="btn btn-primary" @click="loadChannelNews">重试</button>
        </div>
        <div v-else>
          <div class="news-list">
            <div v-for="item in channelNews" :key="item.id" class="news-item" @click="goNews(item.id)" role="button" tabindex="0" @keydown.enter="goNews(item.id)">
              <img v-if="item.imageUrl" v-lazy="item.imageUrl" :alt="item.title" :data-channel="item.channel" class="news-thumb" />
              <div v-else class="news-thumb-placeholder"></div>
              <div class="news-body">
                <div class="news-header">
                  <h3 class="news-title">{{ item.title }}</h3>
                  <span v-if="item.isBreaking" class="breaking-tag-sm">BREAKING</span>
                </div>
                <p class="news-summary">{{ item.summary }}</p>
                <div class="news-meta">
                  <span>{{ item.source }}</span>
                  <span>{{ formatRelativeTime(item.publishTime) }}</span>
                  <span>{{ formatViewCount(item.viewCount) }} 阅读</span>
                  <span>{{ item.commentCount }} 评论</span>
                </div>
              </div>
            </div>
          </div>
          <div v-if="!channelNews.length" class="empty-state">
            <div class="empty-icon"><svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M4 4h12a2 2 0 012 2v12a2 2 0 01-2 2H4a2 2 0 01-2-2V6a2 2 0 012-2z"/><path d="M2 8h16"/><path d="M8 8v12"/></svg></div>
            <p>该频道暂无新闻，去看看其他频道吧</p>
          </div>
          <div v-if="hasMore" class="load-more-wrap">
            <button class="btn btn-outline load-more-btn" @click="loadMoreNews" :disabled="loadingMore">
              {{ loadingMore ? '加载中...' : '加载更多' }}
            </button>
            <p v-if="loadMoreError" class="load-more-error">{{ loadMoreError }}</p>
          </div>
        </div>
      </div>
      <div class="content-sidebar">
        <div class="card hot-section">
          <h3 class="sidebar-title">频道热点</h3>
          <div class="hot-list">
            <div v-for="(item, idx) in channelHotNews" :key="item.id" class="hot-item" @click="goNews(item.id)">
              <span :class="['rank-num', { 'rank-hot': idx < 3 }]">{{ idx + 1 }}</span>
              <div class="hot-info">
                <span class="hot-title">{{ item.title }}</span>
                <span class="hot-meta">{{ formatViewCount(item.viewCount) }} 阅读</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { channels, formatRelativeTime } from '../../utils'
import { behaviorApi, newsApi } from '../../api'
import { cleanText, formatViewCount, CHANNEL_LABEL_MAP } from '../../utils'

const route = useRoute()
const router = useRouter()

const channelLabelMap = CHANNEL_LABEL_MAP

const channelName = computed(() => route.params.name)
const channelInfo = computed(() => {
  const actualChannelName = channelNameMap[channelName.value] || channelName.value
  let info = channels.find(c => c.name === actualChannelName)
  if (!info) {
    info = channels.find(c => c.label === channelName.value)
  }
  return info
})
const channelNews = ref([])
const currentPage = ref(1)
const hasMore = ref(true)
const loading = ref(false)
const loadingMore = ref(false)
const error = ref('')
const loadMoreError = ref('')
const channelHotNews = computed(() => {
  return [...channelNews.value]
    .sort((a, b) => (b.viewCount || 0) - (a.viewCount || 0))
    .slice(0, 5)
})

const channelNameMap = {
  '人工智能': 'ai',
  'AI': 'ai',
  '大数据': 'bigdata',
  '云计算': 'cloud',
  '互联网': 'internet',
  '硬件': 'hardware',
  '创业': 'startup',
  '安全': 'security',
  '区块链': 'blockchain',
  '数码': 'digital',
  '汽车科技': 'auto',
}

async function loadChannelNews() {
  loading.value = true
  error.value = ''
  try {
    const channelKey = channelNameMap[channelName.value] || channelName.value
    const res = await newsApi.getByChannel(channelKey)
    const body = res.data
    // 兼容 {code:200, data:[...]} 和 {success:true, data:{...}} 两种格式
    const pageData = body?.data?.data || body?.data || body
    const items = Array.isArray(pageData) ? pageData : (pageData?.records || pageData?.data || [])
    channelNews.value = items.map(item => ({
      ...item,
      id: item.id || item.articleId,
      title: cleanText(item.title),
      summary: cleanText(item.summary),
      channel: item.channel || '',
      channelName: item.channelName || channelLabelMap[item.channel] || '',
      imageUrl: item.imageUrl || item.coverImage || '',
      thumbUrl: item.thumbUrl || item.coverImage || '',
      createdAt: item.createdAt || item.publishTime || item.createTime,
      viewCount: item.viewCount || 0,
      commentCount: item.commentCount || 0,
      source: item.source || '',
      isBreaking: item.isBreaking || item.isTop === 1
    }))
  } catch (err) {
    console.error('Failed to load channel news:', err)
    error.value = '加载失败，请检查网络连接'
  }
  loading.value = false
}

async function loadMoreNews() {
  if (loadingMore.value || !hasMore.value) return
  loadingMore.value = true
  loadMoreError.value = ''
  currentPage.value++
  try {
    const channelKey = channelNameMap[channelName.value] || channelName.value
    const res = await newsApi.getByChannel(channelKey, currentPage.value)
    const body = res.data
    const pageData = body?.data?.data || body?.data || body
    const items = Array.isArray(pageData) ? pageData : (pageData?.records || pageData?.data || [])
    if (items.length > 0) {
      channelNews.value = [...channelNews.value, ...items.map(item => ({
        ...item,
        id: item.id || item.articleId,
        title: cleanText(item.title),
        summary: cleanText(item.summary),
        channel: item.channel || '',
        channelName: item.channelName || channelLabelMap[item.channel] || '',
        imageUrl: item.imageUrl || item.coverImage || '',
        thumbUrl: item.thumbUrl || item.coverImage || '',
        createdAt: item.createdAt || item.publishTime || item.createTime,
        viewCount: item.viewCount || 0,
        commentCount: item.commentCount || 0,
        source: item.source || '',
        isBreaking: item.isBreaking || item.isTop === 1
      }))]
    } else {
      hasMore.value = false
    }
  } catch (err) {
    currentPage.value--
    loadMoreError.value = '加载失败，请稍后重试'
  }
  loadingMore.value = false
}

const channelDescs = {
  'AI': '人工智能前沿动态，大模型、机器学习、计算机视觉等领域最新进展',
  '人工智能': '人工智能前沿动态，大模型、机器学习、计算机视觉等领域最新进展',
  '大数据': '大数据技术生态，流计算、批处理、数据湖与数据治理',
  '云计算': '云原生与云计算，容器、微服务、DevOps 最新实践',
  '互联网': '互联网行业观察，平台经济与数字化转型',
  '硬件': '芯片与硬件，半导体、处理器、通信技术前沿',
  '创业': '科技创业，融资、投资与商业模式创新',
  '安全': '网络安全与信息安全，漏洞防护与隐私保护',
  '区块链': '区块链与Web3，加密货币与去中心化技术',
  '数码': '数码产品与消费电子，手机、电脑、智能设备',
  '汽车科技': '汽车科技，新能源、智能驾驶与车联网',
}

const channelDesc = computed(() => {
  const actualChannelName = channelNameMap[channelName.value] || channelName.value
  return channelDescs[actualChannelName] || '科技新闻资讯'
})

function goNews(id) {
  behaviorApi.report({ eventType: 'click', targetId: String(id), targetType: 'news' })
  router.push(`/news/${id}`)
}

onMounted(async () => {
  await loadChannelNews()
  behaviorApi.report({ eventType: 'page_view', targetId: channelName.value, targetType: 'channel' })
})

onUnmounted(() => {
  behaviorApi.flush()
})

watch(() => route.params.name, async () => {
  if (route.params.name) {
    currentPage.value = 1
    hasMore.value = true
    await loadChannelNews()
    behaviorApi.report({ eventType: 'page_view', targetId: channelName.value, targetType: 'channel' })
  }
})
</script>

<style scoped>
.channel-header {
  padding: 24px 0 20px;
  border-bottom: 3px solid var(--color-primary);
  margin-bottom: 24px;
}
.channel-title {
  font-size: 28px;
  font-weight: 800;
  color: var(--color-text);
  font-family: Georgia, 'Noto Serif SC', serif;
}
.channel-desc {
  margin-top: 8px;
  font-size: 14px;
  color: var(--color-text-secondary);
}
.channel-content {
  display: flex;
  gap: 20px;
  align-items: flex-start;
}
.content-main {
  flex: 3;
}
.content-sidebar {
  flex: 1;
  min-width: 240px;
}
.news-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.news-item {
  display: flex;
  gap: 16px;
  padding: 16px;
  background: var(--color-bg-white);
  border-radius: 4px;
  box-shadow: 0 1px 3px var(--color-card-shadow);
  cursor: pointer;
  transition: box-shadow 0.2s;
  height: 142px;
  box-sizing: border-box;
  overflow: hidden;
}
.news-item:hover {
  box-shadow: 0 3px 10px var(--color-shadow-md);
}
.news-item:hover .news-title {
  text-decoration: underline;
}
.news-thumb {
  width: 180px;
  height: 110px;
  object-fit: cover;
  object-position: center;
  border-radius: 3px;
  flex-shrink: 0;
  background-color: var(--color-bg-tertiary);
}
.news-thumb-placeholder {
  width: 180px;
  height: 110px;
  border-radius: 3px;
  flex-shrink: 0;
  background: var(--color-skeleton);
  animation: skeleton-shine 1.5s ease-in-out infinite;
}
@keyframes skeleton-shine {
  0% { background: var(--color-skeleton); }
  50% { background: var(--color-skeleton-shine); }
  100% { background: var(--color-skeleton); }
}
.news-body {
  flex: 1;
  min-width: 0;
}
.news-header {
  display: flex;
  align-items: center;
  gap: 8px;
}
.news-title {
  font-size: 17px;
  font-weight: 600;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.breaking-tag-sm {
  display: inline-block;
  background: var(--color-accent);
  color: var(--color-text-white);
  padding: 1px 8px;
  font-size: 10px;
  font-weight: 700;
  letter-spacing: 1px;
  border-radius: 2px;
  flex-shrink: 0;
}
.news-summary {
  font-size: 13px;
  color: var(--color-text-secondary);
  line-height: 1.5;
  margin-top: 8px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.news-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 10px;
  font-size: 12px;
  color: var(--color-text-light);
}
.sidebar-title {
  font-size: 16px;
  font-weight: 700;
  margin-bottom: 14px;
  padding-bottom: 8px;
  border-bottom: 2px solid var(--color-primary);
}
.hot-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.hot-item {
  display: flex;
  gap: 10px;
  cursor: pointer;
  padding: 4px 0;
}
.hot-item:hover .hot-title {
  text-decoration: underline;
}
.rank-num {
  width: 22px;
  height: 22px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 700;
  border-radius: 3px;
  flex-shrink: 0;
  background: var(--color-bg-tertiary);
  color: var(--color-text-light);
}
.rank-hot {
  background: var(--color-accent);
  color: var(--color-text-white);
}
.hot-info {
  flex: 1;
  min-width: 0;
}
.hot-title {
  font-size: 13px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.hot-meta {
  font-size: 11px;
  color: var(--color-text-light);
  margin-top: 2px;
}
.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: var(--color-text-light);
  font-size: 15px;
}
.empty-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-text-light);
  margin-bottom: 12px;
}
.empty-icon svg {
  width: 48px;
  height: 48px;
}
.error-state {
  text-align: center;
  padding: 60px 20px;
  color: var(--color-text-secondary);
  font-size: 15px;
}
.error-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-text-light);
  margin-bottom: 12px;
}
.error-icon svg {
  width: 48px;
  height: 48px;
}
.loading-skeleton {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.skeleton-item {
  display: flex;
  gap: 16px;
  padding: 16px;
  background: var(--color-bg-white);
  border-radius: 4px;
  box-shadow: 0 1px 3px var(--color-card-shadow);
}
.skeleton-thumb {
  width: 180px;
  height: 110px;
  border-radius: 3px;
  flex-shrink: 0;
  background: var(--color-skeleton);
  animation: skeleton-shine 1.5s ease-in-out infinite;
}
.skeleton-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.skeleton-title {
  height: 24px;
  width: 80%;
  background: var(--color-skeleton);
  border-radius: 2px;
  animation: skeleton-shine 1.5s ease-in-out infinite;
}
.skeleton-meta {
  height: 12px;
  width: 40%;
  background: var(--color-skeleton);
  border-radius: 2px;
  animation: skeleton-shine 1.5s ease-in-out infinite;
  margin-top: auto;
}
.load-more-wrap {
  text-align: center;
  padding: 20px 0;
}
.load-more-btn {
  min-width: 160px;
}
.load-more-error {
  margin-top: 8px;
  color: var(--color-accent);
  font-size: 13px;
}
@media (max-width: 768px) {
  .channel-header {
    padding: 16px 0 12px;
    margin-bottom: 16px;
    border-bottom-width: 2px;
  }
  .channel-title {
    font-size: 22px;
    font-weight: 700;
    letter-spacing: 0.3px;
  }
  .channel-desc {
    font-size: 13px;
    margin-top: 6px;
    color: var(--color-text-secondary);
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }
  .channel-content {
    flex-direction: column;
    gap: 20px;
  }
  .content-sidebar {
    min-width: auto;
    order: 2;
  }
  .content-main {
    order: 1;
  }
  .news-list {
    gap: 10px;
  }
  .news-item {
    flex-direction: row;
    gap: 12px;
    padding: 12px;
    border-radius: var(--radius-lg);
    box-shadow: 0 1px 4px var(--color-card-shadow);
    background: var(--color-bg-white);
    transition: box-shadow 0.15s;
    height: 104px;
    box-sizing: border-box;
    overflow: hidden;
  }
  .news-item:active {
    transform: scale(0.99);
    box-shadow: 0 2px 8px var(--color-shadow-lg);
  }
  .news-thumb {
    width: 110px;
    height: 80px;
    border-radius: var(--radius-md);
    object-fit: cover;
    flex-shrink: 0;
  }
  .news-thumb-placeholder {
    width: 110px;
    height: 80px;
    border-radius: var(--radius-md);
    flex-shrink: 0;
  }
  .news-body {
    flex: 1;
    min-width: 0;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
  }
  .news-header {
    gap: 6px;
  }
  .news-title {
    font-size: 15px;
    font-weight: 600;
    line-height: 1.45;
    color: var(--color-text);
    -webkit-line-clamp: 2;
  }
  .news-summary {
    font-size: 12px;
    margin-top: 4px;
    line-height: 1.5;
    color: var(--color-text-light);
    -webkit-line-clamp: 1;
  }
  .news-meta {
    flex-wrap: wrap;
    gap: 6px;
    font-size: 11px;
    margin-top: 6px;
    color: var(--color-text-light);
  }
  .breaking-tag-sm {
    padding: 1px 6px;
    font-size: 9px;
  }
  .card {
    padding: 16px;
    border-radius: var(--radius-lg);
    background: var(--color-bg-white);
    box-shadow: 0 1px 4px var(--color-card-shadow);
  }
  .sidebar-title {
    font-size: 16px;
    margin-bottom: 12px;
    padding-bottom: 8px;
    font-weight: 600;
    border-bottom-width: 2px;
  }
  .hot-list {
    gap: 8px;
  }
  .hot-item {
    padding: 10px 12px;
    border-radius: var(--radius-md);
    transition: background 0.15s;
    display: flex;
    align-items: center;
  }
  .hot-item:active {
    background: var(--color-bg);
  }
  .rank-num {
    width: 20px;
    height: 20px;
    font-size: 11.5px;
    font-weight: 700;
    border-radius: 4px;
    flex-shrink: 0;
  }
  .hot-title {
    font-size: 14px;
    line-height: 1.4;
    -webkit-line-clamp: 2;
  }
  .hot-meta {
    font-size: 11.5px;
    color: var(--color-text-light);
    margin-top: 2px;
  }
  .empty-state {
    padding: 40px 20px;
    font-size: 14px;
  }
  .loading-skeleton {
    gap: 10px;
  }
  .skeleton-item {
    gap: 12px;
    padding: 12px;
    border-radius: var(--radius-lg);
  }
  .skeleton-thumb {
    width: 110px;
    height: 80px;
    border-radius: var(--radius-md);
  }
}

@media (max-width: 480px) {
  .news-item {
    height: 92px;
  }
  .news-thumb {
    width: 90px;
    height: 68px;
  }
  .news-thumb-placeholder {
    width: 90px;
    height: 68px;
  }
  .news-title {
    font-size: 14px;
  }
  .news-summary {
    font-size: 11.5px;
  }
  .news-meta {
    font-size: 10.5px;
  }
  .skeleton-thumb {
    width: 90px;
    height: 68px;
  }
}
</style>
