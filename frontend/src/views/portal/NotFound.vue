<template>
  <div class="not-found-page">
    <div class="not-found-content">
      <h1>404</h1>
      <p>页面不存在</p>
      <button class="btn btn-primary" @click="router.push('/')">返回首页</button>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
const router = useRouter()
</script>

<style scoped>
.not-found-page {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 60vh;
  text-align: center;
}
.not-found-content h1 {
  font-size: 96px;
  color: var(--color-primary);
  margin-bottom: 8px;
}
.not-found-content p {
  font-size: 18px;
  color: var(--color-text-secondary);
  margin-bottom: 24px;
}
</style>
