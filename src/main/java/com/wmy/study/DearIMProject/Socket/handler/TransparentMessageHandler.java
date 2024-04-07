package com.wmy.study.DearIMProject.Socket.handler;

import com.wmy.study.DearIMProject.Socket.Message;
import com.wmy.study.DearIMProject.Socket.UserTokenChannel;
import com.wmy.study.DearIMProject.Socket.message.TransparentMessage;
import com.wmy.study.DearIMProject.domain.UserToken;
import com.wmy.study.DearIMProject.service.IUserService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ChannelHandler.Sharable
public class TransparentMessageHandler extends SimpleChannelInboundHandler<TransparentMessage> {
    @Resource
    private UserTokenChannel tokenChannel;

    @Resource
    private IUserService userService;

    @Override
    protected void channelRead0(ChannelHandlerContext chc, TransparentMessage message) throws Exception {
        List<UserToken> userTokens = userService.getUserTokens(message.getToId());
        for (UserToken userToken : userTokens) {
            Channel channel = tokenChannel.getChannel(userToken.getToken());
            if (channel != null) {
                channel.writeAndFlush(message);
            }
        }
    }
}
