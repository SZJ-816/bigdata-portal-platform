<template>
  <div class="login-page">
    <div class="login-card">
      <div class="login-header">
        <img src="/logo.jpg" alt="智讯" class="login-logo" />
        <h2>{{ isLogin ? '登录' : '注册' }}</h2>
        <p class="login-subtitle">{{ isLogin ? '登录智讯平台' : '创建新账号' }}</p>
      </div>
      <div class="login-form">
        <div class="form-group">
          <label>用户名</label>
          <input v-model="form.username" class="w-full" placeholder="请输入用户名" @keyup.enter="handleSubmit" />
        </div>
        <div class="form-group">
          <label>密码</label>
          <input v-model="form.password" type="password" class="w-full" placeholder="请输入密码" @keyup.enter="handleSubmit" />
        </div>
        <div v-if="!isLogin" class="form-group">
          <label>邮箱</label>
          <input v-model="form.email" type="email" class="w-full" placeholder="请输入邮箱" @keyup.enter="handleSubmit" />
        </div>
        <div v-if="!isLogin" class="form-group">
          <label>邮箱验证码</label>
          <div class="code-row">
            <input v-model="form.code" class="code-input" placeholder="请输入验证码" @keyup.enter="handleSubmit" />
            <button class="send-code-btn" :disabled="codeCooldown > 0 || sendingCode" @click="handleSendCode">
              <span v-if="sendingCode" class="btn-loading"></span>
              {{ codeCooldown > 0 ? `${codeCooldown}s` : '发送验证码' }}
            </button>
          </div>
        </div>
        <div v-if="errorMsg" class="error-msg">{{ errorMsg }}</div>
        <div v-if="successMsg" class="success-msg">{{ successMsg }}</div>
        <button class="btn btn-primary login-btn" :disabled="loading" @click="handleSubmit">
          {{ loading ? '处理中...' : (isLogin ? '登录' : '注册') }}
        </button>
        <p class="switch-text">
          {{ isLogin ? '没有账号？' : '已有账号？' }}
          <a href="#" @click.prevent="switchMode">{{ isLogin ? '去注册' : '去登录' }}</a>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { userApi } from '../../api'
import request from '../../api'
import { useAuthStore } from '../../stores/auth'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const isLogin = ref(true)
const form = ref({ username: '', password: '', email: '', code: '' })
const errorMsg = ref('')
const successMsg = ref('')
const loading = ref(false)
const sendingCode = ref(false)
const codeCooldown = ref(0)
let cooldownTimer = null

function switchMode() {
  isLogin.value = !isLogin.value
  errorMsg.value = ''
  successMsg.value = ''
  form.value = { username: '', password: '', email: '', code: '' }
  if (cooldownTimer) {
    clearInterval(cooldownTimer)
    cooldownTimer = null
  }
  codeCooldown.value = 0
}

async function handleSendCode() {
  errorMsg.value = ''
  successMsg.value = ''
  if (!form.value.email) {
    errorMsg.value = '请先填写邮箱'
    return
  }
  const emailRegex = /^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$/
  if (!emailRegex.test(form.value.email)) {
    errorMsg.value = '邮箱格式不正确'
    return
  }
  sendingCode.value = true
  try {
    const res = await request.post('/users/send-code', { email: form.value.email })
    if (res.data && res.data.success) {
      successMsg.value = '验证码已发送到您的邮箱'
      codeCooldown.value = 60
      cooldownTimer = setInterval(() => {
        codeCooldown.value--
        if (codeCooldown.value <= 0) {
          clearInterval(cooldownTimer)
          cooldownTimer = null
        }
      }, 1000)
    } else {
      errorMsg.value = (res.data && res.data.error) || '发送失败'
    }
  } catch (err) {
    errorMsg.value = (err.response && err.response.data && err.response.data.error) || '验证码发送失败，请稍后重试'
  } finally {
    sendingCode.value = false
  }
}

async function handleSubmit() {
  errorMsg.value = ''
  successMsg.value = ''
  if (!form.value.username || !form.value.password) {
    errorMsg.value = '请填写用户名和密码'
    return
  }
  if (!isLogin.value && !form.value.email) {
    errorMsg.value = '请填写邮箱'
    return
  }
  if (!isLogin.value && !form.value.code) {
    errorMsg.value = '请填写邮箱验证码'
    return
  }
  loading.value = true
  try {
    const api = isLogin.value ? userApi.login : userApi.register
    const res = await api(form.value)
    const data = res.data
    if (data && data.success) {
      if (data.data && data.data.token) {
        authStore.setAuth(data.data)
      }
      router.push(route.query.redirect || '/')
    } else {
      errorMsg.value = (data && data.error) || '操作失败'
    }
  } catch (err) {
    const respData = err.response && err.response.data
    errorMsg.value = isLogin.value
      ? (respData && respData.error) || '用户名或密码错误'
      : (respData && respData.error) || '注册失败，请重试'
  } finally {
    loading.value = false
  }
}

