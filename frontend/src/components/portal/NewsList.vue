<template>
  <div v-if="loading" class="loading-skeleton">
    <div class="skeleton-hero">
      <div class="skeleton-main-img"></div>
      <div class="skeleton-side">
        <div class="skeleton-side-item"></div>
        <div class="skeleton-side-item"></div>
        <div class="skeleton-side-item"></div>
      </div>
    </div>
    <div class="skeleton-list">
      <div v-for="i in 5" :key="i" class="skeleton-news-item">
        <div class="skeleton-thumb"></div>
        <div class="skeleton-body">
          <div class="skeleton-line-title"></div>
          <div class="skeleton-line-meta"></div>
        </div>
      </div>
    </div>
  </div>
  <div v-else-if="error" class="error-state">
    <div class="error-icon"><svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M12 2L2 20h20L12 2z"/><path d="M12 9v4"/><circle cx="12" cy="16" r="0.5" fill="currentColor"/></svg></div>
    <p>加载失败</p>
    <button class="btn btn-primary" @click="$emit('retry')">重新加载</button>
  </div>
  <template v-else>
    <section class="main-content">
      <div class="content-left">
        <h2 class="section-title">最新资讯</h2>
        <div class="news-list">
          <NewsCard
            v-for="item in leftNews"
            :key="item.id"
            :item="item"
            @click-news="(id) => $emit('click-news', id)"
            @click-channel="(ch) => $emit('click-channel', ch)"
          />
        </div>
      </div>

      <div class="content-center">
        <h2 class="section-title">资讯流</h2>
        <div class="news-stream">
          <div v-for="item in centerNews" :key="item.id" class="stream-item" @click="$emit('click-news', item.id)">
            <h3 class="stream-title">{{ item.title }}</h3>
            <p class="stream-summary">{{ item.summary }}</p>
            <div class="stream-meta">
              <span class="channel-tag-sm" @click.stop="$emit('click-channel', item.channel)">{{ item.channelName }}</span>
              <span>{{ formatRelativeTime(item.createdAt) }}</span>
              <span>{{ formatViewCount(item.viewCount || 0) }} 阅读</span>
            </div>
          </div>
        </div>
      </div>

      <div class="content-right">
        <slot name="sidebar"></slot>
      </div>
    </section>

    <div class="load-more-wrap">
      <button class="btn btn-outline load-more-btn" @click="$emit('load-more')">更多新闻</button>
    </div>
  </template>
</template>

<script setup>
import NewsCard from './NewsCard.vue'
import { formatRelativeTime, formatViewCount } from '../../utils'

defineProps({
  leftNews: { type: Array, default: () => [] },
  centerNews: { type: Array, default: () => [] },
  loading: { type: Boolean, default: false },
  error: { type: Boolean, default: false }
})

defineEmits(['click-news', 'click-channel', 'retry', 'load-more'])
</script>

