## 主要功能模块

### 1. 首页 (Dashboard)
* **数据概览**：以图表形式（如饼图、条形图）展示关键指标，例如：
    * 公司总人数、部门数量。
    * 员工男女比例。
    * 各部门人数分布。
* **公告栏**：显示最新的公司通知或重要信息。
* **最新入职员工**：快速查看近期加入公司的新成员。
* **快捷导航**：提供常用功能的快速入口。
* **AI 智能考勤分析**：(推测功能) 可能包含对整体考勤数据的趋势分析、异常预警等。

### 2. 系统信息管理
* **部门管理 (Department Management)**：
    * 支持对部门信息进行增、删、改、查操作。
    * 显示部门列表及相关信息（如部门名称、负责人、创建/更新时间等）。
* **员工管理 (Employee Management)**：
    * 支持对员工档案信息进行全面的增、删、改、查。
    * 员工信息字段可能包括：工号、姓名、性别、联系方式（手机号）、所属部门、职位、薪资、入职日期、员工照片/头像、个人简介、紧急联系人等。
    * 提供员工信息的批量导入、导出功能。
* **考勤管理 (Attendance Management)**：
    * **上下班打卡**：员工可以进行每日的上班和下班打卡操作，系统记录打卡时间、地点（若有）、设备信息（若有）。
    * **考勤日历视图**：以日历的形式清晰展示员工每日的打卡状态，例如：正常、迟到、早退、缺勤（未打卡）、请假、外出等。不同状态以不同颜色或图标标记。
    * **考勤记录查询**：员工和管理员可以查询指定时间范围内的详细考勤记录。
    * **考勤报表与统计**：
        * 生成月度/自定义时间范围的考勤报表。
        * 统计出勤天数、迟到次数、早退次数、缺勤次数、请假天数等。
        * **绩效分数**：根据预设的考勤规则（如迟到、早退、缺勤的扣分标准）自动计算员工的考勤绩效分数。
        * 支持将考勤报表导出为 Excel 文件，方便存档和进一步分析。

### 3. 个人中心 (Personal Center)
* **个人信息查看与编辑**：员工可以查看自己的详细个人信息，并修改允许修改的部分（如联系方式、密码等）。
* **我的考勤**：员工可以快速查看自己的当月或近期的考勤记录和统计数据。
* **请假申请 (Leave Application)**：
    * 员工可以在线提交请假申请，选择请假类型、起止时间、事由等。
    * 可能包含请假审批流程的跟踪。

## 技术栈

### 前端 (test_vue)
* **核心框架**: Vue.js (根据 `package.json` 和目录结构推断为 Vue 3)
* **UI 组件库**: Element Plus (根据常见的 Vue 项目和功能描述推断)
* **HTTP 通信**: Axios (用于与后端 API 交互)
* **路由管理**: Vue Router
* **状态管理**: Vuex 或 Pinia (现代 Vue 项目常用)
* **数据可视化**: ECharts (用于首页的图表展示)
* **日期时间处理**: Day.js (根据 `node_modules` 目录下的 `dayjs` 推断)
* **构建工具**: Vue CLI / Vite

### 后端 (kaoqin)
* **核心框架**: Spring Boot (版本根据 `pom.xml` 分析，如 Spring Boot 3.x)
* **持久层框架**: MyBatis
* **数据库**: MySQL (根据 `application.yml` 中的配置推断)
* **项目构建与依赖管理**: Maven (根据 `pom.xml` 推断)
* **安全认证**: Spring Security 与 JWT (JSON Web Token) (常见的安全方案)
* **Excel 处理**: Apache POI (用于考勤报表的 Excel 导出)
* **工具库**: Lombok (简化 Java 代码)

## 关键代码逻辑解读 (示例)

### 后端：考勤记录与绩效计算
在 `AttendanceService` 或相关服务类中，可能存在以下逻辑：
* **打卡记录保存**：接收前端传递的打卡信息（员工ID、打卡时间），判断是上班卡还是下班卡，并存入数据库。
* **考勤状态判断**：
    * 根据预设的上下班时间规则（可能配置在系统参数或部门设置中），判断打卡记录是正常、迟到还是早退。
    * 每日定时任务（如 Quartz 或 Spring Task）检查当天未打上班卡或下班卡的员工，标记为缺勤。
* **绩效分数计算**：
    * 在生成考勤报表或特定触发条件下，遍历员工的考勤记录。
    * 根据考勤状态（迟到、早退、缺勤、请假等）和对应的扣分规则，计算绩效得分。例如，基础分为100，迟到一次扣2分，缺勤一次扣5分。
* **考勤报表导出**：
    * 使用 Apache POI 库。
    * 查询指定时间范围内的考勤数据和计算出的绩效分数。
    * 将数据填充到 Excel 模板或动态生成 Excel 工作簿和工作表。
    * 设置表头、单元格样式，并将生成的 Excel 文件以流的形式返回给前端。

