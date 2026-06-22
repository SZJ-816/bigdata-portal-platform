import { resolveAssetUrl } from '../utils'

const CHANNEL_ICONS = {
  'ai': '/images/channel-ai.svg',
  '人工智能': '/images/channel-ai.svg',
  'bigdata': '/images/channel-bigdata.svg',
  '大数据': '/images/channel-bigdata.svg',
  'cloud': '/images/channel-cloud.svg',
  '云计算': '/images/channel-cloud.svg',
  'internet': '/images/channel-internet.svg',
  '互联网': '/images/channel-internet.svg',
  'hardware': '/images/channel-hardware.svg',
  '硬件': '/images/channel-hardware.svg',
  'startup': '/images/channel-startup.svg',
  '创业': '/images/channel-startup.svg',
}

function getChannelFromEl(el) {
  return el.dataset?.channel || el.getAttribute('data-channel') || ''
}

function getChannelFallback(channel) {
  if (!channel) return null
  const lower = channel.toLowerCase().trim()
  for (const [key, icon] of Object.entries(CHANNEL_ICONS)) {
    if (lower.includes(key.toLowerCase()) || key.toLowerCase().includes(lower)) {
      return icon
    }
  }
  return '/images/channel-internet.svg'
}

function isFileProtocol() {
  return window.location.protocol === 'file:'
}

function getApiBase() {
  if (window.__API_BASE_URL__) return window.__API_BASE_URL__
  const saved = localStorage.getItem('apiServerUrl')
  if (saved && saved.trim()) return saved.trim()
  return '/api'
}

