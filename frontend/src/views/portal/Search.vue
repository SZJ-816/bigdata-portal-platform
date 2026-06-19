<template>
  <div class="search-page">
    <div class="search-hero">
      <h1 class="search-title">搜索新闻</h1>
      <div class="search-bar">
        <input v-model="keyword" class="search-input" placeholder="输入关键词搜索新闻..." @keyup.enter="doSearch" />
        <button class="btn btn-primary search-btn" @click="doSearch">搜索</button>
        <button class="btn btn-ai search-btn" @click="doAiSearch" :disabled="aiLoading">
          <span v-if="aiLoading" class="ai-loading"></span>
          <span v-else><svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="currentColor" stroke="none"><path d="M12 2L9.19 8.63 2 9.24l5.46 4.73L5.82 21 12 17.27 18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2z"/></svg></span> AI 搜索
        </button>
      </div>
      <div class="search-tabs">
        <button :class="['tab-btn', { active: activeTab === 'news' }]" @click="activeTab = 'news'">新闻搜索</button>
        <button :class="['tab-btn', { active: activeTab === 'ai' }]" @click="activeTab = 'ai'">AI 智能搜索</button>
      </div>
      <div class="hot-words">
        <span class="hot-label">热搜：</span>
        <button v-for="word in hotSearchWords" :key="word" class="hot-word" @click="searchHotWord(word)">{{ word }}</button>
      </div>
    </div>

    <div v-if="activeTab === 'news'" class="search-results">
      <div v-if="searchLoading" class="loading-skeleton">
        <div v-for="i in 5" :key="i" class="skeleton-item">
          <div class="skeleton-content">
            <div class="skeleton-title"></div>
            <div class="skeleton-meta"></div>
          </div>
        </div>
      </div>
      <div v-else-if="processedResults.length" class="result-section">
        <p class="result-count">找到 {{ processedResults.length }} 条相关新闻</p>
        <div class="result-list">
          <div v-for="item in processedResults" :key="item.id" class="result-item" @click="goNews(item.id)">
            <h3 class="result-title" v-html="item.title"></h3>
            <p class="result-summary" v-html="item.summary"></p>
            <div class="result-meta">
              <span class="channel-tag-sm">{{ item.channelLabel }}</span>
              <span>{{ item.source }}</span>
              <span>{{ formatRelativeTime(item.publishTime) }}</span>
              <span>{{ formatViewCount(item.viewCount) }} 阅读</span>
            </div>
          </div>
        </div>
      </div>
      <div v-else-if="searched" class="empty-state">
        <div class="empty-icon"><svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><circle cx="11" cy="11" r="7"/><path d="M21 21l-4.35-4.35"/></svg></div>
        <p>未找到与"{{ searchedKeyword }}"相关的新闻</p>
        <p class="empty-hint">试试其他关键词吧</p>
      </div>
    </div>

    <div v-if="activeTab === 'ai'" class="ai-results">
      <div v-if="aiAnswer || aiLoading" class="ai-answer-card">
        <div class="ai-answer-header">
          <span class="ai-icon"><svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="currentColor" stroke="none"><path d="M12 2L9.19 8.63 2 9.24l5.46 4.73L5.82 21 12 17.27 18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2z"/></svg></span>
          <span class="ai-label">AI 智能分析</span>
          <span v-if="aiLoading" class="ai-typing">正在深度分析中...</span>
        </div>
        <div class="ai-answer-content" v-html="renderMarkdown(aiAnswer)"></div>
        <div v-if="aiLoading" class="ai-cursor">▊</div>
      </div>
      <div v-else class="ai-empty-state">
        <div class="ai-empty-icon"><svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M12 2L9.19 8.63 2 9.24l5.46 4.73L5.82 21 12 17.27 18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2z"/></svg></div>
        <p>输入关键词，点击 AI 搜索获取智能分析</p>
        <p class="ai-empty-hint">AI 将结合平台新闻数据，为你提供深度分析和专业解读</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { hotSearchWords, formatRelativeTime } from '../../utils'
