package com.wmy.study.DearIMProject.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.wmy.study.DearIMProject.Exception.BusinessException;
import com.wmy.study.DearIMProject.Socket.Message;
import com.wmy.study.DearIMProject.Socket.MessageType;

import java.util.List;


public interface IMessageService extends IService<Message> {
    void saveOfflineMessage(Message message);

    void saveOnlineMessage(Message message);

    /**
     * 获取token下所有的离线消息
     *
     * @param token
     * @return
     */
    List<Message> getOfflineMessages(String token, Long timestamp);

    void setReaded(Long timestamp);

    /**
     * 获取
     *
     * @param timestamp
     * @return
     */
    Message getMessageByTimestamp(Long timestamp);

    boolean removeByTimestamp(Long timestamp);

    boolean recallByTimestamp(Long timestamp);


    void sendMessage(String fromUid, String toUid, String content, MessageType messageType) throws InterruptedException;

    void sendReadedMessage(Long timestamp) throws BusinessException;
}
