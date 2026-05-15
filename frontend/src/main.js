import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import './styles/global.css'
import lazyLoadDirective from './directives/lazyLoad'

const app = createApp(App)
app.use(router)
app.directive('lazy', lazyLoadDirective)

app.config.errorHandler = (err, instance, info) => {
  console.error('Vue error:', err, info)
}

app.mount('#app')

if ('serviceWorker' in navigator) {
  window.addEventListener('load', () => {
    navigator.serviceWorker.register('/sw.js').catch(() => {})
  })
}
