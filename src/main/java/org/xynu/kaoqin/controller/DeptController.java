package org.xynu.kaoqin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xynu.kaoqin.common.Result;
import org.xynu.kaoqin.entity.Dept;
import org.xynu.kaoqin.entity.Emp;
import org.xynu.kaoqin.mapper.EmpMapper;
import org.xynu.kaoqin.service.DeptService;
import org.xynu.kaoqin.util.JwtUtil;

@RestController
@RequestMapping("/depts")
public class DeptController {

    @Autowired
    private DeptService deptService;

    @Autowired
    private EmpMapper empMapper;

    // 部门列表查询
    @GetMapping
    public Result<List<Dept>> list() {
        return Result.success(deptService.list());
    }

    // 删除部门
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id, @RequestHeader("token") String token) {
        Integer empId = JwtUtil.getUserId(token);
        Emp emp = empMapper.selectById(empId);
        if (emp == null || !"admin".equals(emp.getUsername())) {
            return Result.fail("无权限操作");
        }
        deptService.delete(id);
        return Result.success(null);
    }

    // 添加部门
    @PostMapping
    public Result<Void> add(@RequestBody Dept dept, @RequestHeader("token") String token) {
        Integer empId = JwtUtil.getUserId(token);
        Emp emp = empMapper.selectById(empId);
        if (emp == null || !"admin".equals(emp.getUsername())) {
            return Result.fail("无权限操作");
        }
        deptService.add(dept);
        return Result.success(null);
    }

    // 根据ID查询
    @GetMapping("/{id}")
    public Result<Dept> getById(@PathVariable Integer id) {
        return Result.success(deptService.getById(id));
    }

    // 修改部门
    @PutMapping
    public Result<Void> update(@RequestBody Dept dept, @RequestHeader("token") String token) {
        Integer empId = JwtUtil.getUserId(token);
        Emp emp = empMapper.selectById(empId);
        if (emp == null || !"admin".equals(emp.getUsername())) {
            return Result.fail("无权限操作");
        }
        deptService.update(dept);
        return Result.success(null);
    }
} 