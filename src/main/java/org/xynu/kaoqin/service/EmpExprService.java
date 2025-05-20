package org.xynu.kaoqin.service;

import java.util.List;

import org.xynu.kaoqin.entity.EmpExpr;

public interface EmpExprService {
    List<EmpExpr> getByEmpId(Integer empId);
}
