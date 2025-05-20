<!-- eslint-disable vue/multi-word-component-names -->
<template>
  <div class="login-container">
    <div class="login-left">
      <img src="@/assets/login-bg.png" alt="illustration" class="login-img" />
      <div class="login-left-content">
        <h2 class="welcome-text">欢迎使用</h2>
        <h1 class="system-name">我爱上班</h1>
        <p class="system-desc">高效、便捷的员工管理系统</p>
      </div>
    </div>
    <div class="login-right">
      <div class="login-box">
        <div class="login-header">
          <div class="login-title">员工管理系统</div>
          <div class="login-subtitle">Employee Management System</div>
        </div>
        <el-form :model="form" :rules="rules" ref="formRef" class="login-form">
          <el-form-item prop="username">
            <el-input 
              v-model="form.username" 
              placeholder="请输入员工用户名"
              :prefix-icon="User"
              size="large"
            />
          </el-form-item>
          <el-form-item prop="password">
            <el-input 
              v-model="form.password" 
              type="password" 
              placeholder="请输入密码" 
              show-password
              :prefix-icon="Lock"
              size="large"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="onLogin" class="login-btn" size="large">登录</el-button>
            <el-button @click="onReset" class="reset-btn" size="large">重置</el-button>
            <div class="register-link">
              <span>还没有账号？</span>
              <el-button type="text" @click="goRegister">立即注册</el-button>
            </div>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import axios from 'axios'

const router = useRouter()
const form = ref({
  username: '',
  password: ''
})
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}
const formRef = ref(null)

const onLogin = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      const res = await axios.post('/login', form.value)
      if (res.data.code === 1) {
        // 登录成功，保存token
        localStorage.setItem('token', res.data.data.token)
        localStorage.setItem('user', JSON.stringify(res.data.data))
        ElMessage.success('登录成功')
        router.push('/home')
      } else {
        ElMessage.error(res.data.msg || '用户名或密码错误')
      }
    } catch (e) {
      ElMessage.error('登录失败，请稍后重试')
    }
  })
}

const onReset = () => {
  form.value.username = ''
  form.value.password = ''
}

const goRegister = () => {
  router.push('/register')
}
</script>

<style scoped>
.login-container {
  display: flex;
  height: 100vh;
  background: linear-gradient(135deg, #1a237e 0%, #0d47a1 100%);
  position: relative;
  overflow: hidden;
}

.login-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('@/assets/pattern.svg') repeat;
  opacity: 0.1;
  animation: backgroundMove 20s linear infinite;
}

@keyframes backgroundMove {
  from { background-position: 0 0; }
  to { background-position: 100% 100%; }
}

.login-left {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  position: relative;
  padding: 40px;
  color: white;
}

.login-left-content {
  text-align: center;
  margin-top: 40px;
  animation: fadeInUp 1s ease-out;
}

.welcome-text {
  font-size: 24px;
  margin-bottom: 16px;
  opacity: 0.9;
}

.system-name {
  font-size: 48px;
  font-weight: bold;
  margin-bottom: 16px;
  background: linear-gradient(45deg, #fff, #e3f2fd);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.system-desc {
  font-size: 18px;
  opacity: 0.8;
}

.login-img {
  width: 80%;
  max-width: 600px;
  animation: float 6s ease-in-out infinite;
}

@keyframes float {
  0% { transform: translateY(0px); }
  50% { transform: translateY(-20px); }
  100% { transform: translateY(0px); }
}

.login-right {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  position: relative;
}

.login-box {
  width: 480px;
  padding: 48px 40px;
  animation: fadeIn 1s ease-out;
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.login-title {
  font-size: 32px;
  font-weight: bold;
  color: #1a237e;
  margin-bottom: 8px;
  font-family: 'FZShuTi', '微软雅黑', 'Arial';
}

.login-subtitle {
  font-size: 16px;
  color: #666;
  letter-spacing: 2px;
}

.login-form {
  margin-top: 32px;
}

.login-form :deep(.el-input__wrapper) {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  border-radius: 8px;
  padding: 8px 16px;
}

.login-form :deep(.el-input__inner) {
  height: 40px;
}

.login-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
  font-weight: bold;
  background: linear-gradient(45deg, #1a237e, #0d47a1);
  border: none;
  border-radius: 8px;
  margin-bottom: 16px;
  transition: all 0.3s ease;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(26, 35, 126, 0.2);
}

.reset-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
  border-radius: 8px;
  margin-bottom: 16px;
}

.register-link {
  text-align: center;
  margin-top: 16px;
  color: #666;
}

.register-link .el-button {
  font-weight: bold;
  color: #1a237e;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(40px); }
  to { opacity: 1; transform: translateY(0); }
}

@media (max-width: 1024px) {
  .login-container {
    flex-direction: column;
  }
  
  .login-left {
    display: none;
  }
  
  .login-right {
    flex: none;
    width: 100%;
    height: 100%;
  }
  
  .login-box {
    width: 90%;
    max-width: 480px;
  }
}
</style>