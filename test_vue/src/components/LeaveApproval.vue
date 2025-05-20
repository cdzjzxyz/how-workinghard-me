<!-- eslint-disable vue/multi-word-component-names -->
<template>
  <div class="approval-container">
    <el-card class="approval-card">
      <template #header>
        <div class="card-header">
          <span class="header-title">请假审批</span>
          <div class="header-desc">管理员工的请假申请</div>
        </div>
      </template>

      <div class="filter-section">
        <el-radio-group v-model="statusFilter" @change="handleFilterChange">
          <el-radio-button label="all">全部</el-radio-button>
          <el-radio-button label="pending">待审批</el-radio-button>
          <el-radio-button label="approved">已通过</el-radio-button>
          <el-radio-button label="rejected">已拒绝</el-radio-button>
        </el-radio-group>
      </div>

      <el-table 
        :data="filteredLeaveList" 
        style="width: 100%" 
        v-loading="loading"
        :header-cell-style="{ background: '#f5f7fa', color: '#606266' }"
      >
        <el-table-column prop="name" label="姓名" width="100">
          <template #default="scope">
            <div class="employee-info">
              <img 
                v-if="scope.row.image" 
                :src="scope.row.image" 
                style="width:32px;height:32px;border-radius:50%;object-fit:cover;"
                @error="handleImageError"
              />
              <el-avatar v-else :size="32">
                {{ scope.row.name?.charAt(0)?.toUpperCase() }}
              </el-avatar>
              <span>{{ scope.row.name }}</span>
            </div>
          </template>
        </el-table-column>
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
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="scope">
            <div class="action-buttons">
              <el-button 
                v-if="scope.row.status === 0"
                type="success" 
                size="small" 
                @click="approve(scope.row.id, 1)"
                class="approve-btn"
              >
                <el-icon><Check /></el-icon>同意
              </el-button>
              <el-button 
                v-if="scope.row.status === 0"
                type="danger" 
                size="small" 
                @click="approve(scope.row.id, 2)"
                class="reject-btn"
              >
                <el-icon><Close /></el-icon>拒绝
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { Check, Close } from '@element-plus/icons-vue'

const leaveList = ref([])
const loading = ref(false)
const statusFilter = ref('all')

const filteredLeaveList = computed(() => {
  if (statusFilter.value === 'all') return leaveList.value
  const statusMap = {
    pending: 0,
    approved: 1,
    rejected: 2
  }
  return leaveList.value.filter(item => item.status === statusMap[statusFilter.value])
})

const fetchLeaves = async () => {
  loading.value = true
  try {
    const res = await axios.get('/leave/all', { headers: { token: localStorage.getItem('token') } })
    if (res.data.code === 1) {
      leaveList.value = res.data.data
    }
  } finally {
    loading.value = false
  }
}

const approve = async (id, status) => {
  try {
    const res = await axios.post('/leave/approve', null, { 
      params: { id, status }, 
      headers: { token: localStorage.getItem('token') } 
    })
    if (res.data.code === 1) {
      ElMessage.success('审批成功')
      fetchLeaves()
    } else {
      ElMessage.error(res.data.msg || '审批失败')
    }
  } catch (error) {
    ElMessage.error('操作失败，请重试')
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

const handleFilterChange = () => {
  // 可以在这里添加额外的过滤逻辑
}

const handleImageError = (e) => {
  e.target.style.display = 'none'
  e.target.nextElementSibling.style.display = 'block'
}

onMounted(fetchLeaves)
</script>

<style scoped>
.approval-container {
  padding: 20px;
}

.approval-card {
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

.approval-card:hover {
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

.filter-section {
  margin-bottom: 20px;
}

.employee-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.employee-info :deep(.el-avatar) {
  background: linear-gradient(135deg, #409EFF 0%, #36D1DC 100%);
  color: #fff;
  font-weight: bold;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.approve-btn, .reject-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  border-radius: 4px;
  font-weight: bold;
  transition: all 0.3s ease;
}

.approve-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(103, 194, 58, 0.3);
}

.reject-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(245, 108, 108, 0.3);
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

:deep(.el-radio-button__inner) {
  border-radius: 4px;
  padding: 8px 16px;
  font-weight: bold;
}

:deep(.el-radio-button__original-radio:checked + .el-radio-button__inner) {
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
}
</style>
