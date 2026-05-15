<template>
  <div class="login-page">
    <div class="login-card">
      <div class="login-header">
        <span class="brand-icon">◆</span>
        <h2>BigData Portal</h2>
        <p>数据管理平台登录</p>
      </div>
      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <label>用户名</label>
          <input v-model="username" type="text" placeholder="请输入用户名" required autocomplete="username" />
        </div>
        <div class="form-group">
          <label>密码</label>
          <input v-model="password" type="password" placeholder="请输入密码" required autocomplete="current-password" />
        </div>
        <div v-if="errorMsg" class="error-msg">{{ errorMsg }}</div>
        <button type="submit" class="login-btn" :disabled="loading">
          {{ loading ? '登录中...' : '登 录' }}
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const emit = defineEmits(['login'])
const username = ref('')
const password = ref('')
const loading = ref(false)
const errorMsg = ref('')

async function handleLogin() {
  if (!username.value.trim() || !password.value) {
    errorMsg.value = '请填写用户名和密码'
    return
  }
  loading.value = true
  errorMsg.value = ''
  try {
    const res = await fetch('/api/users/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username: username.value, password: password.value })
    })
    const data = await res.json()
    if (data.success && data.data && data.data.token) {
      localStorage.setItem('dashboard_token', data.data.token)
      localStorage.setItem('dashboard_user', data.data.username)
      emit('login', data.data)
    } else {
      errorMsg.value = data.error || '登录失败'
    }
  } catch (err) {
    errorMsg.value = '网络错误，请稍后重试'
  }
  loading.value = false
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #F0F4F8;
}
.login-card {
  width: 400px;
  background: #fff;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 4px 24px rgba(0,0,0,0.08);
}
.login-header {
  text-align: center;
  margin-bottom: 32px;
}
.brand-icon {
  font-size: 32px;
  color: #2F6BFF;
}
.login-header h2 {
  margin: 12px 0 4px;
  font-size: 22px;
  color: #1E293B;
}
.login-header p {
  color: #94A3B8;
  font-size: 14px;
}
.form-group {
  margin-bottom: 20px;
}
.form-group label {
  display: block;
  font-size: 13px;
  font-weight: 600;
  color: #475569;
  margin-bottom: 6px;
}
.form-group input {
  width: 100%;
  padding: 10px 14px;
  border: 1px solid #E2E8F0;
  border-radius: 8px;
  font-size: 14px;
  outline: none;
  transition: border-color 0.2s;
  box-sizing: border-box;
}
.form-group input:focus {
  border-color: #2F6BFF;
}
.error-msg {
  color: #EF4444;
  font-size: 13px;
  margin-bottom: 16px;
}
.login-btn {
  width: 100%;
  padding: 12px;
  background: #2F6BFF;
  color: #fff;
  border: none;
  border-radius: 8px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s;
}
.login-btn:hover {
  background: #1E5BD8;
}
.login-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>
