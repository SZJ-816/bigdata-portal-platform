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
        <div v-if="loading" class="loading-skeleton">
          <div v-for="i in 3" :key="i" class="skeleton-item">
            <div class="skeleton-thumb"></div>
            <div class="skeleton-body">
              <div class="skeleton-line"></div>
              <div class="skeleton-line-sm"></div>
            </div>
          </div>
        </div>
        <div v-else-if="favoritesError" class="error-state">
          <div class="error-icon">⚠️</div>
          <p>{{ favoritesError }}</p>
          <button class="btn btn-primary" @click="loadFavorites">重新加载</button>
        </div>
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
            <button class="btn btn-text remove-btn" @click.stop="removeFavorite(item.newsId)" :disabled="removingId === item.newsId">
              <span v-if="removingId === item.newsId">移除中...</span>
              <span v-else>取消收藏</span>
            </button>
          </div>
        </div>
        <div v-else class="empty-state">
          <div class="empty-icon">❤️</div>
          <p>暂无收藏内容</p>
          <p class="empty-hint">浏览新闻时点击收藏按钮添加</p>
        </div>
      </div>

      <div v-if="activeTab === 'history'" class="tab-content">
          <h3 class="section-title">浏览历史</h3>
          <div v-if="loading" class="loading-skeleton">
            <div v-for="i in 3" :key="i" class="skeleton-item">
              <div class="skeleton-thumb"></div>
              <div class="skeleton-body">
                <div class="skeleton-line"></div>
                <div class="skeleton-line-sm"></div>
              </div>
            </div>
          </div>
          <div v-else-if="historyError" class="error-state">
            <div class="error-icon">⚠️</div>
            <p>{{ historyError }}</p>
            <button class="btn btn-primary" @click="loadHistory">重新加载</button>
          </div>
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
          <div v-else class="empty-state">
            <div class="empty-icon">📖</div>
            <p>暂无浏览记录</p>
            <p class="empty-hint">开始浏览新闻吧</p>
          </div>
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
        <div v-else class="empty-state">
          <div class="empty-icon">📊</div>
          <p>暂无偏好数据</p>
          <p class="empty-hint">浏览更多新闻后将自动分析</p>
        </div>
      </div>
    </template>

    <div v-if="toast.show" :class="['toast', toast.type]">{{ toast.message }}</div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue'
import { useRouter } from 'vue-router'
import { userApi } from '../../api'

const router = useRouter()
const user = ref(null)
const favorites = ref([])
const history = ref([])
const activeTab = ref('favorites')
const loading = ref(false)
const channelPreference = ref([])
const favoritesError = ref('')
const historyError = ref('')
const removingId = ref(null)

const toast = ref({ show: false, message: '', type: 'success' })
const showToast = (msg, type = 'success') => {
  toast.value = { show: true, message: msg, type }
  setTimeout(() => { toast.value.show = false }, 3000)
}

const isLoggedIn = computed(() => userApi.isLoggedIn())

const tabs = [
  { key: 'favorites', label: '我的收藏' },
  { key: 'history', label: '浏览历史' },
  { key: 'preference', label: '频道偏好' },
]

function goLogin() {
  router.push({ path: '/login', query: { redirect: router.currentRoute.value.fullPath } })
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
    showToast('加载用户信息失败', 'error')
  }
}

async function loadFavorites() {
  loading.value = true
  favoritesError.value = ''
  try {
    const res = await userApi.getFavorites()
    if (res.data.success) {
      favorites.value = res.data.data || []
    }
  } catch (err) {
    console.error('加载收藏失败', err)
    favoritesError.value = '加载收藏失败，请检查网络连接'
    showToast('加载收藏失败', 'error')
  } finally {
    loading.value = false
  }
}

