<template>
  <div class="portal-layout">
    <header class="portal-header">
      <div class="header-inner">
        <div class="header-left">
          <h1 class="logo" @click="$router.push('/')">
            <span class="logo-mark">TECH</span>
            <span class="logo-text">科技资讯</span>
          </h1>
          <nav :class="['nav-menu', { open: menuOpen }]">
            <router-link to="/" @click="menuOpen = false">首页</router-link>
            <router-link v-for="ch in navChannels" :key="ch.name" :to="`/channel/${ch.name}`" @click="menuOpen = false">{{ ch.label }}</router-link>
            <router-link to="/search" @click="menuOpen = false">搜索</router-link>
          </nav>
        </div>
        <div class="header-right">
          <div v-if="isLoggedIn" class="user-info">
            <span class="username">{{ username }}</span>
            <router-link to="/profile" class="btn btn-outline btn-sm">个人中心</router-link>
            <button class="btn btn-outline btn-sm btn-logout" @click="handleLogout">退出</button>
          </div>
          <div v-else class="auth-btns">
            <button class="btn btn-primary btn-sm btn-login" @click="goLogin">登录</button>
          </div>
          <button class="menu-toggle" @click="menuOpen = !menuOpen">
            <span :class="['hamburger', { active: menuOpen }]">
              <span></span><span></span><span></span>
            </span>
          </button>
        </div>
      </div>
    </header>
    <main class="portal-main">
      <router-view />
    </main>
    <footer class="portal-footer">
      <div class="footer-inner">
        <p>科技资讯平台 © 2026 | 基于大数据实时分析技术构建</p>
      </div>
    </footer>
    <nav class="mobile-nav">
      <a :class="['nav-item', { active: currentPath === '/' }]" @click="$router.push('/')">
        <span class="nav-icon">🏠</span>
        <span class="nav-label">首页</span>
      </a>
      <a :class="['nav-item', { active: currentPath.startsWith('/channel') }]" @click="$router.push('/channel/AI')">
        <span class="nav-icon">📰</span>
        <span class="nav-label">频道</span>
      </a>
      <a :class="['nav-item', { active: currentPath === '/search' }]" @click="$router.push('/search')">
        <span class="nav-icon">🔍</span>
        <span class="nav-label">搜索</span>
      </a>
      <a v-if="isLoggedIn" :class="['nav-item', { active: currentPath === '/profile' }]" @click="$router.push('/profile')">
        <span class="nav-icon">👤</span>
        <span class="nav-label">我的</span>
      </a>
      <a v-else :class="['nav-item', { active: currentPath === '/login' }]" @click="goLogin">
        <span class="nav-icon">👤</span>
        <span class="nav-label">登录</span>
      </a>
    </nav>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { channels } from '../../mock/newsData'

const router = useRouter()
const route = useRoute()
const navChannels = channels.slice(0, 4)
const token = ref(localStorage.getItem('token'))
const username = ref(localStorage.getItem('username'))
const menuOpen = ref(false)

const isLoggedIn = computed(() => !!token.value)
const currentPath = computed(() => route.path)

function updateAuth() {
  token.value = localStorage.getItem('token')
  username.value = localStorage.getItem('username')
}

function goLogin() {
  router.push('/login')
}

function handleLogout() {
  localStorage.removeItem('token')
  localStorage.removeItem('userId')
  localStorage.removeItem('username')
  token.value = null
  username.value = null
  router.push('/')
}

function handleAuthUpdate() {
  updateAuth()
}

function handleStorageUpdate(e) {
  if (e.key === 'token' || e.key === 'username') {
    updateAuth()
  }
}

onMounted(() => {
  updateAuth()
  window.addEventListener('storage', handleStorageUpdate)
  window.addEventListener('auth-updated', handleAuthUpdate)
})

onUnmounted(() => {
  window.removeEventListener('storage', handleStorageUpdate)
  window.removeEventListener('auth-updated', handleAuthUpdate)
})
</script>

