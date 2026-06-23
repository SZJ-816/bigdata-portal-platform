<template>
  <article class="article">
    <h1 class="article-title">{{ news.title }}</h1>
    <div class="article-meta">
      <span v-if="news.isBreaking" class="breaking-tag">BREAKING</span>
      <span class="channel-tag" @click="$emit('goChannel', news.channel)">{{ channelLabelMap[news.channel] || news.channel }}</span>
      <span>{{ news.source }}</span>
      <span>{{ formatRelativeTime(news.publishTime) }}</span>
      <span>{{ formatViewCount(news.viewCount) }} 阅读</span>
      <span>{{ news.commentCount }} 评论</span>
      <button v-if="isEnglish" class="translate-btn" @click="$emit('translate')" :disabled="translating">
        <span v-if="translating" class="ai-spin-sm"></span>
        {{ translated ? '已翻译' : '翻译' }}
      </button>
    </div>
    <img v-if="news.imageUrl" :src="resolveImageUrl(news.imageUrl)" :alt="news.title" :data-channel="news.channel" class="article-cover" @error="onCoverImgError" />
    <div class="article-content" v-html="resolveContentImages(news.content)"></div>

    <!-- AI 智能分析区域 -->
    <div v-if="aiSummary || aiTags" class="ai-analysis">
      <div class="ai-badge">AI 智能分析</div>
      <div v-if="aiSummary" class="ai-summary">
        <h4>摘要</h4>
        <p>{{ aiSummary }}</p>
      </div>
      <div v-if="aiTags" class="ai-tags-result">
        <h4>标签</h4>
        <div class="tag-list">
          <span v-for="tag in aiTags.split(/[,，]/)" :key="tag" class="ai-tag">{{ tag.trim() }}</span>
        </div>
      </div>
    </div>
  </article>

  <div class="article-footer">
    <div class="footer-tags">
      <span class="channel-tag" @click="$emit('goChannel', news.channel)">{{ channelLabelMap[news.channel] || news.channel }}</span>
    </div>
    <div class="footer-actions">
      <button class="action-btn" @click="analyzeWithAI" :disabled="aiLoading">
        <span class="action-icon"><svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M12 2a4 4 0 014 4v2a4 4 0 01-8 0V6a4 4 0 014-4z"/><path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2"/></svg></span>
        {{ aiLoading ? '分析中...' : 'AI分析' }}
      </button>
      <button class="action-btn" @click="$emit('share')">
        <span class="action-icon"><svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M4 12v8a2 2 0 002 2h12a2 2 0 002-2v-8"/><polyline points="16 6 12 2 8 6"/><line x1="12" y1="2" x2="12" y2="15"/></svg></span> 分享
      </button>
      <button :class="['action-btn', { active: isFavorited }]" @click="$emit('favorite')">
        <span class="action-icon"><svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M12 21C12 21 3 14.5 3 8.5a4.5 4.5 0 019-1 4.5 4.5 0 019 1C21 14.5 12 21 12 21z"/></svg></span> {{ isFavorited ? '已收藏' : '收藏' }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { formatRelativeTime, formatViewCount, getChannelIcon, CHANNEL_LABEL_MAP, resolveImageUrl, resolveAssetUrl } from '../../utils'
import { aiApi } from '../../api'

const channelLabelMap = CHANNEL_LABEL_MAP
const aiSummary = ref('')
const aiTags = ref('')
const aiLoading = ref(false)

const props = defineProps({
  news: { type: Object, required: true },
  isFavorited: { type: Boolean, default: false },
  translating: { type: Boolean, default: false },
  translated: { type: Boolean, default: false }
})

defineEmits(['goChannel', 'translate', 'share', 'favorite'])

const isEnglish = computed(() => {
  if (!props.news || !props.news.title) return false
  return !/[\u4e00-\u9fa5]/.test(props.news.title)
})

function resolveContentImages(content) {
  if (!content) return content
  content = content.replace(/src=["'](https?:\/\/[^"']+)["']/g, (match, url) => {
    const proxySrc = '/api/image/proxy?url=' + encodeURIComponent(url)
    return `src="${proxySrc}"`
  })
  const base = window.__API_BASE_URL__ || ''
  const baseUrl = (base && base.startsWith('http')) ? base.replace(/\/api$/, '') : ''
  if (!baseUrl) return content
  content = content.replace(/src=["'](\/[^"']+)["']/g, (match, path) => {
    return `src="${baseUrl}${path}"`
  })
  return content
}

function onCoverImgError(e) {
  const el = e.target
  const channel = el.dataset?.channel || ''
  const fallback = getChannelIcon(channel)
  const resolvedFallback = resolveAssetUrl(fallback)
  if (!el.src.endsWith(resolvedFallback)) {
    el.src = resolvedFallback
  }
}

