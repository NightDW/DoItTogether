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

    //插入一条数据
    int insertMessage(Message message);

    //根据id删除数据
    int deleteMessageById(Integer id);

    int deleteMessagesBetween(Integer id1, Integer id2);

    int deleteMessagesByGroupId(Integer gid);

    //修改群留言的发送者，在用户退群时需要用到
    int updateGroupMessageSenderId(@Param("oid") Integer oldId, @Param("nid") Integer newId, @Param("gid") Integer groupId);

    //查询某个群内的所有留言
    List<Message> selectMessagesByGroupId(Integer gid);

    //查询某两个人之间的私聊
    List<Message> selectMessagesBetween(Integer id1, Integer id2);
}
