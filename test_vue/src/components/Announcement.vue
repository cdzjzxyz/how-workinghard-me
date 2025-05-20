<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <span>公告管理</span>
        <el-button type="primary" @click="openDialog('add')">新增公告</el-button>
      </div>
    </template>

    <!-- 公告列表页面 -->
    <template v-if="!isUpdatePage">
      <el-table :data="announcements" style="width: 100%">
        <el-table-column prop="title" label="标题" width="180" />
        <el-table-column prop="content" label="内容" />
        <el-table-column prop="createTime" label="发布时间" width="180" />
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button size="small" @click="openDialog('edit', row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteAnnouncement(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </template>

    <!-- 更新系统公告页面 -->
    <template v-else>
        <el-form :model="latestAnnouncementForm" label-width="120px">
          <el-form-item label="最新公告标题">
            <el-input v-model="latestAnnouncementForm.title"></el-input>
          </el-form-item>
          <el-form-item label="最新公告内容">
            <el-input v-model="latestAnnouncementForm.content" type="textarea" :rows="6"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="submitUpdateForm">更新公告</el-button>
          </el-form-item>
        </el-form>
    </template>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="form.title"></el-input>
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="form.content" type="textarea" :rows="4"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import axios from 'axios';
import { ElMessage, ElMessageBox } from 'element-plus';
import dayjs from 'dayjs';
import { useRoute } from 'vue-router';

const announcements = ref([]);
const dialogVisible = ref(false);
const dialogTitle = ref('');
const form = ref({
  id: null,
  title: '',
  content: ''
});
const isEditing = ref(false);

const route = useRoute();

const isUpdatePage = computed(() => route.path === '/announcement/update');
const latestAnnouncementForm = ref({
  id: null,
  title: '',
  content: ''
});

const fetchAnnouncements = async () => {
  try {
    const res = await axios.get('/announcement');
    if (res.data.code === 1) {
      // 格式化时间
      announcements.value = res.data.data.map(item => ({
        ...item,
        createTime: dayjs(item.createTime).format('YYYY-MM-DD HH:mm:ss')
      }));
    }
  } catch (error) {
    ElMessage.error('获取公告列表失败');
    console.error(error);
  }
};

const fetchLatestAnnouncement = async () => {
  try {
    const res = await axios.get('/announcement/latest');
    if (res.data.code === 1 && res.data.data) {
      latestAnnouncementForm.value = res.data.data;
    } else {
      ElMessage.info('暂无系统公告可更新。');
      latestAnnouncementForm.value = { id: null, title: '', content: '' };
    }
  } catch (error) {
    ElMessage.error('获取最新公告失败');
    console.error(error);
  }
};

const openDialog = (type, row = null) => {
  if (type === 'add') {
    dialogTitle.value = '新增公告';
    form.value = { id: null, title: '', content: '' };
    isEditing.value = false;
  } else { // edit
    dialogTitle.value = '编辑公告';
    form.value = { ...row }; // 复制现有数据
    isEditing.value = true;
  }
  dialogVisible.value = true;
};

const submitForm = async () => {
  try {
    let res;
    if (isEditing.value) {
      res = await axios.put(`/announcement/${form.value.id}`, form.value);
    } else {
      res = await axios.post('/announcement', form.value);
    }

    if (res.data.code === 1) {
      ElMessage.success(`${isEditing.value ? '更新' : '新增'}成功`);
      dialogVisible.value = false;
      fetchAnnouncements(); // 刷新列表
    } else {
      ElMessage.error(res.data.msg || `${isEditing.value ? '更新' : '新增'}失败`);
    }
  } catch (error) {
    ElMessage.error(`${isEditing.value ? '更新' : '新增'}异常`);
    console.error(error);
  }
};

const submitUpdateForm = async () => {
  if (!latestAnnouncementForm.value.title || !latestAnnouncementForm.value.content) {
    ElMessage.warning('标题和内容不能为空');
    return;
  }
  try {
    const res = await axios.put(`/announcement/${latestAnnouncementForm.value.id}`, latestAnnouncementForm.value);
    if (res.data.code === 1) {
      ElMessage.success('系统公告更新成功');
    } else {
      ElMessage.error(res.data.msg || '系统公告更新失败');
    }
  } catch (error) {
    ElMessage.error('更新系统公告异常');
    console.error(error);
  }
};

const deleteAnnouncement = async (row) => {
  ElMessageBox.confirm(`确定删除公告 "${row.title}" 吗?`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    try {
      const res = await axios.delete(`/announcement/${row.id}`);
      if (res.data.code === 1) {
        ElMessage.success('删除成功');
        fetchAnnouncements(); // 刷新列表
      } else {
        ElMessage.error(res.data.msg || '删除失败');
      }
    } catch (error) {
      ElMessage.error('删除异常');
      console.error(error);
    }
  }).catch(() => {
    ElMessage.info('已取消删除');
  });
};

onMounted(() => {
  if (isUpdatePage.value) {
    fetchLatestAnnouncement();
  } else {
    fetchAnnouncements();
  }
});
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
