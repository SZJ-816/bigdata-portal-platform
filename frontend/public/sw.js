const SW_VERSION = 'v1'
const CACHE_STATIC = `static-${SW_VERSION}`
const CACHE_API = `api-${SW_VERSION}`
const CACHE_IMAGE = `image-${SW_VERSION}`
const CACHE_HTML = `html-${SW_VERSION}`

const PRE_CACHE_URLS = [
  '/favicon.svg',
  '/images/channel-ai.svg',
  '/images/channel-bigdata.svg',
  '/images/channel-cloud.svg',
  '/images/channel-internet.svg',
  '/images/channel-hardware.svg',
  '/images/channel-startup.svg'
]

const MAX_API_ENTRIES = 100
const MAX_IMAGE_ENTRIES = 200

self.addEventListener('install', (event) => {
  event.waitUntil(
    caches.open(CACHE_STATIC).then((cache) => cache.addAll(PRE_CACHE_URLS))
  )
  self.skipWaiting()
})

self.addEventListener('activate', (event) => {
  const currentCaches = [CACHE_STATIC, CACHE_API, CACHE_IMAGE, CACHE_HTML]
  event.waitUntil(
    caches.keys().then((keys) =>
      Promise.all(
        keys
          .filter((key) => !currentCaches.includes(key))
          .map((key) => caches.delete(key))
      )
    )
  )
  self.clients.claim()
})

self.addEventListener('fetch', (event) => {
  const { request } = event
  const url = new URL(request.url)

  if (request.method !== 'GET') return

  if (url.pathname.includes('/stream')) return

  if (
    url.pathname.startsWith('/assets/') ||
    url.pathname.match(/\.(svg|ico|woff2?)$/i)
  ) {
    event.respondWith(cacheFirst(request, CACHE_STATIC))
    return
  }

  if (url.pathname.startsWith('/images/')) {
    event.respondWith(cacheFirst(request, CACHE_STATIC))
    return
  }

  if (url.pathname === '/favicon.svg') {
    event.respondWith(cacheFirst(request, CACHE_STATIC))
    return
  }

  if (url.pathname.startsWith('/api/image/proxy')) {
    event.respondWith(cacheFirstWithLimit(request, CACHE_IMAGE, MAX_IMAGE_ENTRIES))
    return
  }

  if (url.pathname.startsWith('/api/')) {
    event.respondWith(staleWhileRevalidate(request, CACHE_API, MAX_API_ENTRIES))
    return
  }

  if (request.headers.get('accept')?.includes('text/html')) {
    event.respondWith(staleWhileRevalidate(request, CACHE_HTML, 10))
    return
  }
})

async function cacheFirst(request, cacheName) {
  const cached = await caches.match(request)
  if (cached) return cached
  try {
    const response = await fetch(request)
    if (response.ok) {
      const cache = await caches.open(cacheName)
      cache.put(request, response.clone())
    }
    return response
  } catch {
    return new Response('', { status: 503, statusText: 'Offline' })
  }
}

async function cacheFirstWithLimit(request, cacheName, maxEntries) {
  const cached = await caches.match(request)
  if (cached) return cached
  try {
    const response = await fetch(request)
    if (response.ok) {
      const cache = await caches.open(cacheName)
      cache.put(request, response.clone())
      trimCache(cacheName, maxEntries)
    }
    return response
  } catch {
    return new Response('', { status: 503, statusText: 'Offline' })
  }
}

async function staleWhileRevalidate(request, cacheName, maxEntries) {
  const cache = await caches.open(cacheName)
  const cached = await cache.match(request)
  const fetchPromise = fetch(request)
    .then((response) => {
      if (response.ok) {
        cache.put(request, response.clone())
        trimCache(cacheName, maxEntries)
      }
      return response
    })
    .catch(() => cached || new Response('{"success":false}', {
      status: 503,
      headers: { 'Content-Type': 'application/json' }
    }))
  return cached || fetchPromise
}

function trimCache(cacheName, maxEntries) {
  caches.open(cacheName).then((cache) => {
    cache.keys().then((keys) => {
      if (keys.length > maxEntries) {
        cache.delete(keys[0])
      }
    })
  })
}
