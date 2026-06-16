import { ref, onMounted, onUnmounted } from 'vue'

export function useSSEStream(urlBuilder, options = {}) {
  const data = ref('')
  const loading = ref(false)
  const error = ref(null)
  let abortController = null

  async function start(...args) {
    data.value = ''
    loading.value = true
    error.value = null
    if (abortController) abortController.abort()
    abortController = new AbortController()

    let streamSuccess = false
    try {
      const url = urlBuilder(...args)
      const headers = {}
      const token = localStorage.getItem('token')
      if (token) headers['Authorization'] = `Bearer ${token}`

      const response = await fetch(url, {
        signal: abortController.signal,
        headers
      })
      if (!response.ok) throw new Error('Stream response not ok')

      const reader = response.body.getReader()
      const decoder = new TextDecoder()
      let buffer = ''

      while (true) {
        const { done, value } = await reader.read()
        if (done) break
        buffer += decoder.decode(value, { stream: true })
        const lines = buffer.split('\n')
        buffer = lines.pop() || ''
        for (const line of lines) {
          if (line.startsWith('data:')) {
            const chunk = line.substring(5).trim()
            if (chunk === '[DONE]') continue
            if (chunk.startsWith('[ERROR]')) {
              data.value += '\n\n[错误] ' + chunk.substring(7)
              continue
            }
            data.value += chunk
            streamSuccess = true
          }
        }
      }
      if (buffer.trim() && buffer.startsWith('data:')) {
        const chunk = buffer.substring(5).trim()
        if (chunk !== '[DONE]' && !chunk.startsWith('[ERROR]')) {
          data.value += chunk
          streamSuccess = true
        }
      }
    } catch (err) {
      if (err.name !== 'AbortError') {
        error.value = err
      }
    }

    loading.value = false
    return streamSuccess
  }

  function abort() {
    if (abortController) {
      abortController.abort()
      abortController = null
    }
  }

  onUnmounted(() => abort())

  return { data, loading, error, start, abort }
}
