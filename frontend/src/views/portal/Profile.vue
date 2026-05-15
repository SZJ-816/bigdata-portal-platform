<template>
  <div class="profile-page">
    <div v-if="!isLoggedIn" class="login-prompt">
      <div class="prompt-content">
        <h3>请先登录</h3>
        <p>查看个人中心需要登录账号</p>
        <button class="btn btn-primary" @click="goLogin">去登录</button>
      </div>
    </div>

    <template v-else>
      <div class="profile-header card">
        <div class="profile-info">
          <div class="avatar">{{ (user?.username || 'U')[0].toUpperCase() }}</div>
          <div class="info-detail">
            <h2 class="username">{{ user?.username || '加载中...' }}</h2>
            <p class="email">{{ user?.email || '' }}</p>
          </div>
          <button class="btn btn-text logout-btn" @click="handleLogout">退出登录</button>
        </div>
      </div>

      <div class="profile-tabs">
        <span v-for="tab in tabs" :key="tab.key" :class="['tab-item', { active: activeTab === tab.key }]" @click="activeTab = tab.key">{{ tab.label }}</span>
      </div>

      <div v-if="activeTab === 'favorites'" class="tab-content">
        <h3 class="section-title">我的收藏</h3>
        <div v-if="loading" class="loading-state">加载中...</div>
        <div v-else-if="favorites.length" class="news-list">
          <div v-for="item in favorites" :key="item.id" class="news-item" @click="goNews(item.newsId)">
            <div v-if="item.imageUrl" class="news-image">
              <img v-lazy="item.imageUrl" :alt="item.newsTitle || ''" />
            </div>
            <div class="news-body">
              <h4 class="news-title">{{ item.newsTitle || item.title || '新闻 ' + item.newsId }}</h4>
              <div class="news-meta">
                <span>{{ formatDate(item.createdAt) }}</span>
              </div>
            </div>
            <button class="btn btn-text remove-btn" @click.stop="removeFavorite(item.newsId)">取消收藏</button>
          </div>
        </div>
        <div v-else class="empty-state">暂无收藏</div>
      </div>

      <div v-if="activeTab === 'history'" class="tab-content">
          <h3 class="section-title">浏览历史</h3>
          <div v-if="loading" class="loading-state">加载中...</div>
          <div v-else-if="history.length" class="news-list">
            <div v-for="item in history" :key="item.id" class="news-item" @click="goNews(item.newsId)">
              <div v-if="item.imageUrl" class="news-image">
                <img v-lazy="item.imageUrl" :alt="item.title || ''" />
              </div>
              <div class="news-body">
                <h4 class="news-title">{{ item.title || '新闻 ' + item.newsId }}</h4>
                <div class="news-meta">
                  <span>浏览时长: {{ item.duration || 0 }}秒</span>
                  <span>{{ formatDate(item.createdAt) }}</span>
                </div>
              </div>
            </div>
          </div>
          <div v-else class="empty-state">暂无浏览记录</div>
        </div>

      <div v-if="activeTab === 'preference'" class="tab-content">
        <h3 class="section-title">频道偏好分析</h3>
        <div v-if="channelPreference.length" class="preference-list">
          <div v-for="item in channelPreference" :key="item.channel" class="preference-item">
            <div class="preference-header">
              <span class="channel-name">{{ item.channel }}</span>
              <span class="channel-percent">{{ item.percent }}%</span>
            </div>
            <div class="progress-bar">
              <div class="progress-fill" :style="{ width: item.percent + '%' }"></div>
            </div>
          </div>
        </div>
        <div v-else class="empty-state">暂无偏好数据</div>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, nextTick, computed } from 'vue'
import { useRouter } from 'vue-router'
import { userApi } from '../../api'

const router = useRouter()
const user = ref(null)
const favorites = ref([])
const history = ref([])
const activeTab = ref('favorites')
const loading = ref(false)
const channelPreference = ref([])

const isLoggedIn = computed(() => userApi.isLoggedIn())

const tabs = [
  { key: 'favorites', label: '我的收藏' },
  { key: 'history', label: '浏览历史' },
  { key: 'preference', label: '频道偏好' },
]

function goLogin() {
  router.push('/login')
}

function goNews(newsId) {
  router.push(`/news/${newsId}`)
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN')
}

async function loadProfile() {
  try {
    const res = await userApi.getProfile()
    if (res.data.success) {
      user.value = res.data.data
    }
  } catch (err) {
    console.error('加载用户信息失败', err)
  }
}

async function loadFavorites() {
  loading.value = true
  try {
    const res = await userApi.getFavorites()
    if (res.data.success) {
      favorites.value = res.data.data || []
    }
  } catch (err) {
    console.error('加载收藏失败', err)
  } finally {
    loading.value = false
  }
}

