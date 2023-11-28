package com.wmy.study.DearIMProject.Socket.handler;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wmy.study.DearIMProject.Socket.Message;
import com.wmy.study.DearIMProject.Socket.MessageType;
import com.wmy.study.DearIMProject.Socket.UserTokenChannel;
import com.wmy.study.DearIMProject.Socket.message.ChatMessage;
import com.wmy.study.DearIMProject.Socket.message.MessageFactory;
import com.wmy.study.DearIMProject.domain.UserToken;
import com.wmy.study.DearIMProject.service.IMessageService;
import com.wmy.study.DearIMProject.service.IUserTokenService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

@ChannelHandler.Sharable
@Component
public class SingleChatHandler extends SimpleChannelInboundHandler<ChatMessage> {
    private SingleChatHandler singleChatHandler;
    @Resource
    private UserTokenChannel userTokenChannel;
    @Resource
    private IUserTokenService userTokenService;

    @Resource
    private IMessageService messageService;

    @PostConstruct
    public void init() {
        singleChatHandler = this;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ChatMessage chatMessage) throws Exception {
        //TODO: wmy 查找user对应的channel
        QueryWrapper<UserToken> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", chatMessage.getToId());
        List<UserToken> list = userTokenService.list(queryWrapper);
        boolean hasSendMsg = false;
        for (UserToken userToken : list) {
            String token = userToken.getToken();
            Channel channel = userTokenChannel.getChannel(token);
            if (channel != null) {
                hasSendMsg = true;
                channel.writeAndFlush(chatMessage);
            }
        }
        chatMessage.setMsgId(null);
        // 添加到数据库中
        if (!hasSendMsg) {
            messageService.saveOfflineMessage(chatMessage);
        } else {
            messageService.saveOnlineMessage(chatMessage);
        }
    }
}
