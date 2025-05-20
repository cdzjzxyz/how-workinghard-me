package org.xynu.kaoqin.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xynu.kaoqin.entity.EmpExpr;
import org.xynu.kaoqin.mapper.EmpExprMapper;
import org.xynu.kaoqin.service.EmpExprService;

@Service
public class EmpExprServiceImpl implements EmpExprService {
    @Autowired
    private EmpExprMapper empExprMapper;

    @Override
    public List<EmpExpr> getByEmpId(Integer empId) {
        return empExprMapper.selectByEmpId(empId);
    }
}
