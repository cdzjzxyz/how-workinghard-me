<template>
  <div>
    <el-form :inline="true" style="margin-bottom: 18px;">
      <el-form-item label="月份">
        <el-date-picker v-model="month" type="month" value-format="YYYY-MM" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="fetchReport">查询</el-button>
        <el-button @click="exportExcel">导出Excel</el-button>
      </el-form-item>
    </el-form>
    <el-alert v-if="monthScore !== null" :title="`本月考勤绩效分：${monthScore}`" type="success" show-icon style="margin-bottom: 18px;" />
    <el-row :gutter="20" style="margin-bottom: 18px;">
      <el-col :span="4"><div class="stat-block attend">出勤：{{ report.attend }}</div></el-col>
      <el-col :span="4"><div class="stat-block late">迟到：{{ report.late }}</div></el-col>
      <el-col :span="4"><div class="stat-block early">早退：{{ report.early }}</div></el-col>
      <el-col :span="4"><div class="stat-block absent">缺卡：{{ report.absent }}</div></el-col>
      <el-col :span="4"><div class="stat-block leave">请假：{{ report.leave }}</div></el-col>
    </el-row>
    <el-table :data="detailList" border stripe style="margin-bottom: 18px;">
      <el-table-column prop="date" label="日期" width="110" />
      <el-table-column prop="checkIn" label="签到时间" width="120" />
      <el-table-column prop="checkOut" label="签退时间" width="120" />
      <el-table-column prop="status" label="状态" width="90">
        <template #default="scope">
          <span :class="statusClass(scope.row.status)">{{ statusText(scope.row.status) }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="performanceScore" label="绩效分数" width="100" />
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const month = ref(new Date().toISOString().slice(0, 7))
const report = ref({ attend: 0, late: 0, early: 0, absent: 0, leave: 0 })
const detailList = ref([])
const monthScore = ref(null)

const fetchReport = async () => {
  // 假设后端接口 /attendance/report?month=YYYY-MM
  const { data } = await axios.get('/attendance/report', { params: { month: month.value } })
  if (data.code === 1) {
    report.value = data.data.summary
    detailList.value = data.data.detail
    fetchMonthScore()
  } else {
    ElMessage.error(data.msg || '获取报表失败')
  }
}
const fetchMonthScore = async () => {
  const { data } = await axios.get('/attendance/month-score', { params: { month: month.value } })
  if (data.code === 1) {
    monthScore.value = data.data
  } else {
    monthScore.value = null
  }
}
const exportExcel = async () => {
  try {
    // 自动带token
    const token = localStorage.getItem('token')
    const res = await axios.get('/attendance/export', {
      params: { month: month.value },
      responseType: 'blob',
      headers: token ? { token } : {}
    })
    // 处理文件名（兼容中文）
    let fileName = `考勤报表_${month.value}.xlsx`
    const disposition = res.headers && (res.headers['content-disposition'] || res.headers['Content-Disposition'])
    if (disposition) {
      const match = disposition.match(/filename\*=UTF-8''(.+)/)
      if (match) fileName = decodeURIComponent(match[1])
      else {
        const match2 = disposition.match(/filename="?([^";]+)"?/)
        if (match2) fileName = decodeURIComponent(match2[1])
      }
    }
    const blob = new Blob([res.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = fileName
    document.body.appendChild(a)
    a.click()
    document.body.removeChild(a)
    window.URL.revokeObjectURL(url)
  } catch (e) {
    ElMessage.error('导出失败，请联系管理员！')
  }
}
const statusText = (status) => {
  switch (status) {
    case 1: return '正常';
    case 2: return '迟到';
    case 3: return '早退';
    case 4: return '缺卡';
    case 5: return '请假';
    default: return '未知';
  }
}
const statusClass = (status) => {
  switch (status) {
    case 1: return 'attendance-status-normal'
    case 2: return 'attendance-status-late'
    case 3: return 'attendance-status-early'
    case 4: return 'attendance-status-absent'
    case 5: return 'attendance-status-leave'
    default: return ''
  }
}
onMounted(() => { fetchReport(); fetchMonthScore(); })
</script>

<style scoped>
.stat-block {
  background: linear-gradient(135deg, #e0eafc 60%, #cfdef3 100%);
  border-radius: 8px;
  padding: 10px 0;
  font-size: 15px;
  text-align: center;
  margin-bottom: 6px;
  color: #409EFF;
  font-weight: bold;
  box-shadow: 0 1px 4px rgba(64,158,255,0.05);
}
.stat-block.attend { background: linear-gradient(135deg, #e8f8ef 60%, #d2f4e5 100%); color: #219150; }
.stat-block.late { background: linear-gradient(135deg, #fff6e5 60%, #ffe6c7 100%); color: #b26a00; }
.stat-block.early { background: linear-gradient(135deg, #ffeaea 60%, #ffd6d6 100%); color: #c0392b; }
.stat-block.absent { background: linear-gradient(135deg, #f4f4f5 60%, #e0e0e0 100%); color: #606266; }
.stat-block.leave { background: linear-gradient(135deg, #e6f4ff 60%, #c7e6ff 100%); color: #1765ad; }
.attendance-status-normal { color: #219150; font-weight: bold; }
.attendance-status-late { color: #b26a00; font-weight: bold; }
.attendance-status-early { color: #c0392b; font-weight: bold; }
.attendance-status-absent { color: #606266; font-weight: bold; }
.attendance-status-leave { color: #1765ad; font-weight: bold; }
</style>
