<template>
  <el-card style="width: 800px; margin: 100px auto;">
    <el-form :model="form" label-width="120px">
      <el-form-item label="原密码">
        <el-input v-model="form.oldPassword" type="password" style="width: 400px;" />
      </el-form-item>
      <el-form-item label="新密码">
        <el-input v-model="form.newPassword" type="password" style="width: 400px;" />
      </el-form-item>
      <el-form-item label="确认新密码">
        <el-input v-model="form.confirmPassword" type="password" style="width: 400px;" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="changePassword">修改密码</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>
<script setup>
import { ref } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const form = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const changePassword = async () => {
  if (form.value.newPassword !== form.value.confirmPassword) {
    ElMessage.error('两次输入的新密码不一致')
    return
  }
  const res = await axios.post('/emps/change-password', form.value)
  if (res.data.code === 1) {
    ElMessage.success('密码修改成功')
  } else {
    ElMessage.error(res.data.msg || '修改失败')
  }
}
</script>