<template>
  <Login v-if="!isLoggedIn" @login="onLogin" />
  <Dashboard v-else />
</template>

<script setup>
import { ref, onMounted } from 'vue'
import Login from './views/Login.vue'
import Dashboard from './views/Dashboard.vue'

const isLoggedIn = ref(false)

function onLogin() {
  isLoggedIn.value = true
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
</style>
