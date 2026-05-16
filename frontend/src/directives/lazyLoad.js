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

function isLocalPath(url) {
  if (!url) return true
  return url.startsWith('/') || url.startsWith('./') || url.startsWith('data:')
}

function proxyUrl(url) {
  if (isLocalPath(url)) return url
  return '/api/image/proxy?url=' + encodeURIComponent(url)
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
  rootMargin: '400px 0px',
  threshold: 0.01
})

function loadImage(el, src) {
  const MAX_RETRY = 1

  if (isLocalPath(src)) {
    if (el.tagName === 'IMG') {
      el.src = src
      el.style.opacity = '1'
      el.classList.add('loaded')
      el.onerror = () => {
        const channel = getChannelFromEl(el)
        const fallback = getChannelFallback(channel)
        if (fallback && el.src !== fallback) {
          el.src = fallback
        }
      }
    } else {
      el.style.backgroundImage = `url(${src})`
      el.style.backgroundSize = 'cover'
      el.style.backgroundPosition = 'center'
      el.classList.add('loaded')
    }
    return
  }

  const proxiedSrc = proxyUrl(src)

  if (el.tagName === 'IMG') {
    const img = new Image()
    img.onload = () => {
      el.src = proxiedSrc
      el.style.opacity = '1'
      el.classList.add('loaded')
    }
    img.onerror = () => {
      if (el._lazyRetryCount < MAX_RETRY) {
        el._lazyRetryCount++
        setTimeout(() => {
          const retrySrc = proxiedSrc + (proxiedSrc.includes('?') ? '&' : '?') + '_retry=' + el._lazyRetryCount
          img.src = retrySrc
        }, 500 * el._lazyRetryCount)
      } else {
        const channel = getChannelFromEl(el)
        const fallback = getChannelFallback(channel)
        el.src = fallback
        el.style.opacity = '1'
        el.classList.add('loaded')
        el.classList.add('img-error')
      }
    }
    img.src = proxiedSrc
  } else {
    el.style.backgroundImage = `url(${proxiedSrc})`
    el.style.backgroundSize = 'cover'
    el.style.backgroundPosition = 'center'
    el.classList.add('loaded')
  }
}

function forceLoad(el, src) {
  if (el._lazyLoaded) return
  el._lazyLoaded = true
  sharedObserver.unobserve(el)
  loadImage(el, src)
}

export default {
  mounted(el, binding) {
    const src = binding.value
    if (!src) {
      const channel = getChannelFromEl(el)
      const fallback = getChannelFallback(channel)
      if (el.tagName === 'IMG') {
        el.src = fallback
      } else {
        el.style.backgroundImage = `url(${fallback})`
        el.style.backgroundSize = 'cover'
        el.style.backgroundPosition = 'center'
      }
      return
    }

    el._lazySrc = src
    el._lazyRetryCount = 0
    el._lazyLoaded = false

    if (el.tagName === 'IMG') {
      el.style.backgroundColor = 'var(--color-skeleton)'
      el.style.opacity = '0'
      el.style.transition = 'opacity 0.3s ease-in-out'
    } else {
      el.style.backgroundColor = 'var(--color-skeleton)'
    }

    sharedObserver.observe(el)

    requestAnimationFrame(() => {
      setTimeout(() => {
        if (el._lazyLoaded) return
        const rect = el.getBoundingClientRect()
        const vh = window.innerHeight || document.documentElement.clientHeight
        if (rect.top < vh + 100 && rect.bottom > -100) {
          forceLoad(el, src)
        }
      }, 100)
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
        loadImage(el, binding.value)
      } else {
        const channel = getChannelFromEl(el)
        const fallback = getChannelFallback(channel)
        if (el.tagName === 'IMG') {
          el.src = fallback
        }
      }
    }
  },
  unmounted(el) {
    sharedObserver.unobserve(el)
  }
}
