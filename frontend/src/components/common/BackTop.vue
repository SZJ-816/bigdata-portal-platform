<template>
  <transition name="back-top-fade">
    <button v-if="visible" class="back-top-btn" @click="scrollToTop" aria-label="返回顶部">
      <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 19V5"/><path d="M5 12l7-7 7 7"/></svg>
    </button>
  </transition>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

const props = defineProps({
  threshold: { type: Number, default: 400 }
})

const visible = ref(false)

function onScroll() {
  visible.value = window.scrollY > props.threshold
}

function scrollToTop() {
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

onMounted(() => window.addEventListener('scroll', onScroll, { passive: true }))
onUnmounted(() => window.removeEventListener('scroll', onScroll))
</script>

<style scoped>
.back-top-btn {
  position: fixed;
  bottom: 90px;
  right: 20px;
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: var(--color-primary);
  color: var(--color-text-white);
  border: none;
  cursor: pointer;
  z-index: 999;
  box-shadow: 0 2px 8px var(--color-shadow-lg);
  transition: background 0.2s, opacity 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
}
.back-top-btn:hover {
  filter: brightness(1.1);
}
.back-top-fade-enter-active, .back-top-fade-leave-active {
  transition: opacity 0.3s;
}
.back-top-fade-enter-from, .back-top-fade-leave-to {
  opacity: 0;
}
</style>
