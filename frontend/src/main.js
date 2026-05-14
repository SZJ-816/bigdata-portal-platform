import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import './styles/global.css'
import lazyLoadDirective from './directives/lazyLoad'

const app = createApp(App)
app.use(router)
app.directive('lazy', lazyLoadDirective)
app.mount('#app')
