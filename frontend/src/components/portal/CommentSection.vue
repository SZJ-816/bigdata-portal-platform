<template>
  <div class="comment-section">
    <div class="comment-section-inner">
      <h3 class="section-title">评论 ({{ comments.length }})</h3>
      <div v-if="isLoggedIn" class="comment-form">
        <textarea v-model="commentText" placeholder="发表你的看法..." rows="3"></textarea>
        <button class="btn btn-primary" @click="handleSubmit">发表评论</button>
      </div>
      <div v-else class="login-prompt">
        <router-link :to="`/login?redirect=${encodeURIComponent(redirectPath)}`">登录</router-link> 后即可发表评论
      </div>
      <div v-if="comments.length === 0" class="comment-empty">
        <div class="empty-icon"><svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M21 11.5a8.38 8.38 0 01-.9 3.8 8.5 8.5 0 01-7.6 4.7 8.38 8.38 0 01-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 01-.9-3.8 8.5 8.5 0 014.7-7.6 8.38 8.38 0 013.8-.9h.5a8.48 8.48 0 018 8v.5z"/></svg></div>
        <p>暂无评论，快来发表第一条吧</p>
      </div>
      <div v-else class="comment-list">
        <div v-for="c in comments" :key="c.id" class="comment-item">
          <div class="comment-avatar">{{ (c.username || 'U')[0].toUpperCase() }}</div>
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
</template>

<script setup>
import { ref } from 'vue'
import { commentApi, behaviorApi } from '../../api'
import { formatRelativeTime } from '../../utils'
import { useAuthStore } from '../../stores/auth'

const props = defineProps({
  comments: { type: Array, default: () => [] },
  newsId: { type: [String, Number], required: true },
  redirectPath: { type: String, default: '' }
})

const emit = defineEmits(['submit', 'toast'])

const authStore = useAuthStore()
const isLoggedIn = authStore.isLoggedIn
const commentText = ref('')

async function handleSubmit() {
  if (!commentText.value.trim()) return
  if (!authStore.isLoggedIn) return
  try {
    const userId = localStorage.getItem('userId')
    const res = await commentApi.add(props.newsId, { userId: Number(userId), content: commentText.value })
    if (res.data?.success || res.data?.data) {
      commentText.value = ''
      emit('submit')
      emit('toast', '评论发送成功')
      behaviorApi.report({ eventType: 'comment', targetId: String(props.newsId), targetType: 'news' })
    }
  } catch (err) {
    console.error('Failed to submit comment:', err)
    emit('toast', '评论发送失败，请稍后重试')
  }
}
</script>

<style scoped>
.section-title {
  font-size: 18px;
  font-weight: 700;
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 2px solid var(--color-primary);
}
.comment-section {
  margin-top: 24px;
  background: transparent;
  padding: 0;
  border-radius: 0;
  box-shadow: none;
}
.comment-section-inner {
  background: var(--color-bg-white);
  padding: 24px;
  border-radius: 4px;
  box-shadow: 0 1px 4px var(--color-shadow-sm);
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
  background: var(--color-bg);
  border-radius: 4px;
  margin-bottom: 20px;
  font-size: 14px;
  color: var(--color-text-secondary);
}
.login-prompt a {
  color: var(--color-text);
  font-weight: 600;
}
.comment-empty {
  text-align: center;
  padding: 40px 20px;
  color: var(--color-text-secondary);
}
.comment-empty .empty-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-text-light);
  margin-bottom: 12px;
}
.comment-empty .empty-icon svg {
  width: 48px;
  height: 48px;
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
  border-bottom: 1px solid var(--color-border);
}
.comment-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: var(--color-primary);
  color: var(--color-text-white);
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
  color: var(--color-text);
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
@media (max-width: 768px) {
  .section-title {
    font-size: 16px;
    margin-bottom: 12px;
    padding-bottom: 8px;
    font-weight: 600;
    border-bottom-width: 2px;
    padding-left: 0;
  }
  .comment-section {
    padding: 0 8px;
    border-radius: 0;
    box-shadow: none;
    background: transparent;
    margin-top: 12px;
    max-width: 100vw;
    overflow-x: hidden;
  }
  .comment-section-inner {
    background: var(--color-bg-white);
    padding: 16px;
    border-radius: var(--radius-lg);
    box-shadow: 0 1px 4px var(--color-card-shadow);
  }
  .comment-form textarea {
    width: 100%;
    padding: 10px 12px;
    font-size: 14px;
    border-radius: 8px;
    border: 1px solid var(--color-border);
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
    background: var(--color-bg-secondary);
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
    border-bottom: 1px solid var(--color-bg);
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
}
@media (max-width: 480px) {
  .comment-section {
    padding: 0 10px;
  }
}
</style>
