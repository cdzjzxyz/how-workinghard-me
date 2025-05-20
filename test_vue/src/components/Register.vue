<!-- eslint-disable vue/multi-word-component-names -->
<template>
  <div class="login-container">
    <div class="login-left">
      <img src="@/assets/login-bg.png" alt="illustration" class="login-img" />
    </div>
    <div class="login-right">
      <div class="login-box">
        <div class="login-title">员工注册</div>
        <el-form :model="form" :rules="rules" ref="formRef" class="login-form">
          <el-form-item label="用户名" prop="username" label-width="70px">
            <el-input v-model="form.username" placeholder="请输入用户名" />
          </el-form-item>
          <el-form-item label="姓名" prop="name" label-width="70px">
            <el-input v-model="form.name" placeholder="请输入姓名" />
          </el-form-item>
          <el-form-item label="密码" prop="password" label-width="70px">
            <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="onRegister" style="width: 120px;">注册</el-button>
            <el-button style="width: 120px; margin-left: 20px;" @click="onReset">重置</el-button>
            <el-button type="text" @click="goLogin" style="margin-left: 20px;">已有账号？去登录</el-button>
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
import axios from 'axios'

const router = useRouter()
const form = ref({
  username: '',
  name: '',
  password: ''
})
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}
const formRef = ref(null)

const onRegister = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      const res = await axios.post('/register', form.value)
      if (res.data.code === 1) {
        ElMessage.success('注册成功，请登录')
        router.push('/login')
      } else {
        ElMessage.error(res.data.msg || '注册失败')
      }
    } catch (e) {
      ElMessage.error('注册失败，请稍后重试')
    }
  })
}

const onReset = () => {
  form.value.username = ''
  form.value.name = ''
  form.value.password = ''
}

const goLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
.login-container {
  display: flex;
  height: 100vh;
  background: #fff;
}
.login-left {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}
.login-img {
  width: 80%;
  max-width: 600px;
}
.login-right {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f3feff;
}
.login-box {
  width: 480px;
  background: #eafdff;
  border-radius: 8px;
  padding: 48px 40px 32px 40px;
  box-shadow: 0 2px 16px 0 rgba(0,0,0,0.04);
}
.login-title {
  font-size: 32px;
  font-weight: bold;
  text-align: center;
  margin-bottom: 32px;
  font-family: 'FZShuTi', '微软雅黑', 'Arial';
  color: #222;
  letter-spacing: 2px;
}
.login-form {
  margin-top: 16px;
}
</style>