import { behaviorApi, newsApi } from '../../api'
import request from '../../api'
import { cleanText, formatViewCount, CHANNEL_LABEL_MAP, renderMarkdown, getFullApiUrl } from '../../utils'

const route = useRoute()
const router = useRouter()

const keyword = ref('')
const results = ref([])
const searched = ref(false)
const searchedKeyword = ref('')
const searchLoading = ref(false)
const activeTab = ref('news')

const aiAnswer = ref('')
const aiLoading = ref(false)
let aiAbortController = null

const channelLabelMap = CHANNEL_LABEL_MAP

const processedResults = ref([])
let searchTimer = null

function highlightKeywords(text, searchKeyword) {
  if (!searchKeyword || !text) return text
  const keywords = searchKeyword.trim().split(/\s+/).filter(k => k)
  if (!keywords.length) return text
  let result = text
  keywords.forEach(keyword => {
    const regex = new RegExp(`(${keyword})`, 'gi')
    result = result.replace(regex, '<span class="highlight-keyword">$1</span>')
  })
  return result
}

function updateProcessedResults() {
  processedResults.value = results.value.map(item => ({
    ...item,
    title: highlightKeywords(cleanText(item.title), searchedKeyword.value),
    summary: highlightKeywords(cleanText(item.summary), searchedKeyword.value),
    channelLabel: channelLabelMap[item.channel] || item.channel
  }))
}

async function doSearch() {
  if (!keyword.value.trim()) return
  if (searchTimer) {
    clearTimeout(searchTimer)
    searchTimer = null
  }
  searched.value = true
  searchedKeyword.value = keyword.value
  activeTab.value = 'news'
  searchLoading.value = true
  try {
    const res = await newsApi.search(keyword.value)
    const body = res.data
    // 兼容多种 API 返回格式
    if (body?.data?.records) {
      results.value = body.data.records
    } else if (body?.data?.data) {
      results.value = body.data.data
    } else if (Array.isArray(body?.data)) {
      results.value = body.data
    } else if (Array.isArray(body)) {
      results.value = body
    } else if (body?.records) {
      results.value = body.records
    } else {
      results.value = []
    }
  } catch (err) {
    console.error('Failed to search news:', err)
    results.value = []
  }
  updateProcessedResults()
  searchLoading.value = false
  behaviorApi.report({ eventType: 'search', targetId: keyword.value, targetType: 'keyword' })
}

function debouncedSearch() {
  if (searchTimer) clearTimeout(searchTimer)
  searchTimer = setTimeout(doSearch, 300)
}

async function doAiSearch() {
  if (!keyword.value.trim()) return
  activeTab.value = 'ai'
  aiAnswer.value = ''
  aiLoading.value = true
  if (aiAbortController) aiAbortController.abort()
  aiAbortController = new AbortController()

  let streamSuccess = false
  try {
    const headers = {}
    const token = localStorage.getItem('token')
    if (token) headers['Authorization'] = `Bearer ${token}`
    const response = await fetch(getFullApiUrl('/ai/search/stream?keyword=' + encodeURIComponent(keyword.value)), {
      signal: aiAbortController.signal,
      headers
    })
    if (!response.ok) throw new Error('Stream response not ok')

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
          if (data.startsWith('[ERROR]')) {
            aiAnswer.value += '\n\n[错误] ' + data.substring(7)
            continue
          }
          aiAnswer.value += data
          streamSuccess = true
        }
      }
    }
    if (buffer.trim()) {
      if (buffer.startsWith('data:')) {
        const data = buffer.substring(5).trim()
        if (data !== '[DONE]' && !data.startsWith('[ERROR]')) {
          aiAnswer.value += data
          streamSuccess = true
        }
      }
    }
  } catch (err) {
    if (err.name !== 'AbortError') {
      console.warn('AI stream failed, trying non-stream:', err.message)
    }
  }

  if (!streamSuccess) {
    aiAnswer.value = ''
    try {
      const res = await request.get('/ai/search', { params: { keyword: keyword.value }, timeout: 120000 })
      if (res.data.success) {
        aiAnswer.value = res.data.data
      } else {
        aiAnswer.value = 'AI 搜索暂时不可用：' + (res.data.message || '未知错误')
      }
    } catch (err2) {
      console.error('AI search failed:', err2)
      aiAnswer.value = 'AI 搜索暂时不可用，请稍后再试。'
    }
  }

  aiLoading.value = false
  behaviorApi.report({ eventType: 'ai_search', targetId: keyword.value, targetType: 'keyword' })
}

