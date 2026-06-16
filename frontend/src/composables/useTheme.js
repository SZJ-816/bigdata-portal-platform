import { ref, computed, watch, onMounted, onUnmounted } from 'vue'

const THEME_KEY = 'theme'
const THEMES = { LIGHT: 'light', DARK: 'dark', SYSTEM: 'system' }

const currentTheme = ref(localStorage.getItem(THEME_KEY) || THEMES.LIGHT)
const systemPrefersDark = ref(false)

const LIGHT_VARS = {
  '--color-bg': '#f5f5f5',
  '--color-bg-white': '#ffffff',
  '--color-bg-secondary': '#f8f9fa',
  '--color-bg-tertiary': '#f0f0f0',
  '--color-bg-card': '#ffffff',
  '--color-bg-hover': '#f0f0f0',
  '--color-text': '#1a1a1a',
  '--color-text-secondary': '#555555',
  '--color-text-light': '#999999',
  '--color-text-white': '#ffffff',
  '--color-text-article': '#333333',
  '--color-text-heading': '#1a1a1a',
  '--color-border': '#e0e0e0',
  '--color-border-light': '#e9ecef',
  '--color-primary': '#1a2a4a',
  '--color-primary-light': '#e8ecf3',
  '--color-primary-hover': '#243656',
  '--color-accent': '#c41230',
  '--color-accent-light': '#fce8ec',
  '--color-danger': '#c41230',
  '--color-header-bg': '#1a2a4a',
  '--color-header-text': 'rgba(255,255,255,0.85)',
  '--color-header-text-hover': '#ffffff',
  '--color-mobile-nav-bg': '#ffffff',
  '--color-mobile-nav-border': '#e0e0e0',
  '--color-mobile-nav-text': '#999999',
  '--color-side-bg': '#f8f9fa',
  '--color-article-bg': '#ffffff',
  '--color-tag-bg': '#1a2a4a',
  '--color-tag-text': '#ffffff',
  '--color-card-shadow': 'rgba(0, 0, 0, 0.06)',
  '--color-shadow-sm': 'rgba(0, 0, 0, 0.04)',
  '--color-shadow-md': 'rgba(0, 0, 0, 0.08)',
  '--color-shadow-lg': 'rgba(0, 0, 0, 0.1)',
  '--color-purple': '#6366f1',
  '--color-purple-light': '#8b5cf6',
  '--color-purple-dark': '#4338ca',
  '--color-purple-deeper': '#4f46e5',
  '--color-purple-deepest': '#7c3aed',
  '--color-purple-bg': '#f8f7ff',
  '--color-purple-bg-end': '#f0efff',
  '--color-purple-border': '#e8e5f0',
  '--color-purple-border-active': '#8b5cf6',
  '--color-purple-shadow': 'rgba(139, 92, 246, 0.1)',
  '--color-purple-glow': 'rgba(99, 102, 241, 0.3)',
  '--color-blue': '#3b82f6',
  '--color-blue-dark': '#1976d2',
  '--color-blue-bg': '#e3f2fd',
  '--color-red': '#ff3b30',
  '--color-overlay-dark': 'rgba(0, 0, 0, 0.9)',
  '--color-overlay-medium': 'rgba(0, 0, 0, 0.85)',
  '--color-overlay-light': 'rgba(26, 42, 74, 0.92)',
  '--color-white-overlay': 'rgba(255, 255, 255, 0.3)',
  '--color-white-overlay-light': 'rgba(255, 255, 255, 0.06)',
  '--color-white-overlay-medium': 'rgba(255, 255, 255, 0.08)',
  '--color-white-overlay-strong': 'rgba(255, 255, 255, 0.1)',
  '--color-primary-overlay': 'rgba(26, 42, 74, 0.1)',
  '--color-gradient-primary': 'linear-gradient(135deg, #6366f1, #8b5cf6)',
  '--color-gradient-primary-hover': 'linear-gradient(135deg, #4f46e5, #7c3aed)',
  '--color-gradient-header': 'linear-gradient(135deg, #1a2a4a, #2d4a7a)',
  '--color-gradient-hero': 'linear-gradient(transparent, rgba(0, 0, 0, 0.9))',
  '--color-gradient-reading': 'linear-gradient(90deg, #1a2a4a, #3a7bd5)',
  '--color-ai-spinner-top': '#6366f1',
  '--color-ai-spinner-bottom': '#8b5cf6',
  '--color-scrollbar-thumb': 'rgba(0, 0, 0, 0.15)',
  '--color-scrollbar-thumb-hover': 'rgba(0, 0, 0, 0.25)',
  '--color-input-bg': '#ffffff',
  '--color-input-text': '#1a1a1a',
  '--color-input-border': '#e0e0e0',
  '--color-input-focus-border': '#1a2a4a',
  '--color-btn-primary-bg': '#1a2a4a',
  '--color-btn-primary-text': '#ffffff',
  '--color-btn-primary-hover-bg': '#243656',
  '--color-btn-outline-bg': '#ffffff',
  '--color-btn-outline-text': '#1a2a4a',
  '--color-btn-outline-border': '#1a2a4a',
  '--color-btn-outline-hover-text': '#243656',
  '--color-btn-outline-hover-border': '#243656',
  '--color-text-shadow': 'transparent',
  '--color-skeleton': '#e8e8e8',
  '--color-skeleton-shine': '#f0f0f0'
}

