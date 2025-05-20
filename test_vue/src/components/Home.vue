/* eslint-disable */
<template>
  <div class="home-container">
    <el-row :gutter="20" style="margin-bottom: 20px;">
      <el-col :span="16">
        <el-card class="welcome-card" :style="{ background: welcomeCardBackground }">
          <h2>欢迎来到员工管理系统</h2>
          <p>本系统支持员工信息管理、部门管理、考勤统计、个人中心等功能。</p>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="avatar-card" :style="{ background: avatarCardBackground }">
          <div style="text-align:center;">
            <el-avatar :size="80" :src="userInfo.avatar || '默认头像地址'" />
            <div style="margin-top:10px;font-size:20px;font-weight:bold;">{{ userInfo.name }}</div>
            <div style="color: #888;">{{ positionMap[userInfo.job] }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="20" style="margin-bottom: 20px;">
      <el-col :span="6" v-for="item in statItems" :key="item.label">
        <el-card :style="{ background: item.color1 ? `linear-gradient(135deg,${item.color1},${item.color2})` : statCardBackground }">
          <div style="display:flex;align-items:center;">
            <i :class="item.icon" style="font-size:36px;margin-right:16px;"></i>
            <div>
              <div style="font-size:16px;">{{ item.label }}</div>
              <div style="font-size:32px;font-weight:bold;">{{ item.value }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="20" style="margin-bottom: 20px;">
      <el-col :span="12">
        <el-card :style="{ background: chartCardBackground }">
          <div ref="deptChart" class="dept-chart"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card :style="{ background: chartCardBackground }">
          <div ref="genderChart" class="gender-chart"></div>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card :style="{ background: latestEmpCardBackground }">
          <div style="font-weight:bold;">最新入职员工</div>
          <ul>
            <li v-for="emp in latestEmps.slice(0, 2)" :key="emp.id" style="display:flex;align-items:center;margin-bottom:8px;">
              <el-avatar :src="emp.image" :size="32" style="margin-right:8px;" />
              <span>{{ emp.name }}（{{ emp.deptName }}，{{ formatDate(emp.entryDate) }}）</span>
            </li>
          </ul>
          <el-divider />
          <el-button type="primary" @click="goToAddEmp">新增员工</el-button>
          <el-button @click="goToDept" style="margin-left:8px;">部门管理</el-button>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card :style="{ background: announcementCardBackground }">
          <div style="font-weight:bold;">公告栏</div>
          <div v-if="latestAnnouncement" style="margin: 10px 0 16px 0; color: #666;">
            <h4 style="margin: 0 0 8px 0; color: #333;">{{ latestAnnouncement.title }}</h4>
            <p style="margin: 0; white-space: pre-line;">{{ latestAnnouncement.content }}</p>
            <div style="font-size: 12px; color: #888; text-align: right; margin-top: 8px;">
              发布时间: {{ formatDate(latestAnnouncement.createTime) }}
            </div>
          </div>
          <div v-else style="margin: 10px 0 16px 0; color: #888;">暂无公告。</div>
          <el-divider />
          <div style="font-weight:bold; margin-bottom: 8px;">快捷操作</div>
          <el-button type="primary" size="small" @click="goToAttendance">考勤打卡</el-button>
          <el-button size="small" @click="goToProfile" style="margin-left:8px;">个人信息</el-button>
          <el-button size="small" @click="goToLeave" style="margin-left:8px;">请假申请</el-button>
        </el-card>
      </el-col>
    </el-row>
  </div>
  <el-card class="ai-card" style="margin-top: 24px;" :style="{ background: aiCardBackground }">
      <div style="font-weight:bold;font-size:18px;margin-bottom:8px;">AI智能考勤分析</div>
      <el-input
        v-model="aiInput"
        placeholder="请输入你想让AI分析的问题，如：本月考勤异常有哪些？"
        @keyup.enter="handleAskAI"
        clearable
        style="width: 80%; margin-right: 12px;"
      />
      <el-button type="primary" @click="handleAskAI" :loading="aiLoading">发送</el-button>
      <div v-if="aiAnswer" class="ai-answer" style="margin-top:16px;white-space:pre-line;">
        <el-divider>AI分析结果</el-divider>
        {{ aiAnswer }}
      </div>
    </el-card>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import * as echarts from 'echarts'
import axios from 'axios'
import dayjs from 'dayjs'
import { useRouter } from 'vue-router'
import { analyzeAttendance } from '@/api/ai'

// 函数：生成随机浅色或柔和的渐变色
function getRandomSoftGradient() {
  const hue = Math.random() * 360; // 0-360色相
  const s1 = Math.random() * 30 + 70; // 70-100饱和度
  const l1 = Math.random() * 10 + 90; // 90-100亮度 (非常浅)
  const s2 = Math.random() * 30 + 60; // 60-90饱和度
  const l2 = Math.random() * 10 + 85; // 85-95亮度 (稍深一点)

  const color1 = `hsl(${hue}, ${s1}%, ${l1}%)`;
  const color2 = `hsl(${(hue + 60) % 360}, ${s2}%, ${l2}%)`; // 稍微不同的色相
  const angle = Math.random() * 360;
  return `linear-gradient(${angle}deg, ${color1}, ${color2})`;
}

// 为不同的卡片定义背景 ref
const welcomeCardBackground = ref(getRandomSoftGradient());
const avatarCardBackground = ref(getRandomSoftGradient());
const statCardBackground = ref(getRandomSoftGradient());
const chartCardBackground = ref(getRandomSoftGradient());
const latestEmpCardBackground = ref(getRandomSoftGradient());
const announcementCardBackground = ref(getRandomSoftGradient());
const aiCardBackground = ref(getRandomSoftGradient());

const router = useRouter()

const stats = ref({
  empCount: 0,
  deptCount: 0,
  maleCount: 0,
  femaleCount: 0
})
const latestEmps = ref([])
const deptCounts = ref([])
const genderRatio = ref([])

const deptChart = ref(null)
const genderChart = ref(null)

const userInfo = ref({
  name: '',
  avatar: '',
  job: ''
})

const positionMap = { 1: '经理', 2: '总监', 3: '主管', 4: '组长', 5: '普通员工', 6: '其他' }

const statItems = computed(() => [
  { label: '员工总数', value: stats.value.empCount, icon: 'el-icon-user', color1: '#409EFF', color2: '#53a8ff' },
  { label: '部门数', value: stats.value.deptCount, icon: 'el-icon-office-building', color1: '#67C23A', color2: '#95d475' },
  { label: '男员工', value: stats.value.maleCount, icon: 'el-icon-male', color1: '#36D1C4', color2: '#5B86E5' },
  { label: '女员工', value: stats.value.femaleCount, icon: 'el-icon-female', color1: '#F56C6C', color2: '#fbbd61' }
])
const aiInput = ref('')
const aiAnswer = ref('')
const aiLoading = ref(false)

const latestAnnouncement = ref(null)

const fetchLatestAnnouncement = async () => {
  try {
    const res = await axios.get('/announcement/latest')
    if (res.data.code === 1 && res.data.data) {
      latestAnnouncement.value = res.data.data
    } else {
      latestAnnouncement.value = null
    }
  } catch (error) {
    console.error('获取最新公告失败:', error)
    latestAnnouncement.value = null
  }
}

const handleAskAI = async () => {
  if (!aiInput.value.trim()) return
  aiLoading.value = true
  aiAnswer.value = ''
  try {
    const res = await analyzeAttendance(aiInput.value)
    aiAnswer.value = res.data
  } catch (e) {
    aiAnswer.value = 'AI分析失败，请稍后重试。'
  } finally {
    aiLoading.value = false
  }
}
function formatDate(val) {
  if (!val) return ''
  return dayjs(val).isValid() ? dayjs(val).format('YYYY-MM-DD') : val
}

function formatEmpId(id) {
  return id ? id.toString().padStart(9, '0') : ''
}

function renderDeptChart() {
  const myChart = echarts.init(deptChart.value)
  myChart.setOption({
    title: { text: '各部门人数分布', left: 'center', textStyle: { color: '#409EFF', fontSize: 18 } },
    tooltip: {},
    xAxis: {
      type: 'category',
      data: deptCounts.value.map(item => item.deptName)
    },
    yAxis: { type: 'value' },
    series: [{
      data: deptCounts.value.map(item => item.count),
      type: 'bar',
      barWidth: '50%',
      itemStyle: { color: '#409EFF' }
    }]
  })
}

function renderGenderChart() {
  const myChart = echarts.init(genderChart.value)
  myChart.setOption({
    title: { text: '男女比例', left: 'center', textStyle: { color: '#409EFF', fontSize: 18 } },
    tooltip: { trigger: 'item' },
    legend: { bottom: 10, left: 'center' },
    series: [{
      name: '性别',
      type: 'pie',
      radius: '50%',
      data: genderRatio.value,
      label: { formatter: '{b}: {c} ({d}%)' }
    }]
  })
}

const goToAddEmp = () => router.push('/emp')
const goToDept = () => router.push('/dept')
const goToAttendance = () => router.push('/attendance')
const goToProfile = () => router.push('/profile')
const goToLeave = () => router.push('/leave')

onMounted(async () => {
  const userRes = await axios.get('/emps/profile')
  if (userRes.data.code === 1) {
    userInfo.value = {
      name: userRes.data.data.name,
      avatar: userRes.data.data.image,
      job: userRes.data.data.job
    }
  }
  const res = await axios.get('/emps/stats')
  if (res.data.code === 1) {
    stats.value.empCount = res.data.data.empCount
    stats.value.deptCount = res.data.data.deptCount
    stats.value.maleCount = res.data.data.maleCount
    stats.value.femaleCount = res.data.data.femaleCount
    latestEmps.value = res.data.data.latestEmps
    deptCounts.value = res.data.data.deptCounts
    genderRatio.value = [
      { value: res.data.data.maleCount, name: '男' },
      { value: res.data.data.femaleCount, name: '女' }
    ]
    // 渲染图表
    renderDeptChart()
    renderGenderChart()
  }
  fetchLatestAnnouncement()
})
</script>

<style scoped>
.home-container {
  padding: 30px;
  background: #f7f7f7;
  min-height: 100vh;
}
.welcome-card {
  background: linear-gradient(135deg, #e0eafc 60%, #cfdef3 100%);
  color: #222;
  border: none;
  box-shadow: 0 4px 24px rgba(0,0,0,0.08);
  border-radius: 16px;
}
.avatar-card {
  background: linear-gradient(135deg, #f3e7e9 60%, #e3eeff 100%);
  border: none;
  box-shadow: 0 4px 24px rgba(0,0,0,0.08);
  border-radius: 16px;
}
.welcome-card h2 {
  font-size: 18px;
  margin-bottom: 6px;
}
.welcome-card p {
  font-size: 14px;
  margin-bottom: 0;
}
.avatar-card .el-avatar {
  width: 60px !important;
  height: 60px !important;
}
.avatar-card div {
  font-size: 16px !important;
}
.stat-block {
  min-height: 70px !important;
  padding: 10px 0 !important;
  font-size: 15px !important;
}
.el-card[style*="background:linear-gradient"] {
  min-height: 70px !important;
  padding: 10px 0 !important;
  font-size: 15px !important;
}
.dept-chart, .gender-chart {
  height: 220px !important;
  min-height: 200px !important;
  max-height: 240px !important;
}
ul {
  padding-left: 20px;
  margin: 0;
}
li {
  line-height: 1.8;
}
.el-button {
  font-size: 13px !important;
  padding: 4px 14px !important;
  border-radius: 14px !important;
}
.ai-card {
  max-width: 800px;
  margin: 0 auto;
}
.ai-answer {
  background: #f6f8fa;
  border-radius: 8px;
  padding: 16px;
  color: #222;
  font-size: 16px;
}
</style> 