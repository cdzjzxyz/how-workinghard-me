<template>
  <div class="notification-container">
    <el-card class="notification-card">
      <template #header>
        <div class="card-header">
          <span class="header-title">我的通知</span>
          <div class="header-desc">查看您的所有站内通知</div>
        </div>
      </template>

      <el-table
        :data="notifications"
        style="width: 100%"
        v-loading="loading"
        :header-cell-style="{ background: '#f5f7fa', color: '#606266' }"
      >
        <el-table-column label="状态" width="80">
          <template #default="scope">
            <el-tag v-if="!scope.row.isRead" type="danger" size="small">未读</el-tag>
            <el-tag v-else type="info" size="small">已读</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="通知内容" show-overflow-tooltip />
        <el-table-column label="创建时间" width="180">
           <template #default="scope">
             {{ formatDate(scope.row.createTime) }}
           </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="scope">
            <el-button
              v-if="!scope.row.isRead"
              type="primary"
              size="small"
              @click="markAsRead(scope.row.id)"
            >标记已读</el-button>
             <el-button
               type="danger"
               size="small"
               @click="deleteNotification(scope.row.id)"
             >删除</el-button>
          </template>
        </el-table-column>
      </el-table>

       <el-empty v-if="!loading && notifications.length === 0" description="暂无通知"></el-empty>

    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage, ElCard, ElTable, ElTableColumn, ElTag, ElButton, ElEmpty } from 'element-plus'
import dayjs from 'dayjs'

const notifications = ref([])
const loading = ref(false)

const fetchNotifications = async () => {
  loading.value = true
  try {
    const res = await axios.get('/notification/my', { headers: { token: localStorage.getItem('token') } })
    if (res.data.code === 1) {
      notifications.value = res.data.data
    }
  } catch (error) {
     ElMessage.error('获取通知失败');
     console.error(error);
  } finally {
    loading.value = false
  }
}

const markAsRead = async (id) => {
   try {
     const res = await axios.post(`/notification/markAsRead/${id}`, null, { headers: { token: localStorage.getItem('token') } })
     if (res.data.code === 1) {
       ElMessage.success('标记成功');
       fetchNotifications(); // 刷新列表
     } else {
       ElMessage.error(res.data.msg || '标记失败');
     }
   } catch (error) {
      ElMessage.error('操作失败');
      console.error(error);
   }
}

const deleteNotification = async (id) => {
    try {
     const res = await axios.delete(`/notification/delete/${id}`, { headers: { token: localStorage.getItem('token') } })
     if (res.data.code === 1) {
       ElMessage.success('删除成功');
       fetchNotifications(); // 刷新列表
     } else {
       ElMessage.error(res.data.msg || '删除失败');
     }
   } catch (error) {
      ElMessage.error('操作失败');
      console.error(error);
   }
}

function formatDate(val) {
  return dayjs(val).format('YYYY-MM-DD HH:mm:ss')
}

onMounted(fetchNotifications)

</script>

<style scoped>
.notification-container {
  padding: 20px;
  background-color: #f0f2f5; /* 轻微的背景色 */
  min-height: calc(100vh - 64px - 40px); /* 减去header和padding的高度 */
}

.notification-card {
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
}

.notification-card:hover {
   transform: translateY(-2px);
   box-shadow: 0 6px 24px rgba(0, 0, 0, 0.08);
}

.card-header {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.header-title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.header-desc {
  font-size: 13px;
  color: #909399;
}

:deep(.el-table) {
  border-radius: 8px;
  overflow: hidden;
}

:deep(.el-table th) {
  background-color: #f5f7fa;
  font-weight: bold;
}

.el-empty {
    padding: 40px 0;
}
</style>