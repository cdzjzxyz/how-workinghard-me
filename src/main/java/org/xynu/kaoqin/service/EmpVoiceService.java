package org.xynu.kaoqin.service;

import java.util.List;
import java.util.Map;

import org.xynu.kaoqin.entity.EmpVoice;

public interface EmpVoiceService {
    void postVoice(EmpVoice voice);
    List<EmpVoice> listVoices();
    Map<String, Object> pageList(int page, int pageSize, String keyword, Integer empId);
    void deleteById(Integer id, Integer empId);
    void comment(Integer voiceId, Integer empId, String content);
    List<Map<String, Object>> getComments(Integer voiceId);
    void like(Integer voiceId, Integer empId);
    void unlike(Integer voiceId, Integer empId);
    void deleteComment(Integer commentId, Integer empId);
    EmpVoice getVoiceById(Integer id);
}
