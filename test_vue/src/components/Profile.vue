<!-- eslint-disable vue/multi-word-component-names -->
<template>
  <el-card>
    <div style="display: flex;">
      <div>
        <el-upload
          class="avatar-uploader"
          :action="uploadUrl"
          :show-file-list="false"
          :on-success="handleUploadSuccess"
          :before-upload="beforeUpload"
          :headers="uploadHeaders"
          name="file"
        >
          <el-avatar :src="user.image" :size="120" style="cursor: pointer;" />
          <div style="font-size: 12px; color: #888; margin-top: 8px; text-align: center;">点击更换头像</div>
        </el-upload>
      </div>
      <el-descriptions title="个人信息" :column="2" border style="margin-left: 32px;">
        <el-descriptions-item label="工号">{{ formatEmpId(user.id) }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ user.name }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ user.gender === 1 ? '男' : '女' }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ user.phone }}</el-descriptions-item>
        <el-descriptions-item label="所属部门">{{ user.deptName }}</el-descriptions-item>
        <el-descriptions-item label="职位">{{ jobMap[user.job] || user.job }}</el-descriptions-item>
        <el-descriptions-item label="薪资">{{ user.salary }}</el-descriptions-item>
        <el-descriptions-item label="入职时间">{{ formatDate(user.entryDate) }}</el-descriptions-item>
        <el-descriptions-item label="最后操作时间">{{ formatDateTime(user.updateTime) }}</el-descriptions-item>
        <el-descriptions-item label="工作经历" :span="2">
          <div v-if="user.exprList && user.exprList.length">
            <el-timeline>
              <el-timeline-item
                v-for="expr in user.exprList"
                :key="expr.id"
                :timestamp="formatDate(expr.begin) + ' ~ ' + formatDate(expr.end)"
              >
                <div>
                  <b>{{ expr.company }}</b> - {{ expr.job }}
                </div>
              </el-timeline-item>
            </el-timeline>
          </div>
          <div v-else>暂无工作经历</div>
        </el-descriptions-item>
      </el-descriptions>
      <el-link type="primary" @click="showChangePwd = true" style="margin-top: 16px;">忘记密码？</el-link>
    </div>
  </el-card>
  <el-dialog title="修改密码" v-model="showChangePwd" width="500px">
    <el-form :model="pwdForm" label-width="120px">
      <el-form-item label="原密码">
        <el-input v-model="pwdForm.oldPassword" type="password" />
      </el-form-item>
      <el-form-item label="新密码">
        <el-input v-model="pwdForm.newPassword" type="password" />
      </el-form-item>
      <el-form-item label="确认新密码">
        <el-input v-model="pwdForm.confirmPassword" type="password" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="changePassword">修改密码</el-button>
        <el-button @click="showChangePwd = false">取消</el-button>
      </el-form-item>
    </el-form>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import axios from 'axios'
import dayjs from 'dayjs'
import { ElMessage } from 'element-plus'

const jobMap = { 1: '经理', 2: '总监', 3: '主管', 4: '组长', 5: '普通员工', 6: '其他' }

const user = reactive({
  username: '',
  name: '',
  gender: 1,
  phone: '',
  job: null,
  salary: '',
  image: '',
  entryDate: '',
  deptId: null,
  updateTime: '',
  exprList: []
})

// 上传相关
const uploadUrl = 'http://localhost:8080/emps/upload'
const uploadHeaders = {}
const handleUploadSuccess = async (res) => {
  if (res.data.startsWith('/upload/')) {
    user.image = `http://localhost:8080${res.data}`
  } else if (res.data.startsWith('http')) {
    user.image = res.data
  } else {
    user.image = `http://localhost:8080/upload/${res.data}`
  }
  // 更新用户信息
  await axios.put('/emps/profile', { image: user.image })
  ElMessage.success('头像更新成功')
}
const beforeUpload = (file) => {
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png' || file.type === 'image/jpg'
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isJPG) {
    ElMessage.error('上传头像图片只能是 JPG/PNG/JPEG 格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('上传头像图片大小不能超过 2MB!')
    return false
  }
  return true
}

const fetchProfile = async () => {
  const res = await axios.get('/emps/profile')
  if (res.data.code === 1) {
    Object.assign(user, res.data.data)
  }
}

function formatDate(val) {
  if (!val) return ''
  if (Array.isArray(val)) {
    return dayjs(new Date(val[0], val[1] - 1, val[2])).format('YYYY-MM-DD')
  }
  return dayjs(val).isValid() ? dayjs(val).format('YYYY-MM-DD') : val
}
function formatDateTime(val) {
  if (!val) return ''
  if (Array.isArray(val)) {
    return dayjs(new Date(val[0], val[1] - 1, val[2], val[3] || 0, val[4] || 0, val[5] || 0)).format('YYYY-MM-DD HH:mm:ss')
  }
  return dayjs(val).isValid() ? dayjs(val).format('YYYY-MM-DD HH:mm:ss') : val
}

function formatEmpId(id) {
  return id ? id.toString().padStart(9, '0') : ''
}

const showChangePwd = ref(false)
const pwdForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const changePassword = async () => {
  if (pwdForm.value.newPassword !== pwdForm.value.confirmPassword) {
    ElMessage.error('两次输入的新密码不一致')
    return
  }
  const res = await axios.post('/emps/change-password', pwdForm.value)
  if (res.data.code === 1) {
    ElMessage.success('密码修改成功')
    showChangePwd.value = false
    pwdForm.value.oldPassword = ''
    pwdForm.value.newPassword = ''
    pwdForm.value.confirmPassword = ''
  } else {
    ElMessage.error(res.data.msg || '修改失败')
  }
}

onMounted(async () => {
  await fetchProfile()
})
</script>

<style scoped>
.el-card {
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
  padding: 20px;
}

.avatar-uploader {
  text-align: center;
}

.avatar-uploader .el-avatar {
  border: 2px solid #fff;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,.1);
  transition: all 0.3s;
}

.avatar-uploader .el-avatar:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 16px 0 rgba(0,0,0,.2);
}
</style>
