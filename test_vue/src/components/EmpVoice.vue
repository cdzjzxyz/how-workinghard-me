<template>
  <div class="emp-voice-container">
    <!-- 发表心声区域 -->
    <el-card class="post-voice-card">
      <template #header>
        <div class="card-header">
          <span class="header-title">发表你的心声</span>
          <div class="header-desc">在这里留下你想说的话吧！</div>
        </div>
      </template>
      
      <div class="post-area">
        <el-input
          v-model="content"
          type="textarea"
          placeholder="说出你的心声吧~"
          :rows="4"
          maxlength="500"
          show-word-limit
          class="voice-textarea"
        />
        <el-button type="primary" @click="postVoice" class="post-button">
          <el-icon><Promotion /></el-icon>发表心声
        </el-button>
      </div>
    </el-card>

    <!-- 心声列表区域 -->
    <el-card class="voice-list-card" v-loading="loading">
      <template #header>
        <div class="card-header">
          <span class="header-title">员工心声列表</span>
          <div class="header-desc">大家的心声都在这里</div>
        </div>
      </template>

      <div class="filter-area">
        <el-input v-model="keyword" placeholder="搜索心声内容或姓名..." class="search-input" @keyup.enter="fetchVoices" clearable />
        <el-button @click="fetchVoices" class="search-button">
          <el-icon><Search /></el-icon>搜索
        </el-button>
      </div>

      <div v-if="voices.length > 0" class="voice-items-list">
        <div class="voice-item" v-for="item in voices" :key="item.id">
          <div class="voice-header">
            <template v-if="item.image">
              <img :src="item.image" :size="40" class="voice-avatar-img"/>
            </template>
            <template v-else>
              <el-avatar :size="40" class="voice-avatar">
                 {{ item.empName?.charAt(0)?.toUpperCase() }} 
              </el-avatar>
            </template>
            <div class="voice-meta">
              <span class="emp-name">{{ item.empName }}</span>
              <span class="post-time">{{ formatDate(item.createTime) }}</span>
            </div>
          </div>
          <div class="voice-content">{{ item.content }}</div>
          <div class="voice-actions">
            <el-button
              v-if="canDelete(item)"
              @click="deleteVoice(item.id)"
              type="danger"
              size="small"
              icon="Delete"
              circle
            ></el-button>
             <el-button
                @click="toggleLike(item)"
                size="small"
                :type="item.hasLiked ? 'primary' : 'info'"
                :icon="item.hasLiked ? 'StarFilled' : 'Star'"
              >
                {{ item.likeCount || 0 }}
            </el-button>
             <el-button
                @click="openCommentDialog(item)"
                size="small"
                icon="ChatDotRound"
              >
                评论 ({{ item.comments ? item.comments.length : 0 }})
              </el-button>
          </div>
          <!-- 评论列表 -->
          <div v-if="item.comments && item.comments.length > 0" class="comment-list">
            <div class="comment-item" v-for="c in item.comments" :key="c.id">
              <template v-if="c.image">
                <img :src="c.image" :size="28" class="comment-avatar-img"/>
              </template>
              <template v-else>
                <el-avatar :size="28" class="comment-avatar">
                   {{ c.empName?.charAt(0)?.toUpperCase() }}
                </el-avatar>
              </template>
              <div class="comment-content-wrap">
                 <div class="comment-meta">
                   <span class="comment-emp-name">{{ c.empName }}</span>
                   <span class="comment-time">{{ formatDate(c.createTime) }}</span>
                 </div>
                 <div class="comment-text">{{ c.content }}</div>
              </div>
               <el-button
                v-if="canDeleteComment(c)"
                @click="deleteComment(c.id)"
                type="danger"
                size="small"
                icon="Delete"
                circle
                class="delete-comment-button"
              ></el-button>
            </div>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无心声"></el-empty>

      <el-pagination
        :total="total"
        :page-size="pageSize"
        :current-page="page"
        @current-change="val => { page = val; fetchVoices() }"
        layout="prev, pager, next, total"
        background
        class="pagination-footer"
      />
    </el-card>

    <!-- 发表评论弹窗 -->
    <el-dialog v-model="commentDialogVisible" title="发表评论" width="400px" class="comment-dialog">
      <el-input
        v-model="commentDialogContent"
        type="textarea"
        placeholder="请输入评论内容"
        maxlength="200"
        show-word-limit
        rows="4"
      />
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="commentDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitComment">发表</el-button>
        </span>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage, ElCard, ElInput, ElButton, ElDivider, ElAvatar, ElPagination, ElDialog, ElEmpty, ElIcon, ElTag } from 'element-plus'
import { Promotion, Search, Delete, StarFilled, Star, ChatDotRound } from '@element-plus/icons-vue'
import dayjs from 'dayjs'

