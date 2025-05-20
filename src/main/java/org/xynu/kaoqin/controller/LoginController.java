package org.xynu.kaoqin.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.xynu.kaoqin.common.Result;
import org.xynu.kaoqin.entity.Emp;
import org.xynu.kaoqin.entity.RegisterDTO;
import org.xynu.kaoqin.mapper.EmpMapper;
import org.xynu.kaoqin.util.JwtUtil;

@RestController
public class LoginController {

    @Autowired
    private EmpMapper empMapper;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> loginForm) {
        String username = loginForm.get("username");
        String password = loginForm.get("password");
        Emp emp = empMapper.selectByUsername(username);
        if (emp == null || !emp.getPassword().equals(password)) {
            return Result.fail("用户名或密码错误");
        }
        // 生成JWT
        String token = JwtUtil.createToken(emp.getId(), emp.getUsername());
        Map<String, Object> data = new HashMap<>();
        data.put("id", emp.getId());
        data.put("username", emp.getUsername());
        data.put("name", emp.getName());
        data.put("token", token);
        return Result.success(data);
    }

    @PostMapping("/register")
    public Result register(@RequestBody RegisterDTO dto) {
        // 1. 检查用户名是否已存在
        Emp existEmp = empMapper.selectByUsername(dto.getUsername());
        if (existEmp != null) {
            return Result.fail("用户名已存在");
        }

        // 2. 创建新用户
        Emp emp = new Emp();
        emp.setUsername(dto.getUsername());
        emp.setPassword(dto.getPassword());
        emp.setName(dto.getName());
        emp.setGender(1); // 默认性别为男
        emp.setPhone(""); // 默认空手机号
        emp.setJob(5); // 默认职位为普通员工
        emp.setEntryDate(LocalDate.now()); // 设置入职日期为当前日期
        emp.setCreateTime(LocalDateTime.now());
        emp.setUpdateTime(LocalDateTime.now());

        // 3. 保存到数据库
        empMapper.insert(emp);
        return Result.success("注册成功");
    }
}