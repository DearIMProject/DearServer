package com.wmy.study.DearIMProject.Socket.handler;

import com.wmy.study.DearIMProject.Socket.UserTokenChannel;
import com.wmy.study.DearIMProject.Socket.message.HeartBeatMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

@Component
@ChannelHandler.Sharable
public class HeartBeatRequestHandler extends SimpleChannelInboundHandler<HeartBeatMessage> {
    @Resource
    private UserTokenChannel userTokenChannel;

//    public HeartBeatRequestHandler heartBeatRequestHandler;

    public HeartBeatRequestHandler() {
    }

    @PostConstruct
    public void init() {
//        heartBeatRequestHandler = this;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, HeartBeatMessage heartBeatMessage) throws Exception {
        // 原封不动发回去消息
        heartBeatMessage.setToId(heartBeatMessage.getFromId());
        heartBeatMessage.setToEntity(heartBeatMessage.getFromEntity());
        channelHandlerContext.writeAndFlush(heartBeatMessage);
    }
}
