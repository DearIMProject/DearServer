package com.wmy.study.DearIMProject.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.wmy.study.DearIMProject.Socket.Message;
import com.wmy.study.DearIMProject.domain.Group;

import java.util.List;

public interface IGroupMessageService extends IService<Message> {
    /**
     * 发送群组添加消息
     *
     * @param userIds 用户id数组
     * @param groupId 群组id
     */
    void sendAddGroupMessage(List<Long> userIds, Long groupId) throws JsonProcessingException;

    /**
     * 发送群组更新信息
     *
     * @param groupId 群组id
     */
    void sendGroupUpdateMessage(Long groupId) throws JsonProcessingException;

    /**
     * 发送消息
     *
     * @param message 消息
     */
    void sendMessage(Message message);

    /**
     * 发送群组删除消息
     *
     * @param group 群组id
     */
    void sendGroupDeleteMessage(Group group) throws JsonProcessingException;

    /**
     * 发送群组删除消息
     *
     * @param userId  用户id
     * @param groupId 群组id
     */
    void sendGroupDeleteMessage(Long userId, Long groupId) throws JsonProcessingException;
}
