<template>
  <Login v-if="!isLoggedIn" @login="onLogin" />
  <Dashboard v-else @logout="onLogout" />
</template>

<script setup>
import { ref, onMounted } from 'vue'
import Login from './views/Login.vue'
import Dashboard from './views/Dashboard.vue'

const isLoggedIn = ref(false)

function onLogin() {
  isLoggedIn.value = true
}

function onLogout() {
  isLoggedIn.value = false
}

onMounted(() => {
  const token = localStorage.getItem('dashboard_token')
  if (token) {
    isLoggedIn.value = true
  }
})
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body {
  width: 100%;
  height: 100%;
  overflow: hidden;
  font-family: 'DM Sans', 'Noto Sans SC', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
  background: #F8FAFC;
  color: #1E293B;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

.logout-btn {
  padding: 4px 12px;
  background: transparent;
  border: 1px solid rgba(255,255,255,0.3);
  color: #94A3B8;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.2s;
}
.logout-btn:hover {
  border-color: #EF4444;
  color: #EF4444;
}
</style>
