<template>
  <div class="news-detail-page" v-if="news">
    <div class="reading-progress" :style="{ width: readingProgress + '%' }"></div>
    <transition name="toast-fade">
      <div v-if="toastMsg" class="toast-msg">{{ toastMsg }}</div>
    </transition>
    <button v-if="showBackTop" class="back-top-btn" @click="scrollToTop" aria-label="返回顶部"><svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 19V5"/><path d="M5 12l7-7 7 7"/></svg></button>
    <div class="detail-layout">
      <div class="detail-main">
        <nav class="breadcrumb">
          <router-link to="/">首页</router-link>
          <span class="sep">/</span>
          <router-link :to="`/channel/${channelLabelMap[news.channel] || news.channel}`">{{ channelLabelMap[news.channel] || news.channel }}</router-link>
          <span class="sep">/</span>
          <span class="current">{{ news.title }}</span>
        </nav>

        <NewsContent
          :news="news"
          :isFavorited="isFavorited"
          :translating="translating"
          :translated="translated"
          @goChannel="goChannel"
          @translate="translateNews"
          @share="handleShare"
          @favorite="handleFavorite"
        />

        <CommentSection
          :comments="comments"
          :newsId="news.id"
          :redirectPath="$route.fullPath"
          @submit="loadComments"
          @toast="showToast"
        />
      </div>

      <RelatedNews
        :relatedNews="relatedNews"
        :hotNews="hotNews"
        @goNews="goNews"
      />
    </div>
  </div>
  <div v-else-if="loading" class="loading-skeleton">
    <div class="skeleton-breadcrumb"></div>
    <div class="skeleton-article">
      <div class="skeleton-title"></div>
      <div class="skeleton-meta">
        <span></span><span></span><span></span><span></span>
      </div>
      <div class="skeleton-cover"></div>
      <div class="skeleton-content">
        <div class="skeleton-line"></div><div class="skeleton-line"></div><div class="skeleton-line"></div><div class="skeleton-line"></div><div class="skeleton-line"></div><div class="skeleton-line"></div>
      </div>
    </div>
  </div>
  <div v-else class="not-found">
    <div v-if="errorMsg" class="error-state">
      <div class="error-icon"><svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M12 2L2 20h20L12 2z"/><path d="M12 9v4"/><circle cx="12" cy="16.5" r="0.5" fill="currentColor"/></svg></div>
      <p>{{ errorMsg }}</p>
      <button class="btn btn-primary" @click="loadNews">重新加载</button>
    </div>
    <div v-else>
      <div class="empty-icon"><svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M4 4h12a2 2 0 012 2v12a2 2 0 01-2 2H4a2 2 0 01-2-2V6a2 2 0 012-2z"/><path d="M2 8h16"/><path d="M8 8v12"/></svg></div>
      <p>新闻不存在</p>
      <router-link to="/">返回首页</router-link>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { newsApi, behaviorApi, userApi, commentApi } from '../../api'
import { formatRelativeTime } from '../../utils'
import { cleanText, cleanHtmlContent, CHANNEL_LABEL_MAP } from '../../utils'
import { useAuthStore } from '../../stores/auth'
import NewsContent from '../../components/portal/NewsContent.vue'
import CommentSection from '../../components/portal/CommentSection.vue'
import RelatedNews from '../../components/portal/RelatedNews.vue'

const channelLabelMap = CHANNEL_LABEL_MAP

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const news = ref(null)
const isFavorited = ref(false)
const comments = ref([])
const isLoggedIn = computed(() => authStore.isLoggedIn)
const loading = ref(true)
const translating = ref(false)
const translated = ref(false)
const toastMsg = ref('')
const errorMsg = ref('')
const showBackTop = ref(false)
const readingProgress = ref(0)
let viewStartTime = Date.now()
let toastTimer = null
let newsAbortController = null

function showToast(msg) {
  toastMsg.value = msg
  clearTimeout(toastTimer)
  toastTimer = setTimeout(() => { toastMsg.value = '' }, 2000)
}

