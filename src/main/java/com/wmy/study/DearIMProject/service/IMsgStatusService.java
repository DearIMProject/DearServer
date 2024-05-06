package com.wmy.study.DearIMProject.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wmy.study.DearIMProject.Socket.Message;
import com.wmy.study.DearIMProject.domain.MsgStatus;

import java.util.List;

public interface IMsgStatusService extends IService<MsgStatus> {
    /**
     * 获取msg消息所有的userId列表
     *
     * @param msgId 消息id
     * @return userId列表
     */
    List<Long> getMessageReaded(Long msgId);
}
