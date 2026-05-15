import { createRouter, createWebHistory } from 'vue-router'
import { userApi } from '../api'

const routes = [
  {
    path: '/',
    component: () => import(/* webpackChunkName: "layout" */ '../components/common/PortalLayout.vue'),
    children: [
      { path: '', name: 'Home', component: () => import('../views/portal/Home.vue') },
      { path: 'news/:id', name: 'NewsDetail', component: () => import('../views/portal/NewsDetail.vue') },
      { path: 'channel/:name', name: 'Channel', component: () => import('../views/portal/Channel.vue') },
      { path: 'search', name: 'Search', component: () => import('../views/portal/Search.vue') },
      { path: 'login', name: 'Login', component: () => import('../views/portal/Login.vue') },
      { path: 'profile', name: 'Profile', component: () => import('../views/portal/Profile.vue'), meta: { requiresAuth: true } },
    ]
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('../views/dashboard/Dashboard.vue')
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('../views/portal/NotFound.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) return savedPosition
    return { top: 0 }
  }
})

router.beforeEach((to, from, next) => {
  if (to.meta.requiresAuth && !userApi.isLoggedIn()) {
    next({
      path: '/login',
      query: { redirect: to.fullPath }
    })
  } else {
    next()
  }
})

const prefetched = new Set()
function prefetchRoute(name) {
  if (prefetched.has(name)) return
  prefetched.add(name)
  let resolved
  try {
    if (name === 'NewsDetail') {
      resolved = router.resolve({ name, params: { id: 0 } })
    } else if (name === 'Channel') {
      resolved = router.resolve({ name, params: { name: 'ai' } })
    } else {
      resolved = router.resolve({ name })
    }
    if (resolved?.matched?.length) {
      resolved.matched.forEach(record => {
        if (record.components?.default?.__asyncLoader) {
          record.components.default.__asyncLoader()
        }
      })
    }
  } catch (e) {
    console.warn('Prefetch failed for', name, e)
  }
}

router.afterEach((to) => {
  if (to.name === 'Home') {
    setTimeout(() => {
      prefetchRoute('Channel')
      prefetchRoute('Search')
    }, 1000)
  }
  if (to.name === 'Channel') {
    setTimeout(() => prefetchRoute('NewsDetail'), 500)
  }
})

export default router
