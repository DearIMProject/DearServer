package com.wmy.study.DearIMProject.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.wmy.study.DearIMProject.Exception.BusinessException;
import com.wmy.study.DearIMProject.Socket.*;
import com.wmy.study.DearIMProject.Socket.message.MessageFactory;
import com.wmy.study.DearIMProject.Socket.message.SuccessContentJsonModel;
import com.wmy.study.DearIMProject.dao.IMessageDao;
import com.wmy.study.DearIMProject.domain.MsgStatus;
import com.wmy.study.DearIMProject.domain.User;
import com.wmy.study.DearIMProject.domain.UserToken;
import com.wmy.study.DearIMProject.service.IMessageService;
import com.wmy.study.DearIMProject.service.IMsgStatusService;
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

    @Resource
    private IMsgStatusService msgStatusService;

    @Override
    public void saveMessage(Message message) {
        message.setStatus(MessageStatus.STATUS_SUCCESS);
        save(message);
    }


    @Override
    public List<Message> getOfflinePersonalMessages(String token, Long timestamp) {
        User user = userService.getFromToken(token);
        QueryWrapper<Message> wrapper = new QueryWrapper<>();
        wrapper.gt("timestamp", timestamp);
        // 离线消息中发送方或接收方为自己的,但是entity_type需要为0的消息
        wrapper.and(i -> i.eq("entity_type", 0)).and(i -> i.eq("from_id", user.getUserId()).or().eq("to_id", user.getUserId()));
        return list(wrapper);
    }

    @Override
    public List<Message> getOfflineGroupMessages(String token, Long timestamp) {
        User user = userService.getFromToken(token);
        QueryWrapper<Message> wrapper = new QueryWrapper<>();
        wrapper.gt("timestamp", timestamp);
        // 离线消息中接收方为自己的,但是entity_type需要为1的消息
        wrapper.and(i -> i.eq("entity_type", 1)).and(i -> i.eq("to_id", user.getUserId()));
        List<Message> list = list(wrapper);
        for (Message message : list) {
            List<Long> userIds = msgStatusService.getMessageReaded(message.getMsgId());
            message.setReadList(userIds);
        }
        return list;
    }

    @Override
    public void setMessageReaded(Long msgId, Long userId) {
        QueryWrapper<MsgStatus> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("msg_id", msgId);
        queryWrapper.eq("user_id", userId);
        MsgStatus one = msgStatusService.getOne(queryWrapper);
        if (one == null) {
            MsgStatus msgStatus = new MsgStatus();
            msgStatus.setMsgId(msgId);
            msgStatus.setUserId(userId);
            msgStatusService.save(msgStatus);
        }
    }


    @Override
    public void setReaded(Long timestamp, Long userId) {
        QueryWrapper<Message> messageQueryWrapper = new QueryWrapper<>();
        messageQueryWrapper.eq("timestamp", timestamp);
        messageQueryWrapper.eq("to_id", userId);
        Message one = getOne(messageQueryWrapper);
        if (one != null) {
            Long msgId = one.getMsgId();
            MsgStatus msgStatus = new MsgStatus();
            msgStatus.setMsgId(msgId);
            msgStatus.setUserId(userId);
            msgStatusService.save(msgStatus);
        }
    }

    @Override
    public Message getMessageByTimestamp(Long timestamp, Long toId) {
        QueryWrapper<Message> wrapper = new QueryWrapper<>();
        wrapper.eq("timestamp", timestamp);
        wrapper.eq("to_id", toId);
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


        for (UserToken userToken : list) {
            String token = userToken.getToken();
            Channel channel = userTokenChannel.getChannel(token);
            if (channel != null) {
                log.info("找到一个channel");
                channel.writeAndFlush(message);
            }
        }
        saveMessage(message);
    }

    @Override
    public void sendReadedMessage(Message message, Long toId) throws BusinessException, JsonProcessingException {
        log.debug("message" + message);
        setMessageReaded(message.getMsgId(), message.getToId());
        // 发送给用户告知已读状态
        List<UserToken> userTokens = userService.getUserTokens(message.getFromId());
        Message readMessage = MessageFactory.factoryWithMessageType(MessageType.READED_MESSAGE);
        readMessage.setFromId(message.getFromId());
        readMessage.setFromEntity(message.getFromEntity());
        readMessage.setToEntity(message.getToEntity());
        readMessage.setToId(message.getToId());
        readMessage.setMsgId(message.getMsgId());
        SuccessContentJsonModel successContentJsonModel = new SuccessContentJsonModel();
        successContentJsonModel.setContent(String.valueOf(toId));
        successContentJsonModel.setMsgId(message.getMsgId());
        successContentJsonModel.setTimestamp(message.getTimestamp());
        readMessage.setContent(successContentJsonModel.jsonString());
        for (UserToken userToken : userTokens) {
            Channel channel = userTokenChannel.getChannel(userToken.getToken());
            if (channel != null) {
                channel.writeAndFlush(readMessage);
            }
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
