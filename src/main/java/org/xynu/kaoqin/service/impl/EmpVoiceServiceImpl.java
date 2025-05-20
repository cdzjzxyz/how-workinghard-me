package org.xynu.kaoqin.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xynu.kaoqin.entity.EmpVoice;
import org.xynu.kaoqin.entity.EmpVoiceComment;
import org.xynu.kaoqin.mapper.EmpMapper;
import org.xynu.kaoqin.mapper.EmpVoiceMapper;
import org.xynu.kaoqin.service.EmpVoiceService;

@Service
public class EmpVoiceServiceImpl implements EmpVoiceService {
    @Autowired
    private EmpVoiceMapper empVoiceMapper;
    @Autowired
    private EmpMapper empMapper;

    @Override
    public void postVoice(EmpVoice voice) {
        empVoiceMapper.insert(voice);
    }

    @Override
    public List<EmpVoice> listVoices() {
        return empVoiceMapper.selectAll();
    }

    @Override
    public Map<String, Object> pageList(int page, int pageSize, String keyword, Integer empId) {
        int offset = (page - 1) * pageSize;
        List<Map<String, Object>> list = empVoiceMapper.selectPage(offset, pageSize, keyword);
        int total = empVoiceMapper.countPage(keyword);
        
        // 查询当前用户是否点赞
        for (Map<String, Object> item : list) {
            // 修改这里：安全地获取id并转换为Integer
            Integer voiceId = item.get("id") == null ? null : ((Number) item.get("id")).intValue();
            item.put("hasLiked", empId != null && empVoiceMapper.hasLiked(voiceId, empId));
            item.put("comments", empVoiceMapper.selectComments(voiceId));
            
            // 如果likeCount是Long类型，也安全转换
            Object likeCount = item.get("likeCount");
            if (likeCount != null) {
                item.put("likeCount", ((Number) likeCount).intValue());
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("rows", list);
        return result;
    }

    @Override
    public void deleteById(Integer id, Integer empId) {
        EmpVoice voice = empVoiceMapper.selectById(id);
        if (voice == null) throw new RuntimeException("帖子不存在");
        if (!voice.getEmpId().equals(empId)) {
            throw new RuntimeException("无权限删除");
        }
        empVoiceMapper.deleteById(id);
    }

    @Override
    public void comment(Integer voiceId, Integer empId, String content) {
        empVoiceMapper.insertComment(voiceId, empId, content);
    }

    @Override
    public List<Map<String, Object>> getComments(Integer voiceId) {
        return empVoiceMapper.selectComments(voiceId);
    }

    @Override
    public void like(Integer voiceId, Integer empId) {
        empVoiceMapper.insertLike(voiceId, empId);
    }

    @Override
    public void unlike(Integer voiceId, Integer empId) {
        empVoiceMapper.deleteLike(voiceId, empId);
    }

    @Override
    public void deleteComment(Integer commentId, Integer empId) {
        EmpVoiceComment comment = empVoiceMapper.selectCommentById(commentId);
        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }
        if (!comment.getEmpId().equals(empId)) {
            throw new RuntimeException("无权限删除此评论");
        }
        empVoiceMapper.deleteCommentById(commentId);
    }

    @Override
    public EmpVoice getVoiceById(Integer id) {
        return empVoiceMapper.selectById(id);
    }
}
