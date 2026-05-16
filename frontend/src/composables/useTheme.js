import { ref, watch, onMounted, onUnmounted } from 'vue'

const THEME_KEY = 'theme'
const THEMES = { LIGHT: 'light', DARK: 'dark', SYSTEM: 'system' }

const currentTheme = ref(localStorage.getItem(THEME_KEY) || THEMES.SYSTEM)
const systemPrefersDark = ref(false)

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

function setupMediaQueryListener() {
  if (typeof window === 'undefined') return
  mediaQuery = window.matchMedia('(prefers-color-scheme: dark)')
  mediaQuery.addEventListener('change', (e) => {
    systemPrefersDark.value = e.matches
    if (currentTheme.value === THEMES.SYSTEM) {
      applyTheme(THEMES.SYSTEM)
    }
  })
}

export function useTheme() {
  onMounted(() => {
    initTheme()
    setupMediaQueryListener()
  })

  onUnmounted(() => {
    if (mediaQuery) {
      mediaQuery.removeEventListener('change', () => {})
    }
  })

  return {
    currentTheme,
    systemPrefersDark,
    effectiveTheme: { value: getEffectiveTheme() },
    setTheme,
    toggleTheme,
    THEMES
  }
}

export { THEMES, getEffectiveTheme, setTheme, toggleTheme }
