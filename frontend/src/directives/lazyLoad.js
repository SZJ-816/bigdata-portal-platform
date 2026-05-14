export default {
  mounted(el, binding) {
    const src = binding.value
    if (!src) return

    const options = {
      rootMargin: '200px 0px',
      threshold: 0.01
    }

    const observer = new IntersectionObserver((entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          if (el.tagName === 'IMG') {
            const img = new Image()
            img.onload = () => {
              el.src = src
              el.classList.add('loaded')
            }
            img.onerror = () => {
              el.src = generatePlaceholder(el.alt || '')
              el.classList.add('loaded')
              el.classList.add('img-error')
            }
            img.src = src
          }
          observer.unobserve(el)
        }
      })
    }, options)

    if (el.tagName === 'IMG') {
      el.style.backgroundColor = '#f0f0f0'
      observer.observe(el)
    }
  },
  updated(el, binding) {
    if (binding.value !== binding.oldValue && el.tagName === 'IMG') {
      el.src = binding.value
    }
  }
}

function generatePlaceholder(text) {
  const shortText = text.length > 8 ? text.substring(0, 8) + '...' : text
  const encoded = encodeURIComponent(shortText)
  return `data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 200 150'%3E%3Crect fill='%231a2a4a' width='200' height='150'/%3E%3Ctext fill='%23fff' font-family='sans-serif' font-size='12' x='50%25' y='50%25' dominant-baseline='middle' text-anchor='middle'%3E${encoded}%3C/text%3E%3C/svg%3E`
}
