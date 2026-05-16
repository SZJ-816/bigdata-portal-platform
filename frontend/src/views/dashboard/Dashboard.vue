<template>
  <div class="dashboard-redirect">
    <div class="redirect-card">
      <h2>数据分析仪表盘</h2>
      <p>仪表盘服务独立部署中，请访问专用地址</p>
      <a :href="dashboardUrl" class="redirect-btn" target="_blank">打开仪表盘</a>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
const dashboardUrl = ref(window.location.origin + ':3000')
</script>

<style scoped>
.dashboard-redirect {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: var(--color-bg);
}
.redirect-card {
  background: var(--color-bg-white);
  border-radius: 12px;
  padding: 48px;
  text-align: center;
  box-shadow: 0 2px 12px var(--color-shadow-md);
}
.redirect-card h2 {
  margin: 0 0 12px;
  font-size: 24px;
  color: var(--color-text);
}
.redirect-card p {
  color: var(--color-text-secondary);
  margin: 0 0 24px;
}
.redirect-btn {
  display: inline-block;
  padding: 10px 32px;
  background: var(--color-primary);
  color: var(--color-text-white);
  border-radius: 6px;
  text-decoration: none;
  font-size: 16px;
  transition: background 0.3s;
}
.redirect-btn:hover {
  background: var(--color-primary);
  opacity: 0.9;
}
</style>
