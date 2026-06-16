import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { userApi } from '../api'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token'))
  const userId = ref(localStorage.getItem('userId'))
  const username = ref(localStorage.getItem('username'))
  const profile = ref(null)

  const isLoggedIn = computed(() => !!token.value)

  function setAuth(data) {
    token.value = data.token
    userId.value = data.userId
    username.value = data.username
    localStorage.setItem('token', data.token)
    localStorage.setItem('userId', data.userId)
    localStorage.setItem('username', data.username)
  }

  function clearAuth() {
    token.value = null
    userId.value = null
    username.value = null
    profile.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('userId')
    localStorage.removeItem('username')
  }

  async function login(credentials) {
    const res = await userApi.login(credentials)
    if (res.data.success && res.data.data.token) {
      setAuth(res.data.data)
    }
    return res
  }

  async function register(data) {
    const res = await userApi.register(data)
    if (res.data.success && res.data.data.token) {
      setAuth(res.data.data)
    }
    return res
  }

  function logout() {
    userApi.logout()
    clearAuth()
  }

  async function fetchProfile() {
    try {
      const res = await userApi.getProfile()
      if (res.data.success) {
        profile.value = res.data.data
      }
    } catch (err) {
      console.error('Failed to fetch profile:', err)
    }
  }

  return {
    token,
    userId,
    username,
    profile,
    isLoggedIn,
    setAuth,
    clearAuth,
    login,
    register,
    logout,
    fetchProfile
  }
})
