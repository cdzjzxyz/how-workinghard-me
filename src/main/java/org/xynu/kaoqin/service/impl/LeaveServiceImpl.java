package org.xynu.kaoqin.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xynu.kaoqin.entity.LeaveRecord;
import org.xynu.kaoqin.mapper.LeaveRecordMapper;
import org.xynu.kaoqin.service.LeaveService;

@Service
public class LeaveServiceImpl implements LeaveService {
    @Autowired
    private LeaveRecordMapper leaveRecordMapper;

    @Override
    public void applyLeave(LeaveRecord leave) {
        leaveRecordMapper.insertLeave(leave);
    }

    @Override
    public List<LeaveRecord> getMyLeaves(Integer empId) {
        return leaveRecordMapper.selectByEmpId(empId);
    }

    @Override
    public List<LeaveRecord> getAllLeaves() {
        return leaveRecordMapper.selectAll();
    }

    @Override
    public void approveLeave(Integer id, Integer status) {
        leaveRecordMapper.updateStatus(id, status);
    }

    @Override
    public LeaveRecord getLeaveRecordById(Integer id) {
        return leaveRecordMapper.selectById(id);
    }
}