function searchHotWord(word) {
  keyword.value = word
  doSearch()
}

function goNews(id) {
  behaviorApi.report({ eventType: 'click', targetId: String(id), targetType: 'news' })
  router.push(`/news/${id}`)
}

onMounted(() => {
  if (route.query.q) {
    keyword.value = route.query.q
    doSearch()
  }
})

onUnmounted(() => {
  if (searchTimer) {
    clearTimeout(searchTimer)
    searchTimer = null
  }
  if (aiAbortController) aiAbortController.abort()
  behaviorApi.flush()
})

watch(() => route.query.q, (newQ) => {
  if (newQ) {
    keyword.value = newQ
    doSearch()
  }
})
</script>

<style scoped>
.search-hero {
  text-align: center;
  padding: 40px 0 24px;
}
.search-title {
  font-size: 28px;
  font-weight: 800;
  color: var(--color-primary);
  margin-bottom: 24px;
  font-family: Georgia, 'Noto Serif SC', serif;
}
.search-bar {
  display: flex;
  max-width: 700px;
  margin: 0 auto;
  gap: 8px;
}
.search-input {
  flex: 1;
  padding: 12px 18px;
  font-size: 16px;
  border: 2px solid var(--color-border);
  border-radius: 4px;
  transition: border-color 0.2s;
}
.search-input:focus {
  border-color: var(--color-primary);
  outline: none;
}
.search-btn {
  padding: 12px 28px;
  font-size: 15px;
  border-radius: 4px;
  white-space: nowrap;
}
.btn-ai {
  background: var(--color-gradient-primary);
  color: var(--color-text-white);
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: all 0.2s;
}
.btn-ai:hover:not(:disabled) {
  background: var(--color-gradient-primary-hover);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px var(--color-purple-glow);
}
.btn-ai:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}
.ai-loading {
  display: inline-block;
  width: 14px;
  height: 14px;
  border: 2px solid var(--color-white-overlay);
  border-top-color: var(--color-text-white);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}
