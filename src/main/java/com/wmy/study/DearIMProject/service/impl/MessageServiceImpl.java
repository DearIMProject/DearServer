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
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageServiceImpl extends ServiceImpl<IMessageDao, Message> implements IMessageService {
    @Resource
    private IUserService userService;

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
        QueryWrapper<Message> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 2);
        queryWrapper.eq("to_id", user.getUserId());
        return list(queryWrapper);
    }

    @Override
    public void setReaded(Long msgId) {
        UpdateWrapper<Message> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("status", 1);
        update(updateWrapper);
    }
}
