<template>
  <div id="app" style="height: 100vh;">
    <!-- 侧边栏只在非登录页显示 -->
    <template v-if="showSidebar">
      <el-container class="main-layout">
        <!-- 侧边栏菜单 -->
        <el-aside width="220px" class="side-menu">
          <div class="logo-area">
            <img src="/favicon.ico" class="logo-img" />
            <span class="logo-title">我爱上班</span>
          </div>

          <el-menu :default-openeds="['2']" router class="custom-menu">
            <el-menu-item index="/home"><el-icon><House /></el-icon> 首页</el-menu-item>
            <el-sub-menu index="announcement-manage" v-if="isAdmin">
              <template #title><el-icon><Message /></el-icon> 公告管理</template>
              <el-menu-item index="/announcement"><el-icon><Document /></el-icon> 公告列表</el-menu-item>
              <el-menu-item index="/announcement/update"><el-icon><Edit /></el-icon> 更新系统公告</el-menu-item>
            </el-sub-menu>
            <el-sub-menu index="system">
              <template #title><el-icon><Setting /></el-icon> 系统信息管理</template>
              <el-menu-item index="/dept"><el-icon><OfficeBuilding /></el-icon> 部门管理</el-menu-item>
              <el-menu-item index="/emp"><el-icon><User /></el-icon> 员工管理</el-menu-item>
              <el-menu-item index="/attendance"><el-icon><Timer /></el-icon> 考勤管理</el-menu-item>
            </el-sub-menu>
            <el-sub-menu index="profile">
              <template #title><el-icon><User /></el-icon> 个人中心</template>
              <el-menu-item index="/profile"><el-icon><Postcard /></el-icon> 个人信息</el-menu-item>
              <el-menu-item v-if="!isAdmin" index="/leave"><el-icon><EditPen /></el-icon> 请假申请</el-menu-item>
              <el-menu-item v-if="isAdmin" index="/leave-approval"><el-icon><DocumentChecked /></el-icon> 请假审批</el-menu-item>
              <el-menu-item index="/notification"><el-icon><Bell /></el-icon> 我的通知</el-menu-item>
            </el-sub-menu>
            <el-menu-item index="/voice"><el-icon><ChatDotRound /></el-icon> 员工心声</el-menu-item>
          </el-menu>
        </el-aside>
        <el-container>
          <!-- 顶部栏 -->
          <el-header class="main-header">
            <div class="header-left">
              <span class="header-title">我爱上班-员工管理系统</span>
            </div>
            <div class="header-right">
               <el-badge :value="unreadNotificationCount" :hidden="unreadNotificationCount === 0" class="notification-badge">
                 <el-icon :size="24" @click="goToNotifications" style="cursor: pointer; color: #fff;"><Bell /></el-icon>
               </el-badge>
              <el-button type="primary" @click="logout" class="logout-btn" round>退出登录【{{ userName }}】</el-button>
            </div>
          </el-header>
          <!-- 右侧内容区域 -->
          <el-main class="main-content">
            <router-view />
          </el-main>
        </el-container>
      </el-container>
    </template>
    <template v-else>
      <router-view />
    </template>
  </div>
</template>

<script setup>
import { computed, ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { House, Message, Document, Edit, Setting, OfficeBuilding, User, Timer, Postcard, EditPen, DocumentChecked, ChatDotRound, Bell } from '@element-plus/icons-vue'
import axios from 'axios'

const route = useRoute()
const router = useRouter()
const showSidebar = computed(() => route.path !== '/login' && route.path !== '/register')

// 获取当前登录用户姓名
const userName = computed(() => {
  const user = localStorage.getItem('user')
  if (user) {
    try {
      return JSON.parse(user).name || ''
    } catch {
      return ''
    }
  }
  return ''
})

// 未读通知数量
const unreadNotificationCount = ref(0);

// 获取未读通知数量
const fetchUnreadCount = async () => {
    const token = localStorage.getItem('token');
    if (!token) return; // 未登录不请求
    try {
        const res = await axios.get('/notification/unreadCount', { headers: { token: token } });
        if (res.data.code === 1) {
            unreadNotificationCount.value = res.data.data;
        }
    } catch (error) {
        console.error('获取未读通知数量失败', error);
    }
};

// 导航到通知页面
const goToNotifications = () => {
  router.push('/notification');
};

// 退出登录
const logout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  router.push('/login')
}

// 获取当前登录用户完整信息
const currentUserInfo = computed(() => {
  const userStr = localStorage.getItem('user');
  if (userStr) {
    try {
      return JSON.parse(userStr);
    } catch (e) {
      console.error('解析用户信息失败', e);
      return {};
    }
  }
  return {};
});

// 职位映射（如果你的用户对象中job是数字）
const positionMap = { 1: '经理', 2: '总监', 3: '主管', 4: '组长', 5: '普通员工', 6: '其他' };

