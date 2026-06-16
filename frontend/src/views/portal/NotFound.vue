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
@media (max-width: 768px) {
  .not-found-page {
    min-height: 50vh;
    padding: 20px;
  }
  .not-found-content h1 {
    font-size: 72px;
  }
  .not-found-content p {
    font-size: 16px;
  }
  .not-found-content .btn {
    min-height: 44px;
    padding: 10px 28px;
    font-size: 15px;
  }
}
@media (max-width: 480px) {
  .not-found-content h1 {
    font-size: 56px;
  }
  .not-found-content p {
    font-size: 14px;
    margin-bottom: 20px;
  }
}
</style>
