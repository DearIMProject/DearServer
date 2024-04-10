package com.wmy.study.DearIMProject.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.wmy.study.DearIMProject.Socket.Message;
import com.wmy.study.DearIMProject.Socket.MessageEntityType;
import com.wmy.study.DearIMProject.Socket.MessageType;
import com.wmy.study.DearIMProject.Socket.UserTokenChannel;
import com.wmy.study.DearIMProject.Socket.message.MessageFactory;
import com.wmy.study.DearIMProject.Socket.message.SuccessContentJsonModel;
import com.wmy.study.DearIMProject.dao.IMessageDao;
import com.wmy.study.DearIMProject.domain.Group;
import com.wmy.study.DearIMProject.domain.User;
import com.wmy.study.DearIMProject.domain.UserToken;
import com.wmy.study.DearIMProject.service.IGroupMessageService;
import com.wmy.study.DearIMProject.service.IGroupService;
import com.wmy.study.DearIMProject.service.IUserService;
import com.wmy.study.DearIMProject.service.IUserTokenService;
import io.netty.channel.Channel;
import jakarta.annotation.Resource;

import java.util.List;

public class GroupMessageServiceImpl extends ServiceImpl<IMessageDao, Message> implements IGroupMessageService {
    @Resource
    private IUserService userService;
    @Resource
    private IUserTokenService tokenService;
    @Resource
    private UserTokenChannel userTokenChannel;
    @Resource
    private IGroupService groupService;

    @Override
    public void sendAddGroupMessage(List<Long> userIds, Long groupId) throws JsonProcessingException {
        List<User> users = userService.listByIds(userIds);
        for (User user : users) {
            Message message = MessageFactory.factoryWithMessageType(MessageType.GROUP_ADD);
            message.setFromId(0L);
            message.setFromEntity(MessageEntityType.SERVER);
            message.setToId(user.getUserId());
            message.setToEntity(MessageEntityType.USER);
            SuccessContentJsonModel model = new SuccessContentJsonModel();
            model.setContent(String.valueOf(groupId));
            message.setContent(model.jsonString());
            message.setMsgId(null);
            sendMessage(message);
        }
    }

    @Override
    public void sendGroupUpdateMessage(Long groupId) throws JsonProcessingException {
        Group group = groupService.getById(groupId);
        if (group != null) {
            Message message = MessageFactory.factoryWithMessageType(MessageType.GROUP_UPDATE);
            message.setFromId(0L);
            message.setFromEntity(MessageEntityType.SERVER);
            message.setToId(group.getOwnUserId());
            message.setToEntity(MessageEntityType.USER);
            SuccessContentJsonModel model = new SuccessContentJsonModel();
            model.setContent(String.valueOf(groupId));
            message.setContent(model.jsonString());
            sendMessage(message, group.getContentUserIds());
        }
    }

    private void sendMessage(Message message, List<Long> userIds) {
        List<User> users = userService.listByIds(userIds);
        for (User user : users) {
            QueryWrapper<UserToken> wrapper = new QueryWrapper<>();
            wrapper.eq("uid", user.getUserId());
            List<UserToken> list = tokenService.list(wrapper);
            for (UserToken userToken : list) {
                String token = userToken.getToken();
                Channel channel = userTokenChannel.getChannel(token);
                if (channel != null) {
                    log.debug("找到一个channel");
                    Message cMessage = message.clone();
                    cMessage.setToId(user.getUserId());
                    channel.writeAndFlush(message);
                }
            }
        }
    }

    @Override
    public void sendMessage(Message message) {
        QueryWrapper<UserToken> wrapper = new QueryWrapper<>();
        wrapper.eq("uid", message.getToId());
        List<UserToken> list = tokenService.list(wrapper);

        for (UserToken userToken : list) {
            String token = userToken.getToken();
            Channel channel = userTokenChannel.getChannel(token);
            if (channel != null) {
                log.debug("找到一个channel");
                channel.writeAndFlush(message);
            }
        }
    }

    @Override
    public void sendGroupDeleteMessage(Group group) throws JsonProcessingException {
        Message message = MessageFactory.factoryWithMessageType(MessageType.GROUP_UPDATE);
        message.setFromId(0L);
        message.setFromEntity(MessageEntityType.SERVER);
        message.setToId(group.getOwnUserId());
        message.setToEntity(MessageEntityType.USER);
        SuccessContentJsonModel model = new SuccessContentJsonModel();
        model.setContent(String.valueOf(group));
        message.setContent(model.jsonString());
        sendMessage(message, group.getContentUserIds());
    }

    @Override
    public void sendGroupDeleteMessage(Long userId, Long groupId) throws JsonProcessingException {
        Message message = MessageFactory.factoryWithMessageType(MessageType.GROUP_UPDATE);
        message.setFromId(0L);
        message.setFromEntity(MessageEntityType.SERVER);
        message.setToId(userId);
        message.setToEntity(MessageEntityType.USER);
        SuccessContentJsonModel model = new SuccessContentJsonModel();
        model.setContent(String.valueOf(groupId));
        message.setContent(model.jsonString());
        sendMessage(message);
    }

}
