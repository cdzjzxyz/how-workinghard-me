# 员工考勤管理系统

## 项目简介

本项目是一个基于 **Spring Boot + Vue 3** 的员工考勤与信息管理系统，支持员工打卡、考勤统计、绩效分数自动计算、数据导出、个人中心等功能。系统界面美观，操作简便，适合中小型企业日常考勤与人事管理。

---

## 系统界面预览

- **首页仪表盘**：统计员工总数、部门数、男女比例、各部门人数分布、最新入职员工、公告等。
- **部门管理**：支持部门的新增、编辑、删除，显示部门及最后操作时间。
- **员工管理**：支持员工信息的增删改查，字段包括工号、姓名、性别、手机号、部门、职位、薪资、入职时间、头像、最后操作时间等。
- **考勤管理**：支持上班/下班打卡，日历视图展示每日考勤状态，统计出勤、迟到、早退、缺卡、请假等。
- **考勤报表**：按月统计考勤明细和绩效分数，支持一键导出Excel。
- **个人中心**：查看和修改个人信息、密码，支持请假申请。

---

## 主要功能

- **员工考勤打卡**：上班签到、下班签退，自动记录考勤状态（正常、迟到、早退、缺卡、请假）。
- **绩效分数自动生成**：根据考勤状态自动计算绩效分数，明细和报表均可查看。
- **考勤数据统计与导出**：按月统计个人考勤，支持Excel导出，包含日期、签到/签退时间、状态、绩效分数等。
- **员工与部门管理**：支持员工、部门的增删改查，信息一目了然。
- **个人中心与请假申请**：员工可查看个人信息、申请请假。
- **JWT登录认证**：安全的登录机制，所有接口需携带token访问。
- **权限控制**：区分普通员工和管理员（可扩展）。

---

## 技术选型

- **前端**：Vue 3、Element Plus、Axios
- **后端**：Spring Boot、MyBatis、MySQL、JWT、POI（Excel导出）
- **数据库**：MySQL
- **工具/依赖**：Maven、Lombok、Jakarta Servlet

---

## 目录结构

```
kaoqin/                        # 后端Spring Boot项目
  ├─ src/main/java/org/xynu/kaoqin/
  │    ├─ controller/           # 控制器（如AttendanceController.java）
  │    ├─ service/              # 业务逻辑（如AttendanceService.java）
  │    ├─ entity/               # 实体类（如Attendance.java）
  │    ├─ mapper/               # MyBatis接口
  │    └─ util/                 # 工具类（如JwtUtil.java）
  ├─ src/main/resources/mapper/ # MyBatis XML（如AttendanceMapper.xml）
  └─ ...                        # 其它配置
test_vue/                      # 前端Vue项目
  ├─ src/
  │    ├─ views/                # 页面组件
  │    ├─ api/                  # 接口请求
  │    └─ ...                   # 其它
  └─ ...
```

---

## 部署与运行

### 1. 数据库准备

- 创建MySQL数据库（如`kaoqin`），导入表结构和初始数据。
- 主要表：`emp`（员工）、`attendance`（考勤）、`emp_expr`（工作经历）等。

### 2. 后端启动

1. 配置`application.yml`中的数据库连接信息。
2. 使用IDEA或命令行运行Spring Boot主类（如`KaoqinApplication.java`）。
3. 后端服务默认端口：`8080`。

### 3. 前端启动

1. 进入`test_vue`目录，安装依赖：
   ```bash
   npm install
   ```
2. 启动前端开发服务器：
   ```bash
   npm run dev
   ```
3. 前端默认端口：`5173`（或见项目配置）。

---

## 典型代码说明

### 1. 后端考勤导出（含绩效分数）

`AttendanceController.java` 片段：

```java
// 表头
header.createCell(0).setCellValue("日期");
header.createCell(1).setCellValue("签到时间");
header.createCell(2).setCellValue("签退时间");
header.createCell(3).setCellValue("状态");
header.createCell(4).setCellValue("绩效分数");
// 数据
for (Attendance a : list) {
    // ... 其它字段
    row.createCell(4).setCellValue(a.getPerformanceScore() == null ? "" : a.getPerformanceScore().toString());
}
```

### 2. 前端请求示例

```js
// axios请求导出考勤报表
axios.get('/attendance/export', {
  params: { month: '2024-06' },
  responseType: 'blob',
  headers: { token: localStorage.getItem('token') }
}).then(res => {
  // 下载Excel
});
```

---

## 常见问题

1. **导出Excel没有绩效分数？**  
   请确保后端`AttendanceController`的导出方法有写入`绩效分数`这一列，且数据库有数据。

2. **token失效或401？**  
   重新登录获取新token，前端自动跳转登录页。

3. **字段不一致导致数据不显示？**  
   前后端字段名需与数据库一致，特别是考勤、员工、工作经历等表。

4. **如何扩展更多功能？**  
   可在`AttendanceService`、`AttendanceController`等处添加新接口，前端通过API调用。

---

## 联系与贡献

如有问题或建议，欢迎提issue或联系开发者。  
本项目欢迎二次开发和贡献！

