import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import './styles/global.css'
import lazyLoadDirective from './directives/lazyLoad'
import { initTheme } from './composables/useTheme'
import { setVueRouter } from './api'

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)
app.use(router)
setVueRouter(router)
app.directive('lazy', lazyLoadDirective)

initTheme()

app.config.errorHandler = (err, instance, info) => {
  console.error('Vue error:', err, info)
}

app.mount('#app')

if (window.location.protocol !== 'file:' && 'serviceWorker' in navigator) {
  window.addEventListener('load', () => {
    navigator.serviceWorker.register('/sw.js').catch(() => {})
  })
}
