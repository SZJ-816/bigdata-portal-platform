<template>
  <div class="card ai-hot-card">
    <h3 class="sidebar-title"><svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="currentColor" stroke="none" style="vertical-align: -2px; margin-right: 4px;"><path d="M12 2L9.19 8.63 2 9.24l5.46 4.73L5.82 21 12 17.27 18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2z"/></svg>AI 今日热点</h3>
    <div v-if="!hotSummary && !hotSummaryLoading" class="ai-hot-prompt">
      <input :value="hotInstruction" class="ai-hot-input" placeholder="输入指示词（可选）" @input="$emit('update:hotInstruction', $event.target.value)" @keyup.enter="$emit('fetch-hot-summary')" />
      <button class="btn btn-ai-sm" @click="$emit('fetch-hot-summary')" :disabled="hotSummaryLoading">生成热点</button>
    </div>
    <div v-if="hotSummaryLoading" class="ai-hot-loading">
      <span class="ai-spin"></span> AI 正在总结今日热点...
    </div>
    <div v-if="hotSummary" class="ai-hot-content" v-html="renderMarkdown(hotSummary)"></div>
    <div v-if="hotSummary && !hotSummaryLoading" class="ai-hot-actions">
      <input :value="hotInstruction" class="ai-hot-input-sm" placeholder="追加指示词..." @input="$emit('update:hotInstruction', $event.target.value)" @keyup.enter="$emit('fetch-hot-summary')" />
      <button class="btn btn-ai-sm" @click="$emit('fetch-hot-summary')">重新生成</button>
    </div>
  </div>
</template>

<script setup>
import { renderMarkdown } from '../../utils'

defineProps({
  hotSummary: { type: String, default: '' },
  hotSummaryLoading: { type: Boolean, default: false },
  hotInstruction: { type: String, default: '' }
})

defineEmits(['fetch-hot-summary', 'update:hotInstruction'])
</script>

<style scoped>
.card {
  background: var(--color-bg-white);
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 1px 3px var(--color-shadow-lg);
  margin-bottom: 16px;
}
.sidebar-title {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 12px 0;
}
.ai-hot-card {
  background: var(--color-bg-white);
  border: 1px solid var(--color-border);
}
.ai-hot-card .sidebar-title {
  border-bottom-color: var(--color-primary);
  color: var(--color-text);
}
.ai-hot-prompt {
  display: flex;
  gap: 6px;
  margin-bottom: 8px;
}
.ai-hot-input {
  flex: 1;
  padding: 6px 10px;
  border: 1px solid var(--color-border);
  border-radius: 4px;
  font-size: 12px;
  outline: none;
}
.ai-hot-input:focus {
  border-color: var(--color-primary);
}
.btn-ai-sm {
  padding: 6px 12px;
  background: var(--color-gradient-primary);
  color: var(--color-text-white);
  border: none;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
  white-space: nowrap;
  transition: all 0.2s;
}
.btn-ai-sm:hover:not(:disabled) {
  background: var(--color-gradient-primary-hover);
}
.btn-ai-sm:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
.ai-hot-loading {
  font-size: 13px;
  color: var(--color-purple);
  padding: 8px 0;
  display: flex;
  align-items: center;
  gap: 8px;
}
.ai-spin {
  display: inline-block;
  width: 14px;
  height: 14px;
  border: 2px solid var(--color-purple-glow);
  border-top-color: var(--color-purple);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}
@keyframes spin {
  to { transform: rotate(360deg); }
}
.ai-hot-content {
  font-size: 13px;
  line-height: 1.7;
  color: var(--color-text);
  padding: 8px 0;
}
.ai-hot-content :deep(strong) {
  color: var(--color-purple-dark);
}
.ai-hot-content :deep(p) {
  margin-bottom: 6px;
}
.ai-hot-actions {
  display: flex;
  gap: 6px;
  margin-top: 8px;
  padding-top: 8px;
  border-top: 1px solid var(--color-border);
}
.ai-hot-input-sm {
  flex: 1;
  padding: 4px 8px;
  border: 1px solid var(--color-border);
  border-radius: 4px;
  font-size: 11px;
  outline: none;
  background: var(--color-bg-white);
  color: var(--color-text);
}
.ai-hot-input-sm:focus {
  border-color: var(--color-primary);
}
@media (max-width: 768px) {
  .card {
    padding: 16px;
    border-radius: var(--radius-lg);
    margin-bottom: 14px;
    box-shadow: 0 1px 4px var(--color-card-shadow);
  }
  .sidebar-title {
    font-size: 16px;
    margin-bottom: 12px;
    font-weight: 600;
    padding-bottom: 8px;
    border-bottom-width: 2px;
  }
  .ai-hot-card {
    background: var(--color-purple-bg);
    border: 1px solid var(--color-purple-border);
  }
  .ai-hot-card .sidebar-title {
    color: var(--color-purple-dark);
    border-bottom-color: var(--color-purple-border-active);
  }
  .ai-hot-prompt {
    flex-direction: column;
    gap: 10px;
  }
  .ai-hot-input {
    padding: 10px 14px;
    font-size: 14px;
    border-radius: 8px;
    border: 1px solid var(--color-purple-border);
  }
  .ai-hot-input:focus {
    border-color: var(--color-purple-light);
    box-shadow: 0 0 0 3px var(--color-purple-shadow);
  }
  .btn-ai-sm {
    padding: 10px 20px;
    font-size: 14px;
    border-radius: 8px;
    font-weight: 500;
  }
  .ai-hot-actions {
    flex-direction: column;
    gap: 8px;
    padding-top: 12px;
  }
  .ai-hot-input-sm {
    padding: 8px 12px;
    font-size: 13px;
    border-radius: 6px;
  }
  .ai-hot-content {
    font-size: 13px;
    line-height: 1.7;
    color: var(--color-text);
    padding: 10px 0;
  }
}
@media (max-width: 480px) {
  .ai-hot-input {
    padding: 9px 12px;
    font-size: 13px;
  }
  .btn-ai-sm {
    padding: 9px 16px;
    font-size: 13px;
  }
}
</style>