const content = ref('')
const voices = ref([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const keyword = ref('')
const user = JSON.parse(localStorage.getItem('user') || '{}')

const loading = ref(false)
const commentDialogVisible = ref(false)
const commentDialogContent = ref('')
const commentDialogVoiceId = ref(null)

const fetchVoices = async () => {
  loading.value = true
  try {
    const res = await axios.get('/voice/list', { params: { page: page.value, pageSize: pageSize.value, keyword: keyword.value } })
    if (res.data.code === 1) {
      console.log('获取到的心声列表数据:', res.data.data.rows);
      voices.value = res.data.data.rows
      total.value = res.data.data.total
       // 确保每条心声和评论对象都有 image 字段，即使后端未提供，避免前端报错
       voices.value.forEach(voice => {
         if (!voice.image) voice.image = '';
         voice.comments?.forEach(comment => {
           if (!comment.image) comment.image = '';
         });
       });
    }
  } finally {
    loading.value = false
  }
}

onMounted(fetchVoices)

const postVoice = async () => {
  if (!content.value.trim()) {
    ElMessage.warning('内容不能为空')
    return
  }
  // 检查用户是否已登录
  if (!user || !user.id) {
      ElMessage.error('请先登录');
      return;
  }
  try {
    const res = await axios.post('/voice/post', { content: content.value }, { headers: { token: localStorage.getItem('token') } })
    if (res.data.code === 1) {
      ElMessage.success('发表成功')
      content.value = ''
      page.value = 1; // 发表成功后回到第一页
      fetchVoices()
    } else {
      ElMessage.error(res.data.msg || '发表失败')
    }
  } catch (error) {
      ElMessage.error('发表异常');
      console.error(error);
  }
}

const deleteVoice = async (id) => {
   // 检查用户是否已登录
  if (!user || !user.id) {
      ElMessage.error('请先登录');
      return;
  }
  try {
    const res = await axios.delete(`/voice/delete/${id}`, { headers: { token: localStorage.getItem('token') } })
    if (res.data.code === 1) {
      ElMessage.success('删除成功')
      fetchVoices()
    } else {
      ElMessage.error(res.data.msg || '删除失败')
    }
  } catch (error) {
      ElMessage.error('删除异常');
      console.error(error);
  }
}

const canDelete = (item) => {
  console.log('当前用户信息:', user);
  console.log('当前帖子信息:', item);
  return user && (user.username === 'admin' || user.id === item.emp_id)
}

const toggleLike = async (item) => {
    // 检查用户是否已登录
  if (!user || !user.id) {
      ElMessage.error('请先登录');
      return;
  }
  try {
    // 优化：先乐观更新 UI，再发送请求
    if (item.hasLiked) {
      item.hasLiked = false;
      item.likeCount = (item.likeCount || 0) - 1;
      await axios.post(`/voice/unlike/${item.id}`, null, { headers: { token: localStorage.getItem('token') } });
    } else {
      item.hasLiked = true;
      item.likeCount = (item.likeCount || 0) + 1;
       await axios.post(`/voice/like/${item.id}`, null, { headers: { token: localStorage.getItem('token') } });
    }
     // 成功后不需要再 refetchVoices，后端如果返回最新数据更好
     // fetchVoices() // 如果后端不支持返回最新列表，才需要这行

  } catch (error) {
      ElMessage.error('操作失败，请重试');
       // 如果请求失败，回滚 UI 状态
      item.hasLiked = !item.hasLiked;
      item.likeCount = (item.likeCount || 0) + (item.hasLiked ? -1 : 1);
      console.error(error);
  }
}

function openCommentDialog(voiceItem) {
  // 检查用户是否已登录
  if (!user || !user.id) {
      ElMessage.error('请先登录');
      return;
  }
  commentDialogVoiceId.value = voiceItem.id // 传递整个item或者只传递id都可以，看后续submit需不需要其他信息
  commentDialogContent.value = ''
  commentDialogVisible.value = true
}

async function submitComment() {
  if (!commentDialogContent.value.trim()) {
    ElMessage.warning('评论不能为空')
    return
  }
   // 检查用户是否已登录
  if (!user || !user.id) {
      ElMessage.error('请先登录');
      return;
  }
  if (!commentDialogVoiceId.value) return; // 确保有心声ID

  try {
    const res = await axios.post('/voice/comment', {
      voiceId: commentDialogVoiceId.value,
      content: commentDialogContent.value
    }, { headers: { token: localStorage.getItem('token') } })
    if (res.data.code === 1) {
      ElMessage.success('评论成功')
      commentDialogVisible.value = false
      // 找到对应的心声，手动添加评论，或者刷新列表
      fetchVoices() // 刷新列表确保数据一致性
    } else {
      ElMessage.error(res.data.msg || '评论失败')
    }
  } catch (error) {
    ElMessage.error('发表评论异常');
    console.error(error);
  }
}

function formatDate(val) {
  return dayjs(val).format('YYYY-MM-DD HH:mm')
}

function canDeleteComment(comment) {
  console.log('当前用户信息:', user);
  console.log('当前评论信息:', comment);
  return user && (user.id === comment.emp_id || user.username === 'admin')
}

async function deleteComment(commentId) {
    // 检查用户是否已登录
  if (!user || !user.id) {
      ElMessage.error('请先登录');
      return;
  }
  try {
    const res = await axios.delete(`/voice/comment/delete/${commentId}`, { headers: { token: localStorage.getItem('token') } });
    if (res.data.code === 1) {
      ElMessage.success('评论删除成功');
      fetchVoices(); // 删除成功后刷新列表
    } else {
      ElMessage.error(res.data.msg || '评论删除失败');
    }
  } catch (error) {
    ElMessage.error('评论删除异常');
    console.error(error);
  }
}

</script>

<style scoped>
.emp-voice-container {
  padding: 20px;
  background-color: #f0f2f5; /* 轻微的背景色 */
  min-height: calc(100vh - 64px - 40px); /* 减去header和padding的高度 */
}

.post-voice-card,
.voice-list-card {
  margin-bottom: 20px;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
}

.post-voice-card:hover,
.voice-list-card:hover {
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

.post-area {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.voice-textarea :deep(.el-textarea__inner) {
  border-radius: 8px;
  padding: 10px;
  font-size: 14px;
  line-height: 1.6;
  transition: border-color 0.3s, box-shadow 0.3s;
}

.voice-textarea :deep(.el-textarea__inner:focus) {
  border-color: #409EFF;
  box-shadow: 0 0 0 1px rgba(64, 158, 255, 0.2);
}

.post-button {
  align-self: flex-end; /* 按钮靠右对齐 */
  width: 120px;
  border-radius: 20px;
  font-weight: bold;
  transition: all 0.3s ease;
}

.post-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

.filter-area {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.search-input {
  flex-grow: 1;
}

.search-input :deep(.el-input__inner) {
    border-radius: 8px;
}

.search-button {
    border-radius: 8px;
}

.voice-items-list {
  /* 列表容器的样式 */
}

.voice-item {
  border-bottom: 1px solid #eee;
  padding: 15px 0;
  display: flex;
  flex-direction: column;
}

.voice-header {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.voice-avatar-img {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  margin-right: 12px;
  flex-shrink: 0;
}

.voice-avatar {
  margin-right: 12px;
  flex-shrink: 0; /* 防止头像被挤压 */
  background: linear-gradient(135deg, #409EFF 0%, #36D1DC 100%); /* 默认头像渐变 */
  color: #fff;
  font-weight: bold;
}

.voice-meta {
  display: flex;
  flex-direction: column;
}

.emp-name {
  font-weight: bold;
  color: #333;
  font-size: 15px;
}

.post-time {
  color: #909399;
  font-size: 12px;
  margin-top: 2px;
}

.voice-content {
  margin: 5px 0 10px 52px; /* 与头像对齐并留出空间 */
  color: #555;
  line-height: 1.6;
  word-break: break-word; /* 自动换行 */
}

.voice-actions {
  display: flex;
  justify-content: flex-end; /* 操作按钮靠右 */
  align-items: center;
  margin-top: 8px;
  gap: 8px; /* 按钮之间的间距 */
}

.voice-actions .el-button {
    transition: all 0.2s ease;
}

.voice-actions .el-button:hover {
    transform: translateY(-1px);
}

.comment-list {
  margin-top: 12px;
  padding-left: 52px; /* 与心声内容对齐 */
  border-top: 1px solid #eee;
  padding-top: 12px;
  background-color: #fefefe; /* 评论区域浅色背景 */
  border-radius: 8px;
}

.comment-item {
  display: flex;
  align-items: flex-start;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px dashed #eee; /* 评论之间的虚线 */
  position: relative; /* 为了删除按钮定位 */
}

.comment-item:last-child {
    border-bottom: none; /* 最后一个评论没有底线 */
    padding-bottom: 0;
}

.comment-avatar-img {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  object-fit: cover;
  margin-right: 10px;
  flex-shrink: 0;
}

.comment-avatar {
  margin-right: 10px;
  flex-shrink: 0;
    background: linear-gradient(135deg, #36D1DC 0%, #5B86E5 100%); /* 评论头像渐变 */
  color: #fff;
  font-weight: bold;
}

.comment-content-wrap {
  flex-grow: 1;
  min-width: 0; /* 允许在 flex 布局中收缩 */
}

.comment-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}

.comment-emp-name {
  font-weight: bold;
  color: #555;
  font-size: 13px;
}

.comment-time {
  color: #a0a0a0;
  font-size: 11px;
}

.comment-text {
  color: #333;
  font-size: 14px;
  line-height: 1.5;
  word-break: break-word;
}

.delete-comment-button {
   margin-left: 8px; /* 按钮与评论内容之间的间距 */
   /* 可以添加绝对定位等更复杂的布局 */
}

.el-empty {
    padding: 40px 0;
}

.pagination-footer {
  margin-top: 20px;
  justify-content: flex-end; /* 分页靠右 */
  display: flex;
}

.comment-dialog :deep(.el-dialog__body) {
    padding: 20px;
}

.dialog-footer {
    /* 弹窗底部按钮样式 */
}

</style>
