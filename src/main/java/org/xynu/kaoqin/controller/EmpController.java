package org.xynu.kaoqin.controller;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.xynu.kaoqin.common.Result;
import org.xynu.kaoqin.entity.DeptCountVO;
import org.xynu.kaoqin.entity.Emp;
import org.xynu.kaoqin.entity.EmpExpr;
import org.xynu.kaoqin.entity.PageResult;
import org.xynu.kaoqin.service.DeptService;
import org.xynu.kaoqin.service.EmpExprService;
import org.xynu.kaoqin.service.EmpService;
import org.xynu.kaoqin.util.JwtUtil;

@RestController
@RequestMapping("/emps")
public class EmpController {

    @Autowired
    private EmpService empService;

    @Autowired
    private EmpExprService empExprService;

    @Autowired
    private DeptService deptService;

    // 分页+条件查询
    @GetMapping
    public Result getEmps(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer gender,
            @RequestParam(required = false) String begin,
            @RequestParam(required = false) String end,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) String sortOrder
    ) {
        PageResult<Emp> pageResult = empService.getEmps(name, gender, begin, end, page, pageSize, sortField, sortOrder);
        return Result.success(pageResult);
    }

    // 批量删除
    @DeleteMapping
    public Result<Void> delete(@RequestParam String ids, @RequestHeader("token") String token) {
        Integer empId = JwtUtil.getUserId(token);
        Emp emp = empService.getById(empId);
        if (emp == null || !"admin".equals(emp.getUsername())) {
            return Result.fail("无权限操作");
        }
        List<Integer> idList = Arrays.stream(ids.split(",")).map(Integer::parseInt).toList();
        empService.delete(idList);
        return Result.success(null);
    }

    // 添加
    @PostMapping
    public Result<Void> add(@RequestBody Emp emp, @RequestHeader("token") String token) {
        Integer empId = JwtUtil.getUserId(token);
        Emp currentEmp = empService.getById(empId);
        if (currentEmp == null || !"admin".equals(currentEmp.getUsername())) {
            return Result.fail("无权限操作");
        }
        empService.add(emp);
        return Result.success(null);
    }

    // 根据ID查询
    @GetMapping("/{id}")
    public Result<Emp> getById(@PathVariable Integer id) {
        return Result.success(empService.getById(id));
    }

    // 修改
    @PutMapping
    public Result<Emp> update(@RequestBody Emp emp, @RequestHeader("token") String token) {
        Integer empId = JwtUtil.getUserId(token);
        Emp currentEmp = empService.getById(empId);
        if (currentEmp == null || !"admin".equals(currentEmp.getUsername())) {
            return Result.fail("无权限操作");
        }
        empService.update(emp);
        // 重新查一遍，带deptName
        Emp updatedEmp = empService.getById(emp.getId());
        return Result.success(updatedEmp);
    }

    // 查询全部
    @GetMapping("/list")
    public Result<List<Emp>> listAll() {
        return Result.success(empService.listAll());
    }

    // 获取当前登录用户信息
    @GetMapping("/profile")
    public Result getProfile(@RequestHeader("token") String token) {
        Integer empId = JwtUtil.getUserId(token);
        Emp emp = empService.getById(empId);
        List<EmpExpr> exprList = empExprService.getByEmpId(empId);

        Map<String, Object> data = new HashMap<>();
        data.put("id", emp.getId());
        data.put("username", emp.getUsername());
        data.put("name", emp.getName());
        data.put("gender", emp.getGender());
        data.put("phone", emp.getPhone());
        data.put("job", emp.getJob());
        data.put("salary", emp.getSalary());
        data.put("image", emp.getImage());
        data.put("entryDate", emp.getEntryDate());
        data.put("deptId", emp.getDeptId());
        data.put("deptName", emp.getDeptName());
        data.put("updateTime", emp.getUpdateTime());
        data.put("exprList", exprList);

        return Result.success(data);
    }

    // 修改当前登录用户信息
    @PutMapping("/profile")
    public Result<Void> updateProfile(@RequestHeader("token") String token, @RequestBody Emp emp) {
        Integer userId = JwtUtil.getUserId(token);
        emp.setId(userId);
        empService.updateProfile(emp);
        return Result.success(null);
    }

    // 修改密码
    @PostMapping("/change-password")
    public Result<Void> changePassword(@RequestHeader("token") String token, @RequestBody Map<String, String> map) {
        Integer userId = JwtUtil.getUserId(token);
        String oldPassword = map.get("oldPassword");
        String newPassword = map.get("newPassword");
        boolean ok = empService.changePassword(userId, oldPassword, newPassword);
        if (ok) return Result.success(null);
        else return Result.fail("原密码错误");
    }

    @GetMapping("/stats")
    public Map<String, Object> getEmpStats() {
        int empCount = empService.countAll();
        int maleCount = empService.countByGender(1);
        int femaleCount = empService.countByGender(2);
        List<Emp> latestEmps = empService.findLatest(5);
        List<DeptCountVO> deptCounts = empService.countByDept();
        int deptCount = deptService.countAll();

        Map<String, Object> data = new HashMap<>();
        data.put("empCount", empCount);
        data.put("maleCount", maleCount);
        data.put("femaleCount", femaleCount);
        data.put("latestEmps", latestEmps);
        data.put("deptCounts", deptCounts);
        data.put("deptCount", deptCount);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 1);
        result.put("data", data);
        return result;
    }

    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        // 1. 保存文件到服务器指定目录
        // 2. 返回图片访问URL
        // 示例代码如下（你可根据实际情况调整）：
        if (file.isEmpty()) {
            return Result.fail("上传文件为空");
        }
        try {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            String uploadDir = "D:/upload/"; // Windows下建议用绝对路径
            File dest = new File(uploadDir + fileName);
            file.transferTo(dest);
            String url = "/upload/" + fileName; // 你实际的访问路径
            return Result.success(url);
        } catch (Exception e) {
            return Result.fail("上传失败: " + e.getMessage());
        }
    }
}