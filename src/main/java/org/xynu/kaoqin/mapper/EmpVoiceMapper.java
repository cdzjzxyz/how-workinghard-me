package org.xynu.kaoqin.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.xynu.kaoqin.entity.EmpVoice;
import org.xynu.kaoqin.entity.EmpVoiceComment;

@Mapper
public interface EmpVoiceMapper {
    int insert(EmpVoice voice);
    List<EmpVoice> selectAll();
    List<Map<String, Object>> selectPage(@Param("offset") int offset, @Param("pageSize") int pageSize, @Param("keyword") String keyword);
    int countPage(@Param("keyword") String keyword);

    // 删除
    int deleteById(@Param("id") Integer id);

    // 评论
    int insertComment(@Param("voiceId") Integer voiceId, @Param("empId") Integer empId, @Param("content") String content);
    List<Map<String, Object>> selectComments(@Param("voiceId") Integer voiceId);
    EmpVoiceComment selectCommentById(@Param("id") Integer id);
    int deleteCommentById(@Param("id") Integer id);

    // 点赞
    int insertLike(@Param("voiceId") Integer voiceId, @Param("empId") Integer empId);
    int deleteLike(@Param("voiceId") Integer voiceId, @Param("empId") Integer empId);
    int countLikes(@Param("voiceId") Integer voiceId);
    boolean hasLiked(@Param("voiceId") Integer voiceId, @Param("empId") Integer empId);

    EmpVoice selectById(@Param("id") Integer id);
}
