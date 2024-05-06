package com.wmy.study.DearIMProject.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.wmy.study.DearIMProject.Exception.BusinessException;
import com.wmy.study.DearIMProject.Socket.Message;
import com.wmy.study.DearIMProject.Socket.MessageType;

import java.util.List;


public interface IMessageService extends IService<Message> {
    void saveMessage(Message message);


    /**
     * 获取token下所有的个人聊天的离线消息
     *
     * @param token
     * @return
     */
    List<Message> getOfflinePersonalMessages(String token, Long timestamp);

    /**
     * 获取token下所有的群聊天消息
     *
     * @param token
     * @param timestamp
     * @return
     */
    List<Message> getOfflineGroupMessages(String token, Long timestamp);

    void setMessageReaded(Long msgId, Long userId);

    @Deprecated
    void setReaded(Long timestamp, Long userId);

    /**
     * 获取
     *
     * @param timestamp
     * @return
     */
    Message getMessageByTimestamp(Long timestamp, Long toId);

    boolean removeByTimestamp(Long timestamp);

    boolean recallByTimestamp(Long timestamp);


    void sendMessage(String fromUid, String toUid, String content, MessageType messageType) throws InterruptedException;

    void sendReadedMessage(Message message, Long fromId) throws BusinessException, JsonProcessingException;
}
