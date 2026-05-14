import { createRouter, createWebHistory } from 'vue-router'
import { userApi } from '../api'

const routes = [
  {
    path: '/',
    component: () => import('../components/common/PortalLayout.vue'),
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
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
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

export default router