const user = localStorage.getItem('user') ? JSON.parse(localStorage.getItem('user')) : {}
const isAdmin = user && user.username === 'admin'

// 在组件挂载时获取未读通知数量
onMounted(() => {
    fetchUnreadCount();
    // 可以考虑定时刷新未读数量，或者在某些操作（如标记已读）后刷新
    // 例如：setInterval(fetchUnreadCount, 60000); // 每分钟刷新一次
});
</script>

<style>
body, html, #app {
  height: 100%;
  margin: 0;
  padding: 0;
  background: #f4f7fa;
}
.main-layout {
  height: 100vh;
  min-width: 0;
  background: linear-gradient(135deg, #e0eafc 60%, #f4f7fa 100%);
}
.side-menu {
  background: linear-gradient(135deg, #1976d2 60%, #409EFF 100%) !important;
  color: #fff !important;
  border-right: none !important;
  border-top-right-radius: 24px;
  border-bottom-right-radius: 24px;
  box-shadow: 2px 0 24px 0 rgba(25, 118, 210, 0.10);
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 0;
  position: relative;
}
.logo-area {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 32px 0 16px 0;
}
.logo-img {
  width: 38px;
  height: 38px;
  border-radius: 12px;
  margin-right: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
}
.logo-title {
  font-size: 22px;
  font-weight: bold;
  letter-spacing: 2px;
  color: #fff;
  font-family: 'FZShuTi', '微软雅黑', 'Arial';
}
.custom-menu {
  background: transparent !important;
  color: #fff !important;
  border: none;
  width: 100%;
}
.custom-menu .el-menu-item.is-active,
.custom-menu .el-menu-item:hover {
  background: rgba(255,255,255,0.18) !important;
  color: #fff !important;
  border-radius: 8px;
  font-weight: bold;
  box-shadow: 0 2px 8px rgba(64,158,255,0.10);
}
.custom-menu .el-menu-item {
  font-size: 16px;
  margin: 4px 0;
  border-radius: 8px;
  transition: background 0.2s, color 0.2s;
}
.main-header {
  min-width: 0;
  background: linear-gradient(90deg, #19aad2 0%, #42a5f5 60%, #a1c4fd 100%) !important;
  border-bottom: none !important;
  font-size: 24px;
  color: #fff;
  font-weight: bold;
  letter-spacing: 2px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  height: 64px !important;
  box-shadow: 0 4px 20px rgba(25, 118, 210, 0.10);
  position: relative;
  overflow: hidden;
}

.main-header::after {
  content: '';
  position: absolute;
  top: -30px;
  right: -60px;
  width: 180px;
  height: 120px;
  background: radial-gradient(circle, rgba(255,255,255,0.18) 0%, rgba(255,255,255,0) 80%);
  z-index: 0;
}

.header-title {
  font-family: 'FZShuTi', '微软雅黑', 'Arial';
  font-size: 28px;
  color: #1976d2;
  font-weight: bold;
  letter-spacing: 2px;
  text-shadow: 1px 1px 2px rgba(0,0,0,0.08);
  position: relative;
  z-index: 1;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-right .logout-btn {
  font-size: 16px;
  font-weight: bold;
  border-radius: 20px;
  background: linear-gradient(90deg, #fffde4 0%, #005bea 100%);
  border: none;
  color: #1976d2;
  box-shadow: 0 2px 8px rgba(25, 118, 210, 0.10);
  transition: all 0.3s ease;
  padding: 8px 20px;
}

.header-right .logout-btn:hover {
  background: linear-gradient(90deg, #ffe082 0%, #42a5f5 100%);
  color: #fff;
  transform: translateY(-1px) scale(1.04);
  box-shadow: 0 4px 16px rgba(25, 118, 210, 0.18);
}

.notification-badge :deep(.el-badge__content) {
   top: 8px;
   right: 8px;
   border: 1px solid #fff;
}

.main-content {
  flex: 1;
  background: linear-gradient(135deg, #e0eafc 0%, #cfdef3 100%);
  padding: 36px 40px 32px 40px;
  min-width: 0;
  border-top-left-radius: 32px;
  border-bottom-left-radius: 32px;
  box-shadow: -8px 0 24px -8px rgba(64,158,255,0.08) inset;
  transition: box-shadow 0.3s;
  min-height: calc(100vh - 64px);
}
@media (max-width: 900px) {
  .main-layout {
    flex-direction: column;
  }
  .side-menu {
    width: 100vw !important;
    border-radius: 0 0 24px 24px;
    min-height: 80px;
    flex-direction: row;
    justify-content: center;
    align-items: center;
    padding: 0;
  }
  .main-header {
    flex-direction: column;
    height: auto !important;
    padding: 12px 8px;
  }
  .main-content {
    padding: 16px 4vw;
    border-radius: 18px;
  }
}
</style>