async function loadHistory() {
  loading.value = true
  try {
    const res = await userApi.getHistory()
    if (res.data.success) {
      history.value = res.data.data || []
      calculatePreference()
    }
  } catch (err) {
    console.error('加载历史失败', err)
  } finally {
    loading.value = false
  }
}

function calculatePreference() {
  const channelCounts = {}
  history.value.forEach(h => {
    const channel = h.channel || '其他'
    channelCounts[channel] = (channelCounts[channel] || 0) + 1
  })
  const total = history.value.length || 1
  channelPreference.value = Object.entries(channelCounts).map(([channel, count]) => ({
    channel,
    percent: Math.round((count / total) * 100)
  }))
}

async function loadChannelPreference() {
  try {
    const res = await userApi.getChannelPreference()
    if (res.data.success && res.data.data) {
      channelPreference.value = res.data.data
    } else {
      calculatePreference()
    }
  } catch (err) {
    calculatePreference()
  }
}

async function removeFavorite(newsId) {
  try {
    await userApi.removeFavorite(newsId)
    favorites.value = favorites.value.filter(f => f.newsId !== newsId)
  } catch (err) {
    console.error('取消收藏失败', err)
  }
}

function handleLogout() {
  userApi.logout()
  user.value = null
  favorites.value = []
  history.value = []
  router.push('/')
}

async function loadData() {
  await loadProfile()
  await loadFavorites()
  await loadHistory()
  await loadChannelPreference()
}

onMounted(async () => {
  if (isLoggedIn.value) {
    await loadData()
  }
})

watch(activeTab, async (val) => {
  if (val === 'favorites' && !favorites.value.length) {
    await loadFavorites()
  } else if (val === 'history' && !history.value.length) {
    await loadHistory()
  }
})
</script>

<style scoped>
.profile-page {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
}
.login-prompt {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 400px;
}
.prompt-content {
  text-align: center;
  padding: 40px;
  background: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.08);
}
.prompt-content h3 {
  font-size: 20px;
  margin-bottom: 8px;
  color: #1a2a4a;
}
.prompt-content p {
  color: var(--color-text-secondary);
  margin-bottom: 20px;
}
.profile-header {
  margin-bottom: 20px;
}
.profile-info {
  display: flex;
  align-items: center;
  gap: 16px;
}
.avatar {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: #1a2a4a;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: 700;
}
.info-detail {
  flex: 1;
}
.username {
  font-size: 20px;
  font-weight: 700;
  color: #1a2a4a;
}
.email {
  font-size: 13px;
  color: var(--color-text-light);
  margin-top: 2px;
}
.logout-btn {
  color: #c41230;
}
.profile-tabs {
  display: flex;
  gap: 0;
  border-bottom: 2px solid var(--color-border);
  margin-bottom: 20px;
}
.tab-item {
  padding: 10px 24px;
  font-size: 14px;
  font-weight: 500;
  color: var(--color-text-secondary);
  cursor: pointer;
  border-bottom: 2px solid transparent;
  margin-bottom: -2px;
  transition: all 0.2s;
}
.tab-item:hover {
  color: #1a2a4a;
}
.tab-item.active {
  color: #1a2a4a;
  border-bottom-color: #c41230;
  font-weight: 600;
}
.section-title {
  font-size: 18px;
  font-weight: 700;
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 2px solid #1a2a4a;
}
.loading-state {
  text-align: center;
  padding: 40px 20px;
  color: var(--color-text-light);
  font-size: 14px;
}
.news-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.news-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 14px;
  background: #fff;
  border-radius: 4px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.04);
  cursor: pointer;
  transition: box-shadow 0.2s;
}
.news-item:hover {
  box-shadow: 0 3px 10px rgba(0,0,0,0.08);
}
.news-item:hover .news-title {
  text-decoration: underline;
}
.news-image {
  width: 80px;
  height: 60px;
  flex-shrink: 0;
  overflow: hidden;
  border-radius: 4px;
  background: #f5f5f5;
}
.news-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.news-body {
  flex: 1;
  min-width: 0;
}
.news-title {
  font-size: 15px;
  font-weight: 600;
  line-height: 1.4;
}
.news-meta {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 6px;
  font-size: 12px;
  color: var(--color-text-light);
}
.remove-btn {
  color: #c41230;
  font-size: 13px;
}
.empty-state {
  text-align: center;
  padding: 40px 20px;
  color: var(--color-text-light);
  font-size: 14px;
}
.preference-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.preference-item {
  background: #fff;
  padding: 16px;
  border-radius: 4px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.04);
}
.preference-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}
.channel-name {
  font-weight: 600;
  color: #1a2a4a;
}
.channel-percent {
  color: #c41230;
  font-weight: 600;
}
.progress-bar {
  height: 8px;
  background: #e8e8e8;
  border-radius: 4px;
  overflow: hidden;
}
.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #1a2a4a, #3a5a8a);
  border-radius: 4px;
  transition: width 0.3s ease;
}
</style>
