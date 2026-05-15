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
  background: #f0f2f5;
}
.redirect-card {
  background: #fff;
  border-radius: 12px;
  padding: 48px;
  text-align: center;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}
.redirect-card h2 {
  margin: 0 0 12px;
  font-size: 24px;
  color: #1a1a2e;
}
.redirect-card p {
  color: #666;
  margin: 0 0 24px;
}
.redirect-btn {
  display: inline-block;
  padding: 10px 32px;
  background: #1a1a2e;
  color: #fff;
  border-radius: 6px;
  text-decoration: none;
  font-size: 16px;
  transition: background 0.3s;
}
.redirect-btn:hover {
  background: #16213e;
}
</style>
