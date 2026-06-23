<template>
  <div class="portal-layout">
    <header class="portal-header">
      <div class="header-inner">
        <div class="header-left">
          <h1 class="logo" @click="$router.push('/')" aria-label="智讯">
          <img src="/logo.jpg" alt="" class="logo-icon" aria-hidden="true" />
          <span class="logo-text">智讯</span>
        </h1>
          <nav :class="['nav-menu', { open: menuOpen }]" :aria-expanded="menuOpen" role="navigation" aria-label="主导航">
            <router-link to="/" @click="menuOpen = false">首页</router-link>
            <router-link v-for="ch in navChannels" :key="ch.name" :to="`/channel/${ch.name}`" class="channel-link" @click="menuOpen = false">{{ ch.label }}</router-link>
            <router-link to="/search" @click="menuOpen = false">搜索</router-link>
            <a @click="goDashboard(); menuOpen = false">数据大屏</a>
          </nav>
        </div>
        <div class="header-right">
          <button class="theme-toggle" @click="handleToggleTheme" :title="themeLabel" :class="{ animating: isAnimating }" aria-label="切换主题">
            <span class="theme-icon">
              <svg v-if="themeIcon === 'light'" xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="5"/><line x1="12" y1="1" x2="12" y2="3"/><line x1="12" y1="21" x2="12" y2="23"/><line x1="4.22" y1="4.22" x2="5.64" y2="5.64"/><line x1="18.36" y1="18.36" x2="19.78" y2="19.78"/><line x1="1" y1="12" x2="3" y2="12"/><line x1="21" y1="12" x2="23" y2="12"/><line x1="4.22" y1="19.78" x2="5.64" y2="18.36"/><line x1="18.36" y1="5.64" x2="19.78" y2="4.22"/></svg>
              <svg v-else-if="themeIcon === 'dark'" xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"/></svg>
              <svg v-else xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="2" y="3" width="20" height="14" rx="2" ry="2"/><line x1="8" y1="21" x2="16" y2="21"/><line x1="12" y1="17" x2="12" y2="21"/></svg>
            </span>
          </button>
          <div v-if="isLoggedIn" class="user-info">
            <span class="username">{{ username }}</span>
            <router-link to="/profile" class="btn btn-outline btn-sm">个人中心</router-link>
            <button class="btn btn-outline btn-sm btn-logout" @click="handleLogout">退出</button>
          </div>
          <div v-else class="auth-btns">
            <button class="btn btn-primary btn-sm btn-login" @click="goLogin">登录</button>
          </div>
          <button class="menu-toggle" @click="menuOpen = !menuOpen" :aria-label="menuOpen ? '关闭菜单' : '打开菜单'" :aria-expanded="menuOpen">
            <span :class="['hamburger', { active: menuOpen }]" aria-hidden="true">
              <span></span><span></span><span></span>
            </span>
          </button>
        </div>
      </div>
    </header>
    <main class="portal-main" id="main-content">
      <router-view />
    </main>
    <footer class="portal-footer">
      <div class="footer-inner">
        <p>智讯平台 © 2026 | 基于大数据实时分析技术构建</p>
      </div>
    </footer>
    <nav class="mobile-nav" role="navigation" aria-label="移动端导航">
      <a :class="['nav-item', { active: currentPath === '/' }]" @click="$router.push('/')">
        <span class="nav-icon"><svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M3 9.5L12 3l9 6.5V20a1 1 0 01-1 1h-5v-6H9v6H4a1 1 0 01-1-1V9.5z"/></svg></span>
        <span class="nav-label">首页</span>
      </a>
      <a :class="['nav-item', { active: currentPath === '/search' }]" @click="$router.push('/search')">
        <span class="nav-icon"><svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><circle cx="11" cy="11" r="7"/><path d="M21 21l-4.35-4.35"/></svg></span>
        <span class="nav-label">搜索</span>
      </a>
      <a :class="['nav-item']" @click="goDashboard">
        <span class="nav-icon"><svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="3" width="7" height="7" rx="1"/><rect x="14" y="3" width="7" height="7" rx="1"/><rect x="3" y="14" width="7" height="7" rx="1"/><rect x="14" y="14" width="7" height="7" rx="1"/></svg></span>
        <span class="nav-label">大屏</span>
      </a>
      <a :class="['nav-item', { active: currentPath === '/profile' }]" @click="$router.push('/profile')">
        <span class="nav-icon"><svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="8" r="4"/><path d="M4 21v-1a6 6 0 0112 0v1"/></svg></span>
        <span class="nav-label">我的</span>
      </a>
    </nav>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { channels } from '../../utils'
