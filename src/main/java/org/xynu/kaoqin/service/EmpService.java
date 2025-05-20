package org.xynu.kaoqin.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

import org.xynu.kaoqin.entity.Emp;
import org.xynu.kaoqin.entity.DeptCountVO;
import org.xynu.kaoqin.entity.PageResult;

public interface EmpService {
    Map<String, Object> pageList(String name, Integer gender, LocalDate begin, LocalDate end, Integer page, Integer pageSize);
    void add(Emp emp);
    void update(Emp emp);
    void delete(List<Integer> ids);
    Emp getById(Integer id);
    List<Emp> listAll();
    void updateProfile(Emp emp);
    boolean changePassword(Integer userId, String oldPassword, String newPassword);
    int countAll();
    int countByGender(int gender);
    List<Emp> findLatest(int limit);
    List<DeptCountVO> countByDept();
    PageResult getEmps(String name, Integer gender, String begin, String end, Integer page, Integer pageSize, String sortField, String sortOrder);
}