@keyframes spin {
  to { transform: rotate(360deg); }
}
.search-tabs {
  display: flex;
  justify-content: center;
  gap: 4px;
  margin-top: 16px;
}
.tab-btn {
  padding: 8px 24px;
  border: none;
  background: transparent;
  color: var(--color-text-secondary);
  font-size: 14px;
  cursor: pointer;
  border-radius: 4px;
  transition: all 0.2s;
}
.tab-btn:hover {
  background: var(--color-bg-tertiary);
}
.tab-btn.active {
  background: var(--color-primary);
  color: var(--color-text-white);
}
.hot-words {
  margin-top: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  flex-wrap: wrap;
}
.hot-label {
  font-size: 13px;
  color: var(--color-text-light);
}
.hot-word {
  display: inline-block;
  padding: 4px 14px;
  background: var(--color-bg-tertiary);
  border: none;
  border-radius: 3px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
  color: var(--color-text-secondary);
  font-family: inherit;
}
.hot-word:hover {
  background: var(--color-primary);
  color: var(--color-text-white);
}
.loading-skeleton {
  display: flex;
  flex-direction: column;
  gap: 14px;
}
.skeleton-item {
  display: flex;
  gap: 14px;
  padding: 18px 20px;
  background: var(--color-bg-white);
  border-radius: 4px;
  box-shadow: 0 1px 3px var(--color-card-shadow);
}
.skeleton-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.skeleton-title {
  height: 22px;
  width: 60%;
  border-radius: 2px;
  background: var(--color-skeleton);
  animation: skeleton-shine 1.5s ease-in-out infinite;
}
.skeleton-meta {
  height: 14px;
  width: 30%;
  border-radius: 2px;
  background: var(--color-skeleton);
  animation: skeleton-shine 1.5s ease-in-out infinite;
}
@keyframes skeleton-shine {
  0% { background: var(--color-skeleton); }
  50% { background: var(--color-skeleton-shine); }
  100% { background: var(--color-skeleton); }
}
.result-count {
  font-size: 14px;
  color: var(--color-text-light);
  margin-bottom: 16px;
}
.result-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}
.result-item {
  padding: 18px 20px;
  background: var(--color-bg-white);
  border-radius: 4px;
  box-shadow: 0 1px 3px var(--color-card-shadow);
  cursor: pointer;
  transition: box-shadow 0.2s;
}
.result-item:hover {
  box-shadow: 0 3px 10px var(--color-shadow-md);
}
.result-item:hover .result-title {
  text-decoration: underline;
}
.result-title {
  font-size: 17px;
  font-weight: 600;
  line-height: 1.4;
  color: var(--color-primary);
}
.result-summary {
  font-size: 14px;
  color: var(--color-text-secondary);
  line-height: 1.5;
  margin-top: 8px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.result-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 10px;
  font-size: 12px;
  color: var(--color-text-light);
}
.channel-tag-sm {
  background: var(--color-tag-bg);
  color: var(--color-tag-text);
  padding: 1px 8px;
  border-radius: 2px;
  font-size: 11px;
}
.highlight-keyword {
  background-color: #ff9;
  padding: 0 2px;
  border-radius: 2px;
  font-weight: 600;
}
@media (prefers-color-scheme: dark) {
  .highlight-keyword {
    background-color: #555;
    color: #ff0;
  }
}
[data-theme="dark"] .highlight-keyword {
  background-color: #4a4a4a;
  color: #ffdd00;
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
.empty-state p {
  margin-bottom: 8px;
}
.empty-hint {
  font-size: 13px !important;
  color: var(--color-text-light) !important;
}

.ai-results {
  margin-top: 8px;
}
.ai-answer-card {
  background: var(--color-bg-white);
  border-radius: 8px;
  box-shadow: 0 2px 12px var(--color-card-shadow);
  overflow: hidden;
}
.ai-answer-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 16px 24px;
  background: linear-gradient(135deg, var(--color-purple-bg), var(--color-purple-bg-end));
  border-bottom: 1px solid var(--color-purple-border);
}
.ai-icon {
  display: inline-flex;
  align-items: center;
  color: var(--color-purple);
}
.ai-icon svg {
  width: 16px;
  height: 16px;
}
.ai-label {
  font-size: 15px;
  font-weight: 600;
  color: var(--color-purple-dark);
}
.ai-typing {
  font-size: 13px;
  color: var(--color-purple-light);
  animation: pulse 1.5s ease-in-out infinite;
}
@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.4; }
}
.ai-answer-content {
  padding: 24px;
  font-size: 15px;
  line-height: 1.8;
  color: var(--color-text);
}
.ai-answer-content :deep(p) {
  margin-bottom: 12px;
}
.ai-answer-content :deep(strong) {
  color: var(--color-purple-dark);
}
.ai-answer-content :deep(code) {
  background: var(--color-purple-bg);
  padding: 2px 6px;
  border-radius: 3px;
  font-size: 13px;
  color: var(--color-purple);
}
.ai-answer-content :deep(pre) {
  background: var(--color-primary);
  color: var(--color-text);
  padding: 16px;
  border-radius: 6px;
  overflow-x: auto;
  margin: 12px 0;
}
.ai-answer-content :deep(pre code) {
  background: transparent;
  color: inherit;
  padding: 0;
}
.ai-cursor {
  display: inline;
  color: var(--color-purple);
  animation: blink 1s step-end infinite;
  font-size: 16px;
  margin-left: 2px;
}
@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0; }
}
.ai-empty-state {
  text-align: center;
  padding: 80px 20px;
}
.ai-empty-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-purple);
  margin-bottom: 16px;
  opacity: 0.5;
}
.ai-empty-icon svg {
  width: 48px;
  height: 48px;
}
.ai-empty-state p {
  color: var(--color-text-secondary);
  font-size: 16px;
  margin-bottom: 8px;
}
.ai-empty-hint {
  font-size: 14px !important;
  color: var(--color-text-light) !important;
}
@media (max-width: 768px) {
  .search-hero {
    padding: 20px 0 16px;
    text-align: left;
  }
  .search-title {
    font-size: 22px;
    margin-bottom: 16px;
    font-weight: 700;
  }
  .search-bar {
    flex-wrap: wrap;
    gap: 10px;
  }
  .search-input {
    width: 100%;
    flex: none;
    padding: 12px 16px;
    font-size: 15px;
    border-radius: 10px;
    border: 2px solid var(--color-border);
    transition: all 0.2s;
  }
  .search-input:focus {
    border-color: var(--color-primary);
    box-shadow: 0 0 0 3px var(--color-primary-overlay);
  }
  .search-btn {
    flex: 1;
    padding: 12px 20px;
    font-size: 14px;
    border-radius: 10px;
    font-weight: 500;
    min-height: 44px;
  }
  .btn-ai {
    flex: 1;
    gap: 8px;
  }
  .search-tabs {
    margin-top: 14px;
    gap: 6px;
  }
  .tab-btn {
    padding: 8px 20px;
    font-size: 13px;
    border-radius: 8px;
  }
  .hot-words {
    gap: 8px;
    justify-content: flex-start;
    margin-top: 14px;
  }
  .hot-label {
    font-size: 12.5px;
    color: var(--color-text-light);
  }
  .hot-word {
    padding: 4px 12px;
    font-size: 12.5px;
    border-radius: 4px;
    transition: all 0.15s;
  }
  .hot-word:active {
    transform: scale(0.95);
  }
  .loading-skeleton {
    gap: 10px;
  }
  .skeleton-item {
    padding: 14px;
    border-radius: var(--radius-lg);
    box-shadow: 0 1px 4px var(--color-shadow-sm);
  }
  .result-count {
    font-size: 13px;
    margin-bottom: 12px;
  }
  .result-list {
    gap: 10px;
  }
  .result-item {
    padding: 14px;
    border-radius: var(--radius-lg);
    box-shadow: 0 1px 4px var(--color-shadow-sm);
    transition: box-shadow 0.15s;
  }
  .result-item:active {
    transform: scale(0.99);
    box-shadow: 0 2px 8px var(--color-shadow-lg);
  }
  .result-title {
    font-size: 15.5px;
    font-weight: 600;
    line-height: 1.45;
    color: var(--color-text);
  }
  .result-summary {
    font-size: 13px;
    margin-top: 6px;
    line-height: 1.5;
  }
  .result-meta {
    flex-wrap: wrap;
    gap: 8px;
    font-size: 11.5px;
    margin-top: 8px;
  }
  .channel-tag-sm {
    padding: 2px 8px;
    font-size: 10.5px;
    border-radius: 3px;
  }
  .empty-state {
    padding: 40px 20px;
    font-size: 14px;
  }
  .ai-answer-card {
    border-radius: var(--radius-lg);
    overflow: hidden;
  }
  .ai-answer-header {
    padding: 14px 16px;
    gap: 10px;
  }
  .ai-icon svg {
    width: 19px;
    height: 19px;
  }
  .ai-label {
    font-size: 14.5px;
    font-weight: 600;
  }
  .ai-answer-content {
    padding: 18px;
    font-size: 14px;
    line-height: 1.7;
  }
  .ai-empty-state {
    padding: 48px 16px;
  }
  .ai-empty-icon svg {
    width: 40px;
    height: 40px;
    margin-bottom: 14px;
  }
  .ai-empty-state p {
    font-size: 15px;
    margin-bottom: 6px;
  }
  .ai-empty-hint {
    font-size: 13px !important;
  }
}

@media (max-width: 480px) {
  .search-title {
    font-size: 20px;
  }
  .result-title {
    font-size: 14.5px;
  }
  .result-summary {
    font-size: 12.5px;
  }
}
</style>