<style scoped>
.loading-skeleton {
  margin-bottom: 24px;
}
.skeleton-hero {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
}
.skeleton-main-img {
  flex: 2;
  aspect-ratio: 16 / 9;
  max-height: 400px;
  border-radius: 4px;
  background: var(--color-skeleton);
  animation: skeleton-shine 1.5s ease-in-out infinite;
}
.skeleton-side {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.skeleton-side-item {
  flex: 1;
  border-radius: 8px;
  background: var(--color-skeleton);
  animation: skeleton-shine 1.5s ease-in-out infinite;
}
.skeleton-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.skeleton-news-item {
  display: flex;
  gap: 12px;
  padding: 12px;
  background: var(--color-bg-white);
  border-radius: 8px;
  box-shadow: 0 1px 3px var(--color-card-shadow);
}
.skeleton-thumb {
  width: 120px;
  height: 80px;
  border-radius: 4px;
  flex-shrink: 0;
  background: var(--color-skeleton);
  animation: skeleton-shine 1.5s ease-in-out infinite;
}
.skeleton-body {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
  justify-content: center;
}
.skeleton-line-title {
  height: 20px;
  width: 80%;
  border-radius: 2px;
  background: var(--color-skeleton);
  animation: skeleton-shine 1.5s ease-in-out infinite;
}
.skeleton-line-meta {
  height: 14px;
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
.error-state p {
  font-size: 16px;
  margin-bottom: 16px;
}
.main-content {
  display: grid;
  grid-template-columns: 1fr 1fr 280px;
  gap: 24px;
}
.section-title {
  font-size: 18px;
  font-weight: 600;
  margin: 0 0 16px 0;
  padding-bottom: 8px;
  border-bottom: 2px solid var(--color-blue);
}
.news-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.news-stream {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.stream-item {
  padding: 12px;
  background: var(--color-bg-secondary);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}
.stream-item:hover {
  background: var(--color-border-light);
}
.stream-title {
  font-size: 14px;
  font-weight: 500;
  margin: 0 0 6px 0;
  line-height: 1.4;
}
.stream-summary {
  font-size: 12px;
  color: var(--color-text-secondary);
  margin: 0 0 8px 0;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.stream-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 11px;
  color: var(--color-text-light);
}
.channel-tag-sm {
  background: var(--color-tag-bg);
  color: var(--color-tag-text);
  padding: 1px 6px;
  border-radius: 3px;
  font-size: 11px;
}
.load-more-wrap {
  text-align: center;
  margin-top: 32px;
  padding-bottom: 32px;
}
.load-more-btn {
  padding: 10px 32px;
}
@media (max-width: 1024px) {
  .main-content {
    grid-template-columns: 1fr 1fr;
  }
  .content-right {
    grid-column: span 2;
    max-width: 100%;
  }
  .content-right :slotted(.card) {
    max-width: 100%;
  }
}
@media (max-width: 768px) {
  .main-content {
    grid-template-columns: 1fr;
    gap: 16px;
    padding: 0 4px;
    overflow-x: hidden;
  }
  .content-left {
    padding: 0;
    overflow-x: hidden;
  }
  .content-right {
    grid-column: span 1;
    overflow-x: hidden;
  }
  .content-center {
    padding: 0;
    overflow-x: hidden;
    order: 3;
  }
  .content-right {
    order: 2;
  }
  .section-title {
    font-size: 17px;
    padding-bottom: 8px;
    margin-bottom: 14px;
    font-weight: 600;
    border-bottom-width: 3px;
  }
  .news-list {
    gap: 10px;
  }
  .news-stream {
    gap: 8px;
  }
  .stream-item {
    padding: 12px;
    border-radius: var(--radius-md);
    background: var(--color-bg-white);
    box-shadow: 0 1px 4px var(--color-card-shadow);
  }
  .stream-item:active {
    transform: scale(0.99);
  }
  .stream-title {
    font-size: 14.5px;
    font-weight: 500;
    line-height: 1.4;
  }
  .stream-summary {
    font-size: 12.5px;
    margin: 5px 0 7px 0;
    line-height: 1.5;
  }
  .stream-meta {
    font-size: 11px;
    gap: 6px;
  }
  .load-more-wrap {
    margin-top: 24px;
    padding-bottom: 24px;
  }
  .load-more-btn {
    padding: 12px 32px;
    font-size: 15px;
    border-radius: 8px;
    font-weight: 500;
  }
  .skeleton-hero {
    flex-direction: column;
    gap: 12px;
  }
  .skeleton-main-img {
    aspect-ratio: 16 / 9;
    max-height: 280px;
    flex: none;
    border-radius: var(--radius-lg);
  }
  .skeleton-side {
    flex-direction: row;
    gap: 10px;
  }
  .skeleton-side-item {
    min-width: 170px;
    border-radius: var(--radius-md);
    flex: none;
    width: 33%;
  }
  .skeleton-news-item {
    padding: 12px;
    border-radius: var(--radius-md);
    gap: 12px;
  }
  .skeleton-thumb {
    width: 85px;
    height: 65px;
    border-radius: var(--radius-sm);
  }
}
@media (max-width: 480px) {
  .skeleton-main-img {
    max-height: 190px;
  }
  .skeleton-side-item {
    min-width: 155px;
  }
  .skeleton-thumb {
    width: 75px;
    height: 55px;
  }
  .section-title {
    font-size: 15px;
  }
}
@media (max-width: 375px) {
  .skeleton-thumb {
    width: 65px;
    height: 48px;
  }
}
</style>