import { useTheme, THEMES, toggleTheme } from '../../composables/useTheme'
import { useAuthStore } from '../../stores/auth'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const navChannels = channels
const menuOpen = ref(false)
const { currentTheme, isDark } = useTheme()
const isAnimating = ref(false)

const themeIcon = computed(() => {
  if (currentTheme.value === THEMES.SYSTEM) return 'system'
  return isDark.value ? 'light' : 'dark'
})

const themeLabel = computed(() => {
  if (currentTheme.value === THEMES.SYSTEM) return '跟随系统'
  return isDark.value ? '浅色模式' : '深色模式'
})

const isLoggedIn = computed(() => authStore.isLoggedIn)
const username = computed(() => authStore.username)
const currentPath = computed(() => route.path)

function goLogin() {
  router.push({ path: '/login', query: { redirect: route.path } })
}

function goDashboard() {
  const url = window.DASHBOARD_URL || '/bigscreen/'
  // 在同一页面内导航，不打开新窗口
  window.location.href = url
}

function handleToggleTheme() {
  if (isAnimating.value) return
  isAnimating.value = true
  toggleTheme()
  setTimeout(() => { isAnimating.value = false }, 300)
}

function handleLogout() {
  authStore.logout()
  router.push('/')
}

function handleClickOutside(e) {
  if (menuOpen.value && !e.target.closest('.nav-menu') && !e.target.closest('.menu-toggle')) {
    menuOpen.value = false
  }
}

function handleEscKey(e) {
  if (e.key === 'Escape' && menuOpen.value) {
    menuOpen.value = false
  }
}

