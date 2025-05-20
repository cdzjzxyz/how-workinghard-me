package org.xynu.kaoqin.service.impl;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xynu.kaoqin.entity.DeptCountVO;
import org.xynu.kaoqin.entity.Emp;
import org.xynu.kaoqin.entity.EmpExpr;
import org.xynu.kaoqin.entity.PageResult;
import org.xynu.kaoqin.mapper.EmpExprMapper;
import org.xynu.kaoqin.mapper.EmpMapper;
import org.xynu.kaoqin.service.EmpService;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@Service
public class EmpServiceImpl implements EmpService {

    @Autowired
    private EmpMapper empMapper;
    @Autowired
    private EmpExprMapper empExprMapper;

    @Override
    public Map<String, Object> pageList(String name, Integer gender, LocalDate begin, LocalDate end, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        List<Emp> emps = empMapper.selectByPage(name, gender, begin, end);
        for (Emp emp : emps) {
            emp.setExprList(empExprMapper.selectByEmpId(emp.getId()));
        }
        PageInfo<Emp> pageInfo = new PageInfo<>(emps);
        Map<String, Object> result = new HashMap<>();
        result.put("total", pageInfo.getTotal());
        result.put("rows", pageInfo.getList());
        return result;
    }

    @Override
    public void add(Emp emp) {
        empMapper.insert(emp);
        if (emp.getExprList() != null && !emp.getExprList().isEmpty()) {
            for (EmpExpr expr : emp.getExprList()) {
                expr.setEmpId(emp.getId());
            }
            empExprMapper.insertBatch(emp.getExprList());
        }
    }

    @Override
    public void update(Emp emp) {
        empMapper.update(emp);
        empExprMapper.deleteByEmpId(emp.getId());
        if (emp.getExprList() != null && !emp.getExprList().isEmpty()) {
            for (EmpExpr expr : emp.getExprList()) {
                expr.setEmpId(emp.getId());
            }
            empExprMapper.insertBatch(emp.getExprList());
        }
    }

    @Override
    public void delete(List<Integer> ids) {
        for (Integer id : ids) {
            empExprMapper.deleteByEmpId(id);
        }
        empMapper.deleteByIds(ids);
    }

    @Override
    public Emp getById(Integer id) {
        Emp emp = empMapper.selectById(id);
        if (emp != null) {
            emp.setExprList(empExprMapper.selectByEmpId(id));
        }
        return emp;
    }

    @Override
    public List<Emp> listAll() {
        return empMapper.selectAll();
    }

    @Override
    public void updateProfile(Emp emp) {
        // 只允许改 name, gender, phone, image
        Emp dbEmp = empMapper.selectById(emp.getId());
        dbEmp.setName(emp.getName());
        dbEmp.setGender(emp.getGender());
        dbEmp.setPhone(emp.getPhone());
        dbEmp.setImage(emp.getImage());
        empMapper.update(dbEmp);
    }

    @Override
    public boolean changePassword(Integer userId, String oldPassword, String newPassword) {
        Emp emp = empMapper.selectById(userId);
        if (emp != null && emp.getPassword().equals(oldPassword)) {
            emp.setPassword(newPassword);
            empMapper.update(emp);
            return true;
        }
        return false;
    }

    @Override
    public int countAll() { return empMapper.countAll(); }

    @Override
    public int countByGender(int gender) { return empMapper.countByGender(gender); }

    @Override
    public List<Emp> findLatest(int limit) { return empMapper.findLatest(limit); }

    @Override
    public List<DeptCountVO> countByDept() { return empMapper.countByDept(); }

    @Override
    public PageResult<Emp> getEmps(String name, Integer gender, String begin, String end, Integer page, Integer pageSize, String sortField, String sortOrder) {
        // 字段映射，防止SQL注入
        Map<String, String> sortFieldMap = new HashMap<>();
        sortFieldMap.put("id", "id");
        sortFieldMap.put("salary", "salary");
        sortFieldMap.put("entryDate", "entry_date");
        String realSortField = sortFieldMap.getOrDefault(sortField, "id");

        int offset = (page - 1) * pageSize;
        List<Emp> rows = empMapper.selectEmps(name, gender, begin, end, offset, pageSize, realSortField, sortOrder);
        Long total = empMapper.countEmps(name, gender, begin, end);
        return new PageResult<>(total, rows);
    }
}