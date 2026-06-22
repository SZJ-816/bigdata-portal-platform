<template>
  <div class="empty-state">
    <div class="empty-icon">
      <slot name="icon">
        <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><path d="M8 15h8"/><path d="M9 9h.01"/><path d="M15 9h.01"/></svg>
      </slot>
    </div>
    <p class="empty-title">{{ title }}</p>
    <p v-if="hint" class="empty-hint">{{ hint }}</p>
    <slot name="action"></slot>
  </div>
</template>

<script setup>
defineProps({
  title: { type: String, default: '暂无数据' },
  hint: { type: String, default: '' }
})
</script>

<style scoped>
.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: var(--color-text-light);
}
.empty-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-text-light);
  margin-bottom: 12px;
  opacity: 0.6;
}
.empty-icon svg {
  width: 48px;
  height: 48px;
}
.empty-title {
  font-size: 15px;
  margin-bottom: 4px;
  color: var(--color-text-secondary);
}
.empty-hint {
  font-size: 13px;
  color: var(--color-text-light);
}
@media (max-width: 768px) {
  .empty-state {
    padding: 40px 20px;
  }
  .empty-icon svg {
    width: 40px;
    height: 40px;
  }
}
</style>
