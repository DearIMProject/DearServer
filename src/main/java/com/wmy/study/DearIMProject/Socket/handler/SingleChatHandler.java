package com.wmy.study.DearIMProject.Socket.handler;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.wmy.study.DearIMProject.Socket.Message;
import com.wmy.study.DearIMProject.Socket.MessageEntityType;
import com.wmy.study.DearIMProject.Socket.MessageType;
import com.wmy.study.DearIMProject.Socket.UserTokenChannel;
import com.wmy.study.DearIMProject.Socket.message.ChatMessage;
import com.wmy.study.DearIMProject.Socket.message.MessageFactory;
import com.wmy.study.DearIMProject.Socket.message.SuccessContentJsonModel;
import com.wmy.study.DearIMProject.domain.Group;
import com.wmy.study.DearIMProject.domain.UserToken;
import com.wmy.study.DearIMProject.service.IGroupService;
import com.wmy.study.DearIMProject.service.IMessageService;
import com.wmy.study.DearIMProject.service.IUserService;
import com.wmy.study.DearIMProject.service.IUserTokenService;
import io.netty.channel.*;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@ChannelHandler.Sharable
@Component
public class SingleChatHandler extends SimpleChannelInboundHandler<ChatMessage> {
    //    private SingleChatHandler singleChatHandler;
    @Resource
    private UserTokenChannel userTokenChannel;
    @Resource
    private IUserTokenService userTokenService;
    @Resource
    private IUserService userService;

    @Resource
    private IMessageService messageService;

    @Resource
    private IGroupService groupService;

    @PostConstruct
    public void init() {
//        singleChatHandler = this;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ChatMessage chatMessage) throws Exception {
        // 查找user对应的channel
        if (chatMessage.getToEntity() == MessageEntityType.GROUP) {
            Long userId = chatMessage.getFromId();
            log.debug("群聊消息");
            Long groupId = chatMessage.getToId();
            if (groupId == null) {
                log.debug("群聊消息没有群id");
                return;
            }
            Group group = groupService.getById(groupId);
            if (group == null) {
                log.debug("群聊消息没有群");
                return;
            }
            if (!group.getContentUserIds().contains(userId)) {
                log.debug("没有权限发送该群信息");
                return;
            }
            // 给原用户发送信息，标识信息已收到
            Message successMsg = MessageFactory.factoryWithMessageType(MessageType.SEND_SUCCESS_MESSAGE);
            successMsg.setContent(String.valueOf(chatMessage.getTimestamp()));
            successMsg.setToId(chatMessage.getFromId());
            successMsg.setToEntity(MessageEntityType.USER);
            successMsg.setContent(new SuccessContentJsonModel(chatMessage.getMsgId(),
                    chatMessage.getTimestamp(),
                    chatMessage.getMessageType(),
                    String.valueOf(chatMessage.getTimestamp())).jsonString());
            successMsg.setMsgId(chatMessage.getMsgId());
            successMsg.setFromEntity(MessageEntityType.SERVER);
            successMsg.setFromId(0L);
            successMsg.setEntityId(0L);
            successMsg.setEntityType(MessageEntityType.SERVER);
            channelHandlerContext.writeAndFlush(successMsg);
            // 群聊消息
            for (Long contentUserId : group.getContentUserIds()) {

                QueryWrapper<UserToken> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("uid", contentUserId);
                log.debug("发送给的uid:" + chatMessage.getToId());
                List<UserToken> list = userTokenService.list(queryWrapper);

                Message cloned = chatMessage.clone();
                cloned.setMsgId(null);
                cloned.setFromId(chatMessage.getFromId());
                cloned.setFromEntity(chatMessage.getFromEntity());
                cloned.setEntityId(group.getGroupId());
                cloned.setEntityType(MessageEntityType.GROUP);
                cloned.setToId(contentUserId);
                cloned.setToEntity(MessageEntityType.USER);
                boolean isSend = false;
                if (list.isEmpty()) {
                    log.debug("没有找到用户userToken");
                } else {
                    for (UserToken userToken : list) {
                        Channel channel = userTokenChannel.getChannel(userToken.getToken());
                        if (channel != null) {
                            log.debug("找到用户userToken 发送信息" + channel);
                            channel.writeAndFlush(cloned);
                            isSend = true;
                        }
                    }
                }
                if (isSend) {
                    messageService.saveOnlineMessage(cloned);
                } else {
                    messageService.saveOfflineMessage(cloned);
                }
            }
            return;
        }
        execUserMessage(channelHandlerContext, chatMessage);
    }

    private void execUserMessage(ChannelHandlerContext channelHandlerContext, ChatMessage chatMessage) throws JsonProcessingException {
        QueryWrapper<UserToken> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", chatMessage.getToId());
        log.debug("发送给的uid:" + chatMessage.getToId());
        List<UserToken> list = userTokenService.list(queryWrapper);
        boolean hasSendMsg = false;
        Channel findChannel = null;
        for (UserToken userToken : list) {
            String token = userToken.getToken();
            Channel channel = userTokenChannel.getChannel(token);
            if (channel != null) {
                hasSendMsg = true;
                log.debug("找到用户userToken 发送信息" + channel);
                findChannel = channel;
                if (!channelHandlerContext.channel().equals(findChannel)) {
                    findChannel.writeAndFlush(chatMessage);
                }
                break;
            }
        }
        chatMessage.setMsgId(null);

        // 给原用户发送信息，标识信息已收到
        Message successMsg = MessageFactory.factoryWithMessageType(MessageType.SEND_SUCCESS_MESSAGE);
        successMsg.setContent(String.valueOf(chatMessage.getTimestamp()));
        successMsg.setToId(chatMessage.getFromId());
        successMsg.setToEntity(MessageEntityType.USER);
        successMsg.setEntityId(chatMessage.getFromId());
        successMsg.setEntityType(MessageEntityType.USER);
        // 添加到数据库中
        if (!hasSendMsg) {
            messageService.saveOfflineMessage(chatMessage);
        } else {
            messageService.saveOnlineMessage(chatMessage);
        }
        successMsg.setContent(new SuccessContentJsonModel(chatMessage.getMsgId(),
                chatMessage.getTimestamp(),
                chatMessage.getMessageType(),
                String.valueOf(chatMessage.getTimestamp())).jsonString());
        successMsg.setMsgId(chatMessage.getMsgId());
        successMsg.setFromEntity(MessageEntityType.SERVER);
        successMsg.setFromId(0L);
        channelHandlerContext.writeAndFlush(successMsg);
    }
}