watch(menuOpen, (newVal) => {
  if (newVal) {
    document.body.style.overflow = 'hidden'
  } else {
    document.body.style.overflow = ''
  }
})

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
  document.addEventListener('keydown', handleEscKey)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
  document.removeEventListener('keydown', handleEscKey)
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
  background: var(--color-header-bg);
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
.logo-icon {
  width: 32px;
  height: 32px;
  border-radius: 6px;
  flex-shrink: 0;
  object-fit: contain;
}
.logo-text {
  font-size: 18px;
  font-weight: 700;
  color: var(--color-header-text-hover);
}
.nav-menu {
  display: flex;
  gap: 16px;
  overflow-x: auto;
  scrollbar-width: none;
}
.nav-menu::-webkit-scrollbar {
  display: none;
}
.nav-menu a {
  color: var(--color-header-text);
  font-size: 13px;
  padding: 4px 0;
  border-bottom: 2px solid transparent;
  transition: all 0.2s;
  text-decoration: none;
  white-space: nowrap;
  flex-shrink: 0;
}
.nav-menu a:hover,
.nav-menu a.active {
  color: var(--color-header-text-hover);
  border-bottom-color: var(--color-accent);
  text-decoration: none;
}
/* 桌面端显示频道链接 */
/*.channel-link {
  display: none;
}*/
.theme-toggle {
  background: transparent;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-header-text);
  transition: all 0.2s;
  padding: 6px 8px;
  border-radius: 6px;
}
.theme-toggle:hover {
  background: var(--color-white-overlay-light);
  color: var(--color-header-text-hover);
}
.theme-toggle:active {
  transform: scale(0.95);
}
.theme-toggle.animating {
  animation: themePulse 0.3s ease-out;
}
@keyframes themePulse {
  0% { transform: scale(1); }
  50% { transform: scale(1.1); }
  100% { transform: scale(1); }
}
.theme-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.2s ease;
}
.theme-toggle:hover .theme-icon {
  transform: rotate(15deg);
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
  color: var(--color-header-text);
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
  background: var(--color-accent);
  color: var(--color-text-white);
  border-color: var(--color-accent);
}
.btn-primary:hover {
  background: var(--color-accent);
  border-color: var(--color-accent);
  text-decoration: none;
}
.btn-outline {
  background: transparent;
  color: var(--color-header-text);
  border-color: var(--color-white-overlay);
}
.btn-outline:hover {
  color: var(--color-header-text-hover);
  border-color: var(--color-header-text-hover);
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
  background: var(--color-bg-white);
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
  background: var(--color-header-bg);
  padding: 20px 0;
}
.footer-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 32px;
  text-align: center;
}
.footer-inner p {
  color: var(--color-white-overlay);
  font-size: 13px;
}
.mobile-nav {
  display: none;
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: var(--color-mobile-nav-bg);
  border-top: 1px solid var(--color-mobile-nav-border);
  padding: 8px 0;
  z-index: 200;
  box-shadow: 0 -2px 12px var(--color-card-shadow);
}
.mobile-nav .nav-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  padding: 6px 0;
  color: var(--color-mobile-nav-text);
  text-decoration: none;
  transition: all 0.2s;
}
.mobile-nav .nav-item.active {
  color: var(--color-accent);
}
.mobile-nav .nav-icon {
  display: flex;
  align-items: center;
  justify-content: center;
}
.mobile-nav .nav-icon svg {
  width: 20px;
  height: 20px;
}
.mobile-nav .nav-label {
  font-size: 11px;
}
@media (max-width: 768px) {
  .portal-layout {
    padding-bottom: 48px;
  }
  .portal-header {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    z-index: 100;
    background: var(--color-overlay-light);
    backdrop-filter: blur(12px);
    -webkit-backdrop-filter: blur(12px);
  }
  .header-inner {
    height: var(--header-height-mobile);
    padding: 0 6px;
    max-width: 100%;
    gap: 4px;
  }
  @supports (padding-top: env(safe-area-inset-top)) {
    .header-inner {
      padding-top: env(safe-area-inset-top);
      padding-left: calc(8px + env(safe-area-inset-left));
      padding-right: calc(8px + env(safe-area-inset-right));
    }
  }
  .header-left {
    gap: 0;
    flex: 1;
    min-width: 0;
  }
  .logo {
    min-width: auto;
  }
  .logo-icon {
    width: 28px;
    height: 28px;
  }
  .logo-text {
    font-size: 15px;
    font-weight: 700;
  }
  .theme-toggle {
    padding: 8px;
  }
  .header-right {
    gap: 6px;
    flex-shrink: 0;
  }
  .user-info {
    gap: 8px;
  }
  .username {
    display: none;
  }
  .btn-sm {
    padding: 6px 10px;
    font-size: 12px;
    min-height: 34px;
    border-radius: 6px;
    font-weight: 500;
  }
  .btn-login {
    min-height: 34px;
    padding: 6px 12px;
  }
  .nav-menu {
    display: none;
    position: fixed;
    top: var(--header-height-mobile);
    left: 0;
    right: 0;
    background: var(--color-header-bg);
    backdrop-filter: blur(20px);
    -webkit-backdrop-filter: blur(20px);
    flex-direction: column;
    padding: 8px 0;
    gap: 0;
    border-top: 1px solid var(--color-white-overlay-medium);
    box-shadow: 0 8px 32px var(--color-shadow-lg);
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
  /* 移动端菜单展开时显示频道链接 */
  .nav-menu.open .channel-link {
    display: flex;
  }
  .nav-menu a:hover,
  .nav-menu a.active {
    border-bottom: none;
    border-left-color: var(--color-accent);
    background: var(--color-white-overlay-light);
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
    background: var(--color-white-overlay-strong);
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
    padding: calc(var(--header-height-mobile) + 16px) 12px 16px;
    max-width: 100%;
    overflow-x: hidden;
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
  .mobile-nav {
    display: flex;
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    background: var(--color-mobile-nav-bg);
    border-top: 1px solid var(--color-mobile-nav-border);
    padding: 4px 0 6px;
    z-index: 200;
    box-shadow: 0 -2px 12px var(--color-card-shadow);
  }
  @supports (padding-bottom: env(safe-area-inset-bottom)) {
    .mobile-nav {
      padding-bottom: calc(10px + env(safe-area-inset-bottom));
    }
  }
  .mobile-nav .nav-item {
    flex: 1;
    min-height: 40px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 1px;
    padding: 2px 0;
    color: var(--color-mobile-nav-text);
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
    color: var(--color-accent);
}
.mobile-nav .nav-icon {
  display: flex;
  align-items: center;
  justify-content: center;
}
.mobile-nav .nav-icon svg {
  width: 18px;
  height: 18px;
}
.mobile-nav .nav-label {
  font-size: 10px;
  font-weight: 500;
  }
}
</style>
