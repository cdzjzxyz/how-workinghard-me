<template>
  <div>
    <el-card>
      <div style="display: flex; align-items: center; border-bottom: 2px solid #1890ff; margin-bottom: 0;">
        <span style="font-size: 20px; font-weight: bold; color: #1890ff; border-bottom: 2px solid #1890ff; padding: 0 16px 8px 0;">员工管理</span>
        <el-button v-if="isAdmin" type="primary" @click="openAddDialog" style="margin-left: 24px; margin-bottom: 8px;">+ 新增员工</el-button>
      </div>
      <!-- 查询条件 -->
      <div style="display: flex; align-items: center; margin-bottom: 16px;">
        <el-input v-model="query.name" placeholder="请输入员工姓名" style="width: 180px; margin-right: 12px;" clearable />
        <el-select v-model="query.gender" placeholder="性别" style="width: 100px; margin-right: 12px;" clearable>
          <el-option label="男" :value="1" />
          <el-option label="女" :value="2" />
        </el-select>
        <el-date-picker v-model="query.hireRange" type="daterange" range-separator="至" start-placeholder="入职开始" end-placeholder="入职结束" style="margin-right: 12px;" value-format="YYYY-MM-DD" />
        <el-button type="primary" @click="fetchEmps">查询</el-button>
        <el-button @click="resetQuery" style="margin-left: 8px;">重置</el-button>
      </div>
      <!-- 员工表格 -->
      <el-table :data="empList" border v-loading="loading" style="width: 100%;" @sort-change="handleSortChange">
        <el-table-column type="selection" width="50" align="center" />
        <el-table-column label="工号" prop="id" :sortable="true">
          <template #default="scope">
            {{ formatEmpId(scope.row.id) }}
          </template>
        </el-table-column>
        <el-table-column prop="name" label="姓名" align="center" />
        <el-table-column prop="gender" label="性别" align="center">
          <template #default="scope">{{ scope.row.gender === 1 ? '男' : '女' }}</template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" align="center" />
        <el-table-column prop="deptName" label="所属部门" align="center" />
        <el-table-column prop="job" label="职位" align="center">
          <template #default="scope">{{ positionMap[scope.row.job] }}</template>
        </el-table-column>
        <el-table-column prop="salary" label="薪资" align="center" :sortable="true" />
        <el-table-column prop="entryDate" label="入职时间" align="center" :sortable="true">
          <template #default="scope">{{ formatDate(scope.row.entryDate) }}</template>
        </el-table-column>
        <el-table-column prop="image" label="头像" align="center">
          <template #default="scope">
            <img v-if="scope.row.image" :src="scope.row.image" style="width:40px;height:40px;border-radius:50%;object-fit:cover;" />
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="最后操作时间" align="center">
          <template #default="scope">{{ formatDateTime(scope.row.updateTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="120" align="center" v-if="isAdmin">
          <template #default="scope">
            <span class="action-link" @click="openEditDialog(scope.row)">编辑</span>
            <span class="action-link" style="margin-left: 12px;" @click="handleDelete(scope.row.id)">删除</span>
          </template>
        </el-table-column>
      </el-table>
      <!-- 分页 -->
      <div style="margin-top: 16px; text-align: right;">
        <el-pagination
          background
          layout="prev, pager, next, sizes, total"
          :total="total"
          :page-size="query.pageSize"
          :current-page="query.page"
          @current-change="val => { query.page = val; fetchEmps(); }"
          @size-change="val => { query.pageSize = val; query.page = 1; fetchEmps(); }"
          :page-sizes="[5, 10, 20, 50]"
        />
      </div>
    </el-card>

    <!-- 新增/编辑员工弹窗 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="800px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="90px" style="margin-top: 16px;">
        <!-- 第一行：用户名、姓名 -->
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="form.username" maxlength="20" placeholder="请输入用户名，20个字符内" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="form.name" maxlength="10" placeholder="请输入员工姓名，10个字符内" />
            </el-form-item>
          </el-col>
        </el-row>
        <!-- 第二行：性别、手机号 -->
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-select v-model="form.gender" placeholder="请选择">
                <el-option label="男" :value="1" />
                <el-option label="女" :value="2" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="form.phone" maxlength="11" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
        </el-row>
        <!-- 第三行：职位、薪资 -->
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="职位" prop="job">
              <el-select v-model="form.job" placeholder="请选择">
                <el-option v-for="(label, val) in positionMap" :key="val" :label="label" :value="parseInt(val)" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="薪资" prop="salary">
              <el-input v-model="form.salary" type="number" placeholder="请输入薪资" />
            </el-form-item>
          </el-col>
        </el-row>
        <!-- 第四行：所属部门、入职日期 -->
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="所属部门" prop="deptId">
              <el-select v-model="form.deptId" placeholder="请选择">
                <el-option v-for="dept in deptList" :key="dept.id" :label="dept.name" :value="dept.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="入职日期" prop="entryDate">
              <el-date-picker v-model="form.entryDate" type="date" value-format="YYYY-MM-DD" placeholder="请选择入职日期" />
            </el-form-item>
          </el-col>
        </el-row>
        <!-- 第五行：头像 -->
        <el-row>
          <el-col :span="24">
            <el-form-item label="头像" prop="image">
              <el-upload
                class="avatar-uploader"
                :action="uploadUrl"
                :show-file-list="false"
                :on-success="handleUploadSuccess"
                :before-upload="beforeUpload"
                :headers="uploadHeaders"
                name="file"
              >
                <img v-if="form.image" :src="form.image" class="avatar" />
                <i v-else class="el-icon-plus avatar-uploader-icon"></i>
              </el-upload>
              <div style="font-size: 12px; color: #888;">图片大小不超过2M，支持JPG/PNG/JPEG格式，建议比例1:1</div>
            </el-form-item>
          </el-col>
        </el-row>
        <!-- 第六行：工作经历 -->
        <el-row>
          <el-col :span="24">
            <el-form-item label="工作经历">
              <div v-for="(expr, idx) in form.exprList" :key="idx" style="display: flex; align-items: center; margin-bottom: 8px;">
                <el-date-picker
                  v-model="expr.begin"
                  type="date"
                  placeholder="开始"
                  value-format="YYYY-MM-DD"
                  style="width: 120px; margin-right: 8px;"
                />
                <span style="margin: 0 4px;">-</span>
                <el-date-picker
                  v-model="expr.end"
                  type="date"
                  placeholder="结束"
                  value-format="YYYY-MM-DD"
                  style="width: 120px; margin-right: 8px;"
                />
                <el-input v-model="expr.company" placeholder="公司" style="width: 120px; margin-right: 8px;" />
                <el-input v-model="expr.job" placeholder="职位" style="width: 120px; margin-right: 8px;" />
                <el-button type="danger" icon="el-icon-delete" @click="form.exprList.splice(idx, 1)" circle />
              </div>
              <el-button type="primary" plain icon="el-icon-plus" @click="form.exprList.push({begin:'',end:'',company:'',job:''})">
                新增工作经历
              </el-button>
            </el-form-item>
          </el-col>
        </el-row>
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
  name: 'EmployeeManage',
  setup() {
    const isAdmin = computed(() => {
      const user = localStorage.getItem('user') ? JSON.parse(localStorage.getItem('user')) : {}
      return user && user.username === 'admin'
    })
    // 查询条件
    const query = reactive({
      name: '',
      gender: '',
      hireRange: [],
      page: 1,
      pageSize: 10,
      sortField: '',
      sortOrder: ''
    })
    const empList = ref([])
    const total = ref(0)
    const loading = ref(false)
    const dialogVisible = ref(false)
    const dialogTitle = ref('')
    const isEdit = ref(false)
    const form = reactive({
      id: null,
      username: '',
      password: '',
      name: '',
      gender: '',
      phone: '',
      job: '',
      salary: '',
      image: '',
      entryDate: '',
      deptId: '',
      createTime: '',
      updateTime: '',
      exprList: []
    })
    const formRef = ref(null)
    const rules = {
      username: [{ required: true, message: '请输入工号', trigger: 'blur' }],
      name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
      gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
      phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
      job: [{ required: true, message: '请选择职位', trigger: 'change' }],
      entryDate: [{ required: true, message: '请选择入职日期', trigger: 'change' }],
      deptId: [{ required: true, message: '请选择部门', trigger: 'change' }],
      salary: [{ required: true, message: '请输入薪资', trigger: 'blur' }],
      image: [{ required: true, message: '请上传头像', trigger: 'change' }]
    }
    const deptList = ref([
      { id: 1, name: '人事部' },
      { id: 2, name: '销售部' },
      { id: 3, name: '技术部' },
      { id: 4, name: '市场部' },
      { id: 5, name: '财务部' },
      { id: 6, name: '法务部' }
    ])
    const positionMap = {
      1: '经理',
      2: '总监',
      3: '主管',
      4: '组长',
      5: '普通员工',
      6: '其他'
    }
    // 上传相关
    const uploadUrl = 'http://localhost:8080/emps/upload'
    const uploadHeaders = {}
    const handleUploadSuccess = (res, file) => {
      console.log('上传返回内容', res);
      if (res.data.startsWith('/upload/')) {
        form.image = `http://localhost:8080${res.data}`;
      } else if (res.data.startsWith('http')) {
        form.image = res.data;
      } else {
        form.image = `http://localhost:8080/upload/${res.data}`;
      }
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
    // 格式化时间
    const formatDate = (val) => {
      if (!val) return ''
      if (Array.isArray(val)) {
        return dayjs(new Date(
          val[0], val[1] - 1, val[2]
        )).format('YYYY-MM-DD')
      }
      return dayjs(val).isValid() ? dayjs(val).format('YYYY-MM-DD') : val
    }
    const formatDateTime = (val) => {
      if (!val) return ''
      if (Array.isArray(val)) {
        return dayjs(new Date(
          val[0], val[1] - 1, val[2], val[3] || 0, val[4] || 0, val[5] || 0
        )).format('YYYY-MM-DD HH:mm:ss')
      }
      return dayjs(val).isValid() ? dayjs(val).format('YYYY-MM-DD HH:mm:ss') : val
    }
    // 查询员工列表
    const fetchEmps = async () => {
      loading.value = true
      try {
        const params = {}
        if (query.name) params.name = query.name
        if (query.gender) params.gender = query.gender
        params.page = query.page
        params.pageSize = query.pageSize
        if (query.hireRange && query.hireRange.length === 2) {
          params.begin = query.hireRange[0]
          params.end = query.hireRange[1]
        }
        if (query.sortField) params.sortField = query.sortField
        if (query.sortOrder) params.sortOrder = query.sortOrder
        const res = await axios.get(`${BASE_URL}/emps`, { params })
        if (res.data.code === 1 && res.data.data && Array.isArray(res.data.data.rows)) {
          empList.value = res.data.data.rows
          total.value = res.data.data.total
        }
      } finally {
        loading.value = false
      }
    }
    // 处理表格排序事件
    const handleSortChange = ({ prop, order }) => {
      query.sortField = prop
      query.sortOrder = order === 'ascending' ? 'asc' : order === 'descending' ? 'desc' : ''
      query.page = 1
      fetchEmps()
    }
    // 重置查询
    const resetQuery = () => {
      query.name = ''
      query.gender = ''
      query.hireRange = []
      query.page = 1
      fetchEmps()
    }
    // 打开新增弹窗
    const openAddDialog = () => {
      dialogTitle.value = '新增员工'
      isEdit.value = false
      Object.assign(form, { id: null, username: '', name: '', gender: '', phone: '', job: '', entryDate: '', deptId: '', salary: '', image: '', exprList: [] })
      dialogVisible.value = true
    }
    // 打开编辑弹窗
    const openEditDialog = async (row) => {
      dialogTitle.value = '编辑员工'
      isEdit.value = true
      const res = await axios.get(`${BASE_URL}/emps/${row.id}`)
      if (res.data.code === 1) {
        Object.assign(form, res.data.data)
        if (!form.exprList) form.exprList = []
        // 工作经历时间格式转换
        form.exprList = form.exprList.map(expr => ({
          ...expr,
          begin: Array.isArray(expr.begin) ? dayjs(new Date(expr.begin[0], expr.begin[1] - 1, expr.begin[2])).format('YYYY-MM-DD') : expr.begin,
          end: Array.isArray(expr.end) ? dayjs(new Date(expr.end[0], expr.end[1] - 1, expr.end[2])).format('YYYY-MM-DD') : expr.end
        }))
        // 入职日期格式转换
        if (Array.isArray(form.entryDate)) {
          form.entryDate = dayjs(new Date(form.entryDate[0], form.entryDate[1] - 1, form.entryDate[2])).format('YYYY-MM-DD')
        }
      }
      dialogVisible.value = true
    }
    // 新增/编辑提交
    const handleSubmit = () => {
      formRef.value.validate(async (valid) => {
        if (!valid) return
        if (isEdit.value) {
          await axios.put(`${BASE_URL}/emps`, form)
          ElMessage.success('修改成功')
        } else {
          await axios.post(`${BASE_URL}/emps`, form)
          ElMessage.success('新增成功')
        }
        dialogVisible.value = false
        fetchEmps()
      })
    }
    // 删除员工
    const handleDelete = (id) => {
      ElMessageBox.confirm('确定要删除该员工吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }).then(async () => {
        await axios.delete(`${BASE_URL}/emps`, { params: { ids: id } })
        ElMessage.success('删除成功')
        fetchEmps()
      }).catch(() => {})
    }
    // 初始化
    onMounted(() => {
      fetchEmps()
    })
    function formatEmpId(id) {
      return id ? id.toString().padStart(9, '0') : ''
    }
    return {
      isAdmin,
      query,
      empList,
      total,
      loading,
      dialogVisible,
      dialogTitle,
      isEdit,
      form,
      formRef,
      rules,
      deptList,
      positionMap,
      uploadUrl,
      uploadHeaders,
      handleUploadSuccess,
      beforeUpload,
      formatDate,
      formatDateTime,
      fetchEmps,
      resetQuery,
      openAddDialog,
      openEditDialog,
      handleSubmit,
      handleDelete,
      formatEmpId,
      handleSortChange
    }
  }
}
</script>

<style scoped>
.el-card {
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
  padding: 20px;
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
.avatar-uploader .avatar {
  width: 80px;
  height: 80px;
  display: block;
  border-radius: 50%;
  object-fit: cover;
  margin-bottom: 8px;
}
.avatar-uploader-icon {
  font-size: 32px;
  color: #8c939d;
  width: 80px;
  height: 80px;
  line-height: 80px;
  text-align: center;
  border: 1px dashed #d9d9d9;
  border-radius: 50%;
}
</style> 