package com.wmy.study.DearIMProject.Socket.handler;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wmy.study.DearIMProject.Socket.Message;
import com.wmy.study.DearIMProject.Socket.MessageEntityType;
import com.wmy.study.DearIMProject.Socket.MessageType;
import com.wmy.study.DearIMProject.Socket.UserTokenChannel;
import com.wmy.study.DearIMProject.Socket.message.ChatMessage;
import com.wmy.study.DearIMProject.Socket.message.MessageFactory;
import com.wmy.study.DearIMProject.domain.UserToken;
import com.wmy.study.DearIMProject.service.IMessageService;
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
    private IMessageService messageService;

    @PostConstruct
    public void init() {
//        singleChatHandler = this;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ChatMessage chatMessage) throws Exception {
        // 查找user对应的channel
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
                break;
            }
        }
        chatMessage.setMsgId(null);

        // 给原用户发送信息，标识信息已收到
        Message successMsg = MessageFactory.factoryWithMessageType(MessageType.SEND_SUCCESS_MESSAGE);
        successMsg.setContent(String.valueOf(chatMessage.getTimestamp()));
        successMsg.setToId(chatMessage.getFromId());
        successMsg.setToEntity(MessageEntityType.USER);
        // 添加到数据库中
        if (!hasSendMsg) {
            messageService.saveOfflineMessage(chatMessage);
        } else {
            messageService.saveOnlineMessage(chatMessage);
        }
        successMsg.setMsgId(chatMessage.getMsgId());
        successMsg.setFromEntity(MessageEntityType.SERVER);
        successMsg.setFromId(0L);
        channelHandlerContext.writeAndFlush(successMsg);
        if (findChannel != null) {
            findChannel.writeAndFlush(chatMessage);
        }
    }
}
