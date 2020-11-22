package com.laidw.mapper;

import com.laidw.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 针对Message的增删改查操作
 */
@Mapper
public interface MessageMapper {

    /**
     * 插入方法
     * @param message 插入的数据
     * @return 受影响的行数
     */
    int insertMessage(Message message);

    /**
     * 根据id删除记录
     * @param id 记录的id
     * @return 受影响的行数
     */
    int deleteMessageById(Integer id);

    /**
     * 删除两个人之间的私聊信息
     * @param id1 用户的id
     * @param id2 另一个用户的id
     * @return 受影响的行数
     */
    int deletePrivateMessagesBetween(Integer id1, Integer id2);

    /**
     * 删除群组内的所有信息
     * @param gid 群组的id
     * @return 受影响的行数
     */
    int deleteGroupMessagesByGroupId(Integer gid);

    /**
     * 修改群留言的发送者，在用户退群时需要用到
     * 用户退群时，他发送的消息的发送者将会改为一个叫做"已退群用户"的用户
     * @param oldId 被替换的发送者的Membership的id
     * @param newId 新的Membership的id
     * @param groupId 群组的id
     * @return 受影响的行数
     */
    int updateGroupMessageSenderId(@Param("oid") Integer oldId, @Param("nid") Integer newId, @Param("gid") Integer groupId);

    /**
     * 根据消息的id查询记录
     * @param id 消息的id
     * @return 查询结果
     */
    Message selectMessageById(Integer id);

    /**
     * 查询某个群内的所有留言
     * @param gid 群组的id
     * @return 查询结果
     */
    List<Message> selectGroupMessagesByGroupId(Integer gid);

    /**
     * 查询某两个人之间的私聊
     * @param id1 用户的id
     * @param id2 另一个用户的id
     * @return 查询结果
     */
    List<Message> selectPrivateMessagesBetween(Integer id1, Integer id2);
}
