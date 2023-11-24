package com.wmy.study.DearIMProject.Socket.handler;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wmy.study.DearIMProject.Socket.Message;
import com.wmy.study.DearIMProject.Socket.UserTokenChannel;
import com.wmy.study.DearIMProject.Socket.message.ChatMessage;
import com.wmy.study.DearIMProject.Socket.message.MessageFactory;
import com.wmy.study.DearIMProject.domain.UserToken;
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

    @PostConstruct
    public void init() {
        singleChatHandler = this;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ChatMessage chatMessage) throws Exception {
        //TODO: wmy 查找user对应的channel
        QueryWrapper<UserToken> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", chatMessage.getFromId());
        List<UserToken> list = userTokenService.list(queryWrapper);
        for (UserToken userToken : list) {
            String token = userToken.getToken();
            Channel channel = userTokenChannel.getChannel(token);
            if (channel != null) {
                channel.writeAndFlush(chatMessage);
            } else {
                //TODO: wmy 离线消息处理逻辑
            }

        }
    }
}
