<template>
  <div class="error-state">
    <div class="error-icon">
      <slot name="icon">
        <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M12 2L2 20h20L12 2z"/><path d="M12 9v4"/><circle cx="12" cy="16" r="0.5" fill="currentColor"/></svg>
      </slot>
    </div>
    <p class="error-title">{{ title }}</p>
    <slot name="action">
      <button v-if="retry" class="btn btn-primary" @click="$emit('retry')">{{ retryText }}</button>
    </slot>
  </div>
</template>

<script setup>
defineProps({
  title: { type: String, default: '加载失败' },
  retry: { type: Boolean, default: true },
  retryText: { type: String, default: '重新加载' }
})

defineEmits(['retry'])
</script>

<style scoped>
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
.error-title {
  font-size: 15px;
  margin-bottom: 16px;
}
@media (max-width: 768px) {
  .error-state {
    padding: 40px 20px;
  }
  .error-icon svg {
    width: 40px;
    height: 40px;
  }
}
</style>
