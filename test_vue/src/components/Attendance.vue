<template>
  <div class="attendance-container">
    <div>
      <el-card>
        <div class="attendance-flex-row">
          <div>
            <template v-if="todayRecord">
              <div class="attendance-today-title">今日考勤</div>
              <div>
                <span class="attendance-today-label">上班打卡：</span>
                <span class="attendance-today-value">{{ todayRecord.checkInTime }}</span>
              </div>
              <div>
                <span class="attendance-today-label">下班打卡：</span>
                <span class="attendance-today-value">{{ todayRecord.checkOutTime }}</span>
              </div>
              <div>
                <span class="attendance-today-label">状态：</span>
                <span :class="statusClass(todayRecord.status)">{{ statusText(todayRecord.status) }}</span>
              </div>
            </template>
            <template v-else>
              <div style="color: #909399;">您今天还未上班打卡哦</div>
            </template>
            <div style="margin-top: 20px;">
              <el-button type="primary" @click="checkIn" :disabled="!!(todayRecord && todayRecord.checkInTime)">上班打卡</el-button>
              <el-button type="success" @click="checkOut" :disabled="!!(!todayRecord || todayRecord.checkOutTime)">下班打卡</el-button>
            </div>
          </div>
          <img :src="catPoint" alt="指猫" class="cat-point-img-right" />
        </div>
      </el-card>
      <el-card>
        <div ref="calendarChart" style="width: 100%; height: 400px;"></div>
      </el-card>
      <el-card style="margin-top: 20px;">
        <el-date-picker v-model="month" type="month" value-format="YYYY-MM" style="margin-bottom: 10px;" @change="getStats"/>
        <el-row :gutter="20">
          <el-col :span="4">
            <div class="stat-block attend"><i class="el-icon-success"></i> 出勤：{{ stats.attend }}</div>
          </el-col>
          <el-col :span="4">
            <div class="stat-block late"><i class="el-icon-time"></i> 迟到：{{ stats.late }}</div>
          </el-col>
          <el-col :span="4">
            <div class="stat-block early"><i class="el-icon-warning"></i> 早退：{{ stats.early }}</div>
          </el-col>
          <el-col :span="4">
            <div class="stat-block absent"><i class="el-icon-close"></i> 缺卡：{{ stats.absent }}</div>
          </el-col>
          <el-col :span="4">
            <div class="stat-block leave"><i class="el-icon-date"></i> 请假：{{ stats.leave }}</div>
          </el-col>
        </el-row>
      </el-card>
    </div>
    <el-card>
      <el-tabs v-model="activeTab">
        <el-tab-pane label="考勤报表" name="report">
          <attendance-report :emp-id="selectedEmpId" />
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import * as echarts from 'echarts'
import 'echarts/lib/component/calendar'
import 'echarts/lib/component/tooltip'
import AttendanceReport from './AttendanceReport.vue'
import catPoint from '@/assets/cat-point.jpg'

const attendanceList = ref([])
const todayRecord = ref(null)
const stats = ref({})
const month = ref(new Date().toISOString().slice(0, 7))
const calendarData = ref([])
const calendarChart = ref(null)
const activeTab = ref('report')
const selectedEmpId = ref(null)

const getToday = async () => {
  const { data } = await axios.get('/attendance/today')
  todayRecord.value = data.data
}
const getMyAttendance = async () => {
  const { data } = await axios.get('/attendance/my')
  attendanceList.value = data.data
}
const getStats = async () => {
  const { data } = await axios.get('/attendance/stats', { params: { month: month.value } })
  stats.value = data.data
}
const getCalendar = async () => {
  const { data } = await axios.get('/attendance/calendar', { params: { month: month.value } })
  calendarData.value = data.data
  renderCalendar()
}
const checkIn = async () => {
  await axios.post('/attendance/checkin')
  ElMessage.success('上班打卡成功')
  getToday()
  getMyAttendance()
  getStats()
  getCalendar()
}
const checkOut = async () => {
  await axios.post('/attendance/checkout')
  ElMessage.success('下班打卡成功')
  getToday()
  getMyAttendance()
  getStats()
  getCalendar()
}

