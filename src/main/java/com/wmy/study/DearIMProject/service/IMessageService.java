package com.wmy.study.DearIMProject.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wmy.study.DearIMProject.Socket.Message;

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
}
