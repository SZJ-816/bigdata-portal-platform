import { defineStore } from 'pinia'
import { ref } from 'vue'
import { newsApi } from '../api'

export const useNewsStore = defineStore('news', () => {
  const allNews = ref([])
  const loading = ref(false)
  const error = ref(false)

  async function fetchNews() {
    loading.value = true
    error.value = false
    try {
      const res = await newsApi.getList()
      if (res.data.data) {
        allNews.value = res.data.data
      }
    } catch (err) {
      console.error('Failed to load news:', err)
      error.value = true
    }
    loading.value = false
  }

  function clearCache() {
    newsApi.clearCache()
    allNews.value = []
  }

  return {
    allNews,
    loading,
    error,
    fetchNews,
    clearCache
  }
})
