<template>
  <div class="channel-page">
    <div class="channel-header">
      <h1 class="channel-title">{{ channelInfo?.label || channelName }}</h1>
      <p class="channel-desc">{{ channelDesc }}</p>
    </div>
    <div class="channel-content">
      <div class="content-main">
        <div class="news-list">
          <div v-for="item in channelNews" :key="item.id" class="news-item" @click="goNews(item.id)">
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
          <p>该频道暂无新闻</p>
        </div>
        <div v-if="hasMore" class="load-more-wrap">
          <button class="btn btn-outline load-more-btn" @click="loadMoreNews" :disabled="loadingMore">
            {{ loadingMore ? '加载中...' : '加载更多' }}
          </button>
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
import { cleanText, formatViewCount } from '../../utils'

const route = useRoute()
const router = useRouter()

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
const loadingMore = ref(false)
const channelHotNews = computed(() => {
  return [...channelNews.value]
    .sort((a, b) => (b.viewCount || 0) - (a.viewCount || 0))
    .slice(0, 5)
})

const channelNameMap = {
  '人工智能': 'AI',
  'AI': 'AI',
  '大数据': '大数据',
  '云计算': '云计算',
  '互联网': '互联网',
  '硬件': '硬件',
  '创业': '创业'
}

async function loadChannelNews() {
  try {
    const actualChannelName = channelNameMap[channelName.value] || channelName.value
    const res = await newsApi.getByChannel(actualChannelName)
    if (res.data.data) {
      channelNews.value = res.data.data.map(item => ({
        ...item,
        title: cleanText(item.title),
        summary: cleanText(item.summary)
      }))
    }
  } catch (err) {
    console.error('Failed to load channel news:', err)
  }
}

async function loadMoreNews() {
  if (loadingMore.value || !hasMore.value) return
  loadingMore.value = true
  currentPage.value++
  try {
    const actualChannelName = channelNameMap[channelName.value] || channelName.value
    const res = await newsApi.getByChannel(actualChannelName, currentPage.value)
    if (res.data.data && res.data.data.length > 0) {
      channelNews.value = [...channelNews.value, ...res.data.data.map(item => ({
        ...item,
        title: cleanText(item.title),
        summary: cleanText(item.summary)
      }))]
    } else {
      hasMore.value = false
    }
  } catch (err) {
    currentPage.value--
  }
  loadingMore.value = false
}

const channelDescs = {
  'AI': '人工智能前沿动态，大模型、机器学习、计算机视觉等领域最新进展',
  '人工智能': '人工智能前沿动态，大模型、机器学习、计算机视觉等领域最新进展',
  '大数据': '大数据技术生态，实时计算、实时计算、数据湖',
  '云计算': '云原生与云计算',
  '互联网': '互联网行业观察',
  '硬件': '芯片与硬件',
  '创业': '科技创业',
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
}
.news-item:hover {
  box-shadow: 0 3px 10px rgba(0,0,0,0.08);
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
  background-color: #f0f0f0;
}
.news-thumb-placeholder {
  width: 180px;
  height: 110px;
  border-radius: 3px;
  flex-shrink: 0;
  background: linear-gradient(135deg, #1a2a4a 0%, #2d4a7a 100%);
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
  color: #fff;
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
  border-bottom: 2px solid #1a2a4a;
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
  background: #e8e8e8;
  color: #999;
}
.rank-hot {
  background: #c41230;
  color: #fff;
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
.load-more-wrap {
  text-align: center;
  padding: 20px 0;
}
.load-more-btn {
  min-width: 160px;
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
  }
  .news-item:active {
    transform: scale(0.99);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
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
    color: #1a2a4a;
    -webkit-line-clamp: 2;
  }
  .news-summary {
    font-size: 12px;
    margin-top: 4px;
    line-height: 1.5;
    color: #888;
    -webkit-line-clamp: 1;
  }
  .news-meta {
    flex-wrap: wrap;
    gap: 6px;
    font-size: 11px;
    margin-top: 6px;
    color: #999;
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
    background: #f5f5f5;
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
}

@media (max-width: 480px) {
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
}
</style>
