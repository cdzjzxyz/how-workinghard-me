package org.xynu.kaoqin.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.xynu.kaoqin.entity.EmpExpr;

@Mapper
public interface EmpExprMapper {
    List<EmpExpr> selectByEmpId(Integer empId);
    int deleteByEmpId(Integer empId);
    int insertBatch(@Param("exprList") List<EmpExpr> exprList);
}