function resolveUrl(url) {
  if (!url) return url
  if (url.startsWith('data:')) return url

  if (url.startsWith('/api/image/proxy')) {
    if (isFileProtocol()) {
      const base = getApiBase()
      if (base.startsWith('http')) {
        return base.replace(/\/api$/, '') + url
      }
    }
    return url
  }

  if (url.startsWith('http://') || url.startsWith('https://')) {
    if (isFileProtocol()) {
      const base = getApiBase()
      const fullBase = base.startsWith('http') ? base : base.replace(/\/$/, '')
      return fullBase + '/api/image/proxy?url=' + encodeURIComponent(url)
    }
    return '/api/image/proxy?url=' + encodeURIComponent(url)
  }

  if (url.startsWith('/')) {
    if (isFileProtocol()) {
      const base = getApiBase()
      if (base.startsWith('http')) {
        return base.replace(/\/api$/, '') + url
      }
      return url.replace(/^\//, '')
    }
    return url
  }
  return url
}

const MAX_CONCURRENT = 6
let activeCount = 0
const pendingQueue = []

function processQueue() {
  while (activeCount < MAX_CONCURRENT && pendingQueue.length > 0) {
    const task = pendingQueue.shift()
    if (!task.el._lazyLoaded) {
      activeCount++
      executeLoad(task)
    }
  }
}

function executeLoad(task) {
  const { el, src } = task
  doLoadImage(el, src, () => {
    activeCount--
    processQueue()
  })
}

function enqueueLoad(el, src) {
  const priority = el._lazyPriority || 0
  const task = { el, src, priority }
  let inserted = false
  for (let i = 0; i < pendingQueue.length; i++) {
    if (priority > pendingQueue[i].priority) {
      pendingQueue.splice(i, 0, task)
      inserted = true
      break
    }
  }
  if (!inserted) {
    pendingQueue.push(task)
  }
  processQueue()
}

const sharedObserver = new IntersectionObserver((entries) => {
  entries.forEach(entry => {
    if (entry.isIntersecting) {
      const el = entry.target
      const src = el._lazySrc
      if (src && !el._lazyLoaded) {
        forceLoad(el, src)
      }
    }
  })
}, {
  rootMargin: '300px 0px',
  threshold: 0.01
})

function doLoadImage(el, src, onDone) {
  const resolvedSrc = resolveUrl(src)

  if (el.tagName === 'IMG') {
    const img = new Image()
    img.onload = () => {
      el._lazyLoaded = true
      el.src = resolvedSrc
      el.style.opacity = '1'
      el.classList.add('loaded')
      if (onDone) onDone()
    }
    img.onerror = () => {
      el._lazyLoaded = true
      applyFallback(el, src, onDone)
    }
    img.src = resolvedSrc
  } else {
    el._lazyLoaded = true
    el.style.backgroundImage = `url(${resolvedSrc})`
    el.style.backgroundSize = 'cover'
    el.style.backgroundPosition = 'center'
    el.classList.add('loaded')
    if (onDone) onDone()
  }
}

function applyFallback(el, originalSrc, onDone) {
  if (originalSrc && originalSrc.includes('/thumb/')) {
    const fullSizeSrc = originalSrc.replace('/thumb/', '/')
    if (fullSizeSrc !== originalSrc) {
      const resolvedFull = resolveUrl(fullSizeSrc)
      const img = new Image()
      img.onload = () => {
        el.src = resolvedFull
        el.style.opacity = '1'
        el.classList.add('loaded')
        if (onDone) onDone()
      }
      img.onerror = () => {
        applyChannelFallback(el, onDone)
      }
      img.src = resolvedFull
      return
    }
  }
  applyChannelFallback(el, onDone)
}

function applyChannelFallback(el, onDone) {
  const channel = getChannelFromEl(el)
  const fallback = getChannelFallback(channel)
  const fallbackResolved = resolveAssetUrl(fallback)
  if (el.tagName === 'IMG') {
    el.src = fallbackResolved
    el.style.objectFit = 'contain'
    el.style.backgroundColor = 'var(--color-skeleton, #f0f0f0)'
  } else {
    el.style.backgroundImage = `url(${fallbackResolved})`
    el.style.backgroundSize = '60% 60%'
    el.style.backgroundPosition = 'center'
    el.style.backgroundRepeat = 'no-repeat'
    el.style.backgroundColor = 'var(--color-skeleton, #f0f0f0)'
  }
  el.style.opacity = '1'
  el.classList.add('loaded')
  el.classList.add('img-error')
  if (onDone) onDone()
}

function forceLoad(el, src) {
  if (el._lazyLoaded) return
  sharedObserver.unobserve(el)
  enqueueLoad(el, src)
}

function isChannelIconUrl(src) {
  return src && src.includes('/channel-') && src.endsWith('.svg')
}

export default {
  mounted(el, binding) {
    const src = binding.value

    if (!src) {
      applyChannelFallback(el, null)
      return
    }

    if (isChannelIconUrl(src)) {
      const fallbackResolved = resolveAssetUrl(src)
      if (el.tagName === 'IMG') {
        el.src = fallbackResolved
        el.style.objectFit = 'contain'
        el.style.backgroundColor = 'var(--color-skeleton, #f0f0f0)'
      } else {
        el.style.backgroundImage = `url(${fallbackResolved})`
        el.style.backgroundSize = '60% 60%'
        el.style.backgroundPosition = 'center'
        el.style.backgroundRepeat = 'no-repeat'
        el.style.backgroundColor = 'var(--color-skeleton, #f0f0f0)'
      }
      el.style.opacity = '1'
      el.classList.add('loaded')
      return
    }

    el._lazySrc = src
    el._lazyRetryCount = 0
    el._lazyLoaded = false

    const priority = el.dataset?.priority || el.getAttribute('data-priority')
    el._lazyPriority = priority === 'high' ? 10 : (priority === 'low' ? -1 : 0)

    if (el.tagName === 'IMG') {
      el.style.backgroundColor = 'var(--color-skeleton)'
      el.style.opacity = '0'
      el.style.transition = 'opacity 0.3s ease-in-out'
      el.setAttribute('decoding', 'async')
    } else {
      el.style.backgroundColor = 'var(--color-skeleton)'
    }

    sharedObserver.observe(el)

    requestAnimationFrame(() => {
      setTimeout(() => {
        if (el._lazyLoaded) return
        const rect = el.getBoundingClientRect()
        const vh = window.innerHeight || document.documentElement.clientHeight
        if (rect.top < vh + 300 && rect.bottom > -300) {
          forceLoad(el, src)
        }
      }, 50)
    })

    setTimeout(() => {
      if (!el._lazyLoaded) {
        forceLoad(el, src)
      }
    }, 5000)
  },

  updated(el, binding) {
    if (binding.value !== binding.oldValue) {
      if (binding.value) {
        el._lazyLoaded = false
        el._lazyRetryCount = 0
        el._lazySrc = binding.value
        if (isChannelIconUrl(binding.value)) {
          const fallbackResolved = resolveAssetUrl(binding.value)
          if (el.tagName === 'IMG') {
            el.src = fallbackResolved
            el.style.opacity = '1'
          }
        } else {
          enqueueLoad(el, binding.value)
        }
      } else {
        applyChannelFallback(el, null)
      }
    }
  },

  unmounted(el) {
    sharedObserver.unobserve(el)
  }
}
