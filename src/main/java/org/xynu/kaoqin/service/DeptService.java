package org.xynu.kaoqin.service;

import java.util.List;

import org.xynu.kaoqin.entity.Dept;

public interface DeptService {
    List<Dept> list();
    Dept getById(Integer id);
    void add(Dept dept);
    void update(Dept dept);
    void delete(Integer id);
    int countAll();
} 