const DARK_VARS = {
  '--color-bg': '#0f172a',
  '--color-bg-white': '#1e293b',
  '--color-bg-secondary': '#1e293b',
  '--color-bg-tertiary': '#334155',
  '--color-bg-card': '#1e293b',
  '--color-bg-hover': '#334155',
  '--color-text': '#e2e8f0',
  '--color-text-secondary': '#94a3b8',
  '--color-text-light': '#64748b',
  '--color-text-white': '#ffffff',
  '--color-text-article': '#cbd5e1',
  '--color-text-heading': '#f1f5f9',
  '--color-border': '#334155',
  '--color-border-light': '#475569',
  '--color-primary': '#60a5fa',
  '--color-primary-light': '#1e3a5f',
  '--color-primary-hover': '#2563eb',
  '--color-accent': '#f87171',
  '--color-accent-light': '#fce8ec',
  '--color-danger': '#f87171',
  '--color-header-bg': '#0f172a',
  '--color-header-text': 'rgba(226,232,240,0.85)',
  '--color-header-text-hover': '#ffffff',
  '--color-mobile-nav-bg': '#1e293b',
  '--color-mobile-nav-border': '#334155',
  '--color-mobile-nav-text': '#64748b',
  '--color-side-bg': '#1e293b',
  '--color-article-bg': '#1e293b',
  '--color-tag-bg': '#3b82f6',
  '--color-tag-text': '#ffffff',
  '--color-card-shadow': 'rgba(0, 0, 0, 0.3)',
  '--color-shadow-sm': 'rgba(0, 0, 0, 0.2)',
  '--color-shadow-md': 'rgba(0, 0, 0, 0.3)',
  '--color-shadow-lg': 'rgba(0, 0, 0, 0.4)',
  '--color-purple': '#818cf8',
  '--color-purple-light': '#a78bfa',
  '--color-purple-dark': '#6366f1',
  '--color-purple-deeper': '#6366f1',
  '--color-purple-deepest': '#8b5cf6',
  '--color-purple-bg': '#1e1b4b',
  '--color-purple-bg-end': '#1e1b4b',
  '--color-purple-border': '#312e81',
  '--color-purple-border-active': '#a78bfa',
  '--color-purple-shadow': 'rgba(139, 92, 246, 0.2)',
  '--color-purple-glow': 'rgba(99, 102, 241, 0.4)',
  '--color-blue': '#60a5fa',
  '--color-blue-dark': '#3b82f6',
  '--color-blue-bg': '#1e3a5f',
  '--color-red': '#f87171',
  '--color-overlay-dark': 'rgba(0, 0, 0, 0.95)',
  '--color-overlay-medium': 'rgba(0, 0, 0, 0.9)',
  '--color-overlay-light': 'rgba(15, 23, 42, 0.95)',
  '--color-white-overlay': 'rgba(255, 255, 255, 0.15)',
  '--color-white-overlay-light': 'rgba(255, 255, 255, 0.03)',
  '--color-white-overlay-medium': 'rgba(255, 255, 255, 0.05)',
  '--color-white-overlay-strong': 'rgba(255, 255, 255, 0.08)',
  '--color-primary-overlay': 'rgba(96, 165, 250, 0.15)',
  '--color-gradient-primary': 'linear-gradient(135deg, #818cf8, #a78bfa)',
  '--color-gradient-primary-hover': 'linear-gradient(135deg, #6366f1, #8b5cf6)',
  '--color-gradient-header': 'linear-gradient(135deg, #0f172a, #1e293b)',
  '--color-gradient-hero': 'linear-gradient(transparent, rgba(15, 23, 42, 0.95))',
  '--color-gradient-reading': 'linear-gradient(90deg, #60a5fa, #60a5fa)',
  '--color-ai-spinner-top': '#818cf8',
  '--color-ai-spinner-bottom': '#a78bfa',
  '--color-scrollbar-thumb': 'rgba(255, 255, 255, 0.15)',
  '--color-scrollbar-thumb-hover': 'rgba(255, 255, 255, 0.25)',
  '--color-input-bg': '#1e293b',
  '--color-input-text': '#e2e8f0',
  '--color-input-border': '#334155',
  '--color-input-focus-border': '#60a5fa',
  '--color-btn-primary-bg': '#3b82f6',
  '--color-btn-primary-text': '#ffffff',
  '--color-btn-primary-hover-bg': '#2563eb',
  '--color-btn-outline-bg': '#1e293b',
  '--color-btn-outline-text': '#94a3b8',
  '--color-btn-outline-border': '#475569',
  '--color-btn-outline-hover-text': '#e2e8f0',
  '--color-btn-outline-hover-border': '#94a3b8',
  '--color-text-shadow': 'rgba(0, 0, 0, 0.1)',
  '--color-skeleton': '#1e293b',
  '--color-skeleton-shine': '#334155'
}

