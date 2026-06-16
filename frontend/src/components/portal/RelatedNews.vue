<template>
  <div class="detail-sidebar">
    <div class="card related-section">
      <h3 class="sidebar-title">相关新闻</h3>
      <div class="related-list">
        <div v-for="item in relatedNews" :key="item.id" class="related-item" @click="$emit('goNews', item.id)">
          <span class="related-title">{{ item.title }}</span>
          <span class="related-meta">{{ formatRelativeTime(item.publishTime) }}</span>
        </div>
      </div>
    </div>
    <div class="card hot-section">
      <h3 class="sidebar-title">热点新闻</h3>
      <div class="hot-list">
        <div v-for="(item, idx) in hotNews" :key="item.id" class="hot-item" @click="$emit('goNews', item.id)">
          <span :class="['rank-num', { 'rank-hot': idx < 3 }]">{{ idx + 1 }}</span>
          <span class="hot-title">{{ item.title }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { formatRelativeTime } from '../../utils'

defineProps({
  relatedNews: { type: Array, default: () => [] },
  hotNews: { type: Array, default: () => [] }
})

defineEmits(['goNews'])
</script>

<style scoped>
.detail-sidebar {
  flex: 1;
  min-width: 260px;
}
.sidebar-title {
  font-size: 16px;
  font-weight: 700;
  margin-bottom: 14px;
  padding-bottom: 8px;
  border-bottom: 2px solid var(--color-primary);
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
  background: var(--color-bg-tertiary);
  color: var(--color-text-light);
}
.rank-hot {
  background: var(--color-accent);
  color: var(--color-text-white);
}
.hot-title {
  font-size: 13px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
@media (max-width: 768px) {
  .detail-sidebar {
    min-width: auto;
    padding: 0 8px 16px;
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 10px;
    max-width: 100vw;
  }
  .detail-sidebar .card {
    padding: 14px;
    border-radius: var(--radius-lg);
    box-shadow: 0 1px 4px var(--color-card-shadow);
    background: var(--color-bg-white);
    margin-bottom: 0;
    overflow: hidden;
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
    background: var(--color-bg);
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
    background: var(--color-bg-hover);
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
}
@media (max-width: 480px) {
  .detail-sidebar .card {
    min-width: 0;
    max-width: 100%;
    padding: 12px;
  }
  .detail-sidebar {
    gap: 8px;
  }
}
@media (max-width: 375px) {
  .detail-sidebar .card {
    padding: 10px;
  }
  .detail-sidebar {
    gap: 6px;
  }
  .sidebar-title {
    font-size: 13px;
  }
  .related-title {
    font-size: 12px;
  }
  .hot-title {
    font-size: 12px;
  }
}
</style>
