export default {
  mounted(el, binding) {
    const src = binding.value
    if (!src) return

    el._lazyObserver = null
    el._lazyRetryCount = 0
    const MAX_RETRY = 2

    const options = {
      rootMargin: '200px 0px',
      threshold: 0.01
    }

    const observer = new IntersectionObserver((entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          loadImage(el, src)
          observer.unobserve(el)
        }
      })
    }, options)

    el._lazyObserver = observer

    if (el.tagName === 'IMG') {
      el.style.backgroundColor = '#f0f0f0'
      el.style.opacity = '0'
      el.style.transition = 'opacity 0.3s ease-in-out'
    } else {
      el.style.backgroundColor = '#f0f0f0'
    }
    observer.observe(el)
  },
  updated(el, binding) {
    if (binding.value !== binding.oldValue && binding.value) {
      loadImage(el, binding.value)
    }
  },
  unmounted(el) {
    if (el._lazyObserver) {
      el._lazyObserver.disconnect()
      el._lazyObserver = null
    }
  }
}

function loadImage(el, src) {
  const MAX_RETRY = 2
  if (el.tagName === 'IMG') {
    const img = new Image()
    img.onload = () => {
      el.src = src
      el.style.opacity = '1'
      el.classList.add('loaded')
    }
    img.onerror = () => {
      if (el._lazyRetryCount < MAX_RETRY) {
        el._lazyRetryCount++
        setTimeout(() => {
          img.src = src + (src.includes('?') ? '&' : '?') + '_retry=' + el._lazyRetryCount
        }, 500 * el._lazyRetryCount)
      } else {
        el.src = generatePlaceholder(el.alt || '')
        el.style.opacity = '1'
        el.classList.add('loaded')
        el.classList.add('img-error')
      }
    }
    img.src = src
  } else {
    el.style.backgroundImage = `url(${src})`
    el.style.backgroundSize = 'cover'
    el.style.backgroundPosition = 'center'
    el.classList.add('loaded')
  }
}

function generatePlaceholder(text) {
  const shortText = text.length > 8 ? text.substring(0, 8) + '...' : text
  const encoded = encodeURIComponent(shortText)
  return `data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 200 150'%3E%3Crect fill='%231a2a4a' width='200' height='150'/%3E%3Ctext fill='%23fff' font-family='sans-serif' font-size='12' x='50%25' y='50%25' dominant-baseline='middle' text-anchor='middle'%3E${encoded}%3C/text%3E%3C/svg%3E`
}