<style scoped>
.portal-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  padding-bottom: 0;
}
.portal-header {
  background: #1a2a4a;
  position: sticky;
  top: 0;
  z-index: 100;
}
.header-inner {
  height: var(--header-height);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 32px;
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
}
.header-left {
  display: flex;
  align-items: center;
  gap: 28px;
}
.logo {
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
  white-space: nowrap;
}
.logo-mark {
  font-size: 20px;
  font-weight: 800;
  color: #c41230;
  letter-spacing: 2px;
}
.logo-text {
  font-size: 16px;
  font-weight: 600;
  color: #ffffff;
}
.nav-menu {
  display: flex;
  gap: 20px;
}
.nav-menu a {
  color: rgba(255,255,255,0.75);
  font-size: 14px;
  padding: 4px 0;
  border-bottom: 2px solid transparent;
  transition: all 0.2s;
  text-decoration: none;
}
.nav-menu a:hover,
.nav-menu a.active {
  color: #ffffff;
  border-bottom-color: #c41230;
  text-decoration: none;
}
.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}
.username {
  font-size: 14px;
  color: rgba(255,255,255,0.85);
}
.auth-btns {
  position: relative;
  z-index: 10;
}
.btn-login {
  appearance: none;
  -webkit-appearance: none;
  min-height: 32px;
  min-width: 56px;
  pointer-events: auto;
  cursor: pointer;
}
.btn-logout {
  appearance: none;
  -webkit-appearance: none;
  cursor: pointer;
}
.btn-sm {
  padding: 6px 16px;
  font-size: 13px;
  min-height: 32px;
}
.btn-primary {
  background: #c41230;
  color: #fff;
  border-color: #c41230;
}
.btn-primary:hover {
  background: #a80e28;
  border-color: #a80e28;
  text-decoration: none;
}
.btn-outline {
  background: transparent;
  color: rgba(255,255,255,0.85);
  border-color: rgba(255,255,255,0.4);
}
.btn-outline:hover {
  color: #fff;
  border-color: #fff;
  text-decoration: none;
}
.menu-toggle {
  display: none;
  background: none;
  border: none;
  cursor: pointer;
  padding: 4px;
}
.hamburger {
  display: flex;
  flex-direction: column;
  gap: 4px;
  width: 22px;
}
.hamburger span {
  display: block;
  height: 2px;
  background: #fff;
  border-radius: 1px;
  transition: all 0.3s;
}
.hamburger.active span:nth-child(1) {
  transform: rotate(45deg) translate(4px, 4px);
}
.hamburger.active span:nth-child(2) {
  opacity: 0;
}
.hamburger.active span:nth-child(3) {
  transform: rotate(-45deg) translate(4px, -4px);
}
.portal-main {
  flex: 1;
  padding: 24px 32px;
  max-width: 1200px;
  width: 100%;
  margin: 0 auto;
}
.portal-footer {
  background: #1a2a4a;
  padding: 20px 0;
}
.footer-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 32px;
  text-align: center;
}
.footer-inner p {
  color: rgba(255,255,255,0.5);
  font-size: 13px;
}
.mobile-nav {
  display: none;
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #ffffff;
  border-top: 1px solid #e0e0e0;
  padding: 8px 0;
  z-index: 200;
  box-shadow: 0 -2px 12px rgba(0, 0, 0, 0.08);
}
.mobile-nav .nav-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  padding: 6px 0;
  color: #999;
  text-decoration: none;
  transition: all 0.2s;
}
.mobile-nav .nav-item.active {
  color: #c41230;
}
.mobile-nav .nav-icon {
  font-size: 20px;
}
.mobile-nav .nav-label {
  font-size: 11px;
}
@media (max-width: 768px) {
  .portal-layout {
    padding-bottom: 52px;
  }
  .portal-header {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    z-index: 100;
    background: rgba(26, 42, 74, 0.98);
    backdrop-filter: blur(12px);
    -webkit-backdrop-filter: blur(12px);
  }
  .header-inner {
    height: var(--header-height-mobile);
    padding: 0 12px;
    max-width: 100%;
  }
  @supports (padding-top: env(safe-area-inset-top)) {
    .header-inner {
      padding-top: env(safe-area-inset-top);
      padding-left: calc(12px + env(safe-area-inset-left));
      padding-right: calc(12px + env(safe-area-inset-right));
    }
  }
  .header-left {
    gap: 0;
  }
  .logo-mark {
    font-size: 18px;
    font-weight: 800;
  }
  .logo-text {
    font-size: 14px;
    font-weight: 600;
  }
  .nav-menu {
    display: none;
    position: fixed;
    top: var(--header-height-mobile);
    left: 0;
    right: 0;
    background: rgba(26, 42, 74, 0.99);
    backdrop-filter: blur(20px);
    -webkit-backdrop-filter: blur(20px);
    flex-direction: column;
    padding: 8px 0;
    gap: 0;
    border-top: 1px solid rgba(255, 255, 255, 0.08);
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.4);
    max-height: calc(100vh - var(--header-height-mobile) - 64px);
    overflow-y: auto;
    overscroll-behavior: contain;
  }
  .nav-menu.open {
    display: flex;
    animation: slideDown 0.2s ease-out;
  }
  @keyframes slideDown {
    from {
      opacity: 0;
      transform: translateY(-10px);
    }
    to {
      opacity: 1;
      transform: translateY(0);
    }
  }
  .nav-menu a {
    padding: 14px 20px;
    font-size: 16px;
    border-bottom: none;
    border-left: 3px solid transparent;
    border-radius: 0;
    min-height: 48px;
    display: flex;
    align-items: center;
    touch-action: manipulation;
    -webkit-tap-highlight-color: transparent;
    transition: all 0.15s ease;
  }
  .nav-menu a:hover,
  .nav-menu a.active {
    border-bottom: none;
    border-left-color: #c41230;
    background: rgba(255, 255, 255, 0.06);
  }
  .nav-menu a:active {
    transform: scale(0.98);
  }
  .menu-toggle {
    display: flex;
    min-width: 48px;
    min-height: 48px;
    align-items: center;
    justify-content: center;
    touch-action: manipulation;
    -webkit-tap-highlight-color: transparent;
    border-radius: 8px;
    transition: background 0.15s;
  }
  .menu-toggle:active {
    background: rgba(255, 255, 255, 0.1);
  }
  .hamburger {
    width: 24px;
    gap: 5px;
  }
  .hamburger span {
    height: 2.5px;
    border-radius: 2px;
  }
  .portal-main {
    padding: calc(var(--header-height-mobile) + 16px) 8px 16px;
    max-width: 100%;
  }
  @supports (padding-top: env(safe-area-inset-top)) {
    .portal-main {
      padding-top: calc(var(--header-height-mobile) + env(safe-area-inset-top) + 16px);
    }
  }
  .footer-inner {
    padding: 0 12px;
  }
  .footer-inner p {
    font-size: 11px;
    line-height: 1.5;
  }
  .username {
    display: none;
  }
  .btn-sm {
    padding: 8px 16px;
    font-size: 13px;
    min-height: 40px;
    touch-action: manipulation;
    -webkit-tap-highlight-color: transparent;
    border-radius: 8px;
    font-weight: 500;
  }
  .mobile-nav {
    display: flex;
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    background: #ffffff;
    border-top: 1px solid #e0e0e0;
    padding: 6px 0 10px;
    z-index: 200;
    box-shadow: 0 -2px 12px rgba(0, 0, 0, 0.08);
  }
  @supports (padding-bottom: env(safe-area-inset-bottom)) {
    .mobile-nav {
      padding-bottom: calc(10px + env(safe-area-inset-bottom));
    }
  }
  .mobile-nav .nav-item {
    flex: 1;
    min-height: 44px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 2px;
    padding: 4px 0;
    color: #999;
    text-decoration: none;
    transition: all 0.2s;
    touch-action: manipulation;
    -webkit-tap-highlight-color: transparent;
    border-radius: 0;
  }
  .mobile-nav .nav-item:active {
    transform: scale(0.93);
    opacity: 0.7;
  }
  .mobile-nav .nav-item.active {
    color: #c41230;
  }
  .mobile-nav .nav-icon {
    font-size: 20px;
  }
  .mobile-nav .nav-label {
    font-size: 11px;
    font-weight: 500;
  }
}
</style>