function scrollToTop() {
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

function onScroll() {
  showBackTop.value = window.scrollY > 400
  const docHeight = document.documentElement.scrollHeight - window.innerHeight
  readingProgress.value = docHeight > 0 ? Math.min((window.scrollY / docHeight) * 100, 100) : 0
}

const relatedNews = ref([])
const hotNews = ref([])

async function translateNews() {
  if (!news.value || translating.value || translated.value) return
  translating.value = true
  try {
    const res = await newsApi.translate(news.value.id)
    if (res.data.success && res.data.data) {
      const parts = res.data.data.split('|||')
      if (parts.length >= 1 && parts[0]) news.value.title = parts[0]
      if (parts.length >= 2 && parts[1]) news.value.summary = parts[1]
      translated.value = true
    }
  } catch (err) {
    console.error('Translation failed:', err)
    showToast('翻译失败，请稍后重试')
  } finally {
    translating.value = false
  }
}

async function loadRelatedNews() {
  try {
    const res = await newsApi.getList({ channel: news.value?.channel }, true)
    const body = res.data
    const pageData = body?.data?.data || body?.data || body
    const allNews = Array.isArray(pageData) ? pageData : (pageData?.data || pageData?.records || [])
    const mapped = allNews.map(n => ({
      ...n,
      id: n.id || n.articleId,
      channel: n.channel || '',
      channelName: n.channelName || channelLabelMap[n.channel] || '',
      imageUrl: n.imageUrl || n.coverImage || '',
      viewCount: n.viewCount || 0
    }))
    relatedNews.value = news.value
      ? mapped.filter(n => n.id !== news.value.id && n.channel === news.value.channel).slice(0, 5)
      : mapped.slice(0, 5)
    hotNews.value = [...mapped].sort((a, b) => (b.viewCount || 0) - (a.viewCount || 0)).slice(0, 10)
  } catch (err) {
    console.error('Failed to load related news:', err)
  }
}

function goNews(id) {
  router.push(`/news/${id}`)
}

function goChannel(name) {
  const displayName = channelLabelMap[name] || name
  router.push(`/channel/${displayName}`)
}

function handleShare() {
  behaviorApi.report({ eventType: 'share', targetId: String(news.value.id), targetType: 'news' })
  if (navigator.clipboard) {
    navigator.clipboard.writeText(window.location.href).then(() => showToast('链接已复制'))
  }
}

async function checkFavorite() {
  if (!isLoggedIn.value || !news.value) return
  try {
    const res = await userApi.getFavorites()
    if (res.data.success && res.data.data) {
      isFavorited.value = res.data.data.some(f => f.newsId === news.value.id)
    }
  } catch (err) {
  }
}

function handleFavorite() {
  if (!isLoggedIn.value) {
    router.push(`/login?redirect=${encodeURIComponent(route.fullPath)}`)
    return
  }
  isFavorited.value = !isFavorited.value
  if (isFavorited.value) {
    userApi.addFavorite(news.value.id).catch(() => { isFavorited.value = false })
  } else {
    userApi.removeFavorite(news.value.id).catch(() => { isFavorited.value = true })
  }
}

async function loadComments() {
  try {
    const res = await commentApi.getList(news.value.id)
    const list = res.data?.data || res.data || []
    if (Array.isArray(list)) {
      comments.value = list.map(c => ({
        id: c.id,
        username: c.username || '用户' + c.userId,
        content: c.content,
        time: new Date(c.createdAt).getTime(),
      }))
    }
  } catch (err) {
    console.error('Failed to load comments:', err)
  }
}

async function loadNews() {
  loading.value = true
  errorMsg.value = ''
  if (newsAbortController) newsAbortController.abort()
  newsAbortController = new AbortController()
  const id = route.params.id
  try {
    const newsRes = await newsApi.getById(id, { signal: newsAbortController.signal })
    const body = newsRes.data
    // 兼容 {success:true, data:{...}} 和 {code:200, data:{...}} 两种格式
    const raw = body?.data?.data || body?.data || body
    if (raw && (raw.articleId || raw.id)) {
      raw.id = raw.id || raw.articleId
      raw.title = cleanText(raw.title)
      raw.summary = cleanText(raw.summary)
      raw.content = cleanHtmlContent(raw.content)
      raw.channel = raw.channel || ''
      raw.channelName = raw.channelName || channelLabelMap[raw.channel] || ''
      raw.imageUrl = raw.imageUrl || raw.coverImage || ''
      raw.createdAt = raw.createdAt || raw.publishTime || raw.createTime
      news.value = raw
      viewStartTime = Date.now()
      loadComments()
      loadRelatedNews()
      checkFavorite()
      recordHistory()
    } else {
      news.value = null
    }
  } catch (err) {
    console.error('Failed to load news:', err)
    errorMsg.value = '加载失败，请检查网络连接'
    news.value = null
  }
  loading.value = false
}

function recordHistory() {
  if (news.value && isLoggedIn.value) {
    const duration = Math.round((Date.now() - viewStartTime) / 1000)
    userApi.addHistory(news.value.id, duration).catch(() => {})
  }
}

onMounted(() => {
  loadNews()
  behaviorApi.report({ eventType: 'page_view', targetId: String(route.params.id), targetType: 'news' })
  window.addEventListener('scroll', onScroll, { passive: true })
})

onUnmounted(() => {
  recordHistory()
  behaviorApi.flush()
  window.removeEventListener('scroll', onScroll)
  clearTimeout(toastTimer)
  if (newsAbortController) newsAbortController.abort()
})

watch(() => route.params.id, () => {
  if (route.params.id) {
    recordHistory()
    loadNews()
    behaviorApi.report({ eventType: 'page_view', targetId: String(route.params.id), targetType: 'news' })
  }
})
</script>

<style scoped>
.reading-progress {
  position: fixed;
  top: 0;
  left: 0;
  height: 3px;
  background: var(--color-gradient-reading);
  z-index: 9999;
  transition: width 0.1s linear;
}
.toast-msg {
  position: fixed;
  top: 70px;
  left: 50%;
  transform: translateX(-50%);
  background: var(--color-overlay-light);
  color: var(--color-text-white);
  padding: 10px 28px;
  border-radius: 6px;
  font-size: 14px;
  z-index: 9999;
  pointer-events: none;
}
.toast-fade-enter-active, .toast-fade-leave-active {
  transition: opacity 0.3s;
}
.toast-fade-enter-from, .toast-fade-leave-to {
  opacity: 0;
}
.back-top-btn {
  position: fixed;
  bottom: 90px;
  right: 20px;
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: var(--color-primary);
  color: var(--color-text-white);
  border: none;
  cursor: pointer;
  z-index: 999;
  box-shadow: 0 2px 8px var(--color-shadow-lg);
  transition: background 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}
.back-top-btn:hover {
  background: var(--color-primary);
  filter: brightness(1.1);
}
.detail-layout {
  display: flex;
  gap: 24px;
  align-items: flex-start;
}
.detail-main {
  flex: 3;
  min-width: 0;
}
.breadcrumb {
  font-size: 13px;
  color: var(--color-text-light);
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 6px;
  flex-wrap: wrap;
}
.breadcrumb a {
  color: var(--color-text-secondary);
  text-decoration: none;
}
.breadcrumb a:hover {
  color: var(--color-primary);
  text-decoration: underline;
}
.sep {
  color: var(--color-text-light);
}
.current {
  color: var(--color-text);
  max-width: 300px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.loading-skeleton {
  padding: 0;
}
.skeleton-breadcrumb {
  height: 16px;
  width: 200px;
  background: var(--color-skeleton);
  border-radius: 4px;
  margin-bottom: 20px;
  animation: skeleton-shine 1.5s ease-in-out infinite;
}
.skeleton-article {
  background: var(--color-bg-white);
  padding: 32px;
  border-radius: 4px;
  box-shadow: 0 1px 4px var(--color-shadow-sm);
}
.skeleton-title {
  height: 36px;
  width: 80%;
  background: var(--color-skeleton);
  border-radius: 4px;
  margin-bottom: 16px;
  animation: skeleton-shine 1.5s ease-in-out infinite;
}
.skeleton-meta {
  display: flex;
  gap: 12px;
  padding-bottom: 20px;
  border-bottom: 1px solid var(--color-border);
  margin-bottom: 24px;
}
.skeleton-meta span {
  height: 14px;
  width: 60px;
  background: var(--color-skeleton);
  border-radius: 4px;
  animation: skeleton-shine 1.5s ease-in-out infinite;
}
.skeleton-cover {
  width: 100%;
  height: 300px;
  background: var(--color-skeleton);
  border-radius: 4px;
  margin-bottom: 24px;
  animation: skeleton-shine 1.5s ease-in-out infinite;
}
.skeleton-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.skeleton-line {
  height: 16px;
  background: var(--color-skeleton);
  border-radius: 4px;
  animation: skeleton-shine 1.5s ease-in-out infinite;
}
.skeleton-line:nth-child(1) { width: 100%; }
.skeleton-line:nth-child(2) { width: 95%; }
.skeleton-line:nth-child(3) { width: 90%; }
.skeleton-line:nth-child(4) { width: 85%; }
.skeleton-line:nth-child(5) { width: 92%; }
.skeleton-line:nth-child(6) { width: 88%; }
@keyframes skeleton-shine {
  0% { background: var(--color-skeleton); }
  50% { background: var(--color-skeleton-shine); }
  100% { background: var(--color-skeleton); }
}
.not-found {
  text-align: center;
  padding: 80px 20px;
  font-size: 16px;
  color: var(--color-text-secondary);
}
.not-found .empty-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-text-light);
  margin-bottom: 16px;
}
.not-found .empty-icon svg {
  width: 64px;
  height: 64px;
}
.not-found a {
  display: inline-block;
  margin-top: 12px;
  color: var(--color-primary);
  font-weight: 600;
}
.error-state .error-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-text-light);
  margin-bottom: 16px;
}
.error-state .error-icon svg {
  width: 64px;
  height: 64px;
}
.error-state button {
  margin-top: 12px;
}
@media (max-width: 768px) {
  .news-detail-page {
    overflow-x: hidden;
  }
  .detail-layout {
    flex-direction: column;
    gap: 0;
    padding: 0;
    max-width: 100vw;
    overflow-x: hidden;
  }
  .detail-main {
    max-width: 100vw;
    overflow-x: hidden;
  }
  .breadcrumb {
    font-size: 12px;
    margin-bottom: 12px;
    gap: 6px;
    padding: 0 8px;
  }
  .current {
    max-width: 120px;
    font-size: 12px;
  }
  .not-found {
    padding: 40px 20px;
    font-size: 14px;
    text-align: center;
  }
  .loading-skeleton {
    padding: 0 8px;
  }
  .skeleton-article {
    padding: 12px 0;
    background: transparent;
    box-shadow: none;
  }
  .skeleton-title {
    padding: 0 12px;
    height: 30px;
    width: 90%;
  }
  .skeleton-meta {
    padding: 0 12px 12px;
  }
  .skeleton-cover {
    width: 100vw;
    margin-left: calc(-50vw + 50%);
    max-height: 220px;
  }
  .skeleton-content {
    padding: 0 12px;
  }
}
</style>