async function loadHistory() {
  loading.value = true
  historyError.value = ''
  try {
    const res = await userApi.getHistory()
    if (res.data.success) {
      history.value = res.data.data || []
      calculatePreference()
    }
  } catch (err) {
    console.error('加载历史失败', err)
    historyError.value = '加载历史失败，请检查网络连接'
    showToast('加载历史失败', 'error')
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
  if (removingId.value) return
  if (!confirm('确定要取消收藏吗？')) return
  removingId.value = newsId
  try {
    await userApi.removeFavorite(newsId)
    favorites.value = favorites.value.filter(f => f.newsId !== newsId)
    showToast('已取消收藏')
  } catch (err) {
    console.error('取消收藏失败', err)
    showToast('取消收藏失败，请重试', 'error')
  } finally {
    removingId.value = null
  }
}

function handleLogout() {
  if (!confirm('确定要退出登录吗？')) return
  userApi.logout()
  window.dispatchEvent(new CustomEvent('auth-updated'))
  user.value = null
  favorites.value = []
  history.value = []
  showToast('已退出登录')
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
  if (val === 'favorites' && !favorites.value.length && !favoritesError.value) {
    await loadFavorites()
  } else if (val === 'history' && !history.value.length && !historyError.value) {
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
  background: var(--color-bg-white);
  border-radius: 4px;
  box-shadow: 0 2px 12px var(--color-card-shadow);
}
.prompt-content h3 {
  font-size: 20px;
  margin-bottom: 8px;
  color: var(--color-text);
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
  background: var(--color-primary);
  color: var(--color-text-white);
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
  color: var(--color-text);
}
.email {
  font-size: 13px;
  color: var(--color-text-light);
  margin-top: 2px;
}
.logout-btn {
  color: var(--color-accent);
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
  color: var(--color-text);
}
.tab-item.active {
  color: var(--color-text);
  border-bottom-color: var(--color-accent);
  font-weight: 600;
}
.section-title {
  font-size: 18px;
  font-weight: 700;
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 2px solid var(--color-primary);
}
.loading-skeleton {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.skeleton-item {
  display: flex;
  gap: 14px;
  padding: 14px;
  background: var(--color-bg-white);
  border-radius: 8px;
  box-shadow: 0 1px 3px var(--color-shadow-sm);
}
.skeleton-thumb {
  width: 80px;
  height: 60px;
  border-radius: 4px;
  background: var(--color-skeleton);
  animation: skeleton-shine 1.5s ease-in-out infinite;
  flex-shrink: 0;
}
.skeleton-body {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
  justify-content: center;
}
.skeleton-line {
  height: 18px;
  width: 70%;
  border-radius: 2px;
  background: var(--color-skeleton);
  animation: skeleton-shine 1.5s ease-in-out infinite;
}
.skeleton-line-sm {
  height: 12px;
  width: 40%;
  border-radius: 2px;
  background: var(--color-skeleton);
  animation: skeleton-shine 1.5s ease-in-out infinite;
}
@keyframes skeleton-shine {
  0% { background: var(--color-skeleton); }
  50% { background: var(--color-skeleton-shine); }
  100% { background: var(--color-skeleton); }
}
.error-state {
  text-align: center;
  padding: 60px 20px;
  color: var(--color-text-secondary);
}
.error-icon {
  font-size: 48px;
  margin-bottom: 12px;
}
.error-state p {
  font-size: 15px;
  margin-bottom: 16px;
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
  background: var(--color-bg-white);
  border-radius: 4px;
  box-shadow: 0 1px 3px var(--color-shadow-sm);
  cursor: pointer;
  transition: box-shadow 0.2s;
}
.news-item:hover {
  box-shadow: 0 3px 10px var(--color-shadow-md);
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
  background: var(--color-bg);
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
  color: var(--color-accent);
  font-size: 13px;
  min-height: 36px;
  padding: 6px 12px;
  transition: all 0.2s;
}
.remove-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: var(--color-text-light);
  font-size: 14px;
}
.empty-icon {
  font-size: 48px;
  margin-bottom: 12px;
}
.empty-hint {
  font-size: 13px !important;
  color: var(--color-text-light) !important;
  margin-top: 4px;
}
.preference-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.preference-item {
  background: var(--color-bg-white);
  padding: 16px;
  border-radius: 4px;
  box-shadow: 0 1px 3px var(--color-shadow-sm);
}
.preference-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}
.channel-name {
  font-weight: 600;
  color: var(--color-text);
}
.channel-percent {
  color: var(--color-accent);
  font-weight: 600;
}
.progress-bar {
  height: 8px;
  background: var(--color-bg-secondary);
  border-radius: 4px;
  overflow: hidden;
}
.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, var(--color-primary), var(--color-primary));
  border-radius: 4px;
  transition: width 0.3s ease;
}
.toast {
  position: fixed;
  top: 80px;
  left: 50%;
  transform: translateX(-50%);
  padding: 10px 24px;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  z-index: 9999;
  animation: toastIn 0.3s ease;
}
.toast.success {
  background: var(--color-success, #10B981);
  color: #fff;
}
.toast.error {
  background: var(--color-accent, #EF4444);
  color: #fff;
}
@keyframes toastIn {
  from { opacity: 0; transform: translateX(-50%) translateY(-10px); }
  to { opacity: 1; transform: translateX(-50%) translateY(0); }
}
@media (max-width: 768px) {
  .profile-page {
    padding: 0;
  }
  .profile-header {
    margin-bottom: 14px;
    border-radius: 0;
  }
  .profile-info {
    padding: 16px 14px;
    gap: 12px;
  }
  .avatar {
    width: 48px;
    height: 48px;
    font-size: 20px;
  }
  .username {
    font-size: 18px;
  }
  .email {
    font-size: 12px;
  }
  .logout-btn {
    font-size: 13px;
    padding: 8px 14px;
  }
  .profile-tabs {
    padding: 0 8px;
    gap: 0;
    border-bottom-width: 2px;
    overflow-x: auto;
    scrollbar-width: none;
  }
  .profile-tabs::-webkit-scrollbar {
    display: none;
  }
  .tab-item {
    padding: 10px 18px;
    font-size: 13px;
    white-space: nowrap;
    flex-shrink: 0;
  }
  .section-title {
    font-size: 16px;
    margin-bottom: 12px;
    padding: 0 8px 8px;
  }
  .tab-content {
    padding: 0 8px;
  }
  .loading-skeleton {
    gap: 8px;
  }
  .skeleton-item {
    padding: 12px;
    border-radius: 10px;
    gap: 10px;
  }
  .skeleton-thumb {
    width: 70px;
    height: 50px;
    border-radius: 6px;
  }
  .error-state {
    padding: 40px 20px;
    font-size: 14px;
  }
  .error-icon {
    font-size: 40px;
  }
  .news-list {
    gap: 8px;
  }
  .news-item {
    padding: 12px;
    gap: 10px;
    border-radius: 10px;
  }
  .news-item:active {
    transform: scale(0.99);
  }
  .news-image {
    width: 70px;
    height: 50px;
    border-radius: 6px;
  }
  .news-title {
    font-size: 14px;
    line-height: 1.35;
  }
  .news-meta {
    font-size: 11px;
    gap: 8px;
  }
  .remove-btn {
    font-size: 12px;
    padding: 6px 10px;
    min-height: 36px;
  }
  .empty-state {
    padding: 40px 20px;
    font-size: 13px;
  }
  .empty-icon {
    font-size: 40px;
  }
  .preference-list {
    gap: 12px;
  }
  .preference-item {
    padding: 14px;
    border-radius: 8px;
  }
  .login-prompt {
    min-height: 300px;
    padding: 0 12px;
  }
  .prompt-content {
    padding: 32px 20px;
    border-radius: 10px;
  }
  .prompt-content h3 {
    font-size: 18px;
  }
  .prompt-content p {
    font-size: 13px;
  }
  .prompt-content .btn {
    width: 100%;
    min-height: 44px;
    border-radius: 8px;
  }
  .toast {
    top: 70px;
    padding: 10px 20px;
    font-size: 13px;
  }
}
@media (max-width: 480px) {
  .avatar {
    width: 40px;
    height: 40px;
    font-size: 18px;
  }
  .username {
    font-size: 16px;
  }
  .tab-item {
    padding: 8px 14px;
    font-size: 12.5px;
  }
  .news-item {
    padding: 10px;
    gap: 8px;
  }
  .news-image {
    width: 60px;
    height: 44px;
  }
  .news-title {
    font-size: 13px;
  }
  .skeleton-thumb {
    width: 60px;
    height: 44px;
  }
}
</style>
