package com.iohw.knobot.chat.mapper;

import com.iohw.knobot.chat.model.ChatMessageDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author: iohw
 * @date: 2025/4/13 11:15
 * @description:
 */
@Mapper
public interface ChatMessageMapper {
    void insert(@Param("record") ChatMessageDO chatMessageDO);
    // 批量插入聊天记录
    int batchInsert(@Param("records") List<ChatMessageDO> records);

    // 根据 memoryId 查询聊天记录，按时间正序排列
    List<ChatMessageDO> selectByMemoryId(@Param("memoryId") String memoryId);

    // 根据 memoryId 查询最近的 N 条聊天记录
    List<ChatMessageDO> selectRecentByMemoryId(@Param("memoryId") String memoryId, @Param("limit") int limit);

    // 删除指定 memoryId 的聊天记录
    int deleteByMemoryId(@Param("memoryId") String memoryId);

    // 统计指定 memoryId 的聊天记录总 token 数
    int sumTokensByMemoryId(@Param("memoryId") String memoryId);

    /**
     * 根据会话ID和角色查询消息
     * @param memoryId 会话ID
     * @param role 角色（user/assistant）
     * @return 消息列表
     */
    @Select("SELECT * FROM chat_message WHERE memory_id = #{memoryId} AND role = #{role}")
    List<ChatMessageDO> selectByMemoryIdAndRole(@Param("memoryId") String memoryId, @Param("role") String role);
}
