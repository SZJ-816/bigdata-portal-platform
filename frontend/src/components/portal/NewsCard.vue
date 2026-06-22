<template>
  <div class="news-item" @click="$emit('click-news', item.id)">
    <img
      v-if="item.imageUrl"
      v-lazy="item.thumbUrl || item.imageUrl"
      :alt="item.title"
      :data-channel="item.channel"
      class="thumb-img"
    />
    <div v-else class="thumb-wrap"></div>
    <div class="news-body">
      <div class="news-header">
        <h3 class="news-title">{{ item.title }}</h3>
        <span v-if="item.isBreaking" class="breaking-tag-sm">BREAKING</span>
      </div>
      <p class="news-summary">{{ item.summary }}</p>
      <div class="news-meta">
        <span class="channel-tag-sm" @click.stop="$emit('click-channel', item.channel)">{{ item.channelName }}</span>
        <span>{{ formatRelativeTime(item.createdAt) }}</span>
        <span>{{ formatViewCount(item.viewCount || 0) }} 阅读</span>
        <span>{{ item.commentCount || 0 }} 评论</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { formatRelativeTime, formatViewCount } from '../../utils'

defineProps({
  item: { type: Object, required: true }
})

defineEmits(['click-news', 'click-channel'])
</script>

<style scoped>
.thumb-img {
  width: 120px;
  height: 80px;
  position: relative;
  flex-shrink: 0;
  border-radius: 4px;
  object-fit: cover;
}
.thumb-wrap {
  width: 120px;
  height: 80px;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  background-color: var(--color-bg-secondary);
  border-radius: 4px;
  flex-shrink: 0;
}
.news-item {
  display: flex;
  gap: 12px;
  cursor: pointer;
  padding: 12px;
  border-radius: 8px;
  transition: all 0.2s;
  height: 104px;
  max-height: 104px;
  box-sizing: border-box;
  overflow: hidden;
}
.news-item:hover {
  background: var(--color-bg-secondary);
}
.news-body {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  min-width: 0;
}
.news-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}
.news-title {
  font-size: 15px;
  font-weight: 500;
  margin: 0;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.news-summary {
  font-size: 13px;
  color: var(--color-text-secondary);
  margin: 4px 0 8px 0;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.news-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 12px;
  color: var(--color-text-light);
}
.channel-tag-sm {
  background: var(--color-tag-bg);
  color: var(--color-tag-text);
  padding: 1px 6px;
  border-radius: 3px;
  font-size: 11px;
}
.breaking-tag-sm {
  background: var(--color-red);
  color: var(--color-text-white);
  padding: 1px 6px;
  border-radius: 3px;
  font-size: 11px;
}
@media (max-width: 768px) {
  .news-item {
    padding: 12px;
    background: var(--color-bg-white);
    border-radius: var(--radius-md);
    box-shadow: 0 1px 4px var(--color-card-shadow);
    gap: 12px;
    transition: box-shadow 0.15s;
    height: 104px;
    max-height: 104px;
  }
  .news-item:active {
    transform: scale(0.99);
    box-shadow: 0 2px 8px var(--color-shadow-lg);
  }
  .thumb-wrap {
    width: 85px;
    height: 65px;
    border-radius: var(--radius-sm);
    flex-shrink: 0;
  }
  .thumb-img {
    width: 85px;
    height: 65px;
    border-radius: var(--radius-sm);
    flex-shrink: 0;
  }
  .news-body {
    flex: 1;
    min-width: 0;
  }
  .news-header {
    gap: 6px;
    margin-bottom: 4px;
  }
  .news-title {
    font-size: 15px;
    font-weight: 600;
    line-height: 1.45;
    color: var(--color-text);
  }
  .news-summary {
    -webkit-line-clamp: 1;
    font-size: 13px;
    color: var(--color-text-secondary);
    margin: 4px 0 6px 0;
    line-height: 1.5;
  }
  .news-meta {
    flex-wrap: wrap;
    gap: 8px;
    font-size: 11.5px;
    color: var(--color-text-light);
  }
  .breaking-tag-sm {
    padding: 2px 6px;
    font-size: 10px;
    font-weight: 700;
  }
}
@media (max-width: 480px) {
  .news-title {
    font-size: 14px;
  }
  .news-summary {
    font-size: 12px;
  }
  .thumb-img {
    width: 75px;
    height: 55px;
  }
  .thumb-wrap {
    width: 75px;
    height: 55px;
  }
}
@media (max-width: 375px) {
  .news-item {
    padding: 10px;
    gap: 10px;
    height: 88px;
    max-height: 88px;
  }
  .news-body {
    flex: 1;
    min-width: 0;
  }
  .thumb-wrap {
    width: 65px;
    height: 48px;
  }
  .thumb-img {
    width: 65px;
    height: 48px;
  }
  .news-title {
    font-size: 13px;
    font-weight: 600;
    line-height: 1.4;
    -webkit-line-clamp: 2;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }
  .news-summary {
    -webkit-line-clamp: 1;
    font-size: 12px;
  }
}
</style>