const statusColor = (status) => {
  switch (status) {
    case 1: return '#67C23A'; // 正常
    case 2: return '#E6A23C'; // 迟到
    case 3: return '#F56C6C'; // 早退
    case 4: return '#909399'; // 缺卡
    case 5: return '#409EFF'; // 请假
    default: return '#dcdfe6';
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

const renderCalendar = () => {
  if (!calendarChart.value) return;
  const chart = echarts.init(calendarChart.value);

  const statusMap = {
    1: { name: '正常', color: '#67C23A' },
    2: { name: '迟到', color: '#E6A23C' },
    3: { name: '早退', color: '#F56C6C' },
    4: { name: '缺卡', color: '#909399' },
    5: { name: '请假', color: '#409EFF' },
    default: { name: '未知', color: '#dcdfe6'} // 为未知状态添加默认值
  };

  // 准备图例数据
  const legendData = Object.values(statusMap)
                          .filter(status => status.name !== '未知') // 可选：不显示“未知”在图例中
                          .map(status => status.name);

  // 为每种状态生成一个series
  const seriesData = legendData.map(statusName => {
    // 找到 statusMap 中 name 对应的 key (即状态码)
    const statusEntry = Object.entries(statusMap).find(([key, value]) => value.name === statusName);
    const statusCode = statusEntry ? parseInt(statusEntry[0]) : null; // 将key转回数字
    const seriesColor = statusEntry ? statusEntry[1].color : statusMap.default.color;

    return {
      name: statusName, // 系列名称，与图例对应
      type: 'scatter',
      coordinateSystem: 'calendar',
      symbolSize: 22,
      data: calendarData.value
        .filter(item => item.status === statusCode) // 筛选出当前状态的数据
        .map(item => [item.date, item.status]), // value只需要日期和状态值(或特定值用于tooltip)
                                                // ECharts 会自动在日历上找到对应日期
      itemStyle: {
        color: seriesColor, // 为整个系列设置颜色
        shadowBlur: 8,
        shadowColor: 'rgba(64,158,255,0.2)'
      },
      label: { // 标签配置可以移到这里，如果希望每个系列有不同的标签逻辑
        show: true,
        formatter: params => new Date(params.value[0]).getDate(),
        color: '#fff',
        fontWeight: 'bold'
      },
    };
  });

  chart.setOption({
    tooltip: {
      backgroundColor: '#fff',
      borderColor: '#409EFF',
      textStyle: { color: '#333' },
      formatter: params => {
        // params.value 通常是 [日期, status数字]
        // params.seriesName 就是图例中的状态名称，如 '正常', '迟到'
        const dateStr = params.value[0];
        const statusName = params.seriesName; // 直接从 seriesName 获取状态文本
        return `<b>${dateStr}</b><br/>${statusName}`;
      }
    },
    calendar: {
      top: 60,
      left: 30,
      right: 30,
      cellSize: [38, 38], //可以根据需要调整或设为'auto'
      range: month.value,
      dayLabel: { nameMap: 'cn', color: '#409EFF', fontWeight: 'bold' },
      monthLabel: { nameMap: 'cn', color: '#303133', fontSize: 18 },
      itemStyle: { borderRadius: 8, borderWidth: 1, borderColor: '#ebeef5' },
      splitLine: { show: true, lineStyle: { color: '#ebeef5' } }
    },
    legend: {
      data: legendData, // 使用从statusMap生成的图例数据
      bottom: 10,
      left: 'center',
      itemWidth: 16,
      itemHeight: 16
    },
    series: seriesData // 使用新生成的多个系列
  });
};
onMounted(() => {
  getToday()
  getMyAttendance()
  getStats()
  getCalendar()
})
watch(month, () => {
  getStats()
  getCalendar()
})
</script>

<style scoped>
.attendance-container {
  padding: 12px 4px 4px 4px;
  background: linear-gradient(135deg, #f7faff 60%, #e0eafc 100%);
  min-height: 100vh;
  font-family: 'Source Han Sans SC', '微软雅黑', 'Arial', sans-serif;
  font-size: 15px;
}
.el-card {
  border-radius: 10px !important;
  box-shadow: 0 2px 8px rgba(64,158,255,0.05) !important;
  border: none !important;
  margin-bottom: 14px;
  padding: 10px 18px 10px 18px;
}
.section-title {
  font-size: 20px;
  font-weight: bold;
  color: #409EFF;
  margin-bottom: 12px;
  letter-spacing: 1px;
  display: flex;
  align-items: center;
}
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
.stat-block.attend {
  background: linear-gradient(135deg, #e8f8ef 60%, #d2f4e5 100%);
  color: #219150; /* 深绿色 */
  text-shadow: 0 1px 2px #b6e2c6;
}
.stat-block.late {
  background: linear-gradient(135deg, #fff6e5 60%, #ffe6c7 100%);
  color: #b26a00; /* 深橙色 */
  text-shadow: 0 1px 2px #ffe6c7;
}
.stat-block.early {
  background: linear-gradient(135deg, #ffeaea 60%, #ffd6d6 100%);
  color: #c0392b; /* 深红色 */
  text-shadow: 0 1px 2px #ffd6d6;
}
.stat-block.absent {
  background: linear-gradient(135deg, #f4f4f5 60%, #e0e0e0 100%);
  color: #606266; /* 深灰色 */
  text-shadow: 0 1px 2px #e0e0e0;
}
.stat-block.leave {
  background: linear-gradient(135deg, #e6f4ff 60%, #c7e6ff 100%);
  color: #1765ad; /* 深蓝色 */
  text-shadow: 0 1px 2px #c7e6ff;
}
.el-button {
  border-radius: 16px !important;
  font-size: 14px;
  padding: 6px 18px;
  font-weight: bold;
  letter-spacing: 1px;
}
.el-date-picker {
  border-radius: 6px;
  font-size: 14px;
  height: 32px;
}
.attendance-today-title {
  font-size: 17px;
  font-weight: bold;
  color: #409EFF;
  margin-bottom: 6px;
}
.attendance-today-label {
  color: #303133;
  font-weight: bold;
  margin-right: 2px;
  font-size: 14px;
}
.attendance-today-value {
  color: #222;
  font-weight: normal;
  font-size: 14px;
}
.attendance-status-normal {
  color: #219150;
  font-weight: bold;
}
.attendance-status-late {
  color: #b26a00;
  font-weight: bold;
}
.attendance-status-early {
  color: #c0392b;
  font-weight: bold;
}
.attendance-status-absent {
  color: #606266;
  font-weight: bold;
}
.attendance-status-leave {
  color: #1765ad;
  font-weight: bold;
}
[ref="calendarChart"] {
  height: 320px !important;
  min-height: 320px !important;
  max-height: 340px !important;
}
.attendance-flex-row {
  display: flex;
  align-items: flex-start;
}

.cat-point-img-right {
  height: 80px;
  margin-left: 10px;
  margin-top: 10px;
  user-select: none;
}
</style>
