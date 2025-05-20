import { createApp } from 'vue'
import App from './App.vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import axios from 'axios'
import EmployeeHome from './components/Home.vue'
import DepartmentManage from './components/Dept.vue'
import EmployeeManage from './components/Emp.vue'
import Login from './components/Login.vue'
import Register from './components/Register.vue'
import Profile from './components/Profile.vue'
import ChangePassword from './components/ChangePassword.vue'
import Attendance from './components/Attendance.vue'
import Leave from './components/Leave.vue'
import router from './router'
import './assets/global.css'

const routes = [
  { path: '/home', component: EmployeeHome },
  { path: '/dept', component: DepartmentManage },
  { path: '/emp', component: EmployeeManage },
  { path: '/attendance', component: Attendance },
  { path: '/login', component: Login },
  { path: '/register', component: Register },
  { path: '/profile', component: Profile },
  { path: '/change-password', component: ChangePassword },
  { path: '/leave', component: Leave },
  { path: '/', redirect: '/login' }
]

const app = createApp(App)
app.use(ElementPlus)
// 注册所有图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}
app.use(router)

axios.defaults.baseURL = 'http://localhost:8080'

axios.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers['token'] = token
  }
  return config
})

axios.interceptors.response.use(
  res => res,
  err => {
    if (err.response && err.response.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      router.push('/login')
    }
    return Promise.reject(err)
  }
)

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.path !== '/login' && to.path !== '/register' && !token) {
    next('/login')
  } else if ((to.path === '/login' || to.path === '/register') && token) {
    next('/home')
  } else {
    next()
  }
})

app.mount('#app')