function getSystemTheme() {
  if (typeof window === 'undefined') return false
  return window.matchMedia('(prefers-color-scheme: dark)').matches
}

function getEffectiveTheme() {
  if (currentTheme.value === THEMES.SYSTEM) {
    return systemPrefersDark.value ? THEMES.DARK : THEMES.LIGHT
  }
  return currentTheme.value
}

function applyTheme(theme) {
  if (typeof document === 'undefined') return
  const effective = theme === THEMES.SYSTEM ? getEffectiveTheme() : theme
  document.documentElement.setAttribute('data-theme', effective)
  document.documentElement.style.colorScheme = effective
  const vars = effective === THEMES.DARK ? DARK_VARS : LIGHT_VARS
  const root = document.documentElement.style
  for (const [key, value] of Object.entries(vars)) {
    root.setProperty(key, value)
  }
}

function setTheme(theme) {
  currentTheme.value = theme
  localStorage.setItem(THEME_KEY, theme)
  applyTheme(theme)
  window.dispatchEvent(new CustomEvent('theme-changed', { detail: { theme, effective: getEffectiveTheme() } }))
}

function toggleTheme() {
  const effective = getEffectiveTheme()
  setTheme(effective === THEMES.DARK ? THEMES.LIGHT : THEMES.DARK)
}

function initTheme() {
  systemPrefersDark.value = getSystemTheme()
  applyTheme(currentTheme.value)
}

let mediaQuery = null
let mediaQueryHandler = null

function setupMediaQueryListener() {
  if (typeof window === 'undefined') return
  mediaQuery = window.matchMedia('(prefers-color-scheme: dark)')
  mediaQueryHandler = (e) => {
    systemPrefersDark.value = e.matches
    if (currentTheme.value === THEMES.SYSTEM) {
      applyTheme(THEMES.SYSTEM)
    }
  }
  mediaQuery.addEventListener('change', mediaQueryHandler)
}

export function useTheme() {
  const effectiveTheme = computed(() => getEffectiveTheme())

  const isDark = computed(() => effectiveTheme.value === THEMES.DARK)

  onMounted(() => {
    initTheme()
    setupMediaQueryListener()
  })

  onUnmounted(() => {
    if (mediaQuery && mediaQueryHandler) {
      mediaQuery.removeEventListener('change', mediaQueryHandler)
    }
  })

  return {
    currentTheme,
    systemPrefersDark,
    effectiveTheme,
    isDark,
    setTheme,
    toggleTheme,
    THEMES
  }
}

export { THEMES, getEffectiveTheme, setTheme, toggleTheme, initTheme }
