<template>
  <div>
    <el-card>
      <div style="display: flex; align-items: center; border-bottom: 2px solid #1890ff; margin-bottom: 0;">
        <span style="font-size: 20px; font-weight: bold; color: #1890ff; border-bottom: 2px solid #1890ff; padding: 0 16px 8px 0;">部门管理</span>
        <el-button v-if="isAdmin" type="primary" @click="openAddDialog" style="margin-left: 24px; margin-bottom: 8px;">+ 新增部门</el-button>
      </div>
      <el-table :data="deptList" style="width: 100%; margin-top: 16px;" v-loading="loading" border>
        <el-table-column label="序号" type="index" width="60" align="center" />
        <el-table-column prop="name" label="部门名称" align="center" />
        <el-table-column prop="updateTime" label="最后操作时间" align="center">
          <template #default="scope">
            {{ formatDate(scope.row.updateTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" align="center" v-if="isAdmin">
          <template #default="scope">
            <span class="action-link" @click="openEditDialog(scope.row)">编辑</span>
            <span class="action-link" style="margin-left: 12px;" @click="handleDelete(scope.row.id)">删除</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑部门弹窗 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="400px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="部门名称" prop="name">
          <el-input v-model="form.name" autocomplete="off" placeholder="请输入部门名称，长度为2-10位" maxlength="10" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">{{ isEdit ? '保存' : '新增' }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import axios from 'axios'
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import dayjs from 'dayjs'

const BASE_URL = 'http://localhost:8080'

export default {
  name: 'DepartmentManage',
  setup() {
    const isAdmin = computed(() => {
      const user = localStorage.getItem('user') ? JSON.parse(localStorage.getItem('user')) : {}
      return user && user.username === 'admin'
    })
    const deptList = ref([])
    const loading = ref(false)
    const dialogVisible = ref(false)
    const dialogTitle = ref('')
    const form = reactive({ id: null, name: '' })
    const formRef = ref(null)
    const rules = {
      name: [
        { required: true, message: '请输入部门名称', trigger: 'blur' },
        { min: 2, max: 10, message: '长度为2-10位', trigger: 'blur' }
      ]
    }
    const isEdit = ref(false)

    // 时间格式化
    const formatDate = (val) => {
      if (!val) return ''
      if (Array.isArray(val)) {
        return dayjs(new Date(
          val[0], val[1] - 1, val[2], val[3] || 0, val[4] || 0, val[5] || 0
        )).format('YYYY-MM-DD HH:mm:ss')
      }
      return dayjs(val).isValid() ? dayjs(val).format('YYYY-MM-DD HH:mm:ss') : val
    }

    // 查询部门列表
    const fetchDepts = async () => {
      loading.value = true
      try {
        const res = await axios.get(`${BASE_URL}/depts`)
        if (res.data.code === 1) {
          deptList.value = res.data.data
        }
      } finally {
        loading.value = false
      }
    }

    // 打开新增弹窗
    const openAddDialog = () => {
      dialogTitle.value = '新增部门'
      form.id = null
      form.name = ''
      isEdit.value = false
      dialogVisible.value = true
    }

    // 打开编辑弹窗
    const openEditDialog = async (row) => {
      dialogTitle.value = '编辑部门'
      isEdit.value = true
      const res = await axios.get(`${BASE_URL}/depts/${row.id}`)
      if (res.data.code === 1) {
        form.id = res.data.data.id
        form.name = res.data.data.name
        dialogVisible.value = true
      }
    }

    // 提交表单
    const handleSubmit = () => {
      formRef.value.validate(async (valid) => {
        if (!valid) return
        if (isEdit.value) {
          await axios.put(`${BASE_URL}/depts`, { id: form.id, name: form.name })
          ElMessage.success('修改成功')
        } else {
          await axios.post(`${BASE_URL}/depts`, { name: form.name })
          ElMessage.success('新增成功')
        }
        dialogVisible.value = false
        fetchDepts()
      })
    }

    // 删除部门，带二次确认
    const handleDelete = (id) => {
      ElMessageBox.confirm('确定要删除该部门吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }).then(async () => {
        await axios.delete(`${BASE_URL}/depts/${id}`)
        ElMessage.success('删除成功')
        fetchDepts()
      }).catch(() => {})
    }

    onMounted(() => {
      fetchDepts()
    })

    return {
      isAdmin,
      deptList,
      loading,
      dialogVisible,
      dialogTitle,
      form,
      formRef,
      rules,
      isEdit,
      openAddDialog,
      openEditDialog,
      handleSubmit,
      handleDelete,
      formatDate
    }
  }
}
</script>

<style scoped>
.el-card {
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
  padding: 20px;
  background-color: #f9fcff;
}

.action-link {
  color: #faad14;
  cursor: pointer;
  font-size: 15px;
  transition: color 0.2s;
}
.action-link:hover {
  color: #ff6700;
  text-decoration: underline;
}
</style> 