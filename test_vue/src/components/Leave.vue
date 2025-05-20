<!-- eslint-disable vue/multi-word-component-names -->
<template>
  <div class="leave-container">
    <el-card class="leave-card">
      <template #header>
        <div class="card-header">
          <span class="header-title">请假申请</span>
          <div class="header-desc">请填写以下信息提交请假申请</div>
        </div>
      </template>
      
      <el-form :model="form" class="leave-form" label-position="top">
        <el-form-item label="请假时间" class="form-item">
          <el-date-picker
            v-model="form.range"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            class="date-picker"
          />
        </el-form-item>
        <el-form-item label="请假原因" class="form-item">
          <el-input
            v-model="form.reason"
            type="textarea"
            :rows="4"
            placeholder="请详细说明请假原因..."
            class="reason-input"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="applyLeave" class="submit-btn">
            <el-icon><Check /></el-icon>提交申请
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="leave-history-card">
      <template #header>
        <div class="card-header">
          <span class="header-title">请假记录</span>
          <div class="header-desc">查看您的请假申请历史</div>
        </div>
      </template>

      <el-table :data="leaveList" style="width: 100%" v-loading="loading">
        <el-table-column prop="startDate" label="开始日期" width="120" />
        <el-table-column prop="endDate" label="结束日期" width="120" />
        <el-table-column prop="reason" label="请假原因" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)" effect="light">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import { Check } from '@element-plus/icons-vue'

const form = ref({ range: [], reason: '' })
const leaveList = ref([])
const loading = ref(false)

const applyLeave = async () => {
  if (!form.value.range.length) {
    ElMessage.warning('请选择请假时间')
    return
  }
  if (!form.value.reason.trim()) {
    ElMessage.warning('请填写请假原因')
    return
  }
  
  try {
    await axios.post('/leave/apply', {
      startDate: form.value.range[0],
      endDate: form.value.range[1],
      reason: form.value.reason
    })
    ElMessage.success('申请已提交')
    form.value.range = []
    form.value.reason = ''
    getMyLeaves()
  } catch (error) {
    ElMessage.error('提交失败，请重试')
  }
}

const getMyLeaves = async () => {
  loading.value = true
  try {
    const { data } = await axios.get('/leave/my')
    leaveList.value = data.data
  } finally {
    loading.value = false
  }
}

const getStatusType = (status) => {
  switch (status) {
    case 0: return 'warning'
    case 1: return 'success'
    case 2: return 'danger'
    default: return 'info'
  }
}

const getStatusText = (status) => {
  switch (status) {
    case 0: return '待审批'
    case 1: return '已通过'
    case 2: return '已拒绝'
    default: return '未知'
  }
}

onMounted(getMyLeaves)
</script>

<style scoped>
.leave-container {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.leave-card, .leave-history-card {
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

.leave-card:hover, .leave-history-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.08);
}

.card-header {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.header-title {
  font-size: 20px;
  font-weight: bold;
  color: #409EFF;
}

.header-desc {
  font-size: 14px;
  color: #909399;
}

.leave-form {
  max-width: 600px;
  margin: 0 auto;
}

.form-item {
  margin-bottom: 24px;
}

.date-picker {
  width: 100%;
}

.reason-input {
  font-size: 14px;
}

.reason-input :deep(.el-textarea__inner) {
  border-radius: 8px;
  padding: 12px;
  font-size: 14px;
  line-height: 1.6;
  transition: all 0.3s ease;
}

.reason-input :deep(.el-textarea__inner:focus) {
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

.submit-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
  font-weight: bold;
  border-radius: 22px;
  background: linear-gradient(135deg, #409EFF 0%, #36D1DC 100%);
  border: none;
  transition: all 0.3s ease;
}

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

.submit-btn :deep(.el-icon) {
  margin-right: 8px;
}

:deep(.el-table) {
  border-radius: 8px;
  overflow: hidden;
}

:deep(.el-table th) {
  background-color: #f5f7fa;
  font-weight: bold;
}

:deep(.el-tag) {
  border-radius: 4px;
  padding: 0 12px;
  height: 24px;
  line-height: 24px;
  font-weight: bold;
}
</style>