async function analyzeWithAI() {
  if (aiLoading.value) return
  aiLoading.value = true
  const content = (props.news.title || '') + '\n' + (props.news.summary || '') + '\n' + (props.news.content || '')
  try {
    const [summaryRes, tagsRes] = await Promise.all([
      aiApi.generateSummary(content.replace(/<[^>]*>/g, ''), 200),
      aiApi.generateTags(content.replace(/<[^>]*>/g, ''))
    ])
    if (summaryRes.data.success && summaryRes.data.data) {
      aiSummary.value = summaryRes.data.data.summary
    }
    if (tagsRes.data.success && tagsRes.data.data) {
      aiTags.value = tagsRes.data.data.tags
    }
  } catch (err) {
    console.error('AI analysis failed:', err)
  } finally {
    aiLoading.value = false
  }
}
</script>

<style scoped>
.article {
  background: var(--color-bg-white);
  padding: 32px;
  border-radius: 4px;
  box-shadow: 0 1px 4px var(--color-shadow-sm);
}
.article-title {
  font-size: 28px;
  font-weight: 700;
  line-height: 1.4;
  color: var(--color-text);
  font-family: 'Noto Serif SC', 'Source Han Serif SC', 'Songti SC', Georgia, 'Times New Roman', serif;
  letter-spacing: 0.5px;
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
  background: var(--color-accent);
  color: var(--color-text-white);
  padding: 2px 10px;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 1px;
  border-radius: 2px;
}
.channel-tag {
  background: var(--color-tag-bg);
  color: var(--color-tag-text);
  padding: 2px 10px;
  border-radius: 2px;
  font-size: 12px;
  cursor: pointer;
}
.channel-tag:hover {
  background: var(--color-primary);
}
.translate-btn {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 2px 10px;
  background: var(--color-gradient-primary);
  color: var(--color-text-white);
  border: none;
  border-radius: 3px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.2s;
}
.translate-btn:hover:not(:disabled) {
  background: var(--color-gradient-primary-hover);
}
.translate-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
.ai-spin-sm {
  display: inline-block;
  width: 12px;
  height: 12px;
  border: 2px solid var(--color-white-overlay);
  border-top-color: var(--color-text-white);
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
  background-color: var(--color-bg-tertiary);
}
.article-content {
  font-size: 16px;
  line-height: 1.85;
  color: var(--color-text-article);
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
  background: var(--color-bg-white);
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
  color: var(--color-text-secondary);
}
.action-btn:hover {
  border-color: var(--color-primary);
  color: var(--color-primary);
}
.action-btn.active {
  border-color: var(--color-accent);
  color: var(--color-accent);
}
.action-icon {
  font-size: 14px;
  display: inline-flex;
  align-items: center;
  vertical-align: middle;
}
.action-btn.active .action-icon svg {
  fill: var(--color-accent);
}
@media (max-width: 768px) {
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
    border-bottom: 1px solid var(--color-bg-tertiary);
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
    color: var(--color-text-secondary);
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
    text-indent: 1.5em;
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
    border-left: 3px solid var(--color-primary);
    background: var(--color-bg-secondary);
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
    border-top: 1px solid var(--color-bg-tertiary);
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
}
@media (max-width: 375px) {
  .article-title {
    font-size: 17px;
    padding: 0 10px;
  }
  .article-meta {
    padding: 0 10px 10px;
    font-size: 11px;
  }
  .article-cover {
    max-height: 160px;
  }
  .article-content {
    font-size: 13px;
    padding: 0 10px;
    line-height: 1.75;
  }
  .article-content :deep(h2) {
    font-size: 15px;
  }
  .article-content :deep(h3) {
    font-size: 14px;
  }
}
.ai-analysis {
  margin-top: 24px;
  padding: 20px;
  background: linear-gradient(135deg, #f0f7ff, #f5f0ff);
  border-radius: 12px;
  border: 1px solid #e0e7ff;
}
.ai-badge {
  display: inline-block;
  padding: 4px 12px;
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  color: white;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  margin-bottom: 16px;
}
.ai-summary h4, .ai-tags-result h4 {
  font-size: 14px;
  font-weight: 600;
  color: #4338ca;
  margin-bottom: 8px;
}
.ai-summary p {
  font-size: 14px;
  color: #374151;
  line-height: 1.7;
}
.ai-tag {
  display: inline-block;
  padding: 4px 12px;
  background: white;
  color: #6366f1;
  border-radius: 16px;
  font-size: 13px;
  margin: 2px 4px;
  border: 1px solid #c7d2fe;
}
</style>
