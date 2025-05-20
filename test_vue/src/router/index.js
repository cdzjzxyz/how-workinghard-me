import { createRouter, createWebHistory } from 'vue-router'
import EmployeeHome from '../components/Home.vue'
import DepartmentManage from '../components/Dept.vue'
import EmployeeManage from '../components/Emp.vue'
import Login from '../components/Login.vue'
import Profile from '../components/Profile.vue'
import ChangePassword from '../components/ChangePassword.vue'
import Leave from '../components/Leave.vue'
import EmpVoice from '../components/EmpVoice.vue'
import Announcement from '../components/Announcement.vue'
import NotificationList from '../components/NotificationList.vue'

const routes = [
  { path: '/home', component: EmployeeHome },
  { path: '/dept', component: DepartmentManage, meta: { requiresAuth: true } },
  { path: '/emp', component: EmployeeManage, meta: { requiresAuth: true } },
  { path: '/login', component: Login },
  { path: '/attendance', component: () => import('@/components/Attendance.vue'), meta: { requiresAuth: true } },
  { path: '/profile', component: Profile },
  // { path: '/change-password', component: () => import('@/components/ChangePassword.vue') }, // 已集成到个人信息页面
  { path: '/leave-approval', component: () => import('@/components/LeaveApproval.vue') },
  { path: '/leave', component: Leave },
  { path: '/voice', component: EmpVoice },
  { path: '/announcement', component: Announcement, meta: { requiresAuth: true, requiresAdmin: true } },
  { path: '/announcement/update', component: Announcement, meta: { requiresAuth: true, requiresAdmin: true } },
  { path: '/notification', component: NotificationList, meta: { requiresAuth: true } },
  { path: '/', redirect: '/home' }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const user = localStorage.getItem('user') ? JSON.parse(localStorage.getItem('user')) : {}
  const isAdmin = user && user.username === 'admin'

  if (!token && to.path !== '/login' && to.path !== '/register') {
    // 未登录，强制跳转登录页
    next('/login')
  } else if (token && (to.path === '/login' || to.path === '/register')) {
    // 已登录，不能访问登录/注册页
    next('/')
  } else if (to.meta.requiresAdmin && !isAdmin) {
    // 需要管理员权限但当前用户不是管理员
    next('/home')
  } else {
    next()
  }
})

export default router
