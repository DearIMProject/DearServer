package com.wmy.study.DearIMProject.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wmy.study.DearIMProject.Exception.BusinessException;
import com.wmy.study.DearIMProject.Socket.*;
import com.wmy.study.DearIMProject.Socket.message.MessageFactory;
import com.wmy.study.DearIMProject.dao.IMessageDao;
import com.wmy.study.DearIMProject.domain.User;
import com.wmy.study.DearIMProject.domain.UserToken;
import com.wmy.study.DearIMProject.service.IMessageService;
import com.wmy.study.DearIMProject.service.IUserService;
import com.wmy.study.DearIMProject.service.IUserTokenService;
import io.netty.channel.Channel;
import jakarta.annotation.Resource;
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
    @Resource
    private IUserTokenService tokenService;
    @Resource
    private UserTokenChannel userTokenChannel;

    @Override
    public void saveOfflineMessage(Message message) {
        message.setStatus(MessageStatus.STATUS_NOT_SEND_UNREAD);
        save(message);
    }

    @Override
    public void saveOnlineMessage(Message message) {
        message.setStatus(MessageStatus.STATUS_SUCCESS_UNREADED);
        save(message);
    }

    @Override
    public List<Message> getOfflineMessages(String token, Long timestamp) {
        User user = userService.getFromToken(token);
        QueryWrapper<Message> wrapper = new QueryWrapper<>();
        wrapper.gt("timestamp", timestamp);
        // 离线消息中发送方或接收方为自己的
        wrapper.and(i -> i.eq("from_id", user.getUserId()).or().eq("to_id", user.getUserId()));
        // 消息状态为所有未读的状态
//        wrapper.eq("status", 2).or().eq("status", 0);
        return list(wrapper);
    }

    @Override
    public void setReaded(Long timestamp) {
        UpdateWrapper<Message> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("status", 1);
        updateWrapper.eq("timestamp", timestamp);
        update(updateWrapper);
    }

    @Override
    public Message getMessageByTimestamp(Long timestamp) {
        QueryWrapper<Message> wrapper = new QueryWrapper<>();
        wrapper.eq("timestamp", timestamp);
        return getOne(wrapper);
    }

    @Override
    public boolean removeByTimestamp(Long timestamp) {
        UpdateWrapper<Message> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("timestamp", timestamp);
        updateWrapper.set("status", 3);
        return update(updateWrapper);
    }

    @Override
    public boolean recallByTimestamp(Long timestamp) {
        UpdateWrapper<Message> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("timestamp", timestamp);
        updateWrapper.set("status", 4);
        return update(updateWrapper);
    }

    @Override
    public void sendMessage(String fromUid, String toUid, String content, MessageType messageType) throws InterruptedException {
        Message message = getMessage(fromUid, toUid, content, MessageType.TEXT);
        QueryWrapper<UserToken> wrapper = new QueryWrapper<>();
        wrapper.eq("uid", toUid);
        List<UserToken> list = tokenService.list(wrapper);

        boolean hasFindChannel = false;
        boolean hasSendMesssage = false;

        for (UserToken userToken : list) {
            String token = userToken.getToken();
            Channel channel = userTokenChannel.getChannel(token);
            if (channel != null) {
                log.info("找到一个channel");
                if (!hasSendMesssage) {
                    saveOnlineMessage(message);
                    hasSendMesssage = true;
                }
                channel.writeAndFlush(message).sync();
                hasFindChannel = true;
            }
        }
        if (!hasFindChannel) {
            saveOfflineMessage(message);
        }
    }

    @Override
    public void sendReadedMessage(Long timestamp) throws BusinessException {
        Message message = getMessageByTimestamp(timestamp);
        log.debug("message" + message);
        setReaded(timestamp);
        // 发送给用户告知已读状态
        List<UserToken> userTokens = userService.getUserTokens(message.getFromId());
        boolean isSend = false;
        Message readMessage = MessageFactory.factoryWithMessageType(MessageType.READED_MESSAGE);
        readMessage.setFromId(message.getFromId());
        readMessage.setFromEntity(message.getFromEntity());
        readMessage.setToEntity(message.getToEntity());
        readMessage.setToId(message.getToId());
        readMessage.setMsgId(message.getMsgId());
        readMessage.setContent(timestamp.toString());
        for (UserToken userToken : userTokens) {
            Channel channel = userTokenChannel.getChannel(userToken.getToken());
            if (channel != null) {
                channel.writeAndFlush(readMessage);
                isSend = true;
            }
        }
        // 如果用户不在线，则放到消息库中
        if (!isSend) {
            readMessage.setMsgId(null);
            save(readMessage);
        }
    }


    private Message getMessage(String fromUid, String toUid, String content, MessageType messageType) {
        Message message = MessageFactory.factoryWithMessageType(messageType);
        message.setFromId(Long.parseLong(fromUid));
        message.setFromEntity(MessageEntityType.USER);
        message.setToId(Long.parseLong(toUid));
        message.setToEntity(MessageEntityType.USER);
        message.setContent(content);
        message.setMsgId(null);
        return message;
    }

}
