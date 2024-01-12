package com.wmy.study.DearIMProject.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wmy.study.DearIMProject.Socket.Message;
import com.wmy.study.DearIMProject.dao.IMessageDao;
import com.wmy.study.DearIMProject.domain.User;
import com.wmy.study.DearIMProject.service.IMessageService;
import com.wmy.study.DearIMProject.service.IUserService;
import jakarta.annotation.Resource;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class MessageServiceImpl extends ServiceImpl<IMessageDao, Message> implements IMessageService {
    @Resource
    private IUserService userService;
    @Resource
    private IMessageDao dao;

    @Override
    public void saveOfflineMessage(Message message) {
        message.setStatus(2);
        save(message);
    }

    @Override
    public void saveOnlineMessage(Message message) {
        message.setStatus(0);
        save(message);
    }

    @Override
    public List<Message> getOfflineMessages(String token) {
        User user = userService.getFromToken(token);
        QueryWrapper<Message> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 0);
        List<Message> list = list(wrapper);
        return list;
    }

    @Override
    public void setReaded(Long msgId) {
        UpdateWrapper<Message> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("status", 1);
        update(updateWrapper);
    }
}
