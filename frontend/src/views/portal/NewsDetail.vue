<template>
  <div class="news-detail-page" v-if="news">
    <div class="detail-layout">
      <div class="detail-main">
        <nav class="breadcrumb">
          <router-link to="/">首页</router-link>
          <span class="sep">/</span>
          <router-link :to="`/channel/${channelLabelMap[news.channel] || news.channel}`">{{ channelLabelMap[news.channel] || news.channel }}</router-link>
          <span class="sep">/</span>
          <span class="current">{{ news.title }}</span>
        </nav>

        <article class="article">
          <h1 class="article-title">{{ news.title }}</h1>
          <div class="article-meta">
            <span v-if="news.isBreaking" class="breaking-tag">BREAKING</span>
            <span class="channel-tag" @click="goChannel(news.channel)">{{ channelLabelMap[news.channel] || news.channel }}</span>
            <span>{{ news.source }}</span>
            <span>{{ formatRelativeTime(news.publishTime) }}</span>
            <span>{{ formatViewCount(news.viewCount) }} 阅读</span>
            <span>{{ news.commentCount }} 评论</span>
            <button v-if="isEnglish" class="translate-btn" @click="translateNews" :disabled="translating">
              <span v-if="translating" class="ai-spin-sm"></span>
              {{ translated ? '已翻译' : '✦ 翻译' }}
            </button>
          </div>
          <img v-if="news.imageUrl" :src="resolveImageUrl(news.imageUrl)" :alt="news.title" :data-channel="news.channel" class="article-cover" @error="onCoverImgError" />
          <div class="article-content" v-html="news.content"></div>
        </article>

        <div class="article-footer">
          <div class="footer-tags">
            <span class="channel-tag" @click="goChannel(news.channel)">{{ channelLabelMap[news.channel] || news.channel }}</span>
          </div>
          <div class="footer-actions">
            <button class="action-btn" @click="handleShare">
              <span class="action-icon">↗</span> 分享
            </button>
            <button :class="['action-btn', { active: isFavorited }]" @click="handleFavorite">
              <span class="action-icon">{{ isFavorited ? '★' : '☆' }}</span> {{ isFavorited ? '已收藏' : '收藏' }}
            </button>
          </div>
        </div>

        <div class="comment-section">
          <h3 class="section-title">评论 ({{ comments.length }})</h3>
          <div v-if="isLoggedIn" class="comment-form">
            <textarea v-model="commentText" placeholder="发表你的看法..." rows="3"></textarea>
            <button class="btn btn-primary" @click="submitComment">发表评论</button>
          </div>
          <div v-else class="login-prompt">
            <router-link to="/login">登录</router-link> 后即可发表评论
          </div>
          <div class="comment-list">
            <div v-for="c in comments" :key="c.id" class="comment-item">
              <div class="comment-avatar">{{ c.username[0] }}</div>
              <div class="comment-body">
                <div class="comment-header">
                  <span class="comment-user">{{ c.username }}</span>
                  <span class="comment-time">{{ formatRelativeTime(c.time) }}</span>
                </div>
                <p class="comment-text">{{ c.content }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="detail-sidebar">
        <div class="card related-section">
          <h3 class="sidebar-title">相关新闻</h3>
          <div class="related-list">
            <div v-for="item in relatedNews" :key="item.id" class="related-item" @click="goNews(item.id)">
              <span class="related-title">{{ item.title }}</span>
              <span class="related-meta">{{ formatRelativeTime(item.publishTime) }}</span>
            </div>
          </div>
        </div>
        <div class="card hot-section">
          <h3 class="sidebar-title">热点新闻</h3>
          <div class="hot-list">
            <div v-for="(item, idx) in hotNews" :key="item.id" class="hot-item" @click="goNews(item.id)">
              <span :class="['rank-num', { 'rank-hot': idx < 3 }]">{{ idx + 1 }}</span>
              <span class="hot-title">{{ item.title }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
  <div v-else class="not-found">
    <p>新闻不存在</p>
    <router-link to="/">返回首页</router-link>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { newsApi, behaviorApi, userApi } from '../../api'
import { channels, formatRelativeTime } from '../../mock/newsData'
import { cleanText, cleanHtmlContent, formatViewCount, getChannelIcon, CHANNEL_LABEL_MAP } from '../../utils'

const channelLabelMap = CHANNEL_LABEL_MAP

const route = useRoute()
const router = useRouter()

const news = ref(null)
const isFavorited = ref(false)
const commentText = ref('')
const comments = ref([])
const isLoggedIn = computed(() => !!localStorage.getItem('token'))
const loading = ref(true)
const translating = ref(false)
const translated = ref(false)
let viewStartTime = Date.now()

const relatedNews = ref([])
const hotNews = ref([])

const isEnglish = computed(() => {
  if (!news.value || !news.value.title) return false
  return !/[\u4e00-\u9fa5]/.test(news.value.title)
})

async function translateNews() {
  if (!news.value || translating.value || translated.value) return
  translating.value = true
  try {
    const res = await newsApi.getById(news.value.id)
    if (res.data.success && res.data.data) {
      translated.value = true
    }
  } catch (err) {
    console.error('Translation failed:', err)
  }
  translating.value = false
}

async function loadRelatedNews() {
  try {
    const res = await newsApi.getList({ channel: news.value?.channel }, true)
    if (res.data.data) {
      const allNews = res.data.data
      relatedNews.value = news.value
        ? allNews.filter(n => n.id !== news.value.id && n.channel === news.value.channel).slice(0, 5)
        : allNews.slice(0, 5)
      hotNews.value = [...allNews].sort((a, b) => (b.viewCount || 0) - (a.viewCount || 0)).slice(0, 10)
    }
  } catch (err) {
    console.error('Failed to load related news:', err)
  }
}

function goNews(id) {
  router.push(`/news/${id}`)
}

function resolveImageUrl(url) {
  if (!url) return ''
  if (url.startsWith('/') || url.startsWith('data:')) return url
  return '/api/image/proxy?url=' + encodeURIComponent(url)
}

function onCoverImgError(e) {
  const el = e.target
  const channel = el.dataset?.channel || ''
  const fallback = getChannelIcon(channel)
  if (!el.src.endsWith(fallback)) {
    el.src = fallback
  }
}

function goChannel(name) {
  const displayName = channelLabelMap[name] || name
  router.push(`/channel/${displayName}`)
}

function handleShare() {
  behaviorApi.report({ eventType: 'share', targetId: String(news.value.id), targetType: 'news' })
  if (navigator.clipboard) {
    navigator.clipboard.writeText(window.location.href)
  }
}

function handleFavorite() {
  isFavorited.value = !isFavorited.value
  if (isFavorited.value) {
    userApi.addFavorite(news.value.id).catch(() => {})
  } else {
    userApi.removeFavorite(news.value.id).catch(() => {})
  }
}

function submitComment() {
  if (!commentText.value.trim()) return
  behaviorApi.report({ eventType: 'comment', targetId: String(news.value.id), targetType: 'news' })
  comments.value.unshift({
    id: Date.now(),
    username: '当前用户',
    content: commentText.value,
    time: Date.now(),
  })
  commentText.value = ''
}

async function loadNews() {
  loading.value = true
  const id = route.params.id
  try {
    const newsRes = await newsApi.getById(id)
    if (newsRes.data.success && newsRes.data.data) {
      const raw = newsRes.data.data
      raw.title = cleanText(raw.title)
      raw.summary = cleanText(raw.summary)
      raw.content = cleanHtmlContent(raw.content)
      news.value = raw
      viewStartTime = Date.now()
      comments.value = [
        { id: 1, username: '科技观察者', content: '这个技术突破确实令人振奋，期待看到更多实际应用场景。', time: Date.now() - 30 * 60 * 1000 },
        { id: 2, username: '数据工程师', content: '从技术角度来看，这个方案还有很多需要验证的地方，不能盲目乐观。', time: Date.now() - 60 * 60 * 1000 },
        { id: 3, username: '行业分析师', content: '市场反应很积极，但长期影响还需要观察。', time: Date.now() - 120 * 60 * 1000 },
      ]
      loadRelatedNews()
      recordHistory()
    } else {
      news.value = null
    }
  } catch (err) {
    console.error('Failed to load news:', err)
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
})

onUnmounted(() => {
  recordHistory()
  behaviorApi.flush()
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
.detail-layout {
  display: flex;
  gap: 24px;
  align-items: flex-start;
}
.detail-main {
  flex: 3;
  min-width: 0;
}
.detail-sidebar {
  flex: 1;
  min-width: 260px;
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
  color: #1a2a4a;
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
.article {
  background: #fff;
  padding: 32px;
  border-radius: 4px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.04);
}
.article-title {
  font-size: 28px;
  font-weight: 800;
  line-height: 1.35;
  color: #1a2a4a;
  font-family: Georgia, 'Noto Serif SC', serif;
  margin-bottom: 16px;
}
.article-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 13px;
  color: var(--color-text-light);
  padding-bottom: 20px;
  border-bottom: 1px solid var(--color-border);
  margin-bottom: 24px;
  flex-wrap: wrap;
}
.breaking-tag {
  display: inline-block;
  background: #c41230;
  color: #fff;
  padding: 2px 10px;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 1px;
  border-radius: 2px;
}
.channel-tag {
  background: #1a2a4a;
  color: #fff;
  padding: 2px 10px;
  border-radius: 2px;
  font-size: 12px;
  cursor: pointer;
}
.channel-tag:hover {
  background: #243656;
}
.translate-btn {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 2px 10px;
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  color: #fff;
  border: none;
  border-radius: 3px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.2s;
}
.translate-btn:hover:not(:disabled) {
  background: linear-gradient(135deg, #4f46e5, #7c3aed);
}
.translate-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
.ai-spin-sm {
  display: inline-block;
  width: 12px;
  height: 12px;
  border: 2px solid rgba(255,255,255,0.3);
  border-top-color: #fff;
  border-radius: 50%;
  animation: spin-sm 0.8s linear infinite;
}
@keyframes spin-sm {
  to { transform: rotate(360deg); }
}
.article-cover {
  width: 100%;
  max-height: 400px;
  object-fit: cover;
  object-position: center;
  border-radius: 4px;
  margin-bottom: 24px;
  background-color: #f0f0f0;
}
.article-content {
  font-size: 16px;
  line-height: 1.85;
  color: #333;
}
.article-content :deep(p) {
  margin-bottom: 18px;
  text-indent: 2em;
}
.article-content :deep(img) {
  max-width: 100%;
  height: auto;
  max-height: 500px;
  object-fit: contain;
  border-radius: 4px;
  display: block;
  margin: 12px auto;
}
.article-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 0;
  margin-top: 24px;
  border-top: 1px solid var(--color-border);
}
.footer-tags {
  display: flex;
  gap: 8px;
}
.footer-actions {
  display: flex;
  gap: 12px;
}
.action-btn {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 6px 16px;
  border: 1px solid var(--color-border);
  border-radius: 4px;
  background: #fff;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
  color: var(--color-text-secondary);
}
.action-btn:hover {
  border-color: #1a2a4a;
  color: #1a2a4a;
}
.action-btn.active {
  border-color: #c41230;
  color: #c41230;
}
.action-icon {
  font-size: 14px;
}
.section-title {
  font-size: 18px;
  font-weight: 700;
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 2px solid #1a2a4a;
}
.comment-section {
  margin-top: 24px;
  background: #fff;
  padding: 24px;
  border-radius: 4px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.04);
}
.comment-form {
  margin-bottom: 20px;
}
.comment-form textarea {
  width: 100%;
  margin-bottom: 10px;
  resize: vertical;
}
.login-prompt {
  padding: 16px;
  text-align: center;
  background: #f5f5f5;
  border-radius: 4px;
  margin-bottom: 20px;
  font-size: 14px;
  color: var(--color-text-secondary);
}
.login-prompt a {
  color: #1a2a4a;
  font-weight: 600;
}
.comment-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.comment-item {
  display: flex;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}
.comment-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: #1a2a4a;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 600;
  flex-shrink: 0;
}
.comment-body {
  flex: 1;
  min-width: 0;
}
.comment-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 4px;
}
.comment-user {
  font-size: 14px;
  font-weight: 600;
  color: #1a2a4a;
}
.comment-time {
  font-size: 12px;
  color: var(--color-text-light);
}
.comment-text {
  font-size: 14px;
  line-height: 1.6;
  color: var(--color-text-secondary);
}
.sidebar-title {
  font-size: 16px;
  font-weight: 700;
  margin-bottom: 14px;
  padding-bottom: 8px;
  border-bottom: 2px solid #1a2a4a;
}
.related-section {
  margin-bottom: 16px;
}
.related-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.related-item {
  cursor: pointer;
  padding: 4px 0;
}
.related-item:hover .related-title {
  text-decoration: underline;
}
.related-title {
  font-size: 13px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.related-meta {
  font-size: 11px;
  color: var(--color-text-light);
  margin-top: 2px;
}
.hot-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
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
.hot-title {
  font-size: 13px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.not-found {
  text-align: center;
  padding: 80px 20px;
  font-size: 16px;
  color: var(--color-text-secondary);
}
.not-found a {
  display: inline-block;
  margin-top: 12px;
  color: #1a2a4a;
  font-weight: 600;
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
  .detail-sidebar {
    min-width: auto;
    padding: 0 8px 16px;
    display: flex;
    gap: 12px;
    flex-wrap: wrap;
    max-width: 100vw;
  }
  .detail-sidebar .card {
    flex: 1;
    min-width: calc(50% - 6px);
    max-width: calc(50% - 6px);
    padding: 14px;
    border-radius: var(--radius-lg);
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
    background: #fff;
    margin-bottom: 0;
    overflow: hidden;
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
  .article {
    padding: 12px 0;
    border-radius: 0;
    box-shadow: none;
    background: transparent;
    max-width: 100vw;
    overflow-x: hidden;
  }
  .article-title {
    font-size: 20px;
    font-weight: 700;
    line-height: 1.45;
    letter-spacing: 0.3px;
    margin-bottom: 12px;
    padding: 0 12px;
    word-break: break-word;
  }
  .article-meta {
    gap: 6px;
    font-size: 12px;
    flex-wrap: wrap;
    padding: 0 12px 12px;
    margin-bottom: 14px;
    border-bottom: 1px solid #f0f0f0;
  }
  .breaking-tag {
    padding: 1.5px 6px;
    font-size: 10px;
    letter-spacing: 0.5px;
  }
  .channel-tag {
    padding: 1.5px 6px;
    font-size: 10.5px;
    border-radius: 2px;
  }
  .translate-btn {
    padding: 2px 8px;
    font-size: 11px;
    border-radius: 3px;
  }
  .article-cover {
    width: 100vw;
    margin-left: calc(-50vw + 50%);
    max-height: 220px;
    border-radius: 0;
    margin-bottom: 14px;
    object-fit: cover;
    display: block;
  }
  .article-content {
    font-size: 15px;
    line-height: 1.85;
    color: #333;
    padding: 0 12px;
    max-width: 100%;
    overflow-x: hidden;
    word-break: break-word;
  }
  .article-content :deep(h2) {
    font-size: 17px;
    margin: 18px 0 10px;
    word-break: break-word;
  }
  .article-content :deep(h3) {
    font-size: 16px;
    margin: 14px 0 8px;
    word-break: break-word;
  }
  .article-content :deep(p) {
    margin-bottom: 14px;
    text-indent: 2em;
    word-break: break-word;
  }
  .article-content :deep(img) {
    max-width: 100% !important;
    width: 100% !important;
    height: auto !important;
    border-radius: 6px;
    margin: 8px 0;
    display: block;
  }
  .article-content :deep(p img) {
    display: block;
    margin: 12px 0;
    text-indent: 0;
  }
  .article-content :deep(table) {
    width: 100% !important;
    font-size: 13px;
    display: block;
    overflow-x: auto;
    -webkit-overflow-scrolling: touch;
  }
  .article-content :deep(pre) {
    width: 100% !important;
    font-size: 12px;
    overflow-x: auto;
    -webkit-overflow-scrolling: touch;
    padding: 10px;
    box-sizing: border-box;
  }
  .article-content :deep(blockquote) {
    margin: 12px 0;
    padding: 8px 12px;
    border-left: 3px solid #1a2a4a;
    background: #f8f9fa;
    font-size: 14px;
  }
  .article-content :deep(ul), .article-content :deep(ol) {
    padding-left: 20px;
    margin-bottom: 14px;
  }
  .article-content :deep(a) {
    word-break: break-all;
  }
  .article-footer {
    flex-direction: row;
    justify-content: space-between;
    gap: 12px;
    align-items: center;
    padding: 12px;
    margin-top: 12px;
    border-top: 1px solid #f0f0f0;
  }
  .footer-tags {
    gap: 6px;
    flex-wrap: wrap;
  }
  .footer-actions {
    gap: 8px;
  }
  .action-btn {
    padding: 6px 14px;
    font-size: 12px;
    border-radius: 20px;
    min-height: 36px;
    -webkit-tap-highlight-color: transparent;
    touch-action: manipulation;
  }
  .action-icon {
    font-size: 14px;
  }
  .section-title {
    font-size: 16px;
    margin-bottom: 12px;
    padding-bottom: 8px;
    font-weight: 600;
    border-bottom-width: 2px;
    padding-left: 8px;
  }
  .comment-section {
    padding: 12px;
    border-radius: 0;
    box-shadow: none;
    background: transparent;
    margin-top: 12px;
    max-width: 100vw;
    overflow-x: hidden;
  }
  .comment-section-inner {
    background: #fff;
    padding: 16px;
    border-radius: var(--radius-lg);
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
  }
  .comment-form textarea {
    width: 100%;
    padding: 10px 12px;
    font-size: 14px;
    border-radius: 8px;
    border: 1px solid #e0e0e0;
    resize: vertical;
    min-height: 80px;
    box-sizing: border-box;
  }
  .comment-form .btn {
    width: 100%;
    margin-top: 10px;
    min-height: 44px;
    border-radius: 8px;
    font-weight: 500;
  }
  .login-prompt {
    padding: 12px;
    border-radius: 8px;
    font-size: 13px;
    text-align: center;
    background: #f8f8f8;
  }
  .login-prompt a {
    font-weight: 600;
  }
  .comment-list {
    gap: 12px;
  }
  .comment-item {
    padding: 10px 0;
    gap: 10px;
    border-bottom: 1px solid #f5f5f5;
  }
  .comment-avatar {
    width: 32px;
    height: 32px;
    font-size: 12.5px;
  }
  .comment-user {
    font-size: 13px;
    font-weight: 600;
  }
  .comment-time {
    font-size: 11px;
  }
  .comment-text {
    font-size: 13px;
    line-height: 1.55;
    word-break: break-word;
  }
  .sidebar-title {
    font-size: 15px;
    margin-bottom: 10px;
    padding-bottom: 6px;
    font-weight: 600;
    border-bottom-width: 2px;
  }
  .related-list {
    gap: 8px;
  }
  .related-item {
    padding: 6px;
    border-radius: var(--radius-md);
    transition: background 0.15s;
    cursor: pointer;
  }
  .related-item:active {
    background: #f5f5f5;
  }
  .related-title {
    font-size: 13px;
    line-height: 1.45;
    word-break: break-word;
  }
  .related-meta {
    font-size: 10.5px;
  }
  .hot-list {
    gap: 6px;
  }
  .hot-item {
    padding: 6px 8px;
    border-radius: var(--radius-md);
    transition: background 0.15s;
    align-items: center;
    cursor: pointer;
  }
  .hot-item:active {
    background: #f5f5f5;
  }
  .rank-num {
    width: 18px;
    height: 18px;
    font-size: 10.5px;
    font-weight: 700;
    border-radius: 3px;
  }
  .hot-title {
    font-size: 13px;
    line-height: 1.45;
    word-break: break-word;
  }
  .not-found {
    padding: 40px 20px;
    font-size: 14px;
    text-align: center;
  }
}

@media (max-width: 480px) {
  .article-title {
    font-size: 18px;
    padding: 0 10px;
  }
  .article-meta {
    padding: 0 10px 10px;
    gap: 5px;
    font-size: 11px;
  }
  .article-cover {
    max-height: 180px;
  }
  .article-content {
    font-size: 14px;
    padding: 0 10px;
    line-height: 1.8;
  }
  .article-content :deep(h2) {
    font-size: 16px;
  }
  .article-content :deep(h3) {
    font-size: 15px;
  }
  .article-footer {
    padding: 10px;
  }
  .action-btn {
    padding: 5px 12px;
    font-size: 11px;
    min-height: 34px;
  }
  .comment-section {
    padding: 10px;
  }
  .detail-sidebar .card {
    min-width: 100%;
    max-width: 100%;
  }
}
</style>