```java
// AttendanceService.java (伪代码示例)
public class AttendanceService {
    // ... 依赖注入 attendanceMapper, empMapper, ruleConfig 等

    public void clockIn(Long empId) {
        // 记录上班打卡时间
        // 判断是否迟到
    }

    public void clockOut(Long empId) {
        // 记录下班打卡时间
        // 判断是否早退
    }

    public List<AttendanceRecord> getAttendanceRecords(Long empId, Date startDate, Date endDate) {
        // 查询考勤记录
    }

    public ExcelWorkbook exportAttendanceReport(Date month) {
        // 1. 查询该月所有员工的考勤记录
        // 2. 对每条记录计算考勤状态和绩效分数
        // 3. 使用 Apache POI 生成 Excel
    }

    public int calculatePerformanceScore(List<AttendanceRecord> records) {
        int score = 100; // 初始绩效分
        for (AttendanceRecord record : records) {
            if (record.isLate()) score -= 2;
            if (record.isEarlyLeave()) score -= 2;
            if (record.isAbsent()) score -= 5;
            if (record.isLeave()) score -= 1; // 假设请假扣1分
        }
        return Math.max(0, score); // 最低为0分
    }
}
前端：考勤日历展示与打卡
在 Vue 组件中 (例如 AttendanceCalendar.vue):

获取考勤数据：通过 axios 调用后端接口，获取指定月份的员工考勤数据。
日历渲染：
使用 Element Plus 的 el-calendar 组件或自定义日历组件。
遍历日历的每一天，根据从后端获取的考勤数据，判断当天是否有打卡记录以及打卡状态。
使用不同的样式或图标在日历上标记不同的考勤状态 (如绿色代表正常，黄色代表迟到/早退，红色代表缺勤，蓝色代表请假)。
打卡操作：
提供“上班打卡”和“下班打卡”按钮。
点击按钮时，调用后端打卡接口，并根据返回结果给出提示。
成功打卡后，可能需要刷新日历或考勤列表。
<!-- end list -->

代码段

<template>
  <div>
    <el-calendar v-model="currentDate">
      <template #dateCell="{ data }">
        <p :class="data.isSelected ? 'is-selected' : ''">
          {{ data.day.split('-').slice(1).join('-') }}
          <span v-if="getAttendanceStatus(data.day)" :class="getStatusClass(data.day)">
            {{ getAttendanceStatus(data.day) }}
          </span>
        </p>
      </template>
    </el-calendar>
    <el-button @click="handleClockIn">上班打卡</el-button>
    <el-button @click="handleClockOut">下班打卡</el-button>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios'; // 假设API封装在store或单独的api.js中
import dayjs from 'dayjs';

const currentDate = ref(new Date());
const attendanceData = ref({}); // { 'YYYY-MM-DD': '状态' }

// 获取考勤数据
async function fetchAttendanceData(month) {
  // const response = await axios.get(`/api/attendance/month/${month}`);
  // attendanceData.value = processData(response.data);
}

// 处理考勤数据以便在日历上显示
function getAttendanceStatus(dateStr) {
  return attendanceData.value[dateStr];
}

function getStatusClass(dateStr) {
  const status = attendanceData.value[dateStr];
  if (status === '正常') return 'status-normal';
  if (status === '迟到' || status === '早退') return 'status-warning';
  if (status === '缺勤') return 'status-error';
  return '';
}

async function handleClockIn() {
  // await axios.post('/api/attendance/clock-in');
  // 提示成功，并刷新数据
}

async function handleClockOut() {
  // await axios.post('/api/attendance/clock-out');
  // 提示成功，并刷新数据
}

onMounted(() => {
  fetchAttendanceData(dayjs(currentDate.value).format('YYYY-MM'));
});
</script>

<style scoped>
.status-normal { color: green; }
.status-warning { color: orange; }
.status-error { color: red; }
</style>
部署与运行
1. 环境要求
后端:
JDK 17+ (或根据 pom.xml 中 java.version 配置)
Maven 3.6+
MySQL 8.0+
前端:
Node.js (推荐 LTS 版本)
npm 或 yarn
2. 数据库设置
创建数据库: 在 MySQL 中创建一个数据库，例如 employee_attendance_db。
执行 SQL 脚本: 找到项目中的 SQL 初始化脚本 (通常在后端项目的 src/main/resources 目录下或项目根目录的 sql 文件夹下)，并在创建的数据库中执行，以创建必要的表和初始化数据。
配置数据库连接:
打开后端项目 (kaoqin) 中的 src/main/resources/application.yml (或 .properties) 文件。
修改 spring.datasource.url, spring.datasource.username, 和 spring.datasource.password 以匹配您的 MySQL 配置。
3. 后端启动
打开终端或 IDE 的 Maven 工具窗口。
导航到后端项目根目录 (kaoqin)。
编译并运行项目:
使用 Maven 命令: mvn spring-boot:run
或者，如果您使用 IDE (如 IntelliJ IDEA, Eclipse)，可以直接运行 Spring Boot 的主应用程序类 (通常带有 @SpringBootApplication 注解，例如 KaoqinApplication.java)。
后端服务默认会启动在 8080 端口 (可以在 application.yml 中修改 server.port)。
4. 前端启动
打开新的终端窗口。
导航到前端项目根目录 (test_vue)。
安装项目依赖:
Bash

npm install
# 或者
# yarn install
启动开发服务器:
Bash

npm run serve
# 或者
# yarn serve
前端开发服务器通常会启动在 8081 或 8086 等端口 (具体端口可以在 vue.config.js 或终端输出中查看)。
在浏览器中打开显示的地址 (例如 http://localhost:8081) 即可访问系统。
5. 构建生产环境包 (可选)
前端: 在 test_vue 目录下运行 npm run build (或 yarn build)，生成的静态文件会存放在 dist 目录中。之后可以将这些文件部署到 Nginx 或其他 Web 服务器。
后端: 在 kaoqin 目录下运行 mvn package，会在 target 目录下生成一个可执行的 .jar 文件，可以直接通过 java -jar xxx.jar 运行。
注意事项
确保后端服务先于前端服务启动，因为前端需要调用后端的 API。
如果遇到跨域问题 (CORS)，请检查后端 Spring Boot 是否配置了相应的跨域处理 (通常在 WebMvcConfigurer 中配置)。
根据实际部署环境，可能需要修改前端 API 请求的基础 URL (通常在 src/utils/request.js 或类似文件中配置)。
<!-- end list -->