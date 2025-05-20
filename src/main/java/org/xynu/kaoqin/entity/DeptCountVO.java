package org.xynu.kaoqin.entity;

public class DeptCountVO {
    private String deptName;
    private Integer count;

    // 构造方法、getter、setter
    public DeptCountVO() {}
    public DeptCountVO(String deptName, Integer count) {
        this.deptName = deptName;
        this.count = count;
    }
    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }
    public Integer getCount() { return count; }
    public void setCount(Integer count) { this.count = count; }
}
