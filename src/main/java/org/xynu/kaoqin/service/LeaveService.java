package org.xynu.kaoqin.service;

import java.util.List;

import org.xynu.kaoqin.entity.LeaveRecord;

public interface LeaveService {
    void applyLeave(LeaveRecord leave);
    List<LeaveRecord> getMyLeaves(Integer empId);
    List<LeaveRecord> getAllLeaves();
    void approveLeave(Integer id, Integer status);
    LeaveRecord getLeaveRecordById(Integer id);
}