onUnmounted(() => {
  if (cooldownTimer) {
    clearInterval(cooldownTimer)
  }
})
</script>

<style scoped>
.login-page {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 60vh;
}
.login-card {
  width: 100%;
  max-width: 420px;
  background: var(--color-bg-white);
  border-radius: 4px;
  box-shadow: 0 2px 12px var(--color-shadow-md);
  overflow: hidden;
}
.login-header {
  background: var(--color-primary);
  padding: 28px 32px;
  color: var(--color-text-white);
  text-align: center;
}
.login-logo {
  width: 48px;
  height: 48px;
  border-radius: 10px;
  margin-bottom: 10px;
  object-fit: contain;
}
.login-header h2 {
  font-size: 22px;
  font-weight: 700;
  margin-bottom: 4px;
}
.login-subtitle {
  font-size: 13px;
  opacity: 0.7;
}
.login-form {
  padding: 28px 32px;
}
.form-group {
  margin-bottom: 18px;
}
.form-group label {
  display: block;
  font-size: 13px;
  color: var(--color-text-secondary);
  margin-bottom: 6px;
  font-weight: 500;
}
.form-group input {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid var(--color-border);
  border-radius: 4px;
  font-size: 14px;
  transition: border-color 0.2s;
  box-sizing: border-box;
}
.form-group input:focus {
  outline: none;
  border-color: var(--color-primary);
}
.code-row {
  display: flex;
  gap: 10px;
  align-items: center;
}
.code-input {
  flex: 1;
  padding: 10px 12px;
  border: 1px solid var(--color-border);
  border-radius: 4px;
  font-size: 14px;
  transition: border-color 0.2s;
  box-sizing: border-box;
  min-width: 0;
}
.code-input:focus {
  outline: none;
  border-color: var(--color-primary);
}
.send-code-btn {
  flex-shrink: 0;
  padding: 10px 16px;
  background: var(--color-primary);
  color: var(--color-text-white);
  border: none;
  border-radius: 4px;
  font-size: 13px;
  cursor: pointer;
  white-space: nowrap;
  transition: background 0.2s;
  min-height: 42px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
}
.send-code-btn:hover:not(:disabled) {
  background: var(--color-primary);
}
.send-code-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
.btn-loading {
  display: inline-block;
  width: 14px;
  height: 14px;
  border: 2px solid var(--color-white-overlay);
  border-top-color: var(--color-text-white);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}
@keyframes spin {
  to { transform: rotate(360deg); }
}
.error-msg {
  color: var(--color-accent);
  font-size: 13px;
  text-align: center;
  margin-bottom: 12px;
}
.success-msg {
  color: var(--color-success);
  font-size: 13px;
  text-align: center;
  margin-bottom: 12px;
}
.login-btn {
  width: 100%;
  padding: 10px;
  font-size: 15px;
  background: var(--color-primary);
  border-color: var(--color-primary);
  color: var(--color-text-white);
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background 0.2s;
}
.login-btn:hover:not(:disabled) {
  background: var(--color-primary);
}
.login-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
.switch-text {
  text-align: center;
  margin-top: 16px;
  font-size: 13px;
  color: var(--color-text-secondary);
}
.switch-text a {
  color: var(--color-text);
  font-weight: 600;
}
.w-full {
  width: 100%;
  box-sizing: border-box;
}
@media (max-width: 480px) {
  .login-page {
    align-items: flex-start;
    min-height: auto;
    padding: 0;
  }
  .login-card {
    max-width: 100%;
    border-radius: 0;
    box-shadow: none;
  }
  .login-header {
    padding: 28px 20px;
  }
  .login-header h2 {
    font-size: 22px;
  }
  .login-form {
    padding: 24px 20px;
  }
  .form-group {
    margin-bottom: 16px;
  }
  .form-group label {
    font-size: 14px;
    margin-bottom: 6px;
  }
  .form-group input {
    padding: 12px 14px;
    font-size: 16px;
    min-height: 48px;
    border-radius: 8px;
  }
  .code-row {
    gap: 8px;
  }
  .code-input {
    padding: 12px 14px;
    font-size: 16px;
    min-height: 48px;
    border-radius: 8px;
  }
  .send-code-btn {
    padding: 12px 14px;
    font-size: 13px;
    min-height: 48px;
    border-radius: 8px;
  }
  .login-btn {
    padding: 14px;
    font-size: 16px;
    min-height: 48px;
    border-radius: 8px;
  }
  .switch-text {
    margin-top: 20px;
    font-size: 14px;
  }
}
</